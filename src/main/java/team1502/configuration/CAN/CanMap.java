package team1502.configuration.CAN;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;

public class CanMap {
    private EnumMap<DeviceType,
         EnumMap<Manufacturer,
             HashMap<Integer, ICAN>>> deviceMap = new EnumMap<>(DeviceType.class);

    private ArrayList<ICAN> _errorParts = new ArrayList<>();
    private ArrayList<String> _errorMessages = new ArrayList<>();

    public void install(ICAN canDevice) {
        CanInfo canInfo = canDevice.getCanInfo();
        if (canInfo.isValid()) {
            if (!deviceMap.containsKey(canInfo.deviceType)) {
                deviceMap.put(canInfo.deviceType, new EnumMap<>(Manufacturer.class));
            }

            var mfrMap = deviceMap.get(canInfo.deviceType);
            if (!mfrMap.containsKey(canInfo.manufacturer)) {
                mfrMap.put(canInfo.manufacturer, new HashMap<Integer, ICAN>());
            }

            var idMap = mfrMap.get(canInfo.manufacturer);            
            if (idMap.containsKey(canInfo.deviceNumber)) {
                var existing =idMap.get(canInfo.deviceNumber);
                var msg = MessageFormat.format("CAN ID collision on {2} \"{0}\" from \"{1}\" where mfr = \"{3}\" and id = {4}!", existing.getName(), canDevice.getName(), canInfo.deviceType, canInfo.manufacturer, canInfo.deviceNumber);
                 _errorMessages.add(msg);
                 canDevice.addError(msg);
                 _errorParts.add(canDevice);
            }
            else {
                idMap.put(canInfo.deviceNumber, canDevice);
            }

        } else {

        }
    }

    public List<ICAN> getDevices() {
        ArrayList<ICAN> devices = new ArrayList<>(_errorParts);
        deviceMap.values().forEach(d -> d.values().forEach(m -> m.values().forEach(id -> devices.add(id))));
        
        Comparator<ICAN> comparator = Comparator.comparing(d -> d.getCanInfo().deviceType.DeviceName);
        comparator = comparator.thenComparing(Comparator.comparing(d -> d.getCanId()));
        comparator = comparator.thenComparing(Comparator.comparing(d -> d.getCanInfo().manufacturer.ManufacturerName));
        comparator = comparator.thenComparing(Comparator.comparing(d -> d.hasErrors() ? 0 : 1));

        Collections.sort(devices, comparator);
        return devices;
    }
}
