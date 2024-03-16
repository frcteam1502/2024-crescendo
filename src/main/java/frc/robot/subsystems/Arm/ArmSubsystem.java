package frc.robot.subsystems.Arm;

import frc.robot.Logger;
import frc.robot.commands.ArmCommands;

import team1502.configuration.annotations.DefaultCommand;
import team1502.configuration.factory.RobotConfiguration;

import com.revrobotics.CANSparkMax;
import com.revrobotics.RelativeEncoder;
import com.revrobotics.SparkPIDController;

import edu.wpi.first.wpilibj.DutyCycleEncoder;
import edu.wpi.first.wpilibj.Solenoid;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.SubsystemBase;

@DefaultCommand(command = ArmCommands.class)
public class ArmSubsystem extends SubsystemBase {
  public static final String BRAKE_SOLENOID = "Brake Solenoid";

  //Rotation bounds
  private static final double MAX_ROTATE = 5;
  private static final double MIN_ROTATE = -95;
  private static final double MAX_ROTATE_FEEDFORWARD = .06; //TODO: increase?
  private static final double ROTATE_CHANGE = .3; 
  
  enum ArmPosition
  {
    Intake     (-0.5), //Intake
    ShootClose (-24),  //Shoot Close
    ShootFar   (-34),  //Shoot Far
    StowStart  (-76),  //Stow/Start
    AmpTrap    (-90);  //Amp/Trap

    public double angle;
    private ArmPosition(double degrees) {angle = degrees;}
  };

  private static final double BRAKE_THRESHOLD = 0.25;
  private static final double AMP_POSITION_THRESHOLD = 2.0;

  private final double ABS_OFFSET; //This is unique for the robot!

  private final SparkPIDController rotatePID;
  private final RelativeEncoder rotateRelativeEncoder;
  private final DutyCycleEncoder rotateAbsEncoder;
  private final Solenoid brakeSolenoid;

  private double goalRotate = 0;

  private double arm_p_gain;
  private double arm_intake_angle = ArmPosition.Intake.angle;
  private double arm_close_angle = ArmPosition.ShootClose.angle;
  private double arm_far_angle = ArmPosition.ShootFar.angle;

  public ArmSubsystem(RobotConfiguration config) {    
    ABS_OFFSET = config.Encoder().getDouble("ABS_OFFSET");

    //Initialize Motors
    config.MotorController().buildSparkMax();
    arm_p_gain = config.MotorController().PID().P();
    rotateRelativeEncoder = config.MotorController().buildRelativeEncoder();
    rotatePID = config.MotorController().buildPIDController(rotateRelativeEncoder);
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
   private void updateDashboard(){ 
    
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
    SmartDashboard.putBoolean("Is Arm At Rotation Goal", isArmAtRotateGoal());
  }

  public void reset(){
    //Set Arm relative Position to absolute position
    double zeroedArmAbsPosition = getArmAbsPositionDegrees();

    rotateRelativeEncoder.setPosition((zeroedArmAbsPosition));
    goalRotate = (zeroedArmAbsPosition);
  }

  private double getArmAbsPositionDegrees(){
    //REV Encoder is CCW+
    double angleDegrees = rotateAbsEncoder.getAbsolutePosition()*360;

    angleDegrees = angleDegrees - 360;

    return (angleDegrees - ABS_OFFSET);
  }

  private void rotateArm(double position) {
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
    rotateArm(ArmPosition.StowStart.angle);
  }

  public void rotateToAmpTrap() {
    rotateArm(ArmPosition.AmpTrap.angle);
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
       (rotateRelativeEncoder.getPosition() < threshold_min)) {
      brakeSolenoid.set(true); }
    else {
      brakeSolenoid.set(false); }
  }

  public boolean isArmAtAmp(){
    if(getArmAbsPositionDegrees()<=(ArmPosition.AmpTrap.angle + AMP_POSITION_THRESHOLD)){
      return true; }
    else {
      return false; }
  }

  private void checkMaxAndMin() {
    if(rotateRelativeEncoder.getPosition() > MAX_ROTATE){
      goalRotate -= ROTATE_CHANGE * 2;}
    else if(rotateRelativeEncoder.getPosition() < MIN_ROTATE) {
      goalRotate += ROTATE_CHANGE * 2;}
  }

  public boolean isArmAtRotateGoal(){
    double angle = rotateRelativeEncoder.getPosition();
    if((angle>= goalRotate-1.0)&&
       (angle<=goalRotate+1.0)){
        return true;
       }
    return false;
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
  private double dynamicFeedForward(double currentAngle) {
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