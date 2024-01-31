package team1502.configuration.Builders.Controllers;

import team1502.configuration.Builders.Builder;
import team1502.configuration.CAN.Manufacturer;
import team1502.configuration.Parts.Part;

import java.util.function.Function;

import com.ctre.phoenix6.signals.SensorDirectionValue;

public class CANCoder extends Accelerometer {
    private static final String ISREVERSED = "isReversed";

    public CANCoder(String name, Function<CANCoder, Builder> fn) {
        super(name, Manufacturer.CTRElectronics, fn);
    }

    public CANCoder(Part part) {
        super();
        setPart(part);
    }

    // Install
    public CANCoder(Function<CANCoder, Builder> fn) {
        super(fn);
    }

    
    @Override
    public Builder createBuilder() {
        return new CANCoder((Function<CANCoder, Builder>)buildFunction);
    }
    
    public SensorDirectionValue Direction() {
        var result = getBoolean(ISREVERSED, false);
        return result
            ? SensorDirectionValue.Clockwise_Positive
            : SensorDirectionValue.CounterClockwise_Positive;
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
 *  Set the angle in radians per pulse for the turning encoder.
 *  turningEncoder.setPositionConversionFactor(Constants.ModuleConstants.RADIANS_PER_ENCODER_REV);
 
    this.absEncoder.configSensorDirection(CANCoderDirection);
    this.absEncoder.configMagnetOffset(-absOffset);

 */