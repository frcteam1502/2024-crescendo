package frc.robot.subsystems.SwerveDrive;

import com.ctre.phoenix6.hardware.CANcoder;
import com.revrobotics.CANSparkMax;
import com.revrobotics.RelativeEncoder;
import com.revrobotics.SparkPIDController;
import edu.wpi.first.math.controller.PIDController;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.kinematics.SwerveModulePosition;
import edu.wpi.first.math.kinematics.SwerveModuleState;
import edu.wpi.first.util.sendable.Sendable;
import edu.wpi.first.util.sendable.SendableBuilder;

public class SwerveModule implements Sendable {
  private static final double DEAD_ZONE = .2; // m/s

  private final CANSparkMax driveMotor;
  private final CANSparkMax turningMotor;
  private final RelativeEncoder driveEncoder;
  private final CANcoder absEncoder;
  private final SparkPIDController drivePIDController;
  private final PIDController turningPIDController;
  public final double maxSpeed;

  private double commandedSpeed;
  private double commandedAngle;

  public SwerveModule(team1502.configuration.builders.motors.SwerveModule config) {
    config.setSwerveModuleInstance(this); // for logging
    
    maxSpeed = config.calculateMaxSpeed();

    this.driveMotor = config.DrivingMotor().buildSparkMax();
    this.driveEncoder = config.DrivingMotor().buildRelativeEncoder();
    this.drivePIDController = config.DrivingMotor().buildPIDController();

    this.turningMotor = config.TurningMotor().buildSparkMax();
    this.turningPIDController = config.TurningMotor().PID().createPIDController();

    this.absEncoder = config.Encoder().buildCANcoder();
  }

  public SwerveModuleState getState() {
    return new SwerveModuleState(driveEncoder.getVelocity(), new Rotation2d(getAbsPositionRadians()));
  }

  public double getVelocity() {
    return driveEncoder.getVelocity();
  }

  public SwerveModulePosition getPosition() {
    return new SwerveModulePosition(driveEncoder.getPosition(), new Rotation2d(getAbsPositionRadians()));
  }

  public void zeroModule() {
    driveEncoder.setPosition(0);
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

    if (Math.abs(desiredState.speedMetersPerSecond) < DEAD_ZONE) {
      driveMotor.set(0);
      turningMotor.set(0);
      return;
    } else {
      // Optimize the reference state to avoid spinning further than 90 degrees
      SwerveModuleState state = SwerveModuleState.optimize(desiredState, new Rotation2d(getAbsPositionRadians()));

      //Set SmartDashboard variables
      commandedSpeed = desiredState.speedMetersPerSecond;
      commandedAngle = desiredState.angle.getDegrees();


      // Calculate the turning motor output from the turning PID controller.
      final double turnOutput = turningPIDController.calculate(getAbsPositionRadians(), state.angle.getRadians());
      turningMotor.setVoltage(turnOutput);

      var desiredSpeed = state.speedMetersPerSecond/maxSpeed;
      drivePIDController.setReference(desiredSpeed, CANSparkMax.ControlType.kVelocity);
    }
  }

  @Override
  public void initSendable(SendableBuilder builder) {
    builder.setSmartDashboardType("SwerveModule");
    builder.addDoubleProperty("Speed Command", ()->getCommandedSpeed(), null);
    builder.addDoubleProperty("Angle Command", ()->getCommandedAngle(), null);
    builder.addDoubleProperty("Speed Setpoint", ()->getControllerSetpoint(), null);
    builder.addDoubleProperty("Speed", ()->getModuleVelocity(), null);
    builder.addDoubleProperty("Angle", ()->getAbsPositionDegrees(), null);
  }

  //CANcoders in Phoenix return rotations 0 to 1
  private double getAbsPositionRadians() { return absEncoder.getAbsolutePosition().getValue()*2.0*Math.PI; }
  private double getAbsPositionDegrees() { return absEncoder.getAbsolutePosition().getValue()*360; }
  private double getCommandedSpeed() { return commandedSpeed; }
  private double getModuleVelocity() { return driveEncoder.getVelocity(); }
  private double getCommandedAngle() { return commandedAngle; }
  private double getControllerSetpoint() { return driveMotor.get(); }

}