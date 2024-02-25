package team1502.configuration.builders.pneumatics;

import java.util.List;
import java.util.function.Function;

import edu.wpi.first.wpilibj.PneumaticsModuleType;
import team1502.configuration.CAN.DeviceType;
import team1502.configuration.CAN.Manufacturer;
import team1502.configuration.builders.Builder;
import team1502.configuration.builders.IBuild;
import team1502.configuration.builders.Part;
import team1502.configuration.builders.power.PowerChannel;
import team1502.configuration.builders.power.PowerDistributionModule;

public class PneumaticsController extends PowerDistributionModule {
    private static final DeviceType deviceType = DeviceType.PneumaticsController;
    // https://docs.wpilib.org/en/stable/docs/software/hardware-apis/pneumatics/index.html
    public static final String PCM = "PCM"; // CAN(0) CTRE Pneumatics Control Module
    public static final String PH = "PH"; // CAN(1) https://www.revrobotics.com/rev-11-1852/
    public static final String Compressor = "Compressor";
    public static final int CompressorPower = 16;
    public static Function<IBuild, PneumaticsController> Define(Manufacturer manufacturer) {
        return build->new PneumaticsController(build,manufacturer);
    } 
    public static PneumaticsController Wrap(Builder builder) { return new PneumaticsController(builder.getIBuild(), builder.getPart()); }
    public static PneumaticsController WrapPart(Builder builder) { return WrapPart(builder, deviceType.name()); }
    public static PneumaticsController WrapPart(Builder builder, String partName) { return Wrap(builder.getPart(partName)); }

    // Define
    public PneumaticsController(IBuild build, Manufacturer manufacturer) {
        super(build, "PCM", 17);
        Device(deviceType); // also "buildType"
        Manufacturer(manufacturer);
        int channels = 16;
        for (int ch = 0; ch < channels; ch++) {
            updateChannel(ch, 1); // 200mA per solenoid
        }
        updateChannel(CompressorPower, 50);
    }
    //Build Proxy / Eval
    public PneumaticsController(IBuild build, Part part) {
        super(build, part);
    }

    public PneumaticsController Compressor() {
        updateChannel(16, 
            addPart(Builder.DefineAs(Compressor), Compressor, Compressor, c->c));
        return this;
    }
    public PneumaticsController Solenoid(int module, int channel, String name) {
        updateChannel(channel, 
            addPart(Builder.DefineAs(name), name, name, c->c));
        return this;
    }
    public PneumaticsController DoubleSolenoid(int forwardChannel, int reverseChannel, String name) {
        updateChannel(reverseChannel, 
            addPart(Builder.DefineAs(name), name, name, c->c));
        return this;
    }
    public PneumaticsController Ch(Integer channelNumber, Builder part) {
        updateChannel(channelNumber, part);
        return this;
    }

    public edu.wpi.first.wpilibj.Solenoid buildSolenoid(int channel) {
        var module = CanNumber();
        return new edu.wpi.first.wpilibj.Solenoid(module, PneumaticsModuleType.REVPH, channel);
    }

    /*
     * 
    public List<PowerChannel> getChannels() {
        return getPieces().stream().map(ch->PowerChannel.Wrap(ch)).toList();
    }

    public PowerChannel getChannel(int channelNumber) {
        return  PowerChannel.Wrap(getPiece(channelNumber));
    }

    private void updateChannel(Integer channelNumber, Integer fuse) {
        if (channelNumber >= 0) {
            getChannel(channelNumber).Fuse(fuse);            
        }
    }

    @Override // Builder
    public Builder Powers(Builder builder) {
        super.Powers(builder);
        if (builder.hasPowerProfile() && builder.PowerProfile().Channel() != null) {
            updateChannel(builder);
        }
        return this;
    }
    public void updateChannel(Integer channelNumber, Builder part) {
        if (channelNumber >= 0) {
            getChannel(channelNumber).Part(part);
        }
    }
    public void updateChannel(Builder part) {
        int channelNumber = part.PowerChannel();
        updateChannel(channelNumber, part);
    }

    public String[] ChannelNames() {
        return getPieces().stream().map(ch->ch.Name()).toArray(String[]::new);
    }
     */
}
