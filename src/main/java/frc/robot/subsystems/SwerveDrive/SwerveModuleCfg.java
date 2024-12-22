package frc.robot.subsystems.SwerveDrive;

import com.revrobotics.spark.config.SparkBaseConfig;

import edu.wpi.first.math.util.Units;

public class SwerveModuleCfg {
      // kinematics
  public static final double WHEEL_DIAMETER_METERS = Units.inchesToMeters(4);
  public static final double DRIVE_GEAR_RATIO = 1 / ((14.0 / 50.0) * (28.0 / 16.0) * (15.0 / 45.0));//L3 gearing
  public static final double STEER_GEAR_RATIO = 1 / ((14.0 / 50.0) * (10.0 / 60.0));
  public static final double DRIVE_METERS_PER_ENCODER_REV = (WHEEL_DIAMETER_METERS * Math.PI) / DRIVE_GEAR_RATIO;
  public static final double DRIVE_ENCODER_MPS_PER_REV = DRIVE_METERS_PER_ENCODER_REV / 60;
  
  public static final double DRIVE_MOTOR_KV = 565;//https://docs.revrobotics.com/brushless/neo/vortex#motor-specifications
  
  public static final double MODULE_TURN_PID_CONTROLLER_P = 3.4;
  public static final double MODULE_TURN_PID_CONTROLLER_I = 0;
  public static final double MODULE_TURN_PID_CONTROLLER_D = 0;
  
  public static final double MODULE_DRIVE_PID_CONTROLLER_F = 1/DRIVE_MOTOR_KV;
  public static final double MODULE_DRIVE_PID_CONTROLLER_P = .0005;
  public static final double MODULE_DRIVE_PID_CONTROLLER_I = 0;
  public static final double MODULE_DRIVE_PID_CONTROLLER_D = 0;

  public static final double CLSD_LOOP_RAMP_RATE_SECONDS = .5;
  public static final int SMART_CURRENT_LIMIT = 80;

  public static final SparkBaseConfig.IdleMode DRIVE_IDLE_MODE  = SparkBaseConfig.IdleMode.kBrake;
  public static final SparkBaseConfig.IdleMode TURN_IDLE_MODE   = SparkBaseConfig.IdleMode.kBrake;


  /*
  public static final double MAX_METERS_PER_SECOND = 4.4; //5600 * DRIVE_ENCODER_MPS_PER_REV;
  public static final double TURNING_DEGREES_PER_ENCODER_REV = 360 / STEER_GEAR_RATIO;
  public static final double RADIANS_PER_ENCODER_REV = TURNING_DEGREES_PER_ENCODER_REV * (Math.PI/180);
  public static final double MAX_MODULE_ROTATION_RADIANS_PER_SECOND = Math.PI/2;
  public static final double MAX_MODULE_ROTATION_RADIANS_PER_SECOND_PER_SECOND = Math.PI;
  */
}
