/*
 Elbe 5 CMS - A Java based modular Content Management System
 Copyright (C) 2009-2018 Michael Roennau

 This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
 This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.elbe5.file;

import de.elbe5.base.data.BinaryFile;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BinaryFileCache {

    private static BinaryFileCache instance = null;

    public static BinaryFileCache getInstance() {
        if (instance == null) {
            instance = new BinaryFileCache();
        }
        return instance;
    }

    private static boolean dirty = true;
    private static final Integer lockObj = 1;
    private static String name = "";
    private static int maxCount = 100;
    private static long maxSize = 0;
    private static int cacheCount = 0;
    private static long cacheSize = 0;
    private static Map<Integer, BinaryFile> map = new HashMap<>();
    private static List<Integer> list = new ArrayList<>();

    public static void setDirty() {
        dirty = true;
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

    public static void load() {
        map.clear();
        list.clear();
        cacheCount = 0;
        cacheSize = 0;
    }

    public static String getName() {
        return name;
    }

    public static void setName(String name) {
        BinaryFileCache.name = name;
    }

    public static int getCacheCount() {
        return cacheCount;
    }

    public static int getMaxCount() {
        return maxCount;
    }

    public static long getMaxSize() {
        return maxSize;
    }

    public static long getCacheSize() {
        return cacheSize;
    }

    public static void setMaxCount(int maxCount) {
        if ((maxCount <= 0)) {
            return;
        }
        if ((maxCount < BinaryFileCache.maxCount)) {
            synchronized (lockObj) {
                while (cacheCount > maxCount) {
                    removeLeastUsed();
                }
            }
        }
        BinaryFileCache.maxCount = maxCount;
    }

    public static void setMaxSize(long maxSize) {
        if ((maxSize <= 0)) {
            return;
        }
        if ((maxSize < BinaryFileCache.maxSize)) {
            synchronized (lockObj) {
                while (cacheSize > maxSize) {
                    removeLeastUsed();
                }
            }
        }
        BinaryFileCache.maxSize = maxSize;
    }

    public static BinaryFile get(Integer key) {
        checkDirty();
        synchronized (lockObj) {
            BinaryFile data = map.get(key);
            if (data != null) {
                list.remove(key);
                list.add(key);
            }
            return data;
        }
    }

    public static void add(Integer key, BinaryFile data) {
        checkDirty();
        synchronized (lockObj) {
            if (!map.containsKey(key)) {
                if (cacheCount + 1 > maxCount) {
                    while (!map.isEmpty() && (cacheCount + 1 > maxCount)) {
                        removeLeastUsed();
                    }
                }
                cacheCount++;
                cacheSize += data.getFileSize();
                map.put(key, data);
            }
            list.remove(key);
            list.add(key);
        }
    }

    public static void remove(Integer key) {
        checkDirty();
        synchronized (lockObj) {
            BinaryFile data = map.get(key);
            list.remove(key);
            map.remove(key);
            if (data != null) {
                cacheCount--;
                cacheSize -= data.getFileSize();
            }
        }
    }

    private static void removeLeastUsed() {
        checkDirty();
        if (list.isEmpty()) {
            return;
        }
        Integer key = list.get(0);
        BinaryFile data = map.get(key);
        list.remove(0);
        cacheCount--;
        cacheSize -= data.getFileSize();
        map.remove(key);
    }

}
