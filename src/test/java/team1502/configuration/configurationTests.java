package team1502.configuration;
import java.util.Collections;

import org.junit.jupiter.api.Test;

public class configurationTests {

    @Test
    public void buildRobotTest() {
        var config = RobotConfigurations.getConfiguration("");
        var channelNames = config.PDH().ChannelNames();
        var pneumaticChannelNames = config.PCM().ChannelNames();
        var channelNamesAbbr = config.PDH().ChannelNamesAbbr();
        var pneumaticChannelNamesAbbr = config.PCM().ChannelNamesAbbr();
        
        var one = config.Subsystem("One");
        var a1 = one.Value("a");
        var abs = one.Encoder("AbsEncoder");
        var ch5 = abs.DigitalInput();
        var two = one.Subsystem("Two");
        var mc2 = two.MotorController("motorTwo");
        
        var evals = config.Values().GetValueKeys();
        Collections.sort(evals);
        for (String valueName : evals) {
            System.out.println(valueName + config.Values().getValue(valueName).toString());
        };

        if (!true) { // dead, but referencing things that exist in robot code
            var gyro = config.Pigeon2().buildPigeon2();
            var pid1 = config.SwerveModule("#1").TurningMotor().PID().createPIDController();
            var mtr1 = config.SwerveModule("#1").DrivingMotor().buildSparkMax();
            var pid2 = config.SwerveModule("#1").DrivingMotor().createPIDController();
            var rel1 = config.SwerveModule("#1").DrivingMotor().buildRelativeEncoder();
        }

    }

}
