package team1502.configuration.builders;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Part {
    private HashMap<String, Object> _values = new HashMap<>();
    private ArrayList<Part> _pieces = new ArrayList<>();
    private ArrayList<String> _errorMessages = new ArrayList<>();
    private Part parent;
    /** normally the key */
    public static String BUILD_NAME = "buildName"; 
    /** override the key */
    public static String KEY_NAME = "keyName";
    public static String TEMPLATE_NAME = "templateName";

    // COMMON Value keys useful when working with parts
    static String CLASS_NAME = "className";
    static String CATEGORY_NAME = "categoryName";
    public static String friendlyName = "friendlyName";
    public static String abbreviation = "abbreviation";


    public Part() {}
    public Part(String name) { setValue(Part.BUILD_NAME, name);  }

    @Override
    public String toString() { return getKey(); }
    
    public String getName() { return (String)getValue(BUILD_NAME); }
    public Part setName(String name) {
         setValue(BUILD_NAME, name); 
         return this;
    }

    public String getType() { return (String)getValue(Part.CLASS_NAME); }
    
    public String getKey() {
        return parent == null
            ? getName()
            : parent.getKey() + "." + getName();
    }
    
    Part getParent() { return this.parent; }
    public Part setParent(Part parent) {
        if (this.parent == null ) {
            this.parent = parent;
        } else if (this.parent != parent) {
            System.out.println(parent.getKey() + " trying to reparent " + this.getKey());
        }
        return this;
    }

    public List<Part> getPieces() { return _pieces;  }
    
    public Part getPiece(int index) { return _pieces.get(index); }
    public Part addPiece(Part part) {
        if (part.getParent() == null) { // warning: didn't use PartBuilder
            part.setParent(this);
        }
        return refPiece(part);
    }
    public Part refPiece(Part part) {
        _pieces.add(part);
        return this;
    }
    
    public Part getPart(String valueName) { return (Part)getValue(valueName); }
    public Part addPart(Part part) {
        if (part.getParent() == null) { // warning: didn't use PartBuilder
            part.setParent(this);
        }
        var key = (String)part.getValue(BUILD_NAME);
        if (part.hasValue(KEY_NAME)) {            
            key = (String)part.getValue(KEY_NAME);
        }
        setValue(key, part);
        return this;
    }
    
    
    public HashMap<String,Object> getValues() { return _values;  }
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
}

    /*

        +-----------------+
        | parent          |
        |                 |
        | values (map)    |
        |  +--------------+ --- Initial Part Values ------------
        |  |BUILD_NAME    | (default key, to map this part)
        |  +--------------+ --- Optional Part values of note ---
        |  |TEMPLATE_NAME |
        |  +--------------+
        |  |KEY_NAME      | (override for default key)
        |  +--------------+
        |                 |
        | pieces[]        |
        |  +--------------+
        |  | 0            |
        |  +--------------+
        |                 |
        +-----------------+


    */
    

