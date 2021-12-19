/*
 Elbe 5 CMS - A Java based modular Content Management System
 Copyright (C) 2009-2020 Michael Roennau

 This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
 This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.elbe5.defecttracker;

import de.elbe5.base.data.BinaryFile;
import de.elbe5.file.FopBean;

public abstract class DefectFopBean extends FopBean {

    public void addLabeledContent(StringBuilder sb, String label,String content){
        sb.append("<labeledcontent>");
        sb.append("<label>").append(xml(label)).append("</label>");
        sb.append("<content>").append(xml(content)).append("</content>");
        sb.append("</labeledcontent>");
    }

    public void addLabeledImage(StringBuilder sb, String label, BinaryFile file, String height){
        sb.append("<labeledimage>");
        sb.append("<label>").append(xml(label)).append("</label>");
        sb.append("<src>").append(getBase64SrcString(file)).append("</src>");
        sb.append("<height>").append(height).append("</height>");
        sb.append("</labeledimage>");
    }

    public void addImage(StringBuilder sb, BinaryFile file, String height){
        sb.append("<image>");
        sb.append("<src>").append(getBase64SrcString(file)).append("</src>");
        sb.append("<height>").append(height).append("</height>");
        sb.append("</image>");
    }

    public void addCell(StringBuilder sb, String text){
        sb.append("<tablecell><content>");
        sb.append(xml(text));
        sb.append("</content></tablecell>");
    }

}
