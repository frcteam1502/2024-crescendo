package team1502.configuration.builders.sensors;

import java.util.function.Function;

import com.ctre.phoenix6.hardware.Pigeon2;
import com.revrobotics.CANSparkMax;

import team1502.configuration.CAN.CanInfo;
import team1502.configuration.CAN.DeviceType;
import team1502.configuration.CAN.Manufacturer;
import team1502.configuration.builders.Builder;
import team1502.configuration.builders.IBuild;
import team1502.configuration.builders.Part;

public class IMU extends Builder {
    private static final DeviceType deviceType = DeviceType.IMU;
    public static final String Pigeon2 = "Pigeon2";
    public static Function<IBuild, IMU> Define(Manufacturer manufacturer) {
        return build->new IMU(build,manufacturer);
    } 
    public static IMU Wrap(Builder builder) { return new IMU(builder.getIBuild(), builder.getPart()); }
    public static IMU WrapPart(Builder builder) { return WrapPart(builder, deviceType.name()); }
    public static IMU WrapPart(Builder builder, String partName) { return Wrap(builder.getPart(partName)); }

    // Define
    public IMU(IBuild build, Manufacturer manufacturer) {
        super(build);
        CanInfo.addConnector(this, deviceType, manufacturer);
    }
    
    //Build Proxy / Eval
    public IMU(IBuild build, Part part) { super(build, part); }

/*
    // Pigeon2 configuration items:
    public double MountPoseYaw = 0;
    public double MountPosePitch = 0;
    public double MountPoseRoll = 0;
    public boolean EnableCompass = false;
    public boolean DisableTemperatureCompensation = false;
    public boolean DisableNoMotionCalibration = false;
    public double XAxisGyroError = 0;
    public double YAxisGyroError = 0;
    public double ZAxisGyroError = 0;
*/
    public Pigeon2 buildPigeon2() {
        var imu = Pigeon2(new Pigeon2(CanNumber()));
        return imu;
    }
    public Pigeon2 Pigeon2() {
        return (Pigeon2)Value(IMU.Pigeon2);
    }
    public Pigeon2 Pigeon2(Pigeon2 imu) {
        Value(IMU.Pigeon2, imu);
        return imu;
    }

    
}
