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

    private static final String GET_CONTENT_EXTRAS_SQL = "SELECT project_id, approve_date FROM t_location WHERE id=?";

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
                    data.setProjectId(rs.getInt(i++));
                    Timestamp ts = rs.getTimestamp(i);
                    if (ts != null)
                        data.setApproveDateTime(ts.toLocalDateTime());
                    else
                        data.setApproveDate(null);
                }
            }
        } finally {
            closeStatement(pst);
        }
    }

    private static final String INSERT_CONTENT_EXTRAS_SQL = "insert into t_location (project_id,approve_date,id) values(?,?,?)";

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
            if (data.getApproveDate() != null)
                pst.setTimestamp(i++, Timestamp.valueOf(data.getApproveDateTime()));
            else
                pst.setNull(i++, Types.TIMESTAMP);
            pst.setInt(i, data.getId());
            pst.executeUpdate();
            pst.close();
        } finally {
            closeStatement(pst);
        }
        if (data.getPlan()!=null)
            ImageBean.getInstance().saveFile(con,data.getPlan(),true);
    }

    private static final String UPDATE_CONTENT_EXTRAS_SQL = "update t_location set approve_date = ? where id = ?";

    @Override
    public void updateContentExtras(Connection con, ContentData contentData) throws SQLException{
        if (contentData.isNew() || !(contentData instanceof LocationData))
            return;
        LocationData data = (LocationData) contentData;
        if (data.getPlan()!=null)
            ImageBean.getInstance().saveFile(con,data.getPlan(),true);
        PreparedStatement pst = null;
        try {
            pst = con.prepareStatement(UPDATE_CONTENT_EXTRAS_SQL);
            int i = 1;
            if (data.getApproveDate() != null)
                pst.setTimestamp(i++, Timestamp.valueOf(data.getApproveDateTime()));
            else
                pst.setNull(i++, Types.TIMESTAMP);
            pst.setInt(i, data.getId());
            pst.executeUpdate();
            pst.close();
        } finally {
            closeStatement(pst);
        }
    }

}
