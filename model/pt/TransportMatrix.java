package com.transport.model.pt;

import lombok.Data;

@Data
public class TransportMatrix {
    private String originZoneId;        // Зона отправления (например, "Z1001")
    private String destinationZoneId;   // Зона назначения ("Z2001")
    private double value;               // Значение (время в минутах или спрос)
    private String timePeriod;          // Период: "AM", "IP", "PM"
    private String userClass;           // Класс пользователя: "Car", "PT", etc.
}