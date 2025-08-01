package com.transport.test;

import com.transport.analysis.*;
import com.transport.loader.*;
import com.transport.model.geo.Zone;
import com.transport.model.parking.ParkingCharge;
import com.transport.model.parking.ParkingSupply;
import com.transport.model.pt.TransportMatrix;
import com.transport.model.transport.CarOwnership;
import com.transport.visualization.BarChartVisualizer;
import com.transport.visualization.LineChartVisualizer;
import com.transport.visualization.ODHeatmapVisualizer;
import com.transport.visualization.TransportVisualizer;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class TestClass {

    public static void testZoneLoader() {
        try {
            ZoneLoader loader = new ZoneLoader();
            List<Zone> zones = loader.loadZones("C:\\Users\\Lukyanchik_VA\\Desktop\\Parsed\\Motion Zones\\MoTiON_Lookup.xlsx");
            System.out.println("Loaded zones: " + zones.size());
            zones.forEach(z -> {
                if (z.getBorough().equals("Bexley")) {
                    System.out.printf(
                            "Zone %s: Area=%.2f km², Dockland=%s, ULEZ=%s\n",
                            z.getZoneId(), z.getAreaKm2(), z.isDockland(), z.isULEZ()
                    );
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

//    public static void testCarOwnershipLoader() {
//        try {
//            CarOwnershipLoader loader = new CarOwnershipLoader();
//            List<CarOwnership> cars = loader.loadData("C:\\Users\\Lukyanchik_VA\\Desktop\\Parsed\\Car Ownership\\MoTiON 3.1 Car Ownership.xlsx");
//            System.out.println("Loaded car ownership records: " + cars.size());
//
//            // Пример вывода для Bexley
//            cars.stream()
//                    .filter(c -> c.getBorough().equals("Bexley"))
//                    .limit(5)
//                    .forEach(c -> System.out.println(
//                            "Zone " + c.getZoneId() + ": " + c.getCarOwners() + " cars (" + c.getScenario() + ")"
//                    ));
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }

    public static void testParkingChargeLoader() {
        try {
            ParkingChargeLoader loader = new ParkingChargeLoader();
            List<ParkingCharge> charges = loader.loadData("C:\\Users\\Lukyanchik_VA\\Desktop\\Parsed\\Parking\\MoTiON 3.1 Parking Charges.xlsx");
            System.out.println("Loaded parking charges: " + charges.size());

            charges.stream()
                    .filter(p -> p.getBorough().equals("Bexley"))
                    .limit(3)
                    .forEach(p -> System.out.println(
                            "Zone " + p.getZoneId() + ": Other_POS=" + p.getOtherPOS() + "p"
                    ));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void testParkingSupplyLoader() {
        try {
            ParkingSupplyLoader loader = new ParkingSupplyLoader();
            List<ParkingSupply> supplies = loader.loadData("C:\\Users\\Lukyanchik_VA\\Desktop\\Parsed\\Parking\\MoTiON 3.1 Parking Supplies.xlsx");
            System.out.println("Loaded parking supplies: " + supplies.size());

            supplies.stream()
                    .filter(p -> p.getBorough().equals("Bexley"))
                    .limit(3)
                    .forEach(p -> System.out.println(
                            "Zone " + p.getZoneId() + ": Public On-Street=" + p.getPublicOnStreet()
                    ));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void testTransportMatrixLoader() {
        Path path = Paths.get("C:\\Users\\Lukyanchik_VA\\Desktop\\Parsed\\Skims&Demand\\Highway\\2026_Hybrid\\A26hyb05_IP_time_1.csv");
        List<TransportMatrix> matrix = new TransportMatrixLoader()
                .parseMatrixFormat(path, "AM", "Car IWT");

        System.out.println("Total O-D pairs loaded: " + matrix.size());

        // Пример вывода первых 5 записей
        matrix.stream().limit(5).forEach(tm ->
                System.out.printf("%s -> %s: %.2f мин.%n",
                        tm.getOriginZoneId(),
                        tm.getDestinationZoneId(),
                        tm.getValue())
        );

    }

    public static void test() {
        try {
            // Загрузка данных
            ZoneLoader zoneLoader = new ZoneLoader();
            List<Zone> zones = zoneLoader.loadZones("C:\\Users\\Lukyanchik_VA\\Desktop\\Parsed\\Motion Zones\\MoTiON_Lookup.xlsx");

            Path path = Paths.get("C:\\Users\\Lukyanchik_VA\\Desktop\\Parsed\\Skims&Demand\\Highway\\2026_Hybrid\\A26hyb05_IP_time_1.csv");
            TransportMatrixLoader matrixLoader = new TransportMatrixLoader();
            List<TransportMatrix> odMatrix = new TransportMatrixLoader()
                    .parseMatrixFormat(path, "AM", "Car IWT");

            ParkingSupplyLoader parkingLoader = new ParkingSupplyLoader();
            List<ParkingSupply> parking = parkingLoader.loadData("C:\\Users\\Lukyanchik_VA\\Desktop\\Parsed\\Parking\\MoTiON 3.1 Parking Supplies.xlsx");

            // Анализ
            TransportAnalyzer analyzer = new TransportAnalyzer();
            Map<String, Double> parkingDemand = analyzer.calculateParkingDemand(odMatrix, parking);

            // Вывод результатов
            System.out.println("Зоны с наибольшим спросом:");
            parkingDemand.entrySet().stream()
                    .sorted(Map.Entry.<String, Double>comparingByValue().reversed())
                    .limit(10)
                    .forEach(entry -> System.out.printf(
                            "Zone %s: %.2f%n",
                            entry.getKey(),
                            entry.getValue())
                    );

        } catch (Exception e) {
            System.err.println("Ошибка при анализе данных:");
            e.printStackTrace();
        }
    }


//    public static void testing1() {
//        try {
//            // 1. Загрузка данных
//            CarOwnershipLoader loader = new CarOwnershipLoader();
//            List<CarOwnership> data = loader.loadData("C:\\Users\\Lukyanchik_VA\\Desktop\\Parsed\\Car Ownership\\MoTiON 3.1 Car Ownership.xlsx");
//
//            // 2. Анализ
//            CarOwnershipAnalyzer analyzer = new CarOwnershipAnalyzer();
//            Map<String, Double> avgCars = analyzer.getAverageCarsByBorough(data);
//            System.out.println("Average cars per borough: " + avgCars);
//
//            // 3. Визуализация
//            LineChartVisualizer visualizer = new LineChartVisualizer();
//            visualizer.plotCarOwnershipTrend(data, "Bexley");
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }

    public static void testing2() {
        try {
            // 1. Загрузка данных
            CarOwnershipLoader carLoader = new CarOwnershipLoader();
            List<CarOwnership> carData = carLoader.loadData("C:\\Users\\Lukyanchik_VA\\Desktop\\Parsed\\Car Ownership\\MoTiON 3.1 Car Ownership.xlsx");

            ParkingSupplyLoader parkingLoader = new ParkingSupplyLoader();
            List<ParkingSupply> parkingData = parkingLoader.loadData("C:\\Users\\Lukyanchik_VA\\Desktop\\Parsed\\Parking\\MoTiON 3.1 Parking Supplies.xlsx");

            // 2. Анализ
            ParkingAnalyzer parkingAnalyzer = new ParkingAnalyzer();
            Map<String, Integer> publicOnStreet = parkingAnalyzer.getTotalParkingByBorough(parkingData, "PublicOnStreet");

            // 3. Визуализация
            BarChartVisualizer barVisualizer = new BarChartVisualizer();
            barVisualizer.plotParkingByBorough(publicOnStreet, "PublicOnStreet");

            System.out.println("График создан: parking_supply_PublicOnStreet.png");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void testing() {
        try {
            // 1. Загрузка данных
            TransportMatrixLoader matrixLoader = new TransportMatrixLoader();
            List<TransportMatrix> timeSkims = matrixLoader.parseMatrixFormat(
                    Paths.get("C:\\Users\\Lukyanchik_VA\\Desktop\\Parsed\\Skims&Demand\\Highway\\2026_Hybrid\\A26hyb05_IP_time_1.csv"),
                    "AM",
                    "Car"
            );

            ZoneLoader zoneLoader = new ZoneLoader();
            List<Zone> zones = zoneLoader.loadZones("C:\\Users\\Lukyanchik_VA\\Desktop\\Parsed\\Motion Zones\\MoTiON_Lookup.xlsx");
            Map<String, String> zoneToBorough = zones.stream()
                    .collect(Collectors.toMap(Zone::getZoneId, Zone::getBorough));

            // 2. Анализ
            TransportMatrixAnalyzer analyzer = new TransportMatrixAnalyzer();
            Map<String, Double> avgTime = analyzer.getAverageByBoroughPairs(timeSkims, zoneToBorough);
            System.out.println("Среднее время между boroughs: " + avgTime);

            // 3. Визуализация
            ODHeatmapVisualizer visualizer = new ODHeatmapVisualizer();
            visualizer.generateHeatMap(
                    analyzer.filterByThreshold(timeSkims, 10.0),
                    "Time Skims (AM, Car, >10 min)"
            );
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

