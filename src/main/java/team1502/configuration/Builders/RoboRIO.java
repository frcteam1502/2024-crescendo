package team1502.configuration.builders;

import java.util.function.Function;

import team1502.configuration.CAN.DeviceType;
import team1502.configuration.CAN.Manufacturer;

public class RoboRIO extends Builder {
    private static final DeviceType deviceType = DeviceType.RobotController;
    public static final String version = "version"; 

    public static final String NAME = "RoboRIO"; 
    public static final Function<IBuild, RoboRIO> Define = build->new RoboRIO(build);
    public static RoboRIO Wrap(Builder builder) { return new RoboRIO(builder.getIBuild(), builder.getPart()); }
    public static RoboRIO WrapPart(Builder builder) { return WrapPart(builder, NAME); }
    public static RoboRIO WrapPart(Builder builder, String partName) { return Wrap(builder.getPart(partName)); }
    public RoboRIO(IBuild build) {
         super(build); 
         Device(deviceType);
         Manufacturer(Manufacturer.NI);
    }
    public RoboRIO(IBuild build, Part part) { super(build, part); }
    
    public RoboRIO Version(String version) {
        setValue(RoboRIO.version, version);
        return this;
    }
    public RoboRIO PWM(Function</*PulseWidthModulatedBus, PulseWidthModulatedBus*/Builder,Builder> fn) {
        return this;
    }
    public RoboRIO DIO(Function</*DigitalBus, DigitalBus*/Builder,Builder> fn) {
        return this;
    }
    
    // public PulseWidthModulatedBus Spark(int channel, String name, String device) {
    //     return this;
    // }

}
