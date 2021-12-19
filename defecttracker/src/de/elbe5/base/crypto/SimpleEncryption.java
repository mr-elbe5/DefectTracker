/*
 Elbe 5 CMS - A Java based modular Content Management System
 Copyright (C) 2009-2018 Michael Roennau

 This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
 This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.elbe5.base.crypto;

public class SimpleEncryption {

    private static final String KEY = "d+qQgxugZU6Oocs6wAtvrA";
    private static String key = null;

    private static void ensureKey() {
        if (key == null) {
            key = AESEncryption.adjustKey(KEY);
        }
    }

    public static String encrypt(String s) {
        ensureKey();
        return AESEncryption.encryptString(s, key);
    }

    public static String decrypt(String s) {
        ensureKey();
        return AESEncryption.decryptString(s, key);
    }
}
