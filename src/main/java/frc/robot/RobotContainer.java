// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import edu.wpi.first.wpilibj.PneumaticHub;
import edu.wpi.first.wpilibj.PowerDistribution;
import edu.wpi.first.wpilibj.PowerDistribution.ModuleType;
import com.pathplanner.lib.auto.AutoBuilder;
import com.pathplanner.lib.auto.NamedCommands;
import com.pathplanner.lib.commands.PathPlannerAuto;

import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.Command;

import team1502.configuration.configurations.RobotConfigurations;
import team1502.configuration.factory.RobotConfiguration;
import team1502.injection.RobotFactory;

/**
 * This class is where the bulk of the robot should be declared. Since Command-based is a
 * "declarative" paradigm, very little robot logic should actually be handled in the {@link Robot}
 * periodic methods (other than the scheduler calls). Instead, the structure of the robot (including
 * subsystems, commands, and trigger mappings) should be declared here.
 */
public class RobotContainer {
  private final Logger logger = new Logger();

  private final SendableChooser<Command> autoChooser; 
  
  /** The container for the robot. Contains subsystems, OI devices, and commands. */
  public RobotContainer(String radio) {
    var config = RobotConfigurations.getConfiguration(radio);
    RobotFactory.Create(config);

    configureBindings(config);

    Logger.RegisterPdp(new PowerDistribution(1, ModuleType.kRev), config.PDH().ChannelNames());
    Logger.RegisterPneumaticHub(new PneumaticHub(), config.PCM().ChannelNames());
    logger.start();

    //TODO: Register named commands. Must register all commands we want Pathplanner to execute.
    // NamedCommands.registerCommand("Dummy Command 1", new InstantCommand(driveSubsystem::dummyAction1));
    // NamedCommands.registerCommand("Dummy Command 2", new InstantCommand(driveSubsystem::dummyAction2));

    //Build an Autochooser from SmartDashboard selection.  Default will be Commands.none()

    new PathPlannerAuto("MiddleAutoAMPFinal");
    new PathPlannerAuto("LeftAuto-AMPFinal");
    new PathPlannerAuto("RightAuto-AMPFinal");

    autoChooser = AutoBuilder.buildAutoChooser();
    SmartDashboard.putData("Auto Chooser", autoChooser);
  }

  private void configureBindings(RobotConfiguration config) {
  }

  /**
   * Use this to pass the autonomous command to the main {@link Robot} class.
   *
   * @return the command to run in autonomous
   */
  public Command getAutonomousCommand() {
    // An example command will be run in autonomous
    return autoChooser.getSelected();
  }
}
