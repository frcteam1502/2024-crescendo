package team1502.configuration.builders;

import java.util.function.Function;

import team1502.configuration.CAN.DeviceType;
import team1502.configuration.CAN.Manufacturer;

/** https://docs.wpilib.org/en/stable/docs/software/roborio-info/roborio-introduction.html */
public class RoboRIO extends Builder {
    private static final DeviceType deviceType = DeviceType.RobotController;
    public static final String version = "version"; 
    
    public static final String digitalInput = "digitalInput";

    public static final String NAME = "RoboRIO"; 
    public static final Function<IBuild, RoboRIO> Define = build->new RoboRIO(build);
    public static RoboRIO Wrap(Builder builder) { return new RoboRIO(builder.getIBuild(), builder.getPart()); }
    public static RoboRIO WrapPart(Builder builder) { return WrapPart(builder, NAME); }
    public static RoboRIO WrapPart(Builder builder, String partName) { return Wrap(builder.getPart(partName)); }
    public RoboRIO(IBuild build) {
         super(build); 
         Device(deviceType);
         Manufacturer(Manufacturer.NI);
         AddDIO();
         AddPWM();
    }
    public RoboRIO(IBuild build, Part part) { super(build, part); }
    
    public RoboRIO Version(String version) {
        setValue(RoboRIO.version, version);
        return this;
    }
    public RoboRIO PWM(Function</*PulseWidthModulatedBus, PulseWidthModulatedBus*/Builder,Builder> fn) {
        return this;
    }
    Builder PWM() { return getPart("PWM"); }
    void AddPWM() {
        var pwn = addPart(Builder.DefineAs("PWM"), d->d);
        for (int ch = 0; ch < 10; ch++) {
            pwn.addPiece(Channel.Define(Channel.SIGNAL_PWM, NAME, ch));
        }
    }
    
    public RoboRIO DIO(Function</*DigitalBus, DigitalBus*/Builder,Builder> fn) {
        return this;
    }
    Builder DIO() { return getPart("DIO"); }
    void AddDIO() {
        var dio = addPart(Builder.DefineAs("DIO"), d->d);
        for (int ch = 0; ch < 10; ch++) {
            dio.addPiece(Channel.Define(Channel.SIGNAL_DIGITAL, NAME, ch));
        }
    }
    public RoboRIO DIO(Builder part) {
        if (part.hasValue(RoboRIO.digitalInput)) {
            return DIO(part.getInt(RoboRIO.digitalInput), part);
        }
        return this;
    }
    public RoboRIO DIO(Integer channelNumber, Builder part) {
        updateDioChannel(channelNumber, part);
        return this;
    }
    public void updateDioChannel(Integer channelNumber, Builder part) {
        getChannel(channelNumber).TryConnect(part);
    }
    public Channel getChannel(int channelNumber) {
        return  Channel.Wrap(DIO().getPiece(channelNumber));
    }

    // public PulseWidthModulatedBus Spark(int channel, String name, String device) {
    //     return this;
    // }

}
