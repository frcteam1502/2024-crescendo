package team1502.configuration.factory;

import org.junit.jupiter.api.Test;

import team1502.configuration.builders.*;

public class CrescendoFactoryTests {
 
    @Test
    public void partTest() {
        var config = frc.robot.RobotConfigurations.getConfiguration("");
        var parts = config.getBuilder().getParts();

        var factory = new TestBuilder(parts);
        factory.DumpParts();
        factory.reportUnconnected();
        
        var roboRIO = config.getBuilder().getInstalled("RoboRIO");
        factory.reportCanBus(roboRIO.getPart(Channel.SIGNAL_CAN));

    }
    

}
