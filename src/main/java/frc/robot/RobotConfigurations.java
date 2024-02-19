package frc.robot;

import com.revrobotics.CANSparkBase.IdleMode;
import com.revrobotics.CANSparkLowLevel.MotorType;

import team1502.configuration.CAN.Manufacturer;
import team1502.configuration.factory.RobotConfiguration;

public final class RobotConfigurations {
    public static RobotConfiguration getConfiguration(String radio) {
        switch(radio) {
            case "1502": return RobotConfiguration.Create("robot_2024_1502", r->competitionRobot2024(r));
            case "1502_practice": return RobotConfiguration.Create("robot_2024_1502_practice", r->practiceRobot2024(r));
            default: return RobotConfiguration.Create("robot_2024_1502", r->competitionRobot2024(r));
        }
    }

    private static RobotConfiguration competitionRobot2024(RobotConfiguration configuration) {
        buildCompetitionBot(buildStandardElectronics(standardSwerveChassis(configuration)));
        pdpAssignments(configuration);
        return configuration;
    }
    private static RobotConfiguration practiceRobot2024(RobotConfiguration configuration) {
        buildPracticeBot(buildStandardElectronics(standardSwerveChassis(configuration)));
        pdpPracticeAssignments(configuration);
        return configuration;
    }
    
    private static RobotConfiguration buildCompetitionBot(RobotConfiguration parts) {
        parts.PowerDistributionModule(pdh -> pdh
            .Ch(20, parts.RoboRIO())
            .Ch(21, parts.RadioPowerModule()));

        parts.Parts(inventory->inventory
            .Part("REV through-bore Encoder", e->e
            .Note("Reference", "https://www.revrobotics.com/rev-11-1271/")
            )
        );
        return parts.Build(hw -> hw
        .MiniPowerModule("MPM1", mpm->mpm
            .Ch(0, 10)
            .Ch(1, 10)
            .Ch(2, 10, parts.EthernetSwitch().Part("POE"))
            .Ch(3, 10, parts.EthernetSwitch()) // CONFIRM
            .Ch(4, 10)
            .Ch(5, 10)
            .PDH(14, "MPM1"))
        .MiniPowerModule("MPM2", mpm->mpm
            .Ch(0, 10)
            .Ch(1, 10)
            .Ch(2, 10)
            .Ch(3, 10)
            .Ch(4, 10)
            .Ch(5, 10)
            .PDH(19, "MPM2"))

        // GYRO
        .Pigeon2(g->g
            .CanNumber(14)
            .MPM("MPM1", 0))

        // PNEUMATICS
        .Compressor(p->p)
        .PCM(ph -> ph
            .DoubleSolenoid(15, 0, "SOL")
            .PDH(7)
            .Powers(hw.Compressor())
            .CanNumber(1))

        // ARM
        .MotorController("armLead", c->c
            .Motor("NEO 550", m->m)
            .IdleMode(IdleMode.kBrake)
            .SmartCurrentLimit(40) // encoder, PID
            .GearBox(g-> g
                .Gear("5:1", 1, 5)
                .Gear("5:1", 1, 5)
                .Gear("Chain", 1, 4))
            .PDH(1)
            .CanNumber(1))
        .MotorController("armFollow", c->c // DutyCycleEncoder(0) .DIO(0)
            .Motor("NEO 550", m->m)
            .Reversed()
            .IdleMode(IdleMode.kBrake)
            .SmartCurrentLimit(40)
            .GearBox(g-> g
                .Gear("5:1", 1, 5)
                .Gear("5:1", 1, 5)
                .Gear("Chain", 1, 4))
            .PDH(6)
            .CanNumber(6))
        
        // SWERVE DRIVE
        .Note("YELLOW modules are the front, BLUE modules are the back")
        .SwerveDrive(sd -> sd
            .SwerveModule("#1", sm -> sm // just leaving these as numbers, since "Front" may change
                .Wrap(sw->sw.FriendlyName("Front Left").Abbreviation("FL"))
                .CanNumber(16) // 16 16 17 -- also PDP channel
                .Encoder(e -> e
                    .MagneticOffset(103.32)
                    .Abbreviation(sm.Abbreviation()+"E")
                    .MPM("MPM2", 0))
            )
            .SwerveModule("#2", sm -> sm
                .Wrap(sw->sw.FriendlyName("Front Right").Abbreviation("FR"))
                .CanNumber(10) // 10 10 11
                .Encoder(e -> e
                    .MagneticOffset(292.68)
                    .Abbreviation(sm.Abbreviation()+"E")
                    .MPM("MPM1", 1))
            )
            .SwerveModule("#3", sm -> sm
                .Wrap(sw->sw.FriendlyName("Back Left").Abbreviation("BL"))
                .CanNumber(4) // 4 4 5
                .Encoder(e -> e
                    .MagneticOffset(276.48)
                    .Abbreviation(sm.Abbreviation()+"E")
                    .MPM("MPM2", 1))
            )
            .SwerveModule("#4", sm -> sm
                .Wrap(sw->sw.FriendlyName("Back Right").Abbreviation("BR"))
                .CanNumber(8) // 8 8 9
                .Encoder(e -> e
                    .MagneticOffset(12.24)
                    .Abbreviation(sm.Abbreviation()+"E")
                    .MPM("MPM1", 5))
            )
            // miscellaneous
            .GoStraightGain(0.1)
        ));
    }
                
     private static RobotConfiguration buildPracticeBot(RobotConfiguration parts) {
        parts.DisableSubsystem("frc.robot.subsystems.Arm.ArmSubsystem");

        parts.PowerDistributionModule(pdh -> pdh
            .Ch(17, parts.EthernetSwitch().Part("POE"))
            .Ch(18, parts.EthernetSwitch())
        );
        
        return parts.Build(hw->hw
        .PCM(pcm->pcm.PowerChannel(21))
        .RadioPowerModule(rpm->rpm.PowerChannel(22))
        .Pigeon2(gyro->gyro.PowerChannel(23).CanNumber(14))
        .DC(dc -> dc.PDH(1, "??"))
        // limelight 10.15.2.23:5800
        // PIs?
        .MiniPowerModule(mpm->mpm
            .Ch(0, 10)
            .Ch(1, 10)
            .Ch(2, 10)
            .Ch(3, 10)
            .Ch(4, 10)
            .Ch(5, 10)
            .PDH(0, "MPM0"))
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
            .GoStraightGain(0.1)
        )
        );
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
                    .PeakPower(0.060)
                )
                .TurningMotor(Manufacturer.REVRobotics, mc -> mc
                    .Motor("NEO")
                    .IdleMode(IdleMode.kCoast)
                    .Reversed() // all turn motors are reversed
                    .GearBox(g-> g
                        .Gear("Stage1", 14, 50)
                        .Gear("Stage2", 10, 60)
                        .Note("MK4i Standard", "150/7:1")
                    )
                    .PID(3.4, 0.0, 0.0)
                )
                .DrivingMotor(Manufacturer.REVRobotics, mc -> mc
                    .Motor("NEO")
                    .IdleMode(IdleMode.kBrake)
                    .Reversed() // all drive motors are reversed
                    .GearBox(g-> g
                        .Gear("Stage1", 14, 50)
                        .Gear("Stage2", 27, 17)
                        .Gear("Stage3", 15, 45)
                        .Note("Mk4i Option", "L2 = 6.75:1")
                    )
                    .PID(.0005, 0.0, 0.0, 1.0)
                    .ClosedLoopRampRate(.5)
                    .SmartCurrentLimit(30)
                )
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
                .Part("POE", p->p
                    .Note("Description", "Power-Over-Ethernet")
                    .Note("Device", "OONO CZH-LABS.com"))
            )
        );
    }
  
    private static RobotConfiguration pdpAssignments(RobotConfiguration robot) {      
        return robot.PowerDistributionModule(pdh -> pdh
        // LEFT SIDE                                RIGHT SIDE
        //======================================    ======================================
        //  CH  FUSE  EQUIPMENT                         CH FUSE EQUIPMENT
        //======================================    ======================================
        .Ch(10, 40)                                 .Ch(9, 40)
        .Ch(11, 40)                                 .Ch(8, 40)
        .Ch(12)                                     .Ch(7, 10)
        .Ch(13)                                     .Ch(6, 40)
        .Ch(14, 20)                                 .Ch(5, 40)
        .Ch(15, 40)                                 .Ch(4, 40)
        .Ch(16, 40)                                 .Ch(3)
        .Ch(17, 40)                                 .Ch(2)
        .Ch(18)                                     .Ch(1, 40)
        .Ch(19, 20)                                 .Ch(0)

        .Ch(20, 10)
        .Ch(21, 10)
        .Ch(22, 10)
        .Ch(23, 10) // switchable
        );
    }

    private static RobotConfiguration pdpPracticeAssignments(RobotConfiguration robot) {      
        return robot.PowerDistributionModule(pdh -> pdh
        // LEFT SIDE                                RIGHT SIDE
        //======================================    ======================================
        //  CH  FUSE  EQUIPMENT                         CH FUSE EQUIPMENT
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
        .Ch(21, 10)
        .Ch(22, 10)
        .Ch(23, 10) // switchable
        );
    }
    
}
