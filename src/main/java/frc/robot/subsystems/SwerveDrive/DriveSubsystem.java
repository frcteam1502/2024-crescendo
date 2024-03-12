package frc.robot.subsystems.SwerveDrive;

import frc.robot.Logger;
import frc.robot.commands.ControllerCommands;
import frc.robot.subsystems.Vision.Limelight;

import team1502.configuration.annotations.DefaultCommand;
import team1502.configuration.factory.RobotConfiguration;

import com.ctre.phoenix6.hardware.Pigeon2;
import com.pathplanner.lib.auto.AutoBuilder;
import com.pathplanner.lib.util.HolonomicPathFollowerConfig;
import com.pathplanner.lib.util.PIDConstants;
import com.pathplanner.lib.util.ReplanningConfig;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

import edu.wpi.first.math.VecBuilder;
import edu.wpi.first.math.Vector;
import edu.wpi.first.math.estimator.SwerveDrivePoseEstimator;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.kinematics.ChassisSpeeds;
import edu.wpi.first.math.kinematics.SwerveDriveKinematics;
import edu.wpi.first.math.kinematics.SwerveDriveOdometry;
import edu.wpi.first.math.kinematics.SwerveModulePosition;
import edu.wpi.first.math.kinematics.SwerveModuleState;
import edu.wpi.first.math.numbers.N3;
import edu.wpi.first.math.util.Units;
import edu.wpi.first.util.sendable.SendableBuilder;
import edu.wpi.first.wpilibj2.command.SubsystemBase;

import frc.robot.GameState;

final class PoseEstConfig{
  public static final double POSITION_STD_DEV_X = 0.1; // Standard deviation meters
  public static final double POSITION_STD_DEV_Y = 0.1;
  public static final double POSITION_STD_DEV_THETA = 10; // Standard deviation degrees
  
  public static final double VISION_STD_DEV_X = 5;
  public static final double VISION_STD_DEV_Y = 5;
  public static final double VISION_STD_DEV_THETA = 500;

  /**
   * Creates a vector of standard deviations for the states. Standard deviations of model states.
   * Increase these numbers to trust your model's state estimates less.
   */ 
  public static Vector<N3> createStateStdDevs() {
    return createVector(POSITION_STD_DEV_X, POSITION_STD_DEV_Y, POSITION_STD_DEV_THETA);
  }

  /** Creates a vector of standard deviations for the vision measurements. Standard deviations of
  * global measurements from vision. Increase these numbers to trust global measurements from
  * vision less.
  */
  public static Vector<N3> createVisionMeasurementStdDevs() {
    return createVector(VISION_STD_DEV_X, VISION_STD_DEV_Y, VISION_STD_DEV_THETA);
  }
  
  static Vector<N3> createVector(double x, double y, double theta) {
    return VecBuilder.fill(x, y, Units.degreesToRadians(theta));
  }
}

final class VisionConfig{
  public static final boolean IS_LIMELIGHT_MODE = true;
  public static String POSE_LIMELIGHT = "limelight-pose";
}

@DefaultCommand(command = ControllerCommands.class)
public class DriveSubsystem extends SubsystemBase{
  
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
  private final SwerveDriveOdometry odometry;
  private final SwerveDrivePoseEstimator poseEstimator;
  
  private final double goStraightGain;
  private final double maxSpeed;
  private final double driveBaseRadius;

  private Pose2d pose = new Pose2d();
  private Pose2d estimatedPose = new Pose2d();
  private Pose2d visionPose = new Pose2d();

  private final Limelight vision;
  private final boolean limelightEnabled;

  public DriveSubsystem(RobotConfiguration config, Limelight limelight) {
    limelightEnabled = !config.isDisabled("limelight");
    this.vision = limelight;
    gyro = config.Pigeon2().buildPigeon2();
    swerveModules = new SwerveModules(config);
    kinematics = config.SwerveDrive().getKinematics();
    maxSpeed = config.SwerveDrive().calculateMaxSpeed();
    driveBaseRadius = config.SwerveDrive().Chassis().getDriveBaseRadius();
    goStraightGain = config.SwerveDrive().GoStraightGain();

    this.odometry = new SwerveDriveOdometry(kinematics, getGyroRotation2d(), getModulePositions());
    this.poseEstimator = new SwerveDrivePoseEstimator(
      kinematics,
      getGyroRotation2d(),
      getModulePositions(),
      estimatedPose,
      PoseEstConfig.createStateStdDevs(),
      PoseEstConfig.createVisionMeasurementStdDevs());

    reset();
    registerLoggerObjects(config);

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
    SmartDashboard.putData(this);
    swerveModules.send();
  }
  
  private boolean wasAutonExecuted = false;

  @Override
  public void periodic() {

    if (GameState.isFirst()) {
      if (GameState.isAutonomous()) {
        wasAutonExecuted = true; }
      else if (GameState.isTeleop() && wasAutonExecuted) {
        wasAutonExecuted = false;
        resetGyro(getPoseRotationDegrees()); }
    }
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
        targetAngle = getIMU_Yaw(); }
      else if (rot == 0 && isTurning) {
        //Driver stopped commanding a turn
        isTurning = false;
      }

      if (isTurning) {
        turnCommand = rot; }
      else { 
        turnCommand = (targetAngle - getIMU_Yaw()) * goStraightGain; 
      }      
    }
    //Set Dashboard variables
    fieldXCommand = xSpeed;
    fieldYCommand = ySpeed;

    if(fieldRelative){
      speedCommands = ChassisSpeeds.fromFieldRelativeSpeeds(xSpeed, ySpeed, turnCommand, getGyroRotation2d()); }
    else {
      speedCommands.omegaRadiansPerSecond = turnCommand;
      speedCommands.vxMetersPerSecond = xSpeed;
      speedCommands.vyMetersPerSecond = ySpeed;
    }

    driveRobotRelative(speedCommands);
  }

  private void driveRobotRelative(ChassisSpeeds robotRelativeSpeeds){
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

  private ChassisSpeeds getRobotRelativeSpeeds() { return kinematics.toChassisSpeeds(getModuleStates()); }
  private SwerveModuleState[] getModuleStates() { return swerveModules.getModuleStates(); }
  private SwerveModulePosition[] getModulePositions() { return swerveModules.getModulePositions(); }
  private void setDesiredState(SwerveModuleState[] swerveModuleStates) { swerveModules.setDesiredState(swerveModuleStates); }
  private void resetModules() { swerveModules.resetModules(); }

  private void updateOdometry() {
    pose = odometry.update(getGyroRotation2d(), getModulePositions());
  }

  private void updateEstimatedPose(){
    estimatedPose = poseEstimator.update(getGyroRotation2d(), getModulePositions());
  }

  private void resetOdometry(Pose2d pose) {
    odometry.resetPosition(getGyroRotation2d(), getModulePositions(), pose);
  }

  private void resetPoseEstimation(Pose2d pose) {
    poseEstimator.resetPosition(getGyroRotation2d(), getModulePositions(), pose);
  }
  
  
  private SwerveModuleState[] makeSwerveModuleState(double[] speeds, double[] angles) {
    SwerveModuleState[] moduleStates = new SwerveModuleState[angles.length];
    for(int i = 0; i < angles.length; i++) moduleStates[i] = new SwerveModuleState(speeds[i], new Rotation2d(Units.degreesToRadians(angles[i])));
    return moduleStates;
  }

  private void setToBreak() {
    resetModules();
    double[] speeds = {0, 0, 0, 0};
    double[] angles = {90, 90, 90, 90};
    SwerveModuleState[] moduleStates = makeSwerveModuleState(speeds, angles);
    setDesiredState(moduleStates);
  }

  private Rotation2d getGyroRotation2d() {
    return new Rotation2d(Units.degreesToRadians(getIMU_Yaw()));
  }

  private Pose2d getPose2d() {
    return odometry.getPoseMeters();
  }

  private double getPoseRotationDegrees(){
    return pose.getRotation().getDegrees();
  }

  private void resetGyro(double angle) {
    gyro.setYaw(angle);
    //targetAngle = angle;
  }

  private void reset() {
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
    double min_rate = 0.075;

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
        targetingAngularVelocity *= maxSpeed * ControllerCommands.MAX_TELEOP_ROTATION;
      }else{
        targetingAngularVelocity = 0.0;
      }   
    }else{
      //Speaker not found, turn until we find it
      targetingAngularVelocity = 0.75;
    }

    return targetingAngularVelocity;
  }

  public boolean isSpeakerDataValid(){
    return vision.isSpeakerFound();
  }

  private double getDistanceToSpeaker(){
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

  @Override
  public void initSendable(SendableBuilder builder) {
    super.initSendable(builder);
    //builder.setSmartDashboardType("Subsystem");

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
    //SendableRegistry.addLW(swerveModules, SendableRegistry.getSubsystem(this), "SwerveModule");

        //Pose Info
    builder.addStringProperty("FMS Alliance", ()->DriverStation.getAlliance().toString(), null);
    builder.addDoubleProperty("Pose2D X", ()->pose.getX(), null);
    builder.addDoubleProperty("Pose2D Y", ()->pose.getY(), null);
    builder.addDoubleProperty("Pose2D Rotation", ()->pose.getRotation().getDegrees(), null);

    if (limelightEnabled) {
      builder.addDoubleProperty("Vision Pose X", ()->vision.getVisionBotPose().getX(), null);
      builder.addDoubleProperty("Vision Pose Y", ()->vision.getVisionBotPose().getY(), null);
      builder.addDoubleProperty("Vision Pose Rotation", ()->vision.getVisionBotPose().getRotation().getDegrees(), null);
      builder.addBooleanProperty("Is Target Valid", ()->vision.isValidPose(), null);
      builder.addDoubleProperty("Limelight Latency", ()->vision.getTotalLatency(), null);

      builder.addDoubleProperty("Speaker tX", ()->vision.getSpeaker_tx(), null);
      builder.addDoubleProperty("Speaker tY", ()->vision.getSpeaker_ty(), null);
      builder.addBooleanProperty("Speaker Found", ()->vision.isSpeakerFound(), null);
      builder.addDoubleProperty("Distance to Speaker", ()->getDistanceToSpeaker(), null);
    }
  }

  private void registerLoggerObjects(RobotConfiguration config){
    config.registerLoggerObjects(
        (n, r)->Logger.RegisterCanSparkMax(n,r),
        p->Logger.RegisterPigeon(p),
        (n, r)->Logger.RegisterCanCoder(n,r),
        (n, d)->Logger.RegisterSensor(n,d)
      );
  }

}
