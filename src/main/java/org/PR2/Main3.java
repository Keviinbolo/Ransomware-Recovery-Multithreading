package org.PR2;

import org.PR2.model.FileBuffer;
import org.PR2.model.EncryptedFile;
import org.PR2.threads.DecryptorThreadPriorities;
import org.PR2.threads.ScannerThreadPriorities;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class Main3 {
    public static void main(String[] args) {
        System.out.println("============================================================");
        System.out.println("EJERCICIO 3: PRIORIDADES DE THREADS");
        System.out.println("============================================================");

        String encryptedDir = "./documentos_cifrados";
        String outputDir = "./documentos_restaurados_prioridades";
        int bufferCapacity = 5;
        long sizeThresholdKB = 50;

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
        System.out.println("  Umbral prioridad alta (archivos <= " + sizeThresholdKB + " KB)");

        File[] encFiles = encDir.listFiles((d, n) -> n.endsWith(".enc"));
        System.out.println("  Archivos encontrados: " + encFiles.length);

        FileBuffer highPriorityBuffer = new FileBuffer(bufferCapacity);
        FileBuffer lowPriorityBuffer = new FileBuffer(bufferCapacity);

        System.out.println("\n============================================================");
        System.out.println("INICIANDO SISTEMA CON PRIORIDADES");
        System.out.println("============================================================\n");

        ScannerThreadPriorities scanner = new ScannerThreadPriorities(
                encryptedDir, highPriorityBuffer, lowPriorityBuffer, sizeThresholdKB);
        scanner.setName("Scanner");

        DecryptorThreadPriorities highPriorityConsumer = new DecryptorThreadPriorities(
                highPriorityBuffer, outputDir, DecryptorThreadPriorities.PriorityType.HIGH);
        highPriorityConsumer.setName("Consumer-HIGH");

        DecryptorThreadPriorities lowPriorityConsumer1 = new DecryptorThreadPriorities(
                lowPriorityBuffer, outputDir, DecryptorThreadPriorities.PriorityType.LOW);
        lowPriorityConsumer1.setName("Consumer-LOW-1");

        DecryptorThreadPriorities lowPriorityConsumer2 = new DecryptorThreadPriorities(
                lowPriorityBuffer, outputDir, DecryptorThreadPriorities.PriorityType.LOW);
        lowPriorityConsumer2.setName("Consumer-LOW-2");

        System.out.println("[PRIORIDADES ASIGNADAS]");
        System.out.println("  " + highPriorityConsumer.getName() +
                " - Prioridad Java: " + highPriorityConsumer.getPriority() + " (MAX)");
        System.out.println("  " + lowPriorityConsumer1.getName() +
                " - Prioridad Java: " + lowPriorityConsumer1.getPriority() + " (MIN)");
        System.out.println("  " + lowPriorityConsumer2.getName() +
                " - Prioridad Java: " + lowPriorityConsumer2.getPriority() + " (MIN)");

        System.out.println("\n[EJECUCION]");
        System.out.println("------------------------------------------------------------\n");

        long globalStart = System.currentTimeMillis();

        scanner.start();
        highPriorityConsumer.start();
        lowPriorityConsumer1.start();
        lowPriorityConsumer2.start();

        try {
            scanner.join();
            highPriorityConsumer.join();
            lowPriorityConsumer1.join();
            lowPriorityConsumer2.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        long globalEnd = System.currentTimeMillis();

        System.out.println("\n============================================================");
        System.out.println("RESULTADOS FINALES");
        System.out.println("============================================================");
        System.out.println("Tiempo total de ejecucion: " + (globalEnd - globalStart) + " ms");

        File[] restored = new File(outputDir).listFiles((d, n) -> n.endsWith(".txt"));
        System.out.println("Archivos restaurados: " + (restored != null ? restored.length : 0));


    }
}