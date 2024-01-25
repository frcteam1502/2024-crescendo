package team1502.configuration.CAN;

public class CanInfo {
    public DeviceType deviceType;
    public Manufacturer manufacturer;
    public Integer deviceNumber = -1; // invalid value means it has not been set

    public CanInfo() {}
    public CanInfo(DeviceType deviceType, Manufacturer manufacturer) {
        this.deviceType = deviceType;
        this.manufacturer = manufacturer;
    }
    public CanInfo(DeviceType deviceType, Manufacturer manufacturer, int deviceNumber) {
        this(deviceType, manufacturer);
        this.deviceNumber = deviceNumber;
    }

    public int getDeviceTypeId() {return deviceType.DeviceID;}
    public int getManufacturerId() {return manufacturer.ManufacturerID;}

    public CanInfo Manufacturer(Manufacturer manufacturer) {
        this.manufacturer = manufacturer;
        return this;
    }

    public CanInfo Device(DeviceType deviceType) {
        this.deviceType = deviceType;
        return this;
    }
    public CanInfo Number(int number) {
        this.deviceNumber = number;
        return this;
    }

    public boolean isValid() {
        return deviceType != null && manufacturer != null && deviceNumber > -1;  
    }
}