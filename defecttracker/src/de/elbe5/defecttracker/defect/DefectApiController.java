/*
 Elbe 5 CMS - A Java based modular Content Management System
 Copyright (C) 2009-2020 Michael Roennau

 This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
 This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.elbe5.defecttracker.defect;

import de.elbe5.base.data.BinaryFile;
import de.elbe5.base.log.Log;
import de.elbe5.content.ContentBean;
import de.elbe5.content.ContentCache;
import de.elbe5.content.ContentData;
import de.elbe5.defecttracker.BaseApiController;
import de.elbe5.defecttracker.location.LocationData;
import de.elbe5.file.ImageBean;
import de.elbe5.request.ApiRequestData;
import de.elbe5.request.ResponseCode;
import de.elbe5.rights.Right;
import de.elbe5.rights.SystemZone;
import de.elbe5.servlet.ApiControllerCache;
import de.elbe5.user.UserData;
import de.elbe5.view.*;

public class DefectApiController extends BaseApiController {

    public static final String KEY = "defect";

    private static DefectApiController instance = null;

    public static void setInstance(DefectApiController instance) {
        DefectApiController.instance = instance;
    }

    public static DefectApiController getInstance() {
        return instance;
    }

    public static void register(DefectApiController controller) {
        setInstance(controller);
        ApiControllerCache.addApiController(controller.getKey(), getInstance());
    }

    @Override
    public String getKey() {
        return KEY;
    }

    public IApiView uploadNewDefect(ApiRequestData rdata) {
        //Log.log("uploadNewDefect");
        UserData user = rdata.getLoginUser();
        if (user==null)
            return new ApiResponseCodeView(ResponseCode.UNAUTHORIZED);
        int locationId=rdata.getId();
        LocationData location=ContentCache.getContent(locationId, LocationData.class);
        if (location == null || !user.hasSystemRight(SystemZone.CONTENTREAD) && !location.hasUserRight(user, Right.READ)) {
            return new ApiResponseCodeView((ResponseCode.UNAUTHORIZED));
        }
        DefectData data = new DefectData();
        data.setCreateValues(location, rdata);
        data.readApiRequestData(rdata);
        //Log.log(data.getJson().toJSONString());
        if (!ContentBean.getInstance().saveContent(data)) {
            return new ApiResponseCodeView(ResponseCode.BAD_REQUEST);
        }
        data.setNew(false);
        data.setViewType(ContentData.VIEW_TYPE_SHOW);
        ContentCache.setDirty();
        return new JsonView(getIdJson(data.getId()).toJSONString());
    }

    public IApiView uploadNewDefectImage(ApiRequestData rdata) {
        //Log.log("uploadNewDefectImage");
        UserData user = rdata.getLoginUser();
        if (user == null)
            return new ApiResponseCodeView(ResponseCode.UNAUTHORIZED);
        int defectId = rdata.getId();
        DefectData defect=ContentCache.getContent(defectId, DefectData.class);
        assert(defect != null);
        BinaryFile file = rdata.getFile("file");
        assert(file!=null);
        DefectImageData image = new DefectImageData();
        image.setCreateValues(defect, rdata);
        if (!image.createFromBinaryFile(file, image.getMaxWidth(), image.getMaxHeight(), image.getMaxPreviewWidth(),image.getMaxPreviewHeight(), false)) {
            return new ApiResponseCodeView(ResponseCode.BAD_REQUEST);
        }
        image.setChangerId(rdata.getUserId());
        if (!ImageBean.getInstance().saveFile(image,true)) {
            return new ApiResponseCodeView(ResponseCode.BAD_REQUEST);
        }
        image.setNew(false);
        ContentCache.setDirty();
        return new JsonView(getIdJson(image.getId()).toJSONString());
    }

    public IApiView uploadNewComment(ApiRequestData rdata) {
        //Log.log("uploadNewComment");
        UserData user = rdata.getLoginUser();
        if (user == null)
            return new ApiResponseCodeView(ResponseCode.UNAUTHORIZED);
        int defectId = rdata.getId();
        DefectData defect = ContentCache.getContent(defectId, DefectData.class);
        if (defect == null || !user.hasSystemRight(SystemZone.CONTENTREAD) && !defect.hasUserRight(user, Right.READ)) {
            return new ApiResponseCodeView((ResponseCode.UNAUTHORIZED));
        }
        DefectCommentData data = new DefectCommentData();
        data.setCreateValues(defect, user.getId());
        data.readApiRequestData(rdata);
        if (!DefectBean.getInstance().saveDefectComment(data)) {
            return new ApiResponseCodeView(ResponseCode.BAD_REQUEST);
        }
        data.setNew(false);
        ContentCache.setDirty();
        return new JsonView(getIdJson(data.getId()).toJSONString());
    }

    public IApiView uploadNewCommentImage(ApiRequestData rdata) {
        //Log.log("uploadNewCommentImage");
        UserData user = rdata.getLoginUser();
        if (user == null)
            return new ApiResponseCodeView(ResponseCode.UNAUTHORIZED);
        int commentId = rdata.getId();
        DefectCommentData comment=DefectBean.getInstance().getDefectComment(commentId);
        assert(comment !=null);
        DefectData defect = ContentCache.getContent(comment.getDefectId(),DefectData.class);
        assert(defect !=null);
        BinaryFile file = rdata.getFile("file");
        assert(file!=null);
        DefectCommentImageData image = new DefectCommentImageData();
        image.setCreateValues(defect, rdata);
        if (!image.createFromBinaryFile(file, image.getMaxWidth(), image.getMaxHeight(), image.getMaxPreviewWidth(),image.getMaxPreviewHeight(), false)) {
            return new ApiResponseCodeView(ResponseCode.BAD_REQUEST);
        }
        image.setChangerId(rdata.getUserId());
        image.setCommentId(comment.getId());
        if (!ImageBean.getInstance().saveFile(image,true)) {
            return new ApiResponseCodeView(ResponseCode.BAD_REQUEST);
        }
        image.setNew(false);
        ContentCache.setDirty();
        return new JsonView(getIdJson(image.getId()).toJSONString());
    }

}
