package team1502.configuration.factory;

import org.junit.jupiter.api.Test;

import edu.wpi.first.wpilibj.Solenoid;
import team1502.configuration.RobotConfigurations;
import team1502.configuration.builders.*;

public class FactoryTests {
    @Test
    public void partTest() {
        var config = RobotConfigurations.getConfiguration("");
        var parts = config.getBuilder().getParts();

        var factory = new TestBuilder(parts);
        factory.DumpParts();
        factory.reportUnconnected();
    }

    @Test
    public void usingTest() {
        var config = new RobotConfiguration();
        config.Build(pcm->pcm
            .PCM(ph -> ph.Solenoid(0, "Brake Solenoid"))
        );

        config.Build(arm -> arm
            .Subsystem("Arm", a -> a.Solenoid("FAKE", s->s) /*.Solenoid("Brake Solenoid", s->s.PCM(0) */)
        );
        var pcm = config.PCM();
        var ch = pcm.getChannel(0);
        var brakeSolenoid = ch.getConnectedPart();
        // var arm = config.Part("Arm"); // add a Part that referes to the pcm solenoid
        // arm.Value("brakeSolenoid", brakeSolenoid);


        var factory = new TestBuilder(config.getBuilder().getParts());
        factory.DumpParts();
        factory.showType(team1502.configuration.builders.pneumatics.Solenoid.CLASSNAME);
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
        );

        var parts = config.getBuilder().getParts();
        var factory = new TestBuilder(parts);
        factory.DumpParts();
    }
    
}
