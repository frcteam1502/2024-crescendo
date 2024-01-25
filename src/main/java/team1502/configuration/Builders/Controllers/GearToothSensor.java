package team1502.configuration.Builders.Controllers;

import java.util.function.Function;

import team1502.configuration.Builders.Builder;
import team1502.configuration.CAN.DeviceType;

public class GearToothSensor extends Controller {
    public GearToothSensor(String name) {
        super(name, DeviceType.GearToothSensor);
    }
    
    public GearToothSensor(Function<GearToothSensor, Builder> buildFunction) {
        super(DeviceType.GearToothSensor, buildFunction);
    }

    @Override
    public Builder createBuilder() {
        return new GearToothSensor((Function<GearToothSensor, Builder>)buildFunction);
    }


}