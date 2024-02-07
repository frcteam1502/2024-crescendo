package team1502.configuration.CAN;

import java.util.function.Function;

import team1502.configuration.builders.Builder;
import team1502.configuration.builders.IBuild;
import team1502.configuration.builders.Part;

public class CanInfo extends Builder {
    public static String canInfo = "canInfo";
    private static String deviceType = "deviceType";
    private static String manufacturer = "manufacturer";
    private static String deviceNumber = "deviceNumber"; // = -1; // invalid value means it has not been set
    public static final Function<IBuild, CanInfo> Define = build->new CanInfo(build);
    public static CanInfo Wrap(Builder builder) { return new CanInfo(builder.getIBuild(), builder.getPart()); }
    public static CanInfo WrapPart(Builder builder) { return WrapPart(builder, canInfo); }
    public static CanInfo WrapPart(Builder builder, String partName) { return Wrap(builder.getPart(partName)); }
    public CanInfo(IBuild build) { super(build, canInfo); }
    public CanInfo(IBuild build, Part part) { super(build, part); }

    public int getDeviceTypeId() {return Device().DeviceID;}
    public int getManufacturerId() {return Manufacturer().ManufacturerID;}

    public Manufacturer Manufacturer() { return (Manufacturer)getValue(manufacturer); }
    public CanInfo Manufacturer(Manufacturer manufacturer) {
        setValue(CanInfo.manufacturer, manufacturer);
        return this;
    }

    public DeviceType Device() { return (DeviceType)getValue(deviceType); }
    public CanInfo Device(DeviceType deviceType) {
        setValue(CanInfo.deviceType, deviceType);
        return this;
    }

    public Integer Number() { return getInt(deviceNumber); }
    public CanInfo Number(int number) {
        setValue(deviceNumber, number);
        return this;
    }

    public boolean isValid() {
        return Device() != null && Manufacturer() != null && Number() != null;  
    }
    public boolean isCanDevice() {
        return getPart() != null;
    }
}