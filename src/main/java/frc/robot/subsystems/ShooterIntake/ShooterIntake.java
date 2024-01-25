// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.subsystems.ShooterIntake;

import com.revrobotics.CANSparkMax;
import com.revrobotics.RelativeEncoder;
import com.revrobotics.SparkPIDController;
import com.revrobotics.CANSparkBase.ControlType;
import com.revrobotics.CANSparkLowLevel;
import com.revrobotics.CANSparkBase.IdleMode;

import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Operator;

final class Motors{
  public static final CANSparkMax SHOOTER_LEAD = new CANSparkMax(12, CANSparkLowLevel.MotorType.kBrushless);
  public static final CANSparkMax SHOOTER_FOLLOW = new CANSparkMax(13, CANSparkLowLevel.MotorType.kBrushless);
  //public static final CANSparkMax INTAKE = new CANSparkMax(14, CANSparkLowLevel.MotorType.kBrushless);
  public static final boolean SHOOTER_MOTOR_REVERSED = true;
  public static final boolean INTAKE_MOTOR_REVERSED = true;  

  public static final CANSparkMax.IdleMode SHOOTER_LEAD_BRAKE_MODE = IdleMode.kCoast;
  public static final CANSparkMax.IdleMode SHOOTER_FOLLOW_BRAKE_MODE = IdleMode.kCoast;
  public static final CANSparkMax.IdleMode INTAKE_BRAKE_MODE = IdleMode.kCoast;
}

final class PIDFValues{
  public static final double SHOOTER_PID_P = 0;
  public static final double SHOOTER_PID_I = 0;
  public static final double SHOOTER_PID_D = 0;
  public static final double SHOOTER_PID_FF = 1;
}

public class ShooterIntake extends SubsystemBase {
  /** Creates a new ShooterIntake. */
  private final CANSparkMax shooterLead;
  private final CANSparkMax shooterFollow;

  private final RelativeEncoder leadEncoder;
  private final RelativeEncoder followEncoder;

  private final SparkPIDController shooterPID;


  public ShooterIntake() {
    //Initialize the motor objects
    shooterLead = Motors.SHOOTER_LEAD;
    shooterFollow = Motors.SHOOTER_FOLLOW;

    //Initialize the motor controllers, set follow motor to follow the lead motor in opposite direction
    shooterLead.setInverted(Motors.SHOOTER_MOTOR_REVERSED);
    shooterLead.setIdleMode(Motors.SHOOTER_LEAD_BRAKE_MODE);

    shooterFollow.setIdleMode(Motors.SHOOTER_FOLLOW_BRAKE_MODE);
    shooterFollow.follow(shooterLead, true);

    //Initialize the encoder objects
    leadEncoder = shooterLead.getEncoder();
    followEncoder = shooterFollow.getEncoder();

    //Initialize the Shooter velocity PID
    shooterPID = shooterLead.getPIDController();
    shooterPID.setP(PIDFValues.SHOOTER_PID_P);
    shooterPID.setI(PIDFValues.SHOOTER_PID_I);
    shooterPID.setD(PIDFValues.SHOOTER_PID_P);
    shooterPID.setFF(PIDFValues.SHOOTER_PID_FF);
  }

  @Override
  public void periodic() {
    // This method will be called once per scheduler run
  }

  public void setShooterSpeed(double speed){
    //shooterLead.set(-1.0);
    shooterPID.setReference(speed, ControlType.kVelocity);
  }

  public boolean isRightTriggerPressed(){
    return(Operator.getRightTrigger() > 0.5);
  }
}
