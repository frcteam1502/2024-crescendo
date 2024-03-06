package frc.robot.subsystems.Arm;

import frc.robot.Logger;

import com.revrobotics.CANSparkMax;
import com.revrobotics.RelativeEncoder;
import com.revrobotics.SparkPIDController;

import edu.wpi.first.wpilibj.DutyCycleEncoder;
import edu.wpi.first.wpilibj.Solenoid;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.commands.ArmCommands;
import team1502.configuration.annotations.DefaultCommand;
import team1502.configuration.factory.RobotConfiguration;

@DefaultCommand(command = ArmCommands.class)
public class ArmSubsystem extends SubsystemBase {
  public static final String BRAKE_SOLENOID = "Brake Solenoid";

  //Rotation bounds
  private static final double MAX_ROTATE = 5;
  private static final double MIN_ROTATE = -95;
  private static final double MAX_ROTATE_FEEDFORWARD = .06; //TODO: increase?
  private static final double ROTATE_CHANGE = .3; 
  
  private static final double[] POSITION_TABLE = 
  {
    -0.5, //Intake
    -24,  //Shoot Close
    -43,  //Shoot Far
    -70,  //Stow/Start
    -90,  //Amp/Trap
  };

  private static final double BRAKE_THRESHOLD = 0.25;
  private static final double INTAKE_POSITION_THRESHOLD = POSITION_TABLE[0] - 1.0;
  private static final double AMP_POSITION_THRESHOLD = POSITION_TABLE[4] + 1.0;

  private final double ABS_OFFSET; //This is unique for the robot!
  
  private final SparkPIDController rotatePID;
  private final RelativeEncoder rotateRelativeEncoder;
  private final DutyCycleEncoder rotateAbsEncoder;
  private final Solenoid brakeSolenoid;

  private double goalRotate = 0;

  private double arm_p_gain; // = ArmConstants.ARM_P_GAIN;
  private double arm_intake_angle = POSITION_TABLE[0];
  private double arm_close_angle = POSITION_TABLE[1];
  private double arm_far_angle = POSITION_TABLE[2];

  public ArmSubsystem(RobotConfiguration config) {
    config = config.Subsystem(ArmSubsystem.class);
    
    ABS_OFFSET = config.Encoder().getDouble("ABS_OFFSET");

    //Initialize Motors
    config.MotorController().buildSparkMax();
    arm_p_gain = config.MotorController().PID().P();
    rotateRelativeEncoder = config.MotorController().buildRelativeEncoder();
    rotatePID = config.MotorController().createPIDController(rotateRelativeEncoder);
    rotateAbsEncoder = config.Encoder().buildDutyCycleEncoder();
    brakeSolenoid = config.Solenoid(BRAKE_SOLENOID).buildSolenoid();

    SmartDashboard.putNumber("ANGLE P Gain", arm_p_gain);
    SmartDashboard.putNumber("Arm Intake Angle", arm_intake_angle);
    SmartDashboard.putNumber("Arm Close Angle", arm_close_angle);
    SmartDashboard.putNumber("Arm Far Angle", arm_far_angle);
    
    //Reset the subsystem
    reset();
    
    config.MotorController().registerLoggerObjects((n, r)->Logger.RegisterCanSparkMax(n,r));
    Logger.RegisterSensor("Arm Absolute Sensor", ()->getArmAbsPositionDegrees());

  }

   // For Testing
   public void updateDashboard(){ 
    
    // read PID coefficients from SmartDashboard
    arm_p_gain = SmartDashboard.getNumber("ANGLE P Gain", 0);
    // if PID coefficients on SmartDashboard have changed, write new values to controller
    if((arm_p_gain != rotatePID.getP())) { rotatePID.setP(arm_p_gain); }
  
    arm_intake_angle = SmartDashboard.getNumber("Arm Intake Angle", 0);
    arm_close_angle = SmartDashboard.getNumber("Arm Close Angle", 0);
    arm_far_angle = SmartDashboard.getNumber("Arm Far Angle", 0);
    SmartDashboard.putNumber("Arm Far Angle", arm_far_angle);
    SmartDashboard.putNumber("Arm Absolute Encoder Angle", getArmAbsPositionDegrees());
    SmartDashboard.putNumber("Arm Relative Encoder", rotateRelativeEncoder.getPosition());
    SmartDashboard.putNumber("Rotation Goal", goalRotate);
    SmartDashboard.putBoolean("Is Arm At Intake", isArmAtIntake());
    SmartDashboard.putBoolean("Is Arm At Amp", isArmAtAmp());
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

    return (angleDegrees - ABS_OFFSET);
  }

  public void rotateArm(double position) {
    goalRotate = position;
  }

  public void rotateToIntake() {
    rotateArm(arm_intake_angle);
  }

  public void rotateToShootClose() {
    //rotateArm(POSITION_TABLE[1]);
    rotateArm(arm_close_angle);
  }

  public void rotateToShootFar() {
    //rotateArm(POSITION_TABLE[2]);
    rotateArm(arm_far_angle);
  }

  public void rotateToStart() {
    rotateArm(POSITION_TABLE[3]);
  }

  public void rotateToAmpTrap() {
    rotateArm(POSITION_TABLE[4]);
  }

  public void rotateManually(double input) {
    double change = Math.signum(input) * ROTATE_CHANGE;
    if(input > .8) change *= 2;
    rotateArm(goalRotate + change);
    //goalRotate = input;
  }

  private void checkBrake(){
    double threshold_max = goalRotate + BRAKE_THRESHOLD;
    double threshold_min = goalRotate - BRAKE_THRESHOLD;
    if((rotateRelativeEncoder.getPosition() > threshold_max)||
       (rotateRelativeEncoder.getPosition() < threshold_min)){
        brakeSolenoid.set(true);
       }else{
        brakeSolenoid.set(false);
       }

  }

  public void checkMaxAndMin() {
    if(rotateRelativeEncoder.getPosition() > MAX_ROTATE){
      goalRotate -= ROTATE_CHANGE * 2;}
    else if(rotateRelativeEncoder.getPosition() < MIN_ROTATE) {
      goalRotate += ROTATE_CHANGE * 2;}
  }

  public boolean isArmAtIntake(){
    if(getArmAbsPositionDegrees() >= INTAKE_POSITION_THRESHOLD){
      return true;
    }else{
      return false;
    }
  }

  public boolean isArmAtAmp(){
    if(getArmAbsPositionDegrees() <= AMP_POSITION_THRESHOLD){
      return true;
    }else{
      return false;
    }
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
    return MAX_ROTATE_FEEDFORWARD * Math.cos(currentAngle);
  }

  @Override
  public void periodic() {
    updateDashboard();
    checkBrake();
    checkMaxAndMin();
    rotatePID.setReference(goalRotate, CANSparkMax.ControlType.kPosition);
  }
}