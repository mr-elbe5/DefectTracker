/*
 Elbe 5 CMS - A Java based modular Content Management System
 Copyright (C) 2009-2020 Michael Roennau

 This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
 This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.elbe5.content;

import de.elbe5.base.log.Log;
import de.elbe5.database.DbBean;
import de.elbe5.rights.Right;

import java.sql.*;
import java.util.*;

public class ContentBean extends DbBean {

    private static ContentBean instance = null;

    public static ContentBean getInstance() {
        if (instance == null) {
            instance = new ContentBean();
        }
        return instance;
    }

    public int getNextId() {
        return getNextId("s_content_id");
    }

    private static String CHANGED_SQL = "SELECT change_date FROM t_content WHERE id=?";

    public boolean changedContent(Connection con, ContentData data) {
        return changedItem(con, CHANGED_SQL, data);
    }

    private static String GET_ALL_CONTENT_SQL = "SELECT type,id,creation_date,change_date,parent_id,ranking,name,display_name,description,creator_id,changer_id, language, access_type, nav_type, active FROM t_content";
    public List<ContentData> getAllContents() {
        List<ContentData> list = new ArrayList<>();
        Connection con = getConnection();
        PreparedStatement pst = null;
        try {
            pst = con.prepareStatement(GET_ALL_CONTENT_SQL);
            try (ResultSet rs = pst.executeQuery()) {
                while (rs.next()) {
                    ContentData data=readContentData(rs);
                    if (data!=null) {
                        ContentBean extBean = ContentFactory.getBean(data.getType());
                        if (extBean != null)
                            extBean.readContentExtras(con, data);
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

    public ContentData getContent(int id) {
        ContentData data = null;
        Connection con = getConnection();
        try {
            data = readContent(con, id);
            ContentBean extBean = ContentFactory.getBean(data.getType());
            if (extBean != null)
                extBean.readContentExtras(con, data);
        } catch (SQLException se) {
            Log.error("sql error", se);
        } finally {
            closeConnection(con);
        }
        return data;
    }

    public <T extends ContentData> T getContent(int id, Class<T> cls) {
        try {
            return cls.cast(getContent(id));
        }
        catch(NullPointerException | ClassCastException e){
            return null;
        }
    }

    private static String GET_CONTENT_SQL = "SELECT type, id, creation_date, change_date, parent_id, ranking, name, display_name, description, creator_id, changer_id, access_type, language, nav_type, active FROM t_content WHERE id=?";

    public ContentData readContent(Connection con, int id) throws SQLException {
        ContentData data = null;
        PreparedStatement pst = null;
        try {
            pst = con.prepareStatement(GET_CONTENT_SQL);
            pst.setInt(1, id);
            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    data = readContentData(rs);
                    if (data!=null && data.hasIndividualAccess()) {
                        data.setGroupRights(getContentRights(con, data.getId()));
                    }
                }
            }
        } finally {
            closeStatement(pst);
        }
        return data;
    }

    public void readContentExtras(Connection con, ContentData contentData) throws SQLException{
    }

    private ContentData readContentData(ResultSet rs) throws SQLException{
        int i = 1;
        String type = rs.getString(i++);
        ContentData data = ContentFactory.getNewData(type);
        if (data != null) {
            data.setId(rs.getInt(i++));
            data.setCreationDate(rs.getTimestamp(i++).toLocalDateTime());
            data.setChangeDate(rs.getTimestamp(i++).toLocalDateTime());
            data.setParentId(rs.getInt(i++));
            data.setRanking(rs.getInt(i++));
            data.setName(rs.getString(i++));
            data.setDisplayName(rs.getString(i++));
            data.setDescription(rs.getString(i++));
            data.setCreatorId(rs.getInt(i++));
            data.setChangerId(rs.getInt(i++));
            data.setLanguage(rs.getString(i++));
            data.setAccessType(rs.getString(i++));
            data.setNavType(rs.getString(i++));
            data.setActive(rs.getBoolean(i));
        }
        return data;
    }

    private static String GET_CONTENT_RIGHTS_SQL = "SELECT group_id,value FROM t_content_right WHERE content_id=?";

    public Map<Integer, Right> getContentRights(Connection con, int contentId) throws SQLException {
        PreparedStatement pst = null;
        Map<Integer, Right> list = new HashMap<>();
        try {
            pst = con.prepareStatement(GET_CONTENT_RIGHTS_SQL);
            pst.setInt(1, contentId);
            try (ResultSet rs = pst.executeQuery()) {
                while (rs.next()) {
                    list.put(rs.getInt(1), Right.valueOf(rs.getString(2)));
                }
            }
        } finally {
            closeStatement(pst);
        }
        return list;
    }

    public List<ContentInfoData> getContentInfos() {
        List<ContentInfoData> list = new ArrayList<>();
        Connection con = getConnection();
        try {
            PreparedStatement pst;
            pst = con.prepareStatement("select id,name,display_name,description from t_content");
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                ContentInfoData info = new ContentInfoData();
                int i=1;
                info.setId(rs.getInt(i++));
                info.setName(rs.getString(i++));
                info.setDisplayName(rs.getString(i++));
                info.setDescription(rs.getString(i));
                list.add(info);
            }
        } catch (SQLException se) {
            Log.error("sql error", se);
        } finally {
            closeConnection(con);
        }
        return list;
    }

    public boolean saveContent(ContentData data) {
        Connection con = startTransaction();
        try {
            if (!data.isNew() && changedContent(con, data)) {
                return rollbackTransaction(con);
            }
            ContentBean extrasBean = ContentFactory.getBean(data.getType());
            data.setChangeDate(getServerTime(con));
            if (data.isNew()){
                data.setCreationDate(data.getChangeDate());
                createContent(con,data);
                if (extrasBean != null)
                    extrasBean.createContentExtras(con, data);
            }
            else{
                updateContent(con,data);
                if (extrasBean != null)
                    extrasBean.updateContentExtras(con, data);
            }
            return commitTransaction(con);
        } catch (Exception se) {
            return rollbackTransaction(con, se);
        }
    }

    private static String INSERT_CONTENT_SQL = "insert into t_content (type,creation_date,change_date,parent_id,ranking,name,display_name,description,creator_id,changer_id,language,access_type,nav_type,active,id) values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";

    protected void createContent(Connection con, ContentData data) throws SQLException {
        PreparedStatement pst = null;
        try {
            pst = con.prepareStatement(INSERT_CONTENT_SQL);
            int i = 1;
            pst.setString(i++, data.getClass().getSimpleName());
            pst.setTimestamp(i++, Timestamp.valueOf(data.getCreationDate()));
            pst.setTimestamp(i++, Timestamp.valueOf(data.getChangeDate()));
            if (data.getParentId() == 0) {
                pst.setNull(i++, Types.INTEGER);
            } else {
                pst.setInt(i++, data.getParentId());
            }
            pst.setInt(i++, data.getRanking());
            pst.setString(i++, data.getName());
            pst.setString(i++, data.getDisplayName());
            pst.setString(i++, data.getDescription());
            pst.setInt(i++, data.getCreatorId());
            pst.setInt(i++, data.getChangerId());
            pst.setString(i++, data.getLanguage());
            pst.setString(i++, data.getAccessType());
            pst.setString(i++, data.getNavType());
            pst.setBoolean(i++,data.isActive());
            pst.setInt(i, data.getId());
            pst.executeUpdate();
            pst.close();
        } finally {
            closeStatement(pst);
        }
    }

    private static String UPDATE_CONTENT_SQL = "update t_content set change_date=?,ranking=?,name=?,display_name=?,description=?,changer_id=?,language=?,access_type=?,nav_type=?,active=? where id=?";

    protected void updateContent(Connection con, ContentData data) throws SQLException {
        PreparedStatement pst = null;
        try {
            pst = con.prepareStatement(UPDATE_CONTENT_SQL);
            int i = 1;
            pst.setTimestamp(i++, Timestamp.valueOf(data.getChangeDate()));
            pst.setInt(i++, data.getRanking());
            pst.setString(i++, data.getName());
            pst.setString(i++, data.getDisplayName());
            pst.setString(i++, data.getDescription());
            pst.setInt(i++, data.getChangerId());
            pst.setString(i++, data.getLanguage());
            pst.setString(i++, data.getAccessType());
            pst.setString(i++, data.getNavType());
            pst.setBoolean(i++,data.isActive());
            pst.setInt(i, data.getId());
            pst.executeUpdate();
            pst.close();
        } finally {
            closeStatement(pst);
        }
    }

    public void createContentExtras(Connection con, ContentData contentData) throws SQLException{
    }

    public void updateContentExtras(Connection con, ContentData contentData) throws SQLException{
    }

    private static String UPDATE_RANKING_SQL = "UPDATE t_content SET ranking=? WHERE id=?";

    public void updateChildRankings(ContentData data) {
        Connection con = startTransaction();
        PreparedStatement pst = null;
        try {
            pst = con.prepareStatement(UPDATE_RANKING_SQL);
            for (int i = 0; i < data.getChildren().size(); i++) {
                int id = data.getChildren().get(i).getId();
                pst.setInt(1, i + 1);
                pst.setInt(2, id);
                pst.executeUpdate();
            }
            commitTransaction(con);
        } catch (Exception e){
            rollbackTransaction(con);
        } finally {
            closeStatement(pst);
            closeConnection(con);
        }
    }

    private static String GET_GROUP_RIGHT_SQL = "SELECT content_id,value FROM t_content_right WHERE group_id=?";

    public Map<Integer, Integer> getGroupRights(int groupId) {
        Connection con = getConnection();
        PreparedStatement pst = null;
        Map<Integer, Integer> map = new HashMap<>();
        try {
            pst = con.prepareStatement(GET_GROUP_RIGHT_SQL);
            pst.setInt(1, groupId);
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                map.put(rs.getInt(1), rs.getInt(2));
            }
            rs.close();
            return map;
        } catch (SQLException se) {
            Log.error("sql error", se);
        } finally {
            closeStatement(pst);
            closeConnection(con);
        }
        return null;
    }

    private static String DELETE_RIGHTS_SQL = "DELETE FROM t_content_right WHERE content_id=?";
    private static String INSERT_RIGHT_SQL = "INSERT INTO t_content_right (content_id,group_id,value) VALUES(?,?,?)";

    public boolean saveRights(ContentData data) {
        PreparedStatement pst = null;
        Connection con = startTransaction();
        try {
            pst = con.prepareStatement(DELETE_RIGHTS_SQL);
            pst.setInt(1, data.getId());
            pst.executeUpdate();
            if (data.hasIndividualAccess()) {
                pst.close();
                pst = con.prepareStatement(INSERT_RIGHT_SQL);
                pst.setInt(1, data.getId());
                for (int id : data.getGroupRights().keySet()) {
                    pst.setInt(2, id);
                    pst.setString(3, data.getGroupRights().get(id).name());
                    pst.executeUpdate();
                }
            }
            return commitTransaction(con);
        } catch (SQLException e){
            return rollbackTransaction(con);
        } finally {
            closeStatement(pst);
        }
    }

    private static String DELETE_SQL = "DELETE FROM t_content WHERE id=?";

    public boolean deleteContent(int id) {
        return deleteItem(DELETE_SQL, id);
    }

}
