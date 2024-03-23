// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.commands;

import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.Driver;
import frc.robot.subsystems.Climber.Climber;

public class ClimberCommands extends Command {
  /** Creates a new ClimberCommands. */
  private Climber climber;

  public ClimberCommands(Climber climber) {
    // Use addRequirements() here to declare subsystem dependencies.
    this.climber = climber;
    addRequirements(climber);
  }

  // Called when the command is initially scheduled.
  @Override
  public void initialize() {}

  // Called every time the scheduler runs while the command is scheduled.
  @Override
  public void execute() {
    if(Driver.Controller.getHID().getYButton()){
      climber.climbUp();
    }else if(Driver.Controller.getHID().getAButton()){
      climber.climbDown();
    }else{
      climber.climberOff();
    }
  }

  // Called once the command ends or is interrupted.
  @Override
  public void end(boolean interrupted) {}

  // Returns true when the command should end.
  @Override
  public boolean isFinished() {
    return false;
  }
}
