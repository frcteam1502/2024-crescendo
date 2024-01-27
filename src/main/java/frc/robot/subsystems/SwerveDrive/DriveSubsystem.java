package frc.robot.subsystems.SwerveDrive;

import javax.swing.text.Utilities;

import frc.robot.Logger;

import com.ctre.phoenix6.hardware.CANcoder;
import com.ctre.phoenix6.hardware.Pigeon2;

import edu.wpi.first.math.estimator.SwerveDrivePoseEstimator;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.kinematics.ChassisSpeeds;
import edu.wpi.first.math.kinematics.SwerveDriveKinematics;
import edu.wpi.first.math.kinematics.SwerveModulePosition;
import edu.wpi.first.math.kinematics.SwerveModuleState;
import edu.wpi.first.math.util.Units;
import edu.wpi.first.util.sendable.SendableBuilder;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.SubsystemBase;

import frc.robot.GameState;

import team1502.configuration.RobotConfiguration;


public class DriveSubsystem extends SubsystemBase{
  
  public static boolean isTeleOp = false;

  public boolean isTurning = false;
  public double targetAngle = 0.0;
  public double turnCommand = 0.0;
  public double fieldXCommand = 0;
  public double fieldYCommand = 0;

  ChassisSpeeds speedCommands = new ChassisSpeeds(0, 0, 0);

  private final Pigeon2 gyro;

  private final SwerveModules swerveModules;
  private final SwerveDriveKinematics kinematics;
  public final SwerveDrivePoseEstimator odometry;

  private Pose2d pose = new Pose2d();
  
  private final double goStraightGain;
  private final double maxSpeed;
  private final double empiricalSpeed; // for comparison

  public DriveSubsystem(RobotConfiguration config) {
    gyro = new Pigeon2(config.GyroSensor().CanNumber());
  
    swerveModules = new SwerveModules(config);
    kinematics = config.SwerveDrive().getKinematics();
    maxSpeed = swerveModules.maxSpeed;
    empiricalSpeed = swerveModules.empiricalSpeed;
    
    goStraightGain = config.SwerveDrive().getDouble("goStraightGain");

    this.odometry = new SwerveDrivePoseEstimator(kinematics, getGyroRotation2d(), getModulePositions(), pose);

    reset();
    registerLoggerObjects();
    
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
    //Convert from robot frame of reference (ChassisSpeeds) to swerve module frame of reference (SwerveModuleState)
    var swerveModuleStates = kinematics.toSwerveModuleStates(robotRelativeSpeeds);
    //Normalize wheel speed commands to make sure no speed is greater than the maximum achievable wheel speed.
    SwerveDriveKinematics.desaturateWheelSpeeds(swerveModuleStates, maxSpeed);
    //Set the speed and angle of each module
    setDesiredState(swerveModuleStates);
  }

  public ChassisSpeeds getRobotRelativeSpeeds(){
    //This method is a supplier of ChassisSpeeds as determined by the module states.  This is required for PathPlanner 2024
    return kinematics.toChassisSpeeds(getModuleStates());
  }

  public SwerveModuleState[] getModuleStates(){ return swerveModules.getModuleStates(); }
  public SwerveModulePosition[] getModulePositions() { return swerveModules.getModulePositions(); }
  public void setDesiredState(SwerveModuleState[] swerveModuleStates) { swerveModules.setDesiredState(swerveModuleStates); }
  public void resetModules() { swerveModules.resetModules(); }

  public void updateOdometry() {
    pose = odometry.update(getGyroRotation2d(), getModulePositions());
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
    builder.addDoubleProperty("Robot Relative Rotation Command", ()->speedCommands.omegaRadiansPerSecond, null);

    //Robot Relative inputs
    builder.addDoubleProperty("Robot Relative vX Speed Command", ()->speedCommands.vxMetersPerSecond, null);
    builder.addDoubleProperty("Robot Relative vY Speed Command", ()->speedCommands.vyMetersPerSecond, null);

    builder.addDoubleProperty("Gyro Yaw", ()->getIMU_Yaw(), null);

    addChild("SwerveModules", swerveModules);
  }

  private void registerLoggerObjects(){
    Logger.RegisterCanSparkMax("FL Drive", Motors.DRIVE_FRONT_LEFT);
    Logger.RegisterCanSparkMax("FR Drive", Motors.DRIVE_FRONT_RIGHT);
    Logger.RegisterCanSparkMax("RL Drive", Motors.DRIVE_BACK_LEFT);
    Logger.RegisterCanSparkMax("RR Drive", Motors.DRIVE_BACK_RIGHT);

    Logger.RegisterCanSparkMax("FL Turn", Motors.ANGLE_FRONT_LEFT);
    Logger.RegisterCanSparkMax("FR Turn", Motors.ANGLE_FRONT_RIGHT);
    Logger.RegisterCanSparkMax("RL Turn", Motors.ANGLE_BACK_LEFT);
    Logger.RegisterCanSparkMax("RR Turn", Motors.ANGLE_BACK_RIGHT);

    Logger.RegisterPigeon(Gyro.gyro);

    Logger.RegisterCanCoder("FL Abs Position", CANCoders.FRONT_LEFT_CAN_CODER);
    Logger.RegisterCanCoder("FR Abs Position", CANCoders.FRONT_RIGHT_CAN_CODER);
    Logger.RegisterCanCoder("RL Abs Position", CANCoders.BACK_LEFT_CAN_CODER);
    Logger.RegisterCanCoder("RR Abs Position", CANCoders.BACK_RIGHT_CAN_CODER);

    Logger.RegisterSensor("FL Drive Speed", ()->frontLeft.getVelocity());
    Logger.RegisterSensor("FR Drive Speed", ()->frontRight.getVelocity());
    Logger.RegisterSensor("RL Drive Speed", ()->backLeft.getVelocity());
    Logger.RegisterSensor("RR Drive Speed", ()->backRight.getVelocity());
  }

}
