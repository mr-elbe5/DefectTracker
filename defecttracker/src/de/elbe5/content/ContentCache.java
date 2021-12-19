/*
 Elbe 5 CMS - A Java based modular Content Management System
 Copyright (C) 2009-2020 Michael Roennau

 This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
 This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.elbe5.content;

import de.elbe5.base.log.Log;
import de.elbe5.file.FileBean;
import de.elbe5.file.FileData;

import java.util.*;

public class ContentCache {

    private static ContentData contentRoot = null;
    private static int version = 1;
    private static boolean dirty = true;
    private static final Integer lockObj = 1;

    private static Map<Integer, ContentData> contentMap = new HashMap<>();
    private static Map<String, ContentData> pathMap = new HashMap<>();
    private static Map<Integer, FileData> fileMap = new HashMap<>();
    private static List<ContentData> footerList = new ArrayList<>();

    public static synchronized void load() {
        List<ContentData> contentList = ContentBean.getInstance().getAllContents();
        List<FileData> fileList = FileBean.getInstance().getAllFiles();
        Map<Integer, ContentData> contents = new HashMap<>();
        Map<String, ContentData> paths = new HashMap<>();
        List<ContentData> footer = new ArrayList<>();
        for (ContentData contentData : contentList) {
            contents.put(contentData.getId(), contentData);
        }
        Map<Integer, FileData> files = new HashMap<>();
        for (FileData fileData : fileList) {
            files.put(fileData.getId(), fileData);
            ContentData contentData=contents.get(fileData.getParentId());
            if (contentData!=null) {
                contentData.addFile(fileData);
                fileData.setParent(contentData);
            }
        }
        contentRoot = contents.get(ContentData.ID_ROOT);
        if (contentRoot == null)
            return;
        for (ContentData content : contentList) {
            ContentData parent = contents.get(content.getParentId());
            content.setParent(parent);
            if (parent != null) {
                parent.addChild(content);
            }
        }
        contentRoot.initializeChildren();
        for (ContentData contentData : contentList) {
            paths.put(contentData.getUrl(), contentData);
            if (contentData.isInFooterNav()){
                footer.add(contentData);
            }
        }
        Collections.sort(footer);
        fileMap = files;
        pathMap = paths;
        footerList=footer;
        contentMap = contents;
        Log.log("content cache reloaded");
    }

    public static void checkDirty() {
        if (dirty) {
            synchronized (lockObj) {
                if (dirty) {
                    load();
                    dirty = false;
                }
            }
        }
    }

    public static void setDirty() {
        increaseVersion();
        dirty=true;
    }

    public static void increaseVersion() {
        version++;
    }

    public static int getVersion() {
        return version;
    }

    public static ContentData getContentRoot() {
        checkDirty();
        return contentRoot;
    }

    public static ContentData getContent(int id) {
        checkDirty();
        return contentMap.get(id);
    }

    public static <T extends ContentData> T getContent(int id,Class<T> cls) {
        checkDirty();
        try {
            return cls.cast(contentMap.get(id));
        }
        catch(NullPointerException | ClassCastException e){
            return null;
        }
    }

    public static <T extends ContentData> List<T> getContents(Class<T> cls) {
        checkDirty();
        List<T> list = new ArrayList<>();
        try {
            for (ContentData data : contentMap.values()){
                if (cls.isInstance(data))
                    list.add(cls.cast(data));
            }
        }
        catch(NullPointerException | ClassCastException e){
            return list;
        }
        return list;
    }

    public static ContentData getContent(String url) {
        checkDirty();
        return pathMap.get(url);
    }

    public static <T extends ContentData> T getContent(String url,Class<T> cls) {
        checkDirty();
        try {
            return cls.cast(pathMap.get(url));
        }
        catch(NullPointerException | ClassCastException e){
            return null;
        }
    }

    public static int getParentContentId(int id) {
        checkDirty();
        ContentData contentData = getContent(id);
        if (contentData == null) {
            return 0;
        }
        return contentData.getParentId();
    }

    public static List<Integer> getParentContentIds(ContentData data) {
        checkDirty();
        List<Integer> list = new ArrayList<>();
        int id = data == null ? 0 : data.getId();
        if (id <= ContentData.ID_ROOT) {
            list.add(ContentData.ID_ROOT);
        }
        else {
            while (data!=null) {
                list.add(data.getId());
                data = data.getParent();
            }
        }
        return list;
    }

    public static List<ContentData> getFooterList() {
        return footerList;
    }

    public static FileData getFile(int id) {
        checkDirty();
        return fileMap.get(id);
    }

    public static <T extends FileData> T getFile(int id,Class<T> cls) {
        checkDirty();
        try {
            return cls.cast(fileMap.get(id));
        }
        catch(NullPointerException | ClassCastException e){
            return null;
        }
    }

    public static <T extends FileData> List<T> getFiles(Class<T> cls) {
        checkDirty();
        List<T> list = new ArrayList<>();
        try {
            for (FileData data : fileMap.values()){
                if (cls.isInstance(data))
                    list.add(cls.cast(data));
            }
        }
        catch(NullPointerException | ClassCastException e){
            return null;
        }
        return list;
    }

    public static int getFileParentId(int id) {
        checkDirty();
        FileData fileData = getFile(id);
        if (fileData == null) {
            return 0;
        }
        return fileData.getParentId();
    }

}
