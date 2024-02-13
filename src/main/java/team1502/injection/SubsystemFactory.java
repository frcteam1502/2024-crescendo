package team1502.injection;

import edu.wpi.first.util.sendable.SendableRegistry;
import edu.wpi.first.wpilibj2.command.CommandScheduler;
import edu.wpi.first.wpilibj2.command.Subsystem;
import edu.wpi.first.wpilibj2.command.SubsystemBase;

public class SubsystemFactory extends RobotPart {
    public static boolean isSubsystem(Class<?> candidate) {
        return candidate == null 
            ? false 
            : Subsystem.class.isAssignableFrom(candidate);
    }

    public static boolean isSubsystemBase(Class<?> candidate) {
        return candidate == null 
            ? false 
            : SubsystemBase.class.isAssignableFrom(candidate);
    }


    public Subsystem getSubsystem() { return (Subsystem)part; }

    public SubsystemFactory(Class<Subsystem> subsystemClass) {
        super(subsystemClass);
    }

    @Override
    public void onBuilt() {
        if (isBuilt()) {
            if (!isSubsystemBase(this.getPartClass())) { // do the standard thing base does
                CommandScheduler.getInstance().registerSubsystem(getSubsystem());
                if (isSendable()) {
                    SendableRegistry.addLW(Sendable(), getName(), getName());
                }
                // why not use base? for testability/mocks - may want/need to avoid going deep into wpilib
                // why use base? easier, (any) base may also help order things during ctor
            }
        }
    }
}
