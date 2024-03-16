package frc.robot.commands;

import frc.robot.Driver;
import frc.robot.Operator;
import frc.robot.subsystems.PowerManagement.AdaptiveSpeedController;
import frc.robot.subsystems.PowerManagement.IBrownOutDetector;
import frc.robot.subsystems.PowerManagement.MockDetector;
import frc.robot.subsystems.SwerveDrive.DriveSubsystem;

import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.InstantCommand;
import edu.wpi.first.math.MathUtil;
import edu.wpi.first.math.filter.SlewRateLimiter;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

import team1502.configuration.factory.RobotConfiguration;

public class ControllerCommands extends Command {
  private static final double MAX_TELEOP_SPEED = 1; //Range 0 to 1
  private static final double MAX_TELEOP_SPEED_DRIVER_1 = 1; //Range 0 to 1
  private static final double MAX_TELEOP_SPEED_DRIVER_2 = 2; //Range 0 to 1
  private static final double MAX_FINESSE_SPEED = .3;

  private static final double MAX_TELEOP_ROTATION = .3;
  private static final double MAX_FINESSE_ROTATION = .1;

  private static final boolean ADAPTIVE_LIMITING_ENABLED = false;

  private final DriveSubsystem drive;
  private final AdaptiveSpeedController speedController;
  private final double maxSpeed;
  private final double maxRotationSpeed;

  private final String kDriver1 = "Austin";
  private final String kDriver2 = "Ethan";

  private SlewRateLimiter turnLimiter = new SlewRateLimiter(5);
  private final SendableChooser<String> driverChooser = new SendableChooser<>();
  
  public ControllerCommands(RobotConfiguration config, DriveSubsystem drive, MockDetector brownOutDetector) {
    this.drive = drive;
    this.maxSpeed = config.SwerveDrive().calculateMaxSpeed();
    this.maxRotationSpeed = config.SwerveDrive().calculateMaxRotationSpeed();
    this.speedController = new AdaptiveSpeedController(brownOutDetector, 3.0, MAX_FINESSE_SPEED, MAX_TELEOP_SPEED);
    addRequirements(drive);

    driverChooser.setDefaultOption("Default Driver", kDriver1);
    driverChooser.addOption("Austin", kDriver1);
    driverChooser.addOption("Ethan", kDriver2);
    SmartDashboard.putData("Driver Chooser", driverChooser);
  }

  @Override
  public void initialize() {
    //Driver.A.onTrue(new AlignToSpeaker(this));

  }

  @Override
  public void execute() {
    double teleopSpeedGain;
    double teleopRotationGain;
    double driver_gain;

    String driver = (String) driverChooser.getSelected();

    switch(driver){
      case kDriver2:
        driver_gain = MAX_TELEOP_SPEED_DRIVER_2;

      default:
        driver_gain = MAX_TELEOP_SPEED_DRIVER_1;
    }

    if(Driver.LeftBumper.getAsBoolean()){
      teleopSpeedGain = MAX_FINESSE_SPEED;
      teleopRotationGain = MAX_FINESSE_ROTATION;
    }else{
      teleopSpeedGain = driver_gain;
      teleopRotationGain = MAX_TELEOP_ROTATION;
    }
    //Need to convert joystick input (-1 to 1) into m/s!!! 100% == MAX Attainable Speed
    double forwardSpeed = ((MathUtil.applyDeadband(Driver.getLeftY(), 0.1)) * teleopSpeedGain) * maxSpeed;
    double strafeSpeed = ((MathUtil.applyDeadband(Driver.getLeftX(), 0.1)) * teleopSpeedGain) * maxSpeed;

    //Need to convert joystick input (-1 to 1) into m/s!!! 100% == MAX Attainable Rotation
    double rotationSpeed = turnLimiter.calculate(((MathUtil.applyDeadband(Driver.getRightX(), 0.1)) * teleopRotationGain) * maxRotationSpeed);

    SmartDashboard.putNumber("Forward In", forwardSpeed);
    SmartDashboard.putNumber("Strafe In", strafeSpeed);
    SmartDashboard.putNumber("Rotation In", rotationSpeed);

    if(ADAPTIVE_LIMITING_ENABLED){
      var speedCommand = speedController.GetSpeedCommand(
        forwardSpeed, // Forward
        strafeSpeed, // Strafe
        rotationSpeed, // Rotate
        Driver.LeftBumper.getAsBoolean()); // brake
    
      drive.drive(-speedCommand.forwardSpeed, -speedCommand.strafeSpeed, -speedCommand.rotationSpeed, true);
    }else{
      drive.drive(-forwardSpeed, -strafeSpeed, -rotationSpeed, true);
    }

  }

  @Override
  public void end(boolean interrupted) {}

  @Override
  public boolean isFinished() {
    return false;
  }
}