package org.PR2.enc;
import org.PR2.Crytpo.CryptoUtils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;

public class CreateFiles {

    public static void main(String[] args) {
        System.out.println("=".repeat(70));
        System.out.println("🔐 CREACIÓN DE ARCHIVOS DE PRUEBA CON CIFRADO REAL");
        System.out.println("=".repeat(70));

        // Directorios
        String sourceDir = "./documentos_originales";
        String encryptedDir = "./documentos_cifrados";

        // Crear directorios
        new File(sourceDir).mkdirs();
        new File(encryptedDir).mkdirs();

        // Crear archivos .txt originales
        String[] documents = {
                "informe_financiero.txt",
                "facturas_2025.txt",
                "contrato_cliente.txt",
                "base_datos_clientes.txt",
                "backup_servidor.txt",
                "documento_confidencial.txt",
                "reporte_ventas.txt",
                "nominas_empleados.txt"
        };

        System.out.println("\n Creando archivos originales .txt...");
        System.out.println("-".repeat(70));

        for (String docName : documents) {
            String txtPath = sourceDir + "/" + docName;
            createSampleTextFile(txtPath, docName);
        }

        System.out.println("\n Cifrando archivos...");
        System.out.println("-".repeat(70));

        // Cifrar cada archivo .txt a .enc
        for (String docName : documents) {
            String inputPath = sourceDir + "/" + docName;
            String outputPath = encryptedDir + "/" + docName.replace(".txt", ".enc");

            try {
                CryptoUtils.encryptFile(inputPath, outputPath);
                Thread.sleep(100); // Pequeña pausa para ver la salida ordenada
            } catch (Exception e) {
                System.err.println(" Error cifrando " + docName + ": " + e.getMessage());
            }
        }

        System.out.println("\n" + "=".repeat(70));
        System.out.println(" RESUMEN");
        System.out.println("=".repeat(70));
        System.out.println(" Archivos originales (.txt): " + sourceDir);
        System.out.println(" Archivos cifrados (.enc): " + encryptedDir);
        System.out.println(" Total archivos creados: " + documents.length);
        System.out.println("=".repeat(70));
    }

    private static void createSampleTextFile(String path, String fileName) {
        Random random = new Random();
        int contentSize = 5 + random.nextInt(20); // Entre 5KB y 25KB

        try (FileWriter writer = new FileWriter(path)) {
            writer.write("DOCUMENTO: " + fileName + "\n");
            writer.write("FECHA CREACIÓN: " + new java.util.Date() + "\n");
            writer.write("=".repeat(50) + "\n\n");

            // Contenido simulado según tipo de documento
            if (fileName.contains("informe")) {
                writer.write("INFORME FINANCIERO\n");
                writer.write("Beneficios del primer trimestre: 1.2M €\n");
                writer.write("Proyecciones anuales: 5.8M €\n");
            } else if (fileName.contains("facturas")) {
                writer.write("FACTURAS 2025\n");
                for (int i = 1; i <= 10; i++) {
                    writer.write("Factura Nº " + i + ": " + (1000 + random.nextInt(9000)) + " €\n");
                }
            } else if (fileName.contains("contrato")) {
                writer.write("CONTRATO DE SERVICIOS\n");
                writer.write("Cliente: Empresa XYZ\n");
                writer.write("Importe: 50.000€\n");
                writer.write("Duración: 12 meses\n");
            } else if (fileName.contains("base_datos")) {
                writer.write("BASE DE DATOS DE CLIENTES\n");
                String[] clients = {"Cliente A", "Cliente B", "Cliente C", "Cliente D"};
                for (String client : clients) {
                    writer.write(client + " - Email: " + client.toLowerCase().replace(" ", "") + "@empresa.com\n");
                }
            } else {
                // Contenido genérico
                for (int i = 0; i < contentSize * 100; i++) {
                    writer.write("Línea de contenido " + i + ": " + generateRandomText(40) + "\n");
                }
            }

            System.out.println(" Creado: " + new File(path).getName() + " (" + contentSize + " KB aprox)");

        } catch (IOException e) {
            System.err.println(" Error creando " + fileName + ": " + e.getMessage());
        }
    }

    private static String generateRandomText(int length) {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789 ";
        StringBuilder sb = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < length; i++) {
            sb.append(chars.charAt(random.nextInt(chars.length())));
        }
        return sb.toString();
    }
}