package team1502.injection;

import edu.wpi.first.wpilibj2.command.Command;

public class CommandFactory extends RobotPart {
    public static boolean isSubsystem(Class<?> candidate) {
        return Command.class.isAssignableFrom(candidate);
    }

    public Command getCommand() {return (Command)part;}

    public CommandFactory(Class<Command> commandClass) {
        super(commandClass);
    }
    
}
