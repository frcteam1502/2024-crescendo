package team1502.configuration.Builders.Controllers;

import java.util.function.Function;

import team1502.configuration.Builders.Builder;
import team1502.configuration.CAN.DeviceType;
import team1502.configuration.CAN.Manufacturer;

public class GyroSensor extends Controller {
    private static final String NAME = "GyroSensor"; 
    private static final String ISREVERSED = "isReversed";

    // Define
    public GyroSensor(String name, Manufacturer manufacturer, Function<GyroSensor, Builder> fn) {
        super(name, DeviceType.GyroSensor, manufacturer, fn);
    }
    //Build Proxy / Eval
    public GyroSensor() {
        super(DeviceType.GyroSensor);
    }
   
    //Install
    public GyroSensor(Function<GyroSensor, Builder> fn) {
        super(DeviceType.GyroSensor, fn);
    }

    @Override
    public Builder createBuilder() {
        return new GyroSensor((Function<GyroSensor, Builder>)buildFunction);
    }


/*
    // Pigeon2 configuration items:
    public double MountPoseYaw = 0;
    public double MountPosePitch = 0;
    public double MountPoseRoll = 0;
    public boolean EnableCompass = false;
    public boolean DisableTemperatureCompensation = false;
    public boolean DisableNoMotionCalibration = false;
    public double XAxisGyroError = 0;
    public double YAxisGyroError = 0;
    public double ZAxisGyroError = 0;
*/

    public boolean Reversed() {
        var result = getBoolean(ISREVERSED);
        return result == null ? false : result;
    }
    public GyroSensor Reversed(boolean value) {
        setValue(ISREVERSED, value);
        return this;
    }
}
