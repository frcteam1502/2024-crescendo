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

    public Part Name(String name)
    {
        this.name = name;
        return this;
    }

    // /**
    //  * e.g., Logger name
    //  * @return
    //  */
    // public String FriendlyName() { return (String)getValue("friendlyName", name); }
    // public Part FriendlyName(String name) { return setValue("friendlyName", name); }
    
    public List<Part> getPieces() {
        return _pieces;
    }
    public Part getPiece(int index) {
        return _pieces.get(index);
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

    // public Object getValue(String valueName, Object defaultValue) {
    //     var result = getValue(valueName);
    //     return result != null ? result : defaultValue;
    // }
    
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

    
    // CAN

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

    // POWER
    
    public int PowerChannel() { return hasPowerProfile() ? _powerProfile.getChannel() : -1; }
    public double TotalPeakPower() { return hasPowerProfile() ? _powerProfile.getPeakPower() : 0.0; }
    
    private PowerProfile _powerProfile;
    public boolean hasPowerProfile() {
        return _powerProfile != null;
    }
    
    public PowerProfile getPowerProfile() {
        return _powerProfile;
    }
    
    public Part PeakPower(double peakPower) {
        return PowerProfile(p -> p.PeakPower(peakPower));
    }
    
    public Part PowerProfile(PowerProfile profile) {
        _powerProfile = profile;
        return this;
    }
    
    public Part PowerChannel(int channel) {
        return PowerProfile(p -> p.Channel(channel));
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
    

}
