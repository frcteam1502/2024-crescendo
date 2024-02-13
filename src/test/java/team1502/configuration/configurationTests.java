package team1502.configuration;
import java.util.Collections;

import org.junit.jupiter.api.Test;

import edu.wpi.first.wpilibj2.command.Subsystem;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import team1502.configuration.builders.power.PowerDistributionModule;
import team1502.configuration.configurations.RobotConfigurations;
import team1502.injection.*;

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
        // var rel1 = config.SwerveModule("#1").DrivingMotor().buildRelativeEncoder();

    }

    class Subsystem1 extends SubsystemBase {
        
    }
    class Subsystem2 implements Subsystem {
        
    }

    @Test
    public void isAssignableFromTest() {
        var a1 = SubsystemFactory.class.isAssignableFrom(RobotPart.class);
        var a2 = RobotPart.class.isAssignableFrom(SubsystemFactory.class); // true
        var a3 = CommandFactory.class.isAssignableFrom(SubsystemFactory.class);
        Class<?> class1 = Subsystem1.class;
        Class<?> class2 = Subsystem2.class;
        var a4 = !SubsystemBase.class.isAssignableFrom(class1);
        var a5 = !SubsystemBase.class.isAssignableFrom(class2);

    }
    private void Dump(PowerDistributionModule pdm) {

    }
}
