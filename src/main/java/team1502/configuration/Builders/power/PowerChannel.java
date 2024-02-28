package team1502.configuration.builders.power;

import java.util.function.Function;

import team1502.configuration.builders.Builder;
import team1502.configuration.builders.Channel;
import team1502.configuration.builders.IBuild;
import team1502.configuration.builders.Part;

public class PowerChannel extends Channel {
    private static final String NAME = "PowerChannel";
    private static final String fuse = "fuse";
    //private static final String channel = "channel";
    //private static final String part = "part";
    //private static final String type = "power";
    public static Function<IBuild, PowerChannel> Define(String network, Integer channelNumber) { return b->new PowerChannel(b, network, channelNumber); };
    public static PowerChannel Wrap(Builder builder) { return new PowerChannel(builder.getIBuild(), builder.getPart()); }
    public static PowerChannel WrapPart(Builder builder) { return WrapPart(builder, NAME); }
    public static PowerChannel WrapPart(Builder builder, String partName) { return Wrap(builder.getPart(partName)); }

    public PowerChannel(IBuild build, String network, Integer channelNumber) { 
        super(build, Channel.SIGNAL_12VDC, network, channelNumber);
    }
    public PowerChannel(IBuild build, Part part) { super(build, part); }

    public boolean hasFuse() {return hasValue(fuse); }
    public Integer Fuse() { return getInt(fuse); }
    public PowerChannel Fuse(Integer amps) {
        Value(fuse, amps);
        return this; 
    }
    

    /*
     * 
    public Integer Channel() { return getInt(channel); }
    public PowerChannel Channel(Integer channelNumber) {
        Value(channel, channelNumber);
        return this; 
    }
    public boolean hasPart() { return Part().isPartPresent(); }
    public Builder Part() { return getPart(part); }
    
    // public PowerChannel Part(Builder powered) {
    //     Value(part, powered.getPart());
    //     powered.PowerChannel(Channel());
    //     FriendlyName(powered.FriendlyName());
    //     Abbreviation(powered.Abbreviation());
    //     return this; 
    // }

    public String WireLabel() {
        if (isConnected() && Connection().hasPowerProfile()) {
            return Connection().PowerProfile().Label();
        }
        return "";
    }
    public Double ChannelPower() {
        if (isConnected()) {
            return Connection().Host().TotalPeakPower();
        }
        return Double.MIN_NORMAL;
    }
     */

    public static PowerChannel findConnectedChannel(Builder device) {
        var vin = device.findConnector(Channel.SIGNAL_12VDC);
        return Wrap(vin.Connection());
    }

    public static Double getTotalPeakPower(PowerChannel ch) {
        double totalPower = 0.0;
        if (ch.isConnected()) {
            totalPower += getTotalPeakPower(ch.Connection().Host());
        }
        ch.setValue("totalPeakPower", totalPower); // set the power for reporting
        return totalPower;
    }
    
    public static Double getTotalPeakPower(Builder part) {
        double totalPower = 0.0;
        if (part.hasPowerProfile()) {
            totalPower = part.PowerProfile().PeakPower();
        }
        var channels = getChannels(part, Channel.SIGNAL_12VDC).stream().map(ch->Wrap(ch)).toList();
        for (PowerChannel ch : channels) {
            totalPower += getTotalPeakPower(ch);
        }        
        part.Value("totalPeakPower", totalPower); // set the power for reporting
        return totalPower;
    }
    
}
