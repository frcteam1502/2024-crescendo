package team1502.injection;

import edu.wpi.first.util.sendable.SendableRegistry;
import edu.wpi.first.wpilibj2.command.CommandScheduler;
import edu.wpi.first.wpilibj2.command.Subsystem;

public class SubsystemFactory extends RobotPart {
    public static boolean isSubsystem(Class<?> candidate) {
        return candidate == null 
            ? false 
            : Subsystem.class.isAssignableFrom(candidate);
    }

    public Subsystem getSubsystem() { return (Subsystem)part; }

    public SubsystemFactory(Class<Subsystem> subsystemClass) {
        super(subsystemClass);
    }

    @Override
    public void onBuilt() {
        if (isBuilt()) {
            CommandScheduler.getInstance().registerSubsystem(getSubsystem());
            if (isSendable()) {
                SendableRegistry.addLW(Sendable(), getName(), getName());
            }
        }
    }
}
