package team1502.configuration;
import org.junit.jupiter.api.Test;

import team1502.configuration.builders.power.PowerDistributionModule;
import team1502.configuration.configurations.RobotConfigurations;

public class configurationTests {

    @Test
    public void buildRobotTest() {
        var config = RobotConfigurations.getConfiguration("");
        var channelNames = config.PDH().ChannelNames();
        var pneumaticChannelNames = config.PCM().ChannelNames();
        var evals = config.Values().GetValueKeys();
        for (String valueName : evals) {
            System.out.println(valueName + ": " + config.Values().getValue(valueName).toString());
        };

        config.Build(r -> r
            .Part("part1", p->p.CanNumber(99))
        );

        var part1 = config.Part("part1");
        part1.PeakPower(1_000.0);
        var m1 = part1.Manufacturer();
        var pp1 = part1.TotalPeakPower();

        //var gyro = config.Pigeon2().buildPigeon2();


    }

    private void Dump(PowerDistributionModule pdm) {

    }
}
