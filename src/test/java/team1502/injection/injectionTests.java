package team1502.injection;

import edu.wpi.first.wpilibj2.command.Subsystem;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import org.junit.jupiter.api.Test;

import frc.robot.Robot;

public class injectionTests {
    
    @Test
    public void createFactor() throws ClassNotFoundException {
        RobotFactory factory = RobotFactory.Create();
        factory.gather();
        /*
            Found ExampleSubsystem, enabled=true
            Found DriveSubsystem, enabled=true
            Found ExampleCommand, enabled=true
            Found ControllerCommands, enabled=true

         */
       
    }

    @Test
    public void findSubsystems() throws IOException {
        // 2024 complication: subsystems in sub directories, need to dig deeper
        final Class<Robot> robotClass = Robot.class;
        final String subsystemPackageName = "frc/robot/subsystems";
        ClassLoader loader = robotClass.getClassLoader();
        // var urls = loader.getResources("");
        // /C:/Users/jonathan.berent/source/repos/frc/2024/2024-crescendo-config/bin/main/


        ArrayList<String> classes = new ArrayList<>();
        findClassesIn(subsystemPackageName, loader, classes);

        var subsystems = classes.stream()
            //.map(name -> loader.loadClass(name))
            .map(name -> {
                try {
                    return loader.loadClass(name);
                } catch (ClassNotFoundException e) {
                    return null;
                }
            })
            .filter(candidate -> SubsystemFactory.isSubsystem(candidate))
            .map(candidate -> (Class<Subsystem>)candidate)
            .toList();

            subsystems.forEach(ss -> System.out.println(ss.getName()));
    }

    private void findClassesIn(String folder, ClassLoader loader, ArrayList<String> classes) throws IOException {
        InputStream stream = loader.getResourceAsStream(folder);
        BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
        while (reader.ready()) {
            String line = reader.readLine();
            if (line.endsWith(".class")) {
                String className = line.substring(0, line.length()-6);
                String packageName = folder.replaceAll("[/]", ".");
                classes.add(packageName + "." + className);
            } else {
                findClassesIn(folder + "/" + line, loader, classes);            
            }
        }
        
    }
}
