package team1502.configuration.builders;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Part {
    private HashMap<String, Object> _values = new HashMap<>();
    private ArrayList<Part> _pieces = new ArrayList<>();
    private ArrayList<String> _errorMessages = new ArrayList<>();
    private Part parent;

    public static String BUILD_NAME = "buildName";
    public static String ORIGINAL_NAME = "originalName";

    public Part() {}
    public Part(String name) {
        setValue(Part.BUILD_NAME, name);
        setValue(Part.ORIGINAL_NAME, name);
    }
    
    public String getName() { return (String)getValue(BUILD_NAME); }
    public Part setName(String name) {
         setValue(BUILD_NAME, name); 
         return this;
    }

    public String getKey() {
        return parent == null
            ? getName()
            : parent.getKey() + "." + getName();
    }
    

    public Part setParent(Part parent) {
        if (this.parent == null ) {
            this.parent = parent;
        } else {
            System.out.println(parent.getKey() + " trying to reparent " + this.getKey());
        }
        return this;
    }

    public List<Part> getPieces() { return _pieces;  }
    
    public Part getPiece(int index) { return _pieces.get(index); }
    public Part addPiece(Part part) {
        part.setParent(this);
        return refPiece(part);
    }
    public Part refPiece(Part part) {
        _pieces.add(part);
        return this;
    }
    
    public Part getPart(String valueName) { return (Part)getValue(valueName); }
    public Part addPart(Part part) {
        part.setParent(this);
        _values.put((String)part.getValue(BUILD_NAME), part);
        return this;
    }
    
    
    public boolean hasValue(String valueName) { return _values.containsKey(valueName); }
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
