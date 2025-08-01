package com.transport.loader;

import com.transport.model.parking.ParkingCharge;
import org.apache.poi.ss.usermodel.Workbook;

import java.util.ArrayList;
import java.util.List;

import org.apache.poi.ss.usermodel.*;
import org.springframework.stereotype.Component;

import java.io.File;

import static com.transport.util.CellValueGetter.getCellValueAsDouble;
import static com.transport.util.CellValueGetter.getCellValueAsString;

@Component
public class ParkingChargeLoader {

    public List<ParkingCharge> loadData(String excelFilePath) throws Exception {
        List<ParkingCharge> data = new ArrayList<>();
        Workbook workbook = WorkbookFactory.create(new File(excelFilePath));
        Sheet sheet = workbook.getSheetAt(0);

        for (Row row : sheet) {
            if (row.getRowNum() < 2) continue; // Пропускаем заголовки

            ParkingCharge pc = new ParkingCharge();
            pc.setZoneId(getCellValueAsString(row.getCell(0))); // ZoneID
            pc.setBorough(getCellValueAsString(row.getCell(1))); // Borough
            pc.setOtherPOS(getCellValueAsDouble(row.getCell(2))); // Other_POS
            pc.setOtherPNR(getCellValueAsDouble(row.getCell(3))); // Other_PNR
            pc.setOtherOS(getCellValueAsDouble(row.getCell(4)));  // Other_OS
            pc.setCommPOS(getCellValueAsDouble(row.getCell(5)));  // Comm_POS
            pc.setCommPNR(getCellValueAsDouble(row.getCell(6)));  // Comm_PNR
            pc.setCommOS(getCellValueAsDouble(row.getCell(7)));   // Comm_OS

            data.add(pc);
        }

        workbook.close();
        return data;
    }
}