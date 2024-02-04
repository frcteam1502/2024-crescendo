package team1502.configuration.Builders;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.function.Function;

import edu.wpi.first.wpilibj.PowerDistribution;
import team1502.configuration.Builders.Controllers.GyroSensor;
import team1502.configuration.Builders.Controllers.IMU;
import team1502.configuration.Builders.Controllers.MotorController;
import team1502.configuration.Builders.Controllers.PneumaticsController;
import team1502.configuration.Builders.Controllers.PowerDistributionModule;
import team1502.configuration.Builders.Controllers.RoboRIO;
import team1502.configuration.CAN.CanMap;
import team1502.configuration.CAN.Manufacturer;
import team1502.configuration.Factory.PartFactory;
import team1502.configuration.Parts.Part;

public class RobotBuilder implements IBuild /*extends Builder*/{
    private PartFactory _partFactory;
    private HashMap<String, Builder> _buildMap = new HashMap<>(); // built top-level parts wrapped in builder
    private ArrayList<Part> _parts = new ArrayList<>(); // every part created
    
    private CanMap _canMap; // create this after all CAN devices are built
    
    private RobotBuilder(PartFactory partFactory) {
        _partFactory = partFactory;
    }

    public String Note;

    public static RobotBuilder Create(PartFactory partFactory) {
        var robot = new RobotBuilder(partFactory);
        return robot;
    }

    public static RobotBuilder Create(PartFactory partFactory, Function<RobotBuilder, RobotBuilder> fn) {
        var robot = new RobotBuilder(partFactory);
        fn.apply(robot);
        return robot;
    }

    // IBuild INTERFACE
    public void install(Builder builder) {
        _buildMap.put(builder.getName(), builder);
    }

    public Builder getInstalled(String name) {
        return _buildMap.get(name);
    }

    @Override // IBuild
    public Builder createBuilder(String partName, Function<? extends Builder, Builder> fn) {
        var builder = _partFactory.getBuilder(partName);
        builder.create((IBuild)this, partName, fn);
        return builder;
    }

    @Override // IBuild
    public Builder modifyBuilder(String partName, Function<? extends Builder, Builder> fn) {
        var builder = getInstalled(partName);
        builder.apply(fn); // fail fast
        return builder;
    }

    @Override // IBuild
    public void register(Part part) {
        _parts.add(part);
    }

    private Builder installBuilder(Builder builder) {
        install(builder);
        return builder;
    }

    /*
    private <T extends Builder> T installBuilder(
        String name,
        String partName,
        T builder,
        Function<IBuild, T> ctor,
        Function<T, Builder> fn) {
        
        if (_partFactory.hasPart(partName)) {
            builder.Name(name);
            builder.create((IBuild)this, fn);
            return (T)installBuilder(builder);
            
        } else {
            builder = ctor.apply((IBuild)this);
            builder.create((IBuild)this, name);
            return (T)installBuilder(builder);
        }
    }
    */

    private Builder installBuilder(String name, String partName, Builder builder, Function<? extends Builder, Builder> fn) {
        _partFactory.useBuilder(partName, builder);
        builder.Name(name);
        builder.create((IBuild)this, fn);
        return installBuilder(builder);
    }

    // BUILDER AND BUILDER SUBCLASSES

    public RobotBuilder Note(String note) {
        Note = note;
        return this;
    }
    public RobotBuilder Part(String partName, Function<Builder, Builder> fn) {
        return Part(partName, partName, fn);
    }
    public RobotBuilder Part(String name, String partName, Function<Builder, Builder> fn) {
        installBuilder(name, partName, new Builder(), fn);
        return this;
    }

    public RobotBuilder GyroSensor(String partName, Function<GyroSensor, Builder> fn) {        
        return GyroSensor(partName, partName, fn);
    }
    public RobotBuilder GyroSensor(String name, String partName, Function<GyroSensor, Builder> fn) {        
        installBuilder(name, partName, new GyroSensor(), fn);
        return this;
    }    
    public RobotBuilder Pigeon2(String name, Function<IMU, Builder> fn) {        
        installBuilder(name, "Pigeon2", new IMU(), fn);
        return this;
    }    
    public RobotBuilder Motor(Function<Motor, Builder> fn) {
        return Motor("Motor", "Motor", fn);
    }
    public RobotBuilder Motor(String name, String partName, Function<Motor, Builder> fn) {        
        installBuilder(name, partName, new Motor(), fn);
        return this;
    }    
    public RobotBuilder MotorController(String name, String partName, Function<MotorController, Builder> fn) {        
        installBuilder(name, partName, new MotorController(), fn);
        return this;
    }    
    public RobotBuilder SwerveModule(String name, Function<SwerveModule, Builder> fn) {        
        installBuilder(name, "SwerveModule", new SwerveModule(), fn);
        return this;
    }    
    public RobotBuilder SwerveDrive(Function<SwerveDrive, Builder> fn) {        
        installBuilder("SwerveDrive", "SwerveDrive", new SwerveDrive(), fn);
        return this;
    }    

    // Basic Parts
    public RobotBuilder RoboRIO(Function<RoboRIO, Builder> fn) {
        installBuilder(RoboRIO.NAME, RoboRIO.NAME, new RoboRIO(), fn);
        return this;
    }
    public RobotBuilder Pigeon2(Function<IMU, Builder> fn) {
        installBuilder(IMU.Pigeon2, IMU.Pigeon2, new IMU(), fn);
        return this;
    }
    public RobotBuilder PowerDistributionHub(Function<PowerDistributionModule, Builder> fn) {
        installBuilder(PowerDistributionModule.PDH, PowerDistributionModule.PDH,  new PowerDistributionModule(), fn);
        return this;
    }
    public RobotBuilder MiniPowerModule(String name, Function<PowerDistributionModule, Builder> fn) {
        if (!_partFactory.hasTemplate(PowerDistributionModule.MPM)) {
            _partFactory.Part(PowerDistributionModule.MPM, PowerDistributionModule.ctorMPM);
        }

        //Function<String, PowerDistributionModule> create = (n)->new PowerDistributionModule(n, fn);
        installBuilder(name, PowerDistributionModule.MPM, new PowerDistributionModule(), fn);
        return this;
    }
    public RobotBuilder PCM(Function<PneumaticsController, Builder> fn) {
        installBuilder(PneumaticsController.PCM, PneumaticsController.PCM,  new PneumaticsController(), fn);
        return this;
    }

    // "part" Parts
    public RobotBuilder Radio(Function<Builder, Builder> fn) {
        return Part("Radio", fn);
    }
    public RobotBuilder RadioPowerModule(Function<Builder, Builder> fn) {
        return Part("RadioPowerModule", fn);
    }
    public RobotBuilder RadioBarrelJack(Function<Builder, Builder> fn) {
        return Part("RadioBarrelJack", fn);
    }
    public RobotBuilder RadioSignalLight(Function<Builder, Builder> fn) {
        return Part("RadioSignalLight", fn);
    }
    public RobotBuilder EthernetSwitch(Function<Builder, Builder> fn) {
        return Part("EthernetSwitch", fn);
    }
    public RobotBuilder TimeOfFlight(Function<Builder, Builder> fn) {
        return Part("TimeOfFlight", fn);
    }
    public RobotBuilder Compressor(Function<Builder, Builder> fn) {
        return Part("Compressor", fn);
    }
    public RobotBuilder LimeLight(Function<Builder, Builder> fn) {
        return Part("LimeLight", fn);
    }
    public RobotBuilder RaspberryPi(Function<Builder, Builder> fn) {
        return Part("RaspberryPi", fn);
    }
    public RobotBuilder LEDs(Function<Builder, Builder> fn) {
        return Part("LEDs", fn);
    }


    // VALUES and VALUE EXPRESSIONS

    /**
     * Should not create this before all CAN parts are created
     * @return
     */
    public CanMap getCanMap() {
        if (_canMap == null) {
            _canMap = new CanMap();
            for (Part part : _parts) {
                if (part.isCanDevice()) {
                    _canMap.install(part);
                }
            }
        }
        return _canMap;
    }
    public PowerDistributionModule getPowerDistributionModule() {
        var pdm = getInstalled(PowerDistributionModule.PDH);
        if (pdm == null) {
            pdm = getInstalled(PowerDistributionModule.PDP);
            if (pdm == null) {
                var builder = _partFactory.getBuilder(PowerDistributionModule.PDH);
                if (builder == null) {
                    builder = _partFactory.getBuilder(PowerDistributionModule.PDP);
                }
                builder.create((IBuild)this);
                pdm = installBuilder(builder);
            }
 
        }
        return (PowerDistributionModule)pdm;
    }

    private <T extends Builder> Object getValue(String partName, T builder, Function<T, Object> fn) {
        var susBuilder = getInstalled(partName);
        return susBuilder.evalWith(fn, builder);
    
    }
    private <T extends Builder> Object getValue(String partName, Function<T, Object> fn) {
        var builder = getInstalled(partName);
        return fn.apply((T)builder);
    }

}
