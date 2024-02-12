package frc.robot.commands;

import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.Driver;
import frc.robot.subsystems.PowerManagement.MockDetector;
import frc.robot.subsystems.SwerveDrive.DriveSubsystem;
import team1502.configuration.factory.RobotConfiguration;

public class ControllerCommands extends Command {
  private final DriveSubsystem drive;
  
  //public ControllerCommands(DriveSubsystem drive, IBrownOutDetector brownOutDetector) {
  public ControllerCommands(RobotConfiguration config, DriveSubsystem drive, MockDetector brownOutDetector) {
    this.drive = drive;
    addRequirements(drive);
  }

  @Override
  public void initialize() {}

  @Override
  public void execute() {
    var y = Driver.getLeftY();
  }

  @Override
  public void end(boolean interrupted) {}

  @Override
  public boolean isFinished() {
    return false;
  }
}