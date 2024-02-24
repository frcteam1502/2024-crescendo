package team1502.configuration.builders;

import java.util.function.Function;

public class Connector extends Builder {
    public static final String NAME = "Connector";
    private static final String connected = "connected"; // what it is attached/connected to
    private static final String part = "part"; // always "parent" ??
    public static final String signal = "signal";
    public static Function<IBuild, Connector> Define(String signal) { return b->new Connector(b, signal); };
    public static Connector Wrap(Builder builder) { return new Connector(builder.getIBuild(), builder.getPart()); }

    //public static Connector Wrap(Part connector) { return new Connector(builder.getIBuild(), builder.getPart()); }

    protected Connector(IBuild build, String NAME, String signal)
    {
        super(build, NAME);
        Value(Connector.signal, signal);
    }
    public Connector(IBuild build, String signal) {
        super(build, NAME);
        Value(Connector.signal, signal);
    }
    
    public Connector(IBuild build, Part part) { super(build, part); }

    public String Signal() { return getString(Connector.signal); }
    
    public Builder Part() {return wrap(getPart().getParent()); }
    
    // is always a "channel"?
    public boolean isConnected() { return Connector().isPartPresent(); }
    public Connector Connector() { return Wrap(getPart(connected)); }
    public void Connect(Connector channel) {
        Value(Connector.connected, channel.getPart());
    }

    public static Connector findConnector(Builder builder, String signal) {
        var connectors = builder.getPart().getValues().values().stream()
            .filter(o -> o instanceof Part)
            .map(o -> (Part)o)
            .filter(p -> p.getValue(Builder.BUILD_TYPE) == Connector.NAME)
            .map(p -> new Connector(builder.getIBuild(),p))
            .toList();
        var signals =  connectors.stream()
            .filter(c -> c.Signal() == signal)
            .toList();

        return signals.isEmpty() ? null : signals.get(0);
    }
}
