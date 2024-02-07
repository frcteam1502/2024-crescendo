package team1502.configuration;
import org.junit.jupiter.api.Test;

import team1502.configuration.configurations.RobotConfigurations;

public class configurationTests {

    @Test
    public void buildRobotTest() {
        var config = RobotConfigurations.getConfiguration("");
        var channelNames = config.PDH().ChannelNames();
        var evals = config.Values().GetValueKeys();
        for (String valueName : evals) {
            System.out.println(valueName + ": " + config.Values().getValue(valueName).toString());
        };

    }

}
