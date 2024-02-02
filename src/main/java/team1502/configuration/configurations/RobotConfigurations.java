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
            .RoboRIO(r -> r.Version("2.0").PeakPower(45.0)) // +RSL 0.6 ??
            .PowerDistributionHub(p -> p.CanNumber(1))
            .Radio(r -> r.PeakPower(0.79))
            .RadioPowerModule(r -> r.PeakPower(12.0)) // radio power module
            .RadioBarrelJack(r -> r.PeakPower(12.0)) // radio barrel jack
            .RadioSignalLight(r -> r.PeakPower(0.6)) // radio signal light
            .EthernetSwitch(r -> r.PeakPower(12.0))
            .TimeOfFlight(r -> r.PeakPower(0.02))
            .Compressor(r -> r.PeakPower(120.0))
            .LimeLight(r -> r.PeakPower(60.0))
            .RaspberryPi(r -> r.PeakPower(60.0)) //? + camera
            .LEDs(r -> r.PeakPower(25.0)) // 5V addressable LEDs - 5A max
        );
    }

    private static RobotConfiguration standardSwerveChassis(RobotConfiguration inventory) {
        standardChassis(inventory);
        // Inventory Definitions
        return inventory.Parts(define -> define
            .Pigeon2(p -> p
                .PeakPower(0.4)
            )
            .Motor("NEO", m -> m
                .MotorType(MotorType.kBrushless)
                .FreeSpeedRPM(5_820.0) // from MK4i docs, see data sheet for empirical values
                .Note("NAME", "NEO")
                .Note("VERSION", "V1.0/V1.1")
                .Note("gearing", "8mm bore pinion gears")
                .Note("DATA SHEET", "https://www.revrobotics.com/content/docs/REV-21-1650-DS.pdf")
                .Value("empiricalFreeSpeed", 5_676.0) // how to choose best vaule?
                .PeakPower(380.0)
            )
            .SwerveModule(sm -> sm
                .CANCoder(cc -> cc
                    .Direction(false)
                    .PeakPower(0.060)
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
                .SwerveModule("#1", sm -> sm // just leaving these as numbers, since "Front" is arbitrary and undetermined at the moment
                    .CanNumberDown(16) // 16 16 15 -- also PDP channel
                    .Encoder(e -> e.MagneticOffset(151.96)
                    .FriendlyName("Front Left"))
                    )
                    .SwerveModule("#2", sm -> sm
                    .CanNumber(10) // 10 10 11
                    .Encoder(e -> e.MagneticOffset(121.81)
                    .FriendlyName("Front Right"))
                    )
                    .SwerveModule("#3", sm -> sm
                    .CanNumberDown(4) // 4 4 3
                    .Encoder(e -> e.MagneticOffset(4.83)
                    .FriendlyName("Back Left"))
                    )
                    .SwerveModule("#4", sm -> sm
                    .CanNumber(8) // 8 8 9
                    .Encoder(e -> e.MagneticOffset(127.26)
                    .FriendlyName("Back Right"))
                )
                // miscellaneous
                .Value("goStraightGain", 0.02)
            )
        );
    }
/*
                .DC("DC-DC", dc -> dc
                    .Pi("PhotonVisionone", "10.15.02.11")
                    .Pi("PhotonVisiontwo", "10.15.02.12")
                )
                
 */    
    private static RobotConfiguration pdpAssignments(RobotConfiguration robot) {
        robot.Build(hw->hw
            .MiniPowerModule("RM-PDP", mpm -> mpm
                .Ch(0, 10, robot.Values().SwerveDrive().SwerveModule("#4-8").Encoder())
                .Ch(1, 10, robot.Values().SwerveDrive().SwerveModule("#2-10").Encoder()))

            .MiniPowerModule("LM-PDP", mpm -> mpm
                .Ch(0, 10, robot.Values().GyroSensor())
                .Ch(3, 10, robot.Values().SwerveDrive().SwerveModule("#1-16").Encoder())
                .Ch(4, 10, robot.Values()
                    .EthernetSwitch(/*eth->eth // also provides POE
                        .Radio()
                            .RadioPowerModule()
                            .RadioBarrelJack()
                    */)
                )
                .Ch(5, 10, robot.Values().SwerveDrive().SwerveModule("#3-4").Encoder()))
        );
                            
        return robot.PowerDistributionModule(pdh -> pdh
        // LEFT SIDE                                RIGHT SIDE
        //======================================    ======================================
        .Ch(10, 30, "DC-DC")                        .Ch(9, 10, robot.Values().PCM())
        .Ch(11)                                     .Ch(7)
        .Ch(12, 30, robot.Values().MPM("LM-PDP"))   .Ch(8)
        .Ch(13, 30, robot.Values().MPM("RM-PDP"))   .Ch(6)
        .Ch(14, 40)                                 .Ch(5)
        .Ch(15, 40)                                 .Ch(4, 40)
        .Ch(16, 40)                                 .Ch(3, 40)
        .Ch(17, 40)                                 .Ch(2, 40)
        .Ch(18, 40)                                 .Ch(1, 40)
        .Ch(19, 40)                                 .Ch(0, 40)

        .Ch(20, 10, robot.Values().RoboRIO()) // + LEDs?
        .Ch(21, 10, robot.Values().RadioPowerModule()) // these two are
        .Ch(22, 10, robot.Values().RadioBarrelJack())  // redundant power sources
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
