package team1502.configuration.Builders;

import java.util.function.Function;

import team1502.configuration.Builders.Controllers.CANCoder;
import team1502.configuration.Builders.Controllers.MotorController;
import team1502.configuration.CAN.Manufacturer;
import team1502.configuration.Parts.Part;

public class SwerveModule extends Builder {
    private static final String NAME = "SwerveModule";
    private static final String AbsoluteEncoder = "Encoder";
    private static final String TurningMotor = "TurningMotor";
    private static final String DrivingMotor = "DrivingMotor";
    private static final String ISREVERSED = "isReversed";
    
    public SwerveModule() { super(NAME); }
    public SwerveModule(Part part) {
         super(NAME);
         setPart(part);
    }

    public SwerveModule(Function<SwerveModule, Builder> fn) {
        super(NAME, NAME, fn);
    }

    @Override
    public Builder createBuilder() {
        return new SwerveModule((Function<SwerveModule, Builder>)buildFunction);
    }
    
    public CANCoder Encoder() {return new CANCoder(getPart(AbsoluteEncoder));}

    public SwerveModule Encoder(Function<CANCoder, Builder> fn) {
        fn.apply(Encoder());
        return this;
    }
    
    public SwerveModule CANCoder(Function<CANCoder, Builder> fn) {
        Install(new CANCoder(AbsoluteEncoder, fn));
        return this;
    }

    public MotorController TurningMotor() { return new MotorController(getPart(TurningMotor)); }
    public SwerveModule TurningMotor(Manufacturer manufacturer, Function<MotorController, Builder> fn) {
        Install(new MotorController(TurningMotor, manufacturer, fn));
        return this;
    }
    
    public MotorController DrivingMotor() { return new MotorController(getPart(DrivingMotor)); }
    public SwerveModule DrivingMotor(Manufacturer manufacturer, Function<MotorController, Builder> fn) {
        Install(new MotorController(DrivingMotor, manufacturer, fn));
        return this;
    }

    public boolean Reversed() {
        var result = getBoolean(ISREVERSED);
        return result == null ? false : result;
    }
    public SwerveModule Reversed(boolean value) {
        TurningMotor().Reversed(value);
        DrivingMotor().Reversed(value);
        return this;
    }

    public SwerveModule CanNumber(int rootNumber) { return CanNumbers(rootNumber, rootNumber, rootNumber+1); }
    public SwerveModule CanNumberDown(int rootNumber) { return CanNumbers(rootNumber, rootNumber, rootNumber-1); }
    public int CanNumberEncoder() { return Encoder().CanNumber(); }
    public int CanNumberTurningMotor() { return TurningMotor().CanNumber(); }
    public int CanNumberDrivingMotor() { return DrivingMotor().CanNumber(); }
    public SwerveModule CanNumbers(int absoluteEncoder, int turningMotor, int drivingMotor) {
        Encoder().CanNumber(absoluteEncoder);
        TurningMotor().CanNumber(turningMotor);
        DrivingMotor().CanNumber(drivingMotor);
        return this;
    }

    public double getPositionConversionFactor() {
        double WHEEL_DIAMETER_METERS = getDouble("wheelDiameter");
        double DRIVE_GEAR_RATIO = DrivingMotor().GearBox().GearRatio();
        return (WHEEL_DIAMETER_METERS * Math.PI) * DRIVE_GEAR_RATIO;
    }
    public double getVelocityConversionFactor() {
        return getPositionConversionFactor()/60; // mpr * 60 = position/minute (like rpm)
    }
}
