package team1502.configuration.builders.motors;

import java.util.function.Function;

import com.ctre.phoenix6.hardware.CANcoder;

import team1502.configuration.CAN.Manufacturer;
import team1502.configuration.builders.Builder;
import team1502.configuration.builders.IBuild;
import team1502.configuration.builders.Part;
import team1502.configuration.builders.power.PowerDistributionModule;

public class SwerveModule extends Builder {
    public static final String NAME = "SwerveModule";
    public static final String location = "location";
    public static final String wheelDiameter = "wheelDiameter";
    private static final String AbsoluteEncoder = "Encoder";
    private static final String TurningMotor = "TurningMotor";
    private static final String DrivingMotor = "DrivingMotor";
    private static final String isReversed = "isReversed";
    public static Function<IBuild, SwerveModule> Define = build->new SwerveModule(build);
    public static SwerveModule Wrap(Builder builder) { return new SwerveModule(builder.getIBuild(), builder.getPart()); }
    public static SwerveModule WrapPart(Builder builder) { return WrapPart(builder, NAME); }
    public static SwerveModule WrapPart(Builder builder, String partName) { return Wrap(builder.getPart(partName)); }

    public SwerveModule(IBuild build) { super(build, NAME); }
    public SwerveModule(IBuild build, Part part) { super(build, part); }

    public SwerveModule Wrap(Function<SwerveModule, Builder> fn) {
        fn.apply(this);
        return this;
    }
    public CANCoder Encoder() {return CANCoder.WrapPart(this, AbsoluteEncoder);}
    public SwerveModule Encoder(Function<CANCoder, Builder> fn) {
        fn.apply(Encoder());
        return this;
    }
    
    public SwerveModule CANCoder(Function<CANCoder, Builder> fn) {
        addPart(CANCoder.Define, AbsoluteEncoder, fn);
        return this;
    }

    public MotorController TurningMotor() { return MotorController.WrapPart(this, TurningMotor); }
    public SwerveModule TurningMotor(Manufacturer manufacturer, Function<MotorController, Builder> fn) {
        addPart(MotorController.Define(manufacturer), TurningMotor, fn);
        return this;
    }
    
    public MotorController DrivingMotor() { return MotorController.WrapPart(this, DrivingMotor); }
    public SwerveModule DrivingMotor(Manufacturer manufacturer, Function<MotorController, Builder> fn) {
        addPart(MotorController.Define(manufacturer), DrivingMotor, fn);
        return this;
    }

    public boolean Reversed()  {return getBoolean(isReversed, false); }
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
        TurningMotor().CanNumber(turningMotor); TurningMotor().PowerChannel(turningMotor);
        DrivingMotor().CanNumber(drivingMotor); DrivingMotor().PowerChannel(drivingMotor);

        // TODO: HACK -- hard-coded PDH
        PowerDistributionModule pdm = PowerDistributionModule.Wrap(getIBuild().getInstalled(PowerDistributionModule.PDH));
        
        TurningMotor().Powers(TurningMotor().Motor());
        DrivingMotor().Powers(TurningMotor().Motor());

        TurningMotor().Abbreviation(Abbreviation()+"T");
        TurningMotor().PowerChannel(turningMotor);
        pdm.updateChannel(TurningMotor());
        
        DrivingMotor().Abbreviation(Abbreviation()+"D");
        DrivingMotor().PowerChannel(drivingMotor);
        pdm.updateChannel(DrivingMotor());

        return this;
    }

    public double getPositionConversionFactor() {
        double wheelDiameterMeters = getDouble(wheelDiameter);
        double driveGearRatio = DrivingMotor().GearBox().GearRatio();
        return (wheelDiameterMeters * Math.PI) * driveGearRatio;
    }
    
    public double getVelocityConversionFactor() {
        return getPositionConversionFactor()/60; // mpr * 60 = position/minute (like rpm)
    }

    public frc.robot.subsystems.SwerveDrive.SwerveModule getSwerveModuleInstance() {
        return (frc.robot.subsystems.SwerveDrive.SwerveModule)Value("getSwerveModuleInstance");
    }
    public SwerveModule setSwerveModuleInstance(frc.robot.subsystems.SwerveDrive.SwerveModule sm) {
        Value("getSwerveModuleInstance", sm);
        return this;
    }

}
