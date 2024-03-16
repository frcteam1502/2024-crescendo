// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import edu.wpi.first.cameraserver.CameraServer;
import edu.wpi.first.wpilibj.PneumaticHub;
import edu.wpi.first.wpilibj.PowerDistribution;
import edu.wpi.first.wpilibj.PowerDistribution.ModuleType;
import edu.wpi.first.wpilibj.RobotController;
import frc.robot.subsystems.Arm.ArmSubsystem;
import frc.robot.subsystems.PowerManagement.MockDetector;
import frc.robot.subsystems.ShooterIntake.ShooterIntake;
import frc.robot.commands.ControllerCommands;
import frc.robot.commands.IntakeNote;
import frc.robot.commands.MoveToAmp;
import frc.robot.commands.MoveToFarShot;
import frc.robot.commands.MoveToIntake;
import frc.robot.commands.MoveToShoot;
import frc.robot.commands.ShootNote;
import frc.robot.commands.ShooterIntakeCommands;
import frc.robot.controller.Operator;
import frc.robot.commands.AlignToSpeaker;
import frc.robot.commands.ArmCommands;
import frc.robot.subsystems.SwerveDrive.DriveSubsystem;

import com.pathplanner.lib.auto.AutoBuilder;
import com.pathplanner.lib.commands.PathPlannerAuto;

import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.InstantCommand;
import edu.wpi.first.wpilibj2.command.button.CommandXboxController;
import edu.wpi.first.wpilibj2.command.button.Trigger;

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
    var factory = RobotFactory.Create(config);
    
    if (!config.isDisabled("CameraServer")) {
      CameraServer.startAutomaticCapture();
    }
    
    RobotController.setBrownoutVoltage(config.getDouble("brownoutVoltage", 6.75));

    configureBindings(factory);
    
    if (!config.isDisabled("Logger")) {
      Logger.RegisterPdp(new PowerDistribution(1, ModuleType.kRev), config.PDH().ChannelNamesAbbr());
      Logger.RegisterPneumaticHub(new PneumaticHub(), config.PCM().ChannelNamesAbbr());
      logger.start();
    }
    // Path planner NamedCommands are currently in their respective Command class

    // Build an Autochooser from SmartDashboard selection.  Default will be Commands.none()
    new PathPlannerAuto("MiddleAutoAMPFinal");
    new PathPlannerAuto("LeftAuto-AMPFinal");
    new PathPlannerAuto("RightAuto-AMPFinal");
    new PathPlannerAuto("4NoteLeft");
    new PathPlannerAuto("4NoteMiddle");
    new PathPlannerAuto("4NoteRight");
    new PathPlannerAuto("1NoteMiddle");
    new PathPlannerAuto("1NoteLeft");
    new PathPlannerAuto("1NoteRight");
    new PathPlannerAuto("Leave");


    autoChooser = AutoBuilder.buildAutoChooser();
    SmartDashboard.putData("Auto Chooser", autoChooser);
  }

  private void configureBindings(RobotFactory factory) {
    // can get subsystems, etc from factory for e.g., bindings

    // NOTE: Add Command bindings in the respective Command class

    // Drivetrain

    // Arm

    // ShooterIntake

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
