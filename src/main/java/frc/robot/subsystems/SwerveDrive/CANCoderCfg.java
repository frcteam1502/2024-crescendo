package frc.robot.subsystems.SwerveDrive;

import com.ctre.phoenix6.hardware.CANcoder;
import com.ctre.phoenix6.signals.SensorDirectionValue;

public class CANCoderCfg {
    //Front Left CANCoder
  public static final CANcoder FRONT_LEFT_CAN_CODER = new CANcoder(16);
  public static final SensorDirectionValue FRONT_LEFT_CAN_CODER_DIRECTION = SensorDirectionValue.CounterClockwise_Positive;
  public static final double FRONT_LEFT_CAN_CODER_OFFSET = 306.72;

  //Front Right CANCoder
  public static final CANcoder FRONT_RIGHT_CAN_CODER = new CANcoder(10);
  public static final SensorDirectionValue FRONT_RIGHT_CAN_CODER_DIRECTION = SensorDirectionValue.CounterClockwise_Positive;
  public static final double FRONT_RIGHT_CAN_CODER_OFFSET = 126;

  //Back Left CANCoder
  public static final CANcoder BACK_LEFT_CAN_CODER = new CANcoder(4);
  public static final SensorDirectionValue BACK_LEFT_CAN_CODER_DIRECTION = SensorDirectionValue.CounterClockwise_Positive;
  public static final double BACK_LEFT_CAN_CODER_OFFSET = 60.84;

  //Back Right CANCoder
  public static final CANcoder BACK_RIGHT_CAN_CODER = new CANcoder(8);
  public static final SensorDirectionValue BACK_RIGHT_CAN_CODER_DIRECTION = SensorDirectionValue.CounterClockwise_Positive;
  public static final double BACK_RIGHT_CAN_CODER_OFFSET = 122.76;

  public static final double DISCONTINUITY_POINT = 0.5;

  public static final double MAGNET_OFFSET[] = {
    FRONT_LEFT_CAN_CODER_OFFSET,
    FRONT_RIGHT_CAN_CODER_OFFSET,
    BACK_LEFT_CAN_CODER_OFFSET,
    BACK_RIGHT_CAN_CODER_OFFSET
  };

  public static final SensorDirectionValue SENSOR_DIRECTION[] = {
    FRONT_LEFT_CAN_CODER_DIRECTION,
    FRONT_RIGHT_CAN_CODER_DIRECTION,
    BACK_LEFT_CAN_CODER_DIRECTION,
    BACK_RIGHT_CAN_CODER_DIRECTION
  };

}
