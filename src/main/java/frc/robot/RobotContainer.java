// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import edu.wpi.first.wpilibj.PneumaticHub;
import edu.wpi.first.wpilibj.PowerDistribution;
import edu.wpi.first.wpilibj.PowerDistribution.ModuleType;

import com.pathplanner.lib.auto.AutoBuilder;
import com.pathplanner.lib.commands.PathPlannerAuto;

import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.commands.ControllerCommands;
import frc.robot.subsystems.PowerManagement.MockDetector;
import frc.robot.subsystems.SwerveDrive.DriveSubsystem;

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
  
  private final RobotConfiguration config;
  public RobotConfiguration getConfig() {return config; }
  private final RobotFactory factory;
  public RobotFactory getFactory() {return factory; }

  /** The container for the robot. Contains subsystems, IO devices, and commands. */
  public RobotContainer(String radio) {
    config = RobotConfigurations.getConfiguration(radio);
    factory = RobotFactory.Create(config);
    
    configureBindings(factory);

    if (!config.isDisabled("Logger")) {
      Logger.RegisterPdp(new PowerDistribution(1, ModuleType.kRev), config.PDH().ChannelNames());
      Logger.RegisterPneumaticHub(new PneumaticHub(), config.PCM().ChannelNames());
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


    autoChooser = AutoBuilder.buildAutoChooser();
    SmartDashboard.putData("Auto Chooser", autoChooser);
  }

  private void configureBindings(RobotFactory factory) {
    // can get subsystems, etc from factory for e.g., bindings
    DriveSubsystem driveSubsystem = factory.getInstance(DriveSubsystem.class);
    if (driveSubsystem == null) { //BACKUP METHOD if factory failed on roboRIO
      driveSubsystem = doFactoryBackupMethod(factory.getRobotConfiguration());
    }

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

  private DriveSubsystem doFactoryBackupMethod(RobotConfiguration config) {
    System.err.println("ROBOT FACTORY FAILED");
    DriveSubsystem driveSubsystem = new DriveSubsystem(config);
    driveSubsystem.setDefaultCommand(new ControllerCommands(config, driveSubsystem, new MockDetector()));
    if (!config.isDisabled("frc.robot.subsystems.Arm.ArmSubsystem")) {
      var armSubsystem = new frc.robot.subsystems.Arm.ArmSubsystem(config);
      armSubsystem.setDefaultCommand(new frc.robot.commands.ArmCommands(armSubsystem));
    }
    return driveSubsystem;
  }
}
