package team1502.configuration.builders.motors;

import java.util.function.Function;

import com.ctre.phoenix6.hardware.CANcoder;

import team1502.configuration.CAN.Manufacturer;
import team1502.configuration.builders.Builder;
import team1502.configuration.builders.Connector;
import team1502.configuration.builders.IBuild;
import team1502.configuration.builders.Part;

public class SwerveModule extends Builder {
    public static final String CLASSNAME = "SwerveModule";
    /** offset (m) */
    public static final String location = "location";
    private static final String absoluteEncoder = "Encoder";
    private static final String turningMotor = "TurningMotor";
    private static final String drivingMotor = "DrivingMotor";
    private static final String isReversed = "isReversed";
    public static Function<IBuild, SwerveModule> Define = build->new SwerveModule(build);
    public static SwerveModule Wrap(Builder builder) { return builder == null ? null : new SwerveModule(builder.getIBuild(), builder.getPart()); }
    public static SwerveModule WrapPart(Builder builder) { return WrapPart(builder, CLASSNAME); }
    public static SwerveModule WrapPart(Builder builder, String partName) { return Wrap(builder.getPart(partName)); }

    public SwerveModule(IBuild build) { super(build, CLASSNAME); }
    public SwerveModule(IBuild build, Part part) { super(build, part); }

    public SwerveModule Wrap(Function<SwerveModule, Builder> fn) {
        fn.apply(this);
        return this;
    }
    public CANCoder Encoder() {return CANCoder.WrapPart(this, SwerveModule.absoluteEncoder);}
    public SwerveModule Encoder(Function<CANCoder, Builder> fn) {
        fn.apply(Encoder());
        return this;
    }
    
    public SwerveModule CANCoder(Function<CANCoder, Builder> fn) {
        addPart(CANCoder.Define, SwerveModule.absoluteEncoder, fn);
        return this;
    }

    public MotorController TurningMotor() { return MotorController.WrapPart(this, SwerveModule.turningMotor); }
    public SwerveModule TurningMotor(Manufacturer manufacturer, Function<MotorController, Builder> fn) {
        addPart(MotorController.Define(manufacturer), SwerveModule.turningMotor, fn);
        return this;
    }
    
    public MotorController DrivingMotor() { return MotorController.WrapPart(this, SwerveModule.drivingMotor); }
    public SwerveModule DrivingMotor(Manufacturer manufacturer, Function<MotorController, Builder> fn) {
        addPart(MotorController.Define(manufacturer), SwerveModule.drivingMotor, fn);
        return this;
    }

    public boolean Reversed() { return getBoolean(SwerveModule.isReversed, false); }
    public SwerveModule Reversed(boolean value) {
        TurningMotor().Reversed(value);
        DrivingMotor().Reversed(value);
        return this;
    }
    
    /**
     * Root number of standard convention of CAN number and PDH channel
     */
    public SwerveModule CanNumber(int rootNumber) { return CanNumbers(rootNumber, rootNumber, rootNumber+1); }
    public SwerveModule CanNumberDown(int rootNumber) { return CanNumbers(rootNumber, rootNumber, rootNumber-1); }
    public int CanNumberEncoder() { return Encoder().CanNumber(); }
    public int CanNumberTurningMotor() { return TurningMotor().CanNumber(); }
    public int CanNumberDrivingMotor() { return DrivingMotor().CanNumber(); }
    public SwerveModule CanNumbers(int absoluteEncoder, int turningMotor, int drivingMotor) {
        Encoder().CanNumber(absoluteEncoder);

        TurningMotor().CanNumber(turningMotor); 
        Connector.findConnector(TurningMotor(),POWER).Label(turningMotor + " " + turningMotor + " " + turningMotor);
        TurningMotor().Abbreviation(Abbreviation()+"T");
        TurningMotor().FriendlyName((FriendlyName()+" Turning"));
        TurningMotor().PDH(turningMotor);
        
        DrivingMotor().CanNumber(drivingMotor);
        Connector.findConnector(DrivingMotor(), POWER).Label(drivingMotor + " " + drivingMotor + " " + drivingMotor);
        DrivingMotor().Abbreviation(Abbreviation()+"D");
        DrivingMotor().FriendlyName((FriendlyName()+" Driving"));
        DrivingMotor().PDH(drivingMotor);

        return this;
    }

    /** in meters per second */
    public double calculateMaxSpeed() {
        return DrivingMotor().calculateMaxSpeed() * Math.PI; 
    }

    public frc.robot.subsystems.SwerveDrive.SwerveModule getSwerveModuleInstance() {
        return (frc.robot.subsystems.SwerveDrive.SwerveModule)Value("getSwerveModuleInstance");
    }
    public SwerveModule setSwerveModuleInstance(frc.robot.subsystems.SwerveDrive.SwerveModule sm) {
        Value("getSwerveModuleInstance", sm);
        return this;
    }

    public CANcoder getCANcoder() {
        var encoder = Encoder();
        var canCoder = encoder.CANcoder();
        return (canCoder != null) ? canCoder : encoder.buildCANcoder();
    }
}
