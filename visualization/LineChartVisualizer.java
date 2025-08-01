package com.transport.visualization;

import com.transport.model.transport.CarOwnership;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtils;
import org.jfree.chart.JFreeChart;
import org.jfree.data.category.DefaultCategoryDataset;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.List;
import java.util.Map;

@Component
public class LineChartVisualizer {

//    public void plotCarOwnershipTrend(List<CarOwnership> data, String borough) throws Exception {
//        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
//
//        data.stream()
//                .filter(co -> co.getBorough().equals(borough))
//                .forEach(co -> dataset.addValue(
//                        co.getCarOwners(),
//                        "Cars",
//                        String.valueOf(co.getYear()))
//                );
//
//        JFreeChart chart = ChartFactory.createLineChart(
//                "Car Ownership Growth in " + borough + " (2026-2041)",
//                "Year",
//                "Number of Cars",
//                dataset
//        );
//
//        ChartUtils.saveChartAsPNG(
//                new File("car_growth_" + borough + ".png"),
//                chart,
//                800,
//                600
//        );
//    }
}