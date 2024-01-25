package team1502.configuration.Builders;

import java.util.function.Function;

import com.revrobotics.CANSparkLowLevel;

public class Motor extends Builder {
    
    //Define
    public Motor(String name, Function<Motor, Builder> fn) {
        super("Motor", name, fn);
    }

    //Build Proxy / Eval
    public Motor() {
        super("Motor");
    }

    @Override
    public Builder createBuilder() {
        return new Motor(name, (Function<Motor, Builder>)buildFunction);
    }

    public Motor MotorType(CANSparkLowLevel.MotorType motorType) {
          return (Motor)Value("motorType", motorType);
    }      
    
    public CANSparkLowLevel.MotorType MotorType() {
            return (CANSparkLowLevel.MotorType)getValue("motorType");
    }

    public Motor FreeSpeedRPM(double speed) {
        return (Motor)Value("freeSpeedRPM", speed);
    }          
    public double FreeSpeedRPM() {
        return (double)getValue("freeSpeedRPM");
    }          
}
