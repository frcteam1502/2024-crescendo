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
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Logger;

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

  public final static double SHOOTER_DEFAULT_RPM = 4300;
  public final static double SHOOTER_HOLD_RPM = -100;
  public final static double INTAKE_DEFAULT_PICK_UP_RPM = 1800;
  public final static double INTAKE_DEFAULT_INDEX_RPM = 1000;
  public final static double INTAKE_DEFAULT_AMP_RPM = 1800;
  public final static double INTAKE_DEFAULT_EJECT_RPM = -1000;
  public final static double INTAKE_DEFAULT_SHOOT_RPM = 1800;

  public final static double SHOOTER_PID_P = 0.00005;
  public final static double SHOOTER_PID_I = 0;
  public final static double SHOOTER_PID_D = 0;
  public final static double SHOOTER_PID_F = 0.000185;

  public final static double INTAKE_GEAR_RATIO = 1.0/3.0;
  public final static double INTAKE_PID_P = 0.00005;
  public final static double INTAKE_PID_I = 0;
  public final static double INTAKE_PID_D = 0;
  public final static double INTAKE_PID_F = 0.000375;//0.000275;

  public static final double[] SPEED_LOOK_UP_TABLE = 
  {
    5000,//6
    5000,//5.5
    5000,//5
    5000,//4.5
    5000,//4
    4500,//3.5
    4500,//3
    4500,//2.5
    4000,//2
    4000,//1.5
  };

  public static final double LOOKUP_TABLE_MIN = 1.5;
  public static final double LOOKUP_TABLE_MAX = 6;

}

public class ShooterIntake extends SubsystemBase {
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

  private double auto_shooter_speed = ShooterIntakeConstants.SHOOTER_DEFAULT_RPM;
  private double shooter_speed = ShooterIntakeConstants.SHOOTER_DEFAULT_RPM;
  private double intakePickupSpeed = ShooterIntakeConstants.INTAKE_DEFAULT_PICK_UP_RPM;
  private double intakeIndexSpeed = ShooterIntakeConstants.INTAKE_DEFAULT_INDEX_RPM;
  private double intakeAmpSpeed = ShooterIntakeConstants.INTAKE_DEFAULT_AMP_RPM;
  private double intakeEjectSpeed = ShooterIntakeConstants.INTAKE_DEFAULT_EJECT_RPM;
  private double intakeShootSpeed = ShooterIntakeConstants.INTAKE_DEFAULT_SHOOT_RPM;

  private double shooter_ff = ShooterIntakeConstants.SHOOTER_PID_F;
  private double intake_ff = ShooterIntakeConstants.INTAKE_PID_F;
  private double shooter_p = ShooterIntakeConstants.SHOOTER_PID_P;
  private double intake_p = ShooterIntakeConstants.INTAKE_PID_P;

  private boolean isNoteLoading = false;
  private boolean isShooterOn = false;

  private double temp_index;
  private int index;
  
  public ShooterIntake() {
    //Set up shooter control
    shooter_lead = Motors.SHOOTER_LEAD;
    shooter_lead.setIdleMode(Motors.SHOOTER_MOTOR_IDLE_MODE);
    shooter_lead.setSmartCurrentLimit(40);
    shooter_lead_controller = shooter_lead.getPIDController();
    
    shooter_follow = Motors.SHOOTER_FOLLOW;
    shooter_follow.follow(shooter_lead, false);
    //shooter_follow.setIdleMode(Motors.SHOOTER_MOTOR_IDLE_MODE);
    shooter_follow.setSmartCurrentLimit(40);
    shooter_follow_controller = shooter_follow.getPIDController();
    
    //Set up intake control
    intake = Motors.INTAKE;
    intake.setSmartCurrentLimit(40);
    intake_controller = intake.getPIDController();
    intake_controller.setFF(intake_ff);
    
    shooter_lead_encoder = shooter_lead.getEncoder();
    shooter_follow_encoder = shooter_follow.getEncoder();
    intake_encoder = intake.getEncoder();
    intake_encoder.setVelocityConversionFactor(ShooterIntakeConstants.INTAKE_GEAR_RATIO);

    photoSensorNormOpen = ShooterIntakeConstants.PHOTO_SENSOR_NO;
    photoSensorNormClosed = ShooterIntakeConstants.PHOTO_SENSOR_NC;

    shooter_lead_controller.setP(ShooterIntakeConstants.SHOOTER_PID_P);
    shooter_lead_controller.setI(ShooterIntakeConstants.SHOOTER_PID_I);
    shooter_lead_controller.setD(ShooterIntakeConstants.SHOOTER_PID_D);
    shooter_lead_controller.setFF(ShooterIntakeConstants.SHOOTER_PID_F);

    shooter_follow_controller.setP(ShooterIntakeConstants.SHOOTER_PID_P);
    shooter_follow_controller.setI(ShooterIntakeConstants.SHOOTER_PID_I);
    shooter_follow_controller.setD(ShooterIntakeConstants.SHOOTER_PID_D);
    shooter_follow_controller.setFF(ShooterIntakeConstants.SHOOTER_PID_F);

    SmartDashboard.putNumber("Shooter PID FF", shooter_ff);
    SmartDashboard.putNumber("Shooter Set Speed",shooter_speed);
    SmartDashboard.putNumber("Shooter PID P", shooter_p);

    
    SmartDashboard.putNumber("Intake PID FF", intake_ff);
    SmartDashboard.putNumber("Intake Pickup Speed", intakePickupSpeed);
    SmartDashboard.putNumber("Intake PID p", intake_p);

    registerLoggerObjects();
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
    shooter_follow_controller.setFF(shooter_ff);
    shooter_lead_controller.setReference(shooter_speed, CANSparkMax.ControlType.kVelocity);
    shooter_follow_controller.setReference(shooter_speed+200, CANSparkMax.ControlType.kVelocity);
    isShooterOn = true;
  }

  public void setShooterOff(){
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
      return true;
    }else{
      return false;
    }
  }

  public void lookupShooterSpeed(double distance, boolean distanceValid){
    
    if(distanceValid){
      if(distance >= ShooterIntakeConstants.LOOKUP_TABLE_MAX){
        shooter_speed = ShooterIntakeConstants.SPEED_LOOK_UP_TABLE[0];
      }else if(distance <= ShooterIntakeConstants.LOOKUP_TABLE_MIN){
        shooter_speed = ShooterIntakeConstants.SPEED_LOOK_UP_TABLE[9];
      }else{
        temp_index = ((ShooterIntakeConstants.LOOKUP_TABLE_MAX - distance)/0.5);
        index = (int)(temp_index);
        shooter_speed = ShooterIntakeConstants.SPEED_LOOK_UP_TABLE[index];
      } 
    }else{
      shooter_speed = ShooterIntakeConstants.SPEED_LOOK_UP_TABLE[0];
    }

    setShooterOn();
  }

  public void setCloseShotSpeed(){
    shooter_speed = ShooterIntakeConstants.SHOOTER_DEFAULT_RPM;
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
    SmartDashboard.putBoolean("Note Sensor NC", photoSensorNormClosed.get());
    SmartDashboard.putBoolean("Is Note Present", isNotePresent());

    SmartDashboard.putBoolean("Is Shooter On", isShooterOn);
    
  }

  public void registerLoggerObjects(){
    Logger.RegisterCanSparkMax("Shooter Lead", Motors.SHOOTER_LEAD);
    Logger.RegisterCanSparkMax("Shooter Follow", Motors.SHOOTER_FOLLOW);
    Logger.RegisterCanSparkMax("Intake", Motors.INTAKE);

    Logger.RegisterSensor("Shooter Lead Speed",()->shooter_lead_encoder.getVelocity());
    Logger.RegisterSensor("Shooter Follow Speed",()->shooter_follow_encoder.getVelocity());
    Logger.RegisterSensor("Intake Speed",()->intake_encoder.getVelocity());
  }
}
