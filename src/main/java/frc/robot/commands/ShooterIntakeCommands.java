// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.commands;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.Operator;
import frc.robot.subsystems.ShooterIntake.ShooterIntake;

public class ShooterIntakeCommands extends Command {
  /** Creates a new ShooterIntakeCommands. */
  private final ShooterIntake shooterIntake;

  public ShooterIntakeCommands(ShooterIntake shooterIntake) {
    // Use addRequirements() here to declare subsystem dependencies.
    this.shooterIntake = shooterIntake;
    addRequirements(shooterIntake);
  }

  // Called when the command is initially scheduled.
  @Override
  public void initialize() {}

  // Called every time the scheduler runs while the command is scheduled.
  @Override
  public void execute() {
    //double speed = SmartDashboard.getNumber("Shooter Speed (RPM)", 0);
    double speed = 2500;
    
    if(Operator.getRightTrigger() > 0.55){
      shooterIntake.setShooterSpeed(speed);
    }else if (Operator.getRightTrigger() < 0.45){
      shooterIntake.setShooterSpeed(0);
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
