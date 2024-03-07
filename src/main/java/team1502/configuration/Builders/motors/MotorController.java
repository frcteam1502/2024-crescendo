package team1502.configuration.builders.motors;

import java.util.function.BiConsumer;
import java.util.function.Function;

import com.revrobotics.CANSparkBase.IdleMode;

import com.revrobotics.CANSparkMax;
import com.revrobotics.MotorFeedbackSensor;
import com.revrobotics.RelativeEncoder;
import com.revrobotics.SparkPIDController;

import team1502.configuration.CAN.DeviceType;
import team1502.configuration.CAN.Manufacturer;
import team1502.configuration.builders.Builder;
import team1502.configuration.builders.IBuild;
import team1502.configuration.builders.Part;

public class MotorController extends Builder {
    private static final DeviceType deviceType = DeviceType.MotorController; 
    public static final String CLASSNAME = "MotorController";

    private static final String isReversed = "isReversed";
    private static final String closedLoopRampRate = "closedLoopRampRate";
    private static final String smartCurrentLimit = "smartCurrentLimit";
    /** Wheel Diameter (m) */
    public static final String wheelDiameter = "wheelDiameter";
    
    public static final Function<IBuild, MotorController> Define(Manufacturer manufacturer) {
        return build->new MotorController(build,manufacturer);
    }
    public static MotorController Wrap(Builder builder) { return new MotorController(builder.getIBuild(), builder.getPart()); }
    public static MotorController WrapPart(Builder builder) { return WrapPart(builder, CLASSNAME); }
    public static MotorController WrapPart(Builder builder, String partName) { return Wrap(builder.getPart(partName)); }

    // Define
    public MotorController(IBuild build, Manufacturer manufacturer) {
        super(build, deviceType, manufacturer);
        addConnector(POWER, "Vin").FriendlyName("Power connector");
        addChannel(POWER, "Vout").FriendlyName("Motor Power Out");
    }
    public MotorController(IBuild build, Part part) {
        super(build, part);
    }
    
    public Motor Motor() {return Motor.WrapPart(this, Motor.CLASSNAME); }
    public MotorController Motor(String partName) {
        return Motor(partName, m->m);
    }
    public MotorController Motor(String partName, Function<Motor, Builder> fn) {
        var motor = addPart(Motor.Define, Motor.CLASSNAME, partName, fn);
        this.Powers(motor);
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
    public MotorController PID(Function<PID, Builder> fn) {
        return (MotorController)AddPart(PID.Define, fn);
    }
    public MotorController PID(double p, double i, double d) {
        return PID(pid->pid.P(p).I(i).D(d));
    }
    public MotorController PID(double p, double i, double d, double ff) {
        return PID(pid->pid.P(p).I(i).D(d).FF(ff));
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

    public SparkPIDController buildPIDController(MotorFeedbackSensor feedbackDevice) {
        var pid = PID().setPIDController(CANSparkMax());
        pid.setFeedbackDevice(feedbackDevice);
        return pid;
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

    SwerveModule getSwerveModule() {
        var parent = getParentOfType(SwerveModule.CLASSNAME);
        return parent == null ? null : SwerveModule.Wrap(parent);
    }
    public RelativeEncoder buildRelativeEncoder() {
        var encoder = getRelativeEncoder();
        //var swerveModule = getSwerveModule();
        //if (swerveModule != null) {
            encoder.setPositionConversionFactor(getPositionConversionFactor());
            encoder.setVelocityConversionFactor(getVelocityConversionFactor());
        //}
        return encoder;
    }

    /** mpr * 60 = position/minute (like rpm)  */
    public double getVelocityConversionFactor() { return getPositionConversionFactor()/60;  }
    public double getPositionConversionFactor() {
        var circumference = findDouble(MotorController.wheelDiameter, 0.0) * Math.PI;
        if (circumference == 0.0) {circumference = 360.0; } //degrees
        return circumference * getGearBoxRatio(); 
    }
    private double getGearBoxRatio() { return GearBox() != null ? GearBox().GearRatio() : 1.0; }
    private double getWheelDiameter() { return findDouble(MotorController.wheelDiameter, 1.0); }
    public double calculateMaxSpeed() { return calculateMaxSpeed(getWheelDiameter()); }
    public double calculateMaxSpeed(Double wheelDiameter) {
        return Motor().FreeSpeedRPM() / 60.0
        * GearBox().GearRatio()
        * wheelDiameter * Math.PI; 
    }

    public void registerLoggerObjects(BiConsumer<String, CANSparkMax> motorLogger) {
        motorLogger.accept(FriendlyName(), CANSparkMax());
        if (hasValue("Follower")) {
            Follower().registerLoggerObjects(motorLogger);
        }
    }
}
