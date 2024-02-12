package team1502.configuration.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import edu.wpi.first.wpilibj2.command.Command;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface SubsystemInfo {
    boolean disabled() default false;
    Class<? extends Command> defaultCommand();
}
