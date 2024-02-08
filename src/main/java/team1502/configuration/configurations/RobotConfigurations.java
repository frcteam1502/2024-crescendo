package team1502.configuration.configurations;

import com.revrobotics.CANSparkBase.IdleMode;
import com.revrobotics.CANSparkLowLevel.MotorType;

import team1502.configuration.CAN.Manufacturer;
import team1502.configuration.factory.RobotConfiguration;

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
            .RoboRIO(r -> r.Version("2.0").PeakPower(45.0).Abbreviation("RIO")) // +RSL 0.6 ??
            .PowerDistributionHub(p -> p.CanNumber(1).Abbreviation("PDH"))
            .Radio(r -> r.PeakPower(0.79))
            .RadioPowerModule(r -> r.PeakPower(12.0).Abbreviation("RPM")) // radio power module
            .RadioBarrelJack(r -> r.PeakPower(12.0).Abbreviation("RBJ")) // radio barrel jack
            .RadioSignalLight(r -> r.PeakPower(0.6).Abbreviation("RSL")) // radio signal light
            .EthernetSwitch(r -> r.PeakPower(12.0).Abbreviation("ETH"))
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
                .PeakPower(380.0) // free running current 1.4A, stall 100A
            )
            .Motor("NEO 550", m -> m
                .MotorType(MotorType.kBrushless)
                .FreeSpeedRPM(11_000.0) // from REV
                .Note("NAME", "NEO 550")
                .Note("Reference", "https://www.revrobotics.com/rev-21-1651/")
                .PeakPower(279.0) // ~265 @ 40A
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
                    .PID(3.4, 0.0, 0.0)
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
                    .PID(.0005, 0.0, 0.0, 1.0)
                )
                .Value("closedLoopRampRate", .5)
                .Value("smartCurrentLimit", 30)
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
            .RadioSignalLight(r->r)
            .Radio(r->r)
            .RadioPowerModule(r->r.Powers(hw.Radio()))

            .RoboRIO(r -> r // NI Robot Controller 0
                .PWM(p -> p
                // .Spark(0, "LED-L", "LED-5V") // left_blinkin 5V addressable LEDs
                // .Spark(1, "LED-R", "LED-5V") // right_blinkin
                )
                .DIO(d -> d)
                .CanNumber(0)
                .Powers(hw.RadioSignalLight())
            )

            .EthernetSwitch(eth->eth  // also provides POE
                .PDH(18, "??")
                .Part("POE", p->p
                    .Note("Description", "Power-Over-Ethernet")
                    .Note("Device", "OONO CZH-LABS.com")
                    .PDH(17, "??"))
            )

            .Compressor(p->p)
            .PCM(ph -> ph
                .Powers(hw.Compressor())
                //.Solenoid(15, 0, "SOL")
                .CanNumber(1)
            )



        /* from CAN scan:
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
        parts.PowerDistributionModule(pdh -> pdh);
        parts.Build(hw->hw
            .DC(dc -> dc // 60W PIs + camera
            /*                  .Pi("PhotonVisionone", "10.15.02.11")
                                .Pi("PhotonVisiontwo", "10.15.02.12")
            */                 
                .PDH(1, "??")
            )
            
            .MiniPowerModule(mpm->mpm
                .Ch(0, 10)
                .Ch(1, 10)
                .Ch(2, 10)
                .Ch(3, 10)
                .Ch(4, 10)
                .Ch(5, 10)
                .PDH(0, "MPM0")
            )
        );

        buildStandardElectronics(parts);
        // Top-Level Parts
        return parts.Build(hw -> hw
            .Note("Intake is the FRONT for this configuration as all the motors drive that direction unless reversed")
            .Pigeon2(g->g.CanNumber(14))
            .SwerveDrive(sd -> sd
                .SwerveModule("#1", sm -> sm // just leaving these as numbers, since "Front" is arbitrary and undetermined at the moment
                    .Wrap(sw->sw.FriendlyName("Front Left").Abbreviation("FL"))
                    .CanNumberDown(16) // 16 16 15 -- also PDP channel
                    .Encoder(e -> e
                        .MagneticOffset(151.96)
                        .Abbreviation(sm.Abbreviation()+"E")
                        .MPM(3, "FL3"))
                )
                .SwerveModule("#2", sm -> sm
                    .Wrap(sw->sw.FriendlyName("Front Right").Abbreviation("FR"))
                    .CanNumber(10) // 10 10 11
                    .Encoder(e -> e
                        .MagneticOffset(121.81)
                        .Abbreviation(sm.Abbreviation()+"E")
                        .MPM(2, "FR2"))
                )
                .SwerveModule("#3", sm -> sm
                    .Wrap(sw->sw.FriendlyName("Back Left").Abbreviation("BL"))
                    .CanNumberDown(4) // 4 4 3
                    .Encoder(e -> e
                        .MagneticOffset(4.83)
                        .Abbreviation(sm.Abbreviation()+"E")
                        .MPM(0, "BL0"))
                )
                .SwerveModule("#4", sm -> sm
                    .Wrap(sw->sw.FriendlyName("Back Right").Abbreviation("BR"))
                    .CanNumber(8) // 8 8 9
                    .Encoder(e -> e
                        .MagneticOffset(127.26)
                        .Abbreviation(sm.Abbreviation()+"E")
                        .MPM(1, "BR1"))
                )
                // miscellaneous
                .Value("goStraightGain", 0.1)
            )
        );
    }
                
   
    private static RobotConfiguration pdpAssignments(RobotConfiguration robot) {      
        return robot.PowerDistributionModule(pdh -> pdh
        // LEFT SIDE                                RIGHT SIDE
        //======================================    ======================================
        //  CH  FUZE  EQUIPMENT                         CH FUZE EQUIPMENT
        //======================================    ======================================
        .Ch(10, 40)                                 .Ch(9, 40)
        .Ch(11, 40)                                 .Ch(8, 40)
        .Ch(12)                                     .Ch(7)
        .Ch(13)                                     .Ch(6)
        .Ch(14)                                     .Ch(5)
        .Ch(15, 40)                                 .Ch(4, 40)
        .Ch(16, 40)                                 .Ch(3, 40)
        .Ch(17, 10)                                 .Ch(2)
        .Ch(18, 10)                                 .Ch(1)
        .Ch(19)                                     .Ch(0, 30)

        .Ch(20, 10,   robot.RoboRIO())
        .Ch(21, 10,   robot.PCM())
        .Ch(22, 10,   robot.RadioPowerModule())
        .Ch(23, 10,   robot.Pigeon2()) // switchable
        );
    }

    private static RobotConfiguration addEvalHelpers(RobotConfiguration robot) {
        // Configuration Values
        return robot.Values(k -> k
            .Eval("Pigeon2", e->e.Pigeon2().CanNumber())
            .Eval("SwerveModule.getPositionConversionFactor", e -> e
                    .SwerveDrive().SwerveModule("#1").getPositionConversionFactor())
            .Eval("SwerveDrive.calculateMaxSpeed", e -> e
                    .SwerveDrive().calculateMaxSpeed())
            .Eval("SwerveModule.calculateMaxSpeed", e -> e
                    .SwerveDrive().SwerveModule("#1").calculateMaxSpeed())
            .Eval("SwerveModule.TurningMotor.Motor.MotorType", e -> e
                    .SwerveDrive().SwerveModule("#1").TurningMotor().Motor().MotorType())
            .Eval("SwerveModule.TurningMotor.Motor.PowerChannel", e -> e
                    .SwerveDrive().SwerveModule("#1").TurningMotor().PowerChannel())
            .Eval("SwerveModule.TurningMotor.TotalPower", e -> e
                    .SwerveDrive().SwerveModule("#1").TurningMotor().TotalPeakPower())
            .Eval("SwerveModule.MagneticOffset", e -> e
                    .SwerveDrive().SwerveModule("#1").Encoder().MagneticOffset())
            // .Eval("SwerveModule.TurningMotor.Motor.MotorType", e -> e
            //     .SwerveModule(e.partName(), m -> m.TurningMotor().Motor().MotorType()))
            // .Eval("SwerveModule.TurningMotor.Reversed", e -> e
            //     .SwerveModule(e.partName(), m -> m.TurningMotor().Reversed()))
            // .Eval("SwerveModule.Encoder.Direction", e -> e
            //     .SwerveModule(e.partName(), m -> m.Encoder().Direction()))
            // .Eval("SwerveModule.Encoder.MagneticOffset", e -> e
            //     .SwerveModule(e.partName(), m -> m.Encoder().MagneticOffset()))
            // .Eval("SwerveModule.Encoder.MagneticOffset", e -> e
            //     .SwerveModule(e.partName(), m -> m.Encoder().MagneticOffset()))
            // .Eval("SwerveDrive.Modules", e -> e
            //     .SwerveDrive(m -> m.getModules()))
            // .Eval("SwerveDrive.Kinematics", e -> e
            //     .SwerveDrive(m -> m.getKinematics()))
        );
    }
}
