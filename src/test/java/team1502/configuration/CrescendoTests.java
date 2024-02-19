package team1502.configuration;

import org.junit.jupiter.api.Test;

import edu.wpi.first.wpilibj.PneumaticsControlModule;
import team1502.configuration.builders.pneumatics.PneumaticsController;
import team1502.configuration.builders.power.PowerDistributionModule;

public class CrescendoTests {
    
    @Test
    public void buildRobotTest() {
        var config = frc.robot.RobotConfigurations.getConfiguration("");

        printDetailedChannels("PDH", config.PDH());
        printDetailedChannels("MPM1", config.MPM("MPM1"));
        printDetailedChannels("MPM2", config.MPM("MPM2"));
    }

    private void printChannels(PowerDistributionModule pdh) {
        var ch20 = pdh.getChannel(20);
        echo(ch20.Part().ShortName());

        echo("");
        var formatter = MdFormatter.Table("PDH Channel Assignments")
            .Heading("Ch", "Abbr", "Device");
        for (var value : pdh.getChannels()){
            formatter.AddRow(
                value.Channel().toString(), 
                value.hasPart() ? value.Part().ShortName() : "",
                value.hasPart() ? value.Part().FriendlyName() : ""
                );
        }
        formatter.AsTable().forEach(row -> echo(row));

    }

    // private void printDetailedChannels(String name, PneumaticsController pcm) {
    //     for (var value : pcm.getChannels()){
    // }

    private void printDetailedChannels(String name, PowerDistributionModule pdh) {
        echo("");
        var formatter = MdFormatter.Table(name + " Channel Assignments")
            .Heading("Ch", "Amps", "Wire", "Abbr", "Device", "Watts");
        for (var value : pdh.getChannels()){
            formatter.AddRow(
                value.Channel().toString(),
                value.hasFuse() ? value.Fuse().toString() : "",
                value.WireLabel(),
                value.hasPart() ? value.Part().ShortName() : "",
                value.hasPart() ? value.Part().FriendlyName() : "",
                value.hasPart() ? value.ChannelPower().toString() : ""
                );
        }
        formatter.AsTable().forEach(row -> echo(row));

    }

    public static void echo(String text) {System.out.println(text);}

}
