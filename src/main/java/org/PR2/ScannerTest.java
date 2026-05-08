package org.PR2;
import org.PR2.Crytpo.CryptoUtils;
import org.PR2.model.FileBuffer;
import org.PR2.threads.ScannerThread;

import java.io.File;
public class ScannerTest {
    public static void main(String[] args) {
        System.out.println("=".repeat(70));
        System.out.println("PRUEBA DE CIFRADO/DESCIFRADO");
        System.out.println("=".repeat(70));

        try {
            // 1. Crear un archivo de prueba simple
            String originalFile = "./test_original.txt";
            String encryptedFile = "./test_encrypted.enc";
            String decryptedFile = "./test_decrypted.txt";

            System.out.println("\n1. Creando archivo original...");
            java.nio.file.Files.write(java.nio.file.Paths.get(originalFile),
                    "Este es un mensaje secreto que vamos a cifrar. Contiene información confidencial de la empresa.".getBytes());
            System.out.println("   Contenido: " + new String(java.nio.file.Files.readAllBytes(java.nio.file.Paths.get(originalFile))));

            System.out.println("\n2. Cifrando archivo...");
            CryptoUtils.encryptFile(originalFile, encryptedFile);

            System.out.println("\n3. Descifrando archivo...");
            CryptoUtils.decryptFile(encryptedFile, decryptedFile);

            System.out.println("\n4. Comparando resultados...");
            String originalContent = new String(java.nio.file.Files.readAllBytes(java.nio.file.Paths.get(originalFile)));
            String decryptedContent = new String(java.nio.file.Files.readAllBytes(java.nio.file.Paths.get(decryptedFile)));

            System.out.println("   Original: " + originalContent);
            System.out.println("   Descifrado: " + decryptedContent);

            if (originalContent.equals(decryptedContent)) {
                System.out.println("\n¡PRUEBA SUPERADA! El cifrado y descifrado funcionan correctamente.");
            } else {
                System.err.println("\n ERROR: El contenido descifrado no coincide con el original.");
            }

            // Limpiar archivos de prueba
            new java.io.File(originalFile).delete();
            new java.io.File(encryptedFile).delete();
            new java.io.File(decryptedFile).delete();

        } catch (Exception e) {
            System.err.println(" Error en la prueba: " + e.getMessage());
            e.printStackTrace();
        }
    }
}