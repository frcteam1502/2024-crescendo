package team1502.configuration.Builders.Controllers;

import team1502.configuration.Builders.Builder;
import team1502.configuration.Builders.Encoder;
import team1502.configuration.CAN.CanInfo;
import team1502.configuration.CAN.DeviceType;
import team1502.configuration.CAN.ICAN;
import team1502.configuration.CAN.Manufacturer;
import team1502.configuration.Parts.Part;

import java.util.function.Function;

public class CANCoder extends Controller /*Encoder /* implements ICAN */{
    private static final DeviceType TYPE = DeviceType.GyroSensor; // is it?
    private static final String ISREVERSED = "isReversed";

    public CANCoder(String name, Manufacturer manufacturer, Function<CANCoder, Builder> fn) {
        super(name, TYPE, manufacturer, fn);
    }

    public CANCoder(Part part) {
        super(TYPE);
        setPart(part);
    }

    // Install
    public CANCoder(Function<CANCoder, Builder> fn) {
        super(TYPE, fn);
    }

    
    @Override
    public Builder createBuilder() {
        return new CANCoder((Function<CANCoder, Builder>)buildFunction);
    }
    
    public boolean Direction() {
        var result = getBoolean(ISREVERSED);
        return result == null ? false : result;
    }
    /**
      False (default) means positive rotation occurs when magnet
      is spun counter-clockwise when observer is facing the LED side of CANCoder.
   */
    public CANCoder Direction(boolean value) {
        setValue(ISREVERSED, value);
        return this;
    }

    public double MagneticOffset() { return getDouble("magneticOffset"); }
    public CANCoder MagneticOffset(double degrees) {
        setValue("magneticOffset", degrees);
        return this;
    }

}

/*
 *     // Set the angle in radians per pulse for the turning encoder.
    //turningEncoder.setPositionConversionFactor(Constants.ModuleConstants.RADIANS_PER_ENCODER_REV);
    this.absEncoder.configSensorDirection(CANCoderDirection);
    this.absEncoder.configMagnetOffset(-absOffset);

 */