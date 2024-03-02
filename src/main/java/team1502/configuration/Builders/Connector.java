package team1502.configuration.builders;

import java.util.List;
import java.util.function.Function;

public class Connector extends Builder {
    public static final String CLASSNAME = "Connector";
    private static final String connection = "connection"; // what it is attached/connected to
    public static final String signal = "signal";
    public static final String label = "label";
    public static Function<IBuild, Connector> Define(String signal) { return b->new Connector(b, CLASSNAME, signal); };
    public static Connector Wrap(Builder builder) { return new Connector(builder.getIBuild(), builder.getPart()); }

    protected Connector(IBuild build, String className, String signal)
    {
        super(build, className);
        Category(className);
        Value(Connector.signal, signal);
    }
    public Connector(IBuild build, String signal) {
        this(build, CLASSNAME, signal);
    }
    
    public Connector(IBuild build, Part part) { super(build, part); }

    public String Signal() { return getString(Connector.signal); }
    public Connector Signal(String signal) {
        Value(Connector.signal, signal);
        return this;
    }

    //** e.g., the physical label on the wire harness to help identify a connection */
    public String Label() { return getString(Connector.label); }
    public Connector Label(String label) {
        setValue(Connector.label, label);
        return this;
    }
    
    public Builder Host() {return wrap(getPart().getParent()); }
    
    // is always a "channel"?
    public boolean hasConnection() { return hasValue(connection); }
    /** there is a Part on the other side */
    public boolean isConnected() { return hasConnection() && Connection().isPartPresent(); }
    public Builder getConnectedPart() { return isConnected() ? Connection().Host() : null; }

    /** the Connection on the other side */
    public Connector Connection() { return Wrap(getPart(connection)); }
    public Channel getChannel() { return Channel.Wrap(getPart(connection)); }
    

    public void connectToChannel(String hubName, Object channelID) {
        var hub = getIBuild().getInstalled(hubName);
        connectToChannel(hub, channelID);
    }

    void connectToChannel(Builder hub, Object channelID) {
        if (hub.hasValue(Signal())) {
            var subHub = hub.getPart(Signal());
            if (subHub != null) {
                hub = subHub;
            }
        }
        var ch = hub.findChannel(channelID);
        connectToChannel(ch);
    }


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
            .filter(p -> p.getValue(Part.CATEGORY_NAME) == Connector.CLASSNAME)
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
        .filter(p -> p.getValue(Part.CATEGORY_NAME) == Connector.CLASSNAME)
        .map(p -> new Connector(null,p))
        .toList();
    }
}

/*
             Connector
        +-----------------+
Host <--| parent          | 
        |                 |
        | values (map)    |
        |  +--------------+ - - - - - - - Part values
        |  |BUILD_NAME    | "Connector"
        |  +--------------+
        |  |CLASS_NAME    | "Connector"
        |  +--------------+ - - - - - - - Connector values
        |  |CATEGORY_NAME | "Connector"
        |  +--------------+
        |  |signal        | e.g., 12VDC
        |  +--------------+
        |  |label         | e.g., "8 8 8"      Channel
        |  +--------------+                +--------------+
        |  |connection    |--------------->|        parent|--> Hub 
        |  +--------------+                |              |
        |                 |                +--------------+
        |                 |<---------------|connection    |
        +-----------------+                +--------------+
 
 */