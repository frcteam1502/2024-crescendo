package team1502.configuration.builders.pneumatics;

import java.util.function.Function;

import edu.wpi.first.wpilibj.PneumaticsModuleType;
import team1502.configuration.CAN.CanInfo;
import team1502.configuration.builders.Builder;
import team1502.configuration.builders.IBuild;
import team1502.configuration.builders.Part;
import team1502.configuration.builders.power.PowerChannel;

public class Solenoid extends Builder {
    public static final String NAME = "Solenoid";

    public static Function<IBuild, Solenoid> Define = build->new Solenoid(build);
    public static Solenoid Wrap(Builder builder) { return new Solenoid(builder.getIBuild(), builder.getPart()); }
    public static Solenoid WrapPart(Builder builder) { return WrapPart(builder, NAME); }
    public static Solenoid WrapPart(Builder builder, String partName) { return Wrap(builder.getPart(partName)); }

    public Solenoid(IBuild build) { super(build, NAME); }
    public Solenoid(IBuild build, Part part) { super(build, part); }
  
    // public Integer DigitalInput() { return getInt(PneumaticsController.RPH); }
    // public Solenoid DigitalInput(int channel) {
    //     Value(PneumaticsController.RPH, channel);
    //     return this;
    // }

    public edu.wpi.first.wpilibj.Solenoid buildSolenoid() {
        var ch = PowerChannel.findConnectedChannel(this);
        int channel = ch.Channel();
        int module = CanInfo.findConnection(this.getParent()).CanNumber();
        return Solenoid(new edu.wpi.first.wpilibj.Solenoid(7, PneumaticsModuleType.REVPH, channel));
    }

    public edu.wpi.first.wpilibj.Solenoid Solenoid() {
        return (edu.wpi.first.wpilibj.Solenoid)Value(PneumaticsModuleType.REVPH.toString());
    }
    public edu.wpi.first.wpilibj.Solenoid Solenoid(edu.wpi.first.wpilibj.Solenoid solenoid) {
        Value(PneumaticsModuleType.REVPH.toString(), solenoid);
        return solenoid;
    }

}
