package frc.robot.commands;

import frc.robot.Operator;
import frc.robot.subsystems.Arm.ArmSubsystem;
import edu.wpi.first.math.MathUtil;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.InstantCommand;

public class ArmCommands extends Command {
  private final ArmSubsystem arm;

  public ArmCommands(ArmSubsystem arm) {
    this.arm = arm;

    addRequirements(arm);
  }

  @Override
  public void initialize() {
    Operator.Y.onTrue(new InstantCommand(arm::rotateToAmpTrap));
    Operator.B.onTrue(new InstantCommand(arm::rotateToShootFar));
    Operator.A.onTrue(new InstantCommand(arm::rotateToShootClose));
    Operator.X.onTrue(new InstantCommand(arm::rotateToIntake));
    Operator.LeftBumper.onTrue(new InstantCommand(arm::rotateToStart));
    
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