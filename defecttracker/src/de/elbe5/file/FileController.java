/*
 Elbe 5 CMS - A Java based modular File Management System
 Copyright (C) 2009-2020 Michael Roennau

 This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
 This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.elbe5.file;

import de.elbe5.base.cache.Strings;
import de.elbe5.base.data.Token;
import de.elbe5.content.ContentCache;
import de.elbe5.content.ContentData;
import de.elbe5.request.SessionRequestData;
import de.elbe5.servlet.Controller;
import de.elbe5.view.FileStreamView;
import de.elbe5.view.IView;
import de.elbe5.view.UrlView;

public abstract class FileController extends Controller {

    public IView show(SessionRequestData rdata) {
        FileData data;
        int id = rdata.getId();
        data = ContentCache.getFile(id);
        assert(data!=null);
        ContentData parent=ContentCache.getContent(data.getParentId());
        if (!parent.hasUserReadRight(rdata)) {
            String token = rdata.getString("token");
            checkRights(Token.matchToken(id, token));
        }
        return new FileStreamView(data, false);
    }

    public IView download(SessionRequestData rdata) {
        FileData data;
        int id = rdata.getId();
        data = ContentCache.getFile(id);
        ContentData parent=ContentCache.getContent(data.getParentId());
        if (!parent.hasUserReadRight(rdata)) {
            String token = rdata.getString("token");
            checkRights(Token.matchToken(id, token));
        }
        return new FileStreamView(data, true);
    }

    public IView deleteFile(SessionRequestData rdata) {
        int contentId = rdata.getId();
        int parentId = ContentCache.getFileParentId(contentId);
        ContentData parent=ContentCache.getContent(parentId);
        checkRights(parent.hasUserReadRight(rdata));
        FileBean.getInstance().deleteFile(contentId);
        ContentCache.setDirty();
        rdata.put("contentId", Integer.toString(parentId));
        rdata.setMessage(Strings.string("_fileDeleted",rdata.getLocale()), SessionRequestData.MESSAGE_TYPE_SUCCESS);
        return showContentAdministration(rdata,parentId);
    }

    protected IView showContentAdministration(SessionRequestData rdata, int contentId) {
        return new UrlView("/ctrl/admin/openContentAdministration?contentId=" + contentId);
    }

}
