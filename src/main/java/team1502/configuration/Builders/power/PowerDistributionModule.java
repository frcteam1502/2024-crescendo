package team1502.configuration.builders.power;

import java.util.function.Function;

import team1502.configuration.CAN.DeviceType;
import team1502.configuration.CAN.Manufacturer;
import team1502.configuration.builders.Builder;
import team1502.configuration.builders.IBuild;
import team1502.configuration.builders.Part;

/**
 * The CTRE Power Distribution Panel (PDP) and Rev Power Distribution Hub (PDH) can use their CAN connectivity to communicate a wealth of status information regarding the robot’s power use to the roboRIO, for use in user code.
 * WARNING: To enable voltage and current logging in the Driver Station, the CAN ID for the CTRE Power Distribution Panel must be 0, and for the REV Power Distribution Hub it must be 1.
 * see, https://docs.wpilib.org/en/stable/docs/software/can-devices/power-distribution-module.html
 */
public class PowerDistributionModule extends Builder {
    private static final DeviceType deviceType = DeviceType.PowerDistributionModule;

    /**
     * https://www.revrobotics.com/rev-11-1850/
     */
    public static final String PDH = "PDH"; // REV: 20 40A (0-19), 3 15A (20-22), 1 switchable (23)
    
    /**
     * https://store.ctr-electronics.com/power-distribution-panel/
     */
    public static final String PDP = "PDP"; // CTRE: 8 - 40A, 8 30/30A, 1 20A (PCM, VRM), 1 10A (roboRIO)
    
    /**
     * https://www.revrobotics.com/rev-11-1956/
     */
    public static final String MPM = "MPM"; // REV -  6 channels: 0-5  Mini Power Module (not a "Controller"?)
    
    public static final Function<IBuild, PowerDistributionModule> DefinePDH = build->new PowerDistributionModule(build, 24, Manufacturer.REVRobotics);
    public static final Function<IBuild, PowerDistributionModule> DefinePDP = build->new PowerDistributionModule(build, 18, Manufacturer.CTRElectronics);
    public static final Function<IBuild, PowerDistributionModule> DefineMPM = build->new PowerDistributionModule(build, 6);
    //public static PowerDistributionModule WrapPart(Builder builder) { return WrapPart(builder, NAME); }
    public static PowerDistributionModule Wrap(Builder builder) { return new PowerDistributionModule(builder.getIBuild(), builder.getPart()); }
    public static PowerDistributionModule WrapPart(Builder builder, String partName) { return Wrap(builder.getPart(partName)); }
    
    public PowerDistributionModule(IBuild build, int channels) {
        super(build); 
        initializeChannels(channels);
    }
    public PowerDistributionModule(IBuild build, int channels, Manufacturer manufacturer) {
         this(build, channels); 
         Device(deviceType);
         Manufacturer(manufacturer);
    }
    public PowerDistributionModule(IBuild build, Part part) { super(build, part); }

    private PowerDistributionModule initializeChannels(int channels) {
        for (int ch = 0; ch < channels; ch++) {
            createChannel(ch);
        }
        return this;
    }
        
    public PowerDistributionModule Ch(Integer channel, Integer fuze) {
        updateChannel(channel, fuze);
        return this;
    }
    public PowerDistributionModule Ch(Integer channel, Integer fuze, String name) {
        return this;
    }
    public PowerDistributionModule Ch(Integer channel, Integer fuze, Builder part) {
        updateChannel(channel, fuze, part);
        return this;
    }
    public PowerDistributionModule Ch(Integer channel) { // empty
        return this;
    }
    public PowerDistributionModule Module(String module, String ... sub) {
        return this;
    }
    public PowerDistributionModule Module(String module, Function<PowerDistributionModule,PowerDistributionModule> fn) {
        return this;
    }
    /*
     * The REV PDH has one channel (23) that can be switched on or off to control custom circuits.
        examplePD.setSwitchableChannel(true);
        examplePD.setSwitchableChannel(false);
     */

    public void tryAddPart(Builder part) {
        if (part.hasPowerProfile()) {
            updateChannel(part);
        }
    }

    private void createChannel(Integer channelNumber) {
        addPiece(Builder.Define, "Ch " + (channelNumber < 10 ? " " : "") + channelNumber.toString(), c->c
            .Value("Channel", channelNumber));        
    }

    private void updateChannel(Integer channelNumber, Integer fuze, Builder part) {
        updateChannel(channelNumber, fuze);
        updateChannel(channelNumber, part);
    }

    private void updateChannel(Integer channelNumber, Integer fuze) {
        if (channelNumber >= 0) {
            Builder ch = getPiece(channelNumber);
            ch.Value("fuze", fuze);            
        }
    }
    public void updateChannel(Integer channelNumber, Builder part) {
        if (channelNumber >= 0) {
            Builder ch = getPiece(channelNumber);
            ch.Value("part", part);
            ch.Value(Part.BUILD_NAME, part.ShortName());
        }
    }
    public void updateChannel(Builder part) {
        int channelNumber = part.PowerChannel();
        updateChannel(channelNumber, part);
    }

    public String[] ChannelNames() {
        return getPieces().stream().map(ch->ch.Name()).toArray(String[]::new);
    }
}
