package org.PR2;

import org.PR2.model.FileBuffer;
import org.PR2.threads.DecryptorThread;
import org.PR2.threads.ScannerThread;

import java.io.File;


public class MainPCS {
    public static void main(String[] args) {
        System.out.println("=".repeat(70));
        System.out.println("MODO PCS (PROCESS CONTENTION SCOPE)");
        System.out.println("Los threads compiten entre si dentro del mismo proceso");
        System.out.println("=".repeat(70));

        String encryptedDir = "./documentos_cifrados";
        String outputDir = "./documentos_restaurados_pcs";
        int bufferCapacity = 3;
        int numConsumidores = 5;

        File encDir = new File(encryptedDir);
        if (!encDir.exists() || encDir.listFiles((d, n) -> n.endsWith(".enc")).length == 0) {
            System.err.println("\n[ERROR] No hay archivos .enc en " + encryptedDir);
            System.err.println("\nEjecuta primero 'CreateRealEncryptedFiles'");
            return;
        }

        new File(outputDir).mkdirs();

        System.out.println("\n[CONFIGURACION]");
        System.out.println("  Directorio cifrados: " + encryptedDir);
        System.out.println("  Directorio salida: " + outputDir);
        System.out.println("  Capacidad buffer: " + bufferCapacity);
        System.out.println("  Numero consumidores: " + numConsumidores);

        FileBuffer buffer = new FileBuffer(bufferCapacity);

        ScannerThread scanner = new ScannerThread(encryptedDir, buffer);
        scanner.setName("Scanner-PCS");

        DecryptorThread[] consumers = new DecryptorThread[numConsumidores];
        for (int i = 0; i < numConsumidores; i++) {
            consumers[i] = new DecryptorThread(buffer, outputDir);
            consumers[i].setName("Consumer-PCS-" + (i + 1));
            if (i % 2 == 0) {
                consumers[i].setPriority(Thread.MAX_PRIORITY);
            } else {
                consumers[i].setPriority(Thread.MIN_PRIORITY);
            }
        }

        System.out.println("\n[PRIORIDADES EN MODO PCS]");
        for (DecryptorThread c : consumers) {
            System.out.println("  " + c.getName() + " - Prioridad: " + c.getPriority());
        }

        System.out.println("\n" + "=".repeat(70));
        System.out.println("INICIANDO EJECUCION MODO PCS");
        System.out.println("=".repeat(70) + "\n");

        long startTime = System.currentTimeMillis();

        scanner.start();
        for (DecryptorThread consumer : consumers) {
            consumer.start();
        }

        try {
            scanner.join();
            for (DecryptorThread consumer : consumers) {
                consumer.join();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        long endTime = System.currentTimeMillis();

        System.out.println("\n" + "=".repeat(70));
        System.out.println("RESULTADOS MODO PCS");
        System.out.println("=".repeat(70));
        System.out.println("Tiempo total: " + (endTime - startTime) + " ms");
        System.out.println("Threads utilizados: " + (1 + numConsumidores));
        System.out.println("Tipo de contencion: PROCESO (solo compiten entre ellos)");

        File[] restored = new File(outputDir).listFiles((d, n) -> n.endsWith(".txt"));
        System.out.println("Archivos restaurados: " + (restored != null ? restored.length : 0));
        System.out.println("=".repeat(70));
    }
}