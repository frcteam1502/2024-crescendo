package team1502.configuration.Builders.Controllers;

import java.util.function.Function;

import team1502.configuration.Builders.Builder;
import team1502.configuration.CAN.DeviceType;
import team1502.configuration.CAN.Manufacturer;

// a CAN controller of a certain type, a template (or definition?)
public class Controller extends Builder {
    // public DeviceType deviceType; // for CAN assignments
    // public Manufacturer manufacturer; // for CAN assignments
    // public String name;
    // public PowerProfile powerProfile;

    
    public Controller(String name, DeviceType deviceType, Manufacturer manufacturer, Function<? extends Builder,  Builder> fn ) {
        super(deviceType.toString(), name, c -> c
            .CanInfo(can -> can
                .Device(deviceType)
                .Manufacturer(manufacturer))
            .Apply(fn));
    }
    public Controller(String name, DeviceType deviceType, Function<? extends Builder,  Builder> fn ) {
        super(deviceType.toString(), name, c -> c
            .CanInfo(can -> can.Device(deviceType))
            .Apply(fn));
    }

    public Controller(String name, DeviceType deviceType) {
        super(deviceType.toString(), name, c -> c
            .CanInfo(can -> can.Device(deviceType)));
    }
    public Controller(String name, DeviceType deviceType, Manufacturer manufacturer) {
        super(deviceType.toString(), name, c -> c
            .CanInfo(can -> can
                .Device(deviceType)
                .Manufacturer(manufacturer)));
    }

    //Install
    public Controller(DeviceType deviceType, Function<? extends Builder,  Builder> fn) {
        super(deviceType.toString(), "", fn);
    }
    //Build / Eval
    public Controller(DeviceType deviceType) {
        super(deviceType.toString());
    }
    
    public Controller Manufacturer(Manufacturer manufacturer) {
        CanInfo(i->i.Manufacturer(manufacturer));
        return this;
    }
    
    public Controller CanNumber(int number) {
        setCanNumber(number);
        return this;
    }

    public int CanNumber() {
        return getCanNumber();
    }
    public Manufacturer Manufacturer() {
        return getManufacturer();
    }

/*
    public Controller PowerProfile(double peakPower) {
        PowerProfile(w->w.PeakPower(peakPower));
        return this;
    }

    public Controller(String name, double power) {
        this.PowerProfile(power);
    }
    public Controller(String name, DeviceType deviceType, Manufacturer manufacturer) {
        this.name = name;
        this.deviceType = deviceType;
        this.manufacturer = manufacturer;
    }

    public Controller(DeviceType deviceType, Manufacturer manufacturer) {
        this.name = deviceType.DeviceName;
        this.deviceType = deviceType;
        this.manufacturer = manufacturer;
    }


    public Controller Device(DeviceType deviceType) {
        this.deviceType = deviceType;
        return this;
    }

    public Controller PowerProfile(PowerProfile profile) {
        this.powerProfile = profile;
        return this;
    }
  
    public Controller Device(SupportedDevices deviceType) {
        //this.deviceType = deviceType;
        return this;
    }
 */
}
