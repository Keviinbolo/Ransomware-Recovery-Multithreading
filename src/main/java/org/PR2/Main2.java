package org.PR2;

import org.PR2.model.FileBuffer;
import org.PR2.model.EncryptedFile;
import org.PR2.threads.DecryptorThread;
import org.PR2.threads.ScannerThread;
import java.io.File;

public class Main2 {
    public static void main(String[] args) {
        System.out.println("=".repeat(70));
        System.out.println(" EJERCICIO 2: THREADS DE RECUPERACIÓN (CONSUMIDORES)");
        System.out.println("=".repeat(70));

        // Configuración
        String encryptedDir = "./documentos_cifrados";
        String outputDir = "./documentos_restaurados";
        int bufferCapacity = 3;
        int numConsumidores = 3;

        // Verificar que existen archivos cifrados
        File encDir = new File(encryptedDir);
        if (!encDir.exists() || encDir.listFiles((d, n) -> n.endsWith(".enc")).length == 0) {
            System.err.println("\n No hay archivos .enc en " + encryptedDir);
            System.err.println("\n Ejecuta primero la clase 'CreateRealEncryptedFiles'");
            System.err.println("   para crear archivos originales y cifrarlos.\n");
            return;
        }

        // Limpiar directorio de salida anterior
        File output = new File(outputDir);
        if (output.exists()) {
            System.out.println("🧹 Limpiando directorio de salida anterior...");
            for (File f : output.listFiles()) {
                f.delete();
            }
        } else {
            output.mkdirs();
        }

        // Mostrar configuración
        System.out.println("\n Directorio con archivos cifrados: " + encryptedDir);
        System.out.println(" Directorio de salida (restaurados): " + outputDir);
        System.out.println(" Capacidad del buffer: " + bufferCapacity);
        System.out.println(" Número de consumidores: " + numConsumidores);

        // Contar archivos a procesar
        File[] encFiles = encDir.listFiles((d, n) -> n.endsWith(".enc"));
        System.out.println(" Archivos .enc encontrados: " + encFiles.length);

        System.out.println("\n" + "=".repeat(70));
        System.out.println(" INICIANDO PROCESO DE RECUPERACIÓN PARALELA");
        System.out.println("=".repeat(70) + "\n");

        // Crear buffer compartido
        FileBuffer buffer = new FileBuffer(bufferCapacity);

        // 1. Crear y iniciar el productor (escáner)
        ScannerThread scanner = new ScannerThread(encryptedDir, buffer);
        scanner.setName("Scanner");

        // 2. Crear y iniciar los consumidores (descifradores)
        DecryptorThread[] consumers = new DecryptorThread[numConsumidores];
        for (int i = 0; i < numConsumidores; i++) {
            consumers[i] = new DecryptorThread(buffer, outputDir);
            consumers[i].setName("Consumidor-" + (i + 1));
        }

        // Medir tiempo total
        long globalStart = System.currentTimeMillis();

        // Iniciar productor
        scanner.start();

        // Iniciar consumidores
        for (DecryptorThread consumer : consumers) {
            consumer.start();
        }

        // Esperar a que terminen todos
        try {
            scanner.join();
            for (DecryptorThread consumer : consumers) {
                consumer.join();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        long globalEnd = System.currentTimeMillis();

        // Mostrar resultados finales
        System.out.println("\n" + "=".repeat(70));
        System.out.println(" RESULTADOS FINALES");
        System.out.println("=".repeat(70));
        System.out.println("️  Tiempo total de ejecución: " + (globalEnd - globalStart) + " ms");
        System.out.println(" Total archivos procesados: " + encFiles.length);
        System.out.println(" Threads utilizados: " + (1 + numConsumidores) + " (1 productor + " + numConsumidores + " consumidores)");

        // Verificar archivos restaurados
        File[] restoredFiles = output.listFiles((d, n) -> n.endsWith(".txt"));
        System.out.println(" Archivos restaurados: " + (restoredFiles != null ? restoredFiles.length : 0));

        System.out.println("\n Archivos restaurados:");
        if (restoredFiles != null) {
            for (File f : restoredFiles) {
                System.out.println(f.getName() + " (" + f.length() + " bytes)");
            }
        }

        System.out.println("\n" + "=".repeat(70));
        System.out.println(" PROCESO COMPLETADO - Todos los archivos han sido restaurados");
        System.out.println("=".repeat(70));
    }
}