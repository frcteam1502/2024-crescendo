package team1502.configuration.Parts;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.function.Function;

import team1502.configuration.CAN.CanInfo;
import team1502.configuration.CAN.ICAN;
import team1502.configuration.PowerProfile;

public class Part implements ICAN {
    private HashMap<String, Object> _values = new HashMap<>();
    private ArrayList<Part> _pieces = new ArrayList<>();
    private ArrayList<String> _errorMessages = new ArrayList<>();

    public String name;
    public Part() {}

/*
import team1502.configuration.Builders.RobotBuilder;;
    private RobotBuilder _robotBuilder;
    public Part(String name, RobotBuilder rb) {
        this.name = name;
        _robotBuilder = rb;
    }
    public void setBuilder(RobotBuilder rb) {
        _robotBuilder=rb;
    }

    public Part Part(String name, Function<Part, Part> fn) {
        Part part = new Part(name, _robotBuilder);
        fn.apply(part);
        addPart(part);
        return this;
    }
    public Part Piece(String name, Function<Part, Part> fn) {
        Part part = new Part(name, _robotBuilder);
        fn.apply(part);
        addPiece(part);
        return this;
    }

    public Part Piece(Function<Part, Part> fn) {
        Part part = new Part();
        fn.apply(part);
        addPiece(part);
        return this;
    }
*/    
    public Part Name(String name)
    {
        this.name = name;
        return this;
    }

    
    public List<Part> getPieces() {
        return _pieces;
    }

    public Part addPiece(Part part) {
        _pieces.add(part);
        return this;
    }
    public Part addPart(Part part) {
        _values.put(part.name, part);
        return this;
    }
    
    public Part Value(String valueName, Object value) {
        return setValue(valueName, value);
    }

    public Part setValue(String valueName, Object value) {
        _values.put(valueName, value);
        return this;
    }
    
    public Object getValue(String valueName) {
        return _values.get(valueName);
    }

    public Boolean getBoolean(String valueName) {
        return (Boolean)getValue(valueName);
    }
    
    public Double getDouble(String valueName) {
        return (Double)getValue(valueName);
    }

    public Double getDoubleFromInt(String valueName) {
        return ((Integer)getValue(valueName)).doubleValue();
    }

    public Part getPart(String valueName) {
        return (Part)getValue(valueName);
    }

    private CanInfo _canInfo;
    public boolean isCanDevice() {
        return _canInfo != null;
    }
    
    @Override //ICAN
    public String getName() {
        return name;
    }
    @Override //ICAN
    public CanInfo getCanInfo() {
        return _canInfo;
    }
    
	@Override //ICAN
	public void addError(String errorMessage) {
        _errorMessages.add(errorMessage);	
	}
	@Override // ICAN
	public List<String> getErrors() {
        return _errorMessages;
	}
    
    public Part CanInfo(CanInfo canInfo)
    {
        _canInfo = canInfo;
        return this;
    }
    
    public Part CanInfo(Function<CanInfo, CanInfo> fn)
    {
        if (_canInfo == null) {
            _canInfo = new CanInfo();
        }
        return CanInfo(fn.apply(_canInfo));
    }

    private PowerProfile _powerProfile;
    public boolean hasPowerProfile() {
        return _powerProfile != null;
    }
    
    public PowerProfile getPowerProfile() {
        return _powerProfile;
    }
    
    public Part PowerProfile(double peakPower) {
        _powerProfile = new PowerProfile(peakPower);
        return this;
    }
    
    public Part PowerProfile(PowerProfile profile) {
        _powerProfile = profile;
        return this;
    }
    
    public Part PowerProfile(Function<PowerProfile, PowerProfile> fn) {
        if (_powerProfile == null) {
            _powerProfile = new PowerProfile();
        }
        return PowerProfile(fn.apply(_powerProfile));
    }
    /*
*/ 
    /*
    public static void print(String msg) {
        System.out.print(msg);
    }
    public static void println(String msg) {
        System.out.println(msg);
    }
    public void print(String format, String...keys) {
        var values = Arrays.stream(keys).map(key -> getValue(key)).toArray();
        System.out.print(MessageFormat.format(format, values));

    }
    public String format(String format, String...keys) {
        var values = Arrays.stream(keys).map(key -> getValue(key)).toArray();
        return MessageFormat.format(format, values);
    }
    */
    
/*
    
    public Part Note(String name, String detail)
    {
        return this.setValue(name, detail) ;
    }
    public Part GearBox(Function<Part, Part> fn) {
        // get parent name for full path
        return Part("GearBox", fn);
    }
    
    public Part Gear(String stage, int drivingTeeth, int drivenTeeth) {
        return this.Piece("stage", s1 -> s1
            .setValue("drivingTeeth", drivingTeeth)
            .setValue("drivenTeeth", drivenTeeth)
        );
    }
    public double getGearRatio() {
        //double ratio = 1.0;
        var gearBox = this.getPart("GearBox");
        var stages = gearBox.getPart("Stages").getPieces();
        var ratios = stages.stream().map(stage->stage.getDoubleFromInt("drivingTeeth")/stage.getDoubleFromInt("drivenTeeth"));
        return ratios.reduce(1.0, (stageA,stageB) -> stageA * stageB);
        //return stages.stream().reduce(1.0, (stageA,stageB) -> (stageA.getDouble("drivingTeeth")/stageA.getDouble("drivenTeeth") * stageB.getDouble("drivingTeeth")/stageB.getDouble("drivenTeeth")));
        // stages.forEach(stage->ratio = ratio * stage.getDouble("drivingTeeth")/stage.getDouble("drivenTeeth"));
        // return ratio;
    }
*/

}
