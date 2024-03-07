// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.subsystems.ShooterIntake;

import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkLowLevel;
import com.revrobotics.CANSparkBase.IdleMode;
import com.revrobotics.RelativeEncoder;
import com.revrobotics.SparkPIDController;

import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.InstantCommand;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Logger;
import frc.robot.commands.IntakeNote;
import frc.robot.commands.ShootNote;
import frc.robot.commands.ShooterIntakeCommands;
import team1502.configuration.annotations.DefaultCommand;
import team1502.configuration.factory.RobotConfiguration;

@DefaultCommand(command = ShooterIntakeCommands.class)
public class ShooterIntake extends SubsystemBase {
  public final static String SHOOTER = "Shooter";
  public final static String INTAKE = "Intake";
  public final static String PHOTO_SENSOR_NO = "Photosensor NO";
  public final static String PHOTO_SENSOR_NC = "Photosensor NC";

  private final static double SHOOTER_DEFAULT_RPM = 4000;
  private final static double SHOOTER_AMP_RPM = 100;
  private final static double SHOOTER_HOLD_RPM = -100;

  private final static double INTAKE_PICK_UP_FAST_RPM = 2000;
  private final static double INTAKE_PICK_UP_SLOW_RPM = 1000;
  private final static double INTAKE_DEFAULT_INDEX_RPM = 250;
  private final static double INTAKE_DEFAULT_EJECT_RPM = -1000;
  private final static double INTAKE_DEFAULT_SHOOT_RPM = 3500;

  /** Creates a new ShooterIntake. */
  private final CANSparkMax shooter_lead;
  private final CANSparkMax shooter_follow;
  private final CANSparkMax intake;
    
  private final RelativeEncoder shooter_lead_encoder;
  private final RelativeEncoder shooter_follow_encoder;
  private final RelativeEncoder intake_encoder;

  private final SparkPIDController shooter_lead_controller;
  //private final SparkPIDController shooter_follow_controller;
  private final SparkPIDController intake_controller;

  private final DigitalInput photoSensorNormOpen;
  private final DigitalInput photoSensorNormClosed;

  private double shooter_speed = SHOOTER_DEFAULT_RPM;

  private double intakePickupFastSpeed = INTAKE_PICK_UP_FAST_RPM;
  private double intakePickupSlowSpeed = INTAKE_PICK_UP_SLOW_RPM;
  private double intakeIndexSpeed = INTAKE_DEFAULT_INDEX_RPM;
  private double intakeEjectSpeed = INTAKE_DEFAULT_EJECT_RPM;
  private double intakeShootSpeed = INTAKE_DEFAULT_SHOOT_RPM;

  private double shooter_p;
  private double shooter_ff;
  private double intake_p;
  private double intake_ff;

  //private boolean isNoteLoading = false;
  private boolean isShooterOn = false;
  
  public ShooterIntake(RobotConfiguration config) {
    //Set up shooter control
    var shooterConfig = config.Subsystem(SHOOTER);
    shooter_p = shooterConfig.MotorController().PID().P();
    shooter_ff = shooterConfig.MotorController().PID().FF();
    shooter_lead = shooterConfig.MotorController().buildSparkMax();
    shooter_lead_controller = shooterConfig.MotorController().buildPIDController();
    shooter_lead_encoder = shooterConfig.MotorController().buildRelativeEncoder();
    shooter_follow = shooterConfig.MotorController().Follower().CANSparkMax();
    shooter_follow_encoder = shooter_follow.getEncoder();
    shooterConfig.MotorController().registerLoggerObjects((n, r)->Logger.RegisterCanSparkMax(n,r));

    //Set up intake control
    var intakeConfig = config.Subsystem(INTAKE);
    intake_p = intakeConfig.MotorController().PID().P();
    intake_ff = intakeConfig.MotorController().PID().FF();
    intake = intakeConfig.MotorController().buildSparkMax();;
    intake_controller = intakeConfig.MotorController().buildPIDController();;
    intake_encoder = intakeConfig.MotorController().buildRelativeEncoder();
    intakeConfig.MotorController().registerLoggerObjects((n, r)->Logger.RegisterCanSparkMax(n,r));
    Logger.RegisterSensor("Shooter Lead Speed",()->shooter_lead_encoder.getVelocity());
    Logger.RegisterSensor("Shooter Follow Speed",()->shooter_follow_encoder.getVelocity());
    Logger.RegisterSensor("Intake Speed",()->intake_encoder.getVelocity());

    photoSensorNormOpen = intakeConfig.DigitalInput(PHOTO_SENSOR_NO); //.PHOTO_SENSOR_NO;
    photoSensorNormClosed = intakeConfig.DigitalInput(PHOTO_SENSOR_NC); //.PHOTO_SENSOR_NC;

    SmartDashboard.putNumber("Shooter PID FF", shooter_ff);
    SmartDashboard.putNumber("Shooter Set Speed",shooter_speed);
    SmartDashboard.putNumber("Shooter PID P", shooter_p);
    
    SmartDashboard.putNumber("Intake PID FF", intake_ff);
    //SmartDashboard.putNumber("Intake Pickup Speed", intakePickupSpeed);
    SmartDashboard.putNumber("Intake PID p", intake_p);

  }


  @Override
  public void periodic() {
    // This method will be called once per scheduler run
    updateDashboard();
  }

  public void toggleShooter(){
    if(isShooterOn){
      setShooterOff();
    }else{
      setShooterOn();
    }
  }

  public void setShooterOn(){
    shooter_lead_controller.setFF(shooter_ff);
    //shooter_follow_controller.setFF(shooter_ff);
    shooter_lead_controller.setReference(shooter_speed, CANSparkMax.ControlType.kVelocity);
    //shooter_follow_controller.setReference(shooter_speed, CANSparkMax.ControlType.kVelocity);
    isShooterOn = true;
  }

  public void setShooterAmp(){
    shooter_lead_controller.setFF(shooter_ff);
    //shooter_follow_controller.setFF(shooter_ff);
    shooter_lead_controller.setReference(SHOOTER_AMP_RPM, CANSparkMax.ControlType.kVelocity);
    //shooter_follow_controller.setReference(SHOOTER_AMP_RPM, CANSparkMax.ControlType.kVelocity);
  }

  public void setShooterHold(){
    shooter_lead_controller.setFF(shooter_ff);
    //shooter_follow_controller.setFF(shooter_ff);
    shooter_lead_controller.setReference(SHOOTER_HOLD_RPM, CANSparkMax.ControlType.kVelocity);
    //shooter_follow_controller.setReference(SHOOTER_HOLD_RPM, CANSparkMax.ControlType.kVelocity);
  }

  public void setShooterOff(){
    shooter_lead_controller.setReference(0.0, CANSparkMax.ControlType.kVelocity);
    //shooter_follow_controller.setReference(0.0, CANSparkMax.ControlType.kVelocity);
    isShooterOn = false;
  }

  public void setIntakePickupFast(){
    intake_controller.setFF(intake_ff);
    intake_controller.setReference(intakePickupFastSpeed, CANSparkMax.ControlType.kVelocity);
  }

  public void setIntakePickupSlow(){
    intake_controller.setFF(intake_ff);
    intake_controller.setReference(intakePickupSlowSpeed, CANSparkMax.ControlType.kVelocity);
  }

  public void setIntakeIndex(){
    intake_controller.setFF(intake_ff); 
    intake_controller.setReference(intakeIndexSpeed, CANSparkMax.ControlType.kVelocity);
  }

  public void setIntakeShoot(){
    intake_controller.setFF(intake_ff); 
    intake_controller.setReference(intakeShootSpeed, CANSparkMax.ControlType.kVelocity);
  }

  public void setIntakeEject(){
    intake_controller.setReference(intakeEjectSpeed, CANSparkMax.ControlType.kVelocity);
  }

  public void setIntakeOff(){
     intake.set(0);
  }

  public boolean isNotePresent(){
    if((!photoSensorNormOpen.get())||(photoSensorNormClosed.get())){
      return true;
    }else{
      return false;
    }
  }

  public double getIntakeCurrent(){
    return(intake.getOutputCurrent());
  }

  public boolean isShooterAtSpeed(){
    double shooter_on_threshold = shooter_speed - (shooter_speed*0.05);
    if(shooter_lead_encoder.getVelocity() >= shooter_on_threshold){
      return true;
    }
    return false;
  }

  private void updateDashboard(){
    shooter_ff = SmartDashboard.getNumber("Shooter PID FF", 0);
    shooter_speed = SmartDashboard.getNumber("Shooter Set Speed",0);
    shooter_p =SmartDashboard.getNumber("Shooter PID p", 0);
    
    intake_ff = SmartDashboard.getNumber("Intake PID FF", 0);
    //intakePickupSpeed = SmartDashboard.getNumber("Intake Pickup Speed", 0);
    intake_p = SmartDashboard.getNumber("Intake PID p", 0);

    SmartDashboard.putNumber("Shooter Lead Speed",shooter_lead_encoder.getVelocity());
    SmartDashboard.putNumber("Shooter Follow Speed",shooter_follow_encoder.getVelocity());
    SmartDashboard.putNumber("Intake Speed", intake_encoder.getVelocity());

    SmartDashboard.putNumber("Shooter Lead Applied Output %", shooter_lead.getAppliedOutput());
    SmartDashboard.putNumber("Shooter Lead Applied Output Volts", (shooter_lead.getAppliedOutput()*shooter_lead.getBusVoltage()));

    SmartDashboard.putNumber("Shooter Follow Applied Output %", shooter_follow.getAppliedOutput());
    SmartDashboard.putNumber("Shooter Follow Applied Output Volts", (shooter_follow.getAppliedOutput()*shooter_lead.getBusVoltage()));

    SmartDashboard.putNumber("Intake Applied Output %", intake.getAppliedOutput());
    SmartDashboard.putNumber("Intake Applied Output Volts", (intake.getAppliedOutput()*intake.getBusVoltage()));

    SmartDashboard.putBoolean("Note Sensor NO", !photoSensorNormOpen.get());
    SmartDashboard.putBoolean("Note Sensor NC", !photoSensorNormClosed.get());
    SmartDashboard.putBoolean("Is Note Present", isNotePresent());

    SmartDashboard.putBoolean("Is Shooter On", isShooterOn);
    
  }

}
