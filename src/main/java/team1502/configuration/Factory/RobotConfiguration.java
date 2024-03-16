package team1502.configuration.factory;

import java.lang.module.Configuration;
import java.util.HashMap;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.DoubleSupplier;
import java.util.function.Function;

import com.ctre.phoenix6.hardware.CANcoder;
import com.ctre.phoenix6.hardware.Pigeon2;
import com.revrobotics.CANSparkMax;

import edu.wpi.first.wpilibj.DigitalInput;
import team1502.configuration.builders.*;
import team1502.configuration.builders.motors.*;
import team1502.configuration.builders.pneumatics.*;
import team1502.configuration.builders.power.*;
import team1502.configuration.builders.sensors.*;

public class RobotConfiguration {

    public String name;
    private RobotBuilder _robotBuilder;
    private Evaluator _evaluator;
    
    public static RobotConfiguration Create(String name, Function<RobotConfiguration, RobotConfiguration> fn) {
        var robot = new RobotConfiguration();
        robot.name = name;
        return fn.apply(robot);
    }

    static RobotConfiguration Create(Function<RobotConfiguration, RobotConfiguration> fn) {
        var robot = new RobotConfiguration();
        return fn.apply(robot);

    }

    RobotConfiguration() { }
    private RobotConfiguration(RobotBuilder robotBuilder) {
        _robotBuilder = robotBuilder;
    }
    private HashMap<String, String> disabledMap = new HashMap<>();
    public RobotConfiguration DisableSubsystem(Class<?> subsystemclass) {
         return DisableSubsystem(subsystemclass.getName());
    }
    public RobotConfiguration DisableSubsystem(String className) {
        disabledMap.put(className, className);
        return this;
    }
    public boolean isDisabled(String clsName) {
        return disabledMap.containsKey(clsName);
    }
    
    private HashMap<String, Object> valueMap = new HashMap<>();
    public Object getConfigValue(String valueName) { return valueMap.get(valueName); }
    public RobotConfiguration Value(String valueName, Object value) {
        if (_robotBuilder.hasSubsystemPart()) {
            _robotBuilder.Value(valueName, value); }
        else {
            valueMap.put(valueName, value);
        }
        return this;
    }    
    public double getConfigDouble(String valueName, double defaultValue) {
        var result = (Double)getConfigValue(valueName);
        return result != null ? result : defaultValue;
    }


    RobotBuilder getBuilder() {
        if (_robotBuilder == null) {
            _robotBuilder = RobotBuilder.Create();
        }
        return _robotBuilder;
    }

    private PartFactory getFactory() {
        return getBuilder().getPartFactory();
    }
    
    private Evaluator getEvaluator() {
        if (_evaluator == null) {
            _evaluator = new Evaluator(getBuilder());
        }
        return _evaluator;
    }

    //public CanMap getCanMap() {return getBuilder().getCanMap();}
    public PowerDistributionModule getPowerDistributionModule() {return getBuilder().getPowerDistributionModule();}

    public RobotConfiguration Parts(Function<PartFactory, PartFactory> fn) {
        fn.apply(getFactory());
        return this;
    }
    
    public RobotConfiguration Build(Function<RobotBuilder, RobotBuilder> fn) {
        fn.apply(getBuilder());
        return this;
    }

    public RobotConfiguration PowerDistributionModule(Function<PowerDistributionModule, Builder> fn) {
        fn.apply(getPowerDistributionModule());
        return this;
    }

    public Evaluator Values() {
        return getEvaluator();
    }

    public RobotConfiguration Values(Function<Evaluator, Evaluator> fn) {
        fn.apply(getEvaluator());
        return this;
    }

    public Object getValue(String valueName) {
        return Values().getValue(valueName);
    }

    public Object getValue(String valueName, String partName) {
        return Values().getValue(valueName, partName);
    }

    public <T> T Eval(Function<Evaluator,T> fn) {
        return Values().Eval(fn);
    }

    public Builder Part(String name) { return Values().Part(name); }
    public Builder findPart(String name) { return _robotBuilder.findInstalled(name); }

    public RobotConfiguration findSubsystemConfiguration(String partName) {
        var subsystem = findPart(partName);
        if (subsystem != null && subsystem.hasValue("robotBuilder")){
            return new RobotConfiguration((RobotBuilder)subsystem.Value("robotBuilder"));
        }
        return null;
    }

    public RobotConfiguration Subsystem(Class<?> subsystemClass) { return Subsystem(subsystemClass.getName()); }
    public RobotConfiguration Subsystem(String partName) { return new RobotConfiguration((RobotBuilder)Part(partName).Value("robotBuilder")); }
    public Object Value(String valueName) { return _robotBuilder.hasSubsystemPart() ? _robotBuilder.getSubsystemPart().Value(valueName) : getConfigValue(valueName); }
    public Double getDouble(String valueName, double defaultValue) { return _robotBuilder.hasSubsystemPart() ? _robotBuilder.getSubsystemPart().getDouble(valueName, defaultValue) : getConfigDouble(valueName, defaultValue); }
    public boolean hasValue(String valueName) { return _robotBuilder.hasSubsystemPart() ? _robotBuilder.getSubsystemPart().hasValue(valueName) : valueMap.containsKey(valueName); }

    public MotorController MotorController() { return Values().MotorController(); }
    public MotorController MotorController(String name) { return Values().MotorController(name); }
    public Encoder Encoder() { return Encoder(Encoder.CLASSNAME); }
    public Encoder Encoder(String name) { return Values().Encoder(name); }
    public Solenoid Solenoid(String name) { return Solenoid.WrapPart(_robotBuilder.getSubsystemPart(), name); }

    public edu.wpi.first.wpilibj.DigitalInput DigitalInput(String name) { 
        return new DigitalInput(_robotBuilder.getSubsystemPart().getPart(name).getInt(RoboRIO.digitalInput));
    }

    public Chassis Chassis() { return Values().SwerveDrive().Chassis(); }
    public SwerveDrive SwerveDrive() { return Values().SwerveDrive(); }
    public SwerveModule SwerveModule(String name) { return Values().SwerveDrive().SwerveModule(name); }
    public PowerDistributionModule MPM(String name) { return Values().MPM(name); }
    public PowerDistributionModule PDH() { return Values().PDH(); }
    public IMU Pigeon2() { return Values().Pigeon2(); }
    public GyroSensor GyroSensor() { return Values().GyroSensor(); }
    public GyroSensor GyroSensor(String name) { return (GyroSensor)getValue(name); }
    public Builder RadioPowerModule() { return Values().RadioPowerModule(); }
    public RoboRIO RoboRIO() { return Values().RoboRIO(); }
    public Builder RadioBarrelJack() { return Values().RadioBarrelJack(); }
    public Builder EthernetSwitch() { return Values().EthernetSwitch(); }
    public PneumaticsController PCM() { return Values().PCM(); }

    public void registerLoggerObjects(
            BiConsumer<String, CANSparkMax> motorLogger,
            Consumer<Pigeon2> pigeonLogger,
            BiConsumer<String, CANcoder> encoderLogger,
            BiConsumer<String, DoubleSupplier> sensorLogger
        ) {

        for (SwerveModule sm : SwerveDrive().getModules()) {
            var drive = sm.DrivingMotor();
            motorLogger.accept(drive.FriendlyName() + " Drive", drive.CANSparkMax());    
        }
        for (SwerveModule sm : SwerveDrive().getModules()) {
            var drive = sm.TurningMotor();
            motorLogger.accept(drive.FriendlyName() + " Turn", drive.CANSparkMax());    
        }
        
        pigeonLogger.accept(Pigeon2().Pigeon2());

        for (SwerveModule sm : SwerveDrive().getModules()) {
            var drive = sm.Encoder();
            encoderLogger.accept(drive.FriendlyName() + " Abs", drive.CANcoder());    
        }

        for (SwerveModule sm : SwerveDrive().getModules()) {
            sensorLogger.accept(sm.Abbreviation() + " Drive Speed", ()->sm.getSwerveModuleInstance().getVelocity());    
        }
    }

}
