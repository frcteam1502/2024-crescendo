package frc.robot.subsystems.SwerveDrive;

import edu.wpi.first.math.kinematics.SwerveModulePosition;
import edu.wpi.first.math.kinematics.SwerveModuleState;
import edu.wpi.first.util.sendable.Sendable;
import edu.wpi.first.util.sendable.SendableBuilder;
import edu.wpi.first.util.sendable.SendableRegistry;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import team1502.configuration.factory.RobotConfiguration;

public class SwerveModules implements Sendable {
    private SwerveModule[] _modules;
    private String[] _moduleNames;
    public final double maxSpeed;
    public final double empiricalSpeed; // for comparison
  
    public SwerveModules(RobotConfiguration config)
    {
        var modules = config.Eval(e -> e.SwerveDrive(d->d.getModules()));
        _modules = modules.stream()
                    .map(m->new SwerveModule(m))
                    .toArray(SwerveModule[]::new);

        _moduleNames = modules.stream()
                    .map(m->m.FriendlyName())
                    .toArray(String[]::new);

        var swerveModule = modules.get(0); 
        maxSpeed = swerveModule.DrivingMotor().Motor().FreeSpeedRPM() / 60.0
                    * swerveModule.DrivingMotor().GearBox().GearRatio()
                    * swerveModule.getDouble("wheelDiameter") * Math.PI;

        empiricalSpeed = swerveModule.DrivingMotor().Motor().getDouble("empiricalFreeSpeed") / 60.0
                    * swerveModule.DrivingMotor().GearBox().GearRatio()
                    * swerveModule.getDouble("wheelDiameter") * Math.PI;
    }

    public void setDesiredState(SwerveModuleState[] swerveModuleStates) {
        _modules[0].setDesiredState(swerveModuleStates[0]);
        _modules[1].setDesiredState(swerveModuleStates[1]);
        _modules[2].setDesiredState(swerveModuleStates[2]);
        _modules[3].setDesiredState(swerveModuleStates[3]);
    }

    public SwerveModuleState[] getModuleStates() {
        return new SwerveModuleState[] {
            _modules[0].getState(),
            _modules[1].getState(),
            _modules[2].getState(),
            _modules[3].getState()
        };
    }

    public SwerveModulePosition[] getModulePositions() {
        return new SwerveModulePosition[] {
            _modules[0].getPosition(),
            _modules[1].getPosition(),
            _modules[2].getPosition(),
            _modules[3].getPosition()
        };
    }

    public void resetModules() {
        _modules[0].zeroModule();
        _modules[1].zeroModule();
        _modules[2].zeroModule();
        _modules[3].zeroModule();
    }

    @Override
    public void initSendable(SendableBuilder builder) {
        builder.setSmartDashboardType("SwerveModules");

        for(int i = 0; i < 4; i++) {
            SendableRegistry.addLW(_modules[i], "SwerveModules", _moduleNames[i] );
        }
    }
    
    public void send() {
        SmartDashboard.putData(this);
        SmartDashboard.putData(_modules[0]);
        SmartDashboard.putData(_modules[1]);
        SmartDashboard.putData(_modules[2]);
        SmartDashboard.putData(_modules[3]);
    }
}
