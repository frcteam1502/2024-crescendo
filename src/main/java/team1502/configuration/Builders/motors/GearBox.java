package team1502.configuration.builders.motors;

import java.util.function.Function;

import team1502.configuration.builders.Builder;
import team1502.configuration.builders.IBuild;
import team1502.configuration.builders.Part;

public class GearBox extends Builder{
    private static final String NAME = "GearBox"; 
    public static Function<IBuild, GearBox> Define = b->new GearBox(b);
    public static GearBox Wrap(Builder builder) { return new GearBox(builder.getIBuild(), builder.getPart()); }
    public static GearBox WrapPart(Builder builder) { return WrapPart(builder, NAME); }
    public static GearBox WrapPart(Builder builder, String partName) { return Wrap(builder.getPart(partName)); }

    public GearBox(IBuild build) { super(build, NAME); }
    public GearBox(IBuild build, Part part) { super(build, part); }

    public GearBox Gear(String stage, int drivingTeeth, int drivenTeeth) {
        return (GearBox)InstallPiece(Gear.Define, stage, g->g
            .DrivingTeeth(drivingTeeth)
            .DrivenTeeth(drivenTeeth));
    }
    
    /** all stages driving-teeth / driven-teeth */
    public double GearRatio() {
        var stages = getPieces();
        var ratios = stages.stream().map(stage->stage.getDoubleFromInt(Gear.drivingTeeth)/stage.getDoubleFromInt(Gear.drivenTeeth));
        return ratios.reduce(1.0, (stageA,stageB) -> stageA * stageB);
    }
}
