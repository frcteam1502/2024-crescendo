package frc.robot.subsystems.SwerveDrive;

import frc.robot.GameState;
import frc.robot.Logger;
import frc.robot.LimelightHelpers.LimelightResults;
import frc.robot.commands.ControllerCommands;
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
import edu.wpi.first.util.sendable.Sendable;
import edu.wpi.first.util.sendable.SendableBuilder;
import edu.wpi.first.util.sendable.SendableRegistry;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.Subsystem;
import team1502.configuration.annotations.*;
import team1502.configuration.factory.RobotConfiguration;

@DefaultCommand(command = ControllerCommands.class)
public class DriveSubsystem implements Subsystem, Sendable {
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

  private int numAprilTags;
  private void updateDashBoard() {
        //Limelight Info
    LimelightResults llresults = LimelightHelpers.getLatestResults("");
    numAprilTags = llresults.targetingResults.targets_Fiducials.length;
    SmartDashboard.putData(this);
    swerveModules.send();

  }
  
  @Override
  public void periodic() {
    checkInitialAngle();
    updateOdometry();
    updateDashBoard();
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

  /*public void alignToSpeaker(){
    LimelightResults llresults = LimelightHelpers.getLatestResults("");
    int numAprilTags = llresults.targetingResults.targets_Fiducials.length;
    boolean validTarget = llresults.targetingResults.valid;

    double tx = 0;
    boolean targetFound = false;
    ChassisSpeeds speedCommands = new ChassisSpeeds(0,0,0);

    //Determine if any AprilTags are present
    if(validTarget){
      //Parse through the JSON fiducials and see if speaker tags are present
      for(int i=0;i<numAprilTags;i++){
        int tagID = (int)llresults.targetingResults.targets_Fiducials[i].fiducialID;
        
        if((tagID == 7)||(tagID == 4)){
          //Center Tag
          tx = llresults.targetingResults.targets_Fiducials[i].tx;
          targetFound = true;
        }else{
          targetFound = false;
        }
      }

      double turnCommand = (tx - getIMU_Yaw())*goStraightGain;

      ChassisSpeeds speedCommands = new ChassisSpeeds(0,0,turnCommand);

      driveRobotRelative(speedCommands);
    }else

  }*/

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
    //super.initSendable(builder);
    builder.setSmartDashboardType("Subsystem");

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

    //addChild("SwerveModules", swerveModules);
    SendableRegistry.addLW(swerveModules, SendableRegistry.getSubsystem(this), "SwerveModule");

        //Pose Info
    builder.addStringProperty("FMS Alliance", ()->DriverStation.getAlliance().toString(), null);
    builder.addDoubleProperty("Pose2D X", ()->pose.getX(), null);
    builder.addDoubleProperty("Pose2D Y", ()->pose.getY(), null);
    builder.addDoubleProperty("Pose2D Rotation", ()->pose.getRotation().getDegrees(), null);

    builder.addIntegerProperty("Number of AprilTags", ()->numAprilTags, null);
    builder.addDoubleProperty("Tag ID", ()->LimelightHelpers.getFiducialID(""), null);
    builder.addDoubleProperty("Limelight TX", ()->LimelightHelpers.getTX(""), null);
    builder.addDoubleProperty("Limelight TY", ()->LimelightHelpers.getTY(""), null);
    builder.addDoubleProperty("Limelight TA", ()->LimelightHelpers.getTA(""), null);

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
