/*
 Elbe 5 CMS - A Java based modular Content Management System
 Copyright (C) 2009-2018 Michael Roennau

 This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
 This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.elbe5.base.data;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Token {

    private static Map<Integer, Token> map = new HashMap<>();

    public static String createToken(int id) {
        Token token = new Token(id, UUID.randomUUID().toString());
        map.put(id, token);
        return token.getToken();
    }

    public static boolean matchToken(int id, String s) {
        if (!map.containsKey(id))
            return false;
        Token token = map.get(id);
        if (!token.getToken().equals(s))
            return false;
        map.remove(id);
        return LocalDateTime.now().minusSeconds(30).isBefore(token.getTime());
    }

    public static void cleanup(){
        LocalDateTime expireTime=LocalDateTime.now().minusSeconds(300);
        for (int id : map.keySet()){
            Token token=map.get(id);
            if (token.getTime().isBefore(expireTime))
                map.remove(id);
        }
    }

    private int id;
    private String token;
    private LocalDateTime time;

    public Token(int id, String token) {
        this.id = id;
        this.token = token;
        time = LocalDateTime.now();
    }

    public int getId() {
        return id;
    }

    public String getToken() {
        return token;
    }

    public LocalDateTime getTime() {
        return time;
    }
}
