// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import frc.robot.subsystems.Arm.ArmSubsystem;
import frc.robot.subsystems.PowerManagement.MockDetector;
import frc.robot.subsystems.ShooterIntake.ShooterIntake;
import frc.robot.commands.ControllerCommands;
import frc.robot.commands.IntakeNote;
import frc.robot.commands.MoveToAmp;
import frc.robot.commands.MoveToShoot;
import frc.robot.commands.ShootNote;
import frc.robot.commands.ShooterIntakeCommands;
import frc.robot.commands.ArmCommands;
import frc.robot.subsystems.SwerveDrive.DriveSubsystem;

import com.pathplanner.lib.auto.AutoBuilder;
import com.pathplanner.lib.auto.NamedCommands;
import com.pathplanner.lib.commands.PathPlannerAuto;

import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.InstantCommand;
import edu.wpi.first.wpilibj2.command.button.CommandXboxController;
import edu.wpi.first.wpilibj2.command.button.Trigger;

/**
 * This class is where the bulk of the robot should be declared. Since Command-based is a
 * "declarative" paradigm, very little robot logic should actually be handled in the {@link Robot}
 * periodic methods (other than the scheduler calls). Instead, the structure of the robot (including
 * subsystems, commands, and trigger mappings) should be declared here.
 */
public class RobotContainer {
  // The robot's subsystems and commands are defined here...
  public final DriveSubsystem driveSubsystem = new DriveSubsystem();
  public final ArmSubsystem armSubsystem = new ArmSubsystem();
  public final ShooterIntake shooterIntakeSubsystem = new ShooterIntake();
  //private final PdpSubsystem pdpSubsystem = new PdpSubsystem();
  
  //Needed to invoke scheduler
  //public final Vision visionSubsystem = new Vision();

  private final SendableChooser<Command> autoChooser; 

  /* sample

  // Replace with CommandPS4Controller or CommandJoystick if needed
  private final CommandXboxController m_driverController =
      new CommandXboxController(OperatorConstants.kDriverControllerPort);
  */

  
  /** The container for the robot. Contains subsystems, OI devices, and commands. */
  public RobotContainer() {
    // Configure the trigger bindings
    configureBindings();

    //Register named commands. Must register all commands we want Pathplanner to execute.
    NamedCommands.registerCommand("Rotate to amp", new MoveToAmp(armSubsystem));
    NamedCommands.registerCommand("Rotate to intake", new InstantCommand(armSubsystem::rotateToIntake));
    NamedCommands.registerCommand("Rotate to close shot", new MoveToShoot(armSubsystem));
    NamedCommands.registerCommand("Rotate to far shot", new InstantCommand(armSubsystem::rotateToShootFar));
    NamedCommands.registerCommand("Rotate to intake", new InstantCommand(armSubsystem::rotateToIntake));
    NamedCommands.registerCommand("Intake on", new IntakeNote(shooterIntakeSubsystem));
    NamedCommands.registerCommand("Intake off", new InstantCommand(shooterIntakeSubsystem::setIntakeOff));
    NamedCommands.registerCommand("Shot Note", new ShootNote(shooterIntakeSubsystem, ()->armSubsystem.isArmAtAmp()));
    
    
    

    //Build an Autochooser from SmartDashboard selection.  Default will be Commands.none()

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
    //Drivetrain
    driveSubsystem.setDefaultCommand(new ControllerCommands(driveSubsystem, new MockDetector())); //USES THE LEFT BUMPER TO SLOW DOWN

    //Arm
    armSubsystem.setDefaultCommand(new ArmCommands(armSubsystem));
    
    Operator.Controller.a().onTrue(new InstantCommand(armSubsystem::rotateToAmpTrap));
    Operator.Controller.b().onTrue(new InstantCommand(armSubsystem::rotateToShootFar));
    Operator.Controller.y().onTrue(new InstantCommand(armSubsystem::rotateToShootClose));
    Operator.Controller.x().onTrue(new InstantCommand(armSubsystem::rotateToIntake));
    Operator.Controller.start().onTrue(new InstantCommand(armSubsystem::rotateToStart));

    //ShooterIntake
    shooterIntakeSubsystem.setDefaultCommand(new ShooterIntakeCommands(shooterIntakeSubsystem));

    Operator.Controller.rightTrigger(0.5).onTrue(new ShootNote(shooterIntakeSubsystem, ()->armSubsystem.isArmAtAmp()));
    Operator.Controller.rightBumper().toggleOnTrue(new InstantCommand(shooterIntakeSubsystem::toggleShooter));

    Operator.Controller.leftTrigger(.5).whileTrue(new IntakeNote(shooterIntakeSubsystem));

    Operator.Controller.leftBumper().onTrue(new InstantCommand(shooterIntakeSubsystem::setIntakeEject));
    Operator.Controller.leftBumper().onFalse(new InstantCommand(shooterIntakeSubsystem::setIntakeOff));

    


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
    return autoChooser.getSelected();
  }
}
