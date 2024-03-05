package team1502.configuration.builders;

import java.util.function.Function;

import edu.wpi.first.math.util.Units;

import java.text.MessageFormat;
import java.util.Arrays;
import java.util.List;

import team1502.configuration.CAN.CanInfo;
import team1502.configuration.CAN.DeviceType;
import team1502.configuration.CAN.Manufacturer;
import team1502.configuration.builders.pneumatics.PneumaticsController;
import team1502.configuration.builders.power.PowerDistributionModule;
import team1502.configuration.builders.power.PowerProfile;
import team1502.configuration.factory.PartBuilder;
import team1502.configuration.factory.RobotBuilder;

public class Builder {
    private static final String CLASSNAME = "Builder";
    
    public static final String POWER = Channel.SIGNAL_12VDC;

    public static Function<IBuild, Builder> Define = b->new Builder(b);
    public static Function<IBuild, Builder> DefineAs(String buildType) {
        return build->new Builder(build, buildType);
    }
    public static Builder Wrap(Builder builder) { return new Builder(builder.getIBuild(), builder.getPart()); }

    private IBuild _build;
    private Part _part;

    public Builder() {}
    protected Builder(IBuild build, DeviceType deviceType, String name) {
        this(build, deviceType);
        Name(name);
    }

    /** assumes deviceType is also the CLASS_NAME */
    protected Builder(IBuild build, DeviceType deviceType, Manufacturer manufacturer) {
        this(build, deviceType);
        CanInfo.addConnector(this, deviceType, manufacturer);
    }
    protected Builder(IBuild build, DeviceType deviceType) {
         this(build, deviceType.toString());
    }
    // public Builder(IBuild build, String className, String ) {
    // }
    
    protected Builder(IBuild build, String className) {
        _build = build;
        _part = new Part(className);
        Type(className);
        _build.register(_part);
    }
    private Builder(IBuild build) {
        _build = build;
        _part = new Part();
        _build.register(_part);
    }
    
    public void initialize() {

    }

    protected Builder wrap(Part part) { return new Builder(getIBuild(), part); }
    
    public Builder getParentOfType(String type) {
        var parent = getParent();
        return parent != null && parent.Type() == type ? parent : null;
    }
    public Builder getParent() {
        if (isPartPresent()) {
            var parent = getPart().getParent();
            return parent == null ? null : wrap(parent);
        }
        return null;
    }
    public boolean hasParent() { return isPartPresent() && getPart().getParent() != null; }
    
    /** Wrapping an existing part with another builder   */
    public Builder(IBuild build, Part part) {
        this(part);
        _build = build;
    }
    private Builder(Part part) {
        _part = part;
    }
    

    public IBuild getIBuild() { return _build; }
    public Builder setIBuild(IBuild build) { 
        _build = build;
        return this;
     }
    public boolean isPartPresent() {return getPart() != null; }

    public String getName() { return getString(Part.BUILD_NAME, ""); }
    public Builder setName(String name) {
        Value(Part.BUILD_NAME, name);
        return this;
    }

    public String Name() { return getName(); }
    public Builder Name(String newName) { return setName(newName); }   

    public String Type() { return getString(Part.CLASS_NAME, CLASSNAME); }
    public Builder Type(String className) {
        setValue(Part.CLASS_NAME, className);
        return this;
    }
    public String Category() { return getString(Part.CATEGORY_NAME, ""); }
    public Builder Category(String category) {
        setValue(Part.CATEGORY_NAME, category);
        return this;
    }
    
    @Override
    public String toString() {
        return _part == null ? Name() : _part.toString();
    }

    protected Builder findValue(String key) {
        return hasValue(key)
            ? this
            : hasParent()
                ? getParent().findValue(key)
                : null;
    }
    // === PARTS and PIECES ===

    /** Add a generic part  */
    public Builder Part(String partName, Function<Builder, Builder> fn) {
        addPart(Builder.Define, partName, partName, fn);
        return this;
    }

    public Builder Install(String partName, Function<Builder, Builder> fn) {
        var partBuilder = getIBuild().getTemplate(partName, Builder.Define, fn);
        addPart(partName, partBuilder);
        return this;
    }

    private Builder AddPart(PartBuilder<?> partBuilder) {
        addPart(partBuilder.addBuilder(this));
        return this;
    }
    /** build something with its default name; return this */
    public <T extends Builder> Builder AddPart(Function<IBuild, T> define, Function<T, Builder> fn) {
        return AddPart(new PartBuilder<T>(define, fn));
    }

    public <T extends Builder> T addPart(Function<IBuild, T> define, Function<T, Builder> fn) {
        return addPart(new PartBuilder<T>(define, fn));
    }
    public <T extends Builder> T addPart(PartBuilder<T> partBuilder) {
        return addPart(partBuilder.addBuilder(this));
    }
    public <T extends Builder> T addPart(String newName, PartBuilder<T> partBuilder) {
        return addPart(partBuilder.addBuilder(this, newName));
    }
    
    public <T extends Builder> T addPart(Function<IBuild, T> define, String newName, String partName, Function<T, Builder> fn) {
        return addPart(newName, getIBuild().getTemplate(partName, define, fn));
    }
    public <T extends Builder> T addPart(Function<IBuild, T> define, String newName, Function<T, Builder> fn) {
        return addPart(newName, new PartBuilder<>(define, fn));
    }

    public <T extends Builder> T addPart(Function<IBuild, T> define) {
        return addPart(new PartBuilder<>(define, null));
    }
    
    public <T extends Builder> T addPart(T part) {
        addPart(part.getPart());
        return part;
    }
    private void addPart(Part part) {
        _part.addPart(part);
    }
    
    Builder findPart(String name) {
        var part = getIBuild().getInstalled(name);
        return part;
    }
    public Builder usePart(String key) { return usePart(key, k->k); }
    public Builder usePart(String key, Function<Builder, Builder> path) {
        var part = findPart(key);
        Value(key, path.apply(part).getPart());
        return this;
    }

  
    // ---- Pieces --------------

    /** Install and move on, use addPiece to return the piece  */
    public <T extends Builder> Builder Piece(Function<IBuild, T> define, String newName, Function<T, Builder> fn) {
        addPiece(newName, new PartBuilder<>(define, fn));
        return this;
    }

    public <T extends Builder> T addPiece(PartBuilder<T> partBuilder) {
        return addPiece(partBuilder.addBuilder(this));
    }
    public <T extends Builder> T addPiece(String newName, PartBuilder<T> partBuilder) {
        return addPiece(partBuilder.addBuilder(this, newName));
    }

    public <T extends Builder> Builder addPiece(Function<IBuild, T> define, String newName, String partName, Function<T, Builder> fn) {
        return addPiece(newName, getIBuild().getTemplate(partName, define, fn));
    }
    protected <T extends Builder> Builder addPiece(Function<IBuild, T> define) {
        return addPiece(new PartBuilder<>(define, null));
    }

    private <T extends Builder> T addPiece(T part) {
        addPiece(part.getPart());
        return part;
    }

    private void addPiece(Part part) {
        _part.addPiece(part);
    }
    // protected Builder refPiece(Builder part) {
    //     refPiece(part.getPart());
    //     return part;
    // }
    // protected void refPiece(Part part) {
    //     _part.refPiece(part);
    // }

    public Builder Part(String valueName) { return getPart(valueName);  }
    public Builder getPart(String valueName) { 
        if (hasValue(valueName)) {
            var value = getValue(valueName);
            if (value instanceof Part) {
                return wrap((Part)value);  
            }
        }
        return null;
    }
    public Part getPart() { return _part;  }
    public void setPart(Part part) {
        _part = part;
    }
    // Parts and Pieces
    // public Part ensurePart(String valueName) {
    //     var part = getValue(valueName);
    //     if (part == null) {
    //         part = addPart(new Part(valueName));
    //     }
    //     return (Part)getValue(valueName);
    // }
    
    public List<Builder> getPieces() {
        return _part.getPieces().stream().map(p->wrap(p)).toList();
    }

    public Builder getPiece(int index) { return wrap(_part.getPiece(index));  }
    
    
    // == VALUES / (EVAL?) ==========
    public boolean hasValue(String valueName) { return _part == null ? false : _part.hasValue(valueName); }
    public Object getValue(String valueName) { return _part.getValue(valueName); }
    protected Builder setValue(String valueName, Object value) {
        _part.setValue(valueName, value);
        return this;
    }    
    
    public Object Value(String valueName) { return getValue(valueName); }
    public Builder Value(String valueName, Object value) { return setValue(valueName, value); }    

    public String Abbreviation() { return getString(Part.abbreviation, Name()); }
    public Builder Abbreviation(String name) { return Value(Part.abbreviation, name); }

    public String FriendlyName() { return getString(Part.friendlyName, Name()); }
    public Builder FriendlyName(String name) { return Value(Part.friendlyName, name); }

    public String Note(String name) { return (String)Value(name); }    
    public Builder Note(String name, String detail) { return Value(name, detail); }    
    
    // =======================================================================
    // == "Connector" ====


    public Connector addConnector(String signal) { return addConnector(signal, signal); }
    public Connector addConnector(String signal, String name) {
        return addPart(Connector.Define(signal), name, c->c);
    }
    public Connector findConnector(String signal) { return Connector.findConnector(this, signal); }

    void connectTo(Builder hub, String signal, Object channelID) {
        if (hub.hasValue(signal)) {
            var subHub = hub.getPart(signal);
            if (subHub != null) {
                hub = subHub;
            }
        }
        connectTo(hub, channelID);
    }
 
    Connector connectTo(String hubName, Object channelID, String label) {
        return connectTo(hubName, channelID).Label(label);
    }

    Connector connectTo(String hubName, Object channelID) {
        var hub = getIBuild().getInstalled(hubName);
        return connectTo(hub, channelID);
    }

    public Connector connectTo(Builder hub, Object channelID) {
        var ch = hub.findChannel(channelID);
        return ch.connectToPart(this);
    }

    public Channel findChannel(Object id) {
        return Channel.findChannel(this, id);
    }
    
    // == "Channel" ====
    public Builder Channel(String signal, Object id) {
        addChannel(signal, id);
        return this;
    }

    public Channel createChannel(String signal, Object id) {
        var ch = new Channel(getIBuild(), signal, Name(), id);
        return ch;
    }

    Channel createChannelForNode(String signal, Builder node) {
        var ch = new Channel(getIBuild(), signal, Name(), node.getPart().getKey());
        return ch;
    }

    public Channel addChannel(String signal, Builder node) { // e.g. CAN network-node
        if (hasValue(signal)) {
            var subHub = getPart(signal);
            if (subHub != null) {
                return subHub.addChannel(signal, node);
            }
        }
        var ch = addChannel(createChannelForNode(signal, node));
        ch.connectToPart(node);
        return ch;
    }

    public Channel addChannel(String signal, Object id) {
        return addChannel(createChannel(signal, id));
    }
    Channel addChannel(Channel ch) {
        if (ch.ID() instanceof Integer) {
            addPiece(ch);
        } else {
            addPart(ch);
        }
        return ch;
    }

    public Integer DigitalInput(String name) { return getPart(name).getInt(RoboRIO.digitalInput); }
    // == CAN =========
    public Builder CanInfo(DeviceType deviceType, Manufacturer manufacturer, int number) {
        CanInfo(deviceType, manufacturer);
        CanNumber(number);
        return this;
    }
    public Builder CanInfo(DeviceType deviceType, Manufacturer manufacturer) {
        addPart(CanInfo.Define(deviceType, manufacturer));
        return this;
    }
    public Integer CanNumber() { return CanInfo.WrapPart(this).Number(); }
    public Builder CanNumber(int number) {
        CanInfo.WrapPart(this).Number(number);
        return this;
    }
   
    public void addError(String errorMessage) {
        _part.addError(errorMessage);
    }
    public List<String> getErrors() { return _part.getErrors(); }
    public boolean hasErrors() {return getErrors().size() != 0; }

    
    // == POWER ========

    public PowerProfile PowerProfile() { return PowerProfile.WrapPart(this); }
    protected PowerProfile ensurePowerProfile() {
        if (!hasValue(PowerProfile.CLASSNAME))
        {
            addPart(PowerProfile.Define);
        }
        return PowerProfile.WrapPart(this);
    }

    public boolean hasPowerProfile() {
        return hasValue(PowerProfile.CLASSNAME);
    }

    public Builder PeakPower(double peakWatts) {
        ensurePowerProfile().PeakPower(peakWatts);
        return this;
    }
    
    /** Notes the PDH that powers the component and the wire harness label  */
    public Builder PDH(int channel, String wireLabel) { return ConnectTo(PowerDistributionModule.PDH, channel, wireLabel); }
    /** Notes the PDH that powers the component  */
    public Builder PDH(int channel) { return ConnectTo(PowerDistributionModule.PDH, channel); }
    /** Notes the PCM that powers the component  */
    public Builder PCM(int channel) { return ConnectTo(PneumaticsController.PCM, channel); }
    
    /** Notes the MPM that powers the component and the wire harness label  */
    public Builder MPM(int channel, String wireLabel) { return ConnectTo(PowerDistributionModule.MPM, channel, wireLabel); }
    /** Notes the MPM that powers the component  */
    public Builder MPM(int channel) { return MPM(PowerDistributionModule.MPM, channel); }
    /** Notes the MPM that powers the component  */
    public Builder MPM(String mpm, int channel) { return ConnectTo(mpm, channel); }
    
    /**
     * Any of the Power Dstribution Modules
     * @param mpmName - PDP, PDH, MPM (or a named instance), PCM
     * @param channel "name" or 0 (Integer)
     * @param wireLabel - optional label for wire harness
     * @return
     */
    public Builder ConnectTo(String mpmName, Object channelID, String wireLabel) {
        connectTo(mpmName, channelID, wireLabel);
        return this;
    }
    public Builder ConnectTo(String mpmName, Integer channel) {
        connectTo(mpmName, channel);
        return this;
    }

    public Builder Powers(Builder builder) {
        this.addChannel(POWER, builder);
        return this;
    }

    // EVAL 

    public String ShortName() {
        String shortName = Abbreviation();
        if (shortName == null) {
            shortName = FriendlyName();
        }
        if (shortName == null) {
            shortName = getName();
        }
        return shortName;
    }

    public String getString(String valueName, String defaultValue) {
        return (String)getValue(valueName, defaultValue);
    }
    public String getString(String valueName) {
        return (String)getValue(valueName);
    }
    
    public Object getValue(String valueName, Object defaultValue) {
        var result = getValue(valueName);
        return result != null ? result : defaultValue;
    }
    
    public boolean getBoolean(String valueName, boolean defaultValue) {
        var result = getBoolean(valueName);
        return result == null ? defaultValue : result;
    }

    public Boolean getBoolean(String valueName) {
        return (Boolean)getValue(valueName);
    }
    
    public double findDouble(String valueName, double defaultValue) {
        var found = findValue(valueName);
        var result = found != null ? (Double)found.getValue(valueName) : null;
        return result != null ? result : defaultValue;
    }
    public double getDouble(String valueName, double defaultValue) {
        var result = (Double)getValue(valueName);
        return result != null ? result : defaultValue;
    }
    public Double getDouble(String valueName) {
        return (Double)getValue(valueName);
    }

    public Integer getInt(String valueName) {
        return (Integer)getValue(valueName);
    }

    public Double getDoubleFromInt(String valueName) {
        return ((Integer)getValue(valueName)).doubleValue();
    }
    
    public Double getMeters(String valueName) {
        return Units.inchesToMeters(getDouble(valueName));
    }
    
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
        |  +--------------+ --- Initial Builder Values ------------
        |  |CLASS_NAME    | may be same as DeviceType
        |  +--------------+
        |  |CLASS_GROUP   | e.g., Connector, Channel or shared DeviceType
        |  +--------------+
        |                 |
        | pieces[]        |
        |  +--------------+
        |  | 0            |
        |  +--------------+
        |                 |
        +-----------------+


== Part    ==  +--------------+
               | Name         |     "instance" name     "Follower"
               +--------------+
               | template     |     "inventory" name    "Arm Motor"
== Builder ==  +--------------+
               | buildType    |     device "type"       "MotorController"
==         ==  +--------------+
               |              |
               +==============+

Follower         | Arm Motor        | MotorController         | Arm.Leader.Follower                         
Motor            | NEO 550          | Motor                   | Arm.Leader.Motor                            


 */