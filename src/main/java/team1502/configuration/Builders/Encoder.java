package team1502.configuration.Builders;

import java.util.function.Function;

public class Encoder extends Builder {
    private Function<Encoder, Builder> buildFunction;
    
    //Define
    public Encoder(String name, Function<Encoder, Builder> fn) {
        super("EncoderSensor", name, null);
        buildFunction = fn;
    }

    //Build
    public Encoder(Function<Encoder, Builder> fn) {
        super("EncoderSensor");
        buildFunction = fn;
    }
    
    @Override
    public Builder createBuilder() {
        return new Encoder((Function<Encoder, Builder>)buildFunction);
    }
  
}
