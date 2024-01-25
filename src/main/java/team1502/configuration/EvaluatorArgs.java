package team1502.configuration;

import java.util.function.Function;

public class EvaluatorArgs {
    public EvaluatorArgs(String valueName, Function<Evaluator, Object> fn) {
        this.valueName = valueName;
        this.function = fn;
    }

    public String valueName;
    public String partName;
    public Function<Evaluator,Object> function;
}
