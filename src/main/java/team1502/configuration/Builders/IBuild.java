package team1502.configuration.builders;

import java.util.function.Function;

import team1502.configuration.factory.PartBuilder;

public interface IBuild {
    <T extends Builder> PartBuilder<?> getTemplate(String partName, Function<IBuild, T> createFunction, Function<T, Builder> buildFunction);
    void register(Part part);
    Builder getInstalled(String name);
}
