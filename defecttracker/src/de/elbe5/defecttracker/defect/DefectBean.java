/*
 Elbe 5 CMS - A Java based modular Content Management System
 Copyright (C) 2009-2020 Michael Roennau

 This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
 This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.elbe5.defecttracker.defect;

import de.elbe5.content.ContentBean;
import de.elbe5.content.ContentData;
import de.elbe5.file.FileBean;
import de.elbe5.file.FileData;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class DefectBean extends ContentBean {

    private static DefectBean instance = null;

    public static DefectBean getInstance() {
        if (instance == null) {
            instance = new DefectBean();
        }
        return instance;
    }

    public int getNextDisplayId() {
        return getNextId("s_defect_id");
    }

    private static String GET_CONTENT_EXTRAS_SQL = "SELECT display_id,location_id,project_id,plan_id, assigned_id, lot, phase, state, " +
            "costs, position_x, position_y, position_comment, " +
            "due_date1, due_date2, close_date  " +
            "FROM t_defect  WHERE id=?";

    private static String GET_COMMENTS_SQL = "SELECT id, creation_date, creator_id, comment " +
            "from t_defect_comment where defect_id=? order by creation_date desc";

    @Override
    public void readContentExtras(Connection con, ContentData contentData) throws SQLException {
        if (!(contentData instanceof DefectData))
            return;
        DefectData data = (DefectData) contentData;
        PreparedStatement pst = null;
        PreparedStatement commentpst;
        try {
            pst = con.prepareStatement(GET_CONTENT_EXTRAS_SQL);
            commentpst=con.prepareStatement((GET_COMMENTS_SQL));
            pst.setInt(1, data.getId());
            commentpst.setInt(1, data.getId());
            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    int i=1;
                    data.setDisplayId(rs.getInt(i++));
                    data.setLocationId(rs.getInt(i++));
                    data.setProjectId(rs.getInt(i++));
                    data.setPlanId(rs.getInt(i++));
                    data.setAssignedId(rs.getInt(i++));
                    data.setLot(rs.getString(i++));
                    data.setPhase(rs.getString(i++));
                    data.setState(rs.getString(i++));
                    data.setCosts(rs.getInt(i++));
                    data.setPositionX(rs.getInt(i++));
                    data.setPositionY(rs.getInt(i++));
                    data.setPositionComment(rs.getString(i++));
                    Timestamp ts = rs.getTimestamp(i++);
                    data.setDueDate1(ts == null ? null : ts.toLocalDateTime().toLocalDate());
                    ts = rs.getTimestamp(i++);
                    data.setDueDate2(ts == null ? null : ts.toLocalDateTime().toLocalDate());
                    ts = rs.getTimestamp(i);
                    data.setCloseDate(ts == null ? null : ts.toLocalDateTime().toLocalDate());
                    data.getComments().addAll(readAllDefectComments(con,data.getId()));
                }
            }
        } finally {
            closeStatement(pst);
        }
    }

    private static String INSERT_CONTENT_EXTRAS_SQL = "insert into t_defect (" +
            "display_id,location_id,project_id,plan_id, assigned_id, lot, phase, " +
            "due_date1, state, costs, " +
            "position_x, position_y,position_comment,id) " +
            "values(?,?,?,?,?,?,?,?,?,?,?,?,?,?)";

    @Override
    public void createContentExtras(Connection con, ContentData contentData) throws SQLException {
        if (!contentData.isNew() || !(contentData instanceof DefectData))
            return;
        DefectData data = (DefectData) contentData;
        PreparedStatement pst = null;
        try {
            pst = con.prepareStatement(INSERT_CONTENT_EXTRAS_SQL);
            int i = 1;
            pst.setInt(i++,data.getDisplayId());
            pst.setInt(i++,data.getLocationId());
            pst.setInt(i++,data.getProjectId());
            pst.setInt(i++, data.getPlanId());
            pst.setInt(i++, data.getAssignedId());
            pst.setString(i++, data.getLot());
            pst.setString(i++, data.getPhase());
            LocalDate date=data.getDueDate1();
            if (date==null)
                pst.setNull(i++,Types.TIMESTAMP);
            else
                pst.setTimestamp(i++, Timestamp.valueOf(LocalDateTime.of(date,LocalTime.MIDNIGHT)));
            pst.setString(i++, data.getState());
            pst.setInt(i++, data.getCosts());
            pst.setInt(i++, data.getPositionX());
            pst.setInt(i++, data.getPositionY());
            pst.setString(i++, data.getPositionComment());
            pst.setInt(i, data.getId());
            pst.executeUpdate();
            pst.close();
        } finally {
            closeStatement(pst);
        }
        for (FileData file : data.getFiles()){
            FileBean.getInstance().saveFile(con, file, true);
        }
    }

    private static String UPDATE_CONTENT_EXTRAS_SQL = "update t_defect " +
            "set assigned_id=?, lot=?, due_date2=?, costs=? where id=? ";

    @Override
    public void updateContentExtras(Connection con, ContentData contentData) throws SQLException {
        if (!(contentData instanceof DefectData))
            return;
        DefectData data = (DefectData) contentData;
        PreparedStatement pst = null;
        try {
            pst = con.prepareStatement(UPDATE_CONTENT_EXTRAS_SQL);
            int i = 1;
            pst.setInt(i++, data.getAssignedId());
            pst.setString(i++, data.getLot());
            LocalDate date=data.getDueDate2();
            if (date==null)
                pst.setNull(i++,Types.TIMESTAMP);
            else
                pst.setTimestamp(i++, Timestamp.valueOf(LocalDateTime.of(date,LocalTime.MIDNIGHT)));
            pst.setInt(i++, data.getCosts());
            pst.setInt(i, data.getId());
            pst.executeUpdate();
            pst.close();
        } finally {
            closeStatement(pst);
        }
        for (FileData file : data.getFiles()){
            if (file.isNew()) {
                FileBean.getInstance().saveFile(con, file, true);
            }
        }
    }

    private static String UPDATE_CHANGE_SQL = "update t_content set change_date=?, changer_id=? where id=?";
    private static String CLOSE_DEFECT_SQL = "update t_defect set close_date=? where id=?";

    public boolean closeDefect(DefectData data) {
        Connection con = startTransaction();
        PreparedStatement pst = null;
        try {
            data.setChangeDate(getServerTime(con));
            pst = con.prepareStatement(UPDATE_CHANGE_SQL);
            Timestamp now=getTimestamp(con);
            pst.setTimestamp(1,now);
            pst.setInt(2, data.getChangerId());
            pst.setInt(3,data.getId());
            pst.executeUpdate();
            pst.close();
            pst = con.prepareStatement(CLOSE_DEFECT_SQL);
            int i = 1;
            LocalDate date=data.getCloseDate();
            pst.setTimestamp(i++, Timestamp.valueOf(LocalDateTime.of(date,LocalTime.MIDNIGHT)));
            pst.setInt(i, data.getId());
            pst.executeUpdate();
            pst.close();
            return commitTransaction(con);
        } catch (SQLException e){
            return rollbackTransaction(con);
        } finally {
            closeStatement(pst);
            closeConnection(con);
        }
    }

    // project Ids

    private static String GET_PROJECT_DEFECT_IDS_SQL = "SELECT id FROM t_defect  WHERE project_id=?";

    public List<Integer> getProjectDefectIds(int projectId) {
        Connection con = startTransaction();
        PreparedStatement pst = null;
        List<Integer> ids=new ArrayList<>();
        try {
            pst = con.prepareStatement(GET_PROJECT_DEFECT_IDS_SQL);
            pst.setInt(1,projectId);
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                ids.add(rs.getInt(1));
            }
            pst.close();
        } catch (SQLException e){
            e.printStackTrace();
        } finally {
            closeStatement(pst);
            closeConnection(con);
        }
        return ids;
    }

    private static String GET_LOCATION_DEFECT_IDS_SQL = "SELECT id FROM t_defect  WHERE location_id=?";

    public List<Integer> getLocationDefectIds(int locationId) {
        Connection con = startTransaction();
        PreparedStatement pst = null;
        List<Integer> ids=new ArrayList<>();
        try {
            pst = con.prepareStatement(GET_LOCATION_DEFECT_IDS_SQL);
            pst.setInt(1,locationId);
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                ids.add(rs.getInt(1));
            }
            pst.close();
        } catch (SQLException e){
            e.printStackTrace();
        } finally {
            closeStatement(pst);
            closeConnection(con);
        }
        return ids;
    }

    // comments

    public int getNextCommentId() {
        return getNextId("s_defect_comment_id");
    }

    public List<DefectCommentData> getAllDefectComments(int defectId) {
        List<DefectCommentData> comments = null;
        Connection con = getConnection();
        try {
            comments = readAllDefectComments(con, defectId);
        } catch (SQLException se) {
            se.printStackTrace();
        } finally {
            closeConnection(con);
        }
        return comments;
    }

    private static String READ_ALL_DEFECT_COMMENTS_SQL = "SELECT id, creation_date, creator_id, comment, state " +
            "FROM t_defect_comment WHERE defect_id=? order by id";

    public List<DefectCommentData> readAllDefectComments(Connection con, int defectId) throws SQLException {
        PreparedStatement pst = null;
        List<DefectCommentData> comments=new ArrayList<>();
        try {
            pst = con.prepareStatement(READ_ALL_DEFECT_COMMENTS_SQL);
            pst.setInt(1, defectId);
            try (ResultSet rs = pst.executeQuery()) {
                while (rs.next()) {
                    int i = 1;
                    DefectCommentData comment = new DefectCommentData();
                    comment.setId(rs.getInt(i++));
                    comment.setCreationDate(rs.getTimestamp(i++).toLocalDateTime());
                    comment.setDefectId(defectId);
                    comment.setCreatorId(rs.getInt(i++));
                    comment.setComment(rs.getString(i++));
                    comment.setState(rs.getString(i));
                    comments.add(comment);
                }
            }
        } finally {
            closeStatement(pst);
        }
        return comments;
    }

    public DefectCommentData getDefectComment(int commentId) {
        DefectCommentData comment = null;
        Connection con = getConnection();
        try {
            comment = readDefectComment(con, commentId);
        } catch (SQLException se) {
            se.printStackTrace();
        } finally {
            closeConnection(con);
        }
        return comment;
    }

    private static String READ_DEFECT_COMMENT_SQL = "SELECT defect_id, creation_date, creator_id, comment, state " +
            "FROM t_defect_comment WHERE id=? ";

    public DefectCommentData readDefectComment(Connection con, int commentId) throws SQLException {
        PreparedStatement pst = null;
        DefectCommentData comment=null;
        try {
            pst = con.prepareStatement(READ_DEFECT_COMMENT_SQL);
            pst.setInt(1, commentId);
            try (ResultSet rs = pst.executeQuery()) {
                while (rs.next()) {
                    int i = 1;
                    comment = new DefectCommentData();
                    comment.setId(commentId);
                    comment.setDefectId(rs.getInt(i++));
                    comment.setCreationDate(rs.getTimestamp(i++).toLocalDateTime());
                    comment.setCreatorId(rs.getInt(i++));
                    comment.setComment(rs.getString(i++));
                    comment.setState(rs.getString(i));
                }
            }
        } finally {
            closeStatement(pst);
        }
        return comment;
    }

    public boolean saveDefectComment(DefectCommentData data) {
        Connection con = startTransaction();
        try {
            if (!data.isNew()) {
                return rollbackTransaction(con);
            }
            data.setChangeDate(getServerTime(con));
            writeDefectComment(con, data);
            return commitTransaction(con);
        } catch (Exception se) {
            return rollbackTransaction(con, se);
        }
    }

    private static String UPDATE_DEFECT_STATE_SQL = "update t_defect set state=? where id=?";
    private static String INSERT_DEFECT_COMMENT_SQL = "insert into t_defect_comment (creation_date,defect_id,creator_id," +
            "comment,state,id) " +
            "values(?,?,?,?,?,?)";

    protected void writeDefectComment(Connection con, DefectCommentData data) throws SQLException {
        if (!data.isNew()) {
            return;
        }
        LocalDateTime now = getServerTime(con);
        data.setChangeDate(now);
        data.setCreationDate(now);
        PreparedStatement pst = null;
        try {
            pst = con.prepareStatement(UPDATE_DEFECT_STATE_SQL);
            pst.setString(1, data.getState());
            pst.setInt(2,data.getDefectId());
            pst.executeUpdate();
            pst.close();
            pst = con.prepareStatement(INSERT_DEFECT_COMMENT_SQL);
            int i = 1;
            pst.setTimestamp(i++, Timestamp.valueOf(data.getCreationDate()));
            pst.setInt(i++, data.getDefectId());
            pst.setInt(i++, data.getCreatorId());
            pst.setString(i++, data.getComment());
            pst.setString(i++, data.getState());
            pst.setInt(i, data.getId());
            pst.executeUpdate();
            pst.close();
        } finally {
            closeStatement(pst);
        }
    }

    private static String DELETE_DEFECT_COMMENT_SQL = "DELETE FROM t_cd_defect_comment WHERE id=?";

    public boolean deleteDefectComment(int id) {
        return deleteItem(DELETE_DEFECT_COMMENT_SQL, id);
    }

}
