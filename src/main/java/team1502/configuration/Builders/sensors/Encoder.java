package team1502.configuration.builders.sensors;

import java.util.function.Function;

import edu.wpi.first.wpilibj.DutyCycleEncoder;

import team1502.configuration.builders.Builder;
import team1502.configuration.builders.IBuild;
import team1502.configuration.builders.Part;
import team1502.configuration.builders.RoboRIO;

public class Encoder extends Builder {
    public static final String NAME = "Encoder";
    private static final String offset = "offset";
    private static final String dutyCycleEncoder = "dutyCycleEncoder";

    public static Function<IBuild, Encoder> Define = build->new Encoder(build);
    public static Encoder Wrap(Builder builder) { return new Encoder(builder.getIBuild(), builder.getPart()); }
    public static Encoder WrapPart(Builder builder) { return WrapPart(builder, NAME); }
    public static Encoder WrapPart(Builder builder, String partName) { return Wrap(builder.getPart(partName)); }

    public Encoder(IBuild build) { super(build, NAME); }
    public Encoder(IBuild build, Part part) { super(build, part); }
  
    public Integer DigitalInput() { return getInt(RoboRIO.digitalInput); }
    public Encoder DigitalInput(int channel) {
        Value(RoboRIO.digitalInput, channel);
        return this;
    }

    public Encoder Offset(double offset) {
        Value(Encoder.offset, offset);
        return this;
    }

    public DutyCycleEncoder buildDutyCycleEncoder() {
        return DutyCycleEncoder(new DutyCycleEncoder(DigitalInput()));
    }

    public DutyCycleEncoder DutyCycleEncoder() {
        return (DutyCycleEncoder)Value(Encoder.dutyCycleEncoder);
    }
    public DutyCycleEncoder DutyCycleEncoder(DutyCycleEncoder encoder) {
        Value(Encoder.dutyCycleEncoder, encoder);
        return encoder;
    }

    public double getPositionDegrees(){
        //REV Encoder is CCW+
        double angleDegrees = DutyCycleEncoder().getAbsolutePosition()*360;
        return angleDegrees - 360 - getDouble(Encoder.offset, 0.0);
    }
    
    /*
     * 
    public DutyCycleEncoder buildDutyCycleEncoder(int channel) {
        return new DutyCycleEncoder(channel);
    }
    public DutyCycleEncoder buildDutyCycleEncoder(DutyCycle dutyCycle) {
        return new DutyCycleEncoder(dutyCycle);
    }
    public DutyCycleEncoder buildDutyCycleEncoder(DigitalSource source) {
        return new DutyCycleEncoder(source);
    }
     */
}
