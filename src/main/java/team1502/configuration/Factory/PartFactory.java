package team1502.configuration.factory;

import java.util.HashMap;
import java.util.function.Function;

import team1502.configuration.CAN.Manufacturer;
import team1502.configuration.builders.Builder;
import team1502.configuration.builders.IBuild;
import team1502.configuration.builders.RoboRIO;
import team1502.configuration.builders.motors.Motor;
import team1502.configuration.builders.motors.MotorController;
import team1502.configuration.builders.motors.SwerveDrive;
import team1502.configuration.builders.motors.SwerveModule;
import team1502.configuration.builders.power.PowerDistributionModule;
import team1502.configuration.builders.sensors.GyroSensor;
import team1502.configuration.builders.sensors.IMU;

public class PartFactory {
    private HashMap<String, PartBuilder<?>> _builderMap = new HashMap<>();
    private IBuild _build;
    private PartFactory _parent;

    public PartFactory(IBuild build) {
        _build = build;
    }
    public PartFactory(PartFactory parent, IBuild build) {
        _build = build;
        _parent = parent;
    }
    public IBuild getIBuild() {return _build; }

    public <T extends Builder> PartFactory addTemplate(String name, Function<IBuild, T> createFunction, Function<T, Builder> buildFunction)  {
        _builderMap.put(name,  new PartBuilder<T>(name, createFunction, buildFunction));
        return this;
    }
    
    public boolean hasTemplate(String partName) {
        return _builderMap.containsKey(partName);
    }

    public PartBuilder<? extends Builder> getTemplate(String partName) {
        var template = _builderMap.get(partName);
        return template != null 
            ? template
            : _parent != null 
                ? _parent.getTemplate(partName)
                : null;
    }

    public Builder createBuilder(String partName) {
        var partBuilder =  _builderMap.get(partName);
        return partBuilder.creatBuilder(getIBuild());
    }

    public <T extends Builder> PartBuilder<?> getTemplate(String partName, Function<IBuild, T> createFunction, Function<T, Builder> buildFunction) {
        var template = (PartBuilder<T>)getTemplate(partName);
        if (template != null) {
            return template.WithModification(buildFunction);
        } else {
            return new PartBuilder(partName, createFunction, buildFunction);
        }
    }

    public PartFactory Part(String name, Function<Builder, Builder> fn) {
        return addTemplate(name,  b->new Builder(b, name), fn);
    }

    public PartFactory Motor(Function<Motor, Builder> fn) {
        return Motor(Motor.NAME, fn);
    }
    public PartFactory Motor(String name, Function<Motor, Builder> fn) {
        return addTemplate(name, Motor.Define, fn);
    }
    
    public PartFactory MotorController(String name, Manufacturer manufacturer, Function<MotorController, Builder> fn) {
        return addTemplate(name, MotorController.Define(manufacturer), fn);
    }

    public PartFactory GyroSensor(String name, Manufacturer manufacturer, Function<GyroSensor, Builder> fn) {
        return addTemplate(name,  GyroSensor.Define(manufacturer), fn);
    }

    public PartFactory SwerveDrive(Function<SwerveDrive, Builder> fn) {
        return addTemplate(SwerveDrive.NAME, SwerveDrive.Define, fn);
    }
    public PartFactory SwerveModule(Function<SwerveModule, Builder> fn) {
        return addTemplate(SwerveModule.NAME,  SwerveModule.Define, fn);
    }

    // Basic Parts
    public PartFactory RoboRIO(Function<RoboRIO, Builder> fn) {
        return addTemplate(RoboRIO.NAME,  RoboRIO.Define, fn);
    }
    public PartFactory PowerDistributionHub(Function<PowerDistributionModule, Builder> fn) {
        return addTemplate(PowerDistributionModule.PDH,  PowerDistributionModule.DefinePDH, fn);
    }
    public PartFactory PowerDistributionPanel(Function<PowerDistributionModule, Builder> fn) {
        return addTemplate(PowerDistributionModule.PDP,  PowerDistributionModule.DefinePDP, fn);
    }
    public PartFactory Pigeon2(Function<IMU, Builder> fn) {
        return addTemplate(IMU.Pigeon2,  IMU.Define(Manufacturer.CTRElectronics), fn);
    }

    // "part" Parts
    public PartFactory DC(Function<Builder, Builder> fn) {
        return Part("DC-DC", fn);
    }
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
