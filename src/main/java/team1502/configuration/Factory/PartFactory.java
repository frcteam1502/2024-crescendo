package team1502.configuration.Factory;

import java.util.HashMap;
import java.util.function.Function;

import team1502.configuration.Builders.Builder;
import team1502.configuration.Builders.Motor;
import team1502.configuration.Builders.SwerveDrive;
import team1502.configuration.Builders.SwerveModule;
import team1502.configuration.Builders.Controllers.GyroSensor;
import team1502.configuration.Builders.Controllers.IMU;
import team1502.configuration.Builders.Controllers.MotorController;
import team1502.configuration.Builders.Controllers.PowerDistributionModule;
import team1502.configuration.Builders.Controllers.RoboRIO;
import team1502.configuration.CAN.Manufacturer;

public class PartFactory {
    private HashMap<String, Builder> _builderMap = new HashMap<>(); 
       
    public PartFactory Part(String name, Function<Builder, Builder> fn) {
        _builderMap.put(name,  new Builder(name, fn));
        return this;
    }

    public void useBuilder(String partName, Builder builder) {
        var template = _builderMap.get(partName);
        if (template != null) {
            template.createBuilder(builder);
        }
    }
    
    public Builder getBuilder(String name) {
        var template = _builderMap.get(name);
        return template.createBuilder();
    }

    public PartFactory Motor(Function<Motor, Builder> fn) {
        return Motor("Motor", fn);
    }
    public PartFactory Motor(String name, Function<Motor, Builder> fn) {
        _builderMap.put(name,  new Motor(name, fn));
        return this;
    }
    
    // MOTOR CONTROLLER
    public PartFactory MotorController(Function<MotorController, Builder> fn) {
        return MotorController("MotorController", fn);
    }
    public PartFactory MotorController(String name, Manufacturer manufacturer, Function<MotorController, Builder> fn) {
        _builderMap.put(name,  new MotorController(name, manufacturer, fn));
        return this;
    }
    public PartFactory MotorController(String name, Function<MotorController, Builder> fn) {
        _builderMap.put(name,  new MotorController(name, fn));
        return this;
    }

    // GYRO SENSOR
    public PartFactory GyroSensor(String name, Manufacturer manufacturer, Function<GyroSensor, Builder> fn) {
        _builderMap.put(name,  new GyroSensor(name, manufacturer, fn));
        return this;
    }


    public PartFactory SwerveModule(Function<SwerveModule, Builder> fn) {
        _builderMap.put("SwerveModule",  new SwerveModule(fn));
        return this;
    }
    public PartFactory SwerveDrive(Function<SwerveDrive, Builder> fn) {
        _builderMap.put("SwerveDrive",  new SwerveDrive(fn));
        return this;
    }

    // Basic Parts
    public PartFactory RoboRIO(Function<RoboRIO, Builder> fn) {
        _builderMap.put("RoboRIO",  new RoboRIO(fn));
        return this;
    }
    public PartFactory PowerDistributionHub(Function<PowerDistributionModule, Builder> fn) {
        _builderMap.put(PowerDistributionModule.PDH,  new PowerDistributionModule(PowerDistributionModule.PDH, Manufacturer.REVRobotics, fn));
        return this;
    }
    public PartFactory PowerDistributionPanel(Function<PowerDistributionModule, Builder> fn) {
        _builderMap.put(PowerDistributionModule.PDP,  new PowerDistributionModule(PowerDistributionModule.PDP, Manufacturer.CTRElectronics, fn));
        return this;
    }
    public PartFactory Pigeon2(Function<IMU, Builder> fn) {
        _builderMap.put("Pigeon2",  new IMU("Pigeon2", Manufacturer.CTRElectronics, fn));
        return this;
    }

    // "part" Parts
    public PartFactory Radio(Function<Builder, Builder> fn) {
        return Part("Radio", fn);
    }
    public PartFactory RadioPowerModule(Function<Builder, Builder> fn) {
        return Part("RadioPowerModule", fn);
    }
    public PartFactory RadioBarrelJack(Function<Builder, Builder> fn) {
        return Part("RadioBarrelJack", fn);
    }
    public PartFactory RadioSignalLight(Function<Builder, Builder> fn) {
        return Part("RadioSignalLight", fn);
    }
    public PartFactory EthernetSwitch(Function<Builder, Builder> fn) {
        return Part("EthernetSwitch", fn);
    }
    public PartFactory TimeOfFlight(Function<Builder, Builder> fn) {
        return Part("TimeOfFlight", fn);
    }
    public PartFactory Compressor(Function<Builder, Builder> fn) {
        return Part("Compressor", fn);
    }
    public PartFactory LimeLight(Function<Builder, Builder> fn) {
        return Part("LimeLight", fn);
    }
    public PartFactory RaspberryPi(Function<Builder, Builder> fn) {
        return Part("RaspberryPi", fn);
    }
    public PartFactory LEDs(Function<Builder, Builder> fn) {
        return Part("LEDs", fn);
    }

}
