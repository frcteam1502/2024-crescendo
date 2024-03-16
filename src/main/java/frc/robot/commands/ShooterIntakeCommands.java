// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.commands;

import com.pathplanner.lib.auto.NamedCommands;

import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.InstantCommand;
import frc.robot.controller.Operator;
import frc.robot.subsystems.Arm.ArmSubsystem;
import frc.robot.subsystems.ShooterIntake.ShooterIntake;

public class ShooterIntakeCommands extends Command {

  /** Creates a new ShooterIntakeCommands. */
  private final ShooterIntake shooterIntake;
  private final ArmSubsystem arm;

  public ShooterIntakeCommands(ShooterIntake shooterIntake, ArmSubsystem arm) {
    this.shooterIntake = shooterIntake;
    this.arm = arm;
    addRequirements(shooterIntake, arm);

    NamedCommands.registerCommand("Intake on", new IntakeNote(shooterIntake));
    NamedCommands.registerCommand("Intake off", new InstantCommand(shooterIntake::setIntakeOff));
    NamedCommands.registerCommand("Shot Note", new ShootNote(shooterIntake, ()->arm.isArmAtAmp()));
    NamedCommands.registerCommand("Shooter On", new InstantCommand(shooterIntake::setShooterOn));
  }

  // Called when the command is initially scheduled.
  @Override
  public void initialize() {
    Operator.RightTrigger(0.5).onTrue(new ShootNote(shooterIntake, ()->arm.isArmAtAmp()));
    Operator.RightBumper().toggleOnTrue(new InstantCommand(shooterIntake::toggleShooter));

    Operator.LeftTrigger(.5).whileTrue(new IntakeNote(shooterIntake)); // whileTrue() is causing CommandScheduler overruns!
    Operator.LeftBumper().onTrue(new InstantCommand(shooterIntake::setIntakeEject));
    Operator.LeftBumper().onFalse(new InstantCommand(shooterIntake::setIntakeOff));
  }

  // Called every time the scheduler runs while the command is scheduled.
  @Override
  public void execute() {}

  // Called once the command ends or is interrupted.
  @Override
  public void end(boolean interrupted) {}

  // Returns true when the command should end.
  @Override
  public boolean isFinished() {
    return false;
  }
}
