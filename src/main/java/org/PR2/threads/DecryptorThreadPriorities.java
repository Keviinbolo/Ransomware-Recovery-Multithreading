package org.PR2.threads;



import org.PR2.model.EncryptedFile;
import org.PR2.model.FileBuffer;
import java.io.File;

public class DecryptorThreadPriorities extends Thread {
    private final FileBuffer buffer;
    private final String outputDirectory;
    private final PriorityType priorityType;
    private int filesProcessed = 0;
    private long totalTime = 0;

    public enum PriorityType {
        HIGH,
        LOW
    }

    public DecryptorThreadPriorities(FileBuffer buffer, String outputDirectory, PriorityType priorityType) {
        this.buffer = buffer;
        this.outputDirectory = outputDirectory;
        this.priorityType = priorityType;

        if (priorityType == PriorityType.HIGH) {
            this.setPriority(Thread.MAX_PRIORITY);
        } else {
            this.setPriority(Thread.MIN_PRIORITY);
        }
    }

    @Override
    public void run() {
        String typeStr = priorityType == PriorityType.HIGH ? "ALTA" : "BAJA";
        System.out.println("[INICIO] " + getName() + " - Prioridad: " + getPriority() +
                " (" + typeStr + ")");

        while (true) {
            try {
                EncryptedFile file = buffer.take();
                if (file == null) {
                    break;
                }

                System.out.println(getName() + " (Prio:" + getPriority() +
                        ") recuperando: " + file.getName() +
                        " [" + file.getSize() / 1024 + " KB]");

                long startTime = System.currentTimeMillis();
                simulateDecryption(file);
                long endTime = System.currentTimeMillis();
                long timeMs = endTime - startTime;
                totalTime += timeMs;
                filesProcessed++;

                System.out.println("  [OK] " + getName() + " completo: " + file.getName() +
                        " (" + timeMs + " ms)");

                Thread.sleep(10);

            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }

        System.out.println("");
        System.out.println("[RESUMEN] " + getName() + " FINALIZADO:");
        System.out.println("  Prioridad Java: " + getPriority());
        System.out.println("  Tipo: " + priorityType);
        System.out.println("  Archivos procesados: " + filesProcessed);
        System.out.println("  Tiempo total: " + totalTime + " ms");
    }

    private void simulateDecryption(EncryptedFile file) throws InterruptedException {
        long sizeKB = file.getSize() / 1024;

        if (priorityType == PriorityType.HIGH) {
            Thread.sleep(Math.max(10, sizeKB / 2));
        } else {
            Thread.sleep(Math.max(10, sizeKB * 2));
        }
    }
}