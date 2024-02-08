package team1502.configuration.builders.motors;

import java.util.function.Function;

import com.revrobotics.CANSparkBase.IdleMode;
import com.revrobotics.CANSparkMax;

import team1502.configuration.CAN.DeviceType;
import team1502.configuration.CAN.Manufacturer;
import team1502.configuration.builders.Builder;
import team1502.configuration.builders.IBuild;
import team1502.configuration.builders.Part;

public class MotorController extends Builder {
    private static final DeviceType deviceType = DeviceType.MotorController; 
    private static final String isReversed = "isReversed";
    public static final String NAME = "MotorController";
    public static final Function<IBuild, MotorController> Define(Manufacturer manufacturer) {
        return build->new MotorController(build,manufacturer);
    }
    public static MotorController Wrap(Builder builder) { return new MotorController(builder.getIBuild(), builder.getPart()); }
    public static MotorController WrapPart(Builder builder) { return WrapPart(builder, NAME); }
    public static MotorController WrapPart(Builder builder, String partName) { return Wrap(builder.getPart(partName)); }

    // Define
    public MotorController(IBuild build, Manufacturer manufacturer) {
        super(build);
        Device(deviceType); // also "buildType"
        Manufacturer(manufacturer);
    }
    public MotorController(IBuild build, Part part) {
        super(build, part);
    }
    
    public Motor Motor() {return Motor.WrapPart(this, Motor.NAME); }
    public MotorController Motor(String partName) {
        return Motor(partName, null);
    }
    public MotorController Motor(String partName, Function<Motor, Builder> fn) {
        addPart(Motor.Define, Motor.NAME, partName, fn);
        return this;
    }


    public IdleMode IdleMode() { return (IdleMode)getValue(Motor.idleMode); }
    public MotorController IdleMode(IdleMode value) {
        setValue(Motor.idleMode, value);
        return this;
    }
    
    public boolean Reversed() {
        var result = getBoolean(isReversed);
        return result == null ? false : result;
    }
    public MotorController Reversed(boolean value) {
        setValue(isReversed, value);
        return this;
    }

    public GearBox GearBox() { return GearBox.WrapPart(this); }
    public MotorController GearBox(Function<GearBox, Builder> fn) {
        return (MotorController)addPart(GearBox.Define, fn);
    }
    
    public PID PID() { return PID.WrapPart(this); }
    public MotorController PID(double p, double i, double d) {
        return (MotorController)addPart(PID.Define, pid->pid.P(p).I(i).D(d));
    }
    public MotorController PID(double p, double i, double d, double ff) {
        return (MotorController)addPart(PID.Define, pid->pid.P(p).I(i).D(d).FF(ff));
    }

    public CANSparkMax buildSparkMax() {
        var motor = CANSparkMax(new CANSparkMax(CanNumber(), Motor().MotorType()));
        motor.setIdleMode(IdleMode());
        motor.setInverted(Reversed());
        return motor;
    }
    public CANSparkMax CANSparkMax() {
        return (CANSparkMax)Value("CANSparkMax");
    }
    public CANSparkMax CANSparkMax(CANSparkMax motor) {
        Value("CANSparkMax", motor);
        return motor;
    }
}
