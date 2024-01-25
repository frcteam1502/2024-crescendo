package team1502.configuration.Builders;

import java.util.function.Function;

public class GearBox extends Builder{
    private static final String NAME = "GearBox"; 
    //Define
    public GearBox(Function<GearBox, Builder> fn) {
        super(NAME, NAME, fn);
    }

    public GearBox(Builder parent) {
        setPart(parent.getPart(NAME));
    }

    public GearBox Gear(String stage, int drivingTeeth, int drivenTeeth) {
        return (GearBox)InstallPiece(new Gear(stage, drivingTeeth, drivenTeeth));
    }
    
    public double GearRatio() {
        var stages = /*getPart("Stages").*/getPieces();
        var ratios = stages.stream().map(stage->stage.getDoubleFromInt("drivingTeeth")/stage.getDoubleFromInt("drivenTeeth"));
        return ratios.reduce(1.0, (stageA,stageB) -> stageA * stageB);

    }
}
