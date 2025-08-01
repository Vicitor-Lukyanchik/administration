package com.transport.model.geo;

import lombok.Data;

@Data  // Lombok для геттеров/сеттеров
public class Zone {
    private String zoneId;          // MoTiON Sequential Zone
    private String borough;         // Borough/Area (Bexley, Greenwich...)
    private String boroughCode;     // Код боро (если есть)
    private double areaKm2;         // Area (km²)
    private boolean isDockland;     // Dockland zones (Yes/No)
    private String zoneType;        // Central/Inner/Outer
    private boolean isULEZ;         // Входит ли в зону ULEZ
}
