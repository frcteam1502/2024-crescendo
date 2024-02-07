package team1502.configuration.builders.pneumatics;

import java.util.function.Function;

import team1502.configuration.CAN.DeviceType;
import team1502.configuration.CAN.Manufacturer;
import team1502.configuration.builders.Builder;
import team1502.configuration.builders.IBuild;
import team1502.configuration.builders.Part;

public class PneumaticsController extends Builder {
    private static final DeviceType deviceType = DeviceType.PneumaticsController;
    public static final String PCM = "PCM";
    public static final String Compressor = "Compressor";
    public static Function<IBuild, PneumaticsController> Define(Manufacturer manufacturer) {
        return build->new PneumaticsController(build,manufacturer);
    } 
    public static PneumaticsController Wrap(Builder builder) { return new PneumaticsController(builder.getIBuild(), builder.getPart()); }
    public static PneumaticsController WrapPart(Builder builder) { return WrapPart(builder, deviceType.name()); }
    public static PneumaticsController WrapPart(Builder builder, String partName) { return Wrap(builder.getPart(partName)); }

    // Define
    public PneumaticsController(IBuild build, Manufacturer manufacturer) {
        super(build);
        Device(deviceType); // also "buildType"
        Manufacturer(manufacturer);
        int channels = 16;
        for (int ch = 0; ch < channels; ch++) {
            createChannel(ch);
        }
    }
    //Build Proxy / Eval
    public PneumaticsController(IBuild build, Part part) {
        super(build, part);
    }

    public PneumaticsController Compressor() {
        addPart(Builder.Define, Compressor, Compressor, c -> c);
        return this;
    }
    public PneumaticsController Solenoid(int module, int channel, String name) {
        return this;
    }
    public PneumaticsController DoubleSolenoid(int forwardChannel, int reverseChannel, String name) {
        return this;
    }

    private void createChannel(Integer channelNumber) {
        addPiece(Builder.Define, "Ch " + (channelNumber < 10 ? " " : "") + channelNumber.toString(), c->c
            .Value("Channel", channelNumber));        
    }

    private void updateChannel(Integer channelNumber, Builder part) {
        if (channelNumber >= 0) {
            Builder ch = getPiece(channelNumber);
            ch.Value("part", part);
            ch.Value(Part.BUILD_NAME, part.FriendlyName());
        }
    }

    private void updateChannel(Builder part) {
        int channelNumber = part.PowerChannel();
        updateChannel(channelNumber, part);
    }

    public String[] ChannelNames() {
        return getPieces().stream().map(ch->ch.getString(Part.BUILD_NAME, "")).toArray(String[]::new);
    }

}
