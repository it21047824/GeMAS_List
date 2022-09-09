package com.example.gemaslist;

import java.math.BigInteger;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

public class Password {

    //password hash and salt code snippet from
    //https://howtodoinjava.com/java/java-security/how-to-generate-secure-password-hash-md5-sha-pbkdf2-bcrypt-examples/
    public static String hashPassword(String password) {
        int iterations = 1000;
        char[] chars = password.toCharArray();
        byte[] salt = getSalt();

        PBEKeySpec spec = new PBEKeySpec(chars, salt, iterations, 64*8);
        SecretKeyFactory skf;
        byte[] hash = null;
        try {
            skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
            hash = skf.generateSecret(spec).getEncoded();
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            e.printStackTrace();
        }

        return iterations+ ":" +toHex(salt)+ ":" +toHex(hash);
    }

    private static byte[] getSalt() {
        byte[] salt = new byte[16];
        try {
            SecureRandom sr = SecureRandom.getInstance("SHA1PRNG");
            sr.nextBytes(salt);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return salt;
    }

    private static String toHex(byte[] array) {
        BigInteger bigInt = new BigInteger(1, array);
        String hex = bigInt.toString(16);
        int paddingLength = (array.length*2) - hex.length();

        if(paddingLength>0) {
            return String.format("%0" +paddingLength+ "d",0) + hex;
        } else {
            return hex;
        }
    }

    private static byte[] fromHex(String hex) {
        byte[] bytes = new byte[hex.length()/2];
        for(int i=0; i< bytes.length; i++) {
            bytes[i] = (byte) Integer.parseInt(hex.substring(2*i, 2*i+2), 16);
        }
        return bytes;
    }

    public static boolean validatePassword(String inputPassword, String storedPassword) {
        String[] parts = storedPassword.split(":");
        int iterations = Integer.parseInt(parts[0]);

        byte[] salt = fromHex(parts[1]);
        byte[] hash = fromHex(parts[2]);

        PBEKeySpec spec = new PBEKeySpec(inputPassword.toCharArray(), salt, iterations, hash.length*8);
        SecretKeyFactory skf;
        byte[] testHash = null;

        try {
            skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
            testHash = skf.generateSecret(spec).getEncoded();
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            e.printStackTrace();
        }

        assert testHash != null;
        int diff = hash.length^testHash.length;
        for (int i=0; i<hash.length && i<testHash.length; i++) {
            diff |= hash[i]^testHash[i];
        }
        return diff == 0;
    }
    //end of password hash and salt code snippet

}
