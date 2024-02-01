package frc.robot;

import frc.robot.Constants.OperatorConstants;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.wpilibj2.command.button.JoystickButton;
import edu.wpi.first.wpilibj2.command.button.POVButton;

public final class Driver {
  //Comment out LogitechF310 if using XBox controller and vice versa
  //Logitech F310
  public static final Joystick CONTROLLER = new Joystick(OperatorConstants.kDriverControllerPort);
  public static double getLeftX() { return CONTROLLER.getRawAxis(LogitechF310.LEFT_X); }
  public static double getLeftY() { return CONTROLLER.getRawAxis(LogitechF310.LEFT_Y); }
  public static double getLeftTrigger() { return CONTROLLER.getRawAxis(LogitechF310.LEFT_TRIGGER); }
  
  public static double getRightX() { return CONTROLLER.getRawAxis(LogitechF310.RIGHT_X); }
  public static double getRightY() { return CONTROLLER.getRawAxis(LogitechF310.RIGHT_Y);}
  public static double getRightTrigger() { return CONTROLLER.getRawAxis(LogitechF310.RIGHT_TRIGGER);}

  public static boolean getLeftBumper() { return CONTROLLER.getRawButton(LogitechF310.LEFT_BUMPER);}
  
  /*//Xbox Controller
  public static final XboxController CONTROLLER = new XboxController(OperatorConstants.kDriverControllerPort);

  public static double getLeftX() { return CONTROLLER.getLeftX();}
  public static double getLeftY() { return CONTROLLER.getRightX();}
  public static double getLeftTrigger() { return CONTROLLER.getRawAxis(0);}
  
  public static double getRightX() { return CONTROLLER.getRightX();}
  public static double getRightY() { return CONTROLLER.getRightY();}
  public static double getRightTrigger() { return CONTROLLER.getRightTriggerAxis();}*/

  public static final class Buttons {
    //Comment out LogitechF310 if using XBox controller and vice versa
    //Logitech F310
    // Left side controls
    public static final JoystickButton LEFT_STICK  = new JoystickButton(CONTROLLER, LogitechF310.LEFT_STICK);
    public static final JoystickButton LEFT_BUMPER = new JoystickButton(CONTROLLER, LogitechF310.LEFT_BUMPER); 


    // Right side controls
    public static final JoystickButton RIGHT_STICK  = new JoystickButton(CONTROLLER, LogitechF310.RIGHT_STICK);
    public static final JoystickButton RIGHT_BUMPER = new JoystickButton(CONTROLLER, LogitechF310.RIGHT_BUMPER); 
    
    // Action Buttons
    public static final JoystickButton A = new JoystickButton(CONTROLLER, LogitechF310.A_BUTTON); 
    public static final JoystickButton B = new JoystickButton(CONTROLLER, LogitechF310.B_BUTTON); 
    public static final JoystickButton X = new JoystickButton(CONTROLLER, LogitechF310.X_BUTTON); 
    public static final JoystickButton Y = new JoystickButton(CONTROLLER, LogitechF310.Y_BUTTON); 
    
    // Other buttons
    public static final JoystickButton BACK  = new JoystickButton(CONTROLLER, LogitechF310.BACK);
    public static final JoystickButton START = new JoystickButton(CONTROLLER, LogitechF310.START);

    /*//Xbox
    // Left side controls
    public static final JoystickButton LEFT_STICK  = new JoystickButton(CONTROLLER,XboxController.Button.kLeftStick.value);
    public static final JoystickButton LEFT_BUMPER = new JoystickButton(CONTROLLER, XboxController.Button.kLeftBumper.value); 


    // Right side controls
    public static final JoystickButton RIGHT_STICK  = new JoystickButton(CONTROLLER, XboxController.Button.kRightStick.value);
    public static final JoystickButton RIGHT_BUMPER = new JoystickButton(CONTROLLER, XboxController.Button.kRightBumper.value); 
    

    
    // Action Buttons
    public static final JoystickButton A = new JoystickButton(CONTROLLER, XboxController.Button.kA.value); 
    public static final JoystickButton B = new JoystickButton(CONTROLLER, XboxController.Button.kB.value); 
    public static final JoystickButton X = new JoystickButton(CONTROLLER, XboxController.Button.kX.value); 
    public static final JoystickButton Y = new JoystickButton(CONTROLLER, XboxController.Button.kY.value); 
    
    // Other buttons
    public static final JoystickButton BACK  = new JoystickButton(CONTROLLER, XboxController.Button.kBack.value);
    public static final JoystickButton START = new JoystickButton(CONTROLLER, XboxController.Button.kStart.value);
    // "Mode" button swaps the D-pad and left stick
    // "Logitech" button is like Guide or Home*/

    // Directional Pad
    public static final POVButton NORTH     = new POVButton(CONTROLLER, 0);
    public static final POVButton NORTHEAST = new POVButton(CONTROLLER, 45);
    public static final POVButton EAST      = new POVButton(CONTROLLER, 90);
    public static final POVButton SOUTHEAST = new POVButton(CONTROLLER, 135);
    public static final POVButton SOUTH     = new POVButton(CONTROLLER, 180);
    public static final POVButton SOUTHWEST = new POVButton(CONTROLLER, 225);
    public static final POVButton WEST      = new POVButton(CONTROLLER, 270);
    public static final POVButton NORTHWEST = new POVButton(CONTROLLER, 315);
  }
 

  public final class LogitechF310 {
    //Button mapping
    public static final int LEFT_X = 0;
    public static final int LEFT_Y = 1;
    public static final int RIGHT_X = 4;
    public static final int RIGHT_Y= 5;

    public static final int X_BUTTON = 3;
    public static final int A_BUTTON = 1;
    public static final int B_BUTTON = 2;
    public static final int Y_BUTTON = 4;
    
    public static final int LEFT_TRIGGER = 2;
    public static final int RIGHT_TRIGGER = 3;

    public static final int LEFT_BUMPER = 5;
    public static final int RIGHT_BUMPER = 6;

    public static final int BACK = 7;
    public static final int START = 8;

    public static final int LEFT_STICK = 9;
    public static final int RIGHT_STICK = 10;
  }
}