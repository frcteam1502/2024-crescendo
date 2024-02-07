package team1502.configuration.builders.sensors;

import java.util.function.Function;

import team1502.configuration.builders.Builder;
import team1502.configuration.builders.IBuild;
import team1502.configuration.builders.Part;

public class Encoder extends Builder {
    public static final String NAME = "Encoder";
    public static Function<IBuild, Encoder> Define = build->new Encoder(build);
    public static Encoder Wrap(Builder builder) { return new Encoder(builder.getIBuild(), builder.getPart()); }
    public static Encoder WrapPart(Builder builder) { return WrapPart(builder, NAME); }
    public static Encoder WrapPart(Builder builder, String partName) { return Wrap(builder.getPart(partName)); }

    public Encoder(IBuild build) { super(build, NAME); }
    public Encoder(IBuild build, Part part) { super(build, part); }
  
}
