package team1502.configuration.builders.motors;

import team1502.configuration.CAN.CanInfo;
import team1502.configuration.CAN.DeviceType;
import team1502.configuration.CAN.Manufacturer;
import team1502.configuration.builders.Builder;
import team1502.configuration.builders.IBuild;
import team1502.configuration.builders.Part;

import java.util.function.Function;

import com.ctre.phoenix6.signals.AbsoluteSensorRangeValue;
import com.ctre.phoenix6.signals.SensorDirectionValue;
import com.ctre.phoenix6.configs.CANcoderConfiguration;
import com.ctre.phoenix6.hardware.CANcoder;

public class CANCoder extends Builder{ // Accelerometer {
    private static final String isReversed = "isReversed";
    private static final String magneticOffset = "magneticOffset";

    public static final DeviceType deviceType = DeviceType.Accelerometer;
    public static final Function<IBuild, CANCoder> Define =  build->new CANCoder(build);
    public static CANCoder Wrap(Builder builder) { return builder == null ? null : new CANCoder(builder.getIBuild(), builder.getPart()); }
    public static CANCoder WrapPart(Builder builder) { return WrapPart(builder, deviceType.name()); }
    public static CANCoder WrapPart(Builder builder, String partName) { return Wrap(builder.getPart(partName)); }
    public CANCoder(IBuild build) {
        super(build, deviceType); 
        CanInfo.addConnector(this, deviceType, Manufacturer.CTRElectronics);
    }
    public CANCoder(IBuild build, Part part) { super(build, part); }
    
    /** CounterClockwise_Positive (default) or Clockwise_Positive (if reversed) */
    public SensorDirectionValue Direction() {
        var result = getBoolean(isReversed, false);
        return result
            ? SensorDirectionValue.Clockwise_Positive
            : SensorDirectionValue.CounterClockwise_Positive;
    }
    /** CounterClockwise_Positive (default, false) or Clockwise_Positive (if reversed, true) 
        False (default) means positive rotation occurs when magnet
        is spun counter-clockwise when observer is facing the LED side of CANCoder.
   */
    public CANCoder CounterClockwise_Positive() {
        setValue(isReversed, false);
        return this;
    }
    public CANCoder Clockwise_Positive() {
        setValue(isReversed, true);
        return this;
    }

    public double MagneticOffset() { return getDouble(magneticOffset); }
    public CANCoder MagneticOffset(double degrees) {
        setValue(CANCoder.magneticOffset, degrees);
        return this;
    }

    public CANcoder buildCANcoder() {
        var encoder = CANcoder(new CANcoder(CanNumber()));
        encoder.getConfigurator().apply(
            new CANcoderConfiguration().MagnetSensor
            .withMagnetOffset(-MagneticOffset()/360.0)
            .withSensorDirection(Direction())
            .withAbsoluteSensorRange(AbsoluteSensorRangeValue.Unsigned_0To1)
        );
        return encoder;
    }
    public CANcoder CANcoder() {
        return (CANcoder)Value("CANcoder");
    }
    public CANcoder CANcoder(CANcoder encoder) {
        Value("CANcoder", encoder);
        return encoder;
    }

}

/*
 *  Set the angle in radians per pulse for the turning encoder.
 *  turningEncoder.setPositionConversionFactor(Constants.ModuleConstants.RADIANS_PER_ENCODER_REV);
 
    this.absEncoder.configSensorDirection(CANCoderDirection);
    this.absEncoder.configMagnetOffset(-absOffset);

 */