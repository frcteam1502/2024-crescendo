package team1502.configuration.Builders.Controllers;

import java.util.function.Function;

import team1502.configuration.Builders.Builder;
import team1502.configuration.CAN.DeviceType;
import team1502.configuration.CAN.Manufacturer;

public class IMU extends Controller {
    private static final DeviceType Type = DeviceType.IMU;
    public static final String Pigeon2 = "Pigeon2";

    public IMU(String name, Function<? extends IMU, Builder> fn) {
        super(name, Type, fn);
    }
    public IMU(String name, Manufacturer manufacturer, Function<? extends IMU, Builder> fn) {
        super(name, Type, manufacturer, fn);
    }
        
    public IMU(Function<? extends IMU, Builder> buildFunction) {
        super(Type, buildFunction);
    }

    public IMU() {
        super(Type);
    }

    @Override
    public Builder createBuilder() {
        return new IMU((Function<IMU, Builder>)buildFunction);
    }
    
}
