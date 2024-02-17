package frc.robot.commands;

import frc.robot.Operator;
import frc.robot.subsystems.Arm.ArmSubsystem;
import edu.wpi.first.math.MathUtil;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.InstantCommand;

public class ArmCommands extends Command {
  @SuppressWarnings({ "PMD.UnusedPrivateField", "PMD.SingularField" })
  private final ArmSubsystem arm;

  public ArmCommands(ArmSubsystem arm) {
    this.arm = arm;

    addRequirements(arm);

  }

  @Override
  public void initialize() {
    Operator.XboxButtons.Y.onTrue(new InstantCommand(arm::rotateToAmpTrap));
    Operator.XboxButtons.B.onTrue(new InstantCommand(arm::rotateToShootFar));
    Operator.XboxButtons.A.onTrue(new InstantCommand(arm::rotateToShootClose));
    Operator.XboxButtons.X.onTrue(new InstantCommand(arm::rotateToIntake));
    Operator.XboxButtons.LeftBumper.onTrue(new InstantCommand(arm::rotateToStart));
  }

  @Override
  public void execute() {
    arm.rotateManually(MathUtil.applyDeadband(Operator.getLeftY(), 0.1));
  }

  @Override
  public void end(boolean interrupted) {
  }

  @Override
  public boolean isFinished() {
    return false;
  }
}