package PasswordManagerProject.storage;

import PasswordManagerProject.model.Account;
import PasswordManagerProject.model.AccountData;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.*;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class AccountStorage {
    private static final String FILE_PATH = "accounts.json";
    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    public static AccountData loadData() {
        File file = new File(FILE_PATH);
        if (!file.exists()) {
            return new AccountData(null, new ArrayList<>());
        }
        try (Reader reader = new FileReader(FILE_PATH)) {
            AccountData data = gson.fromJson(reader, AccountData.class);
            if (data == null) { // <-- Fix for empty/corrupt file
                return new AccountData(null, new ArrayList<>());
            }
            if (data.getAccounts() == null) {
                data.setAccounts(new ArrayList<>());
            }
            return data;
        } catch (IOException e) {
            e.printStackTrace();
            return new AccountData(null, new ArrayList<>());
        }
    }

    public static void saveData(AccountData data) {
        try (Writer writer = new FileWriter(FILE_PATH)){
            gson.toJson(data, writer);
        } catch (IOException e){
            e.printStackTrace();
        }
    }
}
