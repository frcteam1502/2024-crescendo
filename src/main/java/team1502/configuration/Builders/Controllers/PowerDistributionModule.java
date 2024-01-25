package team1502.configuration.Builders.Controllers;

import java.util.function.Function;

public class PowerDistributionModule {
    
    public PowerDistributionModule Ch(Integer channel, String name) {
        return this;
    }
    public PowerDistributionModule Ch(Integer channel, int fuze, String name) {
        return this;
    }
    public PowerDistributionModule Ch(Integer channel) { // empty
        return this;
    }
    public PowerDistributionModule Module(String module, String ... sub) {
        return this;
    }
    public PowerDistributionModule Module(String module, Function<PowerDistributionModule,PowerDistributionModule> fn) {
        return this;
    }
}
