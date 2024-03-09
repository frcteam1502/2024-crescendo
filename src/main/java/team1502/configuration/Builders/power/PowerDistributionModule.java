package team1502.configuration.builders.power;

import java.util.List;
import java.util.function.Function;

import team1502.configuration.CAN.CanInfo;
import team1502.configuration.CAN.DeviceType;
import team1502.configuration.CAN.Manufacturer;
import team1502.configuration.builders.*;

/**
 * The CTRE Power Distribution Panel (PDP) and Rev Power Distribution Hub (PDH) can use their CAN connectivity to communicate a wealth of status information regarding the robot’s power use to the roboRIO, for use in user code.
 * WARNING: To enable voltage and current logging in the Driver Station, the CAN ID for the CTRE Power Distribution Panel must be 0, and for the REV Power Distribution Hub it must be 1.
 * see, https://docs.wpilib.org/en/stable/docs/software/can-devices/power-distribution-module.html
 */
public class PowerDistributionModule extends Builder {
    private static final DeviceType deviceType = DeviceType.PowerDistributionModule;
    public static final String CATEGORY_POWERHUB = "Power Hub";

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
    
    public static final Function<IBuild, PowerDistributionModule> DefinePDH = build->new PowerDistributionModule(build, PDH, 24, deviceType, Manufacturer.REVRobotics);
    public static final Function<IBuild, PowerDistributionModule> DefinePDP = build->new PowerDistributionModule(build, PDP, 18, deviceType, Manufacturer.CTRElectronics);
    public static final Function<IBuild, PowerDistributionModule> DefineMPM = build -> new PowerDistributionModule(build, MPM, 6);
    
    public static PowerDistributionModule Wrap(Builder builder) { return builder == null ? null : new PowerDistributionModule(builder.getIBuild(), builder.getPart()); }
    public static PowerDistributionModule WrapPart(Builder builder, String partName) { return Wrap(builder.getPart(partName)); }
    
    protected PowerDistributionModule(IBuild build, String name, int channels, DeviceType deviceType, Manufacturer manufacturer) {
        super(build, deviceType, name);
        Category(CATEGORY_POWERHUB);

        CanInfo.addConnector(this, deviceType, manufacturer);
        initializeChannels(channels);
    }
    
    // MPM, non-CAN
    protected PowerDistributionModule(IBuild build, String className, int channels) {
        super(build, className);
        initializeChannels(channels);
    }
    
    protected PowerDistributionModule(IBuild build, Part part) { super(build, part); }

    private PowerDistributionModule initializeChannels(int channels) {
        for (int ch = 0; ch < channels; ch++) {
            createChannel(ch);
        }
        return this;
    }

    private void createChannel(Integer channelNumber) {
        addPiece(PowerChannel.Define(Name(), channelNumber));
    }
        
    public PowerDistributionModule Ch(Integer channel, Integer fuse) {
        updateChannel(channel, fuse);
        return this;
    }
    public PowerDistributionModule Ch(Integer channel, Integer fuse, Builder part) {
        updateChannel(channel, fuse, part);
        return this;
    }
    
    /** used for formating and layout -- does not create a channel */
    public PowerDistributionModule Ch(Integer channel) { // NOP on purpose
        // assume already created when initialized
        return this;
    }

    public List<PowerChannel> getChannels() {
        return getPieces().stream().map(ch->PowerChannel.Wrap(ch)).toList();
    }

    public PowerChannel getChannel(int channelNumber) {
        return  PowerChannel.Wrap(getPiece(channelNumber));
    }

    private void updateChannel(Integer channelNumber, Integer fuse, Builder part) {
        if (channelNumber >= 0) {
            var ch = getChannel(channelNumber).Fuse(fuse);
            ch.connectToPart(part);
        }
    }

    protected void updateChannel(Integer channelNumber, Integer fuse) {
        if (channelNumber >= 0) {
            getChannel(channelNumber).Fuse(fuse);            
        }
    }
    public PowerDistributionModule Ch(Integer channelNumber, Builder part) {
        updateChannel(channelNumber, part);
        return this;
    }
    public void updateChannel(Integer channelNumber, Builder part) {
        if (channelNumber >= 0) {
            var ch = getChannel(channelNumber);
            ch.connectToPart(part);
        }
    }

    public String[] ChannelNames() {
        return getPieces().stream().map(ch->getFriendlyName(ch)).toArray(String[]::new);
    }
    public String[] ChannelNamesAbbr() {
        return getPieces().stream().map(ch->getAbbreviation(ch)).toArray(String[]::new);
    }

    private String getFriendlyName(Builder builder) {
        var ch = PowerChannel.Wrap(builder);
        return ch.isConnected() ? ch.getConnectedPart().FriendlyName() : chX(ch);
    }
    private String getAbbreviation(Builder builder) {
        var ch = PowerChannel.Wrap(builder);
        return ch.isConnected() ? ch.getConnectedPart().Abbreviation() : chX(ch);
    }
    private String chX(Channel ch) {
        return "Ch " + ch.ID().toString();
    }
}

    /*
    public void updateChannel(Builder part) {
        int channelNumber = part.PowerChannel();
        updateChannel(channelNumber, part);
    }
    @Override // Builder
    public Builder Powers(Builder builder) {
        super.Powers(builder);
        if (builder.hasPowerProfile() && builder.PowerProfile().Channel() != null) {
            updateChannel(builder);
        }
        return this;
    }

    public PowerDistributionModule Ch(Integer channel, Integer fuse, String name) {
        return this;
    }
    public PowerDistributionModule Module(String module, String ... sub) {
        return this;
    }
    public PowerDistributionModule Module(String module, Function<PowerDistributionModule,PowerDistributionModule> fn) {
        return this;
    }
     * The REV PDH has one channel (23) that can be switched on or off to control custom circuits.
        examplePD.setSwitchableChannel(true);
        examplePD.setSwitchableChannel(false);

    public void tryAddPart(Builder part) {
        if (part.hasPowerProfile()) {
            updateChannel(part);
        }
    }
     */
        
