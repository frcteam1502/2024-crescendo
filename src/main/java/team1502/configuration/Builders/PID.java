package team1502.configuration.Builders;

import java.util.function.Function;

import team1502.configuration.Builders.Controllers.MotorController;

public class PID extends Builder {
    private static final String NAME = "PID"; 
    public PID(double p, double i, double d, double ff) {
        super(NAME, NAME, s1 -> s1
        .setValue("p", p)
        .setValue("i", i)
        .setValue("d", d)
        .setValue("ff", ff));
    }
    public PID(double p, double i, double d) {
        super(NAME, NAME, s1 -> s1
        .setValue("p", p)
        .setValue("i", i)
        .setValue("d", d));

    }

    public PID(MotorController motorController) {
        setPart(motorController.getPart(NAME));
    }
    
    public PID(Function<PID, Builder> buildFunction) {
        super(NAME, NAME, buildFunction);
    }

    @Override
    public Builder createBuilder() {
        return new PID((Function<PID, Builder>)buildFunction);
    }

    public double P() {return getDouble("p"); }
    public double I() {return getDouble("i"); }
    public double D() {return getDouble("d"); }
    public double FF() {return getDouble("ff"); }
}
