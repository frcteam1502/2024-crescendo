package team1502.injection;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.stream.Collectors;

import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.Subsystem;
import frc.robot.Robot;
import team1502.configuration.annotations.*;
import team1502.configuration.factory.RobotConfiguration;

public class RobotFactory {
    public static Class<SubsystemInfo> subsystemAnnotation = SubsystemInfo.class;
    private static Class<Robot> robotClass = Robot.class;
    private static String subsystemPackageName = "frc/robot/subsystems";
    private static String commandPackageName = "frc/robot/commands";

    public static RobotFactory Create() throws ClassNotFoundException {
        return new RobotFactory();
    }

    public static RobotFactory Create(RobotConfiguration config) {
        var factory = new RobotFactory(config);
        factory.start();
        return factory;        
    }

    private RobotFactory() {}

    private RobotFactory(RobotConfiguration config) {
        configuration = config;
    }

    public RobotConfiguration getRobotConfiguration() { return configuration; }

    private ArrayList<RobotPart> parts = new ArrayList<>();
    private HashMap<String, RobotPart> partMap = new HashMap<>();
    private HashMap<String, SubsystemFactory> subsystemMap = new HashMap<>();
    private HashMap<String, CommandFactory> commandMap = new HashMap<>();
    private List<SubsystemFactory> subsystemFactories;
    private List<CommandFactory> commandFactories;
    private RobotConfiguration configuration;
    private final File jarFile = new File(getClass().getProtectionDomain().getCodeSource().getLocation().getPath());

    private void start() {
        System.out.println("FACTORY: Start");
        gatherSubsystems();
        System.out.println("FACTORY: " + parts.size() + " Subsystems found");
        build();
        for (var part : parts) {
            System.out.println("FACTORY: " + part.getName() + (part.isBuilt() ? "built" : "not built"));
        }
    }

    public void gather() {
        gatherSubsystems();
        gatherCommands();
    }

    public void gatherSubsystems() {
        subsystemFactories = getSubsystemFactories();
        for (SubsystemFactory subsystemFactory : subsystemFactories) {
            System.out.println("Found " + subsystemFactory.getName() + ", enabled=" + (subsystemFactory.isEnabled() ? "true" : "False"));
            subsystemMap.put(subsystemFactory.getName(), subsystemFactory);
            addPart(subsystemFactory);
        }
    }

    // It should be noted that "commands" aren't necessarily created at the beginning
    // e.g., a drive controller would be the default controller for the drive subsystem
    // but there could be other "task" based commands, triggered by some event
    public void gatherCommands() {
        commandFactories = getCommandFactories();
        for (CommandFactory commandFactory : commandFactories) {
            System.out.println("Found " + commandFactory.getName() + ", enabled=" + (commandFactory.isEnabled() ? "true" : "False"));
            commandMap.put(commandFactory.getName(), commandFactory);
            addPart(commandFactory);
        }
    }

    private RobotPart addPart(RobotPart part) {
        parts.add(part);
        partMap.put(part.getName(), part);
        if (configuration.isDisabled(part.getName())) {
            part.Disable();
        }
        return part;
    }

    private RobotPart needPart(Class<?> partClass) {
        var part = getPart(partClass);
        return (part != null) 
            ? part
            : addPart(new RobotPart(partClass));        
    }

    public RobotPart getPart(Class<?> partClass) {
        return partMap.get(partClass.getName());
    }

    @SuppressWarnings("unchecked")
    public <T> T getInstance(Class<T> partClass) {
        var part = getPart(partClass);
        return part == null ? null : (T)part.getPart();
    }
    
    int systemSize;
    private void build() {
        systemSize = parts.size(); // just iterate over the subsytems
        addPart(new RobotPart(configuration));
        buildParts();
    }

    private void buildParts() {
        gatherCommands();
        for (int i = 0; i < systemSize; i++) {
            var part = parts.get(i);
            buildPart(part);
        }
    }

    private Object buildPart(RobotPart part) {
        Object instance = null;
        if (!part.isBuilt() && part.isEnabled()) {
            var dependencies = part.getDependencies();
            Object[] args = new Object[dependencies.length];
            for (int i = 0; i < args.length; i++) {
                var dependency = needPart(dependencies[i]);
                if (dependency.isDisabled()) {
                    part.Disable();
                    break;
                }
                if (!dependency.isBuilt()) {
                    buildPart(dependency);
                }
                args[i] = prepDependency(part, dependency);
            }
            
            // check for null args??
            if (part.isEnabled()) { 
                instance = part.Build(args);
                if (part.hasDefaultCommand()) {
                    var command = (Command)buildPart(needPart(part.getDefaultCommand()));
                    var subsystem = (Subsystem)instance;
                    subsystem.setDefaultCommand(command);
                }
            }
        }
        return instance;
    }

    /** E.g., find a sub-config for a subsystem */
    private Object prepDependency(RobotPart part, RobotPart dependency) {
        var instance = dependency.getPart();
        if (instance instanceof RobotConfiguration) {
            var config = (RobotConfiguration)instance;
            var subsystem = config.findSubsystemConfiguration(part.getName());
            if (subsystem != null) {
                instance = subsystem;
            }
        }
        return instance;
    }

    private List<SubsystemFactory> getSubsystemFactories() {
        Set<Class<Subsystem>> subsystems = getAllSubsystems();
        return subsystems.stream()
            .map(ss->new SubsystemFactory(ss))
            .toList();
    }

    private List<CommandFactory> getCommandFactories() {
        Set<Class<Command>> commands = getAllCommands();
        return commands.stream()
            .map(ss->new CommandFactory(ss))
            .toList();
    }
    

    @SuppressWarnings("unchecked")
    private Set<Class<Subsystem>> getAllSubsystems() {
        ClassLoader loader = robotClass.getClassLoader();
        ArrayList<String> classes = new ArrayList<>();
        
        try {
            if(jarFile.isFile()) {  // Run with JAR file
                getClasses(subsystemPackageName, classes);
            } else {
                findClassesIn(subsystemPackageName, loader, classes);
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return classes.stream()
            .map(name -> getClass(loader, name))
            .filter(candidate -> SubsystemFactory.isSubsystem(candidate))
            .map(candidate -> (Class<Subsystem>)candidate)
            .collect(Collectors.toSet());
    }

    @SuppressWarnings("unchecked")
    private Set<Class<Command>> getAllCommands() {
        ClassLoader loader = robotClass.getClassLoader();
        ArrayList<String> classes = new ArrayList<>();
        
        try {
            if(jarFile.isFile()) {  // Run with JAR file
                getClasses(commandPackageName, classes);
            } else {
                findClassesIn(commandPackageName, loader, classes);
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return classes.stream()
            .map(name -> getClass(loader, name))
            .filter(line -> CommandFactory.isSubsystem(line))
            .map(line -> (Class<Command>)line)
            .collect(Collectors.toSet());
    }
 

    private Class<?> getClass(ClassLoader loader, String name) {
        try {
            return loader.loadClass(name);
        } catch (ClassNotFoundException e) {
            System.err.println("FACTORY :: unable to load: " + name);
            return null;
        }
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

    private void getClasses(String path, ArrayList<String> classes) throws IOException {
        if(jarFile.isFile()) {  // Run with JAR file
            final JarFile jar = new JarFile(jarFile);
            final Enumeration<JarEntry> entries = jar.entries(); //gives ALL entries in jar
            while(entries.hasMoreElements()) {
                final String name = entries.nextElement().getName();
                if (name.startsWith(path + "/") && name.endsWith(".class")) {
                    classes.add(name.substring(0, name.length()-6)
                                    .replaceAll("[/]", "."));
                }
            }
            jar.close();
        }
    }

}
