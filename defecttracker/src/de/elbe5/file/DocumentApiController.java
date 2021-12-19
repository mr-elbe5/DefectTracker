/*
 Elbe 5 CMS - A Java based modular File Management System
 Copyright (C) 2009-2020 Michael Roennau

 This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
 This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.elbe5.file;

import de.elbe5.servlet.ApiControllerCache;

public class DocumentApiController extends FileApiController {

    public static final String KEY = "document";

    private static DocumentApiController instance = null;

    public static void setInstance(DocumentApiController instance) {
        DocumentApiController.instance = instance;
    }

    public static DocumentApiController getInstance() {
        return instance;
    }

    public static void register(DocumentApiController controller){
        setInstance(controller);
        ApiControllerCache.addApiController(controller.getKey(),getInstance());
    }

    @Override
    public String getKey() {
        return KEY;
    }

}
