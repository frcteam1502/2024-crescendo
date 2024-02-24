package team1502.configuration.builders;

import java.util.ArrayList;
import java.util.function.Function;

import org.junit.jupiter.api.Test;

import team1502.configuration.factory.PartBuilder;

public class BuilderTests {
    
    @Test
    public void testConnector() {
        var factory = new TestBuilder();

        Object ch = 2;
        Object chT = "2";
        if (ch instanceof Integer) {
            System.out.println("2 is and Integer");
        }
        if (chT instanceof String) {
            System.out.println("'2' is and String");
        }
        
        var part1 = factory.createPart("Part1");
        var c1 = part1.createConnector("12VDC");
        System.out.println(c1.getPart().getKey());
        var c1a = part1.findConnector("12VDC");
        var c2x = part1.findConnector("24VDC"); // just make sure it doesn't crash
        System.out.println(c1a.getPart().getKey());
    }

    class TestBuilder implements IBuild {
        private ArrayList<Part> _parts = new ArrayList<>(); // every part created

        // public Builder createPart(String partName, Function<Builder, Builder> fn) {
        // }
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
            // TODO Auto-generated method stub
            throw new UnsupportedOperationException("Unimplemented method 'getInstalled'");
        }
        
    }
}
