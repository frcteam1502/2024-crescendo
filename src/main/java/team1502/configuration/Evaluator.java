package team1502.configuration;

import java.util.HashMap;
import java.util.function.Function;

import team1502.configuration.Builders.Builder;
import team1502.configuration.Builders.Motor;
import team1502.configuration.Builders.RobotBuilder;
import team1502.configuration.Builders.SwerveDrive;
import team1502.configuration.Builders.SwerveModule;
import team1502.configuration.Builders.Controllers.GyroSensor;
import team1502.configuration.Builders.Controllers.MotorController;

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

    private Object Eval(EvaluatorArgs args) {
        this.args = args;
        var result = Eval(args.function);
        this.args = null;
        return result;
    }
    // Ad hoc evaluate
    public Object Eval(Function<Evaluator,? extends Object> fn) {
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

    public Object getValue(String valueName) {
        return Eval(_valueMap.get(valueName));
    }

    private Builder getInstalled(String partName) {
        return _configuration.getInstalled(partName);
    }

    private <T extends Builder> Object getValue(String partName, T builder, Function<T, ? extends Object> fn) {
        var susBuilder = getInstalled(partName);
        return susBuilder.evalWith(fn, builder);
    
    }
    // private <T extends Builder> Object getValue(String partName, Function<T, ? extends Object> fn) {
    //     var builder = getInstalled(partName);
    //     return fn.apply((T)builder);
    // }

    public Object Part(String partName, Function<Builder, Object> fn) {
        return getValue(partName, new Builder(), fn);   
    }
    public GyroSensor GyroSensor() {
        return (GyroSensor)getValue("Gyro", new GyroSensor(), g->(GyroSensor)g);   
    }
    public Object GyroSensor(String partName, Function<GyroSensor, Object> fn) {
        return getValue(partName, new GyroSensor(), fn);   
    }
    public Object Motor(String partName, Function<Motor, Object> fn) {
        return getValue(partName, new Motor(), fn);   
    }
    public Object MotorController(String partName, Function<MotorController, Object> fn) {
        return getValue(partName, new MotorController(), fn);   
    }
    public Object SwerveModule(String partName, Function<SwerveModule, Object> fn) {
        return getValue(partName, new SwerveModule(), fn);   
    }

    public <T extends Object> T SwerveDrive(Function<SwerveDrive, T> fn) {
        return (T)getValue("SwerveDrive", new SwerveDrive(), fn);   
    }

}
