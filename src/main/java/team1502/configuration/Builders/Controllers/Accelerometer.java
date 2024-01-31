package team1502.configuration.Builders.Controllers;

import java.util.function.Function;

import team1502.configuration.Builders.Builder;
import team1502.configuration.CAN.DeviceType;
import team1502.configuration.CAN.Manufacturer;

public class Accelerometer extends Controller {
    private static final DeviceType Type = DeviceType.Accelerometer;

    public Accelerometer(String name, Function<? extends Accelerometer, Builder> fn) {
        super(name, Type, fn);
    }
    public Accelerometer(String name, Manufacturer manufacturer, Function<? extends Accelerometer, Builder> fn) {
        super(name, Type, manufacturer, fn);
    }
        
    public Accelerometer(Function<? extends Accelerometer, Builder> buildFunction) {
        super(Type, buildFunction);
    }

    public Accelerometer() {
        super(Type);
    }

    @Override
    public Builder createBuilder() {
        return new Accelerometer((Function<Accelerometer, Builder>)buildFunction);
    }


}
