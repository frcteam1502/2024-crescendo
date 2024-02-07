package team1502.configuration.builders.sensors;

import java.util.function.Function;

import team1502.configuration.CAN.DeviceType;
import team1502.configuration.CAN.Manufacturer;
import team1502.configuration.builders.Builder;
import team1502.configuration.builders.IBuild;
import team1502.configuration.builders.Part;

public class GearToothSensor extends Builder {
    private static final DeviceType deviceType = DeviceType.GearToothSensor;

    public static final Function<IBuild, GearToothSensor> Define(Manufacturer manufacturer) {
        return build->new GearToothSensor(build, manufacturer);
    } 
    public static GearToothSensor Wrap(Builder builder) { return new GearToothSensor(builder.getIBuild(), builder.getPart()); }
    public static GearToothSensor WrapPart(Builder builder) { return WrapPart(builder, deviceType.name()); }
    public static GearToothSensor WrapPart(Builder builder, String partName) { return Wrap(builder.getPart(partName)); }
    public GearToothSensor(IBuild build, Manufacturer manufacturer) {
         super(build); 
         Device(deviceType);
         Manufacturer(manufacturer);
    }
    public GearToothSensor(IBuild build, Part part) { super(build, part); }

}