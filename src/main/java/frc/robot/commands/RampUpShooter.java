// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.commands;

import java.util.function.BooleanSupplier;

import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.subsystems.Arm.ArmSubsystem;
import frc.robot.subsystems.ShooterIntake.ShooterIntake;

public class RampUpShooter extends Command {
  /** Creates a new RampUpShooter. */
  private final ShooterIntake shooterIntake;
  private final ArmSubsystem arm;

  public RampUpShooter(ShooterIntake shooterIntake, ArmSubsystem arm) {
    // Use addRequirements() here to declare subsystem dependencies.
    this.shooterIntake = shooterIntake;
    this.arm = arm;
    addRequirements(shooterIntake);
  }

  // Called when the command is initially scheduled.
  @Override
  public void initialize() {
    //shooterIntake.setShooterOn();
    if(!arm.isArmAtAmp()){
      if(!arm.isAutoAim()){
        shooterIntake.setCloseShotSpeed();
        shooterIntake.setShooterOn();
      }
    }
  }

  // Called every time the scheduler runs while the command is scheduled.
  @Override
  public void execute() {
    /*if(!isArmAtAmp.getAsBoolean()){
      shooterIntake.setShooterOn();
    }*/
  }

  // Called once the command ends or is interrupted.
  @Override
  public void end(boolean interrupted) {}

  // Returns true when the command should end.
  @Override
  public boolean isFinished() {
    if((shooterIntake.isShooterAtSpeed())||
       (arm.isArmAtAmp())){
      return true;
    }
    return false;
  }
}
