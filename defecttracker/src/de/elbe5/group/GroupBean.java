/*
 Elbe 5 CMS - A Java based modular Content Management System
 Copyright (C) 2009-2020 Michael Roennau

 This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
 This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.elbe5.group;

import de.elbe5.base.log.Log;
import de.elbe5.database.DbBean;
import de.elbe5.rights.SystemZone;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Class UserBean is the persistence class for users and groups. <br>
 * Usage:
 */
public class GroupBean extends DbBean {

    private static GroupBean instance = null;

    public static GroupBean getInstance() {
        if (instance == null) {
            instance = new GroupBean();
        }
        return instance;
    }

    public int getNextId() {
        return getNextId("s_group_id");
    }

    private static String CHANGED_SQL = "SELECT change_date FROM t_group WHERE id=?";

    protected boolean changedGroup(Connection con, GroupData data) {
        return changedItem(con, CHANGED_SQL, data);
    }

    public List<GroupData> getAllGroups() {
        List<GroupData> list = new ArrayList<>();
        Connection con = getConnection();
        PreparedStatement pst = null;
        GroupData data;
        try {
            pst = con.prepareStatement("SELECT id,name,notes FROM t_group ORDER BY name");
            try (ResultSet rs = pst.executeQuery()) {
                while (rs.next()) {
                    int i = 1;
                    data = new GroupData();
                    data.setId(rs.getInt(i++));
                    data.setName(rs.getString(i++));
                    data.setNotes(rs.getString(i));
                    readGroupUsers(con,data);
                    readGroupRights(con,data);
                    list.add(data);
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

    private static String GET_GROUP_SQL = "SELECT change_date,name,notes FROM t_group WHERE id=? ORDER BY name";

    public GroupData getGroup(int id) {
        Connection con = getConnection();
        PreparedStatement pst = null;
        ResultSet rs;
        GroupData data = null;
        try {
            pst = con.prepareStatement(GET_GROUP_SQL);
            pst.setInt(1, id);
            rs = pst.executeQuery();
            if (rs.next()) {
                int i = 1;
                data = new GroupData();
                data.setId(id);
                data.setChangeDate(rs.getTimestamp(i++).toLocalDateTime());
                data.setName(rs.getString(i++));
                data.setNotes(rs.getString(i));
                rs.close();
                readGroupUsers(con, data);
                readGroupRights(con, data);
            }
            rs.close();
        } catch (SQLException se) {
            Log.error("sql error", se);
        } finally {
            closeStatement(pst);
            closeConnection(con);
        }
        return data;
    }

    private static String READ_GROUPUSER_SQL = "SELECT user_id FROM t_user2group WHERE group_id=?";

    protected void readGroupUsers(Connection con, GroupData data) throws SQLException {
        PreparedStatement pst = null;
        try {
            pst = con.prepareStatement(READ_GROUPUSER_SQL);
            pst.setInt(1, data.getId());
            try (ResultSet rs = pst.executeQuery()) {
                data.getUserIds().clear();
                while (rs.next()) {
                    data.getUserIds().add(rs.getInt(1));
                }
            }
        } finally {
            closeStatement(pst);
        }
    }

    public boolean saveGroup(GroupData data) {
        Connection con = startTransaction();
        try {
            if (!data.isNew() && changedGroup(con, data)) {
                return rollbackTransaction(con);
            }
            data.setChangeDate(getServerTime(con));
            writeGroup(con, data);
            writeGroupRights(con, data);
            writeGroupUsers(con, data);
            writeGroupRights(con, data);
            return commitTransaction(con);
        } catch (Exception se) {
            return rollbackTransaction(con, se);
        }
    }

    private static String INSERT_GROUP_SQL = "insert into t_group (change_date,name,notes, id) values(?,?,?,?)";
    private static String UPDATE_GROUP_SQL = "update t_group set change_date=?,name=?,notes=? where id=?";

    protected void writeGroup(Connection con, GroupData data) throws SQLException {
        PreparedStatement pst = null;
        try {
            pst = con.prepareStatement(data.isNew() ? INSERT_GROUP_SQL : UPDATE_GROUP_SQL);
            int i = 1;
            pst.setTimestamp(i++, Timestamp.valueOf(data.getChangeDate()));
            pst.setString(i++, data.getName());
            pst.setString(i++, data.getNotes());
            pst.setInt(i, data.getId());
            pst.executeUpdate();
            pst.close();
            writeGroupUsers(con, data);
        } finally {
            closeStatement(pst);
        }
    }

    private static String DELETE_GROUPUSERS_SQL = "DELETE FROM t_user2group WHERE group_id=?";
    private static String INSERT_GROUPUSER_SQL = "INSERT INTO t_user2group (group_id,user_id) VALUES(?,?)";

    protected void writeGroupUsers(Connection con, GroupData data) throws SQLException {
        PreparedStatement pst = null;
        try {
            pst = con.prepareStatement(DELETE_GROUPUSERS_SQL);
            pst.setInt(1, data.getId());
            pst.execute();
            if (data.getUserIds() != null) {
                pst.close();
                pst = con.prepareStatement(INSERT_GROUPUSER_SQL);
                pst.setInt(1, data.getId());
                for (int userId : data.getUserIds()) {
                    pst.setInt(2, userId);
                    pst.executeUpdate();
                }
            }
        } finally {
            closeStatement(pst);
        }
    }

    private static String DELETE_GROUP_SQL = "DELETE FROM t_group WHERE id=?";

    public boolean deleteGroup(int id) {
        return deleteItem(DELETE_GROUP_SQL, id);
    }

    private static String GET_SYSTEM_RIGHT_SQL = "SELECT name FROM t_system_right WHERE group_id=?";

    public void readGroupRights(Connection con,GroupData data) {
        data.getSystemRights().clear();
        PreparedStatement pst = null;
        try {
            pst = con.prepareStatement(GET_SYSTEM_RIGHT_SQL);
            pst.setInt(1, data.getId());
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                data.addSystemRight(SystemZone.valueOf(rs.getString(1)));
            }
            rs.close();
        } catch (SQLException se) {
            Log.error("sql error", se);
        } finally {
            closeStatement(pst);
        }
    }

    private static String DELETE_SYSTEM_RIGHTS_SQL = "DELETE FROM t_system_right WHERE group_id=?";
    private static String INSERT_SYSTEM_RIGHT_SQL = "INSERT INTO t_system_right (name,group_id) VALUES(?,?)";

    public void writeGroupRights(Connection con, GroupData data) throws SQLException {
        PreparedStatement pst = null;
        try {
            pst = con.prepareStatement(DELETE_SYSTEM_RIGHTS_SQL);
            pst.setInt(1, data.getId());
            pst.executeUpdate();
            pst.close();
            pst = con.prepareStatement(INSERT_SYSTEM_RIGHT_SQL);
            pst.setInt(2, data.getId());
            for (SystemZone zone : data.getSystemRights()) {
                pst.setString(1, zone.name());
                pst.setInt(2, data.getId());
                pst.executeUpdate();
            }
        } finally {
            closeStatement(pst);
        }
    }
}
