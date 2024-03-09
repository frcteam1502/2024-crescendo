package team1502.configuration.CAN;

import java.util.function.Function;

import team1502.configuration.builders.*;

public class CanInfo extends Connector {
    public static String canInfo = "canInfo";
    private static String deviceType = "deviceType";
    private static String manufacturer = "manufacturer";
    private static String deviceNumber = "deviceNumber"; // = -1; // invalid value means it has not been set
    
    public static final Function<IBuild, CanInfo> Define(DeviceType deviceType, Manufacturer manufacturer) {
         return  build->new CanInfo(build, deviceType, manufacturer); 
    };
    
    public static CanInfo Wrap(Builder builder) { return builder == null ? null : new CanInfo(builder.getIBuild(), builder.getPart()); }
    public static CanInfo WrapPart(Builder builder) { return WrapPart(builder, canInfo); }
    public static CanInfo WrapPart(Builder builder, String partName) { return Wrap(builder.getPart(partName)); }
    
    // public CanInfo(IBuild build) {
    //     super(build, Channel.SIGNAL_CAN);
    // }
    private CanInfo(IBuild build, DeviceType deviceType, Manufacturer manufacturer) {
        super(build, Channel.SIGNAL_CAN);
        Device(deviceType);
        Manufacturer(manufacturer);
        FriendlyName(manufacturer.ManufacturerName + " " + deviceType.DeviceName);
        Name(Channel.SIGNAL_CAN + ":" + deviceType.toString() + "|" + manufacturer.toString());
        Value(Part.KEY_NAME, CanInfo.canInfo);
    }
    
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
        Name(Channel.SIGNAL_CAN + ":" + "[" + number + "]" + Device().toString() + "|" + Manufacturer().toString());
        FriendlyName(FriendlyName() + " " + number);
        var rio = getIBuild().getInstalled(RoboRIO.CLASSNAME);
        if (rio != null) {
            rio.addChannel(Channel.SIGNAL_CAN, this.Host());
        }
        return this;
    }

    public boolean isValid() {
        return Device() != null && Manufacturer() != null && Number() != null;  
    }
    public boolean isCanDevice() {
        return getPart() != null;
    }

    public static CanInfo findConnection(Builder device) {
        var can = device.findConnector(Channel.SIGNAL_CAN);
        return Wrap(can);
    }

    public static CanInfo addConnector(Builder device, DeviceType deviceType, Manufacturer manufacturer, Integer number) {
        var can = addConnector(device, deviceType, manufacturer);
        can.Number(number);
        return can;
    }

    public static CanInfo addConnector(Builder device, DeviceType deviceType, Manufacturer manufacturer) {
        var can = device.addPart(CanInfo.Define(deviceType, manufacturer));
        if (device.Type() == "") {
            device.Type(deviceType.toString());
        }
        return can;
    }
}