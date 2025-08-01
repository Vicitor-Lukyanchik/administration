package com.transport.analysis;

import com.transport.model.parking.ParkingSupply;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.stream.Collectors;

import com.transport.model.pt.TransportMatrix;
import org.springframework.stereotype.Component;

@Component
public class TransportAnalyzer {
    public Map<String, Double> calculateParkingDemand(
            List<TransportMatrix> odMatrix,
            List<ParkingSupply> parkingSupplies
    ) {
        Map<String, Double> demandByZone = new HashMap<>();

        // Группируем по origin zone
        Map<String, List<TransportMatrix>> tripsByOrigin = odMatrix.stream()
                .collect(Collectors.groupingBy(TransportMatrix::getOriginZoneId));

        // Рассчитываем спрос с учётом парковок
        for (ParkingSupply ps : parkingSupplies) {
            List<TransportMatrix> trips = tripsByOrigin.get(ps.getZoneId());
            if (trips != null) {
                double totalDemand = trips.stream()
                        .mapToDouble(TransportMatrix::getValue)
                        .sum();

                double weightedDemand = totalDemand *
                        (1 + ps.getPublicOnStreet() / 1000.0);

                demandByZone.put(ps.getZoneId(), weightedDemand);
            }
        }

        return demandByZone;
    }
}
