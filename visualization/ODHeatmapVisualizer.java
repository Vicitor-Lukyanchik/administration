package com.transport.visualization;

import com.transport.model.pt.TransportMatrix;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtils;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.CategoryLabelPositions;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.data.category.DefaultCategoryDataset;
import org.springframework.stereotype.Component;

import java.awt.*;
import java.io.File;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
public class ODHeatmapVisualizer {
    public void generateHeatMap(List<TransportMatrix> matrices, String title) throws Exception {
        // 1. Получаем уникальные зоны
        List<String> zones = matrices.stream()
                .flatMap(tm -> Stream.of(tm.getOriginZoneId(), tm.getDestinationZoneId()))
                .distinct()
                .sorted()
                .collect(Collectors.toList());

        // 2. Создаем dataset
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();

        // Заполняем данные
        for (String origin : zones) {
            for (String destination : zones) {
                double value = matrices.stream()
                        .filter(tm -> tm.getOriginZoneId().equals(origin)
                                && tm.getDestinationZoneId().equals(destination))
                        .findFirst()
                        .map(TransportMatrix::getValue)
                        .orElse(0.0);
                dataset.addValue(value, origin, destination);
            }
        }

        // 3. Создаем столбчатую диаграмму (как основу)
        JFreeChart chart = ChartFactory.createBarChart(
                title,
                "Destination Zone",
                "Value",
                dataset
        );

        // 4. Настраиваем внешний вид как тепловую карту
        CategoryPlot plot = chart.getCategoryPlot();
        BarRenderer renderer = (BarRenderer) plot.getRenderer();

        // Устанавливаем цветовую шкалу
        renderer.setSeriesPaint(0, new GradientPaint(
                0.0f, 0.0f, Color.BLUE,
                0.0f, 0.0f, Color.RED
        ));

        // Настраиваем оси
        CategoryAxis domainAxis = plot.getDomainAxis();
        domainAxis.setCategoryLabelPositions(CategoryLabelPositions.UP_90);
        domainAxis.setLowerMargin(0.02);
        domainAxis.setUpperMargin(0.02);

        // 5. Сохраняем
        ChartUtils.saveChartAsPNG(
                new File("od_heatmap.png"),
                chart,
                1200,
                800
        );
    }
}
