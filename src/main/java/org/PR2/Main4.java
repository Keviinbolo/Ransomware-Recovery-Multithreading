package org.PR2;

import org.PR2.model.FileBuffer;
import org.PR2.threads.DecryptorThread;
import org.PR2.threads.MonitorThread;
import org.PR2.threads.ScannerThread;
import java.io.File;

public class Main4 {
    public static void main(String[] args) {
        System.out.println("=".repeat(70));
        System.out.println("EJERCICIO 4: MONITOR DE ESTADO DE THREADS");
        System.out.println("=".repeat(70));

        String encryptedDir = "./documentos_cifrados";
        String outputDir = "./documentos_restaurados_monitor";
        int bufferCapacity = 2;
        int numConsumidores = 3;
        int monitorInterval = 2;

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
        System.out.println("  Consumidores: " + numConsumidores);
        System.out.println("  Intervalo monitor: " + monitorInterval + " segundos");

        FileBuffer buffer = new FileBuffer(bufferCapacity);

        ScannerThread scanner = new ScannerThread(encryptedDir, buffer);
        scanner.setName("Scanner-Productor");

        DecryptorThread[] consumers = new DecryptorThread[numConsumidores];
        for (int i = 0; i < numConsumidores; i++) {
            consumers[i] = new DecryptorThread(buffer, outputDir);
            consumers[i].setName("Consumidor-" + (i + 1));
        }

        MonitorThread monitor = new MonitorThread(monitorInterval);
        monitor.setName("Monitor-Estados");
        monitor.addThreads(scanner);
        monitor.addThreads(consumers);
        monitor.addThread(monitor);

        System.out.println("\n" + "=".repeat(70));
        System.out.println("INICIANDO SISTEMA CON MONITOR");
        System.out.println("=".repeat(70) + "\n");

        long globalStart = System.currentTimeMillis();

        monitor.start();
        scanner.start();
        for (DecryptorThread consumer : consumers) {
            consumer.start();
        }

        try {
            scanner.join();

            Thread.sleep(3000);

            monitor.stopMonitoring();
            monitor.join();

            for (DecryptorThread consumer : consumers) {
                consumer.join();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        long globalEnd = System.currentTimeMillis();

        System.out.println("\n" + "=".repeat(70));
        System.out.println("RESULTADOS FINALES");
        System.out.println("=".repeat(70));
        System.out.println("Tiempo total de ejecucion: " + (globalEnd - globalStart) + " ms");

        File[] restored = new File(outputDir).listFiles((d, n) -> n.endsWith(".txt"));
        System.out.println("Archivos restaurados: " + (restored != null ? restored.length : 0));
        System.out.println("=".repeat(70));
    }
}