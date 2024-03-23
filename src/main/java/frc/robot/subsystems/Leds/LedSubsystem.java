// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.subsystems.Leds;

import java.util.function.BooleanSupplier;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.motorcontrol.Spark;
import edu.wpi.first.wpilibj2.command.SubsystemBase;

final class LedConstants{
  public static final Spark BLINKIN = new Spark(9);
  public static final double ORANGE = .65;
  public static final double RED = .61;
  public static final double BLUE = .87;
}

public class LedSubsystem extends SubsystemBase {
  /** Creates a new LedSubsystem. */
  private BooleanSupplier isNotePresent;
  private Spark blinkin = LedConstants.BLINKIN;

  public LedSubsystem(BooleanSupplier isNotePresent) {
    this.isNotePresent = isNotePresent;
  }

  @Override
  public void periodic() {
    // This method will be called once per scheduler run
    var alliance = DriverStation.getAlliance();
    if(isNotePresent.getAsBoolean()){
      blinkin.set(LedConstants.ORANGE);
    }else if((alliance.isPresent())&&(alliance.get() == DriverStation.Alliance.Red)){
      blinkin.set(LedConstants.RED);
    }else{
      blinkin.set(LedConstants.BLUE);
    }
  }
}
