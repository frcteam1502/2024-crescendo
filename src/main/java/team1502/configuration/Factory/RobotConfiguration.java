package team1502.configuration.factory;

import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.DoubleSupplier;
import java.util.function.Function;

import com.ctre.phoenix6.hardware.CANcoder;
import com.ctre.phoenix6.hardware.Pigeon2;
import com.revrobotics.CANSparkMax;

import team1502.configuration.CAN.CanMap;
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

    private RobotBuilder getBuilder() {
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

    public CanMap getCanMap() {return getBuilder().getCanMap();}
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
        return (T)Values().Eval(fn);
    }

    public Builder Part(String name) { return Values().Part(name); }


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

    public void RegisterCanSparkMaxs(
            BiConsumer<String, CANSparkMax> motorLogger,
            BiConsumer<String, Pigeon2> pigeonLogger,
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
        
        pigeonLogger.accept(Pigeon2().FriendlyName(), Pigeon2().Pigeon2());

        for (SwerveModule sm : SwerveDrive().getModules()) {
            var drive = sm.Encoder();
            encoderLogger.accept(drive.FriendlyName() + " Abs", drive.CANcoder());    
        }

        for (SwerveModule sm : SwerveDrive().getModules()) {
            sensorLogger.accept(sm.Abbreviation() + " Drive Speed", ()->sm.getSwerveModuleInstance().getVelocity());    
        }
    }

}
