package team1502.configuration.builders.motors;

import java.util.function.Function;

import com.revrobotics.CANSparkLowLevel;

import team1502.configuration.builders.Builder;
import team1502.configuration.builders.IBuild;
import team1502.configuration.builders.Part;

public class Motor extends Builder {
    public static final String idleMode = "idleMode";
    public static final String motorType = "motorType";
    public static final String freeSpeedRPM = "freeSpeedRPM";

    public static final String NAME = "Motor";
    public static final Function<IBuild, Motor> Define = build->new Motor(build);
    public static Motor Wrap(Builder builder) { return new Motor(builder.getIBuild(), builder.getPart()); }
    public static Motor WrapPart(Builder builder) { return WrapPart(builder, NAME); }
    public static Motor WrapPart(Builder builder, String partName) { return Wrap(builder.getPart(partName)); }
    public Motor(IBuild build) { super(build, NAME); }
    public Motor(IBuild build, Part part) { super(build, part); }

    public CANSparkLowLevel.MotorType MotorType() { return (CANSparkLowLevel.MotorType)getValue(Motor.motorType); }
    public Motor MotorType(CANSparkLowLevel.MotorType motorType) {
        return (Motor)Value(Motor.motorType, motorType);
    }      
    
    /** reported free-speed (revolutions per minute) */
    public double FreeSpeedRPM() { return (double)getValue(Motor.freeSpeedRPM); }          
    public Motor FreeSpeedRPM(double speed) {
        return (Motor)Value(Motor.freeSpeedRPM, speed);
    }          
}
