package team1502.configuration.builders;

import java.util.List;
import java.util.function.Function;

public class Connector extends Builder {
    public static final String NAME = "Connector";
    private static final String connection = "connection"; // what it is attached/connected to
    //private static final String part = "part"; // always "parent" ??
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
    public Connector Signal(String signal) {
        Value(Connector.signal, signal);
        return this;
    }
    
    public Builder Host() {return wrap(getPart().getParent()); }
    
    // is always a "channel"?
    public boolean hasConnection() { return hasValue(connection); }
    public boolean isConnected() { return hasConnection() && Connection().isPartPresent(); }
    public Connector Connection() { return Wrap(getPart(connection)); }
    public Channel getChannel() { return Channel.Wrap(getPart(connection)); }
    
    
    public void connectToChannel(Connector channel) {
        makeConnection(channel);
        channel.makeConnection(this);
    }
    
    private void makeConnection(Connector channel) {
        Value(Connector.connection, channel.getPart());
    }

    public void tryConnectToChannel(Builder device) {
        var connector = device.findConnector(Signal());
        if (connector == null) {

        }
        connector.connectToChannel(this);
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

    static List<Connector> findConnectors(List<Part> parts) {
        return parts.stream()
        .filter(o -> o instanceof Part)
        .map(o -> (Part)o)
        .filter(p -> p.getValue(Builder.BUILD_TYPE) == Connector.NAME)
        .map(p -> new Connector(null,p))
        .toList();
    }
}

/*
 
    Controller          Channel                 Connector           Part
    +--------------+    +-----------------+    +--------------+    +--------------+
    |              | <= | Host()          |    | Host()       | => |              |
    +              +    +-----------------+    +--------------+    +              +
    |              |    | Connection      |<==>| Connection   |    |              |
    +--------------+    +-----------------+    +--------------+    +--------------+
    |              |    |                 |    |              |    |              |
    +--------------+    +-----------------+    +--------------+    +--------------+
    |              |    |                 |    |              |    |              |
    +--------------+    +-----------------+    +--------------+    +--------------+
    |              |    |                 |    |              |    |              |
    +--------------+    +-----------------+    +--------------+    +--------------+

    Connect part to hub
    
    hub.connectTo(part)                         part.connectTo(hub, id)
    c=part.findConnector(signal)                ch=hub.findChannel(id)
    ch= ?                                       ch.connectTo(part)
                                                c=part.findConnector(signal)
                                                c.connectToChannel(ch)
 */