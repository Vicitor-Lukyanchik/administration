package com.transport.analysis;


import com.transport.model.pt.TransportMatrix;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TransportComparisonAnalyzer {

    // Сравнение времени в пути между авто и общественным транспортом
    public Map<String, Double> compareTravelTimes(
            List<TransportMatrix> carTimes,
            List<TransportMatrix> ptTimes
    ) {
        Map<String, Double> comparison = new HashMap<>();

        carTimes.forEach(car -> {
            ptTimes.stream()
                    .filter(pt -> pt.getOriginZoneId().equals(car.getOriginZoneId())
                            && pt.getDestinationZoneId().equals(car.getDestinationZoneId()))
                    .findFirst()
                    .ifPresent(pt -> {
                        double ratio = pt.getValue() / car.getValue();
                        comparison.put(
                                car.getOriginZoneId() + "->" + car.getDestinationZoneId(),
                                ratio
                        );
                    });
        });

        return comparison;
    }

    // Анализ влияния парковок на выбор транспорта
    public Map<String, String> analyzeTransportChoice(
            Map<String, Double> parkingAvailability,
            Map<String, Double> ptAccessibility
    ) {
        Map<String, String> recommendations = new HashMap<>();

        parkingAvailability.forEach((zoneId, parkingScore) -> {
            double ptScore = ptAccessibility.getOrDefault(zoneId, 0.0);
            recommendations.put(zoneId,
                    (parkingScore > ptScore * 1.5) ? "Увеличить PT" : "Оптимизировать парковки");
        });

        return recommendations;
    }
}