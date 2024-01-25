package team1502.configuration.Builders;

import java.util.function.Function;

import team1502.configuration.CAN.CanInfo;
import team1502.configuration.CAN.DeviceType;
import team1502.configuration.CAN.Manufacturer;
import team1502.configuration.Parts.Part;

public class Gyro extends Builder {
    private Function<Gyro, Builder> buildFunction;
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

    public static final String ISREVERSED = "isReversed";

    //Define
    public Gyro(String name, Function<Gyro, Builder> fn) {
        super("GyroSensor", name, null);
        buildFunction = fn;
    }

    //Build
    public Gyro(Function<Gyro, Builder> fn) {
        super("GyroSensor");
        buildFunction = fn;
    }

    public Gyro Reversed(boolean reversed) {
        setValue(ISREVERSED, reversed);
        return this;
    }

    public Boolean Reversed() {
        return getBoolean(ISREVERSED);
    }
}
