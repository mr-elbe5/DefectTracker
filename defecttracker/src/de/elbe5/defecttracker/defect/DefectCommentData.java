/*
 Elbe 5 CMS - A Java based modular Content Management System
 Copyright (C) 2009-2020 Michael Roennau

 This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
 This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.elbe5.defecttracker.defect;

import de.elbe5.base.cache.Strings;
import de.elbe5.base.data.BaseData;
import de.elbe5.base.util.DateUtil;
import de.elbe5.base.util.StringUtil;
import de.elbe5.file.FileData;
import de.elbe5.request.RequestData;
import de.elbe5.request.SessionRequestData;
import de.elbe5.user.UserCache;
import de.elbe5.user.UserData;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class DefectCommentData extends BaseData {

    public static final String KEY_COMMENT = "commentData";

    protected int defectId = 0;
    protected DefectData defect =null;
    protected String comment = "";
    protected String state="";

    protected List<FileData> files = new ArrayList<>();

    public String getCreatorName(){
        UserData user=UserCache.getUser(getCreatorId());
        if (user!=null)
            return user.getName();
        return "";
    }

    public int getDefectId() {
        return defectId;
    }

    public void setDefectId(int defectId) {
        this.defectId = defectId;
    }

    public DefectData getDefect() {
        return defect;
    }

    public void setDefect(DefectData defect) {
        this.defect = defect;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public List<FileData> getFiles() {
        return files;
    }

    public String geTitle(Locale locale){
        return Strings.string("_comment",locale)
                +" "+ Strings.string("_by",locale)
                +" "+ UserCache.getUser(getCreatorId()).getName()
                +" "+ Strings.string("_ofDate",locale)
                +" "+ StringUtil.toHtmlDateTime(getCreationDate(),locale);
    }

    public void setCreateValues(DefectData defect, int creatorId){
        setNew(true);
        setId(DefectBean.getInstance().getNextCommentId());
        setDefectId(defect.getId());
        setCreatorId(creatorId);
        setState(defect.getState());
    }

    public void readRequestData(SessionRequestData rdata) {
        setComment(rdata.getString("comment"));
        setState(rdata.getString("state"));
        if (getComment().isEmpty()) {
            rdata.addIncompleteField("comment");
        }
    }

    public void readApiRequestData(RequestData rdata) {
        setCreatorId(rdata.getInt("creatorId"));
        setState(rdata.getString("state"));
        setCreationDate(DateUtil.asLocalDateTime(rdata.getLong("creationDate")));
        setComment(rdata.getString("comment"));
    }

    @SuppressWarnings("unchecked")
    public JSONObject getJson(){
        JSONObject json = new JSONObject();
        json.put("id",getId());
        json.put("creationDate", DateUtil.asMillis(getCreationDate()));
        json.put("creatorId", getCreatorId());
        json.put("creatorName", getCreatorName());
        json.put("comment",getComment());
        json.put("state",getState());
        return json;
    }
}
