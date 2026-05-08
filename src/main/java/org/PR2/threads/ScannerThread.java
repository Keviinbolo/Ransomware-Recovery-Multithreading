package org.PR2.threads;

import org.PR2.model.EncryptedFile;
import org.PR2.model.FileBuffer;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ScannerThread extends Thread {
    private final String directoryPath;
    private final FileBuffer buffer;
    private final List<EncryptedFile> foundFiles;

    public ScannerThread(String directoryPath, FileBuffer buffer) {
        this.directoryPath = directoryPath;
        this.buffer = buffer;
        this.foundFiles = new ArrayList<>();
    }

    @Override
    public void run() {
        System.out.println(" Escáner iniciado. Buscando archivos .enc en: " + directoryPath);
        scanDirectory(new File(directoryPath));

        System.out.println(" Escáner completado. Total archivos encontrados: " + foundFiles.size());

        for (EncryptedFile file : foundFiles) {
            try {
                buffer.put(file);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }

        buffer.setFinished(true);
        System.out.println(" Productor finalizado");
    }

    private void scanDirectory(File directory) {
        if (!directory.exists() || !directory.isDirectory()) {
            System.err.println(" Directorio no válido: " + directory.getPath());
            return;
        }

        File[] files = directory.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    scanDirectory(file);
                } else if (file.getName().endsWith(".enc")) {
                    long size = file.length();

                    foundFiles.add(new EncryptedFile(file.getAbsolutePath(), size));
                    System.out.println(" Encontrado: " + file.getName() + " (Tamaño: " + size + " bytes)");
                }
            }
        }
    }
}