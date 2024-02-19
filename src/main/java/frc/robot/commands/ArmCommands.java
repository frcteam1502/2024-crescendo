package frc.robot.commands;

import frc.robot.Operator;
import frc.robot.subsystems.Arm.ArmSubsystem;
import edu.wpi.first.math.MathUtil;
import edu.wpi.first.wpilibj2.command.Command;

public class ArmCommands extends Command {
  @SuppressWarnings({ "PMD.UnusedPrivateField", "PMD.SingularField" })
  private final ArmSubsystem arm;

  public ArmCommands(ArmSubsystem arm) {
    this.arm = arm;

    addRequirements(arm);
  }

  @Override
  public void initialize() {
    arm.reset();
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