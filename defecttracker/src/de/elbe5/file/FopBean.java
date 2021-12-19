/*
 Elbe 5 CMS - A Java based modular Content Management System
 Copyright (C) 2009-2020 Michael Roennau

 This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
 This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.elbe5.file;

import de.elbe5.application.ApplicationPath;
import de.elbe5.base.data.BinaryFile;
import de.elbe5.base.log.Log;
import de.elbe5.base.util.StringUtil;
import de.elbe5.database.DbBean;
import org.apache.fop.apps.FOUserAgent;
import org.apache.fop.apps.Fop;
import org.apache.fop.apps.FopFactory;

import javax.xml.transform.*;
import javax.xml.transform.sax.SAXResult;
import javax.xml.transform.stream.StreamSource;
import java.io.*;
import java.util.Base64;

public class FopBean extends DbBean {

    private static FopBean instance = null;

    public static FopBean getInstance() {
        if (instance == null) {
            instance = new FopBean();
        }
        return instance;
    }

    protected String xml(String src){
        return StringUtil.toXml(src);
    }

    public byte[] generatePdf(String basePath, String xml, Templates xslTemplates) {
        if (xslTemplates==null){
            Log.error("no xsl templates provided");
            return null;
        }
        byte[] pdf = null;
        try {
            File confFile = new File(basePath+"/fop.xconf");
            FopFactory fopFactory=FopFactory.newInstance(confFile);
            FOUserAgent foUserAgent = fopFactory.newFOUserAgent();
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            try (out) {
                Fop fop = fopFactory.newFop("application/pdf", foUserAgent, out);
                Transformer transformer = xslTemplates.newTransformer();
                Source src = new StreamSource(new StringReader(xml));
                Result res = new SAXResult(fop.getDefaultHandler());
                transformer.transform(src, res);
            }
            pdf = out.toByteArray();
        } catch (Exception e) {
            Log.error("PDF generation error", e);
        }
        return pdf;
    }

    public BinaryFile getPdf(String xml, String xslName, String fileName) {
        Templates templates=getXslTemplates(xslName);
        if (templates==null)
            return null;
        BinaryFile file=new BinaryFile();
        file.setFileName(fileName);
        file.setContentType("application/pdf");
        file.setBytes(generatePdf(ApplicationPath.getAppROOTPath(), xml, templates));
        file.setFileSize(file.getBytes().length);
        return file;
    }

    private Templates getXslTemplates(String xslName) {
        File f=new File(ApplicationPath.getAppWEBINFPath()+"/_templates/"+xslName);
        if (!f.exists()){
            Log.error("Could not load xsl file");
            return null;
        }
        try (InputStream xlsStream = new FileInputStream(f)) {
            Source xsl = new StreamSource(xlsStream);
            return TransformerFactory.newInstance().newTemplates(xsl);
        } catch (Exception e) {
            Log.error("Could not transform xsl file", e);
            return null;
        }
    }

    protected String getBase64SrcString(BinaryFile file){
        StringBuilder sb = new StringBuilder("data:").append(file.getContentType()).append(";base64,");
        sb.append(Base64.getEncoder().encodeToString(file.getBytes()));
        return sb.toString();
    }

}
