package frc.robot.controller;

import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.wpilibj.XboxController.Button;
import edu.wpi.first.wpilibj2.command.button.CommandXboxController;

public class Controller {

    public class StateButton {
        private int button;
        public StateButton(int button) { this.button = button; }
        /** true after the 'press' event  */
        public boolean Press() { return m_hid.getRawButton(button); }
        /** the button is currently pressed */
        public boolean Pressed() { return m_hid.getRawButtonPressed(button); }
        /** the button has been released */
        public boolean Release() { return m_hid.getRawButtonReleased(button); }
    }

    public class POVDirection {
        public boolean Forward() {return m_hid.getPOV() == 0; }
        public boolean ForwardRight() {return m_hid.getPOV() == 45; }
        public boolean Right() {return m_hid.getPOV() == 90; }
        public boolean ReverseRight() {return m_hid.getPOV() == 135; }
        public boolean Reverse() {return m_hid.getPOV() == 180; }
        public boolean ReversLeft() {return m_hid.getPOV() == 225; }
        public boolean Left() {return m_hid.getPOV() == 270; }
        public boolean ForwardLeft() {return m_hid.getPOV() == 315; }
    }
    
    CommandXboxController Command;
    XboxController m_hid;
    
    public Controller(int port) {
        Command = new CommandXboxController(port);
        m_hid = Command.getHID();
    }

    public final StateButton LeftStick = new StateButton(Button.kLeftStick.value);
    public final StateButton LeftBumper = new StateButton(Button.kLeftBumper.value);
    public final StateButton RightStick = new StateButton(Button.kRightStick.value);
    public final StateButton RightBumper = new StateButton(Button.kRightBumper.value);

    public final StateButton A = new StateButton(Button.kA.value);
    public final StateButton B = new StateButton(Button.kB.value);
    public final StateButton X = new StateButton(Button.kX.value);
    public final StateButton Y = new StateButton(Button.kY.value);

    public final StateButton Back = new StateButton(Button.kBack.value);
    public final StateButton Start = new StateButton(Button.kStart.value);

    public final POVDirection POVDirection = new POVDirection();
    
}
