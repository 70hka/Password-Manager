package PasswordManagerProject.crypt;

import javax.crypto.Cipher;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.security.SecureRandom;
import java.security.spec.KeySpec;
import java.util.Base64;

public class EncryptionManager {

    private static final String SALT_FILE = "salt.dat";
    private static final String ALGORITHM = "AES/CBC/PKCS5Padding";

    /**
     * Returns existing salt or creates one if missing.
     */
    public static byte[] getOrCreateSalt() throws IOException {
        File file = new File(SALT_FILE);
        if (file.exists()) {
            return readFile(file);
        }
        // Create new salt
        byte[] salt = new byte[16];
        new SecureRandom().nextBytes(salt);
        try (FileOutputStream fos = new FileOutputStream(file)) {
            fos.write(salt);
        }
        return salt;
    }

    /**
     * Derives AES key from PIN + salt.
     */
    public static SecretKeySpec deriveKey(String pin, byte[] salt) throws Exception {
        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
        KeySpec spec = new PBEKeySpec(pin.toCharArray(), salt, 65536, 256);
        byte[] key = factory.generateSecret(spec).getEncoded();
        return new SecretKeySpec(key, "AES");
    }

    /**
     * Encrypts plaintext.
     */
    public static String encrypt(String data, SecretKeySpec key) throws Exception {
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        byte[] iv = new byte[16];
        new SecureRandom().nextBytes(iv);
        cipher.init(Cipher.ENCRYPT_MODE, key, new IvParameterSpec(iv));
        byte[] encrypted = cipher.doFinal(data.getBytes());
        byte[] combined = new byte[iv.length + encrypted.length];
        System.arraycopy(iv, 0, combined, 0, iv.length);
        System.arraycopy(encrypted, 0, combined, iv.length, encrypted.length);
        return Base64.getEncoder().encodeToString(combined);
    }

    /**
     * Decrypts encrypted text.
     */
    public static String decrypt(String encryptedData, SecretKeySpec key) throws Exception {
        byte[] combined = Base64.getDecoder().decode(encryptedData);
        byte[] iv = new byte[16];
        byte[] encrypted = new byte[combined.length - 16];
        System.arraycopy(combined, 0, iv, 0, 16);
        System.arraycopy(combined, 16, encrypted, 0, encrypted.length);
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, key, new IvParameterSpec(iv));
        byte[] decrypted = cipher.doFinal(encrypted);
        return new String(decrypted);
    }

    /**
     * Reads bytes from file.
     */
    private static byte[] readFile(File file) throws IOException {
        try (FileInputStream fis = new FileInputStream(file)) {
            return fis.readAllBytes();
        }
    }
}
