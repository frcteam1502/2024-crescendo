// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.commands;

import java.util.function.BooleanSupplier;
import java.util.function.DoubleSupplier;

import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.subsystems.ShooterIntake.ShooterIntake;

public class AutoShootSpeed extends Command {
  /** Creates a new AutoShootSpeed. */
  private final ShooterIntake shooterIntake;
  private DoubleSupplier distance;
  private BooleanSupplier distanceValid;

  public AutoShootSpeed(ShooterIntake shooterIntake, DoubleSupplier distance, BooleanSupplier distanceValid) {
    // Use addRequirements() here to declare subsystem dependencies.
    this.shooterIntake = shooterIntake;
    this.distance = distance;
    this.distanceValid = distanceValid;
    addRequirements(shooterIntake);
  }

  // Called when the command is initially scheduled.
  @Override
  public void initialize() {
    shooterIntake.lookupShooterSpeed(distance.getAsDouble(), distanceValid.getAsBoolean());
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
    return true;
  }
}
