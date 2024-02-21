package frc.robot.subsystems.Arm;

import frc.robot.Logger;

import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkLowLevel;
import com.revrobotics.CANSparkBase.IdleMode;
import com.revrobotics.RelativeEncoder;
import com.revrobotics.SparkPIDController;

import edu.wpi.first.wpilibj.DutyCycleEncoder;
import edu.wpi.first.wpilibj.PneumaticsModuleType;
import edu.wpi.first.wpilibj.Solenoid;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

import edu.wpi.first.wpilibj2.command.SubsystemBase;

final class Motors{
  public static final CANSparkMax ARM_LEAD = new CANSparkMax(1, CANSparkLowLevel.MotorType.kBrushless);
  public static final CANSparkMax ARM_FOLLOW = new CANSparkMax(6, CANSparkLowLevel.MotorType.kBrushless);
  public static final CANSparkMax.IdleMode ARM_MOTOR_IDLE_MODE = IdleMode.kBrake;

  public static final Solenoid BRAKE_SOLENOID = new Solenoid(7,PneumaticsModuleType.REVPH, 0);
}

final class AbsEncoder{
  public static final int ARM_ABS_ENCODER_CHANNEL = 0;
  public static final DutyCycleEncoder ARM_ABS_ENCODER = new DutyCycleEncoder(ARM_ABS_ENCODER_CHANNEL);
}

final class ArmConstants{
  //Rotation bounds
  public static final double MAX_ROTATE = 5;
  public static final double MIN_ROTATE = -95;

  public static final double ARM_DEGREES_PER_ENCODER_ROTATION = 360.0 / 100.0; 
  public static final double MAX_ROTATE_FEEDFORWARD = .06; //TODO: increase?
  
  public static final double ROTATE_CHANGE = .3; 

  public static final double MAX_ROTATION_SPEED = .3;

  public static final double ABS_OFFSET = -5;//This is unique for the robot!

  public static final double ARM_P_GAIN = 0.4;
  public static final double ARM_I_GAIN = 0;
  public static final double ARM_D_GAIN = 0;
  public static final double ARM_F_GAIN = 0;

  public static final double[] POSITION_TABLE = 
  {
    -3.5,  //Intake
    -26, //Shoot Close
    -45, //Shoot Far
    -18,   //Stow/Start
    -22.5,  //Amp/Trap
  };
}

public class ArmSubsystem extends SubsystemBase {
  private final CANSparkMax rotate;
  private final CANSparkMax rotateFollower;

  private final SparkPIDController rotatePID;

  public final RelativeEncoder rotateRelativeEncoder;

  public final DutyCycleEncoder rotateAbsEncoder;

  public final Solenoid brakeSolenoid;

  private double goalRotate = 0;

  private double arm_p_gain = ArmConstants.ARM_P_GAIN;
  private double arm_intake_angle = ArmConstants.POSITION_TABLE[0];
  private double arm_close_angle = ArmConstants.POSITION_TABLE[1];
  private double arm_far_angle = ArmConstants.POSITION_TABLE[2];

  public ArmSubsystem() {
    //Initialize Motors
    rotate = Motors.ARM_LEAD;
    rotateFollower = Motors.ARM_FOLLOW;
    rotateFollower.follow(rotate, true);
    rotate.setIdleMode(Motors.ARM_MOTOR_IDLE_MODE);
    rotate.setSmartCurrentLimit(40);
    rotateFollower.setSmartCurrentLimit(40);
    rotateFollower.setIdleMode(Motors.ARM_MOTOR_IDLE_MODE);

    brakeSolenoid = Motors.BRAKE_SOLENOID;

    //Initialize Relative Encoder
    rotateRelativeEncoder = rotate.getEncoder();
    rotateRelativeEncoder.setPositionConversionFactor(ArmConstants.ARM_DEGREES_PER_ENCODER_ROTATION);

    //Initialize Absolute Encoder
    rotateAbsEncoder = AbsEncoder.ARM_ABS_ENCODER;
    
    //Initialize PID controller
    rotatePID = rotate.getPIDController();
    rotatePID.setFeedbackDevice(rotateRelativeEncoder);
    rotatePID.setP(ArmConstants.ARM_P_GAIN); //TODO: Get PID values
    rotatePID.setI(0);
    rotatePID.setD(0);
    //rotatePID.setFF(MAX_ROTATE_FEEDFORWARD);
    rotatePID.setOutputRange((-ArmConstants.MAX_ROTATION_SPEED), ArmConstants.MAX_ROTATION_SPEED);

    SmartDashboard.putNumber("ANGLE P Gain", arm_p_gain);
    SmartDashboard.putNumber("Arm Intake Angle", arm_intake_angle);
    SmartDashboard.putNumber("Arm Close Angle", arm_close_angle);
    SmartDashboard.putNumber("Arm Far Angle", arm_far_angle);
    
    //Reset the subsystem
    reset();
    registerLoggerObjects();
  }

   // For Testing
   public void updateDashboard(){ 
    
    // read PID coefficients from SmartDashboard
    arm_p_gain =         SmartDashboard.getNumber("ANGLE P Gain", 0);
    /*double i =         SmartDashboard.getNumber("ANGLE I Gain", 0);
    double d =         SmartDashboard.getNumber("ANGLE D Gain", 0);
    double iz =        SmartDashboard.getNumber("ANGLE I Zone", 0);
    double ff =        SmartDashboard.getNumber("ANGLE Feed Forward", 0);
    double max =       SmartDashboard.getNumber("ANGLE Max Output", 0);
    double min =       SmartDashboard.getNumber("ANGLE Min Output", 0);*/

    

    // if PID coefficients on SmartDashboard have changed, write new values to controller
    if((arm_p_gain != rotatePID.getP())) { rotatePID.setP(arm_p_gain); }
    /*if((i != rotatePID.getI())) { rotatePID.setI(i); }
    if((d != rotatePID.getD())) { rotatePID.setD(d); }
    if((iz != rotatePID.getIZone())) { rotatePID.setIZone(iz); }
    if((ff != rotatePID.getFF())) { rotatePID.setFF(ff); }
    if((max != rotatePID.getOutputMax()) || (min != rotatePID.getOutputMin())) { 
      rotatePID.setOutputRange(min, max); 
    }*/
    arm_intake_angle = SmartDashboard.getNumber("Arm Intake Angle", 0);
    arm_close_angle = SmartDashboard.getNumber("Arm Close Angle", 0);
    arm_far_angle = SmartDashboard.getNumber("Arm Far Angle", 0);
    SmartDashboard.putNumber("Arm Far Angle", arm_far_angle);
    SmartDashboard.putNumber("Arm Absolute Encoder Angle", getArmAbsPositionDegrees());
    SmartDashboard.putNumber("Arm Relative Encoder", rotateRelativeEncoder.getPosition());
    SmartDashboard.putNumber("Rotation Goal", goalRotate);
  }

  public void reset(){
    //Set Arm relative Position to absolute position
    double zeroedArmAbsPosition = getArmAbsPositionDegrees();

    rotateRelativeEncoder.setPosition((zeroedArmAbsPosition));
    goalRotate = (zeroedArmAbsPosition);
  }

  public double getArmAbsPositionDegrees(){
    //REV Encoder is CCW+
    double angleDegrees = rotateAbsEncoder.getAbsolutePosition()*360;

    angleDegrees = angleDegrees - 360;

    return (angleDegrees - ArmConstants.ABS_OFFSET);
  }

  public void rotateArm(double position) {
    goalRotate = position;
  }

  public void rotateToIntake() {
    rotateArm(arm_intake_angle);
  }

  public void rotateToShootClose() {
    //rotateArm(ArmConstants.POSITION_TABLE[1]);
    rotateArm(arm_close_angle);
  }

  public void rotateToShootFar() {
    //rotateArm(ArmConstants.POSITION_TABLE[2]);
    rotateArm(arm_far_angle);
  }

  public void rotateToStart() {
    rotateArm(ArmConstants.POSITION_TABLE[3]);
  }

  public void rotateToAmpTrap() {
    rotateArm(ArmConstants.POSITION_TABLE[4]);
  }

  public void rotateManually(double input) {
    double change = Math.signum(input) * ArmConstants.ROTATE_CHANGE;
    if(input > .8) change *= 2;
    rotateArm(goalRotate + change);
    //goalRotate = input;
  }

  public void checkMaxAndMin() {
    if(rotateRelativeEncoder.getPosition() > ArmConstants.MAX_ROTATE){
      goalRotate -= ArmConstants.ROTATE_CHANGE * 2;}
    else if(rotateRelativeEncoder.getPosition() < ArmConstants.MIN_ROTATE) {
      goalRotate += ArmConstants.ROTATE_CHANGE * 2;}
  }

  /**
   * Takes in the current angle to be used to calculate
   * the necesary feedforward based on the maximum
   * feedforward to hold the arm ar 90 degrees
   * 
   * @param currentAngle the current angle returned by the rotation encoder
   * 
   * @return the feedforward value to use to hold up the arm at the given angle
   */
  public double dynamicFeedForward(double currentAngle) {
    return ArmConstants.MAX_ROTATE_FEEDFORWARD * Math.cos(currentAngle);
  }

  private void registerLoggerObjects(){
    Logger.RegisterCanSparkMax("Arm Lead", Motors.ARM_LEAD);
    Logger.RegisterCanSparkMax("Arm Lead", Motors.ARM_FOLLOW);

    Logger.RegisterSensor("Arm Absolute Sensor", ()->getArmAbsPositionDegrees());
  }

  @Override
  public void periodic() {
    updateDashboard();
    checkMaxAndMin();
    rotatePID.setReference(goalRotate, CANSparkMax.ControlType.kPosition);
    //rotate.set(goalRotate);
  }
}