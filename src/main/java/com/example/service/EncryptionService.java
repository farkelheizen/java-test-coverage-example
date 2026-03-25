package com.example.service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

public class EncryptionService {

    public String hashPassword(String password, String salt) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] bytes = digest.digest((password + salt).getBytes(StandardCharsets.UTF_8));
            return toHex(bytes);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 not available", e);
        }
    }

    private String toHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }

    public String generateSalt() {
        SecureRandom random = new SecureRandom();
        byte[] saltBytes = new byte[8];
        random.nextBytes(saltBytes);
        return toHex(saltBytes);
    }

    public boolean validatePassword(String plaintext, String hash, String salt) {
        String recomputed = hashPassword(plaintext, salt);
        return recomputed.equals(hash);
    }

    public String maskSensitiveData(String data, int visibleChars) {
        if (data == null || data.length() <= visibleChars * 2) return data;
        String start = data.substring(0, visibleChars);
        String end = data.substring(data.length() - visibleChars);
        String mask = "*".repeat(data.length() - visibleChars * 2);
        return start + mask + end;
    }
}
