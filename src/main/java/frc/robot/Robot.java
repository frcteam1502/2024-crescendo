// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import edu.wpi.first.wpilibj.Filesystem;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.CommandScheduler;
import frc.testmode.swerve.AbsoluteEncoderAlignment;

public class Robot extends TimedRobot {
  private RobotContainer m_robotContainer;

  public String branch = "unknown";
  public String commit = "unknown";
  public String radio = "1502";

  @Override
  public void robotInit() {
    Path deployPath = Filesystem.getDeployDirectory().toPath();
    try {
      branch = Files.readString(deployPath.resolve("branch.txt"));
      commit = Files.readString(deployPath.resolve("commit.txt"));
      radio = Files.readString(deployPath.resolve("wifi.txt"));
    }   catch (IOException ex) { }
    
    m_robotContainer = new RobotContainer(radio);
  }

  @Override
  public void robotPeriodic() {
    CommandScheduler.getInstance().run();
    if (GameState.isFirst()) {
      SmartDashboard.putString("Code Branch", branch);
      SmartDashboard.putString("Code Commit", commit);
      SmartDashboard.putString("Configuration (radio)", radio);
    }
    GameState.robotPeriodic();
  }

  @Override
  public void disabledInit() {
    GameState.disabledInit();
  }

  @Override
  public void disabledPeriodic() {}

  @Override
  public void autonomousInit() {
    GameState.autonomousInit();
  }

  @Override
  public void autonomousPeriodic() {}

  @Override
  public void teleopInit() {
    GameState.teleopInit();
    // This makes sure that the autonomous stops running when
    // teleop starts running. If you want the autonomous to
    // continue until interrupted by another command, remove
    // this line or comment it out.
    // if (m_autonomousCommand != null) {
    //   m_autonomousCommand.cancel();
    // }
  }

  @Override
  public void teleopPeriodic() {}

  AbsoluteEncoderAlignment m_swerveTests;
  @Override
  public void testInit() {
    GameState.testInit();
    CommandScheduler.getInstance().cancelAll();
  }

  @Override
  public void testPeriodic() {
  }

  @Override
  public void simulationInit() {
    GameState.simulationInit();
  }

  @Override
  public void simulationPeriodic() {}
}
