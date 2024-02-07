package team1502.configuration.builders.sensors;

import java.util.function.Function;

import team1502.configuration.builders.Builder;
import team1502.configuration.builders.IBuild;
import team1502.configuration.builders.Part;

public class Gyro extends Builder {
    public static final String NAME = "Gyro";
    public static Function<IBuild, Gyro> Define = build->new Gyro(build);
    public static Gyro Wrap(Builder builder) { return new Gyro(builder.getIBuild(), builder.getPart()); }
    public static Gyro WrapPart(Builder builder) { return WrapPart(builder, NAME); }
    public static Gyro WrapPart(Builder builder, String partName) { return Wrap(builder.getPart(partName)); }

    public Gyro(IBuild build) { super(build, NAME); }
    public Gyro(IBuild build, Part part) { super(build, part); }

}
