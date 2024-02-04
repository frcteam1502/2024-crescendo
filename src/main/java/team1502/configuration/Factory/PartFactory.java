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

    public PartFactory addTemplate(String name, Builder builder) {
        _builderMap.put(name,  builder);
        return this;
    }
    
    public boolean hasTemplate(String partName) {
        return _builderMap.containsKey(partName);
    }

    public Builder getTemplate(String partName) {
        return _builderMap.get(partName);
    }

    public PartFactory Part(String name, Function<Builder, Builder> fn) {
        return addTemplate(name,  new Builder(name, fn));
    }

    public void useBuilder(String partName, Builder builder) {
        var template = getTemplate(partName);
        if (template != null) {
            template.createBuilder(builder);
        }
    }
    
    public Builder getBuilder(String name) {
        var template = getTemplate(name);
        return template.createBuilder();
    }

    public PartFactory Motor(Function<Motor, Builder> fn) {
        return Motor("Motor", fn);
    }
    public PartFactory Motor(String name, Function<Motor, Builder> fn) {
        return addTemplate(name,  new Motor(name, fn));
    }
    
    // MOTOR CONTROLLER
    public PartFactory MotorController(Function<MotorController, Builder> fn) {
        return MotorController("MotorController", fn);
    }
    public PartFactory MotorController(String name, Manufacturer manufacturer, Function<MotorController, Builder> fn) {
        return addTemplate(name,  new MotorController(name, manufacturer, fn));
    }
    public PartFactory MotorController(String name, Function<MotorController, Builder> fn) {
        return addTemplate(name,  new MotorController(name, fn));
    }

    // GYRO SENSOR
    public PartFactory GyroSensor(String name, Manufacturer manufacturer, Function<GyroSensor, Builder> fn) {
        return addTemplate(name,  new GyroSensor(name, manufacturer, fn));
    }


    public PartFactory SwerveModule(Function<SwerveModule, Builder> fn) {
        return addTemplate("SwerveModule",  new SwerveModule(fn));
    }
    public PartFactory SwerveDrive(Function<SwerveDrive, Builder> fn) {
        return addTemplate("SwerveDrive",  new SwerveDrive(fn));
    }

    // Basic Parts
    public PartFactory RoboRIO(Function<RoboRIO, Builder> fn) {
        return addTemplate("RoboRIO",  new RoboRIO(fn));
    }
    public PartFactory PowerDistributionHub(Function<PowerDistributionModule, Builder> fn) {
        return addTemplate(PowerDistributionModule.PDH,  new PowerDistributionModule(PowerDistributionModule.PDH, Manufacturer.REVRobotics, fn));
    }
    public PartFactory PowerDistributionPanel(Function<PowerDistributionModule, Builder> fn) {
        return addTemplate(PowerDistributionModule.PDP,  new PowerDistributionModule(PowerDistributionModule.PDP, Manufacturer.CTRElectronics, fn));
    }
    public PartFactory Pigeon2(Function<IMU, Builder> fn) {
        return addTemplate("Pigeon2",  new IMU("Pigeon2", Manufacturer.CTRElectronics, fn));
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
