package team1502.configuration;

public class PowerProfile {
    /*
     * 
    public static PowerProfile getProfile(SupportedDevices equipmentType)
    {
        switch (equipmentType) {
            case Pigeon2 : return new PowerProfile(0.4);
            default: return null;
        }
    }
     */
    
    // see Supported Devices
    static void NominalDevicePower() {
        var neoBrushless = new PowerProfile(380); // free running current 1.4A, stall 100A
        var neo550Brushless = new PowerProfile(279); // ~265 @ 40A
        var compressor = new PowerProfile(120);
        var dc2dc = new PowerProfile(120); // sum of Pis and cameras
        var limelight = new PowerProfile(60);
        var radio = new PowerProfile(0.79);
        var radioPowerModule = new PowerProfile(12);
        var rsl = new PowerProfile(0.6);
        var rio = new PowerProfile(45);
        //var gyro = new PowerProfile(0.4);
        var tof = new PowerProfile(0.02);
    }

    public int channel;
    public double peakPower;
    public PowerProfile() {}
    
    public PowerProfile(double peakWatts /* watts */) {
        this.peakPower = peakWatts;
    }
    
    public PowerProfile PeakPower(double peakWatts /* watts */) {
        this.peakPower = peakWatts;
        return this;
    }

    public PowerProfile Channel(int channel) {
        this.channel = channel;
        return this;
    }

    public int getChannel() { return channel; }
    public double getPeakPower() { return peakPower; }
}
