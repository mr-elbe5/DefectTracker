/*
 Elbe 5 CMS - A Java based modular Content Management System
 Copyright (C) 2009-2020 Michael Roennau

 This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
 This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.elbe5.defecttracker.location;

import de.elbe5.base.data.BinaryFile;
import de.elbe5.base.log.Log;
import de.elbe5.content.ContentCache;
import de.elbe5.defecttracker.BaseApiController;
import de.elbe5.defecttracker.ViewFilter;
import de.elbe5.defecttracker.defect.DefectData;
import de.elbe5.file.ImageBean;
import de.elbe5.request.ApiRequestData;
import de.elbe5.request.ResponseCode;
import de.elbe5.rights.Right;
import de.elbe5.rights.SystemZone;
import de.elbe5.servlet.ApiControllerCache;
import de.elbe5.user.UserData;
import de.elbe5.view.ApiBinaryFileView;
import de.elbe5.view.ApiResponseCodeView;
import de.elbe5.view.IApiView;
import de.elbe5.view.JsonView;

import java.util.List;

public class LocationApiController extends BaseApiController {

    public static final String KEY = "location";

    private static LocationApiController instance = null;

    public static void setInstance(LocationApiController instance) {
        LocationApiController.instance = instance;
    }

    public static LocationApiController getInstance() {
        return instance;
    }

    public static void register(LocationApiController controller){
        setInstance(controller);
        ApiControllerCache.addApiController(controller.getKey(),getInstance());
    }

    @Override
    public String getKey() {
        return KEY;
    }

    @Deprecated
    public IApiView downloadLocationDefectPlan(ApiRequestData rdata) {
        UserData user = rdata.getLoginUser();
        if (user==null)
            return new ApiResponseCodeView(ResponseCode.UNAUTHORIZED);
        Log.info("loading location defect plan");
        int scalePercent = rdata.getInt("scale", 100);
        boolean isEditor = user.hasSystemRight(SystemZone.CONTENTEDIT);
        int id = rdata.getId();
        LocationData data= (LocationData) ContentCache.getContent(id);
        ViewFilter filter = new ViewFilter();
        filter.setEditor(isEditor);
        filter.setCurrentUserId(user.getId());
        if (!data.hasUserReadRight(filter)) {
            Log.error("plan is null");
            return new ApiResponseCodeView(ResponseCode.NOT_FOUND);
        }
        if (data.getPlan()==null)
            return new ApiResponseCodeView(ResponseCode.NOT_FOUND);
        PlanImageData plan = ImageBean.getInstance().getFile(data.getPlan().getId(),true,PlanImageData.class);
        byte[] redarrowBytes = LocationBean.getInstance().getImageBytes("redarrow.png");
        List<DefectData> defects = filter.getLocationDefects(data.getId());
        BinaryFile file = plan.createLocationDefectPlan(redarrowBytes,defects,((float)scalePercent)/100);
        if (file==null) {
            Log.error("file is null");
            return new ApiResponseCodeView(ResponseCode.NOT_FOUND);
        }
        return new ApiBinaryFileView(file);
    }

}
