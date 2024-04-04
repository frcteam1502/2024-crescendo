// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.commands;

import edu.wpi.first.wpilibj2.command.ParallelCommandGroup;
import frc.robot.subsystems.Arm.ArmSubsystem;
import frc.robot.subsystems.ShooterIntake.ShooterIntake;

// NOTE:  Consider using this command inline, rather than writing a subclass.  For more
// information, see:
// https://docs.wpilib.org/en/stable/docs/software/commandbased/convenience-features.html
public class RampAndMoveToSourceAutoShot extends ParallelCommandGroup {
  /** Creates a new RampAndMoveToSourceAutoShot. */
  public RampAndMoveToSourceAutoShot(ShooterIntake shooterIntake, ArmSubsystem arm) {
    // Add your commands in the addCommands() call, e.g.
    // addCommands(new FooCommand(), new BarCommand());
    addCommands(
      new RampUpShooter(shooterIntake, arm),
      new MoveToSourceAutoShot(arm));
  }
}
