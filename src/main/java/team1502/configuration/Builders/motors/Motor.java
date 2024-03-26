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
    public static final String stallTorque = "stallTorque";

    public static final String CLASSNAME = "Motor";
    public static final Function<IBuild, Motor> Define = build->new Motor(build);
    public static Motor Wrap(Builder builder) { return builder == null ? null : new Motor(builder.getIBuild(), builder.getPart()); }
    public static Motor WrapPart(Builder builder) { return WrapPart(builder, CLASSNAME); }
    public static Motor WrapPart(Builder builder, String partName) { return Wrap(builder.getPart(partName)); }
    public Motor(IBuild build) {
         super(build, CLASSNAME);
         addConnector(POWER, "Vin").FriendlyName("Power connector");

    }
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
    public double StallTorque() { return (double)getValue(Motor.stallTorque); }          
    public Motor StallTorque(double speed) {
        return (Motor)Value(Motor.stallTorque, speed);
    }          
}
