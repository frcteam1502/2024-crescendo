package team1502.configuration.factory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.function.Function;

import team1502.configuration.CAN.Manufacturer;
import team1502.configuration.builders.*;
import team1502.configuration.builders.motors.*;
import team1502.configuration.builders.pneumatics.*;
import team1502.configuration.builders.power.*;
import team1502.configuration.builders.sensors.*;

public class RobotBuilder implements IBuild /*extends Builder*/{
    private PartFactory _partFactory;
    private HashMap<String, Builder> _buildMap = new HashMap<>(); // built top-level parts wrapped in builder
    private HashMap<String, RobotBuilder> _subsystemMap = new HashMap<>(); // subsystems
    private ArrayList<Part> _parts = new ArrayList<>(); // every part created
        
    private RobotBuilder() {
        _partFactory = new PartFactory(getIBuild());
    }
    
    private RobotBuilder _parent;
    private Builder _subsystemPart;
    public Builder getPart() { return _subsystemPart; }

    //private Part _part;
    private RobotBuilder(RobotBuilder parent, String name) {
        // todo, look for existing part "sub-factory"
        _parent = parent;
        _parent._subsystemMap.put(name, this);
        _partFactory = new PartFactory(_parent.getPartFactory(), getIBuild());
        _subsystemPart= Builder.DefineAs("Subsystem").apply(parent); // ??
        _subsystemPart.Name(name);
        _subsystemPart.setIBuild(this);
        _subsystemPart.Value("robotBuilder", this);
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

    /** look for top-level parts */
    public Builder findInstalled(String name) { return _buildMap.get(name); }

    @Override // IBuild
    public Builder getInstalled(String name) {
        var installed =_buildMap.get(name);
        return installed != null
            ? installed
            : _parent != null
                ? _parent.getInstalled(name)
                : findPart(name);
    }
    public Builder getInstalledType(String className) {
        var installed =_buildMap.get(className);
        if (installed == null) {
            var parts = _parts.stream()
                .filter(part->(part.getType() == className))
                .toList();
            if (parts.size() != 0) {
                if (parts.size() == 1) {
                     installed = new Builder(this, parts.get(0)); 
                } else {
                    parts = parts.stream().filter(p -> _buildMap.containsKey(p.getName())).toList();
                    if (parts.size() == 1) {
                        installed = new Builder(this, parts.get(0)); 
                    }
                }
            }
        }
        return installed;
    }

    @Override // IBuild
    public <T extends Builder> PartBuilder<T> getTemplate(String partName, Function<IBuild, T> createFunction,
            Function<T, Builder> buildFunction) {
        return _partFactory.getTemplate(partName, createFunction, buildFunction);
    }

    @Override // IBuild
    public void register(Part part) {
        _parts.add(part);
        if (_parent != null) {
            _parent.register(part);
        } else {
            part.setValue("created", _parts.size()); // in case they should be ordered by creation
        }
    }

    private <T extends Builder> RobotBuilder installBuilder(String name, String partName, Function<IBuild, T> createFunction, Function<T, Builder> buildFunction) {
        var template = _partFactory.getTemplate(partName, createFunction, buildFunction);
        T builder = _subsystemPart == null
            ? template.createBuilder(getIBuild(), name)
            : template.addBuilder(_subsystemPart, name);
        install(builder);
        return this;
    }

    public void install(Builder builder) {
        _buildMap.put(builder.getName(), builder);
        if (_subsystemPart != null) {
             var parent = builder.getParent();
             if (parent != null && parent.getPart() == _subsystemPart.getPart()) {
                _subsystemPart.addPart(builder);
                if (!builder.hasValue(Part.friendlyName)) {
                    builder.FriendlyName(_subsystemPart.Name() + " " + builder.Name());
                }
            }
        }
    }

    Part findLocalPart(String name) {
        for (Part part : _parts) {
            if (part.getName() == name) { return part; }
        }
        return null;
    }

    public Builder findPart(String name) {
        var part = findLocalPart(name);
        if (part == null) {
            if (_parent != null) { // normally only runs at top level
                return _parent.findPart(name);
            }
            return null;
        }
        return new Builder(this, part);
    }

    public RobotBuilder UsePart(String key) {
        _subsystemPart.Value(key, getInstalled(key).getPart());
        return this;
    }
    RobotBuilder usePart(Builder user, String key, Function<RobotBuilder, Builder> path) {
        user.Value(key, path.apply(this).getPart());
        return this;
    }

    // BUILDER AND BUILDER SUBCLASSES
    public Object Value(String valueName) { return _subsystemPart.Value(valueName); }
    public RobotBuilder Value(String valueName, Object value) {
        _subsystemPart.Value(valueName, value); 
        return this;
    }    

    public RobotBuilder Note(String note) {
        Note = note;
        return this;
    }
    public Builder Part(String partName) { return getInstalled(partName); }
    public RobotBuilder Part(String partName, Function<Builder, Builder> fn) {
        return Part(partName, partName, fn);
    }
    public RobotBuilder Part(String name, String partName, Function<Builder, Builder> fn) {
        return installBuilder(name, partName, Builder.DefineAs(partName), fn);
    }

    public RobotBuilder Subsystem(String partName) { return (RobotBuilder)getInstalled(partName).Value("robotBuilder"); }
    public RobotBuilder Subsystem(Class<?> subsystemClass, Function<RobotBuilder, RobotBuilder> fn) {
        return Subsystem(subsystemClass.getName(), fn);
    }
    public RobotBuilder Subsystem(String partName, Function<RobotBuilder, RobotBuilder> fn) {
        var child = new RobotBuilder(this, partName);
        if (_subsystemPart != null) {
            child.getPart().getPart().setParent(_subsystemPart.getPart());
        }
        var subsystem = fn.apply(child); //Part(partName, partName, fn);
        install(subsystem._subsystemPart);
        return this;
    }

    public Builder Encoder() { return Encoder(Encoder.CLASSNAME); }
    public Builder Encoder(String partName) { return getInstalled(partName); }
    public RobotBuilder Encoder(Function<Encoder, Builder> fn) {  return Encoder(Encoder.CLASSNAME, fn); }
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
        return Motor(Motor.CLASSNAME, Motor.CLASSNAME, fn);
    }
    public RobotBuilder Motor(String name, String partName, Function<Motor, Builder> fn) {        
        return installBuilder(name, partName, Motor.Define, fn);
    }    

    public RobotBuilder MotorController(String name, Function<MotorController, Builder> fn) {        
        return MotorController(name, MotorController.CLASSNAME, fn);
    }    
    public RobotBuilder MotorController(String name, String partName, Function<MotorController, Builder> fn) {        
        return installBuilder(name, partName, MotorController.Define(Manufacturer.REVRobotics), fn);
    }    

    public RobotBuilder SwerveDrive(Function<SwerveDrive, Builder> fn) {        
        return installBuilder(SwerveDrive.CLASSNAME, SwerveDrive.CLASSNAME, SwerveDrive.Define, fn);
    }    
    public RobotBuilder SwerveModule(String name, Function<SwerveModule, Builder> fn) {        
        return installBuilder(name, SwerveModule.CLASSNAME, SwerveModule.Define, fn);
    }    

    // Basic Parts
    public RobotBuilder RoboRIO(Function<RoboRIO, Builder> fn) {
        return installBuilder(RoboRIO.CLASSNAME, RoboRIO.CLASSNAME, RoboRIO.Define, fn);
    }
    public RobotBuilder PowerDistributionHub(Function<PowerDistributionModule, Builder> fn) {
        return installBuilder(PowerDistributionModule.PDH, PowerDistributionModule.PDH,  PowerDistributionModule.DefinePDH, fn);
    }
    public RobotBuilder MiniPowerModule(Function<PowerDistributionModule, Builder> fn) {
        return MiniPowerModule(PowerDistributionModule.MPM, fn);
    }
    public RobotBuilder MiniPowerModule(String name, Function<PowerDistributionModule, Builder> fn) {
        return installBuilder(name, PowerDistributionModule.MPM, PowerDistributionModule.DefineMPM, fn);
    }
    public PneumaticsController PCM() { return (PneumaticsController)getInstalled(PneumaticsController.PCM); }
    public RobotBuilder PCM(Function<PneumaticsController, Builder> fn) {
        return installBuilder(PneumaticsController.PCM, PneumaticsController.PCM,  PneumaticsController.Define(Manufacturer.REVRobotics), fn);
    }
    public Builder Solenoid() { return Solenoid(Solenoid.CLASSNAME); }
    public Builder Solenoid(String partName) { return getInstalled(partName); }
    public RobotBuilder Solenoid(String partName, Function<Solenoid, Builder> fn) {        
        return Solenoid(partName, partName, fn);
    }
    public RobotBuilder Solenoid(String name, String partName, Function<Solenoid, Builder> fn) {        
        return installBuilder(name, partName, Solenoid.Define, fn);
    }    
    
    public RobotBuilder DigitalInput(String name, Integer channel, Function<Builder, Builder> fn) {
        var abs = _subsystemPart.addConnector(Channel.SIGNAL_DIO, name);
        abs.Value(RoboRIO.digitalInput, channel);
        abs.connectToChannel(RoboRIO.CLASSNAME, channel);
        return this;
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
     */
    public PowerDistributionModule getPowerDistributionModule() {
        var pdm = getInstalled(PowerDistributionModule.PDH);
        if (pdm == null) {
            pdm = getInstalled(PowerDistributionModule.PDP);
            // if (pdm == null) {
            //     var builder = _partFactory.createBuilder(PowerDistributionModule.PDH);
            //     if (builder == null) {
            //         builder = _partFactory.createBuilder(PowerDistributionModule.PDP);
            //     }
            //     pdm = installBuilder(builder);
            // } 
        }
        return (PowerDistributionModule)pdm;
    }

    private <T extends Builder> Object getValue(String partName, Function<T, Object> fn) {
        var builder = getInstalled(partName);
        return fn.apply((T)builder);
    }

}
