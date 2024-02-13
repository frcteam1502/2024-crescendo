package team1502.configuration;

import org.junit.jupiter.api.Test;

import team1502.configuration.builders.power.PowerDistributionModule;

public class Robot2024Test {
    
    @Test
    public void buildRobotTest() {
        var config = RobotConfigurations.getConfiguration("");

        printDetailedChannels(config.PDH());
        printDetailedChannels(config.MPM(PowerDistributionModule.MPM));
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
    private void printDetailedChannels(PowerDistributionModule pdh) {

        echo("");
        var formatter = MdFormatter.Table("PDH Channel Assignments")
            .Heading("Ch", "Amps", "Wire", "Abbr", "Device", "Watts");
        for (var value : pdh.getChannels()){
            formatter.AddRow(
                value.Channel().toString(),
                value.hasFuze() ? value.Fuze().toString() : "",
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
