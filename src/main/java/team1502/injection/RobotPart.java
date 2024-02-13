package team1502.injection;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import edu.wpi.first.util.sendable.Sendable;
import edu.wpi.first.wpilibj2.command.Command;
import team1502.configuration.annotations.*;
import team1502.configuration.factory.RobotConfiguration;

public class RobotPart {
    public static Class<SubsystemInfo> subsystemAnnotation = SubsystemInfo.class;
    public static Class<DefaultCommand> defaultCommandAnnotation = DefaultCommand.class;

    public static boolean isSendable(Class<?> candidate) {
        return Sendable.class.isAssignableFrom(candidate);
    }

    public static Class<?>[] getInterfaces(Class<?> partClass) {
        return partClass.getInterfaces();
    } 
    
    // It would be useful to enforce 1 public constructor to simplify this
    public static Constructor<?> getConstructor(Class<?> partClass) {
        Constructor<?> constructor = null;
        //Constructor<?>[] constructors = partClass.getDeclaredConstructors(); // get the array of constructors
        Constructor<?>[] constructors = partClass.getConstructors(); // get the array of public constructors
        for (Constructor<?> candidate : constructors) {
            if (constructor == null) { constructor = candidate; } // pick one
            Class<?>[] parameterTypes = candidate.getParameterTypes(); // get the array of parameter types
            System.out.println(candidate); // print the constructor
            for (Class<?> p : parameterTypes) {
                if (p.equals(RobotConfiguration.class)) {
                    constructor = candidate; // if more than one take the one with config
                }
            }
        }
        return constructor;
    }

    private final Class<?> partClass;
    private final SubsystemInfo subsystemInfo;
    private final DefaultCommand defaultCommand;
    private boolean disabled;
    protected Object part;

    protected Constructor<?> ctor;
    private String simpleName;
    private String name;
    private Class<?>[] dependencies;

    public RobotPart(Class<?> partClass) {
        this.partClass = partClass;
        simpleName = partClass.getSimpleName();
        name = partClass.getName(); // simpleName.substring(simpleName.lastIndexOf('.') + 1);     
        subsystemInfo = partClass.getDeclaredAnnotation(subsystemAnnotation);
        defaultCommand = partClass.getDeclaredAnnotation(defaultCommandAnnotation);
        disabled = subsystemInfo == null ? false : subsystemInfo.disabled();
        ctor = getConstructor(partClass);
        dependencies = ctor.getParameterTypes();
    }

    public <T> RobotPart(T robotPart) {
        part = robotPart;
        partClass = part.getClass();
        simpleName = partClass.getSimpleName();
        name = partClass.getName(); // simpleName.substring(simpleName.lastIndexOf('.') + 1);     
        subsystemInfo = partClass.getDeclaredAnnotation(subsystemAnnotation);
        defaultCommand = partClass.getDeclaredAnnotation(defaultCommandAnnotation);
        disabled = subsystemInfo == null ? false : subsystemInfo.disabled();
    }

    public String getName() {return name; };
    
    public boolean isEnabled() { return !disabled; }
    public boolean isDisabled() { return disabled; }
    public void Disable() { disabled = true; }

    public boolean isSendable() { return isSendable(partClass); }
    public Sendable Sendable() { return (Sendable)part; }

    public boolean isBuilt() { return part != null; }
    protected void setPart(Object part) { this.part = part; }
    public Object getPart() { return part; }
    public boolean hasDefaultCommand() { return defaultCommand != null; }
    public Class<? extends Command> getDefaultCommand() { return defaultCommand.command(); }
    public Class<?>[] getDependencies() { return dependencies; }
    
    public Object Build(Object... args) { // throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        try {
            part = ctor.newInstance(args);
            onBuilt();
        } catch (InstantiationException | IllegalAccessException | IllegalArgumentException
                | InvocationTargetException e) {
            Disable(); // cannot build
            System.err.println(e.toString());
            e.printStackTrace();
        }
        return part;
    }

    protected void onBuilt() { }

    protected void afterBuilt() {
    }
}
