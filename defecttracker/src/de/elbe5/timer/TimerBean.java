/*
 Elbe 5 CMS - A Java based modular Content Management System
 Copyright (C) 2009-2020 Michael Roennau

 This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
 This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.elbe5.timer;

import de.elbe5.base.log.Log;
import de.elbe5.database.DbBean;

import java.sql.*;

public class TimerBean extends DbBean {

    private static TimerBean instance = null;

    public static TimerBean getInstance() {
        if (instance == null) {
            instance = new TimerBean();
        }
        return instance;
    }

    private static String READ_TASK_SQL = "SELECT display_name,execution_interval,day,hour,minute,active FROM t_timer_task WHERE name=?";

    public void readTimerTask(TimerTaskData task) {
        Connection con = getConnection();
        PreparedStatement pst = null;
        try {
            pst = con.prepareStatement(READ_TASK_SQL);
            pst.setString(1, task.getName());
            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    int i = 1;
                    task.setDisplayName(rs.getString(i++));
                    task.setInterval(TimerInterval.valueOf(rs.getString(i++)));
                    task.setDay(rs.getInt(i++));
                    task.setHour(rs.getInt(i++));
                    task.setMinute(rs.getInt(i++));
                    task.setActive(rs.getBoolean(i));
                }
            }
        } catch (SQLException se) {
            Log.error("sql error", se);
        } finally {
            closeStatement(pst);
            closeConnection(con);
        }
    }

    private static String UPDATE_TASK_SQL = "UPDATE t_timer_task SET execution_interval=?,day=?,hour=?,minute=?,active=? WHERE name=?";

    public void updateTaskData(TimerTaskData task) {
        Connection con = getConnection();
        PreparedStatement pst = null;
        try {
            pst = con.prepareStatement(UPDATE_TASK_SQL);
            int i = 1;
            pst.setString(i++, task.getInterval().name());
            pst.setInt(i++, task.getDay());
            pst.setInt(i++, task.getHour());
            pst.setInt(i++, task.getMinute());
            pst.setBoolean(i++, task.isActive());
            pst.setString(i, task.getName());
            pst.executeUpdate();
        } catch (SQLException se) {
            Log.error("sql error", se);
        } finally {
            closeStatement(pst);
            closeConnection(con);
        }
    }
}
