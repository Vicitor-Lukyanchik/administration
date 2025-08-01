package com.transport;

import org.geotools.data.shapefile.ShapefileDataStore;
import org.geotools.data.simple.SimpleFeatureIterator;
import org.opengis.feature.simple.SimpleFeature;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.TreeSet;

public class ShapefileZoneViewer {

    public static void main(String[] args) {
        String shpPath = "C:\\Users\\Lukyanchik_VA\\Desktop\\Parsed\\Motion Zones\\MoTiON_Zones\\MoTiON_Zones.shp";
        printAllZoneValues(shpPath, "MoTiON Zon");
    }

    public static void printAllZoneValues(String shpPath, String attributeName) {
        ShapefileDataStore store = null;
        try {
            store = new ShapefileDataStore(new File(shpPath).toURI().toURL());
            store.setCharset(StandardCharsets.UTF_8);

            // Используем TreeSet для автоматической сортировки значений
            TreeSet<Double> uniqueValues = new TreeSet<>();

            try (SimpleFeatureIterator features = store.getFeatureSource().getFeatures().features()) {
                while (features.hasNext()) {
                    SimpleFeature feature = features.next();
                    Object value = feature.getAttribute(attributeName);
                    if (value != null) {
                        uniqueValues.add((Double) value);
                    }
                }
            }

            System.out.println("=== Все уникальные значения атрибута '" + attributeName + "' ===");
            System.out.println("Общее количество: " + uniqueValues.size());
            System.out.println("\nЗначения:");

            // Выводим значения с группировкой по сотням для удобства просмотра
            int currentHundred = -1;
            for (Double value : uniqueValues) {
                int hundred = (int) (value / 100);
                if (hundred != currentHundred) {
                    currentHundred = hundred;
                    System.out.printf("\n=== Группа %d00-%d99 ===\n", hundred, hundred);
                }
                System.out.println(value);
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (store != null) {
                store.dispose();
            }
        }
    }
}