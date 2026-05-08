package org.PR2.threads;

import org.PR2.Crytpo.CryptoUtils;
import org.PR2.model.EncryptedFile;
import org.PR2.model.FileBuffer;
import java.io.*;

import java.io.File;

public class DecryptorThread extends Thread {
    private final FileBuffer buffer;
    private final String outputDirectory;
    private final Priority priority;  // Añadir este campo
    private int filesProcessed = 0;
    private long totalTime = 0;

    // Enum de prioridades
    public enum Priority {
        HIGH, LOW
    }

    // Constructor que acepta Priority
    public DecryptorThread(FileBuffer buffer, String outputDirectory, Priority priority) {
        this.buffer = buffer;
        this.outputDirectory = outputDirectory;
        this.priority = priority;
    }

    // Constructor sin Priority (para compatibilidad)
    public DecryptorThread(FileBuffer buffer, String outputDirectory) {
        this(buffer, outputDirectory, Priority.LOW);
    }

    @Override
    public void run() {
        String priorityStr = priority == Priority.HIGH ? "ALTA" : "BAJA";
        System.out.println(getName() + " (" + priorityStr + ") iniciado");

        while (true) {
            try {
                EncryptedFile file = buffer.take();
                if (file == null) {
                    break;
                }

                System.out.println(getName() + " recuperando archivo: " + file.getName());

                long startTime = System.currentTimeMillis();
                decryptFile(file);
                long endTime = System.currentTimeMillis();
                long timeMs = endTime - startTime;
                totalTime += timeMs;
                filesProcessed++;

                System.out.println(getName() + " completó: " + file.getName() +
                        " (" + timeMs + " ms)");

            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            } catch (Exception e) {
                System.err.println(getName() + " ERROR: " + e.getMessage());
            }
        }

        System.out.println(getName() + " FINALIZADO:");
        System.out.println("   Archivos: " + filesProcessed);
        System.out.println("   Tiempo total: " + totalTime + " ms");
    }

    private void decryptFile(EncryptedFile encryptedFile) throws Exception {
        String outputPath = outputDirectory + File.separator +
                new File(encryptedFile.getPath()).getName().replace(".enc", ".txt");

        File outputDir = new File(outputDirectory);
        if (!outputDir.exists()) {
            outputDir.mkdirs();
        }

        CryptoUtils.decryptFile(encryptedFile.getPath(), outputPath);
    }
}