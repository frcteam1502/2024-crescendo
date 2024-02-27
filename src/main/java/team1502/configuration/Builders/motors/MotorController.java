package team1502.configuration.builders.motors;

import java.util.function.Function;

import com.revrobotics.CANSparkBase.IdleMode;

import com.revrobotics.CANSparkMax;
import com.revrobotics.RelativeEncoder;
import com.revrobotics.SparkPIDController;

import team1502.configuration.CAN.CanInfo;
import team1502.configuration.CAN.DeviceType;
import team1502.configuration.CAN.Manufacturer;
import team1502.configuration.builders.Builder;
import team1502.configuration.builders.IBuild;
import team1502.configuration.builders.Part;

public class MotorController extends Builder {
    private static final DeviceType deviceType = DeviceType.MotorController; 
    private static final String isReversed = "isReversed";
    private static final String closedLoopRampRate = "closedLoopRampRate";
    private static final String smartCurrentLimit = "smartCurrentLimit";
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
        CanInfo.addConnector(this, deviceType, manufacturer);
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
    
    public MotorController Follower() { return WrapPart(this, "Follower"); }
    public MotorController Follower(String partName, Function<MotorController, Builder> fn) {
        addPart(MotorController.Define(Manufacturer.REVRobotics), "Follower", partName, fn);
        return this;
    }


    public IdleMode IdleMode() { return (IdleMode)getValue(Motor.idleMode); }
    public MotorController IdleMode(IdleMode value) {
        setValue(Motor.idleMode, value);
        return this;
    }
    
    public boolean IsReversed() {
        var result = getBoolean(isReversed);
        return result == null ? false : result;
    }
    
    /** invert the direction of a speed controller */
    public MotorController Reversed() { return Reversed(true); }
    public MotorController Reversed(boolean value) {
        setValue(isReversed, value);
        return this;
    }

    public GearBox GearBox() { return GearBox.WrapPart(this); }
    public MotorController GearBox(Function<GearBox, Builder> fn) {
        return (MotorController)AddPart(GearBox.Define, fn);
    }
    
    public PID PID() { return PID.WrapPart(this); }
    public MotorController PID(double p, double i, double d) {
        return (MotorController)AddPart(PID.Define, pid->pid.P(p).I(i).D(d));
    }
    public MotorController PID(double p, double i, double d, double ff) {
        return (MotorController)AddPart(PID.Define, pid->pid.P(p).I(i).D(d).FF(ff));
    }

    /** Time in seconds to go from 0 to full throttle. */
    public Double ClosedLoopRampRate() { return getDouble(closedLoopRampRate); }
    public MotorController ClosedLoopRampRate(double rate) {
        Value(closedLoopRampRate, rate);
        return this;
    }
    /** The current limit in Amps. */
    public Integer SmartCurrentLimit() { return getInt(smartCurrentLimit); }
    public MotorController SmartCurrentLimit(Integer limit) {
        Value(smartCurrentLimit, limit);
        return this;
    }

    public SparkPIDController createPIDController() {
        return PID().setPIDController(CANSparkMax());
    }

    public CANSparkMax buildSparkMax() {
        var motor = CANSparkMax(new CANSparkMax(CanNumber(), Motor().MotorType()));
        motor.setIdleMode(IdleMode());
        motor.setInverted(IsReversed());
        if (hasValue(closedLoopRampRate)) {
            motor.setClosedLoopRampRate(ClosedLoopRampRate());
        }
        if (hasValue(smartCurrentLimit)) {
            motor.setSmartCurrentLimit(SmartCurrentLimit());
        }
        if (hasValue("Follower")) {
            var follower = Follower();
            var followerMotor = follower.buildSparkMax();
            followerMotor.follow(motor, follower.IsReversed());
        }
        return motor;
    }
    public CANSparkMax CANSparkMax() {
        return (CANSparkMax)Value("CANSparkMax");
    }
    public CANSparkMax CANSparkMax(CANSparkMax motor) {
        Value("CANSparkMax", motor);
        return motor;
    }

    public RelativeEncoder getRelativeEncoder() {
        return CANSparkMax().getEncoder();
    }

    public RelativeEncoder buildRelativeEncoder() {
        var encoder = getRelativeEncoder();
        if (parent != null) {
            if (parent.Value(Builder.BUILD_TYPE) == SwerveModule.NAME) {
                encoder.setPositionConversionFactor(((SwerveModule)parent).getPositionConversionFactor());
                encoder.setVelocityConversionFactor(((SwerveModule)parent).getVelocityConversionFactor());
            }
        }
        return encoder;
    }

    public Double getPositionConversionFactor() {
        if (parent != null) {
            if (parent.Value(Builder.BUILD_TYPE) == SwerveModule.NAME) {
                return ((SwerveModule)parent).getPositionConversionFactor();
            }
        }
        return null;
    }

    public Double getVelocityConversionFactor() {
        if (parent != null) {
            if (parent.Value(Builder.BUILD_TYPE) == SwerveModule.NAME) {
                return ((SwerveModule)parent).getVelocityConversionFactor();
            }
        }
        return null;
    }
}
