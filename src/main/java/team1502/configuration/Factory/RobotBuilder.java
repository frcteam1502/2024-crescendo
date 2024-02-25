package team1502.configuration.factory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.function.Function;

import team1502.configuration.CAN.CanInfo;
import team1502.configuration.CAN.CanMap;
import team1502.configuration.CAN.Manufacturer;
import team1502.configuration.builders.*;
import team1502.configuration.builders.motors.*;
import team1502.configuration.builders.pneumatics.*;
import team1502.configuration.builders.power.*;
import team1502.configuration.builders.sensors.*;

public class RobotBuilder implements IBuild /*extends Builder*/{
    private PartFactory _partFactory;
    private HashMap<String, Builder> _buildMap = new HashMap<>(); // built top-level parts wrapped in builder
    private ArrayList<Part> _parts = new ArrayList<>(); // every part created
    
    private CanMap _canMap; // create this after all CAN devices are built
    
    private RobotBuilder() {
        _partFactory = new PartFactory(getIBuild());
    }
    
    private RobotBuilder _parent;
    private Builder _part;
    //private Part _part;
    private RobotBuilder(RobotBuilder parent, String name) {
        // todo, look for existing part "sub-factory"
        _parent = parent;
        _partFactory = new PartFactory(_parent.getPartFactory(), getIBuild());
        _part= new Builder(_parent, "Subsystem"); // ??
        _part.Name(name);
        _part.setIBuild(this);
        _part.Value("robotBuilder", this);
    }
    
    public PartFactory getPartFactory() { return _partFactory; }
    ArrayList<Part> getParts() { return _parts; }

    public String Note;

    public static RobotBuilder Create() {
        var robot = new RobotBuilder();
        return robot;
    }

    public static RobotBuilder Create(Function<RobotBuilder, RobotBuilder> fn) {
        var robot = new RobotBuilder();
        fn.apply(robot);
        return robot;
    }

    // IBuild INTERFACE
    public IBuild getIBuild() {return this; }

    //private Builder _lastInstalled = null;
    public void install(Builder builder) {
        _buildMap.put(builder.getName(), builder);
        if (_part != null) {
            _part.addPart(builder);
        }
        //_lastInstalled = builder;
    }

    @Override // IBuild
    public Builder getInstalled(String name) {
        var installed =_buildMap.get(name);
        return installed != null
            ? installed
            : _parent != null
                ? _parent.getInstalled(name)
                : null;
    }

    @Override // IBuild
    public <T extends Builder> PartBuilder<?> getTemplate(String partName, Function<IBuild, T> createFunction,
            Function<T, Builder> buildFunction) {
        return _partFactory.getTemplate(partName, createFunction, buildFunction);
    }

    @Override // IBuild
    public void register(Part part) {
        _parts.add(part);
        if (_parent != null) {
            _parent.register(part);
        }
    }
    private Builder installBuilder(Builder builder) {
        install(builder);
        return builder;
    }


    @SuppressWarnings("unchecked")
    private <T extends Builder> RobotBuilder installBuilder(String name, String partName, Function<IBuild, T> createFunction, Function<T, Builder> buildFunction) {
        var template = _partFactory.getTemplate(partName, createFunction, t -> buildFunction.apply(((T)t.Name(name))));
        var builder = template.creatBuilder(getIBuild());
        //builder.Name(name);
        install(builder);
        return this;
    }

    // BUILDER AND BUILDER SUBCLASSES

    public RobotBuilder Note(String note) {
        Note = note;
        return this;
    }
    public Builder Part(String partName) { return getInstalled(partName); }
    public RobotBuilder Part(String partName, Function<Builder, Builder> fn) {
        return Part(partName, partName, fn);
    }
    public RobotBuilder Part(String name, String partName, Function<Builder, Builder> fn) {
        return installBuilder(name, partName, b->new Builder(b, partName), fn);
    }

    public RobotBuilder Subsystem(String partName) { return (RobotBuilder)getInstalled(partName).Value("robotBuilder"); }
    public RobotBuilder Subsystem(String partName, Function<RobotBuilder, RobotBuilder> fn) {
        var child = new RobotBuilder(this, partName);
        return fn.apply(child); //Part(partName, partName, fn);
    }

    public Builder Encoder() { return Encoder(Encoder.NAME); }
    public Builder Encoder(String partName) { return getInstalled(partName); }
    public RobotBuilder Encoder(String partName, Function<Encoder, Builder> fn) {        
        return Encoder(partName, partName, fn);
    }
    public RobotBuilder Encoder(String name, String partName, Function<Encoder, Builder> fn) {        
        return installBuilder(name, partName, Encoder.Define, fn);
    }    

    public RobotBuilder GyroSensor(String partName, Function<GyroSensor, Builder> fn) {        
        return GyroSensor(partName, partName, fn);
    }
    public RobotBuilder GyroSensor(String name, String partName, Function<GyroSensor, Builder> fn) {        
        return installBuilder(name, partName, GyroSensor.Define(Manufacturer.CTRElectronics), fn);
    }    

    public RobotBuilder Pigeon2(Function<IMU, Builder> fn) {
        return Pigeon2(IMU.Pigeon2, fn);
    }
    public RobotBuilder Pigeon2(String name, Function<IMU, Builder> fn) {        
        return installBuilder(name, IMU.Pigeon2, IMU.Define(Manufacturer.CTRElectronics), fn);
    }    

    public RobotBuilder Motor(Function<Motor, Builder> fn) {
        return Motor(Motor.NAME, Motor.NAME, fn);
    }
    public RobotBuilder Motor(String name, String partName, Function<Motor, Builder> fn) {        
        return installBuilder(name, partName, Motor.Define, fn);
    }    

    public RobotBuilder MotorController(String name, Function<MotorController, Builder> fn) {        
        return MotorController(name, MotorController.NAME, fn);
    }    
    public RobotBuilder MotorController(String name, String partName, Function<MotorController, Builder> fn) {        
        return installBuilder(name, partName, MotorController.Define(Manufacturer.REVRobotics), fn);
    }    

    public RobotBuilder SwerveDrive(Function<SwerveDrive, Builder> fn) {        
        return installBuilder(SwerveDrive.NAME, SwerveDrive.NAME, SwerveDrive.Define, fn);
    }    
    public RobotBuilder SwerveModule(String name, Function<SwerveModule, Builder> fn) {        
        return installBuilder(name, SwerveModule.NAME, SwerveModule.Define, fn);
    }    

    // Basic Parts
    public RobotBuilder RoboRIO(Function<RoboRIO, Builder> fn) {
        return installBuilder(RoboRIO.NAME, RoboRIO.NAME, RoboRIO.Define, fn);
    }
    public RobotBuilder PowerDistributionHub(Function<PowerDistributionModule, Builder> fn) {
        return installBuilder(PowerDistributionModule.PDH, PowerDistributionModule.PDH,  PowerDistributionModule.DefinePDH, fn);
    }
    public RobotBuilder MiniPowerModule(Function<PowerDistributionModule, Builder> fn) {
        return MiniPowerModule(PowerDistributionModule.MPM, fn);
    }
    public RobotBuilder MiniPowerModule(String name, Function<PowerDistributionModule, Builder> fn) {
        return installBuilder(name, PowerDistributionModule.MPM, PowerDistributionModule.DefineMPM(name), fn);
    }
    public RobotBuilder PCM(Function<PneumaticsController, Builder> fn) {
        return installBuilder(PneumaticsController.PCM, PneumaticsController.PCM,  PneumaticsController.Define(Manufacturer.ReduxRobotics), fn);
    }
    public Builder Solenoid() { return Solenoid(Solenoid.NAME); }
    public Builder Solenoid(String partName) { return getInstalled(partName); }
    public RobotBuilder Solenoid(String partName, Function<Solenoid, Builder> fn) {        
        return Solenoid(partName, partName, fn);
    }
    public RobotBuilder Solenoid(String name, String partName, Function<Solenoid, Builder> fn) {        
        return installBuilder(name, partName, Solenoid.Define, fn);
    }    

    // "part" Parts
    public RobotBuilder DC(Function<Builder, Builder> fn) {
        return Part("DC-DC", fn);
    }
    public Builder Radio() { return Part("Radio"); }
    public RobotBuilder Radio(Function<Builder, Builder> fn) {
        return Part("Radio", fn);
    }
    public RobotBuilder RadioPowerModule(Function<Builder, Builder> fn) {
        return Part("RadioPowerModule", fn);
    }
    public RobotBuilder RadioBarrelJack(Function<Builder, Builder> fn) {
        return Part("RadioBarrelJack", fn);
    }
    public Builder RadioSignalLight() { return Part("RadioSignalLight"); }
    public RobotBuilder RadioSignalLight(Function<Builder, Builder> fn) {
        return Part("RadioSignalLight", fn);
    }
    public RobotBuilder EthernetSwitch(Function<Builder, Builder> fn) {
        return Part("EthernetSwitch", fn);
    }
    public RobotBuilder TimeOfFlight(Function<Builder, Builder> fn) {
        return Part("TimeOfFlight", fn);
    }
    public Builder Compressor() { return Part("Compressor"); }
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
                var builder = new Builder(getIBuild(), part);
                var canInfo = CanInfo.WrapPart(builder);
                if (canInfo.isCanDevice()) {
                    _canMap.install(builder);
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
                var builder = _partFactory.createBuilder(PowerDistributionModule.PDH);
                if (builder == null) {
                    builder = _partFactory.createBuilder(PowerDistributionModule.PDP);
                }
                pdm = installBuilder(builder);
            } 
        }
        return (PowerDistributionModule)pdm;
    }

    private <T extends Builder> Object getValue(String partName, Function<T, Object> fn) {
        var builder = getInstalled(partName);
        return fn.apply((T)builder);
    }

}
