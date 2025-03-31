package PasswordManagerProject.storage;

import PasswordManagerProject.model.Account;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.*;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class AccountStorage {
    private static final String FILE_PATH = "accounts.json";
    private static final Gson gson = new Gson();

    public static void saveAccounts(List<Account> accounts) {
        try (Writer writer = new FileWriter(FILE_PATH)) {
            gson.toJson(accounts, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static List<Account> loadAccounts() {
        try (Reader reader = new FileReader(FILE_PATH)) {
            Type accountListType = new TypeToken<List<Account>>() {}.getType();
            List<Account> loadedAccounts = gson.fromJson(reader, accountListType);
            return loadedAccounts != null ? loadedAccounts : new ArrayList<>(); // <-- Fix here
        } catch (FileNotFoundException e) {
            return new ArrayList<>(); // No file yet
        } catch (IOException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
}
