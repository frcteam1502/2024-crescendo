package team1502.configuration.builders.motors;

import java.util.List;
import java.util.function.Function;

import edu.wpi.first.math.geometry.Translation2d;
import edu.wpi.first.math.kinematics.SwerveDriveKinematics;
import team1502.configuration.builders.Builder;
import team1502.configuration.builders.Chassis;
import team1502.configuration.builders.IBuild;
import team1502.configuration.builders.Part;

public class SwerveDrive extends Builder {
    public static final String NAME = "SwerveDrive";
    public static Function<IBuild, SwerveDrive> Define = build->new SwerveDrive(build);
    public static SwerveDrive Wrap(Builder builder) { return new SwerveDrive(builder.getIBuild(), builder.getPart()); }
    public static SwerveDrive WrapPart(Builder builder) { return WrapPart(builder, NAME); }
    public static SwerveDrive WrapPart(Builder builder, String partName) { return Wrap(builder.getPart(partName)); }

    public SwerveDrive(IBuild build) { super(build, NAME); }
    public SwerveDrive(IBuild build, Part part) { super(build, part); }

    public SwerveDrive SwerveModule(String name, Function<SwerveModule, Builder> fn) {
        var module = addPiece(SwerveModule.Define, name, SwerveModule.NAME, fn);
        module.Value(SwerveModule.location, getKinematic(getPieces().size()));
        module.Value(SwerveModule.wheelDiameter, Chassis().getWheelDiameter());
        return this;
    }

    public Chassis Chassis() { return Chassis.WrapPart(this); }
    public SwerveDrive Chassis(Function<Chassis, Builder> fn) {
         return (SwerveDrive)addPart(Chassis.Define, fn);
    }

    public double calculateMaxSpeed() { 
        return SwerveModule.Wrap(getPiece(0)).calculateMaxSpeed();
    }

    public Translation2d getKinematic(int moduleNumber) {
        return Chassis().getModuleLocation(moduleNumber);
    }
    public SwerveDriveKinematics getKinematics() {
        Translation2d[] kinematics = getPieces().stream()
            .map(module->(Translation2d)module.getValue(SwerveModule.location))
            .toArray(Translation2d[]::new);
        return new SwerveDriveKinematics(kinematics);
    }

    public List<SwerveModule> getModules() {
        var parts = getPieces();
        return parts.stream()
            .map(p->SwerveModule.Wrap(p))
            .toList();
    }
    public SwerveModule SwerveModule(String name) {
        return SwerveModule.Wrap(getPieces().stream()
            .filter(p->p.Name() == name)
            .findFirst()
            .get());
    }

}
