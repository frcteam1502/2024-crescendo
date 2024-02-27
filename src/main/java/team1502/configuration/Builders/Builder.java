package team1502.configuration.builders;

import java.util.function.Function;

import edu.wpi.first.math.util.Units;

import java.util.List;

import team1502.configuration.CAN.CanInfo;
import team1502.configuration.builders.power.PowerDistributionModule;
import team1502.configuration.builders.power.PowerProfile;
import team1502.configuration.factory.PartBuilder;

public class Builder {
    private IBuild _build;
    private Part _part;

    public static String BUILD_TYPE = "buildType";
    public static String friendlyName = "friendlyName";
    public static String abbreviation = "abbreviation";
    public static Builder Wrap(Builder builder) { return new Builder(builder.getIBuild(), builder.getPart()); }
    public static Function<IBuild, Builder> Define = b->new Builder(b);
    public static Function<IBuild, Builder> DefineAs(String buildType) {
        return build->new Builder(build, buildType);
    }

    public Builder() {}
    public Builder(IBuild build, String buildType) {
        _build = build;
        _part = new Part(buildType);
        _build.register(_part);
        setValue(BUILD_TYPE, buildType);
    }
    public Builder(IBuild build) {
        _build = build;
        _part = new Part();
        _build.register(_part);
    }
    
    // TODO: use Part.parent or this this different, e.g., the Ancestor that "wrapped" it?
    public Builder parent; // e.g., the "wrapper"; a way to get info from higher up?
    protected Builder wrap(Part part) { return new Builder(getIBuild(), part); }
    // TODO: see above, and rename and/or resolve
    public Builder getParent() { return wrap(getPart().getParent()); }
    /**
     * Wrapping an existing part with another builder
     * @param build
     * @param part
     */
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

    public String getName() { return  getString(Part.BUILD_NAME, ""); }
    public Builder setName(String name) {
        Value(Part.BUILD_NAME, name);
        return this;
    }

    public String Name() { return getName(); }
    public Builder Name(String newName) { return setName(newName); }   

    public String Type() { return getString(BUILD_TYPE, ""); }
    public Builder Type(String buildType) {
        setValue(BUILD_TYPE, buildType);
        return this;
    }
    
    @Override
    public String toString() {
        return Name();
    }

    // === PARTS and PIECES ===

    /**
     * Add a generic part
     * @param partName
     * @param fn
     * @return
     */
    public Builder Part(String partName, Function<Builder, Builder> fn) {
        return installPart(partName, fn);
    }
    public Builder installPart(String partName, Function<Builder, Builder> fn) {
        addPart(Builder.Define, partName, partName, fn);
        return this;
    }

    /** return this */
    public Builder AddPart(PartBuilder<?> partBuilder) {
        var builder = partBuilder.creatBuilder(getIBuild());
        addPart(builder);
        return this;
    }
    /** return this */
    public <T extends Builder> Builder AddPart(Function<IBuild, T> define, Function<T, Builder> fn) {
        return AddPart(new PartBuilder<T>("", define, fn));
    }

    //public Builder Install(PartBuilder<?> partBuilder) { return addPart(partBuilder); }
    public Builder addPart(String newName, PartBuilder<?> partBuilder) {
        var builder = partBuilder.creatBuilder(getIBuild());
        builder.Name(newName);
        return addPart(builder);
    }
    public <T extends Builder> Builder addPart(Function<IBuild, T> define, String newName, String partName, Function<T, Builder> fn) {
        return addPart(newName, getIBuild().getTemplate(partName, define, fn));
    }
    public <T extends Builder> T addPart(Function<IBuild, T> define, String newName, Function<T, Builder> fn) {
        return (T)addPart(newName, new PartBuilder(define, fn));
    }

    public <T extends Builder> Builder addPart(Function<IBuild, T> define) {
        return addPart(define.apply(getIBuild()));
    }
    
    public Builder addPart(Builder part) {
        addPart(part.getPart());
        return part;
    }
    protected Part addPart(Part part) {
        _part.addPart(part);
        return part;
    }
    
    // ---- Pieces --------------
    /**
     * Install and move on, use addPiece to return the piece
     * @param <T>
     * @param define
     * @param newName
     * @param fn
     * @return the INSTALLER
     */
    public <T extends Builder> Builder InstallPiece(Function<IBuild, T> define, String newName, Function<T, Builder> fn) {
        addPiece(newName, new PartBuilder(define, fn));
        return this;
    }

    public Builder addPiece(PartBuilder<?> partBuilder) {
        var builder = partBuilder.creatBuilder(getIBuild());
        return addPiece(builder);
    }
    public <T extends Builder> Builder addPiece(Function<IBuild, T> define, String newName, String partName, Function<T, Builder> fn) {
        return addPiece(newName, getIBuild().getTemplate(partName, define, fn));
    }
    protected <T extends Builder> Builder addPiece(Function<IBuild, T> define) {
        return addPiece(define.apply(getIBuild()));
    }
    public Builder addPiece(String newName, PartBuilder<?> partBuilder) {
        var builder = partBuilder.creatBuilder(getIBuild());
        builder.Name(newName);
        return addPiece(builder);
    }
    protected Builder addPiece(Builder part) {
        addPiece(part.getPart());
        return part;
    }
    protected void addPiece(Part part) {
        _part.addPiece(part);
    }
    protected Builder refPiece(Builder part) {
        refPiece(part.getPart());
        return part;
    }
    protected void refPiece(Part part) {
        _part.refPiece(part);
    }

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
    public Part ensurePart(String valueName) {
        var part = getValue(valueName);
        if (part == null) {
            part = addPart(new Part(valueName));
        }
        return (Part)getValue(valueName);
    }
    
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

    public String Abbreviation() { return getString(abbreviation, Name()); }
    public Builder Abbreviation(String name) { return Value(abbreviation, name); }

    public String FriendlyName() { return getString(friendlyName, Name()); }
    public Builder FriendlyName(String name) { return Value(friendlyName, name); }

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
 
    void connectTo(Builder hub, Object channelID) {
        var ch = hub.findChannel(channelID);
        ch.connectToPart(this);
    }

    Channel findChannel(Object id) {
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

    public Channel addChannel(String signal, Builder node) { // e.g. CAN netwwork-node
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

    Channel addChannel(String signal, Object id) {
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

    // == CAN =========

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
        ensurePart(PowerProfile.powerProfile);
        return PowerProfile.WrapPart(this);
    }

    public boolean hasPowerProfile() {
        return PowerProfile().isPartPresent();
    }

    public double TotalPeakPower() { return PowerProfile().TotalPower(); }
    public Builder PeakPower(double peakWatts) {
        ensurePowerProfile().PeakPower(peakWatts);
        return this;
    }
    
    public int PowerChannel() { return PowerProfile().Channel(); }
    public Builder PowerChannel(int channel) {
        ensurePowerProfile().Channel(channel);
        return this;
    }
    
    /**
     * Notes the MPM that powers the component
     * @param channel
     * @param wireLabel
     * @return
     */
    public Builder PDH(int channel, String wireLabel) { return PDM(PowerDistributionModule.PDH, channel, wireLabel); }
    public Builder MPM(int channel, String wireLabel) { return PDM(PowerDistributionModule.MPM, channel, wireLabel); }
    public Builder MPM(int channel) { return PDM(PowerDistributionModule.MPM, channel); }
    public Builder PDH(int channel) { return PDM(PowerDistributionModule.PDH, channel); }
    public Builder MPM(String mpm, int channel) { return PDM(mpm, channel); }
    
    /**
     * Any of the Power Dstribution Modules
     * @param mpmName - PDP, PDH, MPM (or a named instance)
     * @param channel
     * @param wireLabel
     * @return
     */
    public Builder PDM(String mpmName, int channel, String wireLabel) {
        ensurePowerProfile().Label(wireLabel);
        return PDM(mpmName, channel);
    }
    public Builder PDM(String mpmName, Integer channel) {
        PowerDistributionModule mpm = PowerDistributionModule.Wrap(getIBuild().getInstalled(mpmName));
        this.connectTo(mpm, channel);
        // PowerChannel(channel);
        // mpm.updateChannel(this);
        return this;
    }

    public Builder Powers(Builder builder) {
        // TODO: build power tree for TotalPeakPower
        ensurePowerProfile().AddPowered(builder);
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
}
/*
 

== Part    ==  +--------------+
               | buildName    |     "instance" name     "Follower"
               +--------------+
               | originalName |     "inventory" name    "Arm Motor"
== Builder ==  +--------------+
               | buildType    |     device "type"       "MotorController"
==         ==  +--------------+
               |              |
               +==============+

Follower         | Arm Motor        | MotorController         | Arm.Leader.Follower                         
Motor            | NEO 550          | Motor                   | Arm.Leader.Motor                            


 */