package team1502.injection;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import edu.wpi.first.util.sendable.SendableRegistry;
import edu.wpi.first.wpilibj2.command.CommandScheduler;
import edu.wpi.first.wpilibj2.command.Subsystem;
import team1502.configuration.factory.RobotConfiguration;

public class SubsystemFactory extends RobotPart {
    public static boolean isSubsystem(Class<?> candidate) {
        return Subsystem.class.isAssignableFrom(candidate);
    }


    private Class<Subsystem> subsystemClass;
    private Subsystem subsystem;

    public Subsystem getSubsystem() {return subsystem;}
    public boolean isBuilt() { return subsystem != null; }

    public SubsystemFactory(Class<Subsystem> subsystemClass) {
        super(subsystemClass);
        this.subsystemClass = subsystemClass;
    }

    public void build(RobotConfiguration config) {
        create(config);
        if (isBuilt()) {
            CommandScheduler.getInstance().registerSubsystem(getSubsystem());
            if (isSendable()) {
                SendableRegistry.addLW(Sendable(), name, name);
            }
        }
    
    }

    public void create(RobotConfiguration config) {
        Constructor<Subsystem> ctor = (Constructor<Subsystem>)SubsystemFactory.getConstructor(subsystemClass);
        try {
            // todo: determine parameters
            subsystem = ctor.newInstance(config);
            setPart(subsystem);
        } catch (InstantiationException | IllegalAccessException | IllegalArgumentException e) {
            // Handle exception
            System.err.println(e);
        } catch (InvocationTargetException ie) {
            System.err.println(ie);
        }
    }

 
}
