package team1502.configuration.Builders.Controllers;

import java.util.function.Function;

import team1502.configuration.Builders.Builder;
import team1502.configuration.CAN.DeviceType;
import team1502.configuration.CAN.Manufacturer;

public class RobotController extends Controller {
    private static final DeviceType Type = DeviceType.RobotController;

    public RobotController(String name, Function<? extends RobotController, Builder> fn) {
        super(name, Type, fn);
    }
    public RobotController(String name, Manufacturer manufacturer, Function<? extends RobotController, Builder> fn) {
        super(name, Type, manufacturer, fn);
    }
        
    public RobotController(Function<? extends RobotController, Builder> buildFunction) {
        super(Type, buildFunction);
    }

    public RobotController() {
        super(Type);
    }

    @Override
    public Builder createBuilder() {
        return new RobotController((Function<RobotController, Builder>)buildFunction);
    }
    
}
