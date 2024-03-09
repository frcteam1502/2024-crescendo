package team1502.configuration.factory;

import java.util.function.Function;

import org.junit.jupiter.api.Test;

import team1502.configuration.RobotConfigurations;
import team1502.configuration.CAN.DeviceType;
import team1502.configuration.CAN.Manufacturer;
import team1502.configuration.builders.*;
import team1502.configuration.builders.pneumatics.Solenoid;

public class FactoryTests {
    @Test
    public void partTest() {
        var config = RobotConfigurations.getConfiguration("");
        var parts = config.getBuilder().getParts();

        var factory = new TestBuilder(parts);
        factory.DumpParts2b();
        factory.reportUnconnected();
        
        var roboRIO = config.getBuilder().getInstalled("RoboRIO");
        factory.reportCanBus(roboRIO.getPart(Channel.SIGNAL_CAN));

    }

    @Test
    public void usingTest() {
        var config = new RobotConfiguration();
        config.Build(pcm->pcm
            .PCM(ph -> ph
                .Solenoid(0, "Brake Solenoid")
                .CanNumber(1)
            )
        );

        config.Build(arm -> arm
            .Subsystem("Arm", a -> a
                .usePart(a.getSubsystemPart(), "brakeSolenoid", s->s
                    .PCM().getChannel(0).getConnectedPart()
                )
                .Part("Part1", p->p
                    .usePart("Brake Solenoid")
                 )
            )
        );
        var pcm = config.PCM();
        var ch = pcm.getChannel(0);
        var brakeSolenoid = ch.getConnectedPart();
        var arm = config.Part("Arm"); // add a Part that referes to the pcm solenoid
        var solenoid = Solenoid.Wrap(arm.Part("brakeSolenoid"));
        if (solenoid.getPart() != brakeSolenoid.getPart()) throw new AssertionError();
        var part1Solenoid = Solenoid.Wrap(arm.Part("Part1").Part("Brake Solenoid"));
        //var instance = solenoid.buildSolenoid();

        var factory = new TestBuilder(config.getBuilder().getParts());
        factory.DumpParts();
        factory.showType(team1502.configuration.builders.pneumatics.Solenoid.CLASSNAME);
    }

    RobotBuilder usePart(Builder user, String key, RobotBuilder rb, Function<RobotBuilder, Builder> path) {
        user.Value(key, path.apply(rb).getPart());
        return rb;
    }

    @Test
    public void rioTest1() {
        var config = new RobotConfiguration();
        config.Parts(define->define
            .RoboRIO(r -> r.Version("2.0").PeakPower(45.0).Abbreviation("RIO")) // +RSL 0.6 ??
        );
        config.Build(hw->hw
            .Part("Part1", p->p.Install("RoboRIO", null))
            //.RoboRIO(null)
            .Part("Part2", p->p.CanInfo(DeviceType.GearToothSensor, Manufacturer.DEKA, 1))
        );

        
        var parts = config.getBuilder().getParts();
        var factory = new TestBuilder(parts);
        factory.DumpParts();
        var roboRIO = config.getBuilder().getInstalled("RoboRIO");
        factory.reportCanBus(roboRIO.getPart(Channel.SIGNAL_CAN));
    }

    @Test
    public void subsystemTest() {
        var config = new RobotConfiguration();
        config.Build(hw->hw
            .Subsystem("frc.robot.Arm", arm->arm
                .Part("Part1", p->p)
            )
        );

        var arm = config.findSubsystemConfiguration("frc.robot.Arm");
    }

}
