/*
 Elbe 5 CMS - A Java based modular Content Management System
 Copyright (C) 2009-2020 Michael Roennau

 This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
 This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.elbe5.user;

import de.elbe5.base.crypto.PBKDF2Encryption;
import de.elbe5.base.data.BinaryFile;
import de.elbe5.base.log.Log;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.Instant;
import java.util.Random;

public class UserSecurity {

    public static final String ALL_ALPHA_CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
    public static final String ALL_CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    public static final String UPPER_ALPHA_CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    public static final String UPPER_CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    public static final String LOWER_ALPHA_CHARS = "abcdefghijklmnopqrstuvwxyz";
    public static final String LOWER_CHARS = "abcdefghijklmnopqrstuvwxyz0123456789";
    public static final String NUMBERS = "0123456789";
    public static final String NON_ALPHA_CHARS = "!\"$%&/()[]{}=?#";
    public static final String UPPER_SIMPLE_CONSONANTS = "BCDFGKLMNPRSTVWXZ";
    public static final String LOWER_SIMPLE_CONSONANTS = "bcdfgklmnprstvwxz";
    public static final String UPPER_VOWELS = "AEIOU";
    public static final String LOWER_VOWELS = "aeiou";
    public static final String SMALLNUMBERS = "01234";

    public static String generateSimplePassword() {
        Random random = new Random();
        random.setSeed(Instant.now().toEpochMilli());
        char[] chars = new char[7];
        chars[0] = getRandomChar(UPPER_SIMPLE_CONSONANTS, random);
        chars[1] = getRandomChar(LOWER_VOWELS, random);
        chars[2] = getRandomChar(LOWER_SIMPLE_CONSONANTS, random);
        chars[3] = getRandomChar(LOWER_VOWELS, random);
        chars[4] = getRandomChar(UPPER_SIMPLE_CONSONANTS, random);
        chars[5] = getRandomChar(LOWER_VOWELS, random);
        chars[6] = getRandomChar(LOWER_SIMPLE_CONSONANTS, random);
        return new String(chars);
    }

    public static String generateCaptchaString() {
        Random random = new Random();
        random.setSeed(Instant.now().toEpochMilli());
        char[] chars = new char[6];
        for (int i = 0; i < 6; i++)
            chars[i] = getRandomChar(SMALLNUMBERS, random);
        return new String(chars);
    }

    public static int[] getCaptchaInts(String captchaString) {
        char[] chars = captchaString.toCharArray();
        if (chars.length != 6)
            return null;
        int[] ints = new int[6];
        for (int i = 0; i < 6; i++)
            ints[i] = chars[i] - 48;
        return ints;
    }

    public static String getApprovalString() {
        return getRandomString(8, UPPER_ALPHA_CHARS);
    }

    public static String getRandomString(int count, String sourceChars) {
        Random random = new Random();
        random.setSeed(Instant.now().toEpochMilli());
        char[] chars = new char[count];
        for (int i = 0; i < count; i++) {
            chars[i] = getRandomChar(sourceChars, random);
        }
        return new String(chars);
    }

    private static char getRandomChar(String chars, Random random) {
        return chars.charAt(random.nextInt(chars.length()));
    }

    private static int getRandomInt(String chars, Random random) {
        return chars.charAt(random.nextInt(chars.length()));
    }

    public static BinaryFile getCaptcha(String captcha) {
        int[] ints = getCaptchaInts(captcha);
        if (ints == null)
            return null;
        int width = 300;
        int height = 200;
        Color gradiantStartColor = new Color(60, 60, 60);
        Color gradiantEndColor = new Color(140, 140, 140);
        Color yellowColor = new Color(255, 255, 0);
        Color blueColor = new Color(127, 127, 255);
        Color redColor = new Color(255, 127, 127);
        BinaryFile data;
        BufferedImage bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = bufferedImage.createGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        GradientPaint gp = new GradientPaint(0, 0, gradiantStartColor, 0, height >> 1, gradiantEndColor, true);
        g2d.setPaint(gp);
        g2d.fillRect(0, 0, width, height);
        for (int i = 0; i < ints[0]; i++) {
            drawLine(g2d, blueColor, width, height);
        }
        for (int i = 0; i < ints[1]; i++) {
            drawLine(g2d, yellowColor, width, height);
        }
        for (int i = 0; i < ints[2]; i++) {
            drawLine(g2d, redColor, width, height);
        }
        for (int i = 0; i < ints[3]; i++) {
            drawCircle(g2d, blueColor, width, height);
        }
        for (int i = 0; i < ints[4]; i++) {
            drawCircle(g2d, yellowColor, width, height);
        }
        for (int i = 0; i < ints[5]; i++) {
            drawCircle(g2d, redColor, width, height);
        }
        g2d.dispose();
        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            data = new BinaryFile();
            data.setContentType("image/png");
            ImageIO.write(bufferedImage, "png", out);
            out.close();
            data.setBytes(out.toByteArray());
        } catch (IOException e) {
            return null;
        }
        return data;
    }

    private static void drawLine(Graphics2D g2d, Color col, int width, int height) {
        Random r = new Random();
        int x1 = 10 * r.nextInt(width / 10);
        int y1 = 10 * r.nextInt(height / 10);
        int x2 = 10 * r.nextInt(width / 10);
        int y2 = 10 * r.nextInt(height / 10);
        g2d.setColor(col);
        g2d.drawLine(x1, y1, x2, y2);
    }

    private static void drawCircle(Graphics2D g2d, Color col, int width, int height) {
        Random r = new Random();
        int x1 = width / 10 + r.nextInt(8 * width / 10);
        int y1 = height / 10 + r.nextInt(8 * height / 10);
        int radius = 10 + r.nextInt(height / 10);
        g2d.setColor(col);
        Shape circle = new Ellipse2D.Double(x1 - radius, y1 - radius, 2.0 * radius, 2.0 * radius);
        g2d.draw(circle);
    }

    public static String generateKey() {
        try {
            return PBKDF2Encryption.generateSaltBase64();
        } catch (Exception e) {
            Log.error("failed to create password key", e);
            return null;
        }
    }

    public static String encryptPassword(String pwd, String key) {
        try {
            return PBKDF2Encryption.getEncryptedPasswordBase64(pwd, key);
        } catch (Exception e) {
            Log.error("failed to encrypt password", e);
            return null;
        }
    }
}
