package team1502.configuration.builders.power;

import java.util.function.Function;

import team1502.configuration.builders.Builder;
import team1502.configuration.builders.Channel;
import team1502.configuration.builders.IBuild;
import team1502.configuration.builders.Part;

public class PowerChannel extends Channel {
    private static final String NAME = "PowerChannel";
    //private static final String channel = "channel";
    private static final String fuse = "fuse";
    private static final String part = "part";
    //private static final String type = "power";
    public static Function<IBuild, PowerChannel> Define(Integer channelNumber) { return b->new PowerChannel(b, channelNumber); };
    public static PowerChannel Wrap(Builder builder) { return new PowerChannel(builder.getIBuild(), builder.getPart()); }
    public static PowerChannel WrapPart(Builder builder) { return WrapPart(builder, NAME); }
    public static PowerChannel WrapPart(Builder builder, String partName) { return Wrap(builder.getPart(partName)); }

    public PowerChannel(IBuild build, Integer channelNumber) { 
        super(build, NAME, channelNumber);
    }
    public PowerChannel(IBuild build, Part part) { super(build, part); }

    /*
     * 
    public Integer Channel() { return getInt(channel); }
    public PowerChannel Channel(Integer channelNumber) {
        Value(channel, channelNumber);
        return this; 
    }
    public boolean hasPart() { return Part().isPartPresent(); }
    public Builder Part() { return getPart(part); }
     */
    
    public boolean hasFuse() {return hasValue(fuse); }
    public Integer Fuse() { return getInt(fuse); }
    public PowerChannel Fuse(Integer amps) {
        Value(fuse, amps);
        return this; 
    }
    
    public PowerChannel Part(Builder powered) {
        Value(part, powered.getPart());
        powered.PowerChannel(Channel());
        FriendlyName(powered.FriendlyName());
        Abbreviation(powered.Abbreviation());
        return this; 
    }

    public String WireLabel() {
        if (hasPart() && Part().hasPowerProfile()) {
            return Part().PowerProfile().Label();
        }
        return "";
    }
    public Double ChannelPower() {
        if (hasPart() && Part().hasPowerProfile()) {
            return Part().TotalPeakPower();
        }
        return Double.MIN_NORMAL;
    }
}
