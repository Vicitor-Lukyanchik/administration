package com.transport.analysis;

import com.transport.model.pt.TransportMatrix;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class TransportMatrixAnalyzer {

    // Среднее время/спрос между boroughs
    public Map<String, Double> getAverageByBoroughPairs(
            List<TransportMatrix> matrices,
            Map<String, String> zoneToBorough
    ) {
        return matrices.stream()
                .collect(Collectors.groupingBy(
                        tm -> zoneToBorough.get(tm.getOriginZoneId()) + "->" + zoneToBorough.get(tm.getDestinationZoneId()),
                        Collectors.averagingDouble(TransportMatrix::getValue)
                ));
    }

    // Фильтрация по порогу значения (например, спрос > 1.0)
    public List<TransportMatrix> filterByThreshold(List<TransportMatrix> matrices, double threshold) {
        return matrices.stream()
                .filter(tm -> tm.getValue() > threshold)
                .collect(Collectors.toList());
    }
}