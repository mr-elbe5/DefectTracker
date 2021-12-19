/*
 Elbe 5 CMS - A Java based modular Content Management System
 Copyright (C) 2009-2020 Michael Roennau

 This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
 This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.elbe5.defecttracker.defect;

import de.elbe5.file.FileData;
import de.elbe5.file.ImageBean;

import java.sql.*;

public class DefectCommentImageBean extends ImageBean {

    private static DefectCommentImageBean instance = null;

    public static DefectCommentImageBean getInstance() {
        if (instance == null) {
            instance = new DefectCommentImageBean();
        }
        return instance;
    }

    private static String GET_CONTENT_EXTRAS_SQL = "SELECT comment_id FROM t_defect_comment_image WHERE id=?";

    @Override
    public void readFileExtras(Connection con, FileData contentData, boolean complete) throws SQLException {
        super.readFileExtras(con,contentData,complete);
        if (!(contentData instanceof DefectCommentImageData))
            return;
        DefectCommentImageData data = (DefectCommentImageData) contentData;
        PreparedStatement pst = null;
        try {
            pst = con.prepareStatement(GET_CONTENT_EXTRAS_SQL);
            pst.setInt(1, data.getId());
            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    data.setCommentId(rs.getInt(1));
                }
            }
        } finally {
            closeStatement(pst);
        }
    }

    private static String INSERT_CONTENT_EXTRAS_SQL = "insert into t_defect_comment_image (comment_id,id) values(?,?)";
    private static String UPDATE_CONTENT_EXTRAS_SQL = "update t_defect_comment_image set comment_id=? where id=?";

    @Override
    public void writeFileExtras(Connection con, FileData contentData, boolean complete) throws SQLException {
        super.writeFileExtras(con,contentData,complete);
        if (!(contentData instanceof DefectCommentImageData))
            return;
        DefectCommentImageData data = (DefectCommentImageData) contentData;
        PreparedStatement pst;
        int i = 1;
        pst = con.prepareStatement(data.isNew() ? INSERT_CONTENT_EXTRAS_SQL : UPDATE_CONTENT_EXTRAS_SQL);
        pst.setInt(i++, data.getCommentId());
        pst.setInt(i, data.getId());
        pst.executeUpdate();
        pst.close();
    }

}
