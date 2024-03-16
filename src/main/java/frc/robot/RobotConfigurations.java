package frc.robot;

import com.revrobotics.CANSparkBase.IdleMode;
import com.revrobotics.CANSparkLowLevel.MotorType;

import frc.robot.subsystems.Arm.ArmSubsystem;
import frc.robot.subsystems.ShooterIntake.ShooterIntake;
import team1502.configuration.CAN.Manufacturer;
import team1502.configuration.factory.RobotConfiguration;

public final class RobotConfigurations {
    public static RobotConfiguration getConfiguration(String radio) {
        switch(radio) {
            case "1502": return RobotConfiguration.Create("robot_2024_1502", r->competitionRobot2024(r));
            //TODO: add this config
            case "1502_backup": return RobotConfiguration.Create("robot_2024_1502_backup", r->competitionRobot2024(r)); 
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
        parts.DisableSubsystem("Logger");
        parts.Value("brownoutVoltage", 0.3);

        // PDH
        parts.PowerDistributionModule(pdh -> pdh
            .Ch(20, parts.RoboRIO())
            .Ch(21, parts.RadioPowerModule()));

        // ARM and SHOOTER parts
        parts.Parts(inventory->inventory
            .Part("REV through-bore Encoder", e->e
                .Note("Reference", "https://www.revrobotics.com/rev-11-1271/")
            )

            .MotorController("Arm Motor", Manufacturer.REVRobotics, c->c
                .Motor("NEO 550", m->m)
                .IdleMode(IdleMode.kBrake)
                .SmartCurrentLimit(40)
                .GearBox(g-> g
                    .Gear("Cartridge #1 5:1", 1, 5)
                    .Gear("Cartridge #2 5:1", 1, 5)
                    .Gear("Chain 4:1", 1, 4))
            )

            .MotorController("Shooter Motor", Manufacturer.REVRobotics, c->c
                .Motor("NEO", m->m)
                .IdleMode(IdleMode.kCoast)
                .SmartCurrentLimit(40)
            )

            .MotorController("Intake Motor", Manufacturer.REVRobotics, c->c
                .Motor("NEO", m->m)
                .IdleMode(IdleMode.kBrake)
                .SmartCurrentLimit(40)
                .GearBox(g-> g
                    .Gear("Cartridge #1 3:1", 1, 3))
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
        .PCM(ph -> ph
            .Compressor()
            .Solenoid(0, ArmSubsystem.BRAKE_SOLENOID)
            .PDH(7)
            .CanNumber(7)
        )

        // ARM
        .Subsystem(ArmSubsystem.class, s -> s
            .MotorController("Leader", "Arm Motor", c->c
                .Follower("Arm Motor", f->f
                    .Reversed()
                    .PDH(6)
                    .CanNumber(6)
                    .FriendlyName("Arm Follower").Abbreviation("Arm-F")
                )
                .PID(p->p
                    .Gain(0.4, 0, 0)
                    .OutputRange(-0.3, 0.3/4) // MAX_ROTATION_SPEED
                )
                .PDH(1)
                .CanNumber(1)
                .Abbreviation("Arm-L")

                // .Value("MAX_ROTATE", 5)
                // .Value("MIN_ROTATE", -95)
                // .Value("MAX_ROTATE_FEEDFORWARD", .06)
                // .Value("ROTATE_CHANGE", .3)    
            )
            .UsePart(ArmSubsystem.BRAKE_SOLENOID)
            .Encoder(e-> e  //DutyCycleEncoder
                .DigitalInput(0)
                .Value("ABS_OFFSET", -6.5)
            )
            
        )

        .Subsystem(ShooterIntake.class, si -> si
            .Subsystem(ShooterIntake.SHOOTER, s -> s
                .MotorController(ShooterIntake.LEADER, "Shooter Motor", c->c
                    .PID(.00005, 0.0, 0.0, 0.000185)
                    .PDH(2).CanNumber(2).Abbreviation("Sh-Ldr")
                    //.Value("SHOOTER_DEFAULT_RPM", 5000)
                )
                .MotorController(ShooterIntake.FOLLOWER, "Shooter Motor", f->f
                    .PID(.00005, 0.0, 0.0, 0.000185)
                    .PDH(3).CanNumber(3).FriendlyName("Shooter Follower").Abbreviation("Sh-Flw")
                )
            )
            .Subsystem(ShooterIntake.INTAKE, i -> i
                .MotorController("Motor", "Intake Motor", c->c
                    .PID(.00005, 0.0, 0.0, 0.000275)
                    .PDH(18).CanNumber(18).Abbreviation("Intk")
                    
                    // .Value("INTAKE_DEFAULT_PICK_UP_RPM", 2500)
                    // .Value("INTAKE_DEFAULT_EJECT_RPM", -2500)
                )
                .DigitalInput(ShooterIntake.PHOTO_SENSOR_NO, 1, io->io.FriendlyName("Note Present 1"))  
                .DigitalInput(ShooterIntake.PHOTO_SENSOR_NC, 2, io->io.FriendlyName("Note Present 2"))
            )
        )

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
                .Wrap(sw->sw.FriendlyName("Rear Left").Abbreviation("RL"))
                .CanNumber(4) // 4 4 5
                .Encoder(e -> e
                    .MagneticOffset(276.48)
                    .Abbreviation(sm.Abbreviation()+"E")
                    .MPM("MPM2", 1))
            )
            .SwerveModule("#4", sm -> sm
                .Wrap(sw->sw.FriendlyName("Rear Right").Abbreviation("RR"))
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
        parts.DisableSubsystem(ArmSubsystem.class.getName());
        parts.DisableSubsystem(ShooterIntake.class.getName());
        parts.DisableSubsystem("limelight");

        parts.PowerDistributionModule(pdh -> pdh
            .Ch(17, parts.EthernetSwitch().Part("POE"))
            .Ch(18, parts.EthernetSwitch())
        );
        
        return parts.Build(hw->hw
        //.DC(dc -> dc.PDH(1, "??"))
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
        // GYRO
        .Pigeon2(g->g
            .CanNumber(14)
            .MPM(0))
        .SwerveDrive(sd -> sd
            .SwerveModule("#1", sm -> sm // just leaving these as numbers, since "Front" is arbitrary and undetermined at the moment
                .Wrap(sw->sw.FriendlyName("Front Left").Abbreviation("FL"))
                .CanNumber(16) // 16 16 17 -- also PDP channel
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
                .Wrap(sw->sw.FriendlyName("Rear Left").Abbreviation("RL"))
                .CanNumber(4) // 4 4 5
                .Encoder(e -> e
                    .MagneticOffset(4.83)
                    .Abbreviation(sm.Abbreviation()+"E")
                    .MPM(0, "RL0"))
            )
            .SwerveModule("#4", sm -> sm
                .Wrap(sw->sw.FriendlyName("Rear Right").Abbreviation("RR"))
                .CanNumber(8) // 8 8 9
                .Encoder(e -> e
                    .MagneticOffset(127.26)
                    .Abbreviation(sm.Abbreviation()+"E")
                    .MPM(1, "RR1"))
            )
            // miscellaneous
            .GoStraightGain(0.1)
        )
        );
    }
                

    private static RobotConfiguration standardChassis(RobotConfiguration inventory) {
        return inventory.Parts(define -> define
            .RoboRIO(r -> r.Version("2.0").PeakPower(45.0).Abbreviation("RIO"))
            .PowerDistributionHub(p -> p.CanNumber(1).Abbreviation("PDH"))
            .Radio(r -> r.PeakPower(0.79))
            .RadioPowerModule(r -> r.PeakPower(12.0).Abbreviation("RPM"))
            .RadioBarrelJack(r -> r.PeakPower(12.0).Abbreviation("RBJ"))
            .RadioSignalLight(r -> r.PeakPower(0.6).Abbreviation("RSL"))
            .EthernetSwitch(r -> r.PeakPower(12.0).Abbreviation("ETH"))
            .TimeOfFlight(r -> r.PeakPower(0.02))
            .Compressor(r -> r.PeakPower(120.0))
            .LimeLight(r -> r.PeakPower(60.0))
            .RaspberryPi(r -> r.PeakPower(60.0)) //? + camera
            .LEDs(r -> r.PeakPower(25.0)) // 5V addressable LEDs - 5A max
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
                // 40A
                .PeakPower(380.0) // free running current 1.4A, stall 100A
            )
            .Motor("NEO 550", m -> m
                .MotorType(MotorType.kBrushless)
                .FreeSpeedRPM(11_000.0) // from REV
                .Note("NAME", "NEO 550")
                .Note("Reference", "https://www.revrobotics.com/rev-21-1651/")
                // current limit 20A
                .PeakPower(279.0) // ~265 @ 40A
            )
        );
    }

    private static RobotConfiguration standardSwerveChassis(RobotConfiguration inventory) {
        standardChassis(inventory);
        // Inventory Definitions
        return inventory.Parts(define -> define
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
                    .PID(p->p
                        .Gain(3.4, 0.0, 0.0)
                        .EnableContinuousInput(-Math.PI, Math.PI)
                    )
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
            .PowerDistributionHub(r->r)
            .RadioSignalLight(r->r)
            .Radio(r->r)
            .RadioPowerModule(r->r.Powers(hw.Radio()))

            .RoboRIO(r -> r // NI Robot Controller 0
                .PWM(p -> p
                // .Spark(0, "LED-L", "LED-5V") // left_blinkin 5V addressable LEDs
                // .Spark(1, "LED-R", "LED-5V") // right_blinkin
                )
                .DIO(d -> d)
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