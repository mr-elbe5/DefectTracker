/*
 Elbe 5 CMS - A Java based modular Content Management System
 Copyright (C) 2009-2018 Michael Roennau

 This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
 This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.elbe5.base.cache;

import de.elbe5.base.log.Log;
import de.elbe5.base.data.CsvFile;
import org.apache.commons.text.StringEscapeUtils;

import java.util.*;

public class Strings {

    public static Locale DEFAULT_LOCALE = Locale.ENGLISH;

    private static Map<Locale, Strings> cacheMap = new HashMap<>();

    public static boolean hasLocale(Locale locale) {
        return cacheMap.containsKey(locale);
    }

    private static Strings getLocalizedStrings(Locale locale) {
        if (!hasLocale(locale)) {
            cacheMap.put(locale, new Strings(locale));
        }
        return cacheMap.get(locale);
    }

    public static void readFromCsv(String filePath) {
        CsvFile file = new CsvFile(filePath);
        file.readFile();
        file.writeFile();
        CsvFile.CsvLine csvLine = file.getCsvLines().get(0);
        List<Strings> caches = new ArrayList<>();
        for (String lang : csvLine.Values) {
            Strings cache = getLocalizedStrings(new Locale(lang));
            caches.add(cache);
        }
        for (int i = 1; i < file.getCsvLines().size(); i++) {
            csvLine = file.getCsvLines().get(i);
            for (int k = 0; k < csvLine.Values.size(); k++) {
                String value = csvLine.Values.get(k);
                caches.get(k).allStrings.put(csvLine.Key, value);
            }
        }
    }

    public static String string(String key, Locale locale) {
        Locale l = hasLocale(locale) ? locale : DEFAULT_LOCALE;
        String s = getLocalizedStrings(l).getSingleString(key);
        if (s == null) {
            Log.warn("resource string not found for key " + key + " of locale " + locale);
            return "...";
        }
        if (s.isEmpty()) {
            Log.warn("resource string is empty for key " + key + " of locale " + locale);
            return "..";
        }
        return s;
    }

    public static String string(String key) {
        return string(key, DEFAULT_LOCALE);
    }

    public static String html(String key, Locale locale) {
        return StringEscapeUtils.escapeHtml4(string(key, locale));
    }

    public static String htmlMultiline(String key, Locale locale) {
        return StringEscapeUtils.escapeHtml4(string(key, locale)).replaceAll("\\\\n", "<br/>");
    }

    public static String js(String key, Locale locale) {
        return StringEscapeUtils.escapeEcmaScript(string(key, locale));
    }

    public static String xml(String key, Locale locale) {
        return StringEscapeUtils.escapeXml11(string(key, locale));
    }

    public static String html(String key) {
        return html(key, DEFAULT_LOCALE);
    }

    protected Locale locale;
    private Map<String, String> allStrings;

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
