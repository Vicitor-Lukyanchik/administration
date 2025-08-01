package com.transport.model.pt;

import lombok.Data;

import java.util.List;

@Data
public class PublicTransportRoute {
    private String routeId;
    private String transportType; // "Bus", "Rail"
    private List<String> zoneIds; // Зоны через которые проходит маршрут
    private double avgFrequency; // Рейсов в час
}