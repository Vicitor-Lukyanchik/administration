package com.transport.analysis;

import com.transport.model.parking.ParkingSupply;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class ParkingAnalyzer {

    // Сумма парковок по типам для каждого borough
    public Map<String, Integer> getTotalParkingByBorough(List<ParkingSupply> data, String parkingType) {
        return data.stream()
                .collect(Collectors.groupingBy(
                        ParkingSupply::getBorough,
                        Collectors.summingInt(ps -> {
                            switch (parkingType) {
                                case "PublicOffStreet": return ps.getPublicOffStreet();
                                case "PublicOnStreet": return ps.getPublicOnStreet();
                                default: return 0;
                            }
                        })
                ));
    }

    // Среднее количество парковок на зону
    public double getAverageParkingPerZone(List<ParkingSupply> data, String borough) {
        return data.stream()
                .filter(ps -> ps.getBorough().equals(borough))
                .mapToInt(ParkingSupply::getPublicOnStreet)
                .average()
                .orElse(0.0);
    }
}