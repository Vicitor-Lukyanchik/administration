package com.transport.analysis;

import com.transport.model.parking.ParkingCharge;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.usermodel.Font;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtils;
import org.jfree.chart.JFreeChart;

import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.CategoryLabelPositions;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.NumberTickUnit;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.renderer.category.CategoryItemRenderer;
import org.jfree.chart.renderer.category.GroupedStackedBarRenderer;
import org.jfree.chart.renderer.category.LineAndShapeRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.chart.title.LegendTitle;
import org.jfree.chart.ui.RectangleEdge;
import org.jfree.chart.util.SortOrder;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import java.awt.*;
import java.awt.Color;
import java.awt.Shape;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.FieldPosition;
import java.text.NumberFormat;
import java.text.ParsePosition;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

public class ParkingAnalysis {

    private static final List<String> TARGET_BOROUGHS = List.of("Bexley", "Bromley", "Greenwich", "Lewisham");

    public static void main(String[] args) {
        try {
            // 1. Анализируем файл с зонами
            Map<String, String> zoneToBorough = analyzeZonesFile(
                    "C:\\Users\\Lukyanchik_VA\\Desktop\\Parsed\\Motion Zones\\MoTiON_Lookup.xlsx"
            );

            // 2. Загружаем данные о парковках
            List<ParkingCharge> charges = loadParkingData(
                    "C:\\Users\\Lukyanchik_VA\\Desktop\\Parsed\\Parking\\MoTiON 3.1 Parking Charges.xlsx",
                    zoneToBorough
            );

            // 3. Генерация графиков
            //  plotOSChargesByZoneAndModel(charges, "Average Parking Charges by Borough", "Стоимость парковки по зонам и тип OS.png");
            //  plotPOSChargesByZoneAndModel(charges, "Average Parking Charges by Borough", "Стоимость парковки по зонам и тип POS.png");
            //  plotComparisonChart(charges, "Commute vs Other Parking Charges", "Разница стоимости парковки между поездками на работу и другими.png");
            //  plotYearlyTrends(charges, "Parking Charges Trend by Year", "yearly_trends.png");

            exportZonesToExcelByModel(charges, "parking_zones_full_report.xlsx");
            System.out.println("Анализ завершен. Графики сохранены в рабочую директорию.");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    public static void exportZonesToExcelByModel(List<ParkingCharge> charges, String filename) throws IOException {
        // Фильтрация данных для нужных районов
        List<ParkingCharge> filteredCharges = charges.stream()
                .filter(pc -> pc != null)
                .filter(pc -> pc.getBorough() != null &&
                        (pc.getBorough().equalsIgnoreCase("Bexley") ||
                                pc.getBorough().equalsIgnoreCase("Bromley") ||
                                pc.getBorough().equalsIgnoreCase("Greenwich") ||
                                pc.getBorough().equalsIgnoreCase("Lewisham")))
                .filter(pc -> pc.getZoneId() != null && !pc.getZoneId().isEmpty())
                .filter(pc -> pc.getModel() != null &&
                        (pc.getModel().equals("Parking_Charges_2016") ||
                                pc.getModel().equals("parking_charges_2026") ||
                                pc.getModel().equals("parking_charges_2031") ||
                                pc.getModel().equals("Parking_Charges_2041")))
                .sorted(Comparator.comparing(ParkingCharge::getBorough)
                        .thenComparing(ParkingCharge::getZoneId))
                .collect(Collectors.toList());

        if (filteredCharges.isEmpty()) {
            throw new IllegalArgumentException("Нет данных для указанных районов и моделей");
        }

        // Группировка по моделям
        Map<String, List<ParkingCharge>> chargesByModel = filteredCharges.stream()
                .collect(Collectors.groupingBy(ParkingCharge::getModel));

        // Создаем новую Excel-книгу
        try (Workbook workbook = new XSSFWorkbook()) {
            // Создаем стили один раз для всей книги
            CellStyle headerStyle = createHeaderStyle(workbook);
            CellStyle dataStyle = createDataStyle(workbook);

            // Создаем лист для каждой модели
            for (Map.Entry<String, List<ParkingCharge>> entry : chargesByModel.entrySet()) {
                String model = entry.getKey();
                List<ParkingCharge> modelCharges = entry.getValue();

                // Создаем лист с именем модели (обрезаем до 31 символа)
                String sheetName = model.replace("_", " ");
                if (sheetName.length() > 31) {
                    sheetName = sheetName.substring(0, 31);
                }
                Sheet sheet = workbook.createSheet(sheetName);

                // Создаем заголовки
                createHeaders(sheet, headerStyle);

                // Заполняем данные
                fillData(sheet, modelCharges, dataStyle);
            }

            // Записываем файл
            try (FileOutputStream outputStream = new FileOutputStream(filename)) {
                workbook.write(outputStream);
            }
        }
    }

    // Создание стиля для заголовков
    private static CellStyle createHeaderStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        font.setColor(IndexedColors.WHITE.getIndex());
        style.setFont(font);
        style.setFillForegroundColor(IndexedColors.DARK_BLUE.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        style.setAlignment(HorizontalAlignment.CENTER);
        return style;
    }

    // Создание стиля для данных
    private static CellStyle createDataStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        style.setDataFormat(workbook.createDataFormat().getFormat("#,##0.00"));
        return style;
    }

    // Создание строки заголовков
    private static void createHeaders(Sheet sheet, CellStyle headerStyle) {
        Row headerRow = sheet.createRow(0);
        String[] headers = {
                "Район", "ID зоны", "Модель",
                "Comm POS", "Comm PNR", "Comm OS",
                "Other POS", "Other PNR", "Other OS"
        };

        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerStyle);
        }
    }

    // Заполнение данных
    private static void fillData(Sheet sheet, List<ParkingCharge> charges, CellStyle dataStyle) {
        int rowNum = 1;
        for (ParkingCharge pc : charges) {
            Row row = sheet.createRow(rowNum++);

            // Текстовые значения
            createCell(row, 0, pc.getBorough(), dataStyle);
            createCell(row, 1, pc.getZoneId(), dataStyle);
            createCell(row, 2, pc.getModel(), dataStyle);

            // Числовые значения
            createCell(row, 3, pc.getCommPOS(), dataStyle);
            createCell(row, 4, pc.getCommPNR(), dataStyle);
            createCell(row, 5, pc.getCommOS(), dataStyle);
            createCell(row, 6, pc.getOtherPOS(), dataStyle);
            createCell(row, 7, pc.getOtherPNR(), dataStyle);
            createCell(row, 8, pc.getOtherOS(), dataStyle);
        }

        // Автонастройка ширины столбцов
        for (int i = 0; i < 9; i++) {
            sheet.autoSizeColumn(i);
        }
    }

    // Вспомогательный метод для создания ячеек
    private static void createCell(Row row, int column, String value, CellStyle style) {
        Cell cell = row.createCell(column);
        cell.setCellValue(value);
        cell.setCellStyle(style);
    }

    private static void createCell(Row row, int column, double value, CellStyle style) {
        Cell cell = row.createCell(column);
        cell.setCellValue(value);
        cell.setCellStyle(style);
    }

    public static Map<String, String> analyzeZonesFile(String filePath) throws Exception {
        Map<String, String> zoneToBorough = new HashMap<>();
        Workbook workbook = WorkbookFactory.create(new File(filePath));
        Sheet sheet = workbook.getSheet("MoTiON_OtherLookups");

        for (Row row : sheet) {
            if (row.getRowNum() == 0) continue; // Пропускаем заголовок

            String zoneId = getCellStringValue(row.getCell(0)); // Колонка A - MoTiON Sequential Zone
            String borough = getCellStringValue(row.getCell(2)); // Колонка C - Borough/Area

            if (TARGET_BOROUGHS.contains(borough)) {
                zoneToBorough.put(zoneId, borough);
            }
        }
        workbook.close();

        System.out.println("Найдено зон в целевых районах: " + zoneToBorough.size());

        return zoneToBorough;
    }

    public static List<ParkingCharge> loadParkingData(String filePath, Map<String, String> zoneToBorough) throws Exception {
        List<ParkingCharge> charges = new ArrayList<>();
        Workbook workbook = WorkbookFactory.create(new File(filePath));

        for (Sheet sheet : workbook) {
            String model = sheet.getSheetName(); // Получаем название листа (модель)

            for (Row row : sheet) {
                if (row.getRowNum() < 1) continue; // Пропускаем заголовок

                String zoneId = getCellStringValue(row.getCell(0)); // Zn
                // Пропускаем зоны не из целевых районов
                if (!zoneToBorough.containsKey(zoneId)) continue;

                ParkingCharge pc = new ParkingCharge();
                pc.setZoneId(zoneId);
                pc.setBorough(zoneToBorough.get(zoneId));
                pc.setModel(model);

                // Парсим данные согласно структуре файла
                pc.setOtherPOS(getCellNumericValue(row.getCell(2)));  // Other_POS
                pc.setOtherPNR(getCellNumericValue(row.getCell(3))); // Other_PNR
                pc.setOtherOS(getCellNumericValue(row.getCell(5)));  // Other_OS
                pc.setCommPOS(getCellNumericValue(row.getCell(6)));  // Comm_POS
                pc.setCommPNR(getCellNumericValue(row.getCell(7))); // Comm_PNR
                pc.setCommOS(getCellNumericValue(row.getCell(9)));  // Comm_OS

                charges.add(pc);
            }
        }
        workbook.close();

        System.out.println("Загружено записей о парковках: " + charges.size());
        return charges;
    }

    private static String getCellStringValue(Cell cell) {
        if (cell == null) return "";

        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue();
            case NUMERIC:
                return String.valueOf((int) cell.getNumericCellValue());
            case FORMULA:
                // Обрабатываем ячейки с формулами
                switch (cell.getCachedFormulaResultType()) {
                    case STRING:
                        return cell.getStringCellValue();
                    case NUMERIC:
                        return String.valueOf((int) cell.getNumericCellValue());
                    default:
                        try {
                            // Пытаемся получить raw value
                            return cell.getRichStringCellValue().getString();
                        } catch (Exception e) {
                            return "";
                        }
                }
            default:
                return "";
        }
    }

    private static double getCellNumericValue(Cell cell) {
        if (cell == null) return 0.0;
        switch (cell.getCellType()) {
            case NUMERIC: return cell.getNumericCellValue();
            case STRING: return Double.parseDouble(cell.getStringCellValue().replace(",", "."));
            default: return 0.0;
        }
    }

    public static void plotOSChargesByZoneAndModel(List<ParkingCharge> charges, String title, String filename) throws IOException {
        // 1. Фильтрация данных
        List<ParkingCharge> filteredCharges = charges.stream()
                .filter(pc -> pc != null)
                .filter(pc -> pc.getModel() != null &&
                        (pc.getModel().equals("Parking_Charges_2016") ||
                                pc.getModel().equals("parking_charges_2026") ||
                                pc.getModel().equals("parking_charges_2031") ||
                                pc.getModel().equals("Parking_Charges_2041")))
                .filter(pc -> pc.getBorough() != null &&
                        (pc.getBorough().equals("Lewisham") ||
                                pc.getBorough().equals("Bexley") ||
                                pc.getBorough().equals("Bromley") ||
                                pc.getBorough().equals("Greenwich")))
                .filter(pc -> pc.getCommOS() >= 0)
                .filter(pc -> pc.getZoneId() != null && !pc.getZoneId().isEmpty())
                .collect(Collectors.toList());

        if (filteredCharges.isEmpty()) {
            throw new IllegalArgumentException("Нет данных для указанных моделей и районов");
        }

        // 2. Получаем упорядоченный список зон
        List<String> uniqueZones = filteredCharges.stream()
                .map(ParkingCharge::getZoneId)
                .distinct()
                .sorted()
                .collect(Collectors.toList());

        // 3. Настройки визуализации
        Map<String, Color> boroughColors = Map.of(
                "Lewisham", new Color(31, 119, 180),
                "Bexley", new Color(255, 127, 14),
                "Bromley", new Color(44, 160, 44),
                "Greenwich", new Color(214, 39, 40)
        );

        Map<String, Shape> modelShapes = Map.of(
                "Parking_Charges_2016", new Ellipse2D.Double(-4, -4, 8, 8),
                "parking_charges_2026", new Rectangle2D.Double(-4, -4, 8, 8),
                "parking_charges_2031", new Polygon(new int[]{0, -4, 4}, new int[]{-4, 4, 4}, 3),
                "Parking_Charges_2041", new Polygon(new int[]{0, -4, 0, 4}, new int[]{-4, 0, 4, 0}, 4)
        );

        // 4. Создаем dataset
        XYSeriesCollection dataset = new XYSeriesCollection();

        for (String borough : boroughColors.keySet()) {
            for (String model : modelShapes.keySet()) {
                String seriesKey = borough + " (" + model.replace("_", " ") + ")";
                XYSeries series = new XYSeries(seriesKey);

                filteredCharges.stream()
                        .filter(pc -> pc.getBorough().equals(borough) && pc.getModel().equals(model))
                        .forEach(pc -> {
                            double xValue = uniqueZones.indexOf(pc.getZoneId());
                            series.add(xValue, pc.getCommOS());
                        });

                if (series.getItemCount() > 0) {
                    dataset.addSeries(series);
                }
            }
        }

        // 5. Создаем scatter plot
        JFreeChart chart = ChartFactory.createScatterPlot(
                title + "\nСтоимость парковки по зонам (OS)",
                "Зоны парковки",
                "Стоимость (пенсы)",
                dataset,
                PlotOrientation.VERTICAL,
                true,
                true,
                false
        );

        // 6. Настраиваем рендерер
        XYPlot plot = chart.getXYPlot();
        XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer(false, true) {
            @Override
            public Paint getItemPaint(int series, int item) {
                String seriesName = (String) dataset.getSeriesKey(series);
                String borough = seriesName.substring(0, seriesName.indexOf(" ("));
                return boroughColors.get(borough);
            }

            @Override
            public Shape getItemShape(int series, int item) {
                String seriesName = (String) dataset.getSeriesKey(series);
                String model = seriesName.substring(seriesName.indexOf("(") + 1, seriesName.indexOf(")"));
                return modelShapes.get(model.replace(" ", "_"));
            }
        };

        plot.setRenderer(renderer);
        plot.setBackgroundPaint(Color.WHITE);
        plot.setDomainGridlinePaint(Color.LIGHT_GRAY);
        plot.setRangeGridlinePaint(Color.LIGHT_GRAY);

        // 7. Настраиваем оси
        NumberAxis domainAxis = (NumberAxis) plot.getDomainAxis();
        domainAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
        domainAxis.setRange(-0.5, uniqueZones.size() - 0.5);
      //  domainAxis.setTickLabelFont(new Font("SansSerif", Font.PLAIN, 8));
        domainAxis.setVerticalTickLabels(true);

        // Полная реализация NumberFormat для отображения реальных названий зон
        domainAxis.setNumberFormatOverride(new NumberFormat() {
            @Override
            public StringBuffer format(double number, StringBuffer toAppendTo, FieldPosition pos) {
                int index = (int) Math.round(number);
                if (index >= 0 && index < uniqueZones.size()) {
                    return toAppendTo.append(uniqueZones.get(index));
                }
                return toAppendTo.append("");
            }

            @Override
            public Number parse(String source, ParsePosition parsePosition) {
                return null; // Не требуется для визуализации
            }

            @Override
            public StringBuffer format(long number, StringBuffer toAppendTo, FieldPosition pos) {
                return format((double)number, toAppendTo, pos);
            }

            @Override
            public StringBuffer format(Object number, StringBuffer toAppendTo, FieldPosition pos) {
                return format(((Number)number).doubleValue(), toAppendTo, pos);
            }
        });

        NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
        rangeAxis.setLowerBound(0);
        double maxValue = filteredCharges.stream()
                .mapToDouble(ParkingCharge::getCommOS)
                .max()
                .orElse(1000);
        rangeAxis.setUpperBound(maxValue * 1.1);

        // 8. Настраиваем легенду
        LegendTitle legend = chart.getLegend();
        legend.setPosition(RectangleEdge.RIGHT);
      //  legend.setItemFont(new Font("SansSerif", Font.PLAIN, 9));

        // 9. Сохраняем график
        ChartUtils.saveChartAsPNG(new File(filename), chart, 2200, 1200);
    }

    public static void plotPOSChargesByZoneForMultipleBoroughs(List<ParkingCharge> charges, String title, String filename) throws IOException {
        // 1. Фильтрация данных для 4 районов
        List<ParkingCharge> filteredCharges = charges.stream()
                .filter(pc -> pc.getModel() != null && pc.getModel().equals("parking_charges_2026"))
                .filter(pc -> pc.getBorough() != null &&
                        (pc.getBorough().equals("Lewisham") ||
                                pc.getBorough().equals("Bexley") ||
                                pc.getBorough().equals("Bromley") ||
                                pc.getBorough().equals("Greenwich")))
                .filter(pc -> pc.getCommPOS() >= 0)
                .collect(Collectors.toList());

        if (filteredCharges.isEmpty()) {
            throw new IllegalArgumentException("Нет данных для указанных районов в модели parking_charges_2026");
        }

        // 2. Создание набора данных с группировкой по районам
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        filteredCharges.forEach(pc -> {
            dataset.addValue(pc.getCommPOS(), pc.getBorough(), pc.getZoneId());
        });

        // 3. Создание графика
        JFreeChart chart = ChartFactory.createBarChart(
                title + " (Сравнение районов, 2026, POS)",
                "Зоны парковки",
                "Стоимость (пенсы)",
                dataset,
                PlotOrientation.VERTICAL,
                true,
                true,
                false
        );

        // 4. Настройка внешнего вида
        CategoryPlot plot = chart.getCategoryPlot();
        plot.setBackgroundPaint(Color.WHITE);

        // Настройка вертикальной оси
        NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
        rangeAxis.setLowerBound(0);
        double maxValue = filteredCharges.stream()
                .mapToDouble(ParkingCharge::getCommPOS)
                .max()
                .orElse(0);
        rangeAxis.setUpperBound(maxValue * 1.1);

        // 5. Настройка цветов для каждого района
        CategoryItemRenderer renderer = plot.getRenderer();
        renderer.setSeriesPaint(0, new Color(31, 119, 180));   // Lewisham - синий
        renderer.setSeriesPaint(1, new Color(255, 127, 14));   // Bexley - оранжевый
        renderer.setSeriesPaint(2, new Color(44, 160, 44));    // Bromley - зеленый
        renderer.setSeriesPaint(3, new Color(214, 39, 40));    // Greenwich - красный

        // 6. Настройка отображения подписей и группировки столбцов
        plot.getDomainAxis().setCategoryMargin(0.2);
        plot.getDomainAxis().setLowerMargin(0.02);
        plot.getDomainAxis().setUpperMargin(0.02);

        // Настройка отступов между группами столбцов
        plot.setRenderer(new GroupedStackedBarRenderer());
        plot.setColumnRenderingOrder(SortOrder.ASCENDING);

        // 7. Добавление легенды для районов
        chart.getLegend().setPosition(RectangleEdge.RIGHT);

        // 8. Сохранение
        ChartUtils.saveChartAsPNG(new File(filename), chart, 1400, 800); // Увеличил размер изображения
    }

public static void plotPOSChargesByZoneAndModel(List<ParkingCharge> charges, String title, String filename) throws IOException {
        // 1. Фильтрация данных для POS
        List<ParkingCharge> filteredCharges = charges.stream()
                .filter(pc -> pc != null)
                .filter(pc -> pc.getModel() != null &&
                        (pc.getModel().equals("Parking_Charges_2016") ||
                                pc.getModel().equals("parking_charges_2026") ||
                                pc.getModel().equals("parking_charges_2031") ||
                                pc.getModel().equals("Parking_Charges_2041")))
                .filter(pc -> pc.getBorough() != null &&
                        (pc.getBorough().equals("Lewisham") ||
                                pc.getBorough().equals("Bexley") ||
                                pc.getBorough().equals("Bromley") ||
                                pc.getBorough().equals("Greenwich")))
                .filter(pc -> pc.getCommPOS() >= 0)  // Используем POS вместо OS
                .filter(pc -> pc.getZoneId() != null && !pc.getZoneId().isEmpty())
                .collect(Collectors.toList());

        if (filteredCharges.isEmpty()) {
            throw new IllegalArgumentException("Нет POS данных для указанных моделей и районов");
        }

        // 2. Получаем упорядоченный список зон
        List<String> uniqueZones = filteredCharges.stream()
                .map(ParkingCharge::getZoneId)
                .distinct()
                .sorted()
                .collect(Collectors.toList());

        // 3. Настройки визуализации (аналогичные)
        Map<String, Color> boroughColors = Map.of(
                "Lewisham", new Color(31, 119, 180),
                "Bexley", new Color(255, 127, 14),
                "Bromley", new Color(44, 160, 44),
                "Greenwich", new Color(214, 39, 40)
        );

        Map<String, Shape> modelShapes = Map.of(
                "Parking_Charges_2016", new Ellipse2D.Double(-4, -4, 8, 8),
                "parking_charges_2026", new Rectangle2D.Double(-4, -4, 8, 8),
                "parking_charges_2031", new Polygon(new int[]{0, -4, 4}, new int[]{-4, 4, 4}, 3),
                "Parking_Charges_2041", new Polygon(new int[]{0, -4, 0, 4}, new int[]{-4, 0, 4, 0}, 4)
        );

        // 4. Создаем dataset для POS
        XYSeriesCollection dataset = new XYSeriesCollection();

        for (String borough : boroughColors.keySet()) {
            for (String model : modelShapes.keySet()) {
                String seriesKey = borough + " (" + model.replace("_", " ") + ")";
                XYSeries series = new XYSeries(seriesKey);

                filteredCharges.stream()
                        .filter(pc -> pc.getBorough().equals(borough) && pc.getModel().equals(model))
                        .forEach(pc -> {
                            double xValue = uniqueZones.indexOf(pc.getZoneId());
                            series.add(xValue, pc.getCommPOS());  // Используем POS данные
                        });

                if (series.getItemCount() > 0) {
                    dataset.addSeries(series);
                }
            }
        }

        // 5. Создаем scatter plot для POS
        JFreeChart chart = ChartFactory.createScatterPlot(
                title + "\nСтоимость парковки по зонам (POS)",  // Указываем POS в заголовке
                "Зоны парковки",
                "Стоимость (пенсы)",
                dataset,
                PlotOrientation.VERTICAL,
                true,
                true,
                false
        );

        // 6. Настраиваем рендерер (аналогично)
        XYPlot plot = chart.getXYPlot();
        XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer(false, true) {
            @Override
            public Paint getItemPaint(int series, int item) {
                String seriesName = (String) dataset.getSeriesKey(series);
                String borough = seriesName.substring(0, seriesName.indexOf(" ("));
                return boroughColors.get(borough);
            }

            @Override
            public Shape getItemShape(int series, int item) {
                String seriesName = (String) dataset.getSeriesKey(series);
                String model = seriesName.substring(seriesName.indexOf("(") + 1, seriesName.indexOf(")"));
                return modelShapes.get(model.replace(" ", "_"));
            }
        };

        plot.setRenderer(renderer);
        plot.setBackgroundPaint(Color.WHITE);
        plot.setDomainGridlinePaint(Color.LIGHT_GRAY);
        plot.setRangeGridlinePaint(Color.LIGHT_GRAY);

        // 7. Настраиваем оси (аналогично)
        NumberAxis domainAxis = (NumberAxis) plot.getDomainAxis();
        domainAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
        domainAxis.setRange(-0.5, uniqueZones.size() - 0.5);
   //     domainAxis.setTickLabelFont(new Font("SansSerif", Font.PLAIN, 8));
        domainAxis.setVerticalTickLabels(true);

        // Форматирование названий зон
        domainAxis.setNumberFormatOverride(new NumberFormat() {
            @Override
            public StringBuffer format(double number, StringBuffer toAppendTo, FieldPosition pos) {
                int index = (int) Math.round(number);
                if (index >= 0 && index < uniqueZones.size()) {
                    return toAppendTo.append(uniqueZones.get(index));
                }
                return toAppendTo.append("");
            }

            @Override public Number parse(String source, ParsePosition parsePosition) { return null; }
            @Override public StringBuffer format(long number, StringBuffer toAppendTo, FieldPosition pos) {
                return format((double)number, toAppendTo, pos);
            }
            @Override public StringBuffer format(Object number, StringBuffer toAppendTo, FieldPosition pos) {
                return format(((Number)number).doubleValue(), toAppendTo, pos);
            }
        });

        NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
        rangeAxis.setLowerBound(0);
        double maxPOSValue = filteredCharges.stream()
                .mapToDouble(ParkingCharge::getCommPOS)
                .max()
                .orElse(1000);
        rangeAxis.setUpperBound(maxPOSValue * 1.1);

        // 8. Настраиваем легенду
        LegendTitle legend = chart.getLegend();
        legend.setPosition(RectangleEdge.RIGHT);
   //     legend.setItemFont(new Font("SansSerif", Font.PLAIN, 9));

        // 9. Сохраняем график
        ChartUtils.saveChartAsPNG(new File(filename), chart, 2200, 1200);
    }

    public static void plotComparisonChart(List<ParkingCharge> charges, String title, String filename) throws IOException {
        // 1. Фильтрация данных по модели "parking_charges_2026"
        List<ParkingCharge> filteredCharges = charges.stream()
                .filter(pc -> pc.getModel() != null && pc.getModel().contains("2026"))
                .collect(Collectors.toList());

        // 2. Проверка наличия данных
        if (filteredCharges.isEmpty()) {
            throw new IllegalArgumentException("Нет данных для модели parking_charges_2026");
        }

        // 3. Подготовка структуры для агрегации данных: район -> цель поездки -> тип парковки -> значения
        Map<String, Map<String, Map<String, List<Double>>>> dataStructure = new LinkedHashMap<>();

        // 4. Заполнение структуры данных
        for (ParkingCharge pc : filteredCharges) {
            // Пропускаем записи без района или с некорректными значениями
            if (pc.getBorough() == null || pc.getCommPOS() < 0 || pc.getCommOS() < 0 ||
                    pc.getOtherPOS() < 0 || pc.getOtherOS() < 0) {
                continue;
            }

            // Инициализация вложенных структур при необходимости
            dataStructure
                    .computeIfAbsent(pc.getBorough(), k -> new LinkedHashMap<>())
                    .computeIfAbsent("Commute", k -> new LinkedHashMap<>())
                    .computeIfAbsent("POS", k -> new ArrayList<>())
                    .add(pc.getCommPOS());

            dataStructure
                    .get(pc.getBorough())
                    .get("Commute")
                    .computeIfAbsent("OS", k -> new ArrayList<>())
                    .add(pc.getCommOS());

            dataStructure
                    .get(pc.getBorough())
                    .computeIfAbsent("Other", k -> new LinkedHashMap<>())
                    .computeIfAbsent("POS", k -> new ArrayList<>())
                    .add(pc.getOtherPOS());

            dataStructure
                    .get(pc.getBorough())
                    .get("Other")
                    .computeIfAbsent("OS", k -> new ArrayList<>())
                    .add(pc.getOtherOS());
        }

        // 5. Создание набора данных для графика
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();

        // 6. Расчет средних значений и добавление в dataset
        dataStructure.forEach((borough, purposeMap) -> {
            purposeMap.forEach((purpose, typeMap) -> {
                typeMap.forEach((type, values) -> {
                    if (!values.isEmpty()) {
                        double average = values.stream()
                                .mapToDouble(Double::doubleValue)
                                .average()
                                .orElse(0);
                        // Формируем ключ: "Район (Purpose)"
                        String seriesKey = String.format("%s (%s)", borough, purpose);
                        dataset.addValue(average, seriesKey, type);
                    }
                });
            });
        });

        // 7. Создание и настройка графика
        JFreeChart chart = ChartFactory.createBarChart(
                title + " (2026)",  // Добавляем год в заголовок
                "Тип парковки",
                "Средняя стоимость (пенсы)",
                dataset,
                PlotOrientation.VERTICAL,
                true,  // Показывать легенду
                true,  // Показывать подсказки
                false  // Не использовать URLs
        );

        // 8. Дополнительные настройки для улучшения читаемости
//        chart.getCategoryPlot().setBackgroundPaint(Color.WHITE);
        chart.getCategoryPlot().getDomainAxis().setCategoryMargin(0.2);
        chart.getCategoryPlot().getRangeAxis().setLowerBound(0);

        // 9. Сохранение графика
        File outputFile = new File(filename);
        ChartUtils.saveChartAsPNG(outputFile, chart, 1200, 800);
        System.out.println("График успешно сохранен: " + outputFile.getAbsolutePath());
    }

    public static void plotYearlyTrends(List<ParkingCharge> charges, String title, String filename) throws IOException {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();

        // Группируем по году и типу парковки
        Map<String, Map<String, Double>> yearlyData = new HashMap<>();

        for (ParkingCharge pc : charges) {
            String year = pc.getModel().split("_")[0]; // Извлекаем год из названия модели
            yearlyData.putIfAbsent(year, new HashMap<>());

            // Суммируем значения по типам (Commute)
            yearlyData.get(year).merge("POS", pc.getCommPOS(), Double::sum);
            yearlyData.get(year).merge("PNR", pc.getCommPNR(), Double::sum);
            yearlyData.get(year).merge("OS", pc.getCommOS(), Double::sum);
        }

        // Рассчитываем средние значения
        yearlyData.forEach((year, types) -> {
            long count = charges.stream().filter(pc -> pc.getModel().startsWith(year)).count();
            types.forEach((type, sum) -> dataset.addValue(sum/count, type, year));
        });

        JFreeChart chart = ChartFactory.createLineChart(
                title,
                "Year",
                "Average Charge (pence)",
                dataset
        );

        ChartUtils.saveChartAsPNG(new File(filename), chart, 1000, 600);
    }
}