/*
 Elbe 5 CMS - A Java based modular Content Management System
 Copyright (C) 2009-2020 Michael Roennau

 This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
 This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.elbe5.defecttracker.project;

import de.elbe5.content.ContentBean;
import de.elbe5.content.ContentData;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ProjectBean extends ContentBean {

    private static ProjectBean instance = null;

    public static ProjectBean getInstance() {
        if (instance == null) {
            instance = new ProjectBean();
        }
        return instance;
    }
    private static final String GET_CONTENT_EXTRAS_SQL = "SELECT group_id,phase FROM t_project WHERE id=?";

    @Override
    public void readContentExtras(Connection con, ContentData contentData) throws SQLException {
        if (!(contentData instanceof ProjectData))
            return;
        ProjectData data = (ProjectData) contentData;
        PreparedStatement pst = null;
        try {
            pst = con.prepareStatement(GET_CONTENT_EXTRAS_SQL);
            pst.setInt(1, data.getId());
            ResultSet rs=pst.executeQuery();
            if (rs.next()){
                int i=1;
                data.setGroupId(rs.getInt(i++));
                data.setPhase(rs.getString(i));
            }
        } finally {
            closeStatement(pst);
        }
    }

    private static final String INSERT_CONTENT_EXTRAS_SQL = "insert into t_project (group_id, phase, id) values(?,?,?)";

    @Override
    public void createContentExtras(Connection con, ContentData contentData) throws SQLException {
        if (!(contentData instanceof ProjectData))
            return;
        ProjectData data = (ProjectData) contentData;
        PreparedStatement pst = null;
        try {
            pst = con.prepareStatement(INSERT_CONTENT_EXTRAS_SQL);
            int i=1;
            pst.setInt(i++, data.getGroupId());
            pst.setString(i++, data.getPhase());
            pst.setInt(i, data.getId());
            pst.executeUpdate();
            pst.close();
        } finally {
            closeStatement(pst);
        }
    }

    private static final String UPDATE_CONTENT_EXTRAS_SQL = "update t_project set group_id=? where id=?";

    @Override
    public void updateContentExtras(Connection con, ContentData contentData) throws SQLException {
        if (!(contentData instanceof ProjectData))
            return;
        ProjectData data = (ProjectData) contentData;
        PreparedStatement pst = null;
        try {
            pst = con.prepareStatement(UPDATE_CONTENT_EXTRAS_SQL);
            int i=1;
            pst.setInt(i++, data.getGroupId());
            pst.setInt(i, data.getId());
            pst.executeUpdate();
            pst.close();
        } finally {
            closeStatement(pst);
        }
    }

    private static final String GET_ALL_PROJECTIDS_SQL = "SELECT t1.id FROM t_project t1, t_content t2 where t1.id=t2.id and t2.active=true";
    private static final String GET_USER_PROJECTIDS_SQL = "SELECT distinct t1.id FROM t_project t1, t_user2group t2, t_content t3 WHERE t1.group_id=t2.group_id and t1.id=t3.id and t2.user_id=? and t3.active=true";

    public List<Integer> getUserProjectIds(int userId, boolean isEditor) {
        List<Integer> projectIds = new ArrayList<>();
        Connection con = getConnection();
        PreparedStatement pst = null;
        try {
            if (isEditor){
                pst = con.prepareStatement(GET_ALL_PROJECTIDS_SQL);
            }
            else {
                pst = con.prepareStatement(GET_USER_PROJECTIDS_SQL);
                pst.setInt(1, userId);
            }
            ResultSet rs=pst.executeQuery();
            while (rs.next()){
                projectIds.add(rs.getInt(1));
            }
        } catch (SQLException se) {
            se.printStackTrace();
        } finally {
            closeStatement(pst);
            closeConnection(con);
        }
        return projectIds;
    }

}
