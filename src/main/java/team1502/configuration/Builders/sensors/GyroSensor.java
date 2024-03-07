package team1502.configuration.builders.sensors;

import java.util.function.Function;

import team1502.configuration.CAN.CanInfo;
import team1502.configuration.CAN.DeviceType;
import team1502.configuration.CAN.Manufacturer;
import team1502.configuration.builders.Builder;
import team1502.configuration.builders.IBuild;
import team1502.configuration.builders.Part;

public class GyroSensor extends Builder {
    private static final DeviceType deviceType = DeviceType.GyroSensor; 
    public static final String Gyro = "Gyro"; // alt name
    private static final String isReversed = "isReversed";
    public static Function<IBuild, GyroSensor> Define(Manufacturer manufacturer) {
        return build->new GyroSensor(build,manufacturer);
    } 
    public static GyroSensor Wrap(Builder builder) { return builder == null ? null : new GyroSensor(builder.getIBuild(), builder.getPart()); }
    public static GyroSensor WrapPart(Builder builder) { return WrapPart(builder, deviceType.name()); }
    public static GyroSensor WrapPart(Builder builder, String partName) { return Wrap(builder.getPart(partName)); }

    public GyroSensor(IBuild build, Manufacturer manufacturer) {
        super(build, deviceType);
        CanInfo.addConnector(this, deviceType, manufacturer);
    }
    
    public GyroSensor(IBuild build, Part part) {super(build, part); }
   
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
        var result = getBoolean(isReversed);
        return result == null ? false : result;
    }
    public GyroSensor Reversed(boolean value) {
        setValue(isReversed, value);
        return this;
    }
}
