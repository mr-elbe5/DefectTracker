/*
 Elbe 5 CMS - A Java based modular Content Management System
 Copyright (C) 2009-2020 Michael Roennau

 This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
 This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.elbe5.file;

import de.elbe5.base.data.BinaryFile;
import de.elbe5.base.data.BinaryStreamFile;
import de.elbe5.base.log.Log;
import de.elbe5.database.FileBasedDbBean;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class FileBean extends FileBasedDbBean {

    private static FileBean instance = null;

    public static FileBean getInstance() {
        if (instance == null) {
            instance = new FileBean();
        }
        return instance;
    }

    public int getNextId() {
        return getNextId("s_file_id");
    }

    private static String CHANGED_SQL = "SELECT change_date FROM t_file WHERE id=?";

    public boolean changedFile(Connection con, FileData data) {
        return changedItem(con, CHANGED_SQL, data);
    }

    private static String GET_ALL_FILES_SQL = "SELECT type,id,creation_date,change_date,parent_id,file_name,display_name,description,creator_id,changer_id,content_type,file_size FROM t_file order by file_name";

    public List<FileData> getAllFiles() {
        List<FileData> list = new ArrayList<>();
        Connection con = getConnection();
        PreparedStatement pst = null;
        try {
            pst = con.prepareStatement(GET_ALL_FILES_SQL);
            try (ResultSet rs = pst.executeQuery()) {
                while (rs.next()) {
                    int i = 1;
                    String type = rs.getString(i++);
                    FileData data = FileFactory.getNewData(type);
                    if (data != null) {
                        data.setId(rs.getInt(i++));
                        data.setCreationDate(rs.getTimestamp(i++).toLocalDateTime());
                        data.setChangeDate(rs.getTimestamp(i++).toLocalDateTime());
                        data.setParentId(rs.getInt(i++));
                        data.setFileName(rs.getString(i++));
                        data.setDisplayName(rs.getString(i++));
                        data.setDescription(rs.getString(i++));
                        data.setCreatorId(rs.getInt(i++));
                        data.setChangerId(rs.getInt(i++));
                        data.setContentType(rs.getString(i++));
                        data.setFileSize(rs.getInt(i));
                    }
                    if (data!=null) {
                        FileBean extBean = FileFactory.getBean(data.getType());
                        if (extBean != null)
                            extBean.readFileExtras(con, data, false);
                        list.add(data);
                    }
                }
            }
        } catch (SQLException se) {
            Log.error("sql error", se);
        } finally {
            closeStatement(pst);
            closeConnection(con);
        }
        return list;
    }

    public FileData getFile(int id,boolean complete) {
        FileData data = null;
        Connection con = getConnection();
        try {
            data = readFile(con, id, complete);
            FileBean extBean = FileFactory.getBean(data.getType());
            if (extBean != null)
                extBean.readFileExtras(con, data, complete);
        } catch (SQLException se) {
            Log.error("sql error", se);
        } finally {
            closeConnection(con);
        }
        return data;
    }

    public <T extends FileData> T getFile(int id, boolean complete, Class<T> cls) {
        try {
            return cls.cast(getFile(id,complete));
        }
        catch(NullPointerException | ClassCastException e){
            return null;
        }
    }

    private static String GET_FILE_SQL = "SELECT type,id,creation_date,change_date,parent_id,file_name,display_name,description,creator_id,changer_id,content_type,file_size FROM t_file WHERE id=?";
    private static String GET_FILE_COMPLETE_SQL = "SELECT type,id,creation_date,change_date,parent_id,file_name,display_name,description,creator_id,changer_id,content_type,file_size,bytes FROM t_file WHERE id=?";

    public FileData readFile(Connection con, int id, boolean complete) throws SQLException {
        FileData data = null;
        PreparedStatement pst = null;
        try {
            pst = con.prepareStatement(complete ? GET_FILE_COMPLETE_SQL : GET_FILE_SQL);
            pst.setInt(1, id);
            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    int i = 1;
                    String type = rs.getString(i++);
                    data = FileFactory.getNewData(type);
                    if (data != null) {
                        data.setId(rs.getInt(i++));
                        data.setCreationDate(rs.getTimestamp(i++).toLocalDateTime());
                        data.setChangeDate(rs.getTimestamp(i++).toLocalDateTime());
                        data.setParentId(rs.getInt(i++));
                        data.setFileName(rs.getString(i++));
                        data.setDisplayName(rs.getString(i++));
                        data.setDescription(rs.getString(i++));
                        data.setCreatorId(rs.getInt(i++));
                        data.setChangerId(rs.getInt(i++));
                        data.setContentType(rs.getString(i++));
                        data.setFileSize(rs.getInt(i++));
                        if (complete){
                            data.setBytes(rs.getBytes(i));
                        }
                    }
                }
            }
        } finally {
            closeStatement(pst);
        }
        return data;
    }

    public void readFileExtras(Connection con, FileData fileData, boolean complete) throws SQLException{
    }

    public boolean saveFile(FileData data, boolean complete) {
        Connection con = startTransaction();
        try {
            if (!saveFile(con, data, complete))
                return rollbackTransaction(con);
            return commitTransaction(con);
        } catch (Exception se) {
            return rollbackTransaction(con, se);
        }
    }

    public boolean saveFile(Connection con, FileData data, boolean complete) throws SQLException{
        if (!data.isNew() && changedFile(con, data)) {
            return false;
        }
        data.setChangeDate(getServerTime(con));
        writeFile(con, data, complete);
        FileBean extrasBean = FileFactory.getBean(data.getType());
        if (extrasBean != null) {
            extrasBean.writeFileExtras(con, data, complete);
        }
        return true;
    }

    private static String INSERT_FILE_SQL = "insert into t_file (type,creation_date,change_date,parent_id,file_name,display_name,description,creator_id,changer_id,content_type,file_size,bytes,id) values(?,?,?,?,?,?,?,?,?,?,?,?,?)";
    private static String UPDATE_FILE_SQL = "update t_file set type=?,creation_date=?,change_date=?,parent_id=?,file_name=?,display_name=?,description=?,creator_id=?,changer_id=?,content_type=?,file_size=?,bytes=? where id=?";
    private static String UPDATE_FILE_NOBYTES_SQL = "update t_file set type=?,creation_date=?,change_date=?,parent_id=?,file_name=?,display_name=?,description=?,creator_id=?,changer_id=?,content_type=?,file_size=? where id=?";

    public void writeFile(Connection con, FileData data, boolean complete) throws SQLException {
        if (!data.isNew() && data.getBytes()==null)
            return;
        PreparedStatement pst;
        data.setChangeDate(getServerTime(con));
        int i = 1;
        pst = con.prepareStatement(data.isNew() ? INSERT_FILE_SQL : (complete ? UPDATE_FILE_SQL : UPDATE_FILE_NOBYTES_SQL));
        pst.setString(i++, data.getClass().getSimpleName());
        pst.setTimestamp(i++, Timestamp.valueOf(data.getCreationDate()));
        pst.setTimestamp(i++, Timestamp.valueOf(data.getChangeDate()));
        if (data.getParentId() == 0) {
            pst.setNull(i++, Types.INTEGER);
        } else {
            pst.setInt(i++, data.getParentId());
        }
        pst.setString(i++, data.getFileName());
        pst.setString(i++, data.getDisplayName());
        pst.setString(i++, data.getDescription());
        pst.setInt(i++, data.getCreatorId());
        pst.setInt(i++, data.getChangerId());
        pst.setString(i++, data.getContentType());
        pst.setInt(i++, data.getFileSize());
        if (complete) {
            pst.setBytes(i++, data.getBytes());
        }
        pst.setInt(i, data.getId());
        pst.executeUpdate();
        pst.close();
    }

    public void writeFileExtras(Connection con, FileData contentData, boolean complete) throws SQLException {
    }

    private static String GET_FILE_STREAM_SQL = "SELECT file_name,content_type,file_size,bytes FROM t_file WHERE id=?";

    public BinaryStreamFile getBinaryStreamFile(int id) {
        Connection con = getConnection();
        PreparedStatement pst = null;
        BinaryStreamFile data = null;
        try {
            pst = con.prepareStatement(GET_FILE_STREAM_SQL);
            pst.setInt(1, id);
            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    int i = 1;
                    data = new BinaryStreamFile();
                    data.setFileName(rs.getString(i++));
                    data.setContentType(rs.getString(i++));
                    data.setFileSize(rs.getInt(i++));
                    data.setInputStream(rs.getBinaryStream(i));
                }
            }
        } catch (SQLException e) {
            Log.error("error while streaming file", e);
            return null;
        } finally {
            closeStatement(pst);
            closeConnection(con);
        }
        return data;
    }

    private static String GET_FILE_DATA_SQL = "SELECT file_name,content_type,file_size,bytes FROM t_file WHERE id=?";

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
                    data.setFileSize(rs.getInt(i++));
                    data.setBytes(rs.getBytes(i));
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

    private static String DELETE_SQL = "DELETE FROM t_file WHERE id=?";

    public boolean deleteFile(int id) {
        return deleteItem(DELETE_SQL, id);
    }

}
