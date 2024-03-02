package team1502.configuration.builders;

import org.junit.jupiter.api.Test;

import team1502.configuration.CAN.*;
import team1502.configuration.builders.motors.MotorController;
import team1502.configuration.builders.motors.SwerveModule;
import team1502.configuration.builders.power.PowerChannel;
import team1502.configuration.builders.power.PowerDistributionModule;

public class BuilderTests {
    public static final String POWER = Channel.SIGNAL_12VDC;

    @Test
    public void parentTest() {
        var factory = new TestBuilder();
        var module1 = SwerveModule.Define.apply(factory);
        var mc1 = module1.addPart(MotorController.Define(Manufacturer.Copperforge));

        var sm1 = mc1.getParentOfType(SwerveModule.NAME);
        if (sm1 == null) {throw new AssertionError();}
    }

    @Test
    public void testConnector() {
        var factory = new TestBuilder();
        
        // hub
        var hub1 = factory.createPart("hub1");
        hub1.addChannel("12VDC", "Vin");
        hub1.addChannel("12VDC", 0);
        hub1.addChannel("12VDC", 1);

        var ctr1 = Channel.findChannel(hub1,"Vin");
        System.out.println(ctr1.getPart().getKey());

        var part1 = factory.createPart("Part1");
        var c1 = part1.addConnector("12VDC", "Vcc");
        System.out.println(c1.getPart().getKey());

        var c1a = part1.findConnector("12VDC");
        var c2x = part1.findConnector("24VDC"); // just make sure it doesn't crash
        System.out.println(c1a.getPart().getKey());
        
        part1.connectTo(hub1, "Vin");

        var part2 = factory.createPart("Part2");
        part2.connectTo(hub1, 0);

        var part3 = factory.createPart("Part3");
        var part4 = factory.createPart("Part4");
        part3.addChannel(POWER, part4);

        System.out.println(ctr1.Connection().Host().getPart().getKey());

        factory.DumpParts();
        factory.DumpChannels(hub1);
        factory.DumpChannels(part3);
        factory.DumpChannels(part4);

    }

    @Test
    public void canTest() { // CAN does not have pre-assigned channels
        var factory = new TestBuilder();
        
        // hub
        var hub = factory.createPart("Hub");

        var part1 = factory.createPart("Part1");
        CanInfo.addConnector(part1, DeviceType.MotorController, Manufacturer.KauaiLabs, 1);

        var cn = part1.CanNumber();
        
        hub.addChannel(Channel.SIGNAL_CAN, part1);
        
        factory.reportCanBus(hub);

        factory.DumpParts();
        factory.DumpChannels(hub);
    
    }

    @Test
    public void powerTest() {
        var factory = new TestBuilder();
        
        var hub1 = factory.createPart("hub1");
        System.out.println("0: " +  PowerChannel.getTotalPeakPower(hub1));
        
        hub1.addPiece(PowerChannel.Define("hub1", 0));
        hub1.addPiece(PowerChannel.Define("hub1", 2));
        System.out.println("1: " +  PowerChannel.getTotalPeakPower(hub1));

        var part1 = factory.createPart("Part1", p->p.PeakPower(3.0));
        hub1.addChannel(POWER, "Compressor");

        part1.connectTo("hub1", "Compressor", "Compressor Vcc");
        System.out.println("1a: " +  PowerChannel.getTotalPeakPower(hub1));

        var part2 = factory.createPart("Part2", p->p.PeakPower(1.0));
        part2.connectTo(hub1, 1);
        System.out.println("2a: " +  PowerChannel.getTotalPeakPower(part2));
        System.out.println("2b: " +  PowerChannel.getTotalPeakPower(hub1));

        var part3 = factory.createPart("Part3", p->p.PeakPower(5.0));
        part1.addChannel(POWER, "Relay");
        part3.connectTo(part1, "Relay");
        var part4 = factory.createPart("Part4", p->p.PeakPower(7.0));
        part1.addChannel(POWER, 0);
        part4.connectTo(part1, 0);

        System.out.println("3a: " +  PowerChannel.getTotalPeakPower(part4));
        System.out.println("3b: " +  PowerChannel.getTotalPeakPower(part1));
        System.out.println("3c: " +  PowerChannel.getTotalPeakPower(hub1));
        
        factory.reportPartPower();
        factory.DumpParts();
    }

    @Test
    public void mockRioTest() { // rio has CAN PWM etc.
        var factory = new TestBuilder();
        
        // hub
        var hub = factory.createPart(RoboRIO.NAME);
        hub.AddPart(Builder.DefineAs(Channel.SIGNAL_CAN), p->p)
           .AddPart(Builder.DefineAs(Channel.SIGNAL_PWM), p->p
                .Channel(Channel.SIGNAL_PWM, 0)
                .Channel(Channel.SIGNAL_PWM, 1)
            );

        var part1 = factory.createPart("Part1");
        CanInfo.addConnector(part1, DeviceType.MotorController, Manufacturer.KauaiLabs, 1);
        
        var part2 = factory.createPart("Part2");


        //hub.addChannel(Channel.SIGNAL_CAN, part1);
        //hub.connect(Channel.SIGNAL_PWM, part2, 0);
        // part1.connectTo(hub);
        factory.DumpParts();
        part2.connectTo(hub, Channel.SIGNAL_PWM, 0);


        factory.DumpParts();
        factory.DumpChannels(hub.getPart(Channel.SIGNAL_CAN));
        factory.DumpChannels(hub.getPart(Channel.SIGNAL_PWM));
    
    }

    @Test void rioTest(){
        var factory = new TestBuilder();

        var rio = RoboRIO.Define.apply(factory);
        var pdh = PowerDistributionModule.DefinePDH.apply(factory);
        pdh.CanNumber(1);
        rio.PDH(20);

        var part1 = factory.createPart("Part1");
        CanInfo.addConnector(part1, DeviceType.MotorController, Manufacturer.KauaiLabs, 1);

        var part2 = factory.createPart("Part2");
        var part3 = factory.createPart("Part3");
        CanInfo.addConnector(part3, DeviceType.MotorController, Manufacturer.KauaiLabs, 1);

        var part4 = factory.createPart("Part4");
        CanInfo.addConnector(part4, DeviceType.MotorController, Manufacturer.KauaiLabs);
        factory.reportCanBus(rio.CAN());

        part4.CanNumber(1);

        part2.connectTo(rio, Channel.SIGNAL_PWM, 0);
        part3.connectTo(rio, Channel.SIGNAL_DIO, 1);
        factory.DumpParts();
        factory.DumpChannels(rio.getPart(Channel.SIGNAL_CAN));
        factory.DumpChannels(rio.getPart(Channel.SIGNAL_PWM));
        factory.DumpChannels(rio.getPart(Channel.SIGNAL_DIO));
        
        factory.reportCanBus(rio.CAN());
        factory.reportUnconnected(); // Part4:CAN, roboRIO:12VDC

    }
}
