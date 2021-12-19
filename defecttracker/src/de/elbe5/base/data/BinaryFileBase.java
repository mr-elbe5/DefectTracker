/*
 Elbe 5 CMS - A Java based modular Content Management System
 Copyright (C) 2009-2018 Michael Roennau

 This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
 This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.elbe5.base.data;

public abstract class BinaryFileBase {

    protected String fileName = null;
    protected String contentType = null;
    protected int size = 0;

    public BinaryFileBase() {
    }

    public String getFileName() {
        return fileName;
    }

    public String getExtension() {
        if (fileName == null) {
            return null;
        }
        int pos = fileName.lastIndexOf('.');
        if (pos == -1) {
            return null;
        }
        return fileName.substring(pos + 1).toLowerCase();
    }

    public String getFileNameWithoutExtension() {
        if (fileName == null) {
            return null;
        }
        int pos = fileName.lastIndexOf('.');
        if (pos == -1) {
            return fileName;
        }
        return fileName.substring(0, pos);
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getContentType() {
        return contentType;
    }

    public boolean isImage() {
        return contentType.startsWith("image/");
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public int getFileSize() {
        return size;
    }

    public void setFileSize(int size) {
        this.size = size;
    }

}
