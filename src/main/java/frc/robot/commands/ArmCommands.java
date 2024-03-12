package frc.robot.commands;

import frc.robot.controller.Operator;
import frc.robot.subsystems.Arm.ArmSubsystem;

import com.pathplanner.lib.auto.NamedCommands;

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
    Operator.A().onTrue(new InstantCommand(arm::rotateToAmpTrap));
    Operator.B().onTrue(new InstantCommand(arm::rotateToShootFar));
    Operator.Y().onTrue(new InstantCommand(arm::rotateToShootClose));
    Operator.X().onTrue(new InstantCommand(arm::rotateToIntake));
    Operator.Start().onTrue(new InstantCommand(arm::rotateToStart));

    NamedCommands.registerCommand("Rotate to amp", new MoveToAmp(arm));
    NamedCommands.registerCommand("Rotate to intake", new InstantCommand(arm::rotateToIntake));
    NamedCommands.registerCommand("Rotate to close shot", new InstantCommand(arm::rotateToShootClose));
    NamedCommands.registerCommand("Rotate to far shot", new InstantCommand(arm::rotateToShootFar));
    
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