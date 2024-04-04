package frc.robot.subsystems.SwerveDrive;

import frc.robot.Logger;
import frc.robot.subsystems.Vision.Limelight;
import frc.robot.subsystems.Vision.LimelightHelpers;

import com.ctre.phoenix6.hardware.CANcoder;
import com.ctre.phoenix6.hardware.Pigeon2;
import com.ctre.phoenix6.signals.SensorDirectionValue;
import com.pathplanner.lib.auto.AutoBuilder;
import com.pathplanner.lib.util.HolonomicPathFollowerConfig;
import com.pathplanner.lib.util.PIDConstants;
import com.pathplanner.lib.util.ReplanningConfig;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkLowLevel;
import com.revrobotics.CANSparkBase.IdleMode;

import edu.wpi.first.math.VecBuilder;
import edu.wpi.first.math.Vector;
import edu.wpi.first.math.estimator.SwerveDrivePoseEstimator;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.geometry.Translation2d;
import edu.wpi.first.math.kinematics.ChassisSpeeds;
import edu.wpi.first.math.kinematics.SwerveDriveKinematics;
import edu.wpi.first.math.kinematics.SwerveDriveOdometry;
import edu.wpi.first.math.kinematics.SwerveModulePosition;
import edu.wpi.first.math.kinematics.SwerveModuleState;
import edu.wpi.first.math.numbers.N3;
import edu.wpi.first.math.util.Units;
import edu.wpi.first.wpilibj2.command.SubsystemBase;

import frc.robot.GameState;

final class Gyro {
  public static final Pigeon2 gyro = new Pigeon2(14);
  public static final boolean GYRO_REVERSED = true;
}

final class CANCoders {
  //Front Left CANCoder
  public static final CANcoder FRONT_LEFT_CAN_CODER = new CANcoder(16);
  public static final SensorDirectionValue FRONT_LEFT_CAN_CODER_DIRECTION = SensorDirectionValue.CounterClockwise_Positive;
  //public static final SensorDirectionValue FRONT_LEFT_CAN_CODER_DIRECTION = SensorDirectionValue.Clockwise_Positive;
  public static final double FRONT_LEFT_CAN_CODER_OFFSET = 103.32;

  //Front Right CANCoder
  public static final CANcoder FRONT_RIGHT_CAN_CODER = new CANcoder(10);
  public static final SensorDirectionValue FRONT_RIGHT_CAN_CODER_DIRECTION = SensorDirectionValue.CounterClockwise_Positive;
  //public static final SensorDirectionValue FRONT_RIGHT_CAN_CODER_DIRECTION = SensorDirectionValue.Clockwise_Positive;
  public static final double FRONT_RIGHT_CAN_CODER_OFFSET = 292.68;

  //Back Left CANCoder
  public static final CANcoder BACK_LEFT_CAN_CODER = new CANcoder(4);
  public static final SensorDirectionValue BACK_LEFT_CAN_CODER_DIRECTION = SensorDirectionValue.CounterClockwise_Positive;
  //public static final SensorDirectionValue BACK_LEFT_CAN_CODER_DIRECTION = SensorDirectionValue.Clockwise_Positive;
  public static final double BACK_LEFT_CAN_CODER_OFFSET = 276.48;

  //Back Right CANCoder
  public static final CANcoder BACK_RIGHT_CAN_CODER = new CANcoder(8);
  public static final SensorDirectionValue BACK_RIGHT_CAN_CODER_DIRECTION = SensorDirectionValue.CounterClockwise_Positive;
  //public static final SensorDirectionValue BACK_RIGHT_CAN_CODER_DIRECTION = SensorDirectionValue.Clockwise_Positive;
  public static final double BACK_RIGHT_CAN_CODER_OFFSET = 12.24;
}

final class DriveConstants {
  public static final double MAX_SPEED_METERS_PER_SECOND = 4.6; //IF YOU UP THE SPEED CHANGE ACCELERATION
  public static final double MAX_ROTATION_RADIANS_PER_SECOND = 11;
  public static final double MAX_TELEOP_ROTATION = .3;

  //Turning Motors
  public static final boolean FrontLeftTurningMotorReversed = true;
  public static final boolean BackLeftTurningMotorReversed = true;
  public static final boolean FrontRightTurningMotorReversed = true;
  public static final boolean BackRightTurningMotorReversed = true;

  public static final CANSparkMax.IdleMode FrontLeftTurningMotorBrake = IdleMode.kBrake;
  public static final CANSparkMax.IdleMode BackLeftTurningMotorBrake = IdleMode.kBrake;
  public static final CANSparkMax.IdleMode FrontRightTurningMotorBrake = IdleMode.kBrake;
  public static final CANSparkMax.IdleMode BackRightTurningMotorBrake = IdleMode.kBrake;

  //Drive Motors
  public static final boolean FrontLeftDriveMotorReversed  = true;
  public static final boolean BackLeftDriveMotorReversed   = true;
  public static final boolean FrontRightDriveMotorReversed = true;
  public static final boolean BackRightDriveMotorReversed  = true;

  public static final CANSparkMax.IdleMode FrontLeftDriveMotorBrake = IdleMode.kBrake;
  public static final CANSparkMax.IdleMode BackLeftDriveMotorBrake = IdleMode.kBrake;
  public static final CANSparkMax.IdleMode FrontRightDriveMotorBrake = IdleMode.kBrake;
  public static final CANSparkMax.IdleMode BackRightDriveMotorBrake = IdleMode.kBrake;

  //Wheel Base
  public static final double WHEEL_BASE_WIDTH = Units.inchesToMeters(23.25);
  public static final double WHEEL_BASE_LENGTH = Units.inchesToMeters(23.25);
  public static final double WHEEL_BASE_DIAMETER = Units.inchesToMeters(32.880);


  public static final Translation2d FRONT_LEFT_MODULE = new Translation2d(WHEEL_BASE_LENGTH/2, WHEEL_BASE_WIDTH/2);
  public static final Translation2d FRONT_RIGHT_MODULE = new Translation2d(WHEEL_BASE_LENGTH/2, -WHEEL_BASE_WIDTH/2);
  public static final Translation2d BACK_LEFT_MODULE = new Translation2d(-WHEEL_BASE_LENGTH/2, WHEEL_BASE_WIDTH/2);
  public static final Translation2d BACK_RIGHT_MODULE = new Translation2d(-WHEEL_BASE_LENGTH/2, -WHEEL_BASE_WIDTH/2);

  public static final SwerveDriveKinematics KINEMATICS =
  new SwerveDriveKinematics(
    FRONT_LEFT_MODULE,
    FRONT_RIGHT_MODULE,
    BACK_LEFT_MODULE,
    BACK_RIGHT_MODULE
    );

    /*
      public static final double MAX_ROTATION_RADIANS_PER_SECOND = (Math.PI/2);
      public static final double MAX_ROTATION_RADIANS_PER_SECOND_PER_SECOND = Math.PI;
      */

      public static final double GO_STRAIGHT_GAIN = 0.1;
      public static final double MIN_ALIGN_SPEED = 0.05;
}


final class Motors {
  //drive
  public static final CANSparkMax DRIVE_FRONT_LEFT = new CANSparkMax(17, CANSparkLowLevel.MotorType.kBrushless);
  public static final CANSparkMax DRIVE_FRONT_RIGHT = new CANSparkMax(11, CANSparkLowLevel.MotorType.kBrushless);
  public static final CANSparkMax DRIVE_BACK_RIGHT = new CANSparkMax(9, CANSparkLowLevel.MotorType.kBrushless);
  public static final CANSparkMax DRIVE_BACK_LEFT = new CANSparkMax(5, CANSparkLowLevel.MotorType.kBrushless);

  /*public static final CANSparkMax DRIVE_FRONT_LEFT = new CANSparkMax(15, CANSparkLowLevel.MotorType.kBrushless);
  public static final CANSparkMax DRIVE_FRONT_RIGHT = new CANSparkMax(11, CANSparkLowLevel.MotorType.kBrushless);
  public static final CANSparkMax DRIVE_BACK_RIGHT = new CANSparkMax(9, CANSparkLowLevel.MotorType.kBrushless);
  public static final CANSparkMax DRIVE_BACK_LEFT = new CANSparkMax(3, CANSparkLowLevel.MotorType.kBrushless);*/
  
  //turn
  public static final CANSparkMax ANGLE_FRONT_LEFT = new CANSparkMax(16, CANSparkLowLevel.MotorType.kBrushless);
  public static final CANSparkMax ANGLE_FRONT_RIGHT = new CANSparkMax(10, CANSparkLowLevel.MotorType.kBrushless);
  public static final CANSparkMax ANGLE_BACK_RIGHT = new CANSparkMax(8, CANSparkLowLevel.MotorType.kBrushless);
  public static final CANSparkMax ANGLE_BACK_LEFT = new CANSparkMax(4, CANSparkLowLevel.MotorType.kBrushless);
  
}

final class PoseEstConfig{
  public static final double POSITION_STD_DEV_X = 0.1;
  public static final double POSITION_STD_DEV_Y = 0.1;
  public static final double POSITION_STD_DEV_THETA = 10; 

  public static final double VISION_STD_DEV_X = 5;
  public static final double VISION_STD_DEV_Y = 5;
  public static final double VISION_STD_DEV_THETA = 500;
}

final class VisionConfig{
  public static final boolean IS_LIMELIGHT_MODE = true;
  public static String POSE_LIMELIGHT = "limelight-pose";
}

public class DriveSubsystem extends SubsystemBase{
  
  public static boolean isTeleOp = false;

  public boolean isTurning = false;
  public double targetAngle = 0.0;
  public double turnCommand = 0.0;
  public double fieldXCommand = 0;
  public double fieldYCommand = 0;

  ChassisSpeeds speedCommands = new ChassisSpeeds(0, 0, 0);
  ChassisSpeeds relativeCommands = new ChassisSpeeds(0,0,0);

  private final SwerveModule frontLeft = new SwerveModule(
    Motors.DRIVE_FRONT_LEFT, Motors.ANGLE_FRONT_LEFT, 
    CANCoders.FRONT_LEFT_CAN_CODER, 
    CANCoders.FRONT_LEFT_CAN_CODER_OFFSET,
    CANCoders.FRONT_LEFT_CAN_CODER_DIRECTION);

  private final SwerveModule frontRight = new SwerveModule(
    Motors.DRIVE_FRONT_RIGHT, Motors.ANGLE_FRONT_RIGHT, 
    CANCoders.FRONT_RIGHT_CAN_CODER, 
    CANCoders.FRONT_RIGHT_CAN_CODER_OFFSET,
    CANCoders.FRONT_RIGHT_CAN_CODER_DIRECTION);

  private final SwerveModule backLeft = new SwerveModule(
    Motors.DRIVE_BACK_LEFT, Motors.ANGLE_BACK_LEFT, 
    CANCoders.BACK_LEFT_CAN_CODER, 
    CANCoders.BACK_LEFT_CAN_CODER_OFFSET,
    CANCoders.BACK_LEFT_CAN_CODER_DIRECTION);

  private final SwerveModule backRight = new SwerveModule(
    Motors.DRIVE_BACK_RIGHT, Motors.ANGLE_BACK_RIGHT, 
    CANCoders.BACK_RIGHT_CAN_CODER, 
    CANCoders.BACK_RIGHT_CAN_CODER_OFFSET,
    CANCoders.BACK_RIGHT_CAN_CODER_DIRECTION);

  private final Pigeon2 gyro = Gyro.gyro;

  private final SwerveDriveKinematics kinematics = DriveConstants.KINEMATICS;

  //public final SwerveDrivePoseEstimator odometry;
  public final SwerveDriveOdometry odometry;

  public final SwerveDrivePoseEstimator poseEstimator;

  private Pose2d pose = new Pose2d();
  private Pose2d estimatedPose = new Pose2d();
  private Pose2d visionPose = new Pose2d();

  private static Limelight vision = new Limelight();

  public DriveSubsystem() {

    //this.odometry = new SwerveDrivePoseEstimator(kinematics, getGyroRotation2d(), getModulePositions(), pose);
    this.odometry = new SwerveDriveOdometry(kinematics, getGyroRotation2d(), getModulePositions());

    this.poseEstimator = new SwerveDrivePoseEstimator(
      kinematics, 
      getGyroRotation2d(), 
      getModulePositions(),
      estimatedPose,
      createStateStdDevs(
          PoseEstConfig.POSITION_STD_DEV_X,
          PoseEstConfig.POSITION_STD_DEV_Y,
          PoseEstConfig.POSITION_STD_DEV_THETA),
      createVisionMeasurementStdDevs(
          PoseEstConfig.VISION_STD_DEV_X,
          PoseEstConfig.VISION_STD_DEV_Y,
          PoseEstConfig.VISION_STD_DEV_THETA));

    reset();
    ConfigMotorDirections();
    registerLoggerObjects();

    //Configure Auto Builder last!
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

  private void updateDashboard(){
    //Field Oriented inputs
    SmartDashboard.putNumber("Field Oriented X Command (Forward)", fieldXCommand);
    SmartDashboard.putNumber("Field Oriented Y Command (Forward)", fieldYCommand);

    //Robot Relative inputs
    /*SmartDashboard.putNumber("Robot Relative vX Speed Command", speedCommands.vxMetersPerSecond);
    SmartDashboard.putNumber("Robot Relative vY Speed Command", speedCommands.vyMetersPerSecond);
    SmartDashboard.putNumber("Robot Relative Rotation Command", speedCommands.omegaRadiansPerSecond);*/

    /*SmartDashboard.putNumber("Drive Robot Relative vX Speed Command", relativeCommands.vxMetersPerSecond);
    SmartDashboard.putNumber("Drive Robot Relative vY Speed Command", relativeCommands.vyMetersPerSecond);*/
    SmartDashboard.putNumber("Drive Robot Relative Rotation Command", relativeCommands.omegaRadiansPerSecond);

    SmartDashboard.putNumber("Gyro Yaw", getIMU_Yaw());
    SmartDashboard.putNumber("Target Angle", targetAngle);

    //Swerve Module info
    /*SmartDashboard.putNumber("Front Left Speed Command", frontLeft.getCommandedSpeed());
    SmartDashboard.putNumber("Front Left Angle Command", frontLeft.getCommandedAngle());
    SmartDashboard.putNumber("Front Left Speed Setpoint", frontLeft.getControllerSetpoint());
    SmartDashboard.putNumber("Front Left Measured Speed", frontLeft.getModuleVelocity());
    SmartDashboard.putNumber("Front Left CANcoder Angle", (frontLeft.getAbsPositionZeroed()*(180/Math.PI)));

    SmartDashboard.putNumber("Front Right Speed Command", frontRight.getCommandedSpeed());
    SmartDashboard.putNumber("Front Right Angle Command", frontRight.getCommandedAngle());
    SmartDashboard.putNumber("Front Right Speed Setpoint", frontRight.getControllerSetpoint());
    SmartDashboard.putNumber("Front Right Measured Speed", frontRight.getModuleVelocity());
    SmartDashboard.putNumber("Front Right CANcoder Angle", (frontRight.getAbsPositionZeroed()*(180/Math.PI)));

    SmartDashboard.putNumber("Rear Right Speed Command", backRight.getCommandedSpeed());
    SmartDashboard.putNumber("Rear Right Angle Command", backRight.getCommandedAngle());
    SmartDashboard.putNumber("Rear Right Speed Setpoint", backRight.getControllerSetpoint());
    SmartDashboard.putNumber("Rear Right Measured Speed", backRight.getModuleVelocity());
    SmartDashboard.putNumber("Rear Right CANcoder Angle", (backRight.getAbsPositionZeroed()*(180/Math.PI)));
    
    SmartDashboard.putNumber("Rear Left Speed Command", backLeft.getCommandedSpeed());
    SmartDashboard.putNumber("Rear Left Angle Command", backLeft.getCommandedAngle());
    SmartDashboard.putNumber("Rear Left Speed Setpoint", backLeft.getControllerSetpoint());
    SmartDashboard.putNumber("Rear Left Measured Speed", backLeft.getModuleVelocity());
    SmartDashboard.putNumber("Rear Left CANcoder Angle", (backLeft.getAbsPositionZeroed()*(180/Math.PI)));*/

    //Pose Info
    SmartDashboard.putString("FMS Alliance", DriverStation.getAlliance().toString());
    SmartDashboard.putNumber("Pose2D X", pose.getX());
    SmartDashboard.putNumber("Pose2D Y", pose.getY());
    SmartDashboard.putNumber("Pose2D Rotation", pose.getRotation().getDegrees());

    SmartDashboard.putNumber("EstimatedPose X", estimatedPose.getX());
    SmartDashboard.putNumber("EstimatedPose Y", estimatedPose.getY());
    SmartDashboard.putNumber("EstimatedPose Rotation", estimatedPose.getRotation().getDegrees());

    SmartDashboard.putNumber("Vision Pose X", vision.getVisionBotPose().getX());
    SmartDashboard.putNumber("Vision Pose Y", vision.getVisionBotPose().getY());
    SmartDashboard.putNumber("Vision Pose Rotation", vision.getVisionBotPose().getRotation().getDegrees());
    SmartDashboard.putBoolean("Is Target Valid", vision.isValidPose());
    SmartDashboard.putNumber("Limelight Latency", vision.getTotalLatency());

    SmartDashboard.putNumber("Speaker tX", vision.getSpeaker_tx());
    SmartDashboard.putNumber("Speaker tY", vision.getSpeaker_ty());
    SmartDashboard.putBoolean("Speaker Found", vision.isSpeakerFound());
    SmartDashboard.putNumber("Distance to Speaker", getDistanceToSpeaker());
  }
  
  @Override
  public void periodic() {
    checkInitialAngle();
    updateOdometry();
    updateEstimatedPose();
    vision.update();
    visionPose = vision.getVisionBotPose();
    
    if (VisionConfig.IS_LIMELIGHT_MODE && visionPose != null) { // Limelight mode
      
      double currentTimestamp = vision.getTimestampSeconds(vision.getTotalLatency());
      
      if (vision.visionAccurate(visionPose)) 
      {
        poseEstimator.addVisionMeasurement(visionPose, currentTimestamp);
      }
    }
    updateDashboard();
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
        turnCommand = (targetAngle - getIMU_Yaw()) * DriveConstants.GO_STRAIGHT_GAIN;
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
    SwerveDriveKinematics.desaturateWheelSpeeds(swerveModuleStates, DriveConstants.MAX_SPEED_METERS_PER_SECOND);

    //Set the speed and angle of each module
    setDesiredState(swerveModuleStates);
  }

  public ChassisSpeeds getRobotRelativeSpeeds(){
    //This method is a supplier of ChassisSpeeds as determined by the module states.  This is required for PathPlanner 2024
    /*var robotSpeeds = kinematics.toChassisSpeeds(getModuleStates());
    robotSpeeds.vxMetersPerSecond *= -1;
    robotSpeeds.vyMetersPerSecond *= -1;
    robotSpeeds.omegaRadiansPerSecond *= -1;
    return robotSpeeds;*/

    return kinematics.toChassisSpeeds(getModuleStates());
  }

  public void setDesiredState(SwerveModuleState[] swerveModuleStates) {
    frontLeft.setDesiredState(swerveModuleStates[0]);
    frontRight.setDesiredState(swerveModuleStates[1]);
    backLeft.setDesiredState(swerveModuleStates[2]);
    backRight.setDesiredState(swerveModuleStates[3]);
  }

  public SwerveModuleState[] getModuleStates(){
    return new SwerveModuleState[] {
      frontLeft.getState(),
      frontRight.getState(),
      backLeft.getState(),
      backRight.getState()};
  }

  public void updateOdometry() {
    pose = odometry.update(
        getGyroRotation2d(),
        new SwerveModulePosition[] {
          frontLeft.getPosition(),
          frontRight.getPosition(),
          backLeft.getPosition(),
          backRight.getPosition()
        });
  }

  public void updateEstimatedPose(){
    estimatedPose = poseEstimator.update(
      getGyroRotation2d(), 
      getModulePositions());
  }


  public void resetOdometry(Pose2d pose) {
    odometry.resetPosition(getGyroRotation2d(), getModulePositions(), pose);
    poseEstimator.resetPosition(getGyroRotation2d(), getModulePositions(), pose);
  }

  public void resetPoseEstimation(Pose2d pose) {
    poseEstimator.resetPosition(getGyroRotation2d(), getModulePositions(), pose);
  }
  
  public SwerveModulePosition[] getModulePositions() {
    //Returns 
    return new SwerveModulePosition[] {
      frontLeft.getPosition(),
      frontRight.getPosition(),
      backLeft.getPosition(),
      backRight.getPosition()
    };
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
    //return odometry.getEstimatedPosition();
    return odometry.getPoseMeters();
  }

  public double getPoseRotationDegrees(){
    return pose.getRotation().getDegrees();
  }

  public void resetGyro(double angle) {
    gyro.setYaw(angle);
    targetAngle = angle;
  }

  public void resetModules() {
    frontLeft.zeroModule();
    frontRight.zeroModule();
    backLeft.zeroModule();
    backRight.zeroModule();
  }

  public void reset() {
    resetGyro(0);
    resetModules();
    resetOdometry(pose);
  }
  
  public double vision_aim_proportional(){    
    // kP (constant of proportionality)
    // this is a hand-tuned number that determines the aggressiveness of our proportional control loop
    // if it is too high, the robot will oscillate around.
    // if it is too low, the robot will never reach its target
    // if the robot never turns in the correct direction, kP should be inverted.
    double targetingAngularVelocity;
    double kP = .01;

    double error = vision.getSpeaker_tx();
    double min_rate = .075;

    // tx ranges from (-hfov/2) to (hfov/2) in degrees. If your target is on the rightmost edge of 
    // your limelight 3 feed, tx should return roughly 31 degrees.
    
    if(vision.isSpeakerFound()){  
      if(Math.abs(error) > 1.0){
        if(error > 0){
          targetingAngularVelocity = (error * kP) + min_rate;
        }else{
          targetingAngularVelocity = (error * kP) - min_rate;
        }

        // convert to radians per second for our drive method
        targetingAngularVelocity *= DriveConstants.MAX_ROTATION_RADIANS_PER_SECOND * DriveConstants.MAX_TELEOP_ROTATION;
      }else{
        targetingAngularVelocity = 0.0;
      }   
    }else{
      //Speaker not found, turn until we find it
      targetingAngularVelocity = 2.0;
    }

    return targetingAngularVelocity;
  }

  public boolean isSpeakerDataValid(){
    return vision.isSpeakerFound();
  }

  public double getDistanceToSpeaker(){
    double ty = vision.getSpeaker_ty();
    double camera_pitch = 20;
    ty += camera_pitch;
    // tan(w)=y/x -> x = y/tan(w)
    double distance = 1.055/(Math.tan(Math.toRadians(ty)));
    return distance;
  }

  public double getVisionTargetAngle(){
    return vision.getSpeaker_tx();
  }

  public void ConfigMotorDirections() {
    Motors.ANGLE_FRONT_LEFT.setInverted(DriveConstants.FrontLeftTurningMotorReversed);
    Motors.ANGLE_FRONT_RIGHT.setInverted(DriveConstants.FrontRightTurningMotorReversed);
    Motors.ANGLE_BACK_LEFT.setInverted(DriveConstants.BackLeftTurningMotorReversed);
    Motors.ANGLE_BACK_RIGHT.setInverted(DriveConstants.BackRightTurningMotorReversed);
    Motors.DRIVE_FRONT_LEFT.setInverted(DriveConstants.FrontLeftDriveMotorReversed);
    Motors.DRIVE_FRONT_RIGHT.setInverted(DriveConstants.FrontRightDriveMotorReversed);
    Motors.DRIVE_BACK_LEFT.setInverted(DriveConstants.BackLeftDriveMotorReversed);
    Motors.DRIVE_BACK_RIGHT.setInverted(DriveConstants.BackRightDriveMotorReversed);

    Motors.DRIVE_FRONT_LEFT.setIdleMode(DriveConstants.FrontLeftDriveMotorBrake);
    Motors.DRIVE_FRONT_RIGHT.setIdleMode(DriveConstants.FrontRightDriveMotorBrake);
    Motors.DRIVE_BACK_LEFT.setIdleMode(DriveConstants.BackLeftDriveMotorBrake);
    Motors.DRIVE_BACK_RIGHT.setIdleMode(DriveConstants.BackRightDriveMotorBrake);

    Motors.ANGLE_FRONT_LEFT.setIdleMode(DriveConstants.FrontLeftTurningMotorBrake);
    Motors.ANGLE_FRONT_RIGHT.setIdleMode(DriveConstants.FrontRightTurningMotorBrake);
    Motors.ANGLE_BACK_LEFT.setIdleMode(DriveConstants.BackLeftTurningMotorBrake);
    Motors.ANGLE_BACK_RIGHT.setIdleMode(DriveConstants.BackRightTurningMotorBrake);
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
                DriveConstants.MAX_SPEED_METERS_PER_SECOND, 
                (DriveConstants.WHEEL_BASE_DIAMETER/2),
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

  /**
   * Creates a vector of standard deviations for the states. Standard deviations of model states.
   * Increase these numbers to trust your model's state estimates less.
   *
   * @param x in meters
   * @param y in meters
   * @param theta in degrees
   * @return the Vector of standard deviations need for the poseEstimator
   */
  public Vector<N3> createStateStdDevs(double x, double y, double theta) {
    return VecBuilder.fill(x, y, Units.degreesToRadians(theta));
  }

  /**
   * Creates a vector of standard deviations for the vision measurements. Standard deviations of
   * global measurements from vision. Increase these numbers to trust global measurements from
   * vision less.
   *
   * @param x in meters
   * @param y in meters
   * @param theta in degrees
   * @return the Vector of standard deviations need for the poseEstimator
   */
  public Vector<N3> createVisionMeasurementStdDevs(double x, double y, double theta) {
    return VecBuilder.fill(x, y, Units.degreesToRadians(theta));
  }


}
