package team1502.configuration.Builders;

import java.util.List;
import java.util.function.Function;

import edu.wpi.first.math.geometry.Translation2d;
import edu.wpi.first.math.kinematics.SwerveDriveKinematics;

import team1502.configuration.Parts.Part;

public class SwerveDrive extends Builder {
    public static final String NAME = "SwerveDrive";
    private static final String MODULE = "SwerveModule";

    public SwerveDrive() { super(NAME); }
    public SwerveDrive(Function<SwerveDrive, Builder> fn) { super(NAME, NAME, fn); }

    @Override
    public Builder createBuilder() {
        return new SwerveDrive((Function<SwerveDrive, Builder>)buildFunction);
    }

    public SwerveDrive SwerveModule(String name, Function<SwerveModule, Builder> fn) {
        var module = InstallPiece(name, MODULE, fn);
        module.Value("location", getKinematic(getPieces().size()));
        module.Value("wheelDiameter", Chassis().WheelDiameter());
        return this;
    }

    public Chassis Chassis() { return new Chassis(this); }
    public SwerveDrive Chassis(Function<Chassis, Builder> fn) {
         return (SwerveDrive)Install(new Chassis(fn));
    }

    public Translation2d getKinematic(int moduleNumber) {
        return Chassis().getModuleLocation(moduleNumber);
    }
    public SwerveDriveKinematics getKinematics() {
        Translation2d[] kinematics = getPieces().stream()
            .map(module->(Translation2d)module.getValue("location"))
            .toArray(Translation2d[]::new);
        return new SwerveDriveKinematics(kinematics);
    }

    public List<SwerveModule> getModules() {
        List<Part> parts = getPieces();
        return parts.stream().map(p->new SwerveModule(p)).toList();
    }
    public SwerveModule SwerveModule(String name) {
        return new SwerveModule(getPieces().stream().filter(p->p.name == name).findFirst().get());
    }

}
/*
  
  public static final double WHEEL_BASE_WIDTH = Units.inchesToMeters(23.25);
  public static final double WHEEL_BASE_LENGTH = Units.inchesToMeters(23.25);


  public static final Translation2d FRONT_LEFT_MODULE = new Translation2d(WHEEL_BASE_LENGTH/2, WHEEL_BASE_WIDTH/2);
  public static final Translation2d FRONT_RIGHT_MODULE = new Translation2d(WHEEL_BASE_LENGTH/2, -WHEEL_BASE_WIDTH/2);
  public static final Translation2d BACK_LEFT_MODULE = new Translation2d(-WHEEL_BASE_LENGTH/2, WHEEL_BASE_WIDTH/2);
  public static final Translation2d BACK_RIGHT_MODULE = new Translation2d(-WHEEL_BASE_LENGTH/2, -WHEEL_BASE_WIDTH/2);

    public static final double GO_STRAIGHT_GAIN = 0.02;

 */
/*
  
    public static final double WHEEL_DIAMETER_METERS = Units.inchesToMeters(4);
    public static final double DRIVE_GEAR_RATIO = 1 / ((14.0 / 50.0) * (27.0 / 17.0) * (15.0 / 45.0));
    public static final double STEER_GEAR_RATIO = 1 / ((14.0 / 50.0) * (10.0 / 60.0));
    public static final double DRIVE_METERS_PER_ENCODER_REV = (WHEEL_DIAMETER_METERS * Math.PI) / DRIVE_GEAR_RATIO;
    public static final double DRIVE_ENCODER_MPS_PER_REV = DRIVE_METERS_PER_ENCODER_REV / 60; 

 */
