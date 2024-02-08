// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import frc.robot.subsystems.PowerManagement.MockDetector;
import frc.robot.commands.Autos;
import frc.robot.commands.ControllerCommands;
import frc.robot.subsystems.SwerveDrive.DriveSubsystem;
import frc.robot.subsystems.ExampleSubsystem;

import edu.wpi.first.wpilibj.PneumaticHub;
import edu.wpi.first.wpilibj.PowerDistribution;
import edu.wpi.first.wpilibj.PowerDistribution.ModuleType;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.button.CommandXboxController;
import edu.wpi.first.wpilibj2.command.button.Trigger;

import team1502.configuration.configurations.RobotConfigurations;

/**
 * This class is where the bulk of the robot should be declared. Since Command-based is a
 * "declarative" paradigm, very little robot logic should actually be handled in the {@link Robot}
 * periodic methods (other than the scheduler calls). Instead, the structure of the robot (including
 * subsystems, commands, and trigger mappings) should be declared here.
 */
public class RobotContainer {
  private final Logger logger = new Logger();

  // The robot's subsystems and commands are defined here...
  public final DriveSubsystem driveSubsystem;
  //private final PdpSubsystem pdpSubsystem = new PdpSubsystem();
  
  private final ExampleSubsystem m_exampleSubsystem = new ExampleSubsystem();
  /* sample

  // Replace with CommandPS4Controller or CommandJoystick if needed
  private final CommandXboxController m_driverController =
      new CommandXboxController(OperatorConstants.kDriverControllerPort);
  */
  
  /** The container for the robot. Contains subsystems, OI devices, and commands. */
  public RobotContainer(String radio) {
    var config = RobotConfigurations.getConfiguration(radio);
    driveSubsystem = new DriveSubsystem(config);

    // Configure the trigger bindings
    configureBindings();

    Logger.RegisterPdp(new PowerDistribution(1, ModuleType.kRev), config.PDH().ChannelNames());
    Logger.RegisterPneumaticHub(new PneumaticHub(), config.PCM().ChannelNames());
    logger.start();

  }

  /**
   * Use this method to define your trigger->command mappings. Triggers can be created via the
   * {@link Trigger#Trigger(java.util.function.BooleanSupplier)} constructor with an arbitrary
   * predicate, or via the named factories in {@link
   * edu.wpi.first.wpilibj2.command.button.CommandGenericHID}'s subclasses for {@link
   * CommandXboxController Xbox}/{@link edu.wpi.first.wpilibj2.command.button.CommandPS4Controller
   * PS4} controllers or {@link edu.wpi.first.wpilibj2.command.button.CommandJoystick Flight
   * joysticks}.
   */
  private void configureBindings() {
    driveSubsystem.setDefaultCommand(new ControllerCommands(driveSubsystem, new MockDetector())); //USES THE LEFT BUMPER TO SLOW DOWN

    /* sample code
    // Schedule `ExampleCommand` when `exampleCondition` changes to `true`
    new Trigger(m_exampleSubsystem::exampleCondition)
        .onTrue(new ExampleCommand(m_exampleSubsystem));

    // Schedule `exampleMethodCommand` when the Xbox controller's B button is pressed,
    // cancelling on release.
    m_driverController.b().whileTrue(m_exampleSubsystem.exampleMethodCommand());
    */
  }

  /**
   * Use this to pass the autonomous command to the main {@link Robot} class.
   *
   * @return the command to run in autonomous
   */
  public Command getAutonomousCommand() {
    // An example command will be run in autonomous
    return Autos.exampleAuto(m_exampleSubsystem);
  }
}
