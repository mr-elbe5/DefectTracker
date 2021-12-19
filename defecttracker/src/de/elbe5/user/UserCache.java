/*
 Elbe 5 CMS - A Java based modular Content Management System
 Copyright (C) 2009-2020 Michael Roennau

 This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
 This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.elbe5.user;

import java.util.*;

public class UserCache {

    private static UserCache instance = null;

    public static UserCache getInstance() {
        if (instance == null) {
            instance = new UserCache();
        }
        return instance;
    }

    private static int version = 1;
    private static boolean dirty = true;
    private static final Integer lockObj = 1;

    private static Map<Integer, UserData> userMap = new HashMap<>();

    public static synchronized void load() {
        UserBean bean = UserBean.getInstance();
        List<UserData> userList = bean.getAllUsers();
        Map<Integer, UserData> users = new HashMap<>();
        for (UserData user : userList) {
            users.put(user.getId(), user);
        }
        userMap = users;
    }

    public static void setDirty() {
        increaseVersion();
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

    public static void increaseVersion() {
        version++;
    }

    public static int getVersion() {
        return version;
    }

    public static UserData getUser(int id) {
        checkDirty();
        return userMap.get(id);
    }
}
