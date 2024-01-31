package team1502.configuration.configurations;

import com.revrobotics.CANSparkBase.IdleMode;
import com.revrobotics.CANSparkLowLevel.MotorType;
import team1502.configuration.RobotConfiguration;
import team1502.configuration.Builders.Controllers.PowerDistributionModule;
import team1502.configuration.CAN.Manufacturer;

public final class RobotConfigurations {

    public static RobotConfiguration getConfiguration(String radio) {
        switch(radio) {
            case "1502": return RobotConfiguration.Create("robot_2024_1502", r->createRobot2024(r));
            default: return RobotConfiguration.Create("robot_2024_1502", r->createRobot2024(r));
        }
    }

    private static RobotConfiguration createRobot2024(RobotConfiguration configuration) {
        buildCompetitionBot(standardSwerveChassis(configuration));
        pdpAssignments(configuration);
        addEvalHelpers(configuration);
        return configuration;
    }

    private static RobotConfiguration standardChassis(RobotConfiguration inventory) {
        return inventory.Parts(define -> define
            .RoboRIO(r -> r.Version("2.0").PowerProfile(45.0)) // +RSL 0.6 ??
            .PowerDistributionHub(p -> p.CanNumber(1))
            .Radio(r -> r.PowerProfile(0.79))
            .RadioPowerModule(r -> r.PowerProfile(12.0)) // radio power module
            .RadioBarrelJack(r -> r.PowerProfile(12.0)) // radio barrel jack
            .RadioSignalLight(r -> r.PowerProfile(0.6)) // radio signal light
            .EthernetSwitch(r -> r.PowerProfile(12.0))
            .TimeOfFlight(r -> r.PowerProfile(0.02))
            .Compressor(r -> r.PowerProfile(120.0))
            .LimeLight(r -> r.PowerProfile(60.0))
            .RaspberryPi(r -> r.PowerProfile(60.0)) //? + camera
            .LEDs(r -> r.PowerProfile(25.0)) // 5V addressable LEDs - 5A max
        );
    }

    private static RobotConfiguration standardSwerveChassis(RobotConfiguration inventory) {
        standardChassis(inventory);
        // Inventory Definitions
        return inventory.Parts(define -> define
            .Pigeon2(p -> p
                .PowerProfile(0.4)
            )
            .Motor("NEO", m -> m
                .MotorType(MotorType.kBrushless)
                .FreeSpeedRPM(5_820.0) // from MK4i docs, see data sheet for empirical values
                .Note("NAME", "NEO")
                .Note("VERSION", "V1.0/V1.1")
                .Note("gearing", "8mm bore pinion gears")
                .Note("DATA SHEET", "https://www.revrobotics.com/content/docs/REV-21-1650-DS.pdf")
                .Value("empiricalFreeSpeed", 5_676.0) // how to choose best vaule?
                .PowerProfile(380.0)
            )
            .SwerveModule(sm -> sm
                .CANCoder(cc -> cc
                    .Direction(false)
                    .PowerProfile(0.060)
                )
                .TurningMotor(Manufacturer.REVRobotics, mc -> mc
                    .Motor("NEO")
                    .IdleMode(IdleMode.kCoast)
                    .GearBox(g-> g
                        .Gear("Stage1", 14, 50)
                        .Gear("Stage2", 10, 60)
                        .Note("MK4i Standard", "150/7:1")
                    )
                    .PID(3.5, 0.0, 0.0)
                    .Reversed(true) // all turn motors are reversed
                )
                .DrivingMotor(Manufacturer.REVRobotics, mc -> mc
                    .Motor("NEO")
                    .IdleMode(IdleMode.kBrake)
                    .GearBox(g-> g
                        .Gear("Stage1", 14, 50)
                        .Gear("Stage2", 27, 17)
                        .Gear("Stage3", 15, 45)
                        .Note("Mk4i Option", "L2 = 6.75:1")
                    )
                    .PID(.08, 0.0, 0.0, 1.0)
                        // no drive motors were reversed, but now left (?)
                )
                .Value("closedLoopRampRate", .25)
                .Value("smartCurrentLimit", 40)
            )
            .SwerveDrive(sd -> sd
                .Chassis(c -> c
                    .Square(23.25)
                    .WheelDiameter(4.0)
                )
                .Value("MAX_SPEED_METERS_PER_SECOND", 4.6) // hard-code or calc?
            )
        );
    }

    private static RobotConfiguration buildStandardElectronics(RobotConfiguration parts) {
        // Top-Level Parts
        return parts.Build(hw -> hw
            .RoboRIO(r -> r // NI Robot Controller 0
                .PWM(p -> p
                // .Spark(0, "LED-L", "LED-5V") // left_blinkin 5V addressable LEDs
                // .Spark(1, "LED-R", "LED-5V") // right_blinkin
                )
                .DIO(d -> d)
                .CanNumber(0)
            )
            .PCM(ph -> ph
                .Compressor()
                //.Solenoid(15, 0, "SOL")
                .CanNumber(1)
            )


        /*
         * NI Robot Controller 0
         * REV Robotics Pneumatics Controller        1
         * REV Robotics Power Distribution Module    1
         * CTR Electronics DeviceType #21           14 Pigeon2
         * CTR Electronics DeviceType #21            9  ??
         * REV Robotics Motor Controller             0  ??
         * CTR Electronics Accelerometer            10 CANcoder          
         */
        );
    }
    private static RobotConfiguration buildCompetitionBot(RobotConfiguration parts) {
        // Top-Level Parts
        return parts.Build(hw -> hw
            .Note("Intake is the FRONT for this configuration as all the motors drive that direction unless reversed")
            .Pigeon2("Gyro", g->g.CanNumber(14))
            .SwerveDrive(sd -> sd
                .SwerveModule("#1-16", sm -> sm // just leaving these as numbers, since "Front" is arbitrary and undetermined at the moment
                    .CanNumberDown(16) // 16 16 15 -- also PDP channel
                    .Encoder(e -> e.MagneticOffset(151.96))
                )
                .SwerveModule("#2-10", sm -> sm
                    .CanNumber(10) // 10 10 11
                    .Encoder(e -> e.MagneticOffset(121.81))
                )
                .SwerveModule("#3-4", sm -> sm
                    .CanNumberDown(4) // 4 4 3
                    .Encoder(e -> e.MagneticOffset(4.83))
                )
                .SwerveModule("#4-8", sm -> sm
                    .CanNumber(8) // 8 8 9
                    .Encoder(e -> e.MagneticOffset(127.26))
                )
                // miscellaneous
                .Value("goStraightGain", 0.02)
            )
        );
    }
    
    private static RobotConfiguration pdpAssignments(RobotConfiguration robot) {
        return robot.PowerDistributionModule(pdh -> pdh
        // LEFT SIDE                                RIGHT SIDE
        //======================================    ======================================
        .Ch(10, 30, "DC-DC")                        .Ch(9, 10, "PCM")
        .Ch(11)                                     .Ch(7)
        .Ch(12, 30, "RM-PDP")                       .Ch(8)
        .Ch(13, 30, "LM-PDP")                       .Ch(6)
        .Ch(14, 40, "X-ARM")                        .Ch(5)
        .Ch(15, 40, "L-ARM")                        .Ch(4, 40, "R-ARM")
        .Ch(16, 40, "RLD")                          .Ch(3, 40, "FRD")
        .Ch(17, 40, "RLT")                          .Ch(2, 40, "FRT")
        .Ch(18, 40, "RRD")                          .Ch(1, 40, "FLD")
        .Ch(19, 40, "RRT")                          .Ch(0, 40, "FLT")

        .Ch(20, 10, "RIO") // + LEDs?
        .Ch(21, 10, "RPM") // these two are
        .Ch(22, 10, "RBJ") // redundant power sources
        .Ch(22) // switchable
    );

    }
    private static RobotConfiguration addEvalHelpers(RobotConfiguration robot) {
        // Configuration Values
        return robot.Values(k -> k
                .Eval("Pigeon2", e->e.GyroSensor())
                .Eval("SwerveModule.TurningMotor.Motor.MotorType", e -> e
                    .SwerveModule(e.partName(), m -> m.TurningMotor().Motor().MotorType()))
                .Eval("SwerveModule.TurningMotor.Reversed", e -> e
                    .SwerveModule(e.partName(), m -> m.TurningMotor().Reversed()))
                .Eval("SwerveModule.Encoder.Direction", e -> e
                    .SwerveModule(e.partName(), m -> m.Encoder().Direction()))
                .Eval("SwerveModule.Encoder.MagneticOffset", e -> e
                    .SwerveModule(e.partName(), m -> m.Encoder().MagneticOffset()))
                .Eval("SwerveModule.Encoder.MagneticOffset", e -> e
                    .SwerveModule(e.partName(), m -> m.Encoder().MagneticOffset()))
                .Eval("SwerveDrive.Modules", e -> e
                    .SwerveDrive(m -> m.getModules()))
                .Eval("SwerveDrive.Kinematics", e -> e
                    .SwerveDrive(m -> m.getKinematics()))
            );
    }
}
