package team1502.configuration.builders;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class Channel extends Connector {
    private static final String CLASSNAME = "Channel";
    public static final String CATEGORY_CHANNEL = "Channel";

    private static final String id = "id";
    public static final String network = "network";

    /** Battery power */
    public static final String SIGNAL_12VDC = "12VDC";
    /** 5V power */
    public static final String SIGNAL_5VDC = "5VDC";
    /** Controller area network */
    public static final String SIGNAL_CAN = "CAN";
    /** Pulse-width modulation */
    public static final String SIGNAL_PWM = "PWM";
    /** Digtal input and output */
    public static final String SIGNAL_DIO= "DIO";
    /** Analog input */
    public static final String SIGNAL_AI= "AI";
    /** Ethernet */
    public static final String SIGNAL_ETH= "Ethernet";
    /** RS-232 */
    public static final String SIGNAL_RS232= "RS-232";
    
    
    public static Function<IBuild, Channel> Define(String signal, String network, Object channelId) { return b->new Channel(b, signal, network, channelId); };
    public static Channel Wrap(Builder builder) { return new Channel(builder.getIBuild(), builder.getPart()); }
    public static Channel WrapPart(Builder builder) { return WrapPart(builder, CLASSNAME); }
    public static Channel WrapPart(Builder builder, String partName) { return Wrap(builder.getPart(partName)); }

    public Channel(IBuild build, String signal, String network, Object id) { 
        super(build, CLASSNAME, signal);
        Category(CLASSNAME);
        Value("network", network);
        var baseName = network;
        if (network != signal) {
            baseName += " " + signal;
        }
        Name(id.toString());
        Channel(id);
        FriendlyName(baseName + " " + Name());
        // if (id instanceof Integer) {
        //     Integer channelNumber = (Integer)id;
        //     FriendlyName(network + " " + signal + " Ch " + (channelNumber < 10 ? " " : "") + channelNumber.toString());
        // } else {
        //     FriendlyName(network + " " + signal + " " + id.toString());
        // }
        //Name(NAME + "|" + network + "|" + signal + "|" + id.toString());
    }
    public Channel(IBuild build, Part part) { super(build, part); }

    public Object ID() { return Value(id); }
    public String ChannelID() { return getString(id); }
    public Integer Channel() { return getInt(id); }
    public Channel Channel(Object id) {
        Value(Channel.id, id);
        return this; 
    }

    public String Network() { return getString(Channel.network); }
    public Channel Network(String network) {
        Value(Channel.network, network);
        return this;
    }

    public Connector connectToPart(Builder part) {
        var connector = part.findConnector(Signal());
        if (connector == null) {
            connector = part.addConnector(Signal());
        }
        connector.connectToChannel(this);
        return connector;
    }


    public static Channel findChannel(Builder hub, int channelNumber) {
        return Channel.Wrap(hub.getPiece(channelNumber));
    }

    public static List<Channel> getChannels(Builder hub , String signal) {
        var list = new ArrayList<Channel>();
        hub.getPart().getValues().values().stream()
            .filter(o -> o instanceof Part)
            .map(o -> (Part)o)
            .filter(p -> p.getValue(Part.CATEGORY_NAME) == Channel.CLASSNAME && p.getValue(Channel.signal) == signal)
            .map(p -> new Channel(hub.getIBuild(),p))
            .forEach(c->list.add(c));
        hub.getPart().getPieces().stream()
            .filter(o -> o instanceof Part)
            .map(o -> (Part)o)
            .filter(p -> p.getValue(Part.CATEGORY_NAME) == Channel.CLASSNAME && p.getValue(Channel.signal) == signal)
            .map(p -> new Channel(hub.getIBuild(),p))
            .forEach(c->list.add(c));
        return list;
    }

    public static List<Channel> getPartChannels(Builder hub) {
        return  hub.getPart().getValues().values().stream()
            .filter(o -> o instanceof Part)
            .map(o -> (Part)o)
            .filter(p -> p.getValue(Part.CATEGORY_NAME) == Channel.CLASSNAME)
            .map(p -> new Channel(hub.getIBuild(),p))
            .toList();
    }

    public static List<Channel> getPieceChannels(Builder hub) {
        return  hub.getPart().getPieces().stream()
            .filter(o -> o instanceof Part)
            .map(o -> (Part)o)
            .filter(p -> p.getValue(Part.CATEGORY_NAME) == Channel.CLASSNAME)
            .map(p -> new Channel(hub.getIBuild(),p))
            .toList();
    }

    public static Channel findChannel(Builder hub, Object id) {
        if (id instanceof Integer) {
            return findChannel(hub, (int)id);
        }

        var connectors = hub.getPart().getValues().values().stream()
        .filter(o -> o instanceof Part)
        .map(o -> (Part)o)
        .filter(p -> p.getValue(Part.CATEGORY_NAME) == Channel.CLASSNAME)
        .map(p -> new Channel(hub.getIBuild(),p))
        .toList();

        
        String channelName = id.toString();
        var signals =  connectors.stream()
        .filter(c -> c.ChannelID() == channelName)
        .toList();

        return signals.isEmpty() ? null : signals.get(0);
    }

}

/*

channel, connector, source, port, jack :
 PowerChannel <=> PowerProfile -- will there always be pairs
 CanMap <=> CanInfo
 DigitalIOChannel <=> DigitalInput
type, kind, signal, interface : 12VDC, Digital/IO, Analog, PWM, CAN, RS485, USB-C, ETH
network : DIO, PWM, CAN, CAN0, CAN1, MPM2

Channel => part.FindConnector("12VDC")
-- should always know "network"
Channel(MPM2) => part.FindConnector("12VDC", "MPM2") // find closest match or create

part("12VDC") => PDH.Connect(7) => FindChannel(7)

Need to be able to search a device for channels AND connectors

"Connector", signal                "Channel", signal, network, id

+-----------------+ +--------------+ +----------------------+
| connector|12VDC | |              | | channel|12VDC|MPM|01 | fuse:10A
+-----------------+ |              | +----------------------+
                    |              | | channel|12VDC|MPM|02 |
                    |              | +----------------------+
                    |              | | channel|12VDC|MPM|03 |
                    |              | +----------------------+
                    |              | | channel|12VDC|MPM|04 |
                    |              | +----------------------+
                    |              | | channel|12VDC|MPM|05 |
                    |              | +----------------------+
                    |              | | channel|12VDC|MPM|06 |
                    |    MPM       | +----------------------+
                    +--------------+ 
+-----------------+ +--------------+ +----------------------+
| connector|12VDC | | VIn        0 | | channel|24VDC|PCM|00 | 200mA
+-----------------+ |              | +----------------------+
                    |            1 | | channel|24VDC|PCM|01 |
                    |              | +----------------------+
                    |            2 | | channel|24VDC|PCM|02 |
                    |              | +----------------------------------+
                    |    Compressor| | channel|12VDC|PCM|Compressor Out | 50A
                    |    Out       | +----------------------------------+
                    |           SW | | connector|IO|        |
                    |              | +----------------------+
                    |              | | connector|CAN|       |
                    |    PCM       | +----------------------+
                    +--------------+ 


             Channel
        +-----------------+
Hub  <--| parent          | 
        |                 |
        | values (map)    |
        |  +--------------+ - - - - - - - Part value
        |  |BUILD_NAME    | "Channel"
        |  +--------------+
        |  |CLASS_NAME    | "Channel"
        |  +--------------+ - - - - - - - Connector values
        |  |CATEGORY_NAME | "Channel" 
        |  +--------------+
        |  |signal        | e.g., 12VDC
        |  +--------------+
        |  |label         |  e.g., "8 8 8"     Connector
        |  +--------------+                 +--------------+
        |  |connection    |---------------->|        parent|--> Host 
        |  +--------------+ - - - - - - -   |              |
        |  |network       | "MPM1"          |              |
        |  +--------------+                 |              |
        |  |id (channel)  | 5               |              |
        |  +--------------+                 +--------------+
        |                 |<----------------|connection    |
        +-----------------+                 +--------------+
    
  
 
    CHANNEL EXAMPLES
    +--------------+    +--------------+    +--------------+
    | channelType  |    | power        |    | CAN          |
    +--------------+    +--------------+    +--------------+
    | networkName  |    | "MPM1"       |    | "CAN"        |
    +--------------+    +--------------+    +--------------+
    | channelName  |    | "CH 10"      |    | dev/mfr/num  |
    +--------------+    +--------------+    +--------------+
    | part         |    | part         |    | part         |
    +==============+    +==============+    +==============+
                        | fuse         |    | device       |
                        +--------------+    +--------------+
                        | label        |    | manufacturer |
                        +--------------+    +--------------+
                        | totalPower() |    | number       |
                        +--------------+    +--------------+


 */