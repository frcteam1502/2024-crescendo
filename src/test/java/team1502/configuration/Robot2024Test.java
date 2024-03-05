package team1502.configuration;

import org.junit.jupiter.api.Test;

import team1502.configuration.builders.power.PowerChannel;
import team1502.configuration.builders.power.PowerDistributionModule;

public class Robot2024Test {
    
    @Test
    public void buildRobotTest() {
        var config = RobotConfigurations.getConfiguration("");
        PowerChannel.getTotalPeakPower(config.PDH());
        printDetailedChannels(config.PDH());
        printDetailedChannels(config.MPM("MPM1"));
        printDetailedChannels(config.PCM());
    }

    private void printChannels(PowerDistributionModule pdh) {
        var ch20 = pdh.getChannel(20);
        echo(ch20.Connection().Host().ShortName());

        echo("");
        var formatter = MdFormatter.Table("PDH Channel Assignments")
            .Heading("Ch", "Abbr", "Device");
        for (var value : pdh.getChannels()){
            formatter.AddRow(
                value.Channel().toString(), 
                value.isConnected() ? value.Connection().Host().ShortName() : "",
                value.isConnected() ? value.Connection().Host().FriendlyName() : ""
                );
        }
        formatter.AsTable().forEach(row -> echo(row));

    }
    private void printDetailedChannels(PowerDistributionModule pdh) {

        echo("");
        var formatter = MdFormatter.Table("PDH Channel Assignments")
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
