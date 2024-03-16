package frc.testmode.swerve;

import com.ctre.phoenix6.configs.MagnetSensorConfigs;
import com.ctre.phoenix6.hardware.CANcoder;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.CommandScheduler;
import edu.wpi.first.wpilibj2.command.FunctionalCommand;
import edu.wpi.first.wpilibj2.command.InstantCommand;
import edu.wpi.first.wpilibj2.command.RunCommand;
import edu.wpi.first.wpilibj2.command.Subsystem;

final class CANCoders {
    public static final CANcoder FRONT_LEFT_CAN_CODER = new CANcoder(16);
    public static final CANcoder FRONT_RIGHT_CAN_CODER = new CANcoder(10);
    public static final CANcoder BACK_LEFT_CAN_CODER = new CANcoder(4);
    public static final CANcoder BACK_RIGHT_CAN_CODER = new CANcoder(8);
}
  
public class AbsoluteEncoderAlignment implements Subsystem {
    public static Command StartAlignmentCommand() {
        var alignment = new AbsoluteEncoderAlignment();
        var command = new FunctionalCommand(
            ()->alignment.init(), //AbsoluteEncoderAlignment.StartAlignment());
            ()->alignment.execute(),
            interrupted -> alignment.finish(),
            ()->alignment.isDone(),
            alignment
        );
        return command;
    } 
    public static void StartAlignment() {
        SmartDashboard.putData("Start Alignment", StartAlignmentCommand());
    }
    private AbsoluteEncoderAlignment() {
    }
    
    void init() {
        zeroPositions();
        //register();
        SmartDashboard.putData("Stop Alignment", new InstantCommand(()->done()));
    }
    void execute() {
        updateDashboard();
    }

    @Override
    public void periodic() {
        updateDashboard();
    }

    void updateDashboard(){
        SmartDashboard.putNumber("16", CANCoders.FRONT_LEFT_CAN_CODER.getAbsolutePosition().getValue());
        SmartDashboard.putNumber("10", CANCoders.FRONT_RIGHT_CAN_CODER.getAbsolutePosition().getValue());
        SmartDashboard.putNumber("4", CANCoders.BACK_LEFT_CAN_CODER.getAbsolutePosition().getValue());
        SmartDashboard.putNumber("8", CANCoders.BACK_RIGHT_CAN_CODER.getAbsolutePosition().getValue());
    }
    
    void zeroPositions(){
        //Reset magnet configs to factory defaults
        CANCoders.FRONT_LEFT_CAN_CODER.getConfigurator().apply(new MagnetSensorConfigs());
        CANCoders.FRONT_RIGHT_CAN_CODER.getConfigurator().apply(new MagnetSensorConfigs());
        CANCoders.BACK_LEFT_CAN_CODER.getConfigurator().apply(new MagnetSensorConfigs());
        CANCoders.BACK_RIGHT_CAN_CODER.getConfigurator().apply(new MagnetSensorConfigs());
    }
    void finish() {
        CommandScheduler.getInstance().unregisterSubsystem(this);
        _done = true;
    }

    boolean _done;
    void done() {
        _done = true;
    }
    boolean isDone() {
        return _done;
    }
}
