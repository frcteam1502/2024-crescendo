package frc.robot.commands;

import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.Driver;
import frc.robot.subsystems.PowerManagement.AdaptiveSpeedController;
import frc.robot.subsystems.PowerManagement.IBrownOutDetector;
import frc.robot.subsystems.SwerveDrive.DriveSubsystem;

import edu.wpi.first.math.MathUtil;
import edu.wpi.first.math.filter.SlewRateLimiter;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

final class DriveConstants {
  public static final double MAX_SPEED_METERS_PER_SECOND = 4.6;
  public static final double MAX_TELEOP_SPEED = 1; //Range 0 to 1
  public static final double MAX_FINESSE_SPEED = .3;

  public static final double MAX_ROTATION_RADIANS_PER_SECOND = 11; //w = ((max_speed)/(2*pi*robot_radius))*(2*pi)
  public static final double MAX_TELEOP_ROTATION = .3;
  public static final double MAX_FINESSE_ROTATION = .1;

  public static final boolean ADAPTIVE_LIMITING_ENABLED = false;
}

public class ControllerCommands extends Command {
  private final DriveSubsystem drive;
  private final AdaptiveSpeedController speedController;

  private SlewRateLimiter turnLimiter = new SlewRateLimiter(5);
  
  public ControllerCommands(DriveSubsystem drive, IBrownOutDetector brownOutDetector) {
    this.drive = drive;
    this.speedController = new AdaptiveSpeedController(brownOutDetector, 3.0, DriveConstants.MAX_FINESSE_SPEED, DriveConstants.MAX_TELEOP_SPEED);
    addRequirements(drive);
  }

  @Override
  public void initialize() {}

  @Override
  public void execute() {
    double teleopSpeedGain;
    double teleopRotationGain;

    if(Driver.Buttons.LEFT_BUMPER.getAsBoolean()){
      teleopSpeedGain = DriveConstants.MAX_FINESSE_SPEED;
      teleopRotationGain = DriveConstants.MAX_FINESSE_ROTATION;
    }else{
      teleopSpeedGain = DriveConstants.MAX_TELEOP_SPEED;
      teleopRotationGain = DriveConstants.MAX_TELEOP_ROTATION;
    }
    //Need to convert joystick input (-1 to 1) into m/s!!! 100% == MAX Attainable Speed
    double forwardSpeed = ((MathUtil.applyDeadband(Driver.getLeftY(), 0.1)) * teleopSpeedGain) *
        DriveConstants.MAX_SPEED_METERS_PER_SECOND;

    double strafeSpeed = ((MathUtil.applyDeadband(Driver.getLeftX(), 0.1)) * teleopSpeedGain) *
        DriveConstants.MAX_SPEED_METERS_PER_SECOND;

    //Need to convert joystick input (-1 to 1) into m/s!!! 100% == MAX Attainable Rotation
    double rotationSpeed = turnLimiter.calculate(((MathUtil.applyDeadband(Driver.getRightX(), 0.1)) * teleopRotationGain) *
    DriveConstants.MAX_ROTATION_RADIANS_PER_SECOND);

    SmartDashboard.putNumber("Forward In", forwardSpeed);
    SmartDashboard.putNumber("Strafe In", strafeSpeed);
    SmartDashboard.putNumber("Rotation In", rotationSpeed);

    if(DriveConstants.ADAPTIVE_LIMITING_ENABLED){
      var speedCommand = speedController.GetSpeedCommand(
        forwardSpeed, // Forward
        strafeSpeed, // Strafe
        rotationSpeed, // Rotate
        Driver.Buttons.LEFT_BUMPER.getAsBoolean()); // brake
    
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