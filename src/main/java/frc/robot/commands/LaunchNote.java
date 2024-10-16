// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.commands;

import java.util.function.BooleanSupplier;

import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.subsystems.Arm.ArmSubsystem;
import frc.robot.subsystems.ShooterIntake.ShooterIntake;

public class LaunchNote extends Command {
  /** Creates a new LaunchNote. */
  private final ShooterIntake shooterIntake;
  private final ArmSubsystem arm;

  private final Timer launchNoteTimer = new Timer();

  public LaunchNote(ShooterIntake shooterIntake, ArmSubsystem arm) {
    // Use addRequirements() here to declare subsystem dependencies.
    this.shooterIntake = shooterIntake;
    this.arm = arm;
    launchNoteTimer.reset();

    addRequirements(shooterIntake);
  }

  // Called when the command is initially scheduled.
  @Override
  public void initialize() {
    launchNoteTimer.reset();
    launchNoteTimer.start();
  }

  // Called every time the scheduler runs while the command is scheduled.
  @Override
  public void execute() {
    if(!arm.isArmAtAmp()){
      shooterIntake.setIntakeShoot();
    }else{
      shooterIntake.setIntakeAmp();
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
    if(launchNoteTimer.get() > 1.0){
      return true;
    }
    return false;
  }
}
