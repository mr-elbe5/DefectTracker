/*
 Elbe 5 CMS - A Java based modular Content Management System
 Copyright (C) 2009-2020 Michael Roennau

 This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
 This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.elbe5.defecttracker.defect;

import de.elbe5.application.ApplicationPath;
import de.elbe5.base.data.BinaryFile;
import de.elbe5.base.log.Log;
import de.elbe5.base.util.StringUtil;
import de.elbe5.defecttracker.DefectPoiBean;
import de.elbe5.user.UserData;
import org.apache.poi.xwpf.usermodel.*;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.util.Locale;

public class DefectDocxBean extends DefectPoiBean {

    private static DefectDocxBean instance = null;

    public static DefectDocxBean getInstance() {
        if (instance == null) {
            instance = new DefectDocxBean();
        }
        return instance;
    }

    protected String xml(String src){
        return StringUtil.toXml(src);
    }

    public BinaryFile getDefectWordFile(DefectData data, UserData fromUser, UserData toUser, Locale locale) {
        BinaryFile file=null;
        try {
            XWPFDocument document = new XWPFDocument();
            addFromAddress(document, fromUser);
            addSpace(document,1);
            addToAddress(document, toUser);
            addSpace(document,2);
            XWPFParagraph para = createParagraph(document);
            XWPFRun run=createRun(para);
            run.setText("lorem ipsum lorem lala");
            addSpace(document,2);
            addTable(document,cTexts);
            addSpace(document,2);
            addSignature(document, fromUser, fromUser.getCity(), LocalDate.now());
            addSpace(document,4);
            file=new BinaryFile();
            ByteArrayOutputStream out= new ByteArrayOutputStream();
            document.write(out);
            file.setBytes(out.toByteArray());
            file.setContentType("application/docx");
        file.setFileName("project-"+data.getName()+".docx");
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return file;
    }

    private static String[][] cTexts={
            {"Row1Col1","Row1Col2","Row1Col3","Row1Col4"},
            {"Row2Col1","Row2Col2","Row2Col3","Row2Col4"},
            {"Row3Col1","Row3Col2","Row3Col3","Row3Col4"},
            {"Row4Col1","Row4Col2","Row4Col3","Row4Col4"}
    };


    protected void applyTemplate(String xmlName, XWPFDocument document){
        try {
            File f = new File(ApplicationPath.getAppWEBINFPath() + "/_templates/" + xmlName);
            Document doc = Jsoup.parse(f, "UTF-8");
            Elements elements = doc.getElementsByTag("p");
            for (Element element : elements) {
                XWPFParagraph paragraph = document.createParagraph();
                XWPFRun run = paragraph.createRun();
                run.setText(element.text());
            }
        }
        catch (IOException e){
            Log.error("could not read template", e);
        }
    }

}
