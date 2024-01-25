package team1502.injection;

import java.lang.reflect.Constructor;

import edu.wpi.first.util.sendable.Sendable;

import team1502.configuration.RobotConfiguration;
import team1502.configuration.annotations.SubsystemInfo;

public class RobotPart {
    public static Class<SubsystemInfo> subsystemAnnotation = SubsystemInfo.class;

    public static boolean isSendable(Class<?> candidate) {
        return Sendable.class.isAssignableFrom(candidate);
    }

    public static Class<?>[] getInterfaces(Class<?> partClass) {
        return partClass.getInterfaces();
    } 
    
    public static Constructor<?> getConstructor(Class<?> partClass) {
        Constructor<?> constructor = null;
        Constructor<?>[] constructors = partClass.getDeclaredConstructors(); // get the array of constructors
        for (Constructor<?> candidate : constructors) {
            Class<?>[] parameterTypes = candidate.getParameterTypes(); // get the array of parameter types
            System.out.println(candidate); // print the constructor
            for (Class<?> p : parameterTypes) {
                if (p.equals(RobotConfiguration.class)) {
                    constructor = candidate;
                }
            }
        }
        return constructor;
    }

    private final Class<?> partClass;
    private final SubsystemInfo annotation;
    private Object part;

    public String simpleName;
    public String name;

    public RobotPart(Class<?> partClass) {
        this.partClass = partClass;
        simpleName = partClass.getSimpleName();
        name = simpleName.substring(simpleName.lastIndexOf('.') + 1);     
        annotation = partClass.getDeclaredAnnotation(subsystemAnnotation);
    }

    public boolean isEnabled() { return annotation == null ? true : !annotation.disabled(); }
    public boolean isSendable() { return isSendable(partClass); }
    public Sendable Sendable() { return (Sendable)part; }

    protected void setPart(Object part) { this.part = part; }
}
