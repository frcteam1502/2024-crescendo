package team1502.configuration.builders;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.function.Function;

import org.junit.jupiter.api.Test;

import team1502.configuration.MdFormatter;
import team1502.configuration.CAN.*;
import team1502.configuration.builders.power.PowerChannel;
import team1502.configuration.builders.power.PowerDistributionModule;
import team1502.configuration.factory.PartBuilder;

public class BuilderTests {
    
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
        part3.addChannel(Channel.SIGNAL_12VDC, part4);

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
        hub1.addChannel(Channel.SIGNAL_12VDC, "Compressor");

        part1.connectTo(hub1, "Compressor");
        System.out.println("1a: " +  PowerChannel.getTotalPeakPower(hub1));

        var part2 = factory.createPart("Part2", p->p.PeakPower(1.0));
        part2.connectTo(hub1, 1);
        System.out.println("2a: " +  PowerChannel.getTotalPeakPower(part2));
        System.out.println("2b: " +  PowerChannel.getTotalPeakPower(hub1));

        var part3 = factory.createPart("Part3", p->p.PeakPower(5.0));
        part1.addChannel(Channel.SIGNAL_12VDC, "Relay");
        part3.connectTo(part1, "Relay");
        var part4 = factory.createPart("Part4", p->p.PeakPower(7.0));
        part1.addChannel(Channel.SIGNAL_12VDC, 0);
        part4.connectTo(part1, 0);

        System.out.println("3a: " +  PowerChannel.getTotalPeakPower(part4));
        System.out.println("3b: " +  PowerChannel.getTotalPeakPower(part1));
        System.out.println("3c: " +  PowerChannel.getTotalPeakPower(hub1));
        
        factory.reportPartPower();
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

    class TestBuilder implements IBuild {
        private ArrayList<Part> _parts = new ArrayList<>(); // every part created

        public Builder createPart(String partName, Function<Builder, Builder> fn) {
            var part = createPart(partName);
            fn.apply(part);
            return part;
        }
        public Builder createPart(String partName) {

            return Builder.DefineAs(partName).apply(this);
        }

        @Override
        public <T extends Builder> PartBuilder<?> getTemplate(String partName, Function<IBuild, T> createFunction,
                Function<T, Builder> buildFunction) {
            // TODO Auto-generated method stub
            throw new UnsupportedOperationException("Unimplemented method 'getTemplate'");
        }

        @Override
        public void register(Part part) {
            _parts.add(part);
        }

        @Override
        public Builder getInstalled(String name) {
            for (Part part : _parts) {
                if (part.getName() == name) {
                    return new Builder(this, part);
                }
            }
            return null;
        }

        public void DumpParts() {
            var keySet = new HashSet<String>();


            var formatter = MdFormatter.Table("Parts Registered")
                .Heading("buildName", "originalName", "buildType", "Key", "Friendly Name");

            for (Part part : _parts) {
                var key = part.getKey();
                if (!keySet.add(key)) {
                    System.out.println(key + " is a duplicate");
                }
                formatter.AddRow(
                    part.hasValue(Part.BUILD_NAME) ? (String)part.getValue(Part.BUILD_NAME) : "",
                    part.hasValue(Part.ORIGINAL_NAME) ? (String)part.getValue(Part.ORIGINAL_NAME) : "",
                    part.hasValue(Builder.BUILD_TYPE) ? (String)part.getValue(Builder.BUILD_TYPE) : "",
                    part.getKey(),
                    part.hasValue(Builder.friendlyName) ? (String)part.getValue(Builder.friendlyName) : "");
            }
            formatter.PrintTable();

        }

        public void reportPartPower() {
            var formatter = MdFormatter.Table("Parts Power")
                .Heading("Watts", "Total", "Key", "Friendly Name");

            var parts = _parts.stream().map(p->new Builder(this, p)).toList();
            for (Builder part : parts) {
                var key = part.getPart().getKey();
                formatter.AddRow(
                    part.hasPowerProfile() ? part.PowerProfile().PeakPower().toString() : "",
                    part.hasValue("totalPeakPower") ? part.getDouble("totalPeakPower").toString() : "",
                    key,
                    part.hasValue(Builder.friendlyName) ? (String)part.getValue(Builder.friendlyName) : "");
            }
            formatter.PrintTable();

        }
        
        public void reportUnconnected() {
            var connectors = Connector.findConnectors(_parts);
            var unconnected = connectors.stream().filter(c->!c.isConnected()).toList();
            if (!unconnected.isEmpty()) {
                var formatter = MdFormatter.Table("WARNING: Unconnected connectors")
                .Heading("Part", "Friendly", "Signal", "Host");
    
               for (Connector part : unconnected) {
                formatter.AddRow(
                    part.getPart().getKey(),
                    part.FriendlyName(),
                    part.Signal(),
                    part.Host().FriendlyName()
                    );    
                }
                formatter.PrintTable();

            }
        }

        public void reportCanBus(Builder bus) {
            var map = new CanMap(bus);

            var formatter = MdFormatter.Table("Channels for " + bus.Name())
                .Heading("Part", "Friendly", "Number", "Device", "Problem");

            for (Builder part : map.getDevices()) {
                var caninfo = CanInfo.WrapPart(part);
                formatter.AddRow(
                    part.getPart().getKey(),
                    part.FriendlyName(),
                    caninfo.Number().toString(),
                    caninfo.FriendlyName(),
                    part.hasErrors() ? part.getErrors().get(0) : ""
                );    
            }
            formatter.PrintTable();

        }
        public void DumpChannels(Builder hub) {
           var partChannels = Channel.getPartChannels(hub);
           var pieceChannels = Channel.getPieceChannels(hub);

           var formatter = MdFormatter.Table("Channels for " + hub.Name())
            .Heading("Part", "Friendly", "Signal", "Network", "Connection");

           for (Channel part : partChannels) {
            formatter.AddRow(
                part.getPart().getKey(),
                part.FriendlyName(),
                part.Signal(),
                part.Network(),
                part.isConnected() ? part.Connection().getPart().getKey() : ""
                );    
            }
           for (Channel part : pieceChannels) {
            formatter.AddRow(
                part.getPart().getKey(),
                part.FriendlyName(),
                part.Signal(),
                part.Network(),
                part.isConnected() ? part.Connection().getPart().getKey() : ""
                );    
            }
            formatter.PrintTable();

        }
    }
}
