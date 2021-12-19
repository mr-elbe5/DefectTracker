/*
 Elbe 5 CMS - A Java based modular Content Management System
 Copyright (C) 2009-2020 Michael Roennau

 This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
 This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.elbe5.file;

import de.elbe5.base.cache.Strings;
import de.elbe5.content.ContentCache;
import de.elbe5.content.ContentData;
import de.elbe5.request.RequestData;
import de.elbe5.request.SessionRequestData;
import de.elbe5.servlet.ControllerCache;
import de.elbe5.view.CloseDialogView;
import de.elbe5.view.IView;
import de.elbe5.view.UrlView;

public class DocumentController extends FileController {

    public static final String KEY = "document";

    private static DocumentController instance = null;

    public static void setInstance(DocumentController instance) {
        DocumentController.instance = instance;
    }

    public static DocumentController getInstance() {
        return instance;
    }

    public static void register(DocumentController controller){
        setInstance(controller);
        ControllerCache.addController(controller.getKey(),getInstance());
    }

    @Override
    public String getKey() {
        return KEY;
    }

    public IView openCreateDocument(SessionRequestData rdata) {
        int parentId = rdata.getInt("parentId");
        ContentData parentData = ContentCache.getContent(parentId);
        assert(parentData!=null);
        checkRights(parentData.hasUserEditRight(rdata));
        String type=rdata.getString("type");
        DocumentData data = FileFactory.getNewData(type,DocumentData.class);
        assert(data!=null);
        data.setCreateValues(parentData, rdata);
        rdata.setSessionObject(RequestData.KEY_DOCUMENT, data);
        return showEditDocument();
    }

    public IView openEditDocument(SessionRequestData rdata) {
        FileData data = FileBean.getInstance().getFile(rdata.getId(),true);
        ContentData parent=ContentCache.getContent(data.getParentId());
        checkRights(parent.hasUserEditRight(rdata));
        rdata.setSessionObject(RequestData.KEY_DOCUMENT,data);
        return showEditDocument();
    }

    public IView saveDocument(SessionRequestData rdata) {
        int contentId = rdata.getId();
        DocumentData data = rdata.getSessionObject(RequestData.KEY_DOCUMENT,DocumentData.class);
        assert(data != null && data.getId() == contentId);
        ContentData parent=ContentCache.getContent(data.getParentId());
        checkRights(parent.hasUserEditRight(rdata));
        data.readSettingsRequestData(rdata);
        if (!rdata.checkFormErrors()) {
            return showEditDocument();
        }
        data.setChangerId(rdata.getUserId());
        //bytes=null, if no new file selected
        if (!FileBean.getInstance().saveFile(data,data.isNew() || data.getBytes()!=null)) {
            setSaveError(rdata);
            return showEditDocument();
        }
        data.setNew(false);
        ContentCache.setDirty();
        rdata.setMessage(Strings.string("_fileSaved",rdata.getLocale()), SessionRequestData.MESSAGE_TYPE_SUCCESS);
        return new CloseDialogView("/ctrl/admin/openContentAdministration?contentId=" + data.getId());
    }

    public IView cutDocument(SessionRequestData rdata) {
        int contentId = rdata.getId();
        DocumentData data = FileBean.getInstance().getFile(contentId,true,DocumentData.class);
        assert(data!=null);
        ContentData parent=ContentCache.getContent(data.getParentId());
        checkRights(parent.hasUserEditRight(rdata));
        rdata.setClipboardData(RequestData.KEY_DOCUMENT, data);
        return showContentAdministration(rdata,data.getParentId());
    }

    public IView copyDocument(SessionRequestData rdata) {
        int contentId = rdata.getId();
        DocumentData data = FileBean.getInstance().getFile(contentId,true,DocumentData.class);
        assert(data!=null);
        ContentData parent=ContentCache.getContent(data.getParentId());
        checkRights(parent.hasUserEditRight(rdata));
        data.setNew(true);
        data.setId(FileBean.getInstance().getNextId());
        data.setCreatorId(rdata.getUserId());
        data.setChangerId(rdata.getUserId());
        rdata.setClipboardData(RequestData.KEY_DOCUMENT, data);
        return showContentAdministration(rdata,data.getId());
    }

    public IView pasteDocument(SessionRequestData rdata) {
        int parentId = rdata.getInt("parentId");
        ContentData parent=ContentCache.getContent(parentId);
        if (parent == null){
            rdata.setMessage(Strings.string("_actionNotExcecuted", rdata.getLocale()), SessionRequestData.MESSAGE_TYPE_ERROR);
            return showContentAdministration(rdata);
        }
        checkRights(parent.hasUserEditRight(rdata));
        DocumentData data=rdata.getClipboardData(RequestData.KEY_DOCUMENT,DocumentData.class);
        if (data==null){
            rdata.setMessage(Strings.string("_actionNotExcecuted", rdata.getLocale()), SessionRequestData.MESSAGE_TYPE_ERROR);
            return showContentAdministration(rdata);
        }
        data.setParentId(parentId);
        data.setParent(parent);
        data.setChangerId(rdata.getUserId());
        FileBean.getInstance().saveFile(data,true);
        rdata.clearClipboardData(RequestData.KEY_DOCUMENT);
        ContentCache.setDirty();
        rdata.setMessage(Strings.string("_documentPasted",rdata.getLocale()), SessionRequestData.MESSAGE_TYPE_SUCCESS);
        return showContentAdministration(rdata,data.getId());
    }

    public IView deleteDocument(SessionRequestData rdata){
        return deleteFile(rdata);
    }

    protected IView showEditDocument() {
        return new UrlView("/WEB-INF/_jsp/file/editDocument.ajax.jsp");
    }

}
