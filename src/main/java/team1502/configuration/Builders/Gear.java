package team1502.configuration.Builders;

import java.util.function.Function;

public class Gear extends Builder {
    public Gear(String stage, int drivingTeeth, int drivenTeeth) {
        super("Gear", stage, s1 -> s1
            .setValue("drivingTeeth", drivingTeeth)
            .setValue("drivenTeeth", drivenTeeth));
    }

    public Gear(Function<Gear, Builder> buildFunction) {
        super("Gear", "", buildFunction);
    }

    @Override
    public Builder createBuilder() {
        return new Gear((Function<Gear, Builder>)buildFunction);
    }

}
