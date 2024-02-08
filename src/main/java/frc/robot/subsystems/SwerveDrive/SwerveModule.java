package frc.robot.subsystems.SwerveDrive;

import com.ctre.phoenix6.configs.CANcoderConfiguration;
import com.ctre.phoenix6.hardware.CANcoder;
import com.ctre.phoenix6.signals.AbsoluteSensorRangeValue;
import com.revrobotics.CANSparkMax;
import com.revrobotics.RelativeEncoder;
import com.revrobotics.SparkPIDController;
import com.revrobotics.CANSparkBase.ControlType;

import edu.wpi.first.math.controller.PIDController;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.kinematics.SwerveModulePosition;
import edu.wpi.first.math.kinematics.SwerveModuleState;
import edu.wpi.first.math.util.Units;
import edu.wpi.first.util.sendable.Sendable;
import edu.wpi.first.util.sendable.SendableBuilder;
import team1502.configuration.builders.motors.CANCoder;
import team1502.configuration.builders.motors.MotorController;

public class SwerveModule implements Sendable {
  private final CANSparkMax driveMotor;
  private final CANSparkMax turningMotor;
  private final RelativeEncoder driveEncoder;
  private final CANcoder absEncoder;
  private final SparkPIDController drivePIDController;
  private final PIDController turningPIDController;

  private double commandedSpeed;
  private double commandedAngle;

  public SwerveModule(team1502.configuration.builders.motors.SwerveModule config) {
    config.setSwerveModuleInstance(this); // for logging
    this.driveMotor = config.DrivingMotor().buildSparkMax();
    this.turningMotor = config.TurningMotor().buildSparkMax();
    this.absEncoder = config.Encoder().buildCANcoder();

    driveMotor.setClosedLoopRampRate(config.getDouble("closedLoopRampRate"));
    driveMotor.setSmartCurrentLimit(config.getInt("smartCurrentLimit"));

    driveEncoder = driveMotor.getEncoder();
    driveEncoder.setPositionConversionFactor(config.getPositionConversionFactor());
    driveEncoder.setVelocityConversionFactor(config.getVelocityConversionFactor());

    var pid = config.TurningMotor().PID();
    this.turningPIDController = new PIDController(pid.P(), pid.I(), pid.D());
    this.turningPIDController.enableContinuousInput(-Math.PI, Math.PI); 

    pid = config.DrivingMotor().PID();
    this.drivePIDController = this.driveMotor.getPIDController();
    this.drivePIDController.setP(pid.P());
    this.drivePIDController.setI(pid.I());
    this.drivePIDController.setD(pid.D());
    this.drivePIDController.setFF(pid.FF());
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

  public void zeroModule() {
    driveEncoder.setPosition(0);
  }

  public double getAbsPositionZeroed() {
    //CANcoders in Phoenix return rotations 0 to 1
    var angle = absEncoder.getAbsolutePosition();
    return angle.getValue()*2.0*Math.PI;
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
      SwerveModuleState state = SwerveModuleState.optimize(desiredState, new Rotation2d(getAbsPositionZeroed()));

      // Calculate the turning motor output from the turning PID controller.
      final double turnOutput = turningPIDController.calculate(getAbsPositionZeroed(), state.angle.getRadians());

      drivePIDController.setReference(state.speedMetersPerSecond, ControlType.kVelocity);
      turningMotor.setVoltage(turnOutput);
    }
  }

  @Override
  public void initSendable(SendableBuilder builder) {
    builder.setSmartDashboardType("SwerveModule");
    builder.addDoubleProperty("Speed Command", ()->getCommandedSpeed(), null);
    builder.addDoubleProperty("Angle Command", ()->getCommandedAngle(), null);
    builder.addDoubleProperty("Speed", ()->getModuleVelocity(), null);
    builder.addDoubleProperty("Angle", ()->getAbsPositionZeroed()*(180/Math.PI), null);
  }
}