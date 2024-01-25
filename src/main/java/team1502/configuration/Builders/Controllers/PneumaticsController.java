package team1502.configuration.Builders.Controllers;

public class PneumaticsController {
    public PneumaticsController Compressor(String name) {
        return this;
    }
    public PneumaticsController Solenoid(int module, int channel, String name) {
        return this;
    }
    public PneumaticsController DoubleSolenoid(int forwardChannel, int reverseChannel, String name) {
        return this;
    }

}
