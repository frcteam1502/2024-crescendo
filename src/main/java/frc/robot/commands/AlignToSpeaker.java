// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.commands;

import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.subsystems.SwerveDrive.DriveSubsystem;


public class AlignToSpeaker extends Command {
  /** Creates a new AlignToSpeaker. */
  private DriveSubsystem driveSubsystem;
  
  public AlignToSpeaker(DriveSubsystem driveSubsystem){
    // Use addRequirements() here to declare subsystem dependencies.
    this.driveSubsystem = driveSubsystem;
    addRequirements(driveSubsystem);
  }

  // Called when the command is initially scheduled.
  @Override
  public void initialize() {}

  // Called every time the scheduler runs while the command is scheduled.
  @Override
  public void execute() {
    driveSubsystem.drive(0, 0, driveSubsystem.vision_aim_proportional(), true);
  }

  // Called once the command ends or is interrupted.
  @Override
  public void end(boolean interrupted) {
    driveSubsystem.drive(0, 0, 0, true);
  }

  // Returns true when the command should end.
  @Override
  public boolean isFinished() {
    if((driveSubsystem.isSpeakerDataValid()) && (driveSubsystem.getVisionTargetAngle() < 1.0)){
      return true;
    }
    return false;
  }
}
