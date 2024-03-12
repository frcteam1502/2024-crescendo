package frc.robot.controller;

import frc.robot.Constants.OperatorConstants;
import frc.robot.controller.Controller.POVDirection;
import frc.robot.controller.Controller.StateButton;
import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.wpilibj2.command.button.CommandXboxController;
import edu.wpi.first.wpilibj2.command.button.Trigger;

public class Driver {
  static final Controller m_controller = new Controller(OperatorConstants.kDriverControllerPort);
  static final CommandXboxController Command = m_controller.Command;
  static final XboxController m_hid = Command.getHID();

  // READ THESE FOR IMMEDIATE VALUE
  
  public static double getLeftX() { return m_hid.getLeftX();}
  public static double getLeftY() { return m_hid.getLeftY();}
  public static double getLeftTrigger() { return m_hid.getLeftTriggerAxis();}
  
  public static double getRightX() { return m_hid.getRightX();}
  public static double getRightY() { return m_hid.getRightY();}
  public static double getRightTrigger() { return m_hid.getRightTriggerAxis();}

  // Action Buttons
  public static StateButton A = m_controller.A;
  public static StateButton B = m_controller.B;
  public static StateButton X = m_controller.X;
  public static StateButton Y = m_controller.Y;

  public static StateButton LeftStick = m_controller.LeftStick;
  public static StateButton RightStick = m_controller.RightStick;
  public static StateButton LeftBumper = m_controller.LeftBumper;
  public static StateButton RightBumper = m_controller.RightBumper;

  // Directional Pad
  public static POVDirection POV = m_controller.POVDirection;

  // MAKE COMMANDS FROM THESE

  public static Trigger A() { return Command.a(); }
  public static Trigger B() { return Command.b(); }
  public static Trigger X() { return Command.x(); }
  public static Trigger Y() { return Command.y(); }

  public static Trigger Back() { return Command.back(); }
  public static Trigger Start() { return Command.start(); }
  
  /*  Logitech 310 Buttons
  __                      _______
    \____________________/        \
    [Back]        [Start]    Y    |
            [Logi]          X + B  |
    [Mode]                   A    |


    "Mode" button swaps the D-pad and left stick
    "Logitech" button is like Guide or Home

  */

}