package frc.robot;

import edu.wpi.first.wpilibj2.command.button.CommandXboxController;
import edu.wpi.first.wpilibj2.command.button.Trigger;
import frc.robot.Constants.OperatorConstants;

public final class Operator {
  public static final CommandXboxController Controller = new CommandXboxController(OperatorConstants.kOperatorControllerPort);
  
  public static double getLeftX() { return Controller.getLeftX();}
  public static double getLeftY() { return Controller.getLeftY();}
  public static double getLeftTrigger() { return Controller.getLeftTriggerAxis();}
  public static Trigger leftTrigger(double threshold) { return Controller.leftTrigger(threshold); }
  
  public static double getRightX() { return Controller.getRightX();}
  public static double getRightY() { return Controller.getRightY();}
  public static double getRightTrigger() { return Controller.getRightTriggerAxis();}
  public static Trigger rightTrigger(double threshold) { return Controller.rightTrigger(threshold); }
  
  // Left side controls
  public static final Trigger LeftStick = Controller.leftStick();
  public static final Trigger LeftBumper = Controller.leftBumper();

  // Right side controls
  public static final Trigger RightStick = Controller.rightStick();
  public static final Trigger RightBumper = Controller.rightBumper();
  
  /*  Logitech 310 Buttons
  __                      _______
    \____________________/        \
    [Back]        [Start]    Y    |
            [Logi]          X + B  |
    [Mode]                   A    |

  */
  
  // Action Buttons
  public static final Trigger A = Controller.a(); 
  public static final Trigger B = Controller.b(); 
  public static final Trigger X = Controller.b(); 
  public static final Trigger Y = Controller.b(); 
  
  // Other buttons
  public static final Trigger Back = Controller.back();
  public static final Trigger Start = Controller.start();
  // "Mode" button swaps the D-pad and left stick
  // "Logitech" button is like Guide or Home

  // Directional Pad
  public static final Trigger North = Controller.povUp();
  public static final Trigger NorthEast = Controller.povUpRight();
  public static final Trigger East = Controller.povRight();
  public static final Trigger SouthEast = Controller.povDownRight();
  public static final Trigger South = Controller.povDown();
  public static final Trigger SouthWest = Controller.povDownLeft();
  public static final Trigger West = Controller.povLeft();
  public static final Trigger NorthWest = Controller.povUpLeft();
}
