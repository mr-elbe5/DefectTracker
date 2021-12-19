/*
 Elbe 5 CMS - A Java based modular Content Management System
 Copyright (C) 2009-2018 Michael Roennau

 This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
 This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.elbe5.base.crypto;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.Base64;

public class PBKDF2Encryption {

    private static final String RNG_ALGORITHM = "SHA1PRNG";
    private static final String HASH_ALGORITHM = "PBKDF2WithHmacSHA1";
    private static final int HASH_ITERATIONS = 22005;

    public static byte[] generateSalt() throws NoSuchAlgorithmException {
        SecureRandom random = SecureRandom.getInstance(RNG_ALGORITHM);
        byte[] salt = new byte[8];
        random.nextBytes(salt);
        return salt;
    }

    public static String generateSaltBase64() throws NoSuchAlgorithmException {
        return Base64.getEncoder().encodeToString(generateSalt());
    }

    public static byte[] getEncryptedPassword(String password, byte[] salt) throws NoSuchAlgorithmException, InvalidKeySpecException {
        int derivedKeyLength = 160;
        int iterations = HASH_ITERATIONS;
        KeySpec spec = new PBEKeySpec(password.toCharArray(), salt, iterations, derivedKeyLength);
        SecretKeyFactory f = SecretKeyFactory.getInstance(HASH_ALGORITHM);
        return f.generateSecret(spec).getEncoded();
    }

    public static String getEncryptedPasswordBase64(String password, String saltBase64) throws NoSuchAlgorithmException, InvalidKeySpecException {
        byte[] salt = Base64.getDecoder().decode(saltBase64);
        return Base64.getEncoder().encodeToString(getEncryptedPassword(password, salt));
    }

}
