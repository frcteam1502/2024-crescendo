// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.subsystems.ShooterIntake;

import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkLowLevel;
import com.revrobotics.CANSparkBase.ControlType;
import com.revrobotics.CANSparkBase.IdleMode;
import com.revrobotics.RelativeEncoder;
import com.revrobotics.SparkPIDController;

import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.SubsystemBase;

final class Motors{
  public final static CANSparkMax SHOOTER_LEAD    = new CANSparkMax(2, CANSparkLowLevel.MotorType.kBrushless);
  public final static CANSparkMax SHOOTER_FOLLOW  = new CANSparkMax(3, CANSparkLowLevel.MotorType.kBrushless);
  public final static CANSparkMax INTAKE          = new CANSparkMax(18, CANSparkLowLevel.MotorType.kBrushless);

  public static final CANSparkMax.IdleMode SHOOTER_MOTOR_IDLE_MODE = IdleMode.kCoast;
}

final class ShooterIntakeConstants{
  public final static int PHOTO_SENSOR_NO_CHANNEL  = 1;
  public final static int PHOTO_SENSOR_NC_CHANNEL  = 2;
  public final static DigitalInput PHOTO_SENSOR_NO = new DigitalInput(PHOTO_SENSOR_NO_CHANNEL);
  public final static DigitalInput PHOTO_SENSOR_NC = new DigitalInput(PHOTO_SENSOR_NC_CHANNEL);

  public final static double SHOOTER_DEFAULT_RPM = 5000;
  public final static double INTAKE_DEFAULT_PICK_UP_RPM = 2500;
  public final static double INTAKE_DEFAULT_EJECT_RPM = -2500;

  public final static double SHOOTER_PID_P = 0.00005;
  public final static double SHOOTER_PID_I = 0;
  public final static double SHOOTER_PID_D = 0;
  public final static double SHOOTER_PID_F = 0.000015;
}

public class ShooterIntake extends SubsystemBase {
  /** Creates a new ShooterIntake. */
  private final CANSparkMax shooter_lead;
  private final CANSparkMax shooter_follow;
  private final CANSparkMax intake;
    
  private final RelativeEncoder shooter_lead_encoder;
  private final RelativeEncoder shooter_follow_encoder;
  private final RelativeEncoder intake_encoder;

  private final SparkPIDController shooter_controller;
  private final SparkPIDController intake_controller;

  private final DigitalInput photoSensorNormOpen;
  private final DigitalInput photoSensorNormClosed;

  private double shooter_speed;
  private double intakePickupSpeed;
  private double intakeFwdSpeed;
  private double intakeEjectSpeed;
  
  public ShooterIntake() {
    shooter_lead = Motors.SHOOTER_LEAD;
    shooter_follow = Motors.SHOOTER_FOLLOW;
    intake = Motors.INTAKE;
    shooter_follow.follow(shooter_lead, false);
    shooter_lead.setIdleMode(Motors.SHOOTER_MOTOR_IDLE_MODE);
    shooter_follow.setIdleMode(Motors.SHOOTER_MOTOR_IDLE_MODE);
    shooter_lead.setSmartCurrentLimit(40);
    shooter_follow.setSmartCurrentLimit(40);

    shooter_lead_encoder = shooter_lead.getEncoder();
    shooter_follow_encoder = shooter_follow.getEncoder();
    intake_encoder = intake.getEncoder();

    shooter_controller = shooter_lead.getPIDController();
    intake_controller = intake.getPIDController();

    photoSensorNormOpen = ShooterIntakeConstants.PHOTO_SENSOR_NO;
    photoSensorNormClosed = ShooterIntakeConstants.PHOTO_SENSOR_NC;

    shooter_controller.setP(ShooterIntakeConstants.SHOOTER_PID_P);
    shooter_controller.setI(ShooterIntakeConstants.SHOOTER_PID_I);
    shooter_controller.setD(ShooterIntakeConstants.SHOOTER_PID_D);
    shooter_controller.setFF(ShooterIntakeConstants.SHOOTER_PID_F);
  }

  @Override
  public void periodic() {
    // This method will be called once per scheduler run
    updateDashboard();
  }

  public void setShooterOn(){
    System.out.println("Shooter On!");
    //shooter_controller.setReference(shooter_speed, CANSparkMax.ControlType.kVelocity);
    shooter_lead.set(.80);
  }

  public void setShooterOff(){
    System.out.println("Shooter Off!");
    shooter_lead.set(0.0);
  }

  public void setIntakePickup(){
    //intake_controller.setReference(intakePickupSpeed, CANSparkMax.ControlType.kVelocity);
    intake.set(1.0);
  }

  public void setIntakeFwd(){
     intake_controller.setReference(intakeFwdSpeed, CANSparkMax.ControlType.kVelocity);
  }

  public void setIntakeEject(){
    intake_controller.setReference(intakeEjectSpeed, CANSparkMax.ControlType.kVelocity);
  }

  public void setIntakeOff(){
     intake.set(0);
  }

  public boolean isNotePresent(){
    if((photoSensorNormOpen.get())||(!photoSensorNormClosed.get())){
      return true;
    }else{
      return false;
    }
  }

  private void updateDashboard(){
    //SmartDashboard.getNumber("Shooter Set Speed",0);

    SmartDashboard.putNumber("Shooter Lead Speed",shooter_lead_encoder.getVelocity());
    SmartDashboard.putNumber("Shooter Follow Speed",shooter_follow_encoder.getVelocity());
    SmartDashboard.putNumber("Intake Speed", intake_encoder.getVelocity());

  }
}
