package com.transport.loader;

import com.transport.model.transport.CarOwnership;
import org.apache.poi.ss.usermodel.*;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static com.transport.util.CellValueGetter.getCellValueAsDouble;
import static com.transport.util.CellValueGetter.getCellValueAsString;

@Component
public class CarOwnershipLoader {

    public List<CarOwnership> loadData(String excelFilePath) throws Exception {
        List<CarOwnership> data = new ArrayList<>();
        Workbook workbook = WorkbookFactory.create(new File(excelFilePath));
        Sheet sheet = workbook.getSheetAt(0); // Данные обычно на первом листе

        for (Row row : sheet) {
            if (row.getRowNum() < 2) continue; // Пропускаем заголовки

            // Пример для 2026 Hybrid (колонка F)
            CarOwnership co = new CarOwnership();
            co.setZoneId(getCellValueAsString(row.getCell(0))); // ZoneID
            co.setBorough(getCellValueAsString(row.getCell(1))); // Borough
//            co.setYear(2026);
//            co.setScenario("Hybrid");
//            co.setCarOwners((int) getCellValueAsDouble(row.getCell(5))); // Число владельцев

            data.add(co);
        }

        workbook.close();
        return data;
    }

}