/*
 Elbe 5 CMS - A Java based modular Content Management System
 Copyright (C) 2009-2018 Michael Roennau

 This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
 This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.elbe5.base.data;

import de.elbe5.base.log.Log;
import org.apache.commons.text.StringEscapeUtils;

import java.util.*;

public class Strings {

    private final static Map<String, String> stringMap = new HashMap<>();

    public static void addBundle(String name, Locale locale){
        ResourceBundle bundle = ResourceBundle.getBundle(name, locale);
        for (String key : bundle.keySet()){
            stringMap.put(key, bundle.getString(key));
        }
    }

    public static String string(String key) {
        try {
            String s = stringMap.get(key);
            if (s!=null)
                return s;
            else
                Log.warn("string not found: " + key);
        }
        catch (Exception e){
            Log.warn("string not found: " + key);
        }
        return "[" + key + "]";
    }

    public static String html(String key) {
        return StringEscapeUtils.escapeHtml4(string(key));
    }

    public static String htmlMultiline(String key) {
        return StringEscapeUtils.escapeHtml4(string(key)).replaceAll("\\\\n", "<br/>");
    }

    public static String js(String key) {
        return StringEscapeUtils.escapeEcmaScript(string(key));
    }

    public static String xml(String key) {
        return StringEscapeUtils.escapeXml11(string(key));
    }

    protected Locale locale;
    private final Map<String, String> allStrings;

    public Strings(Locale locale) {
        this.locale = locale;
        allStrings = new HashMap<>();
    }

    private String getSingleString(String key) {
        if (allStrings.containsKey(key))
            return allStrings.get(key);
        return null;
    }

}
