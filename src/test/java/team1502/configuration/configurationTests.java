package team1502.configuration;
import java.util.Collections;

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
        Collections.sort(evals);
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
        // var pid1 = config.SwerveModule("#1").TurningMotor().PID().createPIDController();
        // var mtr1 = config.SwerveModule("#1").DrivingMotor().buildSparkMax();
        // var pid2 = config.SwerveModule("#1").DrivingMotor().createPIDController();

    }

    private void Dump(PowerDistributionModule pdm) {

    }
}
