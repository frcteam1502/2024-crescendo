// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.subsystems.Climber;

import com.revrobotics.CANSparkMax;
import com.revrobotics.RelativeEncoder;
import com.revrobotics.SparkPIDController;
import com.revrobotics.CANSparkLowLevel;
import com.revrobotics.CANSparkBase.IdleMode;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.SubsystemBase;

final class Motors{
  public final static CANSparkMax CLIMBER_RIGHT = new CANSparkMax(13, CANSparkLowLevel.MotorType.kBrushless);
  public final static CANSparkMax CLIMBER_LEFT  = new CANSparkMax(15, CANSparkLowLevel.MotorType.kBrushless);
  public static final CANSparkMax.IdleMode CLIMB_MOTOR_IDLE_MODE = IdleMode.kBrake;
}

final class ClimberConstants{
  public final static double CLIMBER_SPOOL_DIAMETER = .0222;
  public final static double CLIMBER_GEAR_RATIO = 25;
  public final static double CLIMBER_DISTANCE_PER_MOTOR_REV = (Math.PI*CLIMBER_SPOOL_DIAMETER)/CLIMBER_GEAR_RATIO;
  public final static double CLIMBER_MAX_DISTANCE = .4;
  public final static double CLIMBER_MIN_DISTANCE = 0;

  public final static double CLIMBER_HOME_POSITION = CLIMBER_MAX_DISTANCE;

  public final static double CLIMBER_PID_P = 1;
  public final static double CLIMBER_PID_I = 0;
  public final static double CLIMBER_PID_D = 0;
  public final static double CLIMBER_PID_FF = 0;
}

public class Climber extends SubsystemBase {
  /** Creates a new Climber. */
  private final CANSparkMax climber_right;
  private final CANSparkMax climber_left;
  private final RelativeEncoder climber_right_encoder;
  private final RelativeEncoder climber_left_encoder;
  private final SparkPIDController climber_right_PID;
  private final SparkPIDController climber_left_PID;


  public Climber() {
    climber_right = Motors.CLIMBER_RIGHT;
    climber_right.setIdleMode(Motors.CLIMB_MOTOR_IDLE_MODE);
    climber_right.setSmartCurrentLimit(40);
    climber_right.setInverted(false);
    climber_right_encoder = climber_right.getEncoder();
    climber_right_encoder.setPositionConversionFactor(ClimberConstants.CLIMBER_DISTANCE_PER_MOTOR_REV);
    climber_right_PID = climber_right.getPIDController();
    climber_right_PID.setP(ClimberConstants.CLIMBER_PID_P);
    climber_right_PID.setI(ClimberConstants.CLIMBER_PID_I);
    climber_right_PID.setD(ClimberConstants.CLIMBER_PID_D);
    climber_right_PID.setFF(ClimberConstants.CLIMBER_PID_FF);

    climber_left = Motors.CLIMBER_LEFT;
    climber_left.setIdleMode(Motors.CLIMB_MOTOR_IDLE_MODE);
    climber_left.setSmartCurrentLimit(40);
    climber_left.setInverted(true);
    climber_left_encoder = climber_left.getEncoder();
    climber_left_encoder.setPositionConversionFactor(ClimberConstants.CLIMBER_DISTANCE_PER_MOTOR_REV);
    climber_left_PID = climber_left.getPIDController();
    climber_left_PID.setP(ClimberConstants.CLIMBER_PID_P);
    climber_left_PID.setI(ClimberConstants.CLIMBER_PID_I);
    climber_left_PID.setD(ClimberConstants.CLIMBER_PID_D);
    climber_left_PID.setFF(ClimberConstants.CLIMBER_PID_FF);
  }

  @Override
  public void periodic() {
    // This method will be called once per scheduler run
    updateDashboard();
  }

  public void climbUp(){
    if(climber_left_encoder.getPosition() <= ClimberConstants.CLIMBER_MAX_DISTANCE){
      climber_left.set(1);
    }else{
      climber_left.set(0);
    }

    if(climber_right_encoder.getPosition() <= ClimberConstants.CLIMBER_MAX_DISTANCE){
      climber_right.set(1);
    }else{
      climber_right.set(0);
    }
  }

  public void climbDown(){
    if(climber_left_encoder.getPosition() >= ClimberConstants.CLIMBER_MIN_DISTANCE){
      climber_left.set(-1);
    }else{
      climber_left.set(0);
    }

    if(climber_right_encoder.getPosition() >= ClimberConstants.CLIMBER_MIN_DISTANCE){
      climber_right.set(-1);
    }else{
      climber_right.set(0);
    }
  }

  public void climberOff(){
    climber_right.set(0);
    climber_left.set(0);
  }

  public void setClimberHome(){
    climber_right_PID.setReference(ClimberConstants.CLIMBER_HOME_POSITION, CANSparkMax.ControlType.kPosition);
    climber_left_PID.setReference(ClimberConstants.CLIMBER_HOME_POSITION, CANSparkMax.ControlType.kPosition);
  }

  private void updateDashboard(){
    SmartDashboard.putNumber("Left Climb Position", climber_left_encoder.getPosition());
    SmartDashboard.putNumber("Right Climb Position", climber_right_encoder.getPosition());
  }

  public boolean isLeftClimberAtHome(){
    double position = climber_left_encoder.getPosition();
    if((position >= ClimberConstants.CLIMBER_HOME_POSITION-.01)&&
       (position <= ClimberConstants.CLIMBER_HOME_POSITION+.01)){
      return true;
    }
    return false;
  }

  public boolean isRightClimberAtHome(){
    double position = climber_right_encoder.getPosition();
    if((position >= ClimberConstants.CLIMBER_HOME_POSITION-.01)&&
       (position <= ClimberConstants.CLIMBER_HOME_POSITION+.01)){
      return true;
    }
    return false;
  }
}
