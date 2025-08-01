package com.transport;

import org.apache.poi.ss.usermodel.*;
import org.geotools.data.shapefile.ShapefileDataStore;
import org.geotools.data.simple.*;
import org.geotools.factory.CommonFactoryFinder;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.map.*;
import org.geotools.styling.*;
import org.geotools.styling.Stroke;
import org.geotools.swing.JMapFrame;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.filter.Filter;
import org.opengis.filter.FilterFactory2;
import org.opengis.filter.expression.PropertyName;

import javax.swing.*;
import java.awt.*;
import java.awt.Color;
import java.io.File;
import java.io.FileInputStream;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.List;

public class ShapefileHighlighter {

    private static final Color HIGHLIGHT_COLOR = new Color(255, 0, 0, 150);
    private static final Color BASE_COLOR = Color.LIGHT_GRAY;
    private static final int HIGHLIGHT_WIDTH = 3;

    public static void main(String[] args) {
        String shpPath = "C:\\Users\\Lukyanchik_VA\\Desktop\\Parsed\\Motion Zones\\MoTiON_Zones\\MoTiON_Zones.shp";
        String excelPath = "C:/Users/Lukyanchik_VA/Desktop/графики/Filtered_2026_Hybrid_Population.xlsx";

        try {
            List<String> zonesToHighlight = readZonesFromExcel(excelPath);
            System.out.println("Найдено зон в Excel: " + zonesToHighlight.size());

            highlightZones(shpPath, zonesToHighlight);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null,
                    "Ошибка: " + e.getMessage(), "Ошибка", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    public static List<String> readZonesFromExcel(String filePath) throws Exception {
        List<String> zones = new ArrayList<>();
        try (FileInputStream fis = new FileInputStream(filePath);
             Workbook workbook = WorkbookFactory.create(fis)) {

            Sheet sheet = workbook.getSheetAt(0);
            DataFormatter dataFormatter = new DataFormatter();

            for (Row row : sheet) {
                Cell cell = row.getCell(0);
                if (cell != null) {
                    String cellValue = dataFormatter.formatCellValue(cell).trim();
                    if (!cellValue.isEmpty() && !cellValue.equalsIgnoreCase("zoneID")) {
                        zones.add(normalizeZoneId(cellValue));
                    }
                }
            }
        }
        return zones;
    }

    private static String normalizeZoneId(String zoneId) {
        // Удаляем все нецифровые символы и ведущие нули
        return zoneId.replaceAll("[^\\d]", "").replaceFirst("^0+", "");
    }

    public static void highlightZones(String shpPath, List<String> zonesToHighlight) throws Exception {
        ShapefileDataStore store = null;
        try {
            store = new ShapefileDataStore(new File(shpPath).toURI().toURL());
            store.setCharset(StandardCharsets.UTF_8);
            SimpleFeatureSource featureSource = store.getFeatureSource();

            String zoneAttribute = "MoTiON Zon"; // Явно указываем нужный атрибут
            System.out.println("Используется атрибут для зон: " + zoneAttribute);

            // Выводим примеры значений для диагностики
            printSampleValues(featureSource, zoneAttribute, 20);

            // Создаем расширенный фильтр
            FilterFactory2 ff = CommonFactoryFinder.getFilterFactory2();
            Filter filter = createEnhancedDoubleFilter(ff, zoneAttribute, zonesToHighlight);

            // Применяем фильтр
            SimpleFeatureCollection highlightedFeatures = featureSource.getFeatures(filter);
            System.out.println("Найдено объектов для выделения: " + highlightedFeatures.size());

            if (highlightedFeatures.size() == 0) {
                checkForPotentialMatches(featureSource, zoneAttribute, zonesToHighlight);
                throw new RuntimeException("Не найдено совпадений. Проверьте вывод выше для диагностики.");
            }

            // Визуализация
            visualizeResults(featureSource, highlightedFeatures, zoneAttribute);

        } finally {
            if (store != null) {
                store.dispose();
            }
        }
    }

    private static Filter createEnhancedDoubleFilter(FilterFactory2 ff, String attributeName, List<String> zones) {
        List<Filter> filters = new ArrayList<>();
        PropertyName property = ff.property(attributeName);

        for (String zone : zones) {
            try {
                // Преобразуем в double и сравниваем
                double zoneValue = Double.parseDouble(zone);
                filters.add(ff.equals(property, ff.literal(zoneValue)));

                // Добавляем вариант с .0 на случай разных форматов хранения
                filters.add(ff.equals(property, ff.literal(zone + ".0")));

            } catch (NumberFormatException e) {
                System.out.println("Невозможно преобразовать зону в число: " + zone);
            }
        }

        System.out.println("Создано условий фильтрации: " + filters.size());
        return filters.isEmpty() ? Filter.EXCLUDE : ff.or(filters);
    }

    private static void printSampleValues(SimpleFeatureSource source, String attributeName, int count) throws Exception {
        System.out.println("\nПримеры значений '" + attributeName + "' в Shapefile:");
        try (var features = source.getFeatures().features()) {
            for (int i = 0; i < count && features.hasNext(); i++) {
                SimpleFeature feature = features.next();
                Object value = feature.getAttribute(attributeName);
                System.out.printf("- %s (тип: %s)%n",
                        value != null ? value.toString() : "null",
                        value != null ? value.getClass().getSimpleName() : "null");
            }
        }
    }

    private static void checkForPotentialMatches(SimpleFeatureSource source, String attributeName, List<String> zones) throws Exception {
        System.out.println("\nПроверка возможных совпадений...");

        Set<Double> shapeValues = new HashSet<>();
        try (var features = source.getFeatures().features()) {
            while (features.hasNext()) {
                Object value = features.next().getAttribute(attributeName);
                if (value != null) {
                    shapeValues.add(((Number)value).doubleValue());
                }
            }
        }

        System.out.println("Всего уникальных значений в Shapefile: " + shapeValues.size());
        System.out.println("Ищем совпадения с " + zones.size() + " зонами из Excel...");

        int found = 0;
        for (String zone : zones) {
            try {
                double zoneValue = Double.parseDouble(zone);
                if (shapeValues.contains(zoneValue)) {
                    System.out.printf("Найдено совпадение: %s (как число %.1f)%n", zone, zoneValue);
                    found++;
                }
            } catch (NumberFormatException e) {
                System.out.println("Пропущена нечисловая зона: " + zone);
            }
        }

        System.out.println("Всего найдено совпадений: " + found);

        if (found == 0) {
            System.out.println("\nРекомендации:");
            System.out.println("1. Убедитесь, что номера зон в Excel соответствуют значениям в Shapefile");
            System.out.println("2. Проверьте правильность выбранного атрибута для зон");
            System.out.println("3. Сравните первые 10 значений:");

            System.out.println("\nПервые 10 зон из Excel:");
            zones.stream().limit(10).forEach(System.out::println);

            System.out.println("\nПервые 10 зон из Shapefile:");
            shapeValues.stream().sorted().limit(10).forEach(System.out::println);
        }
    }

    private static void visualizeResults(SimpleFeatureSource source,
                                         SimpleFeatureCollection highlightedFeatures,
                                         String zoneAttribute) throws Exception {
        MapContent map = new MapContent();
        map.setTitle("Выделение зон (найдено: " + highlightedFeatures.size() + ")");

        // Основной слой (все зоны)
        Style baseStyle = createStyle(BASE_COLOR, Color.DARK_GRAY, 1);
        Layer baseLayer = new FeatureLayer(source.getFeatures(), baseStyle);
        map.addLayer(baseLayer);

        // Слой с выделенными зонами
        Style highlightStyle = createStyle(HIGHLIGHT_COLOR, Color.RED, HIGHLIGHT_WIDTH);
        Layer highlightLayer = new FeatureLayer(highlightedFeatures, highlightStyle);
        map.addLayer(highlightLayer);

        // Настройка отображения
        JMapFrame frame = new JMapFrame(map);
        frame.setSize(1200, 800);
        frame.enableToolBar(true);
        frame.enableStatusBar(true);

        // Центрирование карты
        ReferencedEnvelope env = highlightedFeatures.size() > 0 ?
                highlightedFeatures.getBounds() : source.getBounds();
        frame.getMapPane().setDisplayArea(env);

        frame.setVisible(true);
    }

    private static Style createStyle(Color fillColor, Color strokeColor, int strokeWidth) {
        StyleFactory sf = CommonFactoryFinder.getStyleFactory();
        FilterFactory2 ff = CommonFactoryFinder.getFilterFactory2();

        Fill fill = sf.createFill(ff.literal(fillColor), ff.literal(0.5));
        Stroke stroke = sf.createStroke(ff.literal(strokeColor), ff.literal(strokeWidth));
        PolygonSymbolizer sym = sf.createPolygonSymbolizer(stroke, fill, null);

        Rule rule = sf.createRule();
        rule.symbolizers().add(sym);

        FeatureTypeStyle fts = sf.createFeatureTypeStyle();
        fts.rules().add(rule);

        Style style = sf.createStyle();
        style.featureTypeStyles().add(fts);

        return style;
    }
}