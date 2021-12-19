/*
 Elbe 5 CMS - A Java based modular Content Management System
 Copyright (C) 2009-2020 Michael Roennau

 This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
 This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.elbe5.servlet;

import java.util.HashMap;
import java.util.Map;

public class ControllerCache {

    private static final Map<String, Controller> controllers = new HashMap<>();
    private static Controller defaultController = null;

    public static void setDefaultController(Controller defaultController) {
        ControllerCache.defaultController = defaultController;
    }

    public static void addController(String key, Controller controller) {
        assert(controller!=null);
        controllers.put(key, controller);
    }

    public static Controller getController(String key) {
        if (!controllers.containsKey(key)) {
            return defaultController;
        }
        return controllers.get(key);
    }

}
