package team1502.configuration.CAN;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;

import team1502.configuration.builders.Builder;
import team1502.configuration.builders.Channel;

public class CanMap {
    private EnumMap<DeviceType,
         EnumMap<Manufacturer,
             HashMap<Integer, Builder>>> deviceMap = new EnumMap<>(DeviceType.class);

    private ArrayList<Builder> _errorParts = new ArrayList<>();
    private ArrayList<String> _errorMessages = new ArrayList<>();

    public CanMap(Builder bus) {
        var devices = Channel.getPartChannels(bus).stream()
            .map(ch -> ch.Connection().Host()).toList();

        for (Builder device : devices) {
            install(device);
        }
    }

    public void install(Builder canDevice) {
        CanInfo canInfo = CanInfo.WrapPart(canDevice);
        if (canInfo.isValid()) {
            if (!deviceMap.containsKey(canInfo.Device())) {
                deviceMap.put(canInfo.Device(), new EnumMap<>(Manufacturer.class));
            }

            var mfrMap = deviceMap.get(canInfo.Device());
            if (!mfrMap.containsKey(canInfo.Manufacturer())) {
                mfrMap.put(canInfo.Manufacturer(), new HashMap<Integer, Builder>());
            }

            var idMap = mfrMap.get(canInfo.Manufacturer());            
            if (idMap.containsKey(canInfo.Number())) {
                var existing =idMap.get(canInfo.Number());
                var msg = MessageFormat.format("CAN ID collision between {2} \"{0}\" and \"{1}\" !", existing.getName(), canDevice.getName(), canInfo.FriendlyName());
                 _errorMessages.add(msg);
                 canDevice.addError(msg);
                 _errorParts.add(canDevice);
            }
            else {
                idMap.put(canInfo.Number(), canDevice);
            }

        } else {

        }
    }

    public List<Builder> getDevices() {
        ArrayList<Builder> devices = new ArrayList<>(_errorParts);
        deviceMap.values().forEach(d -> d.values().forEach(m -> m.values().forEach(id -> devices.add(id))));
        
        Comparator<Builder> comparator = Comparator.comparing(d -> CanInfo.WrapPart(d).Device().DeviceName);
        comparator = comparator.thenComparing(Comparator.comparing(d -> CanInfo.WrapPart(d).Number()));
        comparator = comparator.thenComparing(Comparator.comparing(d -> CanInfo.WrapPart(d).Manufacturer().ManufacturerName));
        comparator = comparator.thenComparing(Comparator.comparing(d -> d.hasErrors() ? 0 : 1));

        Collections.sort(devices, comparator);
        return devices;
    }
}
