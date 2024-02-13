// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import frc.robot.Logger;
import edu.wpi.first.wpilibj.PneumaticHub;
import edu.wpi.first.wpilibj.PowerDistribution;
import edu.wpi.first.wpilibj.PowerDistribution.ModuleType;
import edu.wpi.first.wpilibj.RobotController;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.CommandScheduler;
import frc.testmode.swerve.AbsoluteEncoderAlignment;

/**
 * The VM is configured to automatically run this class, and to call the functions corresponding to
 * each mode, as described in the TimedRobot documentation. If you change the name of this class or
 * the package after creating this project, you must also update the build.gradle file in the
 * project.
 */
public class Robot extends TimedRobot {
  private Command m_autonomousCommand;
  private RobotContainer m_robotContainer;

  private Logger logger = new Logger();

  private String[] pdhRealChannelNames = {
    null,       //"0"
    null,       //"1"
    null,       //"2"
    "RL Drive", //"3"
    "RL Turn",  //"4"  
    null,       //"5"
    null,       //"6"
    null,       //"7"
    "RR Turn",  //"8"
    "RR Drive", //"9"
    "FR Turn",  //"10"
    "FR Drive", //"11"
    null,       //"12"
    null,       //"13"
    null,       //"14"
    "FL Drive", //"15"
    "FL Turn",  //"16"
    null,       //"17"
    null,       //"18"
    null,       //"19"
    null,       //"20"
    null,       //"21"
    null,       //"22"
    null,       //"23"
};

private String[] pneumaticNames = {
  null, //"0",
  null, //"1",
  null, //"2"
  null, //"3"
  null, //"4",
  null, //"5",
  null, //"6",
  null, //"7",
  null, //"8",
  null, //"9",
  null, //"10",
  null, //"11",
  null, //"12",
  null, //"13",
  null, //"14",
  null, //"15",
};

  /**
   * This function is run when the robot is first started up and should be used for any
   * initialization code.
   */
  @Override
  public void robotInit() {
    RobotController.setBrownoutVoltage(3);
    //Register PDP and PH Logger items
    
    // Instantiate our RobotContainer.  This will perform all our button bindings, and put our
    // autonomous chooser on the dashboard.
    m_robotContainer = new RobotContainer();

    //Register Logger items
    //Logger.RegisterLoopTimes(this);
    Logger.RegisterPdp(new PowerDistribution(1, ModuleType.kRev), pdhRealChannelNames);
    Logger.RegisterPneumaticHub(new PneumaticHub(), pneumaticNames);
    logger.start();
    

    
  }

  /**
   * This function is called every 20 ms, no matter the mode. Use this for items like diagnostics
   * that you want ran during disabled, autonomous, teleoperated and test.
   *
   * <p>This runs after the mode specific periodic functions, but before LiveWindow and
   * SmartDashboard integrated updating.
   */
  @Override
  public void robotPeriodic() {
    // Runs the Scheduler.  This is responsible for polling buttons, adding newly-scheduled
    // commands, running already-scheduled commands, removing finished or interrupted commands,
    // and running subsystem periodic() methods.  This must be called from the robot's periodic
    // block in order for anything in the Command-based framework to work.
    CommandScheduler.getInstance().run();
    GameState.robotPeriodic();
  }

  /** This function is called once each time the robot enters Disabled mode. */
  @Override
  public void disabledInit() {
    GameState.disabledInit();
  }

  @Override
  public void disabledPeriodic() {}

  /** This autonomous runs the autonomous command selected by your {@link RobotContainer} class. */
  @Override
  public void autonomousInit() {
    GameState.autonomousInit();
    m_autonomousCommand = m_robotContainer.getAutonomousCommand();

    // schedule the autonomous command (example)
    if (m_autonomousCommand != null) {
      m_autonomousCommand.schedule();
    }
  }

  /** This function is called periodically during autonomous. */
  @Override
  public void autonomousPeriodic() {}

  @Override
  public void teleopInit() {
    GameState.teleopInit();
    // This makes sure that the autonomous stops running when
    // teleop starts running. If you want the autonomous to
    // continue until interrupted by another command, remove
    // this line or comment it out.
    if (m_autonomousCommand != null) {
      m_autonomousCommand.cancel();
    }
  }

  /** This function is called periodically during operator control. */
  @Override
  public void teleopPeriodic() {}

  AbsoluteEncoderAlignment m_swerveTests;
  @Override
  public void testInit() {
    GameState.testInit();
    // Cancels all running commands at the start of test mode.
    CommandScheduler.getInstance().cancelAll();
    m_swerveTests = new AbsoluteEncoderAlignment();
    m_swerveTests.testInit();
  }

  /** This function is called periodically during test mode. */
  @Override
  public void testPeriodic() {
    m_swerveTests.testPeriodic();
  }

  /** This function is called once when the robot is first started up. */
  @Override
  public void simulationInit() {
    GameState.simulationInit();
  }

  /** This function is called periodically whilst in simulation. */
  @Override
  public void simulationPeriodic() {}
}
