/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package de.elbe5.base.crypto.shiroutil;

/**
 * <p/>
 * Simple utility class for String operations useful across the framework.
 * <p/>
 * <p/>
 * Some methods in this class were copied from the Spring Framework so we didn't
 * have to re-invent the wheel, and in these cases, we have retained all
 * license, copyright and author information.
 *
 * @since 0.9
 */
public class StringUtils {

    /**
     * Check whether the given String has actual text. More specifically, returns
     * <code>true</code> if the string not <code>null</code>, its length is
     * greater than 0, and it contains at least one non-whitespace character.
     * <p/>
     * <code>StringUtils.hasText(null) == false<br/>
     * StringUtils.hasText("") == false<br/>
     * StringUtils.hasText(" ") == false<br/>
     * StringUtils.hasText("12345") == true<br/>
     * StringUtils.hasText(" 12345 ") == true</code>
     * <p/>
     * <p/>
     * Copied from the Spring Framework while retaining all license, copyright and
     * author information.
     *
     * @param str the String to check (may be <code>null</code>)
     * @return <code>true</code> if the String is not <code>null</code>, its
     * length is greater than 0, and it does not contain whitespace only
     * @see Character#isWhitespace
     */
    public static boolean hasText(String str) {
        if (!hasLength(str)) {
            return false;
        }
        int strLen = str.length();
        for (int i = 0; i < strLen; i++) {
            if (!Character.isWhitespace(str.charAt(i))) {
                return true;
            }
        }
        return false;
    }

    /**
     * Check that the given String is neither <code>null</code> nor of length 0.
     * Note: Will return <code>true</code> for a String that purely consists of
     * whitespace.
     * <p/>
     * <code>StringUtils.hasLength(null) == false<br/>
     * StringUtils.hasLength("") == false<br/>
     * StringUtils.hasLength(" ") == true<br/>
     * StringUtils.hasLength("Hello") == true</code>
     * <p/>
     * Copied from the Spring Framework while retaining all license, copyright and
     * author information.
     *
     * @param str the String to check (may be <code>null</code>)
     * @return <code>true</code> if the String is not null and has length
     * @see #hasText(String)
     */
    public static boolean hasLength(String str) {
        return (str != null && str.length() > 0);
    }
}
