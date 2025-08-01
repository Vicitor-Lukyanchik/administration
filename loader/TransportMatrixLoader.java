package com.transport.loader;

import com.opencsv.CSVReader;
import com.transport.model.pt.TransportMatrix;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

@Component
public class TransportMatrixLoader {

    public List<TransportMatrix> parseMatrixFormat(Path filePath, String timePeriod, String userClass) {
        List<TransportMatrix> data = new ArrayList<>();

        try (BufferedReader reader = Files.newBufferedReader(filePath)) {
            // Читаем заголовок с Destination zones
            String header = reader.readLine();
            String[] destinations = header.split(",");

            // Обрабатываем строки с данными
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length < 2) continue;

                String origin = parts[0];
                for (int i = 1; i < parts.length && i <= destinations.length; i++) {
                    TransportMatrix tm = new TransportMatrix();
                    tm.setOriginZoneId(origin);
                    tm.setDestinationZoneId(destinations[i-1]);
                    tm.setValue(Double.parseDouble(parts[i]));
                    tm.setTimePeriod(timePeriod);
                    tm.setUserClass(userClass);
                    data.add(tm);
                }
            }
        } catch (Exception e){
            System.out.println("Ошибка чтения файла:" + filePath);
        }

        return data;
    }
}
