/*
 Elbe 5 CMS - A Java based modular Content Management System
 Copyright (C) 2009-2020 Michael Roennau

 This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
 This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.elbe5.file;

import de.elbe5.base.data.BinaryFile;
import de.elbe5.base.log.Log;
import de.elbe5.content.ContentCache;
import de.elbe5.content.ContentData;
import de.elbe5.request.ApiRequestData;
import de.elbe5.request.ResponseCode;
import de.elbe5.rights.Right;
import de.elbe5.rights.SystemZone;
import de.elbe5.servlet.ApiControllerCache;
import de.elbe5.user.UserData;
import de.elbe5.view.*;

import java.io.IOException;

public class ImageApiController extends FileApiController {

    public static final String KEY = "image";

    private static ImageApiController instance = null;

    public static void setInstance(ImageApiController instance) {
        ImageApiController.instance = instance;
    }

    public static ImageApiController getInstance() {
        return instance;
    }

    public static void register(ImageApiController controller){
        setInstance(controller);
        ApiControllerCache.addApiController(controller.getKey(),getInstance());
    }

    @Override
    public String getKey() {
        return KEY;
    }

    public IApiView download(ApiRequestData rdata) {
        //Log.info("downloading file");
        UserData user = rdata.getLoginUser();
        if (user==null)
            return new ApiResponseCodeView((ResponseCode.UNAUTHORIZED));
        int scalePercent = rdata.getInt("scale", 100);
        if (scalePercent==100)
            return super.download(rdata);
        ImageData data;
        int id = rdata.getId();
        data = ImageBean.getInstance().getFile(id, true, ImageData.class);
        if (data==null){
            Log.error("could not load image with id " + id);
            return new ApiResponseCodeView((ResponseCode.NOT_FOUND));
        }
        ContentData parent=ContentCache.getContent(data.getParentId());
        if (!user.hasSystemRight(SystemZone.CONTENTREAD) && !parent.hasUserRight(user, Right.READ)) {
            return new ApiResponseCodeView((ResponseCode.UNAUTHORIZED));
        }
        try {
            data.createScaledJpeg(scalePercent);
        }
        catch (IOException e){
            return new ApiResponseCodeView(ResponseCode.INTERNAL_SERVER_ERROR);
        }
        //Log.info("image size = "+data.getWidth()+","+data.getHeight());
        BinaryFile file=new BinaryFile();
        file.setFileName(data.getFileName());
        file.setBytes(data.getBytes());
        file.setContentType("image/jpeg");
        file.setFileSize(file.getBytes().length);
        return new ApiBinaryFileView(file);
    }

    public IApiView showPreview(ApiRequestData rdata) {
        //Log.info("downloading preview");
        int imageId = rdata.getId();
        ImageData data = ContentCache.getFile(imageId,ImageData.class);
        if (data==null){
            Log.error("could not load preview with id " + rdata.getId());
            return new ApiResponseCodeView((ResponseCode.NOT_FOUND));
        }
        return new ApiImagePreview(data);
    }

}
