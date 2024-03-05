package team1502.configuration;

import org.junit.jupiter.api.Test;

import edu.wpi.first.wpilibj.PneumaticsControlModule;
import team1502.configuration.builders.pneumatics.PneumaticsController;
import team1502.configuration.builders.power.PowerDistributionModule;
import team1502.configuration.factory.RobotConfiguration;

public class CrescendoTests {
    
    @Test
    public void buildRobotTest() {
        var config = frc.robot.RobotConfigurations.getConfiguration("");
         
        printDetailedChannels("PDH", config.PDH());
        printDetailedChannels("MPM1", config.MPM("MPM1"));
        printDetailedChannels("MPM2", config.MPM("MPM2"));
        printDetailedChannels("Pneumatics Hub", config.PCM());
    }


    private void printDetailedChannels(String hub, PowerDistributionModule pdh) {

        echo("");
        var formatter = MdFormatter.Table(hub + " Channel Assignments")
            .Heading("Ch", "Amps", "Wire", "Abbr", "Device", "Watts");
        for (var value : pdh.getChannels()){
            formatter.AddRow(
                value.Channel().toString(),
                value.hasFuse() ? value.Fuse().toString() : "",
                value.Label(),
                value.isConnected() ? value.Connection().Host().ShortName() : "",
                value.isConnected() ? value.Connection().Host().FriendlyName() : "",
                value.hasValue("totalPeakPower") ? value.getDouble("totalPeakPower").toString() : ""
                );
        }
        formatter.AsTable().forEach(row -> echo(row));

    }

    public static void echo(String text) {System.out.println(text);}

}
