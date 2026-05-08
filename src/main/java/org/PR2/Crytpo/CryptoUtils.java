package org.PR2.Crytpo;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.SecureRandom;
import java.security.spec.KeySpec;

public class CryptoUtils {

    // Algoritmo de cifrado: AES en modo CBC con padding PKCS5
    private static final String ALGORITHM = "AES/CBC/PKCS5Padding";
    private static final String KEY_ALGORITHM = "AES";

    // Salt para derivar la clave (16 bytes)
    private static final int SALT_LENGTH = 16;
    // Vector de inicialización (16 bytes para AES)
    private static final int IV_LENGTH = 16;
    // Iteraciones para derivación de clave
    private static final int ITERATION_COUNT = 65536;
    // Longitud de la clave (256 bits = 32 bytes)
    private static final int KEY_LENGTH = 256;

    // Contraseña para cifrar/descifrar (en un caso real, sería más segura)
    private static final String PASSWORD = "MiClaveSecretaParaLaPractica2026!";

    /**
     * Cifra un archivo .txt y crea un archivo .enc
     */
    public static void encryptFile(String inputPath, String outputPath) throws Exception {
        // Leer archivo original
        byte[] fileBytes = Files.readAllBytes(Paths.get(inputPath));

        // Generar salt aleatorio
        byte[] salt = generateRandomBytes(SALT_LENGTH);

        // Generar vector de inicialización aleatorio
        byte[] iv = generateRandomBytes(IV_LENGTH);

        // Derivar clave a partir de la contraseña y salt
        SecretKey key = deriveKey(PASSWORD, salt);

        // Configurar cifrador
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        IvParameterSpec ivSpec = new IvParameterSpec(iv);
        cipher.init(Cipher.ENCRYPT_MODE, key, ivSpec);

        // Cifrar datos
        byte[] encryptedBytes = cipher.doFinal(fileBytes);

        // Escribir archivo cifrado: [salt][iv][datos_cifrados]
        try (FileOutputStream fos = new FileOutputStream(outputPath)) {
            fos.write(salt);           // Escribir salt (16 bytes)
            fos.write(iv);             // Escribir IV (16 bytes)
            fos.write(encryptedBytes); // Escribir datos cifrados
        }

        System.out.println("Cifrado: " + new File(inputPath).getName() + " → " + new File(outputPath).getName());
        System.out.println("   Tamaño original: " + fileBytes.length + " bytes");
        System.out.println("   Tamaño cifrado: " + (SALT_LENGTH + IV_LENGTH + encryptedBytes.length) + " bytes");
    }

    /**
     * Descifra un archivo .enc y restaura el .txt original
     */
    public static void decryptFile(String inputPath, String outputPath) throws Exception {
        // Leer archivo cifrado completo
        byte[] encryptedData = Files.readAllBytes(Paths.get(inputPath));

        // Extraer salt (primeros 16 bytes)
        byte[] salt = new byte[SALT_LENGTH];
        System.arraycopy(encryptedData, 0, salt, 0, SALT_LENGTH);

        // Extraer IV (siguientes 16 bytes)
        byte[] iv = new byte[IV_LENGTH];
        System.arraycopy(encryptedData, SALT_LENGTH, iv, 0, IV_LENGTH);

        // Extraer datos cifrados (resto)
        byte[] cipherText = new byte[encryptedData.length - SALT_LENGTH - IV_LENGTH];
        System.arraycopy(encryptedData, SALT_LENGTH + IV_LENGTH, cipherText, 0, cipherText.length);

        // Derivar clave usando el mismo salt
        SecretKey key = deriveKey(PASSWORD, salt);

        // Configurar descifrador
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        IvParameterSpec ivSpec = new IvParameterSpec(iv);
        cipher.init(Cipher.DECRYPT_MODE, key, ivSpec);

        // Descifrar datos
        byte[] decryptedBytes = cipher.doFinal(cipherText);

        // Escribir archivo descifrado
        Files.write(Paths.get(outputPath), decryptedBytes);

        System.out.println(" Descifrado: " + new File(inputPath).getName() + " → " + new File(outputPath).getName());
        System.out.println("   Tamaño cifrado: " + encryptedData.length + " bytes");
        System.out.println("   Tamaño restaurado: " + decryptedBytes.length + " bytes");
    }

    /**
     * Genera bytes aleatorios
     */
    private static byte[] generateRandomBytes(int length) {
        byte[] bytes = new byte[length];
        new SecureRandom().nextBytes(bytes);
        return bytes;
    }

    /**
     * Deriva una clave AES a partir de una contraseña y un salt
     * Usa PBKDF2 para derivación segura
     */
    private static SecretKey deriveKey(String password, byte[] salt) throws Exception {
        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
        KeySpec spec = new PBEKeySpec(password.toCharArray(), salt, ITERATION_COUNT, KEY_LENGTH);
        SecretKey tmp = factory.generateSecret(spec);
        return new SecretKeySpec(tmp.getEncoded(), KEY_ALGORITHM);
    }

    /**
     * Verifica si un archivo es un archivo cifrado válido
     */
    public static boolean isValidEncryptedFile(String filePath) {
        File file = new File(filePath);
        if (!file.exists() || !file.getName().endsWith(".enc")) {
            return false;
        }
        // Un archivo cifrado válido debe tener al menos salt + IV = 32 bytes
        return file.length() > (SALT_LENGTH + IV_LENGTH);
    }
}