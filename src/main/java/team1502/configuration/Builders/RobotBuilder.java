package team1502.configuration.Builders;

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

public class RobotBuilder implements IBuild /*extends Builder*/{
    private PartFactory _partFactory;
    private HashMap<String, Builder> _buildMap = new HashMap<>(); // built parts wrapped in builder
    
    private CanMap _canMap = new CanMap();
    
    private RobotBuilder(PartFactory partFactory) {
        //super(null);
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

    private Builder installBuilder(Builder builder) {
        install(builder);
        return builder;
    }

    private Builder installBuilder(String name, String partName, Builder builder, Function<? extends Builder, Builder> fn) {
        _partFactory.useBuilder(partName, builder);
        builder.Name(name);
        builder.create((IBuild)this, fn);
        return installBuilder(builder);
    }
/* 
 * 
    private Builder installBuilder(String name, String partName, Function<? extends Builder, Builder> fn) {
        var builder = createBuilder(partName, fn);
        return installBuilder(builder);
    }

    
    public RobotBuilder Part(DeviceType deviceType, String partName, Function<Builder, Builder> fn) {
        return Part(deviceType.toString(), partName, fn);
    }

    public RobotBuilder Part(String buildType, String partName, Function<Builder, Builder> fn) {
        var builder = new Builder(buildType, partName, fn);
        builder.create((IBuild)this);
        install(builder);
        return this;
    }
*/

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

    public CanMap getCanMap() {return _canMap;}
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

/*
     // Eval as-a Motor as long is in the right "Shape"
     public RobotBuilder Motor(String valueName, String partName, Function<Motor, Object> fn) {
         _valueMap.put(valueName, s -> getValue(partName, new Motor(), fn));   
         return this;
        }
        public RobotBuilder MotorController(String valueName, String partName, Function<MotorController, Object> fn) {
            _valueMap.put(valueName, s -> getValue(partName, new MotorController(), fn));   
        return this;
    }
    //public RobotBuilder Eval(String valueName, )

    // public RobotBuilder Value(String valueName, String partName, Function<? extends Builder, Object> fn) {
    //     _valueMap.put(valueName, s -> getValue(partName, fn));   
    //     return this;
    // }
    // public Object Value(String valueName) {
    //     var fn = _valueMap.get(valueName);
    //     return fn.apply(valueName);
    // }

    @Override // IBuild
    public Part getPart(String name) {
        return _partMap.get(name);
    }
    @Override // IBuild
    public Part createPart(String newName, String partName) {
        var builder = _partFactory.getBuilder(partName);
        Part part = builder.build((IBuild)this, newName);
        part.name= newName;
        return part;
    }
    private Part createPart(String partName) {
        return createPart(partName, partName);
    }

    //@Override
    protected void install(Part part) {
        _partMap.put(part.name, part);
        if (part.hasCanInfo()) {
            _canMap.install(part);
        }
    }
    private Builder getBuilder(String partName) {
        var builder = getInstalled(partName);
        if (builder == null) {
            builder = _partFactory.getBuilder(partName);
            builder.create((IBuild)this, partName);
            builder.install((IBuild)this);
        }
        return builder;
    }
 
    public RobotBuilder Build(String newName, String partName, Function<Part, Part> fn)
    {        
        var part = createPart(newName, partName);
        fn.apply(part);
        install(part);
        return this;
    }

    public RobotBuilder Build(String name, Function<Part, Part> fn)
    {        
        return Build(name, name, fn);
    }    

    public RobotBuilder Part(String name, Function<Part, Part> fn)
    {        
        return Build(name, name, fn);
    }

 */
    // public RobotBuilder GyroSensor(String name, int canNumber, Function<GyroSensor, Part> fn)
    // {        
    //     var part = createPart(name);
    //     fn.apply(part);
    //     install(part);
    //     return this;
    // }    

    // public RobotBuilder SwerveDrive(Function<SwerveBuilder, SwerveBuilder> fn) {
    //     var swerve = new SwerveBuilder(this);
    //     fn.apply(swerve);
    //     return this;
    // }
}
