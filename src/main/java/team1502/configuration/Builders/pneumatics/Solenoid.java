package team1502.configuration.builders.pneumatics;

import java.util.function.Function;

import edu.wpi.first.wpilibj.PneumaticsModuleType;
import team1502.configuration.builders.Builder;
import team1502.configuration.builders.IBuild;
import team1502.configuration.builders.Part;
import team1502.configuration.builders.power.PowerChannel;

public class Solenoid extends Builder {
    public static final String CLASSNAME = "Solenoid";

    public static Function<IBuild, Solenoid> Define = build->new Solenoid(build);
    public static Solenoid Wrap(Builder builder) { return builder == null ? null : new Solenoid(builder.getIBuild(), builder.getPart()); }
    public static Solenoid WrapPart(Builder builder) { return WrapPart(builder, CLASSNAME); }
    public static Solenoid WrapPart(Builder builder, String partName) { return Wrap(builder.getPart(partName)); }

    public Solenoid(IBuild build) { super(build, CLASSNAME); }
    public Solenoid(IBuild build, Part part) { super(build, part); }
  
    public edu.wpi.first.wpilibj.Solenoid buildSolenoid() {
        var ch = PowerChannel.findConnectedChannel(this);
        var pcm = PneumaticsController.Wrap(ch.Host());
        return Solenoid(pcm.buildSolenoid(ch.Channel()));
    }

    public edu.wpi.first.wpilibj.Solenoid Solenoid() {
        return (edu.wpi.first.wpilibj.Solenoid)Value(PneumaticsModuleType.REVPH.toString());
    }
    public edu.wpi.first.wpilibj.Solenoid Solenoid(edu.wpi.first.wpilibj.Solenoid solenoid) {
        Value(PneumaticsModuleType.REVPH.toString(), solenoid);
        return solenoid;
    }

}
