package team1502.configuration.configurations;

import com.revrobotics.CANSparkBase.IdleMode;
import com.revrobotics.CANSparkLowLevel.MotorType;
import team1502.configuration.RobotConfiguration;
import team1502.configuration.CAN.Manufacturer;

public final class RobotConfigurations {

    public static RobotConfiguration getConfiguration(String radio) {
        switch(radio) {
            case "1502": return createRobot2024();
            default: return createRobot2024();
        }
    }
    
    public static RobotConfiguration createRobot2024() { return RobotConfiguration
        .Create("robot_2024_Test", r -> r
            // Inventory Definitions
            .Parts(define -> define
                .GyroSensor("Pigeon2", Manufacturer.CTRElectronics, p -> p
                    .Reversed(true) // no longer used? what did it mean?
                )
                .Motor("NEO", m -> m
                    .MotorType(MotorType.kBrushless)
                    .FreeSpeedRPM(5_820.0) // from MK4i docs, see data sheet for empirical values
                    .Note("NAME", "NEO")
                    .Note("VERSION", "V1.0/V1.1")
                    .Note("gearing", "8mm bore pinion gears")
                    .Note("DATA SHEET", "https://www.revrobotics.com/content/docs/REV-21-1650-DS.pdf")
                    .Value("empiricalFreeSpeed", 5_676.0) // how to choose best vaule?
                )
                .SwerveModule(sm -> sm
                    .Encoder(Manufacturer.REVRobotics, cc -> cc
                        .Direction(false)
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
            )
            // Top-Level Parts
            .Build(hw -> hw
                .GyroSensor("Gyro", "Pigeon2", g->g.CanNumber(14))
                .SwerveDrive(sd -> sd
                    .SwerveModule("Module#1", sm -> sm
                        .CanNumberDown(16) // 16 16 15 -- also PDP channel
                        .Encoder(e -> e.MagneticOffset(151.96))
                        .DrivingMotor().Reversed(true) // left side reversed
                    )
                    .SwerveModule("Module#2", sm -> sm
                        .CanNumber(10) // 10 10 11
                        .Encoder(e -> e.MagneticOffset(121.81))
                    )
                    .SwerveModule("Module#3", sm -> sm
                        .CanNumberDown(4) // 4 4 3
                        .Encoder(e -> e.MagneticOffset(4.83))
                        .DrivingMotor().Reversed(true) // left side reversed
                    )
                    .SwerveModule("Module#4", sm -> sm
                        .CanNumber(8) // 8 8 9
                        .Encoder(e -> e.MagneticOffset(127.26))
                    )
                    // miscellaneous
                    .Value("goStraightGain", 0.02)
                )
            )
            // Configuration Values
            .Values(k -> k
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
            )
        );

    }
}
