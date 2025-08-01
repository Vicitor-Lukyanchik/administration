package com.transport.loader;

import com.transport.model.geo.Zone;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.springframework.stereotype.Component;

import org.apache.poi.ss.usermodel.*;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static com.transport.util.CellValueGetter.getCellValueAsDouble;
import static com.transport.util.CellValueGetter.getCellValueAsString;

@Component
public class ZoneLoader {

    public List<Zone> loadZones(String excelFilePath) throws Exception {
        List<Zone> zones = new ArrayList<>();
        Workbook workbook = WorkbookFactory.create(new File(excelFilePath));

        // 1. Чтение зон из листа MoTiON_ZoneLookup
        Sheet zoneSheet = workbook.getSheet("MoTiON_ZoneLookup");
        for (Row row : zoneSheet) {
            if (row.getRowNum() == 0) continue; // Пропускаем заголовок

            Zone zone = new Zone();
            zone.setZoneId(getCellValueAsString(row.getCell(0))); // MoTiON Zone (может быть числом или строкой)
            zone.setBorough(getCellValueAsString(row.getCell(3))); // Borough/Area
            zone.setULEZ("Yes".equalsIgnoreCase(getCellValueAsString(row.getCell(5)))); // Within ULEZ

            zones.add(zone);
        }

        // 2. Чтение доп. данных из Motion_OtherLookup
        Sheet otherSheet = workbook.getSheet("Motion_OtherLookups");
        for (Row row : otherSheet) {
            if (row.getRowNum() == 0) continue;

            String zoneId = getCellValueAsString(row.getCell(0));
            zones.stream()
                    .filter(z -> z.getZoneId().equals(zoneId))
                    .findFirst()
                    .ifPresent(z -> {
                        z.setAreaKm2(getCellValueAsDouble(row.getCell(1))); // Area (km²)
                        z.setDockland("Yes".equalsIgnoreCase(getCellValueAsString(row.getCell(3)))); // Dockland
                    });
        }

        workbook.close();
        return zones;
    }
}