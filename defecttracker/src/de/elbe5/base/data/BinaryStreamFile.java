/*
 Elbe 5 CMS - A Java based modular Content Management System
 Copyright (C) 2009-2018 Michael Roennau

 This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
 This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.elbe5.base.data;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class BinaryStreamFile extends BinaryFileBase {

    protected InputStream inputStream = null;
    protected int bufferSize = 16384;

    public BinaryStreamFile() {
    }

    public InputStream getInputStream() {
        return inputStream;
    }

    public void setInputStream(InputStream inputStream) {
        this.inputStream = inputStream;
    }

    public int getBufferSize() {
        return bufferSize;
    }

    public void setBufferSize(int bufferSize) {
        this.bufferSize = bufferSize;
    }

    public void writeToStream(OutputStream out) throws IOException {
        if (inputStream == null) {
            return;
        }
        byte[] bytes = new byte[getBufferSize()];
        int len;
        while ((len = inputStream.read(bytes, 0, getBufferSize())) > 0) {
            out.write(bytes, 0, len);
        }
        inputStream.close();
    }

}
