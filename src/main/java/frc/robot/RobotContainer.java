// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import team1502.configuration.configurations.RobotConfigurations;
import team1502.configuration.factory.RobotConfiguration;
import team1502.injection.RobotFactory;
import edu.wpi.first.wpilibj2.command.Command;

public class RobotContainer {
  Command defaultAuto = null;

  public RobotContainer(String radio) {
    var config = RobotConfigurations.getConfiguration(radio);
    RobotFactory.Create(config);
    configureBindings(config);
  }
  
  
  private void configureBindings(RobotConfiguration config) {
    //defaultAuto = config.getDefaultAutoCommand();
    // frc.robot.subsystems.SwerveDrive.DriveSubsystem driveSubsystem = null;
    // driveSubsystem.setDefaultCommand(new ControllerCommands(driveSubsystem, new MockDetector())); //USES THE LEFT BUMPER TO SLOW DOWN
  }

  /**
   * Use this to pass the autonomous command to the main {@link Robot} class.
   *
   * @return the command to run in autonomous
   */
  public Command getAutonomousCommand() {
    // An example command will be run in autonomous
    return defaultAuto;
  }
}
