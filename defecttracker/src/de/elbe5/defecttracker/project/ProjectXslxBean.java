/*
 Elbe 5 CMS - A Java based modular Content Management System
 Copyright (C) 2009-2020 Michael Roennau

 This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
 This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.elbe5.defecttracker.project;

import de.elbe5.base.cache.Strings;
import de.elbe5.base.data.BinaryFile;
import de.elbe5.base.log.Log;
import de.elbe5.base.util.StringUtil;
import de.elbe5.defecttracker.DefectPoiBean;
import de.elbe5.defecttracker.defect.DefectData;
import de.elbe5.defecttracker.location.LocationData;
import de.elbe5.user.UserData;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.xwpf.usermodel.*;

import java.io.ByteArrayOutputStream;
import java.time.LocalDate;
import java.util.List;
import java.util.Locale;

public class ProjectXslxBean extends DefectPoiBean {

    private static ProjectXslxBean instance = null;

    public static ProjectXslxBean getInstance() {
        if (instance == null) {
            instance = new ProjectXslxBean();
        }
        return instance;
    }

    public BinaryFile getProjectExcel(ProjectData data, Locale locale){
        XSSFWorkbook wb = new XSSFWorkbook();

        CreationHelper createHelper = wb.getCreationHelper();
        CellStyle dateStyle = wb.createCellStyle();
        dateStyle.setDataFormat(createHelper.createDataFormat().getFormat(StringUtil.getDatePattern(locale)));
        dateStyle.setAlignment(HorizontalAlignment.LEFT);
        CellStyle wrapStyle = wb.createCellStyle();
        wrapStyle.setWrapText(true);
        CellStyle headerStyle = wb.createCellStyle();
        XSSFColor xssfColor = new XSSFColor(IndexedColors.GREY_25_PERCENT,null);
        headerStyle.setFillForegroundColor(xssfColor.getIndex());
        headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        for (LocationData location : data.getChildren(LocationData.class)){
            writeLocation(location, wb, locale, dateStyle, wrapStyle, headerStyle);
        }
        BinaryFile file=new BinaryFile();
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            wb.write(baos);
            file.setBytes(baos.toByteArray());
        } catch (Exception e) {
            e.printStackTrace();
            e.getCause().printStackTrace();
            Log.error("Could not write XLSX file", e);
            return null;
        }
        file.setContentType("application/xlsx");
        file.setFileName("project-"+data.getName()+".xlsx");
        return file;
    }

    protected void writeLocation(LocationData data, Workbook wb, Locale locale, CellStyle dateStyle, CellStyle wrapStyle, CellStyle headerStyle){
        List<DefectData> defects = data.getChildren(DefectData.class);
        Sheet sheet = wb.createSheet(xml(data.getDisplayName()));
        int rowIdx=0;
        int cellIdx=0;
        //header
        Row row = sheet.createRow(rowIdx++);
        addCell(row, cellIdx++, headerStyle, Strings.xml("_id",locale));
        addCell(row, cellIdx++, headerStyle, Strings.xml("_description",locale));
        addCell(row, cellIdx++, headerStyle, Strings.xml("_creationDate",locale));
        addCell(row, cellIdx++, headerStyle, Strings.xml("_dueDate1",locale));
        addCell(row, cellIdx++, headerStyle, Strings.xml("_dueDate2",locale));
        addCell(row, cellIdx++, headerStyle, Strings.xml("_closeDate",locale));
        addCell(row, cellIdx++, headerStyle, Strings.xml("_state",locale));
        addCell(row, cellIdx++, headerStyle, Strings.xml("_assigned",locale));
        addCell(row, cellIdx++, headerStyle, Strings.xml("_lot",locale));
        addCell(row, cellIdx++, headerStyle, Strings.xml("_costs",locale));

        int cols=cellIdx;
        //defects
        for (DefectData defect : defects) {
            row = sheet.createRow(rowIdx++);
            cellIdx = 0;
            addCell(row, cellIdx++, defect.getDisplayId());
            addCell(row, cellIdx++, xml(defect.getDescription()));
            addCell(row, cellIdx++, StringUtil.toHtmlDate(defect.getCreationDate(),locale), dateStyle);
            addCell(row, cellIdx++, StringUtil.toHtmlDate(defect.getDueDate1(),locale), dateStyle);
            addCell(row, cellIdx++, StringUtil.toHtmlDate(defect.getDueDate2(),locale), dateStyle);
            addCell(row, cellIdx++, StringUtil.toHtmlDate(defect.getCloseDate(),locale), dateStyle);
            addCell(row, cellIdx++, Strings.xml(defect.getState(),locale));
            addCell(row, cellIdx++, xml(defect.getAssignedName()));
            addCell(row, cellIdx++, xml(defect.getLot()));
            addCell(row, cellIdx, defect.getCosts());
        }
        //resize
        for (int i = 0; i < cols; i++)
            sheet.autoSizeColumn(i);
    }
}
