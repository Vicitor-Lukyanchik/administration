package com.transport.loader;

import com.transport.model.parking.ParkingSupply;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static com.transport.util.CellValueGetter.getCellValueAsDouble;
import static com.transport.util.CellValueGetter.getCellValueAsString;

public class ParkingSupplyLoader {

    public List<ParkingSupply> loadData(String excelFilePath) throws Exception {
        List<ParkingSupply> data = new ArrayList<>();
        Workbook workbook = WorkbookFactory.create(new File(excelFilePath));
        Sheet sheet = workbook.getSheetAt(0);

        for (Row row : sheet) {
            if (row.getRowNum() < 2) continue; // Пропускаем заголовки

            ParkingSupply ps = new ParkingSupply();
            ps.setZoneId(getCellValueAsString(row.getCell(0))); // ZoneID
            ps.setBorough(getCellValueAsString(row.getCell(1))); // Borough
            ps.setYear(2026); // Пример для 2026 года (колонки C, D, E...)
            ps.setPublicOffStreet((int) getCellValueAsDouble(row.getCell(2))); // Public Off-Street
            ps.setPrivateNonResidential((int) getCellValueAsDouble(row.getCell(3))); // Private Non-Residential
            ps.setPublicOnStreet((int) getCellValueAsDouble(row.getCell(4))); // Public On-Street

            data.add(ps);
        }

        workbook.close();
        return data;
    }
}
