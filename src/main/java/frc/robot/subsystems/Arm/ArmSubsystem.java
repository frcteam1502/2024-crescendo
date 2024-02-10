package frc.robot.subsystems.Arm;

import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkLowLevel;
import com.revrobotics.CANSparkBase.IdleMode;
import com.revrobotics.RelativeEncoder;
import com.revrobotics.SparkPIDController;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

import edu.wpi.first.wpilibj2.command.SubsystemBase;

final class Motors{
  public static final CANSparkMax ARM_LEAD = new CANSparkMax(1, CANSparkLowLevel.MotorType.kBrushless);
  public static final CANSparkMax ARM_FOLLOW = new CANSparkMax(6, CANSparkLowLevel.MotorType.kBrushless);
  public static final CANSparkMax.IdleMode ARM_MOTOR_IDLE_MODE = IdleMode.kBrake;
}

final class ArmConstants{
  //Rotation bounds
  public static final double MAX_ROTATE = 120;
  public static final double MIN_ROTATE = -7;

  public static final double DEGREES_PER_ROTATION = 360 / 28.5; 
  public static final double MAX_ROTATE_FEEDFORWARD = .06; //TODO: increase?
  
  public static final double ROTATE_CHANGE = .3; 

  public static final double MAX_ROTATION_SPEED = .3;

  public static final double[] POSITION_TABLE = 
  {
    0.0,  //Intake
    18.0, //Shoot Close
    20.1, //Shoot Far
    90.0,   //Stow/Start
    110.0,  //Amp/Trap
  };
}

public class ArmSubsystem extends SubsystemBase {
  private final CANSparkMax rotate;
  private final CANSparkMax rotateFollower;

  private final SparkPIDController rotatePID;

  public final RelativeEncoder rotateRelativeEncoder;

  private double goalRotate = 0;

  public ArmSubsystem() {
    rotate = Motors.ARM_LEAD;
    rotateFollower = Motors.ARM_FOLLOW;

    rotateFollower.follow(rotate, true);

    rotate.setIdleMode(Motors.ARM_MOTOR_IDLE_MODE);

    rotate.setSmartCurrentLimit(40);
    rotateFollower.setSmartCurrentLimit(40);

    rotateRelativeEncoder = rotate.getEncoder();

    rotateRelativeEncoder.setPositionConversionFactor(ArmConstants.DEGREES_PER_ROTATION);
    rotateRelativeEncoder.setPosition(0);//TODO: Set relative encoder based on REV encoder

    rotatePID = rotate.getPIDController();

    rotatePID.setFeedbackDevice(rotateRelativeEncoder);

    rotatePID.setP(.7); //TODO: Get PID values
    rotatePID.setI(0);
    rotatePID.setD(.9);
    //rotatePID.setFF(MAX_ROTATE_FEEDFORWARD);
    rotatePID.setOutputRange((-ArmConstants.MAX_ROTATION_SPEED / 4), ArmConstants.MAX_ROTATION_SPEED);
  }

   // For Testing
   public void updateDashboard(){ 
    // read PID coefficients from SmartDashboard
    double p =         SmartDashboard.getNumber("ANGLE P Gain", 0);
    double i =         SmartDashboard.getNumber("ANGLE I Gain", 0);
    double d =         SmartDashboard.getNumber("ANGLE D Gain", 0);
    double iz =        SmartDashboard.getNumber("ANGLE I Zone", 0);
    double ff =        SmartDashboard.getNumber("ANGLE Feed Forward", 0);
    double max =       SmartDashboard.getNumber("ANGLE Max Output", 0);
    double min =       SmartDashboard.getNumber("ANGLE Min Output", 0);

    // if PID coefficients on SmartDashboard have changed, write new values to controller
    if((p != rotatePID.getP())) { rotatePID.setP(p); }
    if((i != rotatePID.getI())) { rotatePID.setI(i); }
    if((d != rotatePID.getD())) { rotatePID.setD(d); }
    if((iz != rotatePID.getIZone())) { rotatePID.setIZone(iz); }
    if((ff != rotatePID.getFF())) { rotatePID.setFF(ff); }
    if((max != rotatePID.getOutputMax()) || (min != rotatePID.getOutputMin())) { 
      rotatePID.setOutputRange(min, max); 
    }
    
  }

  public void rotateArm(double Pose) {
    goalRotate = Pose;
  }

  public void rotateToIntake() {
    rotateArm(ArmConstants.POSITION_TABLE[0]);
  }

  public void rotateToShootClose() {
    rotateArm(ArmConstants.POSITION_TABLE[1]);
  }

  public void rotateToShootFar() {
    rotateArm(ArmConstants.POSITION_TABLE[2]);
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
  }

  public void checkMaxAndMin() {
    if(rotateRelativeEncoder.getPosition() > ArmConstants.MAX_ROTATE){
      goalRotate -= ArmConstants.ROTATE_CHANGE * 5;}
    else if(rotateRelativeEncoder.getPosition() < ArmConstants.MIN_ROTATE) {
      goalRotate += ArmConstants.ROTATE_CHANGE * 5;}
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

  @Override
  public void periodic() {
    checkMaxAndMin();
    rotatePID.setReference(goalRotate, CANSparkMax.ControlType.kPosition);
  }
}