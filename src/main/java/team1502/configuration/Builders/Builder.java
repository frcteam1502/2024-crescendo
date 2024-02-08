package team1502.configuration.builders;

import java.util.function.Function;
import java.util.List;

import team1502.configuration.CAN.CanInfo;
import team1502.configuration.CAN.DeviceType;
import team1502.configuration.CAN.Manufacturer;
import team1502.configuration.builders.power.PowerDistributionModule;
import team1502.configuration.builders.power.PowerProfile;
import team1502.configuration.factory.PartBuilder;

public class Builder {
    private IBuild _build;
    private Part _part;

    public static String BUILD_TYPE = "buildType";
    public static Builder Wrap(Builder builder) { return new Builder(builder.getIBuild(), builder.getPart()); }
    public static Function<IBuild, Builder> Define = b->new Builder(b);

    public Builder() {}
    public Builder(IBuild build, String buildType) {
        this(build);
        setValue(BUILD_TYPE, buildType);
    }
    public Builder(IBuild build) {
        _build = build;
        register();
    }
    
    private Builder wrap(Part part) { return new Builder(getIBuild(), part); }
    
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
    
    private void register() {
        _part = new Part();
        _build.register(_part);
    }

    public IBuild getIBuild() { return _build; }
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

    //public Builder Install(PartBuilder<?> partBuilder) { return addPart(partBuilder); }
    public Builder addPart(String newName, PartBuilder<?> partBuilder) {
        var builder = partBuilder.creatBuilder(getIBuild());
        builder.Name(newName);
        return addPart(builder);
    }
    public Builder addPart(PartBuilder<?> partBuilder) {
        var builder = partBuilder.creatBuilder(getIBuild());
        addPart(builder);
        return this;
    }
    public <T extends Builder> Builder addPart(Function<IBuild, T> define, Function<T, Builder> fn) {
        return addPart(new PartBuilder<T>("", define, fn));
    }
    public <T extends Builder> Builder addPart(Function<IBuild, T> define, String newName, String partName, Function<T, Builder> fn) {
        return addPart(newName, getIBuild().getTemplate(partName, define, fn));
    }
    public <T extends Builder> Builder addPart(Function<IBuild, T> define, String newName, Function<T, Builder> fn) {
        return addPart(newName, new PartBuilder(define, fn));
    }
    private Builder addPart(Builder part) {
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

    public Builder getPart(String valueName) { return wrap((Part)getValue(valueName));  }
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

    public String Abbreviation() { return getString("abbreviation", Name()); }
    public Builder Abbreviation(String name) { return Value("abbreviation", name); }

    public String FriendlyName() { return getString("friendlyName", Name()); }
    public Builder FriendlyName(String name) { return Value("friendlyName", name); }

    public String Note(String name) { return (String)Value(name); }    
    public Builder Note(String name, String detail) { return Value(name, detail); }    
    
    // == CAN =========

    private CanInfo CanInfo() { return CanInfo.WrapPart(this); }
    private CanInfo ensureCanInfo() {
        ensurePart(CanInfo.canInfo);
        return CanInfo.WrapPart(this);
    }

    public Builder Device(DeviceType deviceType) {
        if (Type() == "") {
            Type(deviceType.toString());
        }
        ensureCanInfo().Device(deviceType);
        return this;
    }

    public Manufacturer Manufacturer() { return getManufacturer();  }
    public Manufacturer getManufacturer() { return CanInfo().Manufacturer();  }
    public Builder Manufacturer(Manufacturer manufacturer) {
        ensureCanInfo().Manufacturer(manufacturer);
        return this;
    }

    public int CanNumber() { return getCanNumber(); }
    public int getCanNumber() { return CanInfo().Number(); }
    public Builder CanNumber(int number) {
        setCanNumber(number);
        return this;
    }
    public void setCanNumber(int number) {
        ensureCanInfo().Number(number);
    }

    // == CAN ============== - TODO: move to CAN
    
    public void addError(String errorMessage) {
        _part.addError(errorMessage);
    }
    public List<String> getErrors() { return _part.getErrors(); }
    public boolean hasErrors() {return getErrors().size() != 0; }

    
    // == POWER ========

    private PowerProfile PowerProfile() { return PowerProfile.WrapPart(this); }
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
    
    // public Builder PowerProfile(PowerProfile power) {
    //     ensurePowerProfile().PowerProfile(power);
    //     return this;
    // }

    /**
     * Notes the MPM that powers the component
     * @param channel
     * @param wireLabel
     * @return
     */
    public Builder PDH(int channel, String wireLabel) { return PDM(PowerDistributionModule.PDH, channel, wireLabel); }
    public Builder MPM(int channel, String wireLabel) { return PDM(PowerDistributionModule.MPM, channel, wireLabel); }
    public Builder MPM(int channel) { return PDM(PowerDistributionModule.MPM, channel); }
    
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
    public Builder PDM(String mpmName, int channel) {
        PowerDistributionModule mpm = PowerDistributionModule.Wrap(getIBuild().getInstalled(mpmName));
        PowerChannel(channel);
        mpm.updateChannel(this);
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
    
    
}
