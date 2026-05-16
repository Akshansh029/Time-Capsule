package com.akshansh.timecapsulebackend.service;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.HexFormat;

@Service
public class AesEncryptionService {

    private static final String ALGORITHM = "AES/GCM/NoPadding";
    private static final int GCM_TAG_LENGTH_BITS = 128;
    private static final int GCM_IV_LENGTH_BYTES = 12;

    private final SecureRandom secureRandom = new SecureRandom();

    @Value("${app.encryption.secret-key}")
    private String configuredSecretKey;

    private SecretKey secretKey;

    @PostConstruct
    void init() {
        byte[] keyBytes = decodeKey(configuredSecretKey);
        if (keyBytes.length != 16 && keyBytes.length != 24 && keyBytes.length != 32) {
            throw new IllegalStateException("app.encryption.secret-key must decode to 16, 24, or 32 bytes");
        }
        this.secretKey = new SecretKeySpec(keyBytes, "AES");
    }

    public EncryptedValue encrypt(String plainText) {
        if (plainText == null) {
            return new EncryptedValue(null, null);
        }

        try {
            byte[] iv = new byte[GCM_IV_LENGTH_BYTES];
            secureRandom.nextBytes(iv);

            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, secretKey, new GCMParameterSpec(GCM_TAG_LENGTH_BITS, iv));

            byte[] cipherText = cipher.doFinal(plainText.getBytes(StandardCharsets.UTF_8));
            return new EncryptedValue(
                    Base64.getEncoder().encodeToString(cipherText),
                    Base64.getEncoder().encodeToString(iv)
            );
        } catch (Exception ex) {
            throw new IllegalStateException("AES encryption failed", ex);
        }
    }

    public String decrypt(String cipherText, String encodedIv) {
        if (cipherText == null) {
            return null;
        }
        if (encodedIv == null || encodedIv.isBlank()) {
            throw new IllegalStateException("Cannot decrypt content because encryption IV is missing");
        }

        try {
            byte[] iv = Base64.getDecoder().decode(encodedIv);
            byte[] encryptedBytes = Base64.getDecoder().decode(cipherText);

            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, secretKey, new GCMParameterSpec(GCM_TAG_LENGTH_BITS, iv));

            return new String(cipher.doFinal(encryptedBytes), StandardCharsets.UTF_8);
        } catch (Exception ex) {
            throw new IllegalStateException("AES decryption failed", ex);
        }
    }

    private byte[] decodeKey(String value) {
        String trimmed = value == null ? "" : value.trim();
        if (trimmed.matches("(?i)^[0-9a-f]+$") && trimmed.length() % 2 == 0) {
            return HexFormat.of().parseHex(trimmed);
        }
        return Base64.getDecoder().decode(trimmed);
    }

    public record EncryptedValue(String cipherText, String iv) {
    }
}
