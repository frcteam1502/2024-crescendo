package team1502.configuration.factory;

import java.util.HashSet;
import java.util.Set;

import org.junit.jupiter.api.Test;

import team1502.configuration.MdFormatter;
import team1502.configuration.RobotConfigurations;
import team1502.configuration.builders.*;

public class FactoryTests {
    @Test
    public void partTest() {
        var config = RobotConfigurations.getConfiguration("");
        var parts = config.getBuilder().getParts();

        var keySet = new HashSet<String>();


        var formatter = MdFormatter.Table("Parts Registered")
            .Heading("buildName", "originalName", "buildType", "Key");

        for (Part part : parts) {
            var key = part.getKey();
            if (!keySet.add(key)) {
                System.out.println(key + " is a duplicate");
            }
            formatter.AddRow(
                part.hasValue(Part.BUILD_NAME) ? (String)part.getValue(Part.BUILD_NAME) : "",
                part.hasValue(Part.ORIGINAL_NAME) ? (String)part.getValue(Part.ORIGINAL_NAME) : "",
                part.hasValue(Builder.BUILD_TYPE) ? (String)part.getValue(Builder.BUILD_TYPE) : "",
                part.getKey());
            //System.out.println(part.getName());
           // System.out.println(part.getKey());
            
        }
        formatter.PrintTable();

    }
    
}
