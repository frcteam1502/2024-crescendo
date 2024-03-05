package team1502.configuration.builders.power;

import java.util.function.Function;

import team1502.configuration.builders.*;

public class PowerChannel extends Channel {
    private static final String NAME = "PowerChannel";
    private static final String fuse = "fuse";
    static final String totalPeakPower = "totalPeakPower";
    
    public static Function<IBuild, PowerChannel> Define(String network, String channelNumber) { return b->new PowerChannel(b, network, channelNumber); };
    public static Function<IBuild, PowerChannel> Define(String network, Integer channelNumber) { return b->new PowerChannel(b, network, channelNumber); };
    public static PowerChannel Wrap(Builder builder) { return new PowerChannel(builder.getIBuild(), builder.getPart()); }
    public static PowerChannel WrapPart(Builder builder) { return WrapPart(builder, NAME); }
    public static PowerChannel WrapPart(Builder builder, String partName) { return Wrap(builder.getPart(partName)); }

    public PowerChannel(IBuild build, String network, Object channelNumber) { 
        super(build, POWER, network, channelNumber);
    }
    public PowerChannel(IBuild build, Part part) { super(build, part); }

    public boolean hasFuse() {return hasValue(fuse); }
    public Integer Fuse() { return getInt(fuse); }
    public PowerChannel Fuse(Integer amps) {
        Value(fuse, amps);
        return this; 
    }
    
    public static PowerChannel findConnectedChannel(Builder device) {
        var vin = device.findConnector(POWER);
        return Wrap(vin.Connection());
    }

    public static Double getTotalPeakPower(PowerChannel ch) {
        double totalPower = 0.0;
        if (ch.isConnected()) {
            totalPower += getTotalPeakPower(ch.Connection().Host());
        }
        ch.setValue(PowerChannel.totalPeakPower, totalPower); // set the power for reporting
        return totalPower;
    }
    
    public static Double getTotalPeakPower(Builder part) {
        double totalPower = 0.0;
        if (part.hasPowerProfile()) {
            totalPower = part.PowerProfile().PeakPower();
        }
        var channels = getChannels(part, POWER).stream().map(ch->Wrap(ch)).toList();
        for (PowerChannel ch : channels) {
            totalPower += getTotalPeakPower(ch);
        }        
        part.Value(PowerChannel.totalPeakPower, totalPower); // set the power for reporting
        return totalPower;
    }
    
}
