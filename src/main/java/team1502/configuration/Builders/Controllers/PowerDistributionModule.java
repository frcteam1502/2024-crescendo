package team1502.configuration.Builders.Controllers;

import java.util.function.Function;

import team1502.configuration.Builders.Builder;
import team1502.configuration.CAN.DeviceType;
import team1502.configuration.CAN.Manufacturer;

/**
 * The CTRE Power Distribution Panel (PDP) and Rev Power Distribution Hub (PDH) can use their CAN connectivity to communicate a wealth of status information regarding the robot’s power use to the roboRIO, for use in user code.
 * WARNING: To enable voltage and current logging in the Driver Station, the CAN ID for the CTRE Power Distribution Panel must be 0, and for the REV Power Distribution Hub it must be 1.
 * see, https://docs.wpilib.org/en/stable/docs/software/can-devices/power-distribution-module.html
 */
public class PowerDistributionModule extends Controller {
    private static final DeviceType Type = DeviceType.PowerDistributionModule;
    public static final String PDP = "PDP"; // CTRE
    public static final String PDH = "PDH"; // REV - 23 channels: 0-22
    
    /**
     * https://www.revrobotics.com/rev-11-1956/
     */
    public static final String MPM = "MPM"; // REV -  6 channels: 0-5  Mini Power Module (not a "Controller"?)

    public PowerDistributionModule(String name, Function<? extends PowerDistributionModule, Builder> fn) {
        super(name, Type, fn);
    }
    public PowerDistributionModule(String name, Manufacturer manufacturer, Function<? extends PowerDistributionModule, Builder> fn) {
        super(name, Type, manufacturer, fn);
        int channels = 23; // REV PDH
        if (manufacturer == Manufacturer.REVRobotics) { // PDP
            if (name == MPM) {
                channels = 6;
            }
        } 
        else if (manufacturer == Manufacturer.CTRElectronics) { // PDP
            channels = 16; // ??
        }
        for (int ch = 0; ch < channels; ch++) {
            InstallPiece(createChannel(ch));
        }
    }
        
    public PowerDistributionModule(Function<? extends PowerDistributionModule, Builder> buildFunction) {
        super(Type, buildFunction);
    }

    public PowerDistributionModule() {
        super(Type);
    }

    @Override
    public Builder createBuilder() {
        return new PowerDistributionModule((Function<PowerDistributionModule, Builder>)buildFunction);
    }
   
    public PowerDistributionModule Ch(Integer channel, Integer fuze) {
        updateChannel(channel, fuze);
        return this;
    }
    public PowerDistributionModule Ch(Integer channel, Integer fuze, String name) {
        return this;
    }
    public PowerDistributionModule Ch(Integer channel, Integer fuze, Builder part) {
        updateChannel(fuze, fuze, part);
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
     * The REV PDH has one channel (22) that can be switched on or off to control custom circuits.
        examplePD.setSwitchableChannel(true);
        examplePD.setSwitchableChannel(false);
     */

    public void tryAddPart(Builder part) {
        if (part.hasPowerProfile()) {
            updateChannel(part);
        }
    }

    private Builder createChannel(Integer channelNumber) {
        return Builder.ForPart("Channel", "Ch " + (channelNumber < 10 ? " " : "") + channelNumber.toString());
    }

    private void updateChannel(Integer channelNumber, Integer fuze, Builder part) {
        updateChannel(channelNumber, fuze);
        updateChannel(channelNumber, part);
    }

    private void updateChannel(Integer channelNumber, Integer fuze) {
        if (channelNumber >= 0) {
            Builder ch = getBuilderPiece(channelNumber);
            ch.Value("fuze", fuze);            
        }
    }
    private void updateChannel(Integer channelNumber, Builder part) {
        if (channelNumber >= 0) {
            Builder ch = getBuilderPiece(channelNumber);
            ch.Value("part", part);
            ch.Value("name", part.FriendlyName());
        }
    }
    private void updateChannel(Builder part) {
        int channelNumber = part.PowerChannel();
        updateChannel(channelNumber, part);
    }

    private class ChannelInfo {
        public int channel;
        public int fuze;
        public Builder part;
    }

    public String[] ChannelNames() {
        return getBuilderPieces().stream().map(ch->ch.getString("name", "")).toArray(String[]::new);
    }
}
