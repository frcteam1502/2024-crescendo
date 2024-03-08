package team1502.configuration.factory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.function.Function;

import team1502.configuration.builders.Builder;
import team1502.configuration.builders.RoboRIO;
import team1502.configuration.builders.motors.*;
import team1502.configuration.builders.pneumatics.*;
import team1502.configuration.builders.power.*;
import team1502.configuration.builders.sensors.*;

public class Evaluator {
    private HashMap<String, EvaluatorArgs> _valueMap = new HashMap<>(); 
    private RobotBuilder _configuration;
    public EvaluatorArgs args;
    
    public String partName() {
        return args.partName;
    }
    public Evaluator(RobotBuilder configuration) {
        _configuration = configuration;
    }

    public Evaluator Subsystem(Class<?> subsystemClass) { return Subsystem(subsystemClass.getName()); }
    public Evaluator Subsystem(String name) {
        return new Evaluator(_configuration.Subsystem(name));
    }
    public Builder SubsystemPart(Class<?> subsystemClass) { return SubsystemPart(subsystemClass.getName()); }
    public Builder SubsystemPart(String name) {
        return _configuration.Subsystem(name).getSubsystemPart();
    }
    private Object Eval(EvaluatorArgs args) {
        this.args = args;
        var result = Eval(args.function);
        this.args = null;
        return result;
    }
    // Ad hoc evaluate
    public <U extends Object> U Eval(Function<Evaluator, U> fn) {
        return fn.apply(this);
    }
    
    public <T extends Builder> Object Eval(T builder, Function<T,Object> fn) {
        return fn.apply(builder);
    }

    // Define
    public Evaluator Eval(String valueName, Function<Evaluator,Object> fn) {
        _valueMap.put(valueName, new EvaluatorArgs(valueName, fn));
        return this;
    }

    // Execute stored eval
    public Object getValue(String valueName, String partName) {
        var args = _valueMap.get(valueName);
        args.partName = partName;
        return Eval(args);
    }
    public ArrayList<String> GetValueKeys() {
        var list = new ArrayList<String>(_valueMap.keySet());
        Collections.sort(list);
        return list;
        
    }
    public Object getValue(String valueName) {
        return Eval(_valueMap.get(valueName));
    }

    private Builder getInstalled(String partName) {
        return _configuration.getInstalled(partName);
    }

    // private <T extends Builder> Object getValue(String partName, Function<Builder, T> wrapper, Function<T, ? extends Object> fn) {
    //     var susBuilder = getInstalled(partName);
    //     var builder = wrapper.apply(susBuilder);
    //     return fn.apply(builder);    
    // }
    private <T extends Builder, U> U getValue(String partName, Function<Builder, T> wrapper, Function<T, U> fn) {
        var susBuilder = getInstalled(partName);
        var builder = wrapper.apply(susBuilder);
        return (U)fn.apply(builder);    
    }
    private <T extends Builder, U> T getType(String className, Function<Builder, T> wrapper) {
        var found = _configuration.getInstalledType(MotorController.CLASSNAME);
        return wrapper.apply(found);
    }

    // private <T extends Builder> Object getValue(String partName, Function<T, ? extends Object> fn) {
    //     var builder = getInstalled(partName);
    //     return fn.apply((T)builder);
    // }


    public Builder Part(String partName) {
        return getValue(partName, b->Builder.Wrap(b), b->b);   
    }
    public <U> U Part(String partName, Function<Builder, U> fn) {
        return getValue(partName, b->Builder.Wrap(b), fn);   
    }
    public IMU Pigeon2() {
        return getValue(IMU.Pigeon2, b->IMU.Wrap(b), g->g);   
    }
    public Encoder Encoder() { return Encoder(Encoder.CLASSNAME); }
    public Encoder Encoder(String name) {
        return getValue(name, b->Encoder.Wrap(b), g->g);   
    }
    public GyroSensor GyroSensor() {
        return getValue(GyroSensor.Gyro, b->GyroSensor.Wrap(b), g->g);   
    }
    public <U> U GyroSensor(String partName, Function<GyroSensor, U> fn) {
        return getValue(partName, b->GyroSensor.Wrap(b), fn);   
    }
    public <U> U Motor(String partName, Function<Motor, U> fn) {
        return getValue(partName, b->Motor.Wrap(b), fn);   
    }
    public MotorController MotorController() {
        return getType(MotorController.CLASSNAME, b->MotorController.Wrap(b));
    }
    public MotorController MotorController(String partName) {
        return getValue(partName, b->MotorController.Wrap(b), mc->mc);   
    }
    public <U> U MotorController(String partName, Function<MotorController, U> fn) {
        return getValue(partName, b->MotorController.Wrap(b), fn);   
    }
    public <U> U SwerveModule(String partName, Function<SwerveModule, U> fn) {
        return getValue(partName, b->SwerveModule.Wrap(b), fn);   
    }
    public PowerDistributionModule MPM(String partName) {
        return getValue(partName, b->PowerDistributionModule.Wrap(b), p->p);   
    }
    public RoboRIO RoboRIO() {
        return (RoboRIO)getValue(RoboRIO.CLASSNAME, b->RoboRIO.Wrap(b), p->p);   
    }
    public Builder EthernetSwitch() {return Part("EthernetSwitch"); }
    public Builder RadioPowerModule() { return Part("RadioPowerModule"); }
    public Builder RadioBarrelJack() { return Part("RadioBarrelJack"); }

    public PneumaticsController PCM() {
        return getValue(PneumaticsController.PCM, b->PneumaticsController.Wrap(b), p->p);   
    }
    public PowerDistributionModule PDH() {
        return getValue(PowerDistributionModule.PDH, b->PowerDistributionModule.Wrap(b), p->p);
    }

    public SwerveDrive SwerveDrive() {return SwerveDrive(d->d); }

    public <T extends Object> T SwerveDrive(Function<SwerveDrive, T> fn) {
        return (T)getValue(SwerveDrive.CLASSNAME, b->SwerveDrive.Wrap(b), fn);   
    }

}
