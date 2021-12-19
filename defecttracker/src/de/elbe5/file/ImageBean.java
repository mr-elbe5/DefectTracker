/*
 Elbe 5 CMS - A Java based modular Content Management System
 Copyright (C) 2009-2020 Michael Roennau

 This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
 This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.elbe5.file;

import de.elbe5.base.data.BinaryFile;
import de.elbe5.base.log.Log;

import java.sql.*;

public class ImageBean extends FileBean {

    private static ImageBean instance = null;

    public static ImageBean getInstance() {
        if (instance == null) {
            instance = new ImageBean();
        }
        return instance;
    }

    private static String GET_CONTENT_EXTRAS_SQL = "SELECT width,height,(preview_bytes IS NOT NULL) as has_preview FROM t_image WHERE id=?";
    private static String GET_CONTENT_EXTRAS_COMPLETE_SQL = "SELECT width,height,preview_bytes FROM t_image WHERE id=?";

    @Override
    public void readFileExtras(Connection con, FileData contentData, boolean complete) throws SQLException {
        if (!(contentData instanceof ImageData))
            return;
        ImageData data = (ImageData) contentData;
        PreparedStatement pst = null;
        try {
            pst = con.prepareStatement(complete ? GET_CONTENT_EXTRAS_COMPLETE_SQL : GET_CONTENT_EXTRAS_SQL);
            pst.setInt(1, data.getId());
            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    int i = 1;
                    data.setWidth(rs.getInt(i++));
                    data.setHeight(rs.getInt(i++));
                    if (complete){
                        data.setPreviewBytes(rs.getBytes(i));
                        data.setHasPreview(data.getPreviewBytes()!=null);
                    }
                    else
                        data.setHasPreview(rs.getBoolean(i));
                }
            }
        } finally {
            closeStatement(pst);
        }
    }

    private static String INSERT_CONTENT_EXTRAS_SQL = "insert into t_image (width,height,preview_bytes,id) values(?,?,?,?)";
    private static String UPDATE_CONTENT_EXTRAS_SQL = "update t_image set width=?,height=?,preview_bytes=? where id=?";
    private static String UPDATE_CONTENT_EXTRAS_NOBYTES_SQL = "update t_image set width=?,height=? where id=?";

    @Override
    public void writeFileExtras(Connection con, FileData contentData, boolean complete) throws SQLException {
        if (!(contentData instanceof ImageData))
            return;
        ImageData data = (ImageData) contentData;
        PreparedStatement pst;
        int i = 1;
        pst = con.prepareStatement(data.isNew() ? INSERT_CONTENT_EXTRAS_SQL : (complete ? UPDATE_CONTENT_EXTRAS_SQL : UPDATE_CONTENT_EXTRAS_NOBYTES_SQL));
        pst.setInt(i++, data.getWidth());
        pst.setInt(i++, data.getHeight());
        if (complete) {
            if (data.getPreviewBytes() == null)
                pst.setNull(i++, Types.BLOB);
            else
                pst.setBytes(i++, data.getPreviewBytes());
        }
        pst.setInt(i, data.getId());
        pst.executeUpdate();
        pst.close();
    }

    private static String GET_PREVIEW_SQL = "SELECT preview_bytes FROM t_image WHERE id=?";

    public BinaryFile getBinaryPreviewFile(int id) {
        Connection con = getConnection();
        PreparedStatement pst = null;
        BinaryFile data = null;
        try {
            pst = con.prepareStatement(GET_PREVIEW_SQL);
            pst.setInt(1, id);
            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    int i = 1;
                    data = new BinaryFile();
                    data.setContentType("image/jpg");
                    data.setBytes(rs.getBytes(i));
                    data.setFileSize(data.getBytes().length);
                }
            }
        } catch (SQLException e) {
            Log.error("error while downloading file", e);
            return null;
        } finally {
            closeStatement(pst);
            closeConnection(con);
        }
        return data;
    }

    private static String GET_FILE_DATA_SQL = "SELECT file_name,content_type,preview_bytes FROM v_preview_file WHERE id=?";

    public BinaryFile getBinaryFile(int id) {
        Connection con = getConnection();
        PreparedStatement pst = null;
        BinaryFile data = null;
        try {
            pst = con.prepareStatement(GET_FILE_DATA_SQL);
            pst.setInt(1, id);
            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    int i = 1;
                    data = new BinaryFile();
                    data.setFileName(rs.getString(i++));
                    data.setContentType(rs.getString(i++));
                    data.setBytes(rs.getBytes(i));
                    data.setFileSize(data.getBytes().length);
                }
            }
        } catch (SQLException e) {
            Log.error("error while getting file", e);
            return null;
        } finally {
            closeStatement(pst);
            closeConnection(con);
        }
        return data;
    }

}
