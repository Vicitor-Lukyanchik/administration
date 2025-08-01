package com.transport.analysis;

import com.transport.model.transport.CarOwnership;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class CarOwnershipAnalyzer {

//    public Map<String, Double> getAverageCarsByBorough(List<CarOwnership> data) {
//        return data.stream().collect(Collectors.groupingBy(
//                        CarOwnership::getBorough,
//                        Collectors.averagingInt(CarOwnership::getCarOwners)
//                ));
//    }
//
//    public Map<Integer, Double> getGrowthByYear(List<CarOwnership> data, String borough) {
//        return data.stream()
//                .filter(co -> co.getBorough().equals(borough))
//                .collect(Collectors.groupingBy(
//                        CarOwnership::getYear,
//                        Collectors.averagingInt(CarOwnership::getCarOwners)
//                ));
//    }
}