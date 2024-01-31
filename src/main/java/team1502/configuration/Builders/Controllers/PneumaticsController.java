package team1502.configuration.Builders.Controllers;

import java.util.function.Function;

import team1502.configuration.Builders.Builder;
import team1502.configuration.CAN.DeviceType;
import team1502.configuration.CAN.Manufacturer;

public class PneumaticsController extends Controller {
    private static final DeviceType Type = DeviceType.PneumaticsController;
    public static final String PCM = "PCM";
    public static final String Compressor = "Compressor";

    public PneumaticsController(String name, Function<? extends PneumaticsController, Builder> fn) {
        super(name, Type, fn);
    }
    public PneumaticsController(String name, Manufacturer manufacturer, Function<? extends PneumaticsController, Builder> fn) {
        super(name, Type, manufacturer, fn);
    }
        
    public PneumaticsController(Function<? extends PneumaticsController, Builder> buildFunction) {
        super(Type, buildFunction);
    }

    public PneumaticsController() {
        super(Type);
    }

    @Override
    public Builder createBuilder() {
        return new PneumaticsController((Function<PneumaticsController, Builder>)buildFunction);
    }


    public PneumaticsController Compressor() {
        Install(Compressor, Compressor, c -> c);
        return this;
    }
    public PneumaticsController Solenoid(int module, int channel, String name) {
        return this;
    }
    public PneumaticsController DoubleSolenoid(int forwardChannel, int reverseChannel, String name) {
        return this;
    }

}
