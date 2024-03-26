package frc.testmode.swerve;

import com.ctre.phoenix6.configs.MagnetSensorConfigs;
import com.ctre.phoenix6.hardware.CANcoder;
import com.revrobotics.CANSparkMax;
import com.revrobotics.REVPhysicsSim;

import edu.wpi.first.math.controller.PIDController;
import edu.wpi.first.util.sendable.Sendable;
import edu.wpi.first.util.sendable.SendableBuilder;
import edu.wpi.first.util.sendable.SendableRegistry;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.CommandScheduler;
import edu.wpi.first.wpilibj2.command.FunctionalCommand;
import edu.wpi.first.wpilibj2.command.InstantCommand;
import edu.wpi.first.wpilibj2.command.RunCommand;
import edu.wpi.first.wpilibj2.command.Subsystem;
import frc.robot.Robot;
import frc.robot.RobotContainer;
import frc.robot.commands.ControllerCommands;
import frc.robot.subsystems.SwerveDrive.DriveSubsystem;
import team1502.configuration.builders.motors.MotorController;
import team1502.configuration.builders.motors.SwerveDrive;
import team1502.configuration.builders.motors.SwerveModule;
import team1502.configuration.factory.RobotConfiguration;
import team1502.injection.RobotFactory;

// final class CANCoders {
//     public static final CANcoder FRONT_LEFT_CAN_CODER = new CANcoder(16);
//     public static final CANcoder FRONT_RIGHT_CAN_CODER = new CANcoder(10);
//     public static final CANcoder BACK_LEFT_CAN_CODER = new CANcoder(4);
//     public static final CANcoder BACK_RIGHT_CAN_CODER = new CANcoder(8);
// }
  
public class AbsoluteEncoderAlignment implements Subsystem, Sendable {
    public static Command StartAlignmentCommand(RobotContainer robotContainer) {
        
        var alignment = new AbsoluteEncoderAlignment(robotContainer);
        var command = new FunctionalCommand(
            ()->alignment.init(), //AbsoluteEncoderAlignment.StartAlignment());
            ()->alignment.execute(),
            interrupted -> alignment.finish(),
            ()->alignment.isDone(),
            alignment
        );
        return command;
    } 
    public static void StartAlignment(RobotContainer robotContainer) {
        SmartDashboard.putData("Alignment Start", StartAlignmentCommand(robotContainer));
    }

    private final RobotConfiguration config;
    private final RobotFactory factory;
    private AbsoluteEncoderAlignment(RobotContainer robotContainer) {
        this.config = robotContainer.getConfig();
        this.factory = robotContainer.getFactory();

        String name = this.getClass().getSimpleName();
        name = name.substring(name.lastIndexOf('.') + 1);
        SendableRegistry.addLW(this, name, name);
        //CommandScheduler.getInstance().registerSubsystem(this);

        module1 = new ModuleAlignment(1);
        module2 = new ModuleAlignment(2);
        module3 = new ModuleAlignment(3);
        module4 = new ModuleAlignment(4);
    }
    
    ModuleAlignment module1;
    ModuleAlignment module2;
    ModuleAlignment module3;
    ModuleAlignment module4;
    
    Command zeroCommand = new InstantCommand(()->zeroPositions());
    Command setCommand = new InstantCommand(()->setOffsets());
    Command resetCommand = new InstantCommand(()->resetOffsets());
    Command saveCommand = new InstantCommand(()->saveOffsets());
    Command driveCommand = new InstantCommand(()->drive());
    Command stopCommand = new InstantCommand(()->stop());

    void init() {
        zeroPositions();
        disableDriveController(); // take control away so periodic processing doesn't interfere
    }

    Subsystem disabledSubsystem;
    Command disabledCommand;
    void disableDriveController() {
        disabledSubsystem = (Subsystem)factory.getInstance(frc.robot.subsystems.SwerveDrive.DriveSubsystem.class);
        disabledCommand = (Command)factory.getInstance(frc.robot.commands.ControllerCommands.class);
        CommandScheduler.getInstance().unregisterSubsystem(disabledSubsystem);
        disabledCommand.cancel();
    }
    void enableSubsystem() {
        CommandScheduler.getInstance().registerSubsystem(disabledSubsystem);
    }

    int epochs;
    boolean _putStop;
    void execute() {
        epochs++;
        if (!_putStop) {
            _putStop = true;

            SmartDashboard.putData("Alignment Stop", new InstantCommand(()->done()));
            SmartDashboard.putData(this);
            SmartDashboard.putData(module1);
            SmartDashboard.putData(module2);
            SmartDashboard.putData(module3);
            SmartDashboard.putData(module4);
        }
        module1.execute();;
        module2.execute();;
        module3.execute();;
        module4.execute();;
        updateDashboard();
    }

    @Override
    public void periodic() {
        updateDashboard();
    }

    void updateDashboard(){
        //SmartDashboard.putData(this);
        //SmartDashboard.putData(module1);
    }
    
    void zeroPositions(){
        module1.zeroPosition();
        module2.zeroPosition();
        module3.zeroPosition();
        module4.zeroPosition();
    }
    void setOffsets(){
        module1.setOffset();
        module2.setOffset();
        module3.setOffset();
        module4.setOffset();
    }
    void saveOffsets(){
        module1.saveOffset();
        module2.saveOffset();
        module3.saveOffset();
        module4.saveOffset();
    }
    void resetOffsets(){
        module1.resetOffset();
        module2.resetOffset();
        module3.resetOffset();
        module4.resetOffset();
    }
    void drive(){
        module1.drive();
        module2.drive();
        module3.drive();
        module4.drive();
    }
    void stop(){
        module1.stop();
        module2.stop();
        module3.stop();
        module4.stop();
    }
    
    double angle;
    void turn(double degrees) {
        if (angle != degrees) {
            angle = degrees;
            module1.turn(degrees);
            module2.turn(degrees);
            module3.turn(degrees);
            module4.turn(degrees);
        }
    }
    
    void finish() {
        CommandScheduler.getInstance().unregisterSubsystem(this);
        var entry = SmartDashboard.getEntry("Stop Alignment");
        entry.unpublish();
        entry.close();
        _done = true;
        module1.stop();
        module2.stop();
        module3.stop();
        module4.stop();
    }

    boolean _done;
    void done() {
        _done = true;
    }
    boolean isDone() {
        return _done;
    }

    private static final String SUBSYSTEM = AbsoluteEncoderAlignment.class.getSimpleName();
    @Override
    public void initSendable(SendableBuilder builder) {
        builder.setSmartDashboardType(SUBSYSTEM);

        builder.addIntegerProperty("epochs", ()->this.epochs, null);
        builder.addDoubleProperty("turn", ()->this.angle, (angle)->turn(angle));

        SendableRegistry.addLW(module1, SUBSYSTEM, "Align " + module1.getName() );
        SendableRegistry.addLW(module2, SUBSYSTEM, "Align " + module2.getName() );
        SendableRegistry.addLW(module3, SUBSYSTEM, "Align " + module3.getName() );
        SendableRegistry.addLW(module4, SUBSYSTEM, "Align " + module4.getName() );
        SendableRegistry.addLW(zeroCommand, SUBSYSTEM, "Align zero");
        SendableRegistry.addLW(setCommand, SUBSYSTEM, "Align set");
        SendableRegistry.addLW(resetCommand, SUBSYSTEM, "Align reset");
        SendableRegistry.addLW(saveCommand, SUBSYSTEM, "Align save");
        SendableRegistry.addLW(driveCommand, SUBSYSTEM, "Align drive");
        SendableRegistry.addLW(stopCommand, SUBSYSTEM, "Align stop");

        // SmartDashboard.putData("zero Cmd",zeroCommand);
        // SmartDashboard.putData("set Cmd",this.setCommand);
        // SmartDashboard.putData("reset Cmd",this.resetCommand);
        // SmartDashboard.putData("save Cmd",this.saveCommand);
        // SmartDashboard.putData("drive Cmd",driveCommand);
        // SmartDashboard.putData("stop Cmd",stopCommand);
        
    }

    class ModuleAlignment implements Sendable {
        final static String SUBSYSTEM_NAME = ModuleAlignment.class.getSimpleName();
        final static String _originalOffset_ = "originalOffset";
        final static String _currentOffset_ = "currentOffset";
        
        SwerveModule module;
        PIDController turningPIDController;
        Command driveCommand = new InstantCommand(()->drive());
        Command stopCommand = new InstantCommand(()->stop());
        public ModuleAlignment(int moduleNumber) {
            module = config.SwerveDrive().SwerveModule(moduleNumber);
            module.Value(_originalOffset_, module.Encoder().MagneticOffset());

            this.turningPIDController = module.TurningMotor().PID().createPIDController();

            if (Robot.isSimulation()) {
                var re = module.TurningMotor().buildRelativeEncoder();
                REVPhysicsSim.getInstance().addSparkMax(
                    getTurning(), 
                    (float)module.TurningMotor().Motor().StallTorque(),
                    (float)module.TurningMotor().Motor().FreeSpeedRPM());
            }
        }
        
        public String getName() { return module.FriendlyName(); }
        public void zeroPosition() {
            setOffset(0.0);
        }
        public void resetOffset() {
            setOffset(module.getDouble(_originalOffset_));
        }
        public void setOffset() {
            setOffset(module.Encoder().MagneticOffset());
        }
        public void saveOffset() {
            module.Encoder().MagneticOffset(module.getDouble(_currentOffset_));
        }
        public void setOffset(double degrees) {
            module.Value(_currentOffset_, degrees);
            module.Encoder().setMagneticOffset(degrees/360.0);
        }
        public void execute() {
            if (turning>0) { turn();}
        }

        int turning;
        double target;
        public void turn(double degrees) {
            if (target != degrees) {
                target = degrees;
                turning = 6;
            }
        }
        double lastPostion;
        public void turn() {
            final double turnOutput = turningPIDController.calculate(Math.toRadians(getAngle()), Math.toRadians(target));
            getTurning().setVoltage(turnOutput);
            if (Math.abs(turnOutput) < 0.01) {
                turning--;
            }
            if (Robot.isSimulation()) {
                REVPhysicsSim.getInstance().run();
                var simPosition = getTurning().getEncoder().getPosition();
                var simState = module.Encoder().CANcoder().getSimState();
                var deltaPosition = simPosition - lastPostion;
                lastPostion = simPosition;
                simState.addPosition(deltaPosition); //turnOutput/360);
            }
        }
        public void drive() {
            //var circumference = module.DrivingMotor().findDouble(MotorController.wheelDiameter, 0.0) * Math.PI;
            // var position = module.DrivingMotor().getPosition();
            // module.DrivingMotor().setPosition(position + circumference);
            module.DrivingMotor().CANSparkMax().setVoltage(0.51);
        }

        public void stop() {
            module.DrivingMotor().CANSparkMax().setVoltage(0.0);
        }
        
        CANSparkMax getDriving() { return module.DrivingMotor().CANSparkMax();}
        CANSparkMax getTurning() { return module.TurningMotor().CANSparkMax();}
        double getAngle() { return module.Encoder().getPosition(); }

        @Override
        public void initSendable(SendableBuilder builder) {
        
            builder.setSmartDashboardType(SUBSYSTEM_NAME);

            builder.addDoubleProperty("Align " + module.Name() + " Original Offset", ()->module.getDouble(_originalOffset_), null);
            builder.addDoubleProperty("Align " + module.Name() + " Current Offset", ()->module.getDouble(_currentOffset_), null);
            builder.addDoubleProperty("Align " + module.Name() + " MagneticOffset", ()->module.Encoder().MagneticOffset(), (offset)->module.Encoder().setMagneticOffset(offset));
            builder.addDoubleProperty("Align " + module.Name() + " Turn", ()->getAngle(), null);
            builder.addIntegerProperty("Align " + module.Name() + " Turning", ()->turning, null);
            builder.addDoubleProperty("Align " + module.Name() + " Target", ()->target, (angle)->turn(angle));
            builder.addDoubleProperty("Align " + module.Name() + " DriveV", ()-> getDriving().get(), null);
            builder.addDoubleProperty("Align " + module.Name() + " DriveO", ()-> getDriving().getAppliedOutput(), null);
            builder.addDoubleProperty("Align " + module.Name() + " DriveP", ()-> module.DrivingMotor().getPosition()*360, null);
            builder.addDoubleProperty("Align " + module.Name() + " Id", ()-> getDriving().getDeviceId(), null);
            builder.addIntegerProperty("Align " + module.Name() + " Faults", ()-> getDriving().getFaults(), null);
            builder.addBooleanProperty("Align " + module.Name() + " Reversed", ()-> getDriving().getInverted(), null);

            //SendableRegistry.addLW(module1, "ModuleAlignment", module1.getName() );
            //SmartDashboard.putData(module1);
            SendableRegistry.addLW(module.Encoder().CANcoder(), SUBSYSTEM_NAME, "Align Encoder " + module.getName() );
            SendableRegistry.addLW(driveCommand, SUBSYSTEM_NAME, "Align (spin drive) " + module.getName() );
            SendableRegistry.addLW(stopCommand, SUBSYSTEM_NAME, "Align (stop drive) " + module.getName() );
            
            // SmartDashboard.putData(driveCommand);
            // SmartDashboard.putData(stopCommand);
            // SmartDashboard.putData(module.getCANcoder());
        }
/*
 * 
    Driving Motor
 
 
    Turning Motor
        buildSpakMax -- does NOT build relative-encoder/conversion-factors
        PID().CreatePIDController
        Encoder().buildCANcoder()


 */
    }
}
