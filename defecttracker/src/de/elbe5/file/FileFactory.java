/*
 Elbe 5 CMS - A Java based modular Content Management System
 Copyright (C) 2009-2020 Michael Roennau

 This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
 This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.elbe5.file;

import de.elbe5.base.log.Log;

import java.util.*;

public class FileFactory {

    private static List<String> defaultDocumentTypes=new ArrayList<>();
    private static List<String> defaultImageTypes=new ArrayList<>();
    private static Map<String, FileClassInfo> infos = new HashMap<>();

    public static List<String> getTypes() {
        List<String> list = new ArrayList<>(infos.keySet());
        Collections.sort(list);
        return list;
    }

    public static List<String> getDocumentTypes() {
        List<String> list = new ArrayList<>();
        for (FileClassInfo info : infos.values()){
            if (info instanceof DocumentClassInfo)
                list.add(info.getType());
        }
        Collections.sort(list);
        return list;
    }

    public static List<String> getImageTypes() {
        List<String> list = new ArrayList<>();
        for (FileClassInfo info : infos.values()){
            if (info instanceof ImageClassInfo)
                list.add(info.getType());
        }
        Collections.sort(list);
        return list;
    }

    public static void addDocumentClassInfo(Class<? extends DocumentData> fileClass, FileBean bean) {
        DocumentClassInfo fileClassInfo = new DocumentClassInfo(fileClass,bean);
        String type=fileClass.getSimpleName();
        infos.put(type,fileClassInfo);
    }

    public static void addImageClassInfo(Class<? extends ImageData> fileClass, FileBean bean) {
        ImageClassInfo fileClassInfo = new ImageClassInfo(fileClass,bean);
        String type=fileClass.getSimpleName();
        infos.put(type,fileClassInfo);
    }

    public static FileData getNewData(String type) {
        if (!infos.containsKey(type)) {
            Log.error("no file info for type "+type);
            return null;
        }
        return infos.get(type).getNewData();
    }

    public static <T extends FileData> T getNewData(String type, Class<T> cls) {
        if (!infos.containsKey(type)) {
            Log.error("no file info for type "+type);
            return null;
        }
        try{
            return cls.cast(infos.get(type).getNewData());
        }
        catch (Exception e){
            Log.error("no cast for type "+type);
        }
        return null;
    }

    public static FileBean getBean(String type){
        if (!infos.containsKey(type)) {
            Log.error("no file info for type "+type);
            return null;
        }
        return infos.get(type).getBean();
    }

    public static void addDefaultDocumentType(Class<? extends DocumentData> documentClass) {
        defaultDocumentTypes.add(documentClass.getSimpleName());
    }

    public static List<String> getDefaultDocumentTypes() {
        return defaultDocumentTypes;
    }

    public static void addDefaultImageType(Class<? extends ImageData> imageClass) {
        defaultImageTypes.add(imageClass.getSimpleName());
    }

    public static List<String> getDefaultImageTypes() {
        return defaultImageTypes;
    }
}
