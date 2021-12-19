/*
 Elbe 5 CMS - A Java based modular File Management System
 Copyright (C) 2009-2020 Michael Roennau

 This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
 This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.elbe5.file;

import de.elbe5.base.log.Log;
import de.elbe5.content.ContentCache;
import de.elbe5.content.ContentData;
import de.elbe5.request.ApiRequestData;
import de.elbe5.request.ResponseCode;
import de.elbe5.rights.Right;
import de.elbe5.rights.SystemZone;
import de.elbe5.servlet.ApiController;
import de.elbe5.user.UserData;
import de.elbe5.view.*;

public abstract class FileApiController extends ApiController {

    public IApiView download(ApiRequestData rdata) {
        //Log.info("loading file");
        UserData user = rdata.getLoginUser();
        if (user==null)
            return new ApiResponseCodeView((ResponseCode.UNAUTHORIZED));
        FileData data;
        int id = rdata.getId();
        data = ContentCache.getFile(id);
        if (data==null){
            Log.error("could not load file with id " + id);
            return new ApiResponseCodeView((ResponseCode.NOT_FOUND));
        }
        ContentData parent=ContentCache.getContent(data.getParentId());
        if (!user.hasSystemRight(SystemZone.CONTENTREAD) && !parent.hasUserRight(user, Right.READ)) {
            return new ApiResponseCodeView((ResponseCode.UNAUTHORIZED));
        }
        return new ApiFileStreamView(data, false);
    }

}
