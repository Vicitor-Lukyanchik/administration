package com.transport.loader;

import com.transport.model.pt.PublicTransportRoute;
import org.springframework.stereotype.Component;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Arrays;


@Component
public class PublicTransportLoader {
    public List<PublicTransportRoute> loadRoutes(Path filePath) {
        List<PublicTransportRoute> routes = new ArrayList<>();

        try {
            Files.lines(filePath).skip(1).forEach(line -> {
                String[] parts = line.split(",");
                PublicTransportRoute route = new PublicTransportRoute();
                route.setRouteId(parts[0]);
                route.setTransportType(parts[1]);
                route.setZoneIds(Arrays.asList(parts[2].split(";")));
                route.setAvgFrequency(Double.parseDouble(parts[3]));
                routes.add(route);
            });
        } catch (Exception ignored){
            System.out.println("Файл не найден: " + filePath);
        }
        return routes;
    }
}