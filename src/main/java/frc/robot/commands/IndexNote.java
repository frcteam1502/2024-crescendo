// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.commands;

import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.subsystems.ShooterIntake.ShooterIntake;

public class IndexNote extends Command {
  /** Creates a new IndexNote. */
  private final ShooterIntake shooterIntake;

  private final Timer indexTimer = new Timer();
  
  public IndexNote(ShooterIntake shooterIntake) {
    // Use addRequirements() here to declare subsystem dependencies.
    this.shooterIntake = shooterIntake;
    addRequirements(shooterIntake);

    indexTimer.reset();
  }

  // Called when the command is initially scheduled.
  @Override
  public void initialize() {
    indexTimer.reset();
    indexTimer.start();
  }

  // Called every time the scheduler runs while the command is scheduled.
  @Override
  public void execute() {
    shooterIntake.setIntakeIndex();
  }

  // Called once the command ends or is interrupted.
  @Override
  public void end(boolean interrupted) {
    shooterIntake.setIntakeOff();
  }

  // Returns true when the command should end.
  @Override
  public boolean isFinished() {
    
    //Hack until photosensor is working!
    if(indexTimer.get() > .5){
      return true;
    }
    /*if(shooterIntake.isNotePresent()){
      return true;
    }*/
    return false;
  }
}
