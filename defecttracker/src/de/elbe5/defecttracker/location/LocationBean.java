/*
 Elbe 5 CMS - A Java based modular Content Management System
 Copyright (C) 2009-2020 Michael Roennau

 This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
 This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.elbe5.defecttracker.location;

import de.elbe5.content.ContentBean;
import de.elbe5.content.ContentData;
import de.elbe5.file.ImageBean;

import java.sql.*;
import java.time.LocalDateTime;

public class LocationBean extends ContentBean {

    private static LocationBean instance = null;

    public static LocationBean getInstance() {
        if (instance == null) {
            instance = new LocationBean();
        }
        return instance;
    }

    private static String GET_CONTENT_EXTRAS_SQL = "SELECT project_id FROM t_location WHERE id=?";

    @Override
    public void readContentExtras(Connection con, ContentData contentData) throws SQLException {
        if (!(contentData instanceof LocationData))
            return;
        LocationData data = (LocationData) contentData;
        PreparedStatement pst = null;
        try {
            pst = con.prepareStatement(GET_CONTENT_EXTRAS_SQL);
            pst.setInt(1, data.getId());
            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    int i = 1;
                    data.setProjectId(rs.getInt(i));
                }
            }
        } finally {
            closeStatement(pst);
        }
    }

    private static String INSERT_CONTENT_EXTRAS_SQL = "insert into t_location (project_id,id) values(?,?)";

    @Override
    public void createContentExtras(Connection con, ContentData contentData) throws SQLException {
        if (!contentData.isNew() || !(contentData instanceof LocationData))
            return;
        LocationData data = (LocationData) contentData;
        LocalDateTime now = getServerTime(con);
        data.setChangeDate(now);
        if (data.isNew()) {
            data.setCreationDate(now);
        }
        PreparedStatement pst = null;
        try {
            pst = con.prepareStatement(INSERT_CONTENT_EXTRAS_SQL);
            int i = 1;
            pst.setInt(i++, data.getProjectId());
            pst.setInt(i, data.getId());
            pst.executeUpdate();
            pst.close();
        } finally {
            closeStatement(pst);
        }
        if (data.getPlan()!=null)
            ImageBean.getInstance().saveFile(con,data.getPlan(),true);
    }

    @Override
    public void updateContentExtras(Connection con, ContentData contentData) throws SQLException{
        if (contentData.isNew() || !(contentData instanceof LocationData))
            return;
        LocationData data = (LocationData) contentData;
        if (data.getPlan()!=null)
            ImageBean.getInstance().saveFile(con,data.getPlan(),true);
    }

}
