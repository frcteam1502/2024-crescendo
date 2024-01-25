package team1502.configuration.Builders;

import java.util.function.Function;

public interface IBuild {
    Builder createBuilder(String partName, Function<? extends Builder, Builder> fn);
    Builder modifyBuilder(String partName, Function<? extends Builder, Builder> fn);
}
