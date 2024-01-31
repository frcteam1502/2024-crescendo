package team1502.configuration.Builders.Controllers;

import java.util.function.Function;

import team1502.configuration.Builders.Builder;
import team1502.configuration.CAN.Manufacturer;

public class RoboRIO extends RobotController {
    public static final String NAME = "RoboRIO"; 

    public RoboRIO() {
        super();
    }
    public RoboRIO(Function<RoboRIO, Builder> buildFunction) {
        super(NAME, Manufacturer.NI, buildFunction);
    }

    @Override
    public Builder Apply(Function<? extends Builder, Builder> fn) {
        return new RoboRIO((Function<RoboRIO, Builder>)buildFunction);
    }
    
    public RoboRIO Version(String version) {
        setValue("version", version);
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
