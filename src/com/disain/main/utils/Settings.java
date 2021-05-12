package com.disain.main.utils;

import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

public class Settings {
    private static final String version = "1.0.0";
    private static final Properties properties = new Properties();

    private static void init() throws IOException {
        properties.load(new FileReader("C:\\Cryptographer\\settings.ini"));

        if (properties.isEmpty()) {
            properties.put("SAVE_ORIGINAL_FILE", false);
            properties.put("USE_PASSWORD", false);
            properties.put("PASSWORD_HASH", "0");
            properties.put("BUFFER_SIZE", 64);
        }
    }

    public static boolean canSaveOriginalFile() {
        return (boolean) properties.get("SAVE_ORIGINAL_FILE");
    }

    public static void setSaveOriginalFile(boolean saveOriginalFile) {
        properties.setProperty("SAVE_ORIGINAL_FILE", String.valueOf(saveOriginalFile));
    }

    public static boolean canUsePassword() {
        return (boolean) properties.get("USE_PASSWORD");
    }

    public static void setUsePassword(boolean usePassword) {
        properties.setProperty("USE_PASSWORD", String.valueOf(usePassword));
    }

    public static String getPasswordHash() {
        return (String) properties.get("PASSWORD_HASH");
    }

    public static void setPasswordHash(String passwordHash) {
        properties.setProperty("PASSWORD_HASH", String.valueOf(passwordHash));
    }

    public static int getBufferSize() {
        return (int) properties.get("BUFFER_SIZE");
    }

    public static void setBufferSize(int bufferSize) {
        properties.setProperty("BUFFER_SIZE", String.valueOf(bufferSize));
    }

    public static String getVersion() {
        return version;
    }
}
