package frc.robot.subsystems.SwerveDrive;

import com.revrobotics.spark.SparkFlex;
import com.revrobotics.spark.SparkLowLevel;
import com.revrobotics.spark.SparkMax;

final class ChassisMotorCfg {
    //drive
    public static final SparkFlex DRIVE_FRONT_LEFT   = new SparkFlex(17, SparkLowLevel.MotorType.kBrushless);
    public static final SparkFlex DRIVE_FRONT_RIGHT  = new SparkFlex(11, SparkLowLevel.MotorType.kBrushless);
    public static final SparkFlex DRIVE_BACK_RIGHT   = new SparkFlex(9, SparkLowLevel.MotorType.kBrushless);
    public static final SparkFlex DRIVE_BACK_LEFT    = new SparkFlex(5, SparkLowLevel.MotorType.kBrushless);

    public static final boolean DRIVE_FRONT_LEFT_REVERSED   = true;
    public static final boolean DRIVE_FRONT_RIGHT_REVERSED  = true;
    public static final boolean DRIVE_BACK_LEFT_REVERSED    = true;
    public static final boolean DRIVE_BACK_RIGHT_REVERSED   = true;

    public static final boolean DRIVE_MOTOR_REVERSED[] = {
        DRIVE_FRONT_LEFT_REVERSED,
        DRIVE_FRONT_RIGHT_REVERSED,
        DRIVE_BACK_LEFT_REVERSED,
        DRIVE_BACK_RIGHT_REVERSED
      };
  
    //turn
    public static final SparkMax ANGLE_FRONT_LEFT   = new SparkMax(16, SparkLowLevel.MotorType.kBrushless);
    public static final SparkMax ANGLE_FRONT_RIGHT  = new SparkMax(10, SparkLowLevel.MotorType.kBrushless);
    public static final SparkMax ANGLE_BACK_RIGHT   = new SparkMax(8, SparkLowLevel.MotorType.kBrushless);
    public static final SparkMax ANGLE_BACK_LEFT    = new SparkMax(4, SparkLowLevel.MotorType.kBrushless);

    public static final boolean ANGLE_FRONT_LEFT_REVERSED   = true;
    public static final boolean ANGLE_FRONT_RIGHT_REVERSED  = true;
    public static final boolean ANGLE_BACK_RIGHT_REVERSED   = true;
    public static final boolean ANGLE_BACK_LEFT_REVERSED    = true;

    public static final boolean ANGLE_MOTOR_REVERSED[] = {
        ANGLE_FRONT_LEFT_REVERSED,
        ANGLE_FRONT_RIGHT_REVERSED,
        ANGLE_BACK_LEFT_REVERSED,
        ANGLE_BACK_RIGHT_REVERSED
      };
}