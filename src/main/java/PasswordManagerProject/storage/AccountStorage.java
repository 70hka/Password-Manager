package PasswordManagerProject.storage;

import PasswordManagerProject.crypt.EncryptionManager;
import PasswordManagerProject.model.AccountData;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import javax.crypto.spec.SecretKeySpec;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;

public class AccountStorage {
    private static final String FILE_PATH = "accounts.json";
    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    /**
     * Loads and decrypts account data.
     */
    public static AccountData loadData(String pin) {
        try {
            File file = new File(FILE_PATH);
            if (!file.exists() || file.length() == 0) {
                return new AccountData(null, new ArrayList<>());
            }

            byte[] salt = EncryptionManager.getOrCreateSalt();
            SecretKeySpec key = EncryptionManager.deriveKey(pin, salt);

            String encryptedContent = new String(Files.readAllBytes(Paths.get(FILE_PATH)));
            String decryptedJson = EncryptionManager.decrypt(encryptedContent, key);

            AccountData data = gson.fromJson(decryptedJson, AccountData.class);
            if (data.getAccounts() == null) {
                data.setAccounts(new ArrayList<>());
            }
            return data;
        } catch (Exception e) {
            System.err.println("❗ Failed to load account data: " + e.getMessage());
            return new AccountData(null, new ArrayList<>());
        }
    }

    /**
     * Encrypts and saves account data.
     */
    public static void saveData(AccountData data, String pin) {
        try {
            byte[] salt = EncryptionManager.getOrCreateSalt();
            SecretKeySpec key = EncryptionManager.deriveKey(pin, salt);

            String json = gson.toJson(data);
            String encryptedContent = EncryptionManager.encrypt(json, key);

            Files.write(Paths.get(FILE_PATH), encryptedContent.getBytes());
            System.out.println("✅ Account data saved & encrypted.");
        } catch (Exception e) {
            System.err.println("❗ Failed to save account data: " + e.getMessage());
        }
    }
}
