/*
 Elbe 5 CMS - A Java based modular Content Management System
 Copyright (C) 2009-2020 Michael Roennau

 This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
 This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.elbe5.defecttracker.defect;

import de.elbe5.base.cache.Strings;
import de.elbe5.base.data.BinaryFile;
import de.elbe5.base.data.Token;
import de.elbe5.defecttracker.DefectBaseController;
import de.elbe5.defecttracker.location.LocationData;
import de.elbe5.content.*;
import de.elbe5.defecttracker.location.PlanImageData;
import de.elbe5.file.FileBean;
import de.elbe5.request.*;
import de.elbe5.servlet.ControllerCache;
import de.elbe5.user.UserCache;
import de.elbe5.user.UserData;
import de.elbe5.view.*;

import java.util.List;

public class DefectController extends DefectBaseController {

    public static final String KEY = "defect";

    private static DefectController instance = null;

    public static void setInstance(DefectController instance) {
        DefectController.instance = instance;
    }

    public static DefectController getInstance() {
        return instance;
    }

    public static void register(DefectController controller){
        setInstance(controller);
        ControllerCache.addController(controller.getKey(),getInstance());
    }

    @Override
    public String getKey() {
        return KEY;
    }

    @Override
    public IView openCreateContentFrontend(SessionRequestData rdata) {
        int parentId=rdata.getInt("parentId");
        LocationData parent= (LocationData) ContentCache.getContent(parentId);
        checkRights(parent.hasUserAnyEditRight(rdata));
        DefectData data = new DefectData();
        data.setCreateValues(parent, rdata);
        data.setViewType(ContentData.VIEW_TYPE_EDIT);
        rdata.setCurrentSessionContent(data);
        return new ContentView(data);
    }

    @Override
    public IView openEditContentFrontend(SessionRequestData rdata) {
        int defectId=rdata.getId();
        DefectData data = ContentBean.getInstance().getContent(defectId,DefectData.class);
        checkRights(data.hasUserAnyEditRight(rdata));
        rdata.setCurrentSessionContent(data);
        data.setViewType(ContentData.VIEW_TYPE_EDIT);
        return new ContentView(data);
    }

    //frontend
    @Override
    public IView saveContentFrontend(SessionRequestData rdata) {
        int contentId=rdata.getId();
        DefectData data=rdata.getCurrentSessionContent(DefectData.class);
        assert(data != null && data.getId() == contentId);
        checkRights(data.hasUserAnyEditRight(rdata));
        if (data.isNew())
            data.readFrontendCreateRequestData(rdata);
        else
            data.readFrontendUpdateRequestData(rdata);
        if (!rdata.checkFormErrors()) {
            return new ContentView(data);
        }
        data.setChangerId(rdata.getUserId());
        if (!ContentBean.getInstance().saveContent(data)) {
            setSaveError(rdata);
            return new ContentView(data);
        }
        data.setNew(false);
        data.setViewType(ContentData.VIEW_TYPE_SHOW);
        ContentCache.setDirty();
        rdata.setMessage(Strings.string("_contentSaved",rdata.getLocale()), SessionRequestData.MESSAGE_TYPE_SUCCESS);
        return show(rdata);
    }

    public IView closeDefect(SessionRequestData rdata) {
        int contentId=rdata.getId();
        DefectData data = ContentBean.getInstance().getContent(contentId,DefectData.class);
        checkRights(data.hasUserAnyEditRight(rdata));
        data.setCloseDate(DefectBean.getInstance().getServerTime().toLocalDate());
        data.setChangerId(rdata.getUserId());
        if (!DefectBean.getInstance().closeDefect(data)) {
            setSaveError(rdata);
            return new ContentView(data);
        }
        data.setViewType(ContentData.VIEW_TYPE_SHOW);
        ContentCache.setDirty();
        rdata.setMessage(Strings.string("_defectClosed",rdata.getLocale()), SessionRequestData.MESSAGE_TYPE_SUCCESS);
        LocationData location = ContentCache.getContent(data.getLocationId(), LocationData.class);
        return new ContentView(location);
    }

    public IView showCroppedDefectPlan(SessionRequestData rdata) {
        int id = rdata.getId();
        DefectData data=ContentCache.getContent(id,DefectData.class);
        assert(data!=null);
        if (!data.hasUserReadRight(rdata)) {
            String token = rdata.getString("token");
            checkRights(Token.matchToken(id, token));
        }
        int x=rdata.getInt("x",data.getPositionX());
        int y=rdata.getInt("y",data.getPositionY());
        PlanImageData plan = FileBean.getInstance().getFile(data.getPlanId(),true,PlanImageData.class);
        byte[] redarrowBytes = FileBean.getInstance().getImageBytes("redarrow.png");
        BinaryFile file = plan.createCroppedDefectPlan(redarrowBytes,data.getDisplayId(),x,y);
        assert(file!=null);
        return new BinaryFileView(file);
    }

    public IView openFullDefectPlan(SessionRequestData rdata) {
        return new UrlView("/WEB-INF/_jsp/defecttracker/defect/defectPlan.ajax.jsp");
    }

    public IView showFullDefectPlan(SessionRequestData rdata) {
        int id = rdata.getId();
        DefectData data=ContentCache.getContent(id,DefectData.class);
        assert(data!=null);
        if (!data.hasUserReadRight(rdata)) {
            String token = rdata.getString("token");
            checkRights(Token.matchToken(id, token));
        }
        int x=rdata.getInt("x",data.getPositionX());
        int y=rdata.getInt("y",data.getPositionY());
        PlanImageData plan = FileBean.getInstance().getFile(data.getPlanId(),true,PlanImageData.class);
        byte[] redarrowBytes = FileBean.getInstance().getImageBytes("redarrow.png");
        BinaryFile file = plan.createFullDefectPlan(redarrowBytes,data.getDisplayId(),x,y);
        assert(file!=null);
        return new BinaryFileView(file);
    }

    public IView showDefectComment(SessionRequestData rdata) {
        int id = rdata.getId();
        if (!rdata.hasAnyContentRight()) {
            String token=rdata.getString("token");
            checkRights(Token.matchToken(id,token));
        }
        DefectCommentData defectComment = DefectBean.getInstance().getDefectComment(id);
        rdata.put(DefectCommentData.KEY_COMMENT,defectComment);
        return showDefectComment();
    }

    public IView openCreateDefectComment(SessionRequestData rdata) {
        int defectId=rdata.getId();
        DefectData defect=ContentCache.getContent(defectId,DefectData.class);
        assert(defect!=null);
        checkRights(defect.hasUserReadRight(rdata));
        rdata.setCurrentRequestContent(defect);
        DefectCommentData data = new DefectCommentData();
        data.setCreateValues(defect, rdata.getUserId());
        rdata.setSessionObject(DefectCommentData.KEY_COMMENT, data);
        return showCreateDefectComment();
    }

    public IView saveDefectComment(SessionRequestData rdata) {
        DefectData defectData = (DefectData) ContentCache.getContent(rdata.getId());
        checkRights(defectData.hasUserReadRight(rdata));
        DefectCommentData data = (DefectCommentData) rdata.getSessionObject(DefectCommentData.KEY_COMMENT);
        assert(data!=null);
        data.readRequestData(rdata);
        if (!rdata.checkFormErrors()) {
            return showCreateDefectComment();
        }
        if (!DefectBean.getInstance().saveDefectComment(data)) {
            setSaveError(rdata);
            return showCreateDefectComment();
        }
        List<BinaryFile> documents = rdata.getFileList("files");
        for (BinaryFile f : documents) {
            if (f.isImage()){
                DefectCommentImageData image = new DefectCommentImageData();
                image.setCreateValues(defectData, rdata);
                if (!image.createFromBinaryFile(f,image.getMaxWidth(),image.getMaxHeight(),image.getMaxPreviewWidth(),image.getMaxPreviewHeight(),false))
                    continue;
                image.setChangerId(rdata.getUserId());
                image.setCommentId(data.getId());
                FileBean.getInstance().saveFile(image,true);
            }
            else {
                DefectCommentDocumentData document = new DefectCommentDocumentData();
                document.setCreateValues(defectData, rdata);
                document.createFromBinaryFile(f);
                document.setChangerId(rdata.getUserId());
                document.setCommentId(data.getId());
                FileBean.getInstance().saveFile(document,true);
            }
        }
        rdata.setMessage(Strings.string("_defectCommentSaved",rdata.getLocale()), SessionRequestData.MESSAGE_TYPE_SUCCESS);
        defectData.getComments().add(data);
        ContentCache.setDirty();
        return new CloseDialogView("/ctrl/content/show/"+defectData.getId());
    }

    public IView getPdfFile(SessionRequestData rdata) {
        int contentId = rdata.getId();
        DefectData data= ContentCache.getContent(contentId,DefectData.class);
        assert(data!=null);
        BinaryFile file = DefectPdfBean.getInstance().getDefectPdfFile(data, rdata);
        assert(file!=null);
        BinaryFileView view=new BinaryFileView(file);
        view.setForceDownload(true);
        return view;
    }

    public IView getDocxFile(SessionRequestData rdata) {
        int contentId = rdata.getId();
        DefectData data= ContentCache.getContent(contentId,DefectData.class);
        assert(data!=null);
        UserData currentUser=UserCache.getUser(rdata.getUserId());
        UserData assignedUser = UserCache.getUser(data.getAssignedId());
        BinaryFile file = DefectDocxBean.getInstance().getDefectWordFile(data, currentUser, assignedUser, rdata.getLocale());
        assert(file!=null);
        BinaryFileView view=new BinaryFileView(file);
        view.setForceDownload(true);
        return view;
    }

    private IView showDefectComment() {
        return new UrlView("/WEB-INF/_jsp/defecttracker/defect/comment.ajax.jsp");
    }

    private IView showCreateDefectComment() {
        return new UrlView("/WEB-INF/_jsp/defecttracker/defect/createComment.ajax.jsp");
    }

}
