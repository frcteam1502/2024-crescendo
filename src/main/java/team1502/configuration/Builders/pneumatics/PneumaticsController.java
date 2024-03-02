package team1502.configuration.builders.pneumatics;

import java.util.function.Function;

import edu.wpi.first.wpilibj.PneumaticsModuleType;

import team1502.configuration.CAN.*;
import team1502.configuration.builders.*;
import team1502.configuration.builders.power.*;

public class PneumaticsController extends PowerDistributionModule {
    private static final DeviceType deviceType = DeviceType.PneumaticsController;
    //public static final String CLASSNAME = deviceType.toString();

    // https://docs.wpilib.org/en/stable/docs/software/hardware-apis/pneumatics/index.html

    /** generic name for Pneumatics Controller, for now */
    public static final String PCM = "PCM"; // CAN(0) CTRE Pneumatics Control Module
    // public static final String REVPH = PneumaticsModuleType.REVPH.toString(); // CAN(1) https://www.revrobotics.com/rev-11-1852/
    public static final String Compressor = "Compressor";
    public static final String compressorPort = "Compressor Out";
    public static Function<IBuild, PneumaticsController> Define(Manufacturer manufacturer) {
        return build->new PneumaticsController(build,manufacturer);
    } 
    public static PneumaticsController Wrap(Builder builder) { return new PneumaticsController(builder.getIBuild(), builder.getPart()); }
    public static PneumaticsController WrapPart(Builder builder) { return WrapPart(builder, deviceType.name()); }
    public static PneumaticsController WrapPart(Builder builder, String partName) { return Wrap(builder.getPart(partName)); }

    // Define
    public PneumaticsController(IBuild build, Manufacturer manufacturer) {
        super(build, PneumaticsController.PCM, 16, deviceType, manufacturer);
        Category(CATEGORY_POWERHUB);
    
        AddPart(PowerChannel.Define(Name(), compressorPort), ch->ch.Fuse(50));
        // set pseudo fuses
        for (int ch = 0; ch < 16; ch++) {
            updateChannel(ch, 1); // 200mA per solenoid
        }
    }

    //Build Proxy / Eval
    public PneumaticsController(IBuild build, Part part) {
        super(build, part);
    }

    public PneumaticsController Compressor() {
        var compressor = addPart(Builder.DefineAs(Compressor));
        compressor.connectTo(this, compressorPort);
        return this;
    }

    public PneumaticsController Solenoid(int channel, String name) {
        updateChannel(channel, addPart(Solenoid.Define, name, c->c));
        return this;
    }

    public PneumaticsController DoubleSolenoid(int forwardChannel, int reverseChannel, String name) {
        updateChannel(reverseChannel, 
            addPart(Builder.DefineAs("DoubleSolenoid"), name, name, c->c));
        return this;
    }

    public PneumaticsController Ch(Integer channelNumber, Builder part) {
        updateChannel(channelNumber, part);
        return this;
    }

    public edu.wpi.first.wpilibj.Solenoid buildSolenoid(int channel) {
        var module = CanNumber();
        PneumaticsModuleType pneumaticsModuleType = CanInfo.WrapPart(this)
            .Manufacturer() == Manufacturer.CTRElectronics
                ? PneumaticsModuleType.CTREPCM
                : PneumaticsModuleType.REVPH;

        return new edu.wpi.first.wpilibj.Solenoid(module, pneumaticsModuleType, channel);
    }
}
