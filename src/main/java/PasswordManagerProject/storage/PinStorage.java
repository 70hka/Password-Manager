package PasswordManagerProject.storage;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class PinStorage {
    private static final String FILE_PATH = "pin.json";
    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    public static String loadPin() {
        try (Reader reader = new FileReader(FILE_PATH)) {
            Map<String, String> data = gson.fromJson(reader, Map.class);
            return data.get("pin");
        } catch (IOException e) {
            return null;
        }
    }

    public static void savePin(String pin) {
        try (Writer writer = new FileWriter(FILE_PATH)) {
            Map<String, String> data = new HashMap<>();
            data.put("pin", pin);
            gson.toJson(data, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static boolean pinExists() {
        File file = new File(FILE_PATH);
        if (!file.exists()) {
            return false;
        }
        try (Reader reader = new FileReader(file)) {
            Map<String, String> data = gson.fromJson(reader, Map.class);
            return data != null && data.get("pin") != null && !data.get("pin").isEmpty();
        } catch (IOException e) {
            return false;
        }
    }

}
