package com.disain.main;

import java.io.*;
import java.security.*;
import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;

public class CryptoUtils {
    private static final String TRANSFORMATION = "AES";

    public static void saveKeyFile(String path, Key key) throws IOException {
        try(ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(path))) {
            oos.writeObject(key);
        }
    }

    public static Key readKeyFile(String path) throws IOException, ClassNotFoundException {
        try(ObjectInputStream ois = new ObjectInputStream(new FileInputStream(path))) {
            return (Key) ois.readObject();
        }
    }

    public static String toString(Key key) {
        return Base64.getEncoder().encodeToString(key.getEncoded());
    }

    public static Key generateKey() throws NoSuchAlgorithmException {
        KeyGenerator generator = KeyGenerator.getInstance("AES");
        generator.init(256);
        return generator.generateKey();
    }

    public static void encrypt(Key key, File inputFile, File outputFile) throws Exception {
        doCrypto(Cipher.ENCRYPT_MODE, key, inputFile, outputFile);
    }

    public static void decrypt(Key key, File inputFile, File outputFile) throws Exception {
        doCrypto(Cipher.DECRYPT_MODE, key, inputFile, outputFile);
    }

    private static void doCrypto(int cipherMode, Key secretKey, File inputFile, File outputFile) throws Exception {
        Cipher cipher = Cipher.getInstance(TRANSFORMATION);
        cipher.init(cipherMode, secretKey);

        try(
                FileInputStream inputStream = new FileInputStream(inputFile);
                FileOutputStream outputStream = new FileOutputStream(outputFile)
        ) {
            byte[] inputBuffer = new byte[1024 * 64];
            int length;

            while ((length = inputStream.read(inputBuffer)) != -1) {
                byte[] outputBuffer = cipher.update(inputBuffer, 0, length);
                if (outputBuffer != null)
                    outputStream.write(outputBuffer);
            }

            byte[] outputBuffer = cipher.doFinal();
            if (outputBuffer != null)
                outputStream.write(outputBuffer);
        }
    }
}
