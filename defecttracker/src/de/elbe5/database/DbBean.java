/*
 Elbe 5 CMS - A Java based modular Content Management System
 Copyright (C) 2009-2020 Michael Roennau

 This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
 This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.elbe5.database;

import de.elbe5.application.ApplicationPath;
import de.elbe5.base.data.BaseData;
import de.elbe5.base.log.Log;
import de.elbe5.base.util.FileUtil;

import java.io.File;
import java.sql.*;
import java.time.LocalDateTime;

public abstract class DbBean {

    public DbBean() {
    }

    public Connection getConnection() {
        return DbConnector.getInstance().getConnection();
    }

    protected Connection startTransaction() {
        return DbConnector.getInstance().startTransaction();
    }

    protected boolean commitTransaction(Connection con) {
        return DbConnector.getInstance().commitTransaction(con);
    }

    protected boolean rollbackTransaction(Connection con) {
        DbConnector.getInstance().rollbackTransaction(con, null);
        return false;
    }

    protected boolean rollbackTransaction(Connection con, Exception e) {
        Log.error("rolling back", e);
        return DbConnector.getInstance().rollbackTransaction(con);
    }

    protected void closeConnection(Connection con) {
        DbConnector.getInstance().closeConnection(con);
    }

    protected void closeStatement(Statement st) {
        DbConnector.getInstance().closeStatement(st);
    }

    public int getNextId(String sequence) {
        int id = 0;
        Connection con = getConnection();
        try {
            id = getNextId(con, sequence);
        } catch (Exception ignored) {
        } finally {
            closeConnection(con);
        }
        return id;
    }

    public int getNextId(Connection con, String sequence) throws SQLException {
        int id;
        PreparedStatement pst = null;
        try {
            pst = con.prepareStatement("SELECT nextval(?)");
            pst.setString(1, sequence);
            try (ResultSet rs = pst.executeQuery()) {
                rs.next();
                id = rs.getInt(1);
            }
            pst.close();
        } finally {
            closeStatement(pst);
        }
        return id;
    }

    public LocalDateTime getServerTime() {
        LocalDateTime now = null;
        Connection con = getConnection();
        try {
            now = getServerTime(con);
        } catch (Exception ignored) {
        } finally {
            closeConnection(con);
        }
        return now;
    }

    public Timestamp getTimestamp(Connection con) throws SQLException {
        Timestamp now;
        PreparedStatement pst = null;
        try {
            pst = con.prepareStatement("SELECT now()");
            try (ResultSet rs = pst.executeQuery()) {
                rs.next();
                now = rs.getTimestamp(1);
            }
        } finally {
            closeStatement(pst);
        }
        return now;
    }

    public LocalDateTime getServerTime(Connection con) throws SQLException {
        LocalDateTime now;
        PreparedStatement pst = null;
        try {
            pst = con.prepareStatement("SELECT now()");
            try (ResultSet rs = pst.executeQuery()) {
                rs.next();
                now = rs.getTimestamp(1).toLocalDateTime();
            }
        } finally {
            closeStatement(pst);
        }
        return now;
    }

    protected boolean changedItem(Connection con, String sql, BaseData data) {
        if (data.isNew()) {
            return true;
        }
        PreparedStatement pst = null;
        ResultSet rs;
        boolean result = true;
        try {
            pst = con.prepareStatement(sql);
            pst.setInt(1, data.getId());
            rs = pst.executeQuery();
            if (rs.next()) {
                LocalDateTime date = rs.getTimestamp(1).toLocalDateTime();
                rs.close();
                result = !date.equals(data.getChangeDate());
            }
        } catch (Exception ignored) {
        } finally {
            closeStatement(pst);
        }
        return result;
    }

    public boolean deleteItem(String sql, int id) {
        Connection con = getConnection();
        PreparedStatement pst = null;
        try {
            pst = con.prepareStatement(sql);
            pst.setInt(1, id);
            pst.executeUpdate();
            return true;
        } catch (SQLException se) {
            Log.error("sql error", se);
            return false;
        } finally {
            closeStatement(pst);
            closeConnection(con);
        }
    }

    public byte[] getImageBytes(String fileName) {
        byte[] data = null;
        String path = ApplicationPath.getAppROOTPath() + "/static-content/img/"+fileName;
        File file=new File(path);
        if (file.exists()) {
            data= FileUtil.readBinaryFile(file);
        }
        return data;
    }

}
