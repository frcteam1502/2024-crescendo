package team1502.configuration.Builders;

import java.util.function.Function;

import team1502.configuration.Parts.Part;

public interface IBuild {
    Builder createBuilder(String partName, Function<? extends Builder, Builder> fn);
    Builder modifyBuilder(String partName, Function<? extends Builder, Builder> fn);
    void register(Part part);
}
