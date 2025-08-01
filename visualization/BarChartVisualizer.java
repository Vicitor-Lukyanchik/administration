package com.transport.visualization;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtils;
import org.jfree.chart.JFreeChart;
import org.jfree.data.category.DefaultCategoryDataset;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.Map;

@Component
public class BarChartVisualizer {

    public void plotParkingByBorough(Map<String, Integer> data, String parkingType) throws Exception {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        data.forEach((borough, count) ->
                dataset.addValue(count, parkingType, borough)
        );

        JFreeChart chart = ChartFactory.createBarChart(
                "Parking Supply by Borough (2026)",
                "Borough",
                "Number of Parking Spots",
                dataset
        );

        ChartUtils.saveChartAsPNG(
                new File("parking_supply_" + parkingType + ".png"),
                chart,
                800,
                600
        );
    }
}
