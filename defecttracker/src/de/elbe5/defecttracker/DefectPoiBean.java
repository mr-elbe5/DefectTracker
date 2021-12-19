/*
 Elbe 5 CMS - A Java based modular Content Management System
 Copyright (C) 2009-2020 Michael Roennau

 This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
 This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.elbe5.defecttracker;

import de.elbe5.base.cache.Strings;
import de.elbe5.base.util.StringUtil;
import de.elbe5.user.UserData;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xwpf.usermodel.*;

import java.time.LocalDate;
import java.util.Locale;

public abstract class DefectPoiBean {

    // general

    protected String xml(String src){
        return StringUtil.toXml(src);
    }

    // xslx

    protected void addCell(Row row, int idx, CellStyle style, String value){
        Cell cell=row.createCell(idx,CellType.STRING);
        cell.setCellStyle(style);
        cell.setCellValue(value);
    }

    protected void addCell(Row row, int idx, String value){
        Cell cell=row.createCell(idx,CellType.STRING);
        cell.setCellValue(value);
    }

    protected void addCell(Row row, int idx, String value, CellStyle style){
        Cell cell=row.createCell(idx);
        cell.setCellStyle(style);
        cell.setCellValue(value);
    }

    protected void addCell(Row row, int idx, int value){
        Cell cell=row.createCell(idx,CellType.NUMERIC);
        cell.setCellValue(value);
    }

    //docx

    protected void addFromAddress(XWPFDocument document, UserData user){
        XWPFParagraph para = createParagraph(document);
        XWPFRun run=createRun(para);
        run.setText(Strings.xml("_from", Locale.GERMAN));
        run.addBreak();
        run.setText(user.getName());
        run.addBreak();
        run.setText(user.getStreet());
        run.addBreak();
        run.setText(user.getZipCode() + " " + user.getCity());
    }

    protected void addToAddress(XWPFDocument document, UserData user){
        XWPFParagraph para = createParagraph(document);
        XWPFRun run=createRun(para);
        run.setText(Strings.xml("_to", Locale.GERMAN));
        run.addBreak();
        run.setText(user.getName());
        run.addBreak();
        run.setText(user.getStreet());
        run.addBreak();
        run.setText(user.getZipCode() + " " + user.getCity());

    }

    protected void addSignature(XWPFDocument document, UserData user, String city, LocalDate date){
        XWPFParagraph para = createParagraph(document);
        XWPFRun run=createRun(para);
        run.setText(city + ", " + StringUtil.toHtmlDate(date, Locale.GERMAN));
        run.addBreak();
        run.addBreak();
        run.addBreak();
        run.setText(user.getName());
    }

    protected void addSpace(XWPFDocument document, int lines) {
        if (lines==0)
            return;
        XWPFParagraph para = createParagraph(document);
        XWPFRun run = createRun(para);
        for (int i=1;i<lines;i++)
            run.addBreak();
    }

    protected void addTable(XWPFDocument doc, String[][] textArr){
        int nRows = textArr.length;
        int nCols = nRows==0 ? 0 : textArr[0].length;
        if (nRows==0 || nCols==0)
            return;
        XWPFTable table = doc.createTable(nRows, nCols);
        table.setWidth("100%");
        table.removeBorders();
        table.setInsideVBorder(XWPFTable.XWPFBorderType.SINGLE, 1, 0, "d8d8d8");
        table.setInsideHBorder(XWPFTable.XWPFBorderType.SINGLE, 1, 0, "d8d8d8");
        table.setLeftBorder(XWPFTable.XWPFBorderType.SINGLE, 1, 0, "d8d8d8");
        table.setRightBorder(XWPFTable.XWPFBorderType.SINGLE, 1, 0, "d8d8d8");
        table.setBottomBorder(XWPFTable.XWPFBorderType.SINGLE, 1, 0, "d8d8d8");
        for (int rIdx=0;rIdx<nRows;rIdx++){
            XWPFTableRow tableRow = table.getRow(rIdx);
            for (int cIdx=0;cIdx<nCols;cIdx++){
                XWPFTableCell cell= tableRow.getCell(cIdx);
                cell.setText(textArr[rIdx][cIdx]);
                cell.setColor("e8e8e8");
            }
        }
    }

    protected XWPFParagraph createParagraph(XWPFDocument doc){
        XWPFParagraph para=doc.createParagraph();
        para.setAlignment(ParagraphAlignment.LEFT);
        return para;
    }

    protected XWPFRun createRun(XWPFParagraph para){
        XWPFRun run=para.createRun();
        run.setFontFamily("Arial");
        run.setFontSize(12);
        run.setColor("000000");
        return run;
    }

}
