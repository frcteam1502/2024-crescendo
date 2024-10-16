// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.commands;

import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.subsystems.Arm.ArmSubsystem;

public class MoveToAmp3NoteClose extends Command {
  /** Creates a new MoveToShoot. */

  private final ArmSubsystem arm;

  public MoveToAmp3NoteClose(ArmSubsystem arm) {
    // Use addRequirements() here to declare subsystem dependencies.
    this.arm = arm;
    addRequirements(arm);
  }

  // Called when the command is initially scheduled.
  @Override
  public void initialize() {
    arm.rotateToAmpSide3NoteClose();
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
    if(arm.isArmAtRotateGoal()){
      return true;
    }
    return false;
  }
}
