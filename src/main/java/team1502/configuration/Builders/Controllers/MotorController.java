package team1502.configuration.Builders.Controllers;

import java.util.function.Function;

import com.revrobotics.CANSparkBase.IdleMode;

import team1502.configuration.Builders.Builder;
import team1502.configuration.Builders.GearBox;
import team1502.configuration.Builders.Motor;
import team1502.configuration.Builders.PID;
import team1502.configuration.CAN.DeviceType;
import team1502.configuration.CAN.Manufacturer;
import team1502.configuration.Parts.Part;

public class MotorController extends Controller {
    private static final String NAME = "MotorController"; 
    private static final String ISREVERSED = "isReversed";

    // Define
    public MotorController(String name, Manufacturer manufacturer, Function<MotorController, Builder> fn) {
        super(name, DeviceType.MotorController, manufacturer, fn);
    }
    public MotorController(String name, Function<MotorController, Builder> fn) {
        super(name, DeviceType.MotorController, fn);
    }
    
    //Build Proxy / Eval
    public MotorController() {
        super(DeviceType.MotorController);
    }
    public MotorController(Part part) {
        super(DeviceType.MotorController);
        setPart(part);
    }

    /**
     * Install
     */
    public MotorController(Function<MotorController, Builder> fn) {
        super(DeviceType.MotorController, fn);
    }

    @Override
    public Builder createBuilder() {
        return new MotorController((Function<MotorController, Builder>)buildFunction);
    }

    
    public MotorController Motor(String partName) {
        return Motor(partName, null);
    }
    public MotorController Motor(String partName, Function<Motor, Builder> fn) {
        Install("Motor", partName, fn);
        return this;
    }

    public Motor Motor() {
        var eval =  new Motor();
        eval.setPart(getPart("Motor"));
        return eval;
    }

    public IdleMode IdleMode() { return (IdleMode)getValue("idleMode"); }
    public MotorController IdleMode(IdleMode value) {
        setValue("idleMode", value);
        return this;
    }
    
    public boolean Reversed() {
        var result = getBoolean(ISREVERSED);
        return result == null ? false : result;
    }
    public MotorController Reversed(boolean value) {
        setValue(ISREVERSED, value);
        return this;
    }

    public GearBox GearBox() { return new GearBox(this); }
    public MotorController GearBox(Function<GearBox, Builder> fn) {
        return (MotorController)Install(new GearBox(fn));
    }
    
    public PID PID() { return new PID(this); }
    public MotorController PID(double p, double i, double d) {
        return (MotorController)Install(new PID(p, i, d));
    }
    public MotorController PID(double p, double i, double d, double ff) {
        return (MotorController)Install(new PID(p, i, d, ff));
    }
}
