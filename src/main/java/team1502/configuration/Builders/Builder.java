package team1502.configuration.Builders;

import java.util.function.Function;
import java.util.List;

import team1502.configuration.PowerProfile;
import team1502.configuration.CAN.CanInfo;
import team1502.configuration.CAN.DeviceType;
import team1502.configuration.CAN.Manufacturer;
import team1502.configuration.Parts.Part;

public class Builder {
    public String name;
    public Function<? extends Builder, Builder> buildFunction;
    public String buildType = "";
    
    private IBuild _build;
    private Part _part;

    public Builder() {}
    /*
     * 
     //private Builder _parent;
     
     public Builder(Part part, IBuild build, Function<Builder, Builder> fn){
         _part = part;
         _build = build;
         fn.apply(this);
        }
        
    */

    // Just a build function (for partFactory)
    public Builder(String name, Function<Builder, Builder> fn){
        this.name = name;
        buildFunction = fn;
    }

    public Builder(String buildType) {
        this.buildType = buildType;
        this.name = buildType; // default name can be useful for sub-parts (eval)
    }
    
    // EVAL
    // protected Builder(String buildType, Part part) {
    //     this.buildType = buildType;
    //     _part = part;
    // }

    // Build Proxy
    protected Builder(String buildType, String name) {
        this.buildType = buildType;
        this.name = name;
    }

    protected Builder(String buildType, String name, Function<? extends Builder,  Builder> fn) {
        this.buildType = buildType;
        this.name = name;
        buildFunction = fn;
    }

    protected void Fill(String buildType, String name, Function<? extends Builder,  Builder> fn) {
        this.buildType = buildType;
        this.name = name;
        buildFunction = fn;
    }

    public Builder createBuilder(Builder builder) {
        builder.Fill(buildType, name, buildFunction);
        return builder;
    }

    public Builder createBuilder() {
        return new Builder(buildType, name, buildFunction);
    }

/*
    protected Builder(String buildType, String name, IBuild build, Function<Builder, Builder> fn)
    {
        this.name = name;
        buildFunction = fn;
        this.buildType = buildType;
        _build = build;
    }

    public Builder(Builder parent){
        _parent =  parent;
    }

    public RobotBuilder getRobotBuilder() {
        return null; //_parent == null ? (RobotBuilder)this : _parent.getRobotBuilder();
    }
 * 
 public Part createPart(String name) {
     return _build.createPart(name, name);
    }

    protected void install() {
        install(_part);
    }

    protected void install(Part part) {
        _parent.install(part);
    }
    */

    public String getName() {
        return  (_part != null) ? _part.name : this.name;
    }

    public Builder Name(String newName) {
        name = newName;
        if (_part != null) {
            _part.name = newName;
        }
        return this;
    
    }   
    // public Builder install(IBuild build) {
    //     build.install(this);
    //     return this;
    // }
    
    // initial part constructor, reusable with new fn for modification
    public Builder create(IBuild build) {
        _build = build;
        _part = new Part().Name(name);
        setValue("buildType", buildType);
        build();
        return this; //_part;
    }
    
    public Builder create(IBuild build, Function<? extends Builder, Builder> fn) {
        create(build);
        apply(fn);
        return this;
    }

    public Builder create(IBuild build, String name, Function<? extends Builder, Builder> fn) {
        create(build, name);
        apply(fn);
        return this;
    }

    public Builder create(IBuild build, String name) {
        _build = build;
        _part = new Part().Name(name);
        setValue("buildType", buildType);
        build();
        return this;
    }

    public Builder build() {
        if (buildType != "" && buildType != _part.getValue("buildType")) {
            _part.addError(buildType + " expected");
        }
        apply(buildFunction);//onBuild(_part, buildFunction);
        //install(_part);
        return this;
    }

    protected void onBuild(Part part, Function<Builder, Builder> fn) {
        apply(fn);
    }

    // (intended) empty builder (e.g., of another subclass) to do the work
    protected <T extends Builder> Object eval(Function<T, ? extends Object> fn, Part part) {
        _part = part;
        return fn.apply((T)this);
    }

    // use another builder (of the right subclass) to apply
    public <T extends Builder> Object evalWith(Function<T, ? extends Object> fn, T builder) {
        return builder.eval(fn, _part);
    }

    public Builder Apply(Function<? extends Builder,  Builder> fn) {
        apply(fn);
        return this;
    }
    // Normal apply
    protected <T extends Builder> void apply(Function<T, Builder> fn) {
        if (fn != null) {
            fn.apply((T)this);
        }
        //return this;
    }
    
    public Builder Build(Part part) {
        _part = part;
        return build();
    }

    public Builder Build(IBuild build, Part part, Function<? extends Builder, Builder> fn) {
        _part = part;
        buildFunction = fn;
        return build();
    }
    
    public Builder Create(String partName, Function<? extends Builder, Builder> fn) {
        var builder = _build.createBuilder(partName, fn);
        return builder;
    }
    public Builder Existing(String partName, Function<? extends Builder, Builder> fn) {
        var builder = _build.modifyBuilder(partName, fn);
        return builder;
    }
    
    // install a part with a new name
    public Builder Install(String newName, String partName, Function<? extends Builder, Builder> fn) {
        var builder = _build.createBuilder(partName, fn);
        // default to existing -- although likely used
        // var builder = _build.modifyBuilder(partName, fn);
        // if (builder == null) { // backup to part factory
        //     builder = _build.createBuilder(partName, fn);
        // }
        return builder
            .Name(newName)
            .addPartTo(this);
    }
    public Builder InstallPiece(String newName, String partName, Function<? extends Builder, Builder> fn) {
        var builder = _build.createBuilder(partName, fn);
        return builder
            .Name(newName)
            .addPieceTo(this);
    }

    public Builder InstallPiece(Builder builder) {
        builder.create(_build);
        builder.addPieceTo(this);
        return this;
    }
    public Builder Install(Builder builder) {
        builder.create(_build);
        builder.addPartTo(this);
        return this;
    }
    public <T extends Builder> Builder Install(T builder, Function<T, Builder> fn) {

        return this;
    }
    
    // "raw" install -- need a Function the creates the builder to be installed
    public Builder Install(String newName, Function<Builder, Builder> fn) {
        var builder = fn.apply(this);
        builder.Name(newName);
        builder.addPartTo(this);
        return this;
    }
    
    private Builder addPartTo(Builder builder) {
        builder.addPart(_part);
        return this;
    }
    
    private void addPart(Part part) {
        _part.addPart(part);
    }

    private Builder addPieceTo(Builder builder) {
        builder.addPiece(_part);
        return this;
    }
    private void addPiece(Part part) {
        _part.addPiece(part);
    }

    // E.g., eval - grab a part and put it in an empty builder
    public void setPart(Part part) {
        _part = part;
        //name = part.name;
    }
/*
 * 
    public Builder Build(String deviceId, String partName, Function<Part, Part> fn)
    {        
        _part = createPart(partName);
        _part.name= deviceId;
        fn.apply(_part);
        install(_part);
        return this;
    }

    public Builder Build(String name, Function<Part, Part> fn)
    {        
        return Build(name, name, fn);
    }    
 */

    // VALUES / (EVAL?)
    
    public void configure(Function<Part, Part> fn)
    {
        fn.apply(_part);
    }
    
    protected Builder setValue(String valueName, Object value) {
        _part.setValue(valueName, value);
        return this;
    }    

    public Object Value(String valueName) {
        return getValue(valueName);
    }
    public Builder Value(String valueName, Object value) {
        return setValue(valueName, value);
    }    

    public String Note(String name) {
        return (String)Value(name);
    }    
    public Builder Note(String name, String detail) {        
        return Value(name, detail);
    }    
    
    public Builder Type(String buildType) {
        this.buildType =  buildType;
        return this;
    }

    // CAN
    public Builder Device(DeviceType deviceType) {
        if (buildType == "") {
            Type(deviceType.toString());
        }
        configure(part -> part.CanInfo(c -> c.Device(deviceType)));
        return this;
    }

    public Builder Manufacturer(Manufacturer manufacturer) {
        configure(part -> part.CanInfo(c -> c.Manufacturer(manufacturer)));
        return this;
    }
    public Builder CanInfo(Function<CanInfo, CanInfo> fn)
    {
        _part.CanInfo(fn);
        return this;
    }

    public void setCanNumber(int number) {
        configure(part -> part.CanInfo(c -> c.Number(number)));
    }

    
    // POWER
/*
    public Builder PowerProfile(double peakPower) {
        _part.PowerProfile(peakPower);
        return this;
    }
    public Builder PowerProfile(PowerProfile power) {
        _part.PowerProfile(power);
        return this;
    }
*/    
    
    // EVAL 

    public Object getValue(String valueName, Object defaultValue) {
        var result = getValue(valueName);
        return result != null ? result : defaultValue;
    }
    public Object getValue(String valueName) {
        return _part.getValue(valueName);
    }
    
    public Boolean getBoolean(String valueName) {
        return (Boolean)getValue(valueName);
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

    // Parts and Pieces
    public Part getPart() {
        return _part;
    }
    public Part getPart(String valueName) {
        return (Part)getValue(valueName);
    }
    
    public List<Part> getPieces() {
        return _part.getPieces();
    }

    // CAN
    public int getCanNumber() {
        return _part.getCanId();
    }
    public Manufacturer getManufacturer() {
        return _part.getCanInfo().manufacturer;
    }
    
}
