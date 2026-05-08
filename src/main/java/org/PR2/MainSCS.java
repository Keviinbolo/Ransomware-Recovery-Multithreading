package org.PR2;

import org.PR2.model.FileBuffer;
import org.PR2.threads.DecryptorThread;
import org.PR2.threads.ScannerThread;

import java.io.File;

public class MainSCS {
    public static void main(String[] args) {
        System.out.println("=".repeat(70));
        System.out.println("MODO SCS (SYSTEM CONTENTION SCOPE)");
        System.out.println("Simula que los threads compiten con otros procesos del sistema");
        System.out.println("=".repeat(70));

        String encryptedDir = "./documentos_cifrados";
        String outputDir = "./documentos_restaurados_scs";
        int bufferCapacity = 5;
        int numConsumidores = 3;

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
        scanner.setName("Scanner-SCS");

        DecryptorThread[] consumers = new DecryptorThread[numConsumidores];
        for (int i = 0; i < numConsumidores; i++) {
            consumers[i] = new DecryptorThread(buffer, outputDir);
            consumers[i].setName("Consumer-SCS-" + (i + 1));
            consumers[i].setPriority(Thread.NORM_PRIORITY);
        }

        System.out.println("\n[PROCESOS DEL SISTEMA SIMULADOS]");
        System.out.println("  Se ejecutaran procesos adicionales para simular carga");

        System.out.println("\n" + "=".repeat(70));
        System.out.println("INICIANDO EJECUCION MODO SCS");
        System.out.println("=".repeat(70) + "\n");

        Thread[] systemProcesses = new Thread[4];
        for (int i = 0; i < systemProcesses.length; i++) {
            final int id = i + 1;
            systemProcesses[i] = new Thread(() -> {
                System.out.println("[PROCESO_SISTEMA_" + id + "] Iniciado");
                for (int j = 0; j < 5; j++) {
                    try {
                        Thread.sleep(300);
                        System.out.println("[PROCESO_SISTEMA_" + id + "] Ejecutando tarea " + (j+1));
                        for (int k = 0; k < 1000000; k++) {
                            Math.random();
                        }
                    } catch (InterruptedException e) {
                        break;
                    }
                }
                System.out.println("[PROCESO_SISTEMA_" + id + "] Finalizado");
            });
            systemProcesses[i].setName("Sistema-Proceso-" + (i+1));
            systemProcesses[i].setPriority(Thread.NORM_PRIORITY);
        }

        long startTime = System.currentTimeMillis();

        scanner.start();
        for (DecryptorThread consumer : consumers) {
            consumer.start();
        }
        for (Thread process : systemProcesses) {
            process.start();
        }

        try {
            scanner.join();
            for (DecryptorThread consumer : consumers) {
                consumer.join();
            }
            for (Thread process : systemProcesses) {
                process.join();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        long endTime = System.currentTimeMillis();

        System.out.println("\n" + "=".repeat(70));
        System.out.println("RESULTADOS MODO SCS");
        System.out.println("=".repeat(70));
        System.out.println("Tiempo total: " + (endTime - startTime) + " ms");
        System.out.println("Threads principales: " + (1 + numConsumidores));
        System.out.println("Procesos sistema simulados: " + systemProcesses.length);
        System.out.println("Total entidades compitiendo: " + (1 + numConsumidores + systemProcesses.length));
        System.out.println("Tipo de contencion: SISTEMA (compten con procesos externos)");

        File[] restored = new File(outputDir).listFiles((d, n) -> n.endsWith(".txt"));
        System.out.println("Archivos restaurados: " + (restored != null ? restored.length : 0));
        System.out.println("=".repeat(70));
    }
}