// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.commands;

import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.InstantCommand;
import frc.robot.Operator;
import frc.robot.subsystems.ShooterIntake.ShooterIntake;

public class ShooterIntakeCommands extends Command {
  private final ShooterIntake shooterIntake;

  public ShooterIntakeCommands(ShooterIntake shooterIntake) {
    this.shooterIntake = shooterIntake;
    
    // Use addRequirements() here to declare subsystem dependencies.
    addRequirements(shooterIntake);
  }

  // Called when the command is initially scheduled.
  @Override
  public void initialize() {
    Operator.rightTrigger(0.5).onTrue(new InstantCommand(shooterIntake::setShooterOn));
    Operator.rightTrigger(0.5).onFalse(new InstantCommand(shooterIntake::setShooterOff));

    Operator.leftTrigger(.5).whileTrue(new PickupNote(shooterIntake));
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
