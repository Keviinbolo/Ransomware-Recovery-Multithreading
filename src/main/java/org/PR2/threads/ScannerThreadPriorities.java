package org.PR2.threads;


import org.PR2.model.EncryptedFile;
import org.PR2.model.FileBuffer;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ScannerThreadPriorities extends Thread {
    private final String directoryPath;
    private final FileBuffer highPriorityBuffer;
    private final FileBuffer lowPriorityBuffer;
    private final long sizeThreshold;

    private List<EncryptedFile> smallFiles;
    private List<EncryptedFile> largeFiles;

    public ScannerThreadPriorities(String directoryPath,
                                   FileBuffer highPriorityBuffer,
                                   FileBuffer lowPriorityBuffer,
                                   long sizeThresholdKB) {
        this.directoryPath = directoryPath;
        this.highPriorityBuffer = highPriorityBuffer;
        this.lowPriorityBuffer = lowPriorityBuffer;
        this.sizeThreshold = sizeThresholdKB * 1024;
        this.smallFiles = new ArrayList<>();
        this.largeFiles = new ArrayList<>();
    }

    @Override
    public void run() {
        System.out.println("[INICIO] Escaner iniciado en: " + directoryPath);
        System.out.println("[CONFIG] Umbral para prioridad alta: " + (sizeThreshold / 1024) + " KB");

        scanDirectory(new File(directoryPath));

        System.out.println("");
        System.out.println("[CLASIFICACION] Archivos encontrados:");
        System.out.println("  Pequenos (prioridad ALTA): " + smallFiles.size());
        System.out.println("  Grandes (prioridad BAJA): " + largeFiles.size());

        System.out.println("");
        System.out.println("[ENVIO] Enviando archivos a los buffers...");

        for (EncryptedFile file : smallFiles) {
            try {
                highPriorityBuffer.put(file);
                System.out.println("  [ALTA] " + file.getName() + " -> Buffer prioridad alta");
                Thread.sleep(20);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }

        for (EncryptedFile file : largeFiles) {
            try {
                lowPriorityBuffer.put(file);
                System.out.println("  [BAJA] " + file.getName() + " -> Buffer prioridad baja");
                Thread.sleep(20);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }

        highPriorityBuffer.setFinished(true);
        lowPriorityBuffer.setFinished(true);

        System.out.println("");
        System.out.println("[FIN] Escaner finalizado");
    }

    private void scanDirectory(File directory) {
        if (!directory.exists() || !directory.isDirectory()) {
            System.err.println("[ERROR] Directorio no valido: " + directory.getPath());
            return;
        }

        File[] files = directory.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    scanDirectory(file);
                } else if (file.getName().endsWith(".enc")) {
                    long size = file.length();
                    EncryptedFile encFile = new EncryptedFile(file.getAbsolutePath(), size);

                    if (size <= sizeThreshold) {
                        smallFiles.add(encFile);
                        System.out.println("[ENCONTRADO] Pequeno: " + file.getName() +
                                " (" + size/1024 + " KB)");
                    } else {
                        largeFiles.add(encFile);
                        System.out.println("[ENCONTRADO] Grande: " + file.getName() +
                                " (" + size/1024 + " KB)");
                    }
                }
            }
        }
    }
}