package team1502.configuration.factory;

import java.util.function.Function;

import team1502.configuration.builders.Builder;
import team1502.configuration.builders.IBuild;
import team1502.configuration.builders.Part;

public class PartBuilder<T extends Builder>  {
    private PartBuilder(String name, Function<IBuild, T> createFunction, Function<T, Builder> buildFunction, Function<T, Builder> modifyFunction) {
        this(name, createFunction, buildFunction);
        this.modifyFunction = modifyFunction;
    }
    public PartBuilder(String name, Function<IBuild, T> createFunction, Function<T, Builder> buildFunction) {
        this.name = name;
        this.createFunction = createFunction;
        this.buildFunction = buildFunction;
    }
    public PartBuilder(Function<IBuild, T> createFunction, Function<T, Builder> buildFunction) {
        this.name = "";
        this.createFunction = createFunction;
        this.buildFunction = buildFunction;
    }
    private String name;
    private Function<IBuild, T> createFunction;
    private Function<T, Builder> buildFunction;
    private Function<T, Builder> modifyFunction;
    
    public Builder creatBuilder(IBuild build) {
        T builder = createFunction.apply(build);
        String name = this.name;
        if (name == "") {
            name = builder.getString(Builder.BUILD_TYPE, "");
        }
        if (name != "") {
            builder.Value(Part.BUILD_NAME, name);
            builder.Value(Part.ORIGINAL_NAME, name);
        }
        buildFunction.apply(builder);
        if (modifyFunction != null) {
            modifyFunction.apply(builder);
        }
        return builder;
    }

    public PartBuilder<?> WithModification(Function<T, Builder> modifyFunction) {
        return new PartBuilder<>(name, createFunction, buildFunction , modifyFunction);
    }

    public static <T extends Builder> T createBuilder(IBuild build, String name, Function<IBuild, T> createFunction, Function<T, Builder> buildFunction) {
        T builder = createFunction.apply(build);
        builder.Value(Part.BUILD_NAME, name);
        builder.Value(Part.ORIGINAL_NAME, name);
        buildFunction.apply(builder);
        return builder;
    }
    public static <T extends Builder> T createBuilder(IBuild build, Function<IBuild, T> createFunction, Function<T, Builder> buildFunction) {
        T builder = createFunction.apply(build);
        var buildType = builder.getString(Builder.BUILD_TYPE, "");
        if (buildType != "") {
            builder.Value(Part.BUILD_NAME, buildType);
            builder.Value(Part.ORIGINAL_NAME, buildType);
        }
        buildFunction.apply(builder);
        return builder;
    }
}
