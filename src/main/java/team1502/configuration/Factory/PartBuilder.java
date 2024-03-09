package team1502.configuration.factory;

import java.util.function.Function;

import team1502.configuration.builders.Builder;
import team1502.configuration.builders.IBuild;
import team1502.configuration.builders.Part;

/** Use PartBuilder when possible to follow the creation process */
public class PartBuilder<T extends Builder>  {
    /** a standard build template */
    public PartBuilder(String partName, Function<IBuild, T> createFunction, Function<T, Builder> buildFunction) {
        this.partName = partName;
        this.createFunction = createFunction;
        this.buildFunction = buildFunction;
    }

    // e.g., addPart/addPiece -- create without an exisitng template
    public PartBuilder(Function<IBuild, T> createFunction, Function<T, Builder> buildFunction) {
        this.createFunction = createFunction;
        this.buildFunction = buildFunction;
    }

    public PartBuilder<T> with(Function<T, Builder> modifyFunction) {
        return new PartBuilder<T>(this, modifyFunction);
    }
    private PartBuilder(PartBuilder<T> partBuilder, Function<T, Builder> modifyFunction) {
        this.partBuilder = partBuilder;
        this.buildFunction = modifyFunction;
    }

    // getTemplate() parameters -- i.e., a PartBuilder
    
    /** this is the intended/default name from the template */
    private String partName;
    private Function<IBuild, T> createFunction;
    private Function<T, Builder> buildFunction;

    /** nested builder */
    private PartBuilder<T> partBuilder;
    
    //this part must be named and parented and registered before the buildFunction
    public T addBuilder(Builder parent) { return addBuilder(parent, null); }

    public T addBuilder(Builder parent, String name) {
        T builder = null;
        if (partBuilder != null) {
            builder = partBuilder.addBuilder(parent, name);
        } else {
            builder = createFunction.apply(parent.getIBuild());
            if (partName != null) {
                builder.Value(Part.TEMPLATE_NAME, partName);
            }    
            // part is registered in the Builder constructor
            if (name != null) {
                builder.Value(Part.BUILD_NAME, name);
            }
            builder.getPart().setParent(parent.getPart());
            builder.initialize();
        }
        return build(builder);
    }
    
    private T build(T builder) {
        if (buildFunction != null) { // may just need a "Define"
            buildFunction.apply(builder);
        }
        return builder;
    }

    // public T createBuilder(IBuild build) {
    //     return createBuilder(build, null);
    // }

    public T createBuilder(IBuild build, String name) {
        T builder = null;
        if (partBuilder != null) {
            builder = partBuilder.createBuilder(build, name);
        } else {
            builder = createFunction.apply(build);
            if (partName != null) {
                builder.Value(Part.TEMPLATE_NAME, partName);
            }    
            // part is registered in the Builder constructor
            if (name != null) {
                builder.Value(Part.BUILD_NAME, name);
            }
            builder.initialize();
        }
        return build(builder);
    }

/*
    public static <T extends Builder> T createBuilder(IBuild build, String name, Function<IBuild, T> createFunction, Function<T, Builder> buildFunction) {
        T builder = createFunction.apply(build);
        builder.Value(Part.BUILD_NAME, name);
        buildFunction.apply(builder);
        return builder;
    }
    public static <T extends Builder> T createBuilder(IBuild build, Function<IBuild, T> createFunction, Function<T, Builder> buildFunction) {
        T builder = createFunction.apply(build);
        var buildType = builder.getString(Builder.CLASS_NAME, "");
        if (buildType != "") {
            builder.Value(Part.BUILD_NAME, buildType);
        }
        buildFunction.apply(builder);
        return builder;
    }
 */
}
