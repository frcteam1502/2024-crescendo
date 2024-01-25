package team1502.configuration;

import java.util.function.Function;

import team1502.configuration.Builders.RobotBuilder;
import team1502.configuration.Builders.Controllers.GyroSensor;
import team1502.configuration.CAN.CanMap;
import team1502.configuration.Factory.PartFactory;

public class RobotConfiguration {

    public String name;
    private PartFactory _partFactory = new PartFactory();
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
            _robotBuilder = RobotBuilder.Create(_partFactory);
        }
        return _robotBuilder;
    }
    
    private Evaluator getEvaluator() {
        if (_evaluator == null) {
            _evaluator = new Evaluator(getBuilder());
        }
        return _evaluator;
    }

    public CanMap getCanMap() {return _robotBuilder.getCanMap();}

    public RobotConfiguration Parts(Function<PartFactory, PartFactory> fn) {
        fn.apply(_partFactory);
        return this;
    }
    
    public RobotConfiguration Build(Function<RobotBuilder, RobotBuilder> fn) {
        fn.apply(getBuilder());
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


    public GyroSensor GyroSensor(String name) {return (GyroSensor)getValue(name);}
}
