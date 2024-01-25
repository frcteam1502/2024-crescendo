package team1502.configuration.CAN;

// see https://docs.wpilib.org/en/stable/docs/software/can-devices/can-addressing.html
public enum DeviceType {
    RobotController         ("Robot Controller",         1),
    MotorController         ("Motor Controller",         2), 
    RelayController         ("Relay Controller",         3),    
    GyroSensor              ("Gyro Sensor",              4),    
    Accelerometer           ("Accelerometer",            5),    
    UltrasonicSensor        ("Ultrasonic Sensor",        6),    
    GearToothSensor         ("Gear Tooth Sensor",        7),    
    PowerDistributionModule ("Power Distribution Module", 8),
    PneumaticsController    ("Pneumatics Controller",    9),
    Miscellaneous           ("Miscellaneous",           10),
    IOBreakout              ("IO Breakout",             11);

    public String DeviceName;
    public int DeviceID;
    
    private DeviceType(String name, int id) {DeviceName = name; DeviceID = id;}
}

