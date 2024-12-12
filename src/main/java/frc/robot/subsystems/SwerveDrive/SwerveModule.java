package frc.robot.subsystems.SwerveDrive;

import com.ctre.phoenix6.configs.CANcoderConfiguration;
import com.ctre.phoenix6.hardware.CANcoder;
import com.ctre.phoenix6.signals.SensorDirectionValue;
import com.revrobotics.spark.SparkBase;
import com.revrobotics.spark.SparkClosedLoopController;
import com.revrobotics.spark.SparkMax;
import com.revrobotics.spark.config.*;
import com.revrobotics.RelativeEncoder;

import edu.wpi.first.math.controller.PIDController;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.kinematics.SwerveModulePosition;
import edu.wpi.first.math.kinematics.SwerveModuleState;
import edu.wpi.first.math.util.Units;
import edu.wpi.first.units.measure.Voltage;

final class ModuleConstants {
 
  // kinematics
  public static final double WHEEL_DIAMETER_METERS = Units.inchesToMeters(4);
  public static final double DRIVE_GEAR_RATIO = 1 / ((14.0 / 50.0) * (27.0 / 17.0) * (15.0 / 45.0));
  public static final double STEER_GEAR_RATIO = 1 / ((14.0 / 50.0) * (10.0 / 60.0));
  public static final double DRIVE_METERS_PER_ENCODER_REV = (WHEEL_DIAMETER_METERS * Math.PI) / DRIVE_GEAR_RATIO;
  public static final double DRIVE_ENCODER_MPS_PER_REV = DRIVE_METERS_PER_ENCODER_REV / 60; 
  
  // max turn speed = (5400/ 21.43) revs per min 240 revs per min 4250 deg per min
  public static final double MODULE_TURN_PID_CONTROLLER_P = 3.4;
  public static final double MODULE_TURN_PID_CONTROLLER_I = 0;
  public static final double MODULE_TURN_PID_CONTROLLER_D = 0;
  
  public static final double MODULE_DRIVE_PID_CONTROLLER_F = 1;
  public static final double MODULE_DRIVE_PID_CONTROLLER_P = .0005;
  public static final double MODULE_DRIVE_PID_CONTROLLER_I = 0;
  public static final double MODULE_DRIVE_PID_CONTROLLER_D = 0;
  
  public static final double MODULE_DRIVE_KV = 2.6527;
  public static final double MODULE_DRIVE_KS = 0.098218;
  public static final double MODULE_DRIVE_KA = 0.66568;

  public static final double CLOSED_LOOP_RAMP_RATE = .5;
  public static final int SMART_CURRENT_LIMIT = 30;

  public static final double MAX_SPEED_METERS_PER_SECOND = 4.6;

  public static final SparkBaseConfig.IdleMode DRIVE_IDLE_MODE  = SparkBaseConfig.IdleMode.kBrake;
  public static final SparkBaseConfig.IdleMode TURN_IDLE_MODE   = SparkBaseConfig.IdleMode.kBrake;

  /*
  public static final double MAX_METERS_PER_SECOND = 4.4; //5600 * DRIVE_ENCODER_MPS_PER_REV;
  public static final double TURNING_DEGREES_PER_ENCODER_REV = 360 / STEER_GEAR_RATIO;
  public static final double RADIANS_PER_ENCODER_REV = TURNING_DEGREES_PER_ENCODER_REV * (Math.PI/180);
  public static final double MAX_MODULE_ROTATION_RADIANS_PER_SECOND = Math.PI/2;
  public static final double MAX_MODULE_ROTATION_RADIANS_PER_SECOND_PER_SECOND = Math.PI;
  */
}


public class SwerveModule{
  private final SparkMax driveMotor;
  private final SparkMax turningMotor;

  private final RelativeEncoder driveEncoder;

  private final CANcoder absEncoder;

  private final SparkClosedLoopController drivePIDController;
  private final PIDController turningPIDController = new PIDController(ModuleConstants.MODULE_TURN_PID_CONTROLLER_P, ModuleConstants.MODULE_TURN_PID_CONTROLLER_I, ModuleConstants.MODULE_TURN_PID_CONTROLLER_D);

  private double commandedSpeed;
  private double commandedAngle;

  public SwerveModule(SparkMax driveMotor, SparkMax turnMotor, CANcoder absEncoder, double absOffset, SensorDirectionValue directionValue) {
    this.driveMotor = driveMotor;
    this.turningMotor = turnMotor;
    this.absEncoder = absEncoder;

    driveEncoder = driveMotor.getEncoder();

    //Setup Encoder Config
    EncoderConfig driveEncoderConfig = new EncoderConfig();
    driveEncoderConfig.positionConversionFactor(ModuleConstants.DRIVE_METERS_PER_ENCODER_REV);
    driveEncoderConfig.velocityConversionFactor(ModuleConstants.DRIVE_ENCODER_MPS_PER_REV);

    //Setup Closed Loop Config settings
    ClosedLoopConfig drivePIDF_Config = new ClosedLoopConfig();
    drivePIDF_Config.p(ModuleConstants.MODULE_DRIVE_PID_CONTROLLER_P);
    drivePIDF_Config.i(ModuleConstants.MODULE_DRIVE_PID_CONTROLLER_I);
    drivePIDF_Config.d(ModuleConstants.MODULE_DRIVE_PID_CONTROLLER_D);
    drivePIDF_Config.velocityFF(ModuleConstants.MODULE_DRIVE_PID_CONTROLLER_F);

    //Setup Drive Motor Config
    SparkMaxConfig driveConfig = new SparkMaxConfig();
    driveConfig.idleMode(ModuleConstants.DRIVE_IDLE_MODE);
    driveConfig.closedLoopRampRate(ModuleConstants.CLOSED_LOOP_RAMP_RATE);
    driveConfig.smartCurrentLimit(ModuleConstants.SMART_CURRENT_LIMIT);
    
    //Apply Encoder Config to this Spark Config
    driveConfig.apply(driveEncoderConfig);

    //Apply Closed Loop Config to this Spark Config
    driveConfig.apply(drivePIDF_Config);

    //Finally, write all the config settings to the controller!
    driveMotor.configure(driveConfig, SparkBase.ResetMode.kResetSafeParameters, SparkBase.PersistMode.kPersistParameters);

    //Set absolute encoder magnet configuration
    CANcoderConfiguration config = new CANcoderConfiguration();
    double offsetRotations = -absOffset/360;
    config.MagnetSensor.MagnetOffset = offsetRotations;
    config.MagnetSensor.SensorDirection = directionValue;
    config.MagnetSensor.AbsoluteSensorDiscontinuityPoint = 1;
    this.absEncoder.getConfigurator().apply(config);

    // Limit the PID Controller's input range between -pi and pi and set the input
    // to be continuous.
    this.turningPIDController.enableContinuousInput(-Math.PI, Math.PI);

    this.drivePIDController = driveMotor.getClosedLoopController();
  }

  /**
   * Returns the current state of the module.
   *
   * @return The current state of the module.
   */
  public SwerveModuleState getState() {
    return new SwerveModuleState(driveEncoder.getVelocity(), new Rotation2d(getAbsPositionZeroed()));
  }

  public double getVelocity() {
    return driveEncoder.getVelocity();
  }

  public Rotation2d getRotation2d() {
    return new Rotation2d(Units.degreesToRadians(getAbsPositionZeroed()));
  }

  /**
   * Returns the current position of the module.
   *
   * @return The current position of the module.
   */
  public SwerveModulePosition getPosition() {
    return new SwerveModulePosition(driveEncoder.getPosition(), new Rotation2d(getAbsPositionZeroed()));
  }

  public double getLinearPosition() {
    return driveEncoder.getPosition();
  }

  public void zeroModule() {
    driveEncoder.setPosition(0);
  }

  public double getAbsPositionZeroed() {
    //CANcoders in Phoenix return rotations 0 to 1
    var angle = absEncoder.getAbsolutePosition();
    return angle.getValueAsDouble()*2.0*Math.PI;
  }

  public double getCommandedSpeed(){
    return commandedSpeed;
  }

  public double getModuleVelocity(){
    return driveEncoder.getVelocity();
  }

  public double getCommandedAngle(){
    return commandedAngle;
  }

  public double getControllerSetpoint(){
    return driveMotor.get();
  }

  /**
   * Sets the desired state for the module.
   *
   * @param desiredState Desired state with speed and angle.
   */
  public void setDesiredState(SwerveModuleState desiredState) {
    
    //Set SmartDashboard variables
    commandedSpeed = desiredState.speedMetersPerSecond;
    commandedAngle = desiredState.angle.getDegrees();

    if(Math.abs(desiredState.speedMetersPerSecond) < .2){
      driveMotor.set(0);
      turningMotor.set(0);
      return;
    }else{
      // Optimize the reference state to avoid spinning further than 90 degrees
      desiredState.optimize(new Rotation2d(getAbsPositionZeroed()));

      //Set SmartDashboard variables
      commandedSpeed = desiredState.speedMetersPerSecond;
      commandedAngle = desiredState.angle.getDegrees();

      //Calculate the motor speed output and pass the value to the SPARK PID Controller object
      var desiredSpeed = desiredState.speedMetersPerSecond/ModuleConstants.MAX_SPEED_METERS_PER_SECOND;
      drivePIDController.setReference(desiredSpeed, SparkMax.ControlType.kVelocity);

      // Calculate the turning motor output from the turning PID controller.
      final double turnOutput = turningPIDController.calculate(getAbsPositionZeroed(), desiredState.angle.getRadians());
      turningMotor.setVoltage(turnOutput);
    }
  }

  public void setSysIDVoltage(Voltage volts){
    //Set drive motor open-loop voltage
    driveMotor.setVoltage(volts.magnitude());
    
    // Calculate the turning motor output from the turning PID controller.  For SysID, all motors should be facing "forward"
    final double turnOutput = turningPIDController.calculate(getAbsPositionZeroed(), 0);
    turningMotor.setVoltage(turnOutput);
  }

  public double getDriveMotorVoltage(){
    return (driveMotor.getAppliedOutput()*driveMotor.getBusVoltage());
  }

  public double getBusVoltage(){
    return driveMotor.getBusVoltage();
  }
}