package team1502.configuration.builders;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Part {
    private HashMap<String, Object> _values = new HashMap<>();
    private ArrayList<Part> _pieces = new ArrayList<>();
    private ArrayList<String> _errorMessages = new ArrayList<>();

    public static String BUILD_NAME = "buildName";
    public static String ORIGINAL_NAME = "originalName";

    //public String name;
    public Part() {}
    public Part(String name) {
        setValue(Part.BUILD_NAME, name);
        setValue(Part.ORIGINAL_NAME, name);
    }
    
    public List<Part> getPieces() { return _pieces;  }
    
    public Part getPiece(int index) { return _pieces.get(index); }
    public Part addPiece(Part part) {
        _pieces.add(part);
        return this;
    }
    
    public Part getPart(String valueName) { return (Part)getValue(valueName); }
    public Part addPart(Part part) {
        _values.put((String)part.getValue(BUILD_NAME), part);
        return this;
    }
    
    public Object getValue(String valueName) { return _values.get(valueName);  }
    public Part setValue(String valueName, Object value) {
        _values.put(valueName, value);
        return this;
    }

    // Move this to CAN ?
	public void addError(String errorMessage) {
        _errorMessages.add(errorMessage);	
	}

    public List<String> getErrors() {
        return _errorMessages;
	}

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
