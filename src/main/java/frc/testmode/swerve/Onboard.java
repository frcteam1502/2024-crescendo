package frc.testmode.swerve;

import com.ctre.phoenix6.configs.MagnetSensorConfigs;
import com.ctre.phoenix6.hardware.CANcoder;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import team1502.configuration.builders.motors.MotorController;
import team1502.configuration.builders.motors.SwerveModule;
import team1502.configuration.factory.RobotConfiguration;

/** looking for combo of Factory Acceptance Tests and All-Up Tests
 * Also initial "gentle" testing, no smoke, no grinding, etc
 */
public class Onboard {

    public Onboard(RobotConfiguration config) {
        module1 = config.SwerveDrive().SwerveModule(1);
        module2 = config.SwerveDrive().SwerveModule(2);
        module3 = config.SwerveDrive().SwerveModule(3);
        module4 = config.SwerveDrive().SwerveModule(4);

    }
    
    SwerveModule module1;
    SwerveModule module2;
    SwerveModule module3;
    SwerveModule module4;
    // Sensors, encoders, power, etc


    // SET ENCODER ALIGNMENT -- using Driver Station
    void initializeAlignment() {
        zeroPositions();

    }

    void zeroPositions() {
        zeroPosition(module1.TurningMotor());
    }

    CANcoder getCANcoder(SwerveModule module) {
        var encoder = module.Encoder();
        var canCoder = encoder.CANcoder();
        return (canCoder != null) ? canCoder : encoder.buildCANcoder();
    }

    void zeroPosition(SwerveModule module) {
        var canCoder = getCANcoder(module);
        canCoder.getConfigurator().apply(new MagnetSensorConfigs()); // now offset is zero
    }

    private void zeroPosition(MotorController motorController) {
        //var encoder = motorController.
    }

    void updateDashboard() {
        SmartDashboard.putNumber("Module1 Abs", module1.getCANcoder().getAbsolutePosition().getValue());
    }
}
