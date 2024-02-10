package frc.robot.subsystems.SwerveDrive;

import frc.robot.GameState;
import frc.robot.Logger;
import frc.robot.LimelightHelpers.LimelightResults;
import frc.robot.LimelightHelpers;

import com.ctre.phoenix6.hardware.Pigeon2;

import com.pathplanner.lib.auto.AutoBuilder;
import com.pathplanner.lib.util.HolonomicPathFollowerConfig;
import com.pathplanner.lib.util.PIDConstants;
import com.pathplanner.lib.util.ReplanningConfig;

import edu.wpi.first.math.estimator.SwerveDrivePoseEstimator;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.kinematics.ChassisSpeeds;
import edu.wpi.first.math.kinematics.SwerveDriveKinematics;
import edu.wpi.first.math.kinematics.SwerveModulePosition;
import edu.wpi.first.math.kinematics.SwerveModuleState;
import edu.wpi.first.math.util.Units;
import edu.wpi.first.util.sendable.SendableBuilder;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.SubsystemBase;

import team1502.configuration.factory.RobotConfiguration;

public class DriveSubsystem extends SubsystemBase {
  private boolean isTurning = false;
  private double targetAngle = 0.0;
  private double turnCommand = 0.0;
  private double fieldXCommand = 0;
  private double fieldYCommand = 0;

  ChassisSpeeds speedCommands = new ChassisSpeeds(0, 0, 0);
  ChassisSpeeds relativeCommands = new ChassisSpeeds(0,0,0);

  private final Pigeon2 gyro;

  private final SwerveModules swerveModules;
  private final SwerveDriveKinematics kinematics;
  private final SwerveDrivePoseEstimator odometry;

  private Pose2d pose = new Pose2d();

  private final double goStraightGain;
  private final double maxSpeed;
  private final double driveBaseRadius;

  public DriveSubsystem(RobotConfiguration config) {
    gyro = config.Pigeon2().buildPigeon2();
  
    swerveModules = new SwerveModules(config);
    kinematics = config.SwerveDrive().getKinematics();
    maxSpeed = config.SwerveDrive().calculateMaxSpeed();
    driveBaseRadius = config.SwerveDrive().Chassis().getDriveBaseRadius();
    goStraightGain = config.SwerveDrive().GoStraightGain();

    this.odometry = new SwerveDrivePoseEstimator(kinematics, getGyroRotation2d(), getModulePositions(), pose);

    reset();
    registerLoggerObjects(config);

    //Configure Auto Builder last! -- why?
    configAutoBuilder(); 
  }

  private void checkInitialAngle() {
    if (GameState.isTeleop() && GameState.isFirst()) { 
      targetAngle = getIMU_Yaw();
    }
  }

  private double getIMU_Yaw() {
    var currentHeading = gyro.getYaw(); 
    return( currentHeading.getValue() );
  }
  
  @Override
  public void periodic() {
    checkInitialAngle();
    updateOdometry();
    
    SmartDashboard.putData(this);
    swerveModules.send();
  }
  
  public void drive(double xSpeed, double ySpeed, double rot, boolean fieldRelative) {
    checkInitialAngle();

    if (GameState.isTeleop()) {
      if (Math.abs(rot) > 0) {
        //Driver is commanding rotation, 
        isTurning = true;
        targetAngle = getIMU_Yaw();
      } 
      else if (rot == 0 && isTurning) {
        //Driver stopped commanding a turn
        isTurning = false;
      }

      if (isTurning) {
        turnCommand = rot;
      }
      else { 
        turnCommand = (targetAngle - getIMU_Yaw()) * goStraightGain;
      }
      
    }
    //Set Dashboard variables
    fieldXCommand = xSpeed;
    fieldYCommand = ySpeed;

    if(fieldRelative){
      speedCommands = ChassisSpeeds.fromFieldRelativeSpeeds(xSpeed, ySpeed, turnCommand, getGyroRotation2d());
    } else {
      speedCommands.omegaRadiansPerSecond = turnCommand;
      speedCommands.vxMetersPerSecond = xSpeed;
      speedCommands.vyMetersPerSecond = ySpeed;
    }

    driveRobotRelative(speedCommands);
  }

  public void driveRobotRelative(ChassisSpeeds robotRelativeSpeeds){
    //This method is a consumer of ChassisSpeed and sets the corresponding module states.  This is required for PathPlanner 2024
    //Save off to SmartDashboard
    relativeCommands.vxMetersPerSecond = robotRelativeSpeeds.vxMetersPerSecond;
    relativeCommands.vyMetersPerSecond = robotRelativeSpeeds.vyMetersPerSecond;
    relativeCommands.omegaRadiansPerSecond = robotRelativeSpeeds.omegaRadiansPerSecond;
    
    //Convert from robot frame of reference (ChassisSpeeds) to swerve module frame of reference (SwerveModuleState)
    var swerveModuleStates = kinematics.toSwerveModuleStates(robotRelativeSpeeds);
    //Normalize wheel speed commands to make sure no speed is greater than the maximum achievable wheel speed.
    SwerveDriveKinematics.desaturateWheelSpeeds(swerveModuleStates, maxSpeed);
    //Set the speed and angle of each module
    setDesiredState(swerveModuleStates);
  }

  public ChassisSpeeds getRobotRelativeSpeeds() { return kinematics.toChassisSpeeds(getModuleStates()); }
  public SwerveModuleState[] getModuleStates(){ return swerveModules.getModuleStates(); }
  public SwerveModulePosition[] getModulePositions() { return swerveModules.getModulePositions(); }
  public void setDesiredState(SwerveModuleState[] swerveModuleStates) { swerveModules.setDesiredState(swerveModuleStates); }
  public void resetModules() { swerveModules.resetModules(); }

  public void updateOdometry() {
    pose = odometry.update(getGyroRotation2d(), getModulePositions());

    if(GameState.isFirst()){
      System.out.println(pose.getX());
      System.out.println(pose.getY());
    }
  }

  public void resetOdometry(Pose2d pose) {
    odometry.resetPosition(getGyroRotation2d(), getModulePositions(), pose);
  }
  
  public SwerveModuleState[] makeSwerveModuleState(double[] speeds, double[] angles) {
    SwerveModuleState[] moduleStates = new SwerveModuleState[angles.length];
    for(int i = 0; i < angles.length; i++) moduleStates[i] = new SwerveModuleState(speeds[i], new Rotation2d(Units.degreesToRadians(angles[i])));
    return moduleStates;
  }

  public void setToBreak() {
    resetModules();
    double[] speeds = {0, 0, 0, 0};
    double[] angles = {90, 90, 90, 90};
    SwerveModuleState[] moduleStates = makeSwerveModuleState(speeds, angles);
    setDesiredState(moduleStates);
  }

  public Rotation2d getGyroRotation2d() {
    return new Rotation2d(Units.degreesToRadians(getIMU_Yaw()));
  }

  public Pose2d getPose2d() {
    return odometry.getEstimatedPosition();
  }

  public void resetGyro() {
    gyro.setYaw(0);
  }

  public void reset() {
    resetGyro();
    resetModules();
    resetOdometry(pose);
  }  

  @Override
  public void initSendable(SendableBuilder builder) {
    super.initSendable(builder);

    //Field Oriented inputs
    builder.addDoubleProperty("Field Oriented X Command (Forward)", ()->fieldXCommand, null);
    builder.addDoubleProperty("Field Oriented Y Command (Forward)", ()->fieldYCommand, null);

    //Robot Relative inputs
    builder.addDoubleProperty("Robot Relative vX Speed Command", ()->speedCommands.vxMetersPerSecond, null);
    builder.addDoubleProperty("Robot Relative vY Speed Command", ()->speedCommands.vyMetersPerSecond, null);
    builder.addDoubleProperty("Robot Relative Rotation Command", ()->speedCommands.omegaRadiansPerSecond, null);
    
    builder.addDoubleProperty("DriveRobot Relative vX Speed Command", ()->relativeCommands.vxMetersPerSecond, null);
    builder.addDoubleProperty("DriveRobot Relative vY Speed Command", ()->relativeCommands.vyMetersPerSecond, null);
    builder.addDoubleProperty("DriveRobot Relative Rotation Command", ()->relativeCommands.omegaRadiansPerSecond, null);
    

    builder.addDoubleProperty("Gyro Yaw", ()->getIMU_Yaw(), null);

    addChild("SwerveModules", swerveModules);

        //Pose Info
    SmartDashboard.putString("FMS Alliance", DriverStation.getAlliance().toString());
    SmartDashboard.putNumber("Pose2D X", pose.getX());
    SmartDashboard.putNumber("Pose2D Y", pose.getY());
    SmartDashboard.putNumber("Pose2D Rotation", pose.getRotation().getDegrees());

    //Limelight Info
    LimelightResults llresults = LimelightHelpers.getLatestResults("");
    int numAprilTags = llresults.targetingResults.targets_Fiducials.length;

    SmartDashboard.putNumber("Number of AprilTags",numAprilTags);
    SmartDashboard.putNumber("Tag ID", LimelightHelpers.getFiducialID(""));
    SmartDashboard.putNumber("Limelight TX", LimelightHelpers.getTX(""));
    SmartDashboard.putNumber("Limelight TY", LimelightHelpers.getTY(""));
    SmartDashboard.putNumber("Limelight TA", LimelightHelpers.getTA(""));

  }

  private void registerLoggerObjects(RobotConfiguration config){
    config.registerLoggerObjects(
        (n, r)->Logger.RegisterCanSparkMax(n,r),
        p->Logger.RegisterPigeon(p),
        (n, r)->Logger.RegisterCanCoder(n,r),
        (n, d)->Logger.RegisterSensor(n,d)
      );
  }

  private void configAutoBuilder(){
    //Wraper for AutoBuilder.configureHolonomic, must be called from DriveTrain config....

    AutoBuilder.configureHolonomic(
      this::getPose2d, //Robot pose supplier
      this::resetOdometry, //Method to reset odometry (will be called if the robot has a starting pose)
      this::getRobotRelativeSpeeds, //ChassisSpeeds provider.  MUST BE ROBOT RELATIVE!!! 
      this::driveRobotRelative, //ChassisSpeeds consumer.  MUST BE ROBOT RELATIVE!!!
      new HolonomicPathFollowerConfig(
        new PIDConstants(5.0, 0, 0), //Translation PID constants
        new PIDConstants(5.0, 0, 0), //Rotation PID constants
        maxSpeed, 
        driveBaseRadius,
        new ReplanningConfig()), //HolonomicPathFollowerConfig
        () -> {
          // Boolean supplier that controls when the path will be mirrored for the red alliance
          // This will flip the path being followed to the red side of the field.
          // THE ORIGIN WILL REMAIN ON THE BLUE SIDE
      
          var alliance = DriverStation.getAlliance();
          if (alliance.isPresent()) {
                return alliance.get() == DriverStation.Alliance.Red;
            }
              return false;
          },
      this); //Reference to this subsystem to set 
  }

  public void dummyAction1(){System.out.println("Drivetrain Command 1!");}
  public void dummyAction2(){System.out.println("Drivetrain Command 2!");}

}
