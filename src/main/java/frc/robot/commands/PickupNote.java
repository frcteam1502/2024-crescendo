// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.commands;

import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.subsystems.ShooterIntake.ShooterIntake;

public class PickupNote extends Command {
  /** Creates a new IntakeNote. */
  private final ShooterIntake shooterIntake;

  private final Timer pickupTimer = new Timer();

  public PickupNote(ShooterIntake shooterIntake) {
    // Use addRequirements() here to declare subsystem dependencies.
    this.shooterIntake = shooterIntake;

    pickupTimer.reset();

    addRequirements(shooterIntake);
  }

  // Called when the command is initially scheduled.
  @Override
  public void initialize() {
    pickupTimer.reset();
    pickupTimer.start();
  }

  // Called every time the scheduler runs while the command is scheduled.
  @Override
  public void execute() {
    if(!shooterIntake.isNotePresent()){
      shooterIntake.setIntakePickup();
    }
  }

  // Called once the command ends or is interrupted.
  @Override
  public void end(boolean interrupted) {
    shooterIntake.setIntakeOff();
  }

  // Returns true when the command should end.
  @Override
  public boolean isFinished() {
    //Wait for inrush current
    if(pickupTimer.get() > 0.5){

      if((shooterIntake.getIntakeCurrent() >= 20)||
         (shooterIntake.isNotePresent())){
        return true;
      }
    }
    return false;
  }
}
