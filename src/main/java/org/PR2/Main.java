package org.PR2;

import org.PR2.model.FileBuffer;
import org.PR2.threads.DecryptorThread;
import org.PR2.threads.ScannerThread;

import java.io.File;

public class Main {
    public static void main(String[] args) {
        // Configuración
        String inputDirectory = "./documentos_cifrados";
        String outputDirectory = "./documentos_restaurados";
        int bufferCapacity = 5;
        int numConsumidores = 3;

        // Crear directorio de salida si no existe
        new File(outputDirectory).mkdirs();

        // Crear buffer compartido
        FileBuffer buffer = new FileBuffer(bufferCapacity);

        // Crear productor
        ScannerThread scanner = new ScannerThread(inputDirectory, buffer);

        // Crear consumidores
        DecryptorThread[] consumers = new DecryptorThread[numConsumidores];
        for (int i = 0; i < numConsumidores; i++) {
            consumers[i] = new DecryptorThread(buffer, outputDirectory,
                    DecryptorThread.Priority.LOW);
            consumers[i].setName("Consumidor-" + (i + 1));
        }

        // Iniciar threads
        scanner.start();
        for (DecryptorThread consumer : consumers) {
            consumer.start();
        }

        // Esperar a que terminen
        try {
            scanner.join();
            for (DecryptorThread consumer : consumers) {
                consumer.join();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println(" PROCESO COMPLETADO - Todos los archivos han sido restaurados");
    }
}
