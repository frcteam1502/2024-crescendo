package team1502.configuration.builders;

import java.util.function.Function;

public class Channel extends Connector {
    private static final String NAME = "Channel";
    private static final String channel = "channel";
    //private static final String part = "part";
    public static Function<IBuild, Channel> Define(String type, Integer channelNumber) { return b->new Channel(b, type, channelNumber); };
    //public static Function<IBuild, Channel> Define(Integer channelNumber) { return b->new Channel(b, NAME, channelNumber); };
    public static Channel Wrap(Builder builder) { return new Channel(builder.getIBuild(), builder.getPart()); }
    public static Channel WrapPart(Builder builder) { return WrapPart(builder, NAME); }
    public static Channel WrapPart(Builder builder, String partName) { return Wrap(builder.getPart(partName)); }

    public Channel(IBuild build, String type, String network, String signal, Object id) { 
        super(build, type, signal);
        Value("network", network);
        if (id instanceof Integer) {
            Integer channelNumber = (Integer)id;
            FriendlyName(network + " " + signal + " Ch " + (channelNumber < 10 ? " " : "") + channelNumber.toString());
        } else {
            FriendlyName(network + " " + signal + " " + id.toString());
        }
        Name(NAME + "|" + network + "|" + signal + "|" + id.toString());
        Channel(id);
    }
    public Channel(IBuild build, Part part) { super(build, part); }

    public String ChannelID() { return getString(channel); }
    public Integer Channel() { return getInt(channel); }
    public Channel Channel(Object id) {
        Value(channel, id);
        return this; 
    }
    // public boolean hasPart() { return Part().isPartPresent(); }
    // public Builder Connector() { return getPart(part); }
    // public boolean hasPart() { return Part().isPartPresent(); }
    // public Builder Part() { return getPart(part); }
    
    public Channel Part(Builder part) {
        Value(Channel.part, part.getPart());
        part.Channel(getString("channelType"), Channel());
        FriendlyName(part.FriendlyName());
        Abbreviation(part.Abbreviation());
        return this; 
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

"Connnector", signal                "Channel", signal, network, id

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

                                                Part
                                             +-------------------+--------------+
                                             | name+channelType  |              |
                                             +-------------------+--------------+
    
  
    Controller          Channel                 Part (connector)
    +--------------+    +-----------------+    +--------------+--------------+
    | channelType  |    | channelType     |    | channelType  |              |
    +--------------+    +-----------------+    +--------------+--------------+
    | name         | == | controllerType  |    | channelType  |
    +--------------+    +-----------------+    +--------------+
    | channelType  |    | channelName     | <- | channel      |
    +--------------+    +-----------------+    +--------------+
    | channelType  |    | part            | -> |              |
    +--------------+    +-----------------+    +--------------+
    |              | <- | controller      |    |              |
    +--------------+    +-----------------+    +--------------+
 
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

    CONNECTOR EXAMPLES
    +--------------+    +--------------+    +--------------+
    | channel      | <- |              |    |              |
    +--------------+    +--------------+    +--------------+
    | part         | -> | part         |    | part         |
    +==============+    +==============+    +==============+
                        | "MPM1"       |    | "CAN"        |
                        +--------------+    +--------------+
                        | "CH 10"      |    | dev/mfr/num  |
                        +--------------+    +--------------+
                        | fuse         |    | manufacturer |
                        +--------------+    +--------------+
                        | label        |    | number       |
                        +--------------+    +--------------+

roboRIO.dio.ch0      <- arm.encoder.dio+ch0
roboRIO.dio.ch0.part -> arm.encoder.dio+ch0.part -> arm.encoder
roboRIO.dio.ch1

pdh.power.ch0
pdh.power.ch0.fuse

 */