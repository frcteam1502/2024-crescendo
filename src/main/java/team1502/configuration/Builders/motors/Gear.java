package team1502.configuration.builders.motors;

import java.util.function.Function;

import team1502.configuration.builders.Builder;
import team1502.configuration.builders.IBuild;
import team1502.configuration.builders.Part;

public class Gear extends Builder {
    public static final String NAME = "Gear";
    public static final String drivingTeeth = "drivingTeeth";
    public static final String drivenTeeth = "drivenTeeth";
    public static Function<IBuild, Gear> Define = b->new Gear(b);
    public static Gear Wrap(Builder builder) { return new Gear(builder.getIBuild(), builder.getPart()); }
    public static Gear WrapPart(Builder builder) { return WrapPart(builder, NAME); }
    public static Gear WrapPart(Builder builder, String partName) { return Wrap(builder.getPart(partName)); }

    public Gear(IBuild build) { super(build, NAME); }
    public Gear(IBuild build, Part part) { super(build, part); }
    
    public Gear DrivingTeeth(int teeth) {
        Value(drivingTeeth, teeth);
        return this;
    }
    public Gear DrivenTeeth(int teeth) {
        Value(drivenTeeth, teeth);
        return this;
    }
}
