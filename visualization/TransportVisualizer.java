package com.transport.visualization;

import org.jfree.chart.*;
import org.jfree.data.category.DefaultCategoryDataset;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.util.Map;

@Component
public class TransportVisualizer {

    public void createTravelTimeChart(
            Map<String, Double> comparisonData,
            String filePath
    ) throws IOException {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();

        comparisonData.forEach((route, ratio) -> {
            dataset.addValue(ratio, "Время PT/Авто", route);
        });

        JFreeChart chart = ChartFactory.createBarChart(
                "Сравнение времени поездки",
                "Маршрут",
                "Отношение PT/Авто",
                dataset
        );

        ChartUtils.saveChartAsPNG(new File(filePath), chart, 800, 600);
    }
}