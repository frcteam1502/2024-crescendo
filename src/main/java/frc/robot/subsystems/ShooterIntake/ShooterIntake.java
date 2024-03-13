// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.subsystems.ShooterIntake;

import com.revrobotics.CANSparkMax;
import com.revrobotics.RelativeEncoder;
import com.revrobotics.SparkPIDController;

import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Logger;
import frc.robot.commands.ShooterIntakeCommands;
import team1502.configuration.annotations.DefaultCommand;
import team1502.configuration.factory.RobotConfiguration;

@DefaultCommand(command = ShooterIntakeCommands.class)
public class ShooterIntake extends SubsystemBase {
  public final static String SHOOTER = "Shooter";
  public final static String LEADER = "Leader";
  public final static String FOLLOWER = "Follower";
  public final static String INTAKE = "Intake";
  public final static String PHOTO_SENSOR_NO = "Photosensor NO";
  public final static String PHOTO_SENSOR_NC = "Photosensor NC";

  private final static double SHOOTER_DEFAULT_RPM = 4000;
  private final static double SHOOTER_THRESHOLD_BELOW_MAX = 0.05; // i.e., within 5% of max
  private final static double FOLLOWER_DELTA_RPM = 200;

  private final static double INTAKE_DEFAULT_PICK_UP_RPM = 2500;
  private final static double INTAKE_DEFAULT_INDEX_RPM = 100;
  private final static double INTAKE_DEFAULT_AMP_RPM = 2000;
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
  private final SparkPIDController shooter_follow_controller;
  private final SparkPIDController intake_controller;

  private final DigitalInput photoSensorNormOpen;
  private final DigitalInput photoSensorNormClosed;

  private double shooter_speed = SHOOTER_DEFAULT_RPM;
  private double intakePickupSpeed = INTAKE_DEFAULT_PICK_UP_RPM;
  private double intakeIndexSpeed = INTAKE_DEFAULT_INDEX_RPM;
  private double intakeAmpSpeed = INTAKE_DEFAULT_AMP_RPM;
  private double intakeEjectSpeed = INTAKE_DEFAULT_EJECT_RPM;
  private double intakeShootSpeed = INTAKE_DEFAULT_SHOOT_RPM;

  private double shooter_ff;
  private double intake_ff;
  private double shooter_p;
  private double intake_p;

  private boolean isShooterOn = false;
  
  public ShooterIntake(RobotConfiguration config) {
    //Set up shooter control
    var shooterConfig = config.Subsystem(SHOOTER);
    var leaderConfig = shooterConfig.MotorController(LEADER);
    shooter_p = leaderConfig.PID().P();
    shooter_ff = leaderConfig.PID().FF();
    shooter_lead = leaderConfig.buildSparkMax();
    shooter_lead_controller = leaderConfig.buildPIDController();
    shooter_lead_encoder = leaderConfig.buildRelativeEncoder();
    leaderConfig.registerLoggerObjects((n, r)->Logger.RegisterCanSparkMax(n,r));
    
    var followerConfig = shooterConfig.MotorController(FOLLOWER);
    shooter_follow = followerConfig.buildSparkMax();
    shooter_follow_controller = followerConfig.buildPIDController();
    shooter_follow_encoder = shooter_follow.getEncoder();
    followerConfig.registerLoggerObjects((n, r)->Logger.RegisterCanSparkMax(n,r));

    //Set up intake control
    var intakeConfig = config.Subsystem(INTAKE);
    var intakeControllerConfig = intakeConfig.MotorController();
    intake_p = intakeControllerConfig.PID().P();
    intake_ff = intakeControllerConfig.PID().FF();
    intake = intakeControllerConfig.buildSparkMax();;
    intake_controller = intakeControllerConfig.buildPIDController();;
    intake_encoder = intakeControllerConfig.buildRelativeEncoder();
    intakeControllerConfig.registerLoggerObjects((n, r)->Logger.RegisterCanSparkMax(n,r));
    Logger.RegisterSensor("Shooter Lead Speed",()->shooter_lead_encoder.getVelocity());
    Logger.RegisterSensor("Shooter Follow Speed",()->shooter_follow_encoder.getVelocity());
    Logger.RegisterSensor("Intake Speed",()->intake_encoder.getVelocity());

    photoSensorNormOpen = intakeConfig.DigitalInput(PHOTO_SENSOR_NO); //.PHOTO_SENSOR_NO;
    photoSensorNormClosed = intakeConfig.DigitalInput(PHOTO_SENSOR_NC); //.PHOTO_SENSOR_NC;

    SmartDashboard.putNumber("Shooter PID FF", shooter_ff);
    SmartDashboard.putNumber("Shooter Set Speed",shooter_speed);
    SmartDashboard.putNumber("Shooter PID P", shooter_p);
    
    SmartDashboard.putNumber("Intake PID FF", intake_ff);
    SmartDashboard.putNumber("Intake Pickup Speed", intakePickupSpeed);
    SmartDashboard.putNumber("Intake PID p", intake_p);
  }

  @Override
  public void periodic() {
    // This method will be called once per scheduler run
    updateDashboard();
  }

  public void toggleShooter(){
    if(isShooterOn){
      setShooterOff(); }
    else {
      setShooterOn(); }
  }

  public void setShooterOn(){
    shooter_lead_controller.setFF(shooter_ff);
    shooter_follow_controller.setFF(shooter_ff);
    shooter_lead_controller.setReference(shooter_speed, CANSparkMax.ControlType.kVelocity);
    shooter_follow_controller.setReference(shooter_speed + FOLLOWER_DELTA_RPM, CANSparkMax.ControlType.kVelocity);
    isShooterOn = true;
  }

  private void setShooterOff(){
    shooter_lead_controller.setReference(0.0, CANSparkMax.ControlType.kVelocity);
    shooter_follow_controller.setReference(0.0, CANSparkMax.ControlType.kVelocity);
    isShooterOn = false;
  }

  public void setIntakePickup(){
    intake_controller.setFF(intake_ff);
    intake_controller.setReference(intakePickupSpeed, CANSparkMax.ControlType.kVelocity);
  }

  public void setIntakeIndex(){
    intake_controller.setFF(intake_ff); 
    intake_controller.setReference(intakeIndexSpeed, CANSparkMax.ControlType.kVelocity);
  }
  public void setIntakeAmp(){
    intake_controller.setFF(intake_ff); 
    intake_controller.setReference(intakeAmpSpeed, CANSparkMax.ControlType.kVelocity);
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
      return true; }
    else {
      return false; }
  }

  public double getIntakeCurrent(){
    return(intake.getOutputCurrent());
  }

  public boolean isShooterAtSpeed(){
    double shooter_on_threshold = shooter_speed - (shooter_speed * SHOOTER_THRESHOLD_BELOW_MAX);
    if(shooter_lead_encoder.getVelocity() >= shooter_on_threshold){
      return true; }
    else {
      return false; }
  }

  private void updateDashboard(){
    shooter_ff = SmartDashboard.getNumber("Shooter PID FF", 0);
    shooter_speed = SmartDashboard.getNumber("Shooter Set Speed",0);
    shooter_p =SmartDashboard.getNumber("Shooter PID p", 0);
    
    intake_ff = SmartDashboard.getNumber("Intake PID FF", 0);
    intakePickupSpeed = SmartDashboard.getNumber("Intake Pickup Speed", 0);
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
