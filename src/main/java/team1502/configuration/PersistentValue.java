package team1502.configuration;

import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class PersistentValue {
    
    private class PersistentDouble{
    
        private NetworkTableEntry entry;
    
        public PersistentDouble(String name, double defaultValue){
            entry = SmartDashboard.getEntry(name);
            entry.setDefaultNumber(defaultValue);
            entry.setPersistent();  
        }

        public double getValue(){
            return entry.getDouble(0);
        }
  
    }
}
