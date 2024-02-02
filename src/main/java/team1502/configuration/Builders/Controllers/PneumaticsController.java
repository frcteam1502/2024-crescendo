package team1502.configuration.Builders.Controllers;

import java.util.function.Function;

import team1502.configuration.Builders.Builder;
import team1502.configuration.CAN.DeviceType;
import team1502.configuration.CAN.Manufacturer;
import team1502.configuration.Parts.Part;

public class PneumaticsController extends Controller {
    private static final DeviceType Type = DeviceType.PneumaticsController;
    public static final String PCM = "PCM";
    public static final String Compressor = "Compressor";

    public PneumaticsController(String name, Function<? extends PneumaticsController, Builder> fn) {
        super(name, Type, fn);
    }
    public PneumaticsController(String name, Manufacturer manufacturer, Function<? extends PneumaticsController, Builder> fn) {
        super(name, Type, manufacturer, fn);
        int channels = 16;
        for (int ch = 0; ch < channels; ch++) {
            addPiece(createChannel(ch));
        }

    }
        
    public PneumaticsController(Function<? extends PneumaticsController, Builder> buildFunction) {
        super(Type, buildFunction);
    }

    public PneumaticsController() {
        super(Type);
    }

    @Override
    public Builder createBuilder() {
        return new PneumaticsController((Function<PneumaticsController, Builder>)buildFunction);
    }


    public PneumaticsController Compressor() {
        Install(Compressor, Compressor, c -> c);
        return this;
    }
    public PneumaticsController Solenoid(int module, int channel, String name) {
        return this;
    }
    public PneumaticsController DoubleSolenoid(int forwardChannel, int reverseChannel, String name) {
        return this;
    }

    private Part createChannel(Integer channelNumber) {
        Part part = new Part();
        part.setValue("Channel", channelNumber);
        return part.Name("Ch " + (channelNumber < 10 ? " " : "") + channelNumber.toString());
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

    public String[] ChannelNames() {
        return getBuilderPieces().stream().map(ch->ch.getString("name", "")).toArray(String[]::new);
    }

}
