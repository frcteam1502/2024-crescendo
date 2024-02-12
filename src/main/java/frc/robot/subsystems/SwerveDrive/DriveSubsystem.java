package frc.robot.subsystems.SwerveDrive;

import edu.wpi.first.util.sendable.Sendable;
import edu.wpi.first.util.sendable.SendableBuilder;
import edu.wpi.first.wpilibj2.command.Subsystem;
import frc.robot.commands.ControllerCommands;
import team1502.configuration.annotations.DefaultCommand;
import team1502.configuration.factory.RobotConfiguration;

@DefaultCommand(command = ControllerCommands.class)
public class DriveSubsystem implements Subsystem, Sendable {
  

  public DriveSubsystem(RobotConfiguration config) {

  }
  
  @Override
  public void periodic() {
  }
  
  public void drive(double xSpeed, double ySpeed, double rot, boolean fieldRelative) {
  }

  @Override
  public void initSendable(SendableBuilder builder) {

  }
}
