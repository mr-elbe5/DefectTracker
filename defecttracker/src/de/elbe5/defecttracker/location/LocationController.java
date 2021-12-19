/*
 Elbe 5 CMS - A Java based modular Content Management System
 Copyright (C) 2009-2020 Michael Roennau

 This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
 This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.elbe5.defecttracker.location;

import de.elbe5.base.data.BinaryFile;
import de.elbe5.base.data.Token;
import de.elbe5.defecttracker.DefectBaseController;
import de.elbe5.defecttracker.ViewFilter;
import de.elbe5.defecttracker.defect.DefectData;
import de.elbe5.content.ContentCache;
import de.elbe5.file.ImageBean;
import de.elbe5.request.*;
import de.elbe5.servlet.ControllerCache;
import de.elbe5.view.BinaryFileView;
import de.elbe5.view.IView;
import de.elbe5.view.ResponseCodeView;

import java.util.List;

public class LocationController extends DefectBaseController {

    public static final String KEY = "location";

    private static LocationController instance = null;

    public static void setInstance(LocationController instance) {
        LocationController.instance = instance;
    }

    public static LocationController getInstance() {
        return instance;
    }

    public static void register(LocationController controller){
        setInstance(controller);
        ControllerCache.addController(controller.getKey(),getInstance());
    }

    @Override
    public String getKey() {
        return KEY;
    }

    public IView showDefectPlan(SessionRequestData rdata) {
        int id = rdata.getId();
        LocationData data= (LocationData) ContentCache.getContent(id);
        if (!data.hasUserReadRight(rdata)) {
            String token = rdata.getString("token");
            checkRights(Token.matchToken(id, token));
        }
        if (data.getPlan()==null)
            return new ResponseCodeView(ResponseCode.NOT_FOUND);
        PlanImageData plan = ImageBean.getInstance().getFile(data.getPlan().getId(),true,PlanImageData.class);
        byte[] redarrowBytes = LocationBean.getInstance().getImageBytes("redarrow.png");
        List<DefectData> defects = ViewFilter.getFilter(rdata).getLocationDefects(data.getId());
        BinaryFile file = plan.createLocationDefectPlan(redarrowBytes,defects,1);
        assert(file!=null);
        return new BinaryFileView(file);
    }

    public IView getReport(SessionRequestData rdata) {
        int contentId = rdata.getId();
        BinaryFile file = LocationPdfBean.getInstance().getLocationReport(contentId, rdata);
        assert(file!=null);
        BinaryFileView view=new BinaryFileView(file);
        view.setForceDownload(true);
        return view;
    }

}
