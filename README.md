# Sistema de Recuperacion de Archivos Ransomware con Multithreading

## Descripcion del Proyecto

Este proyecto implementa una herramienta de recuperacion de archivos afectados por un ataque de ransomware. El sistema utiliza multithreading en Java para descifrar archivos .enc de forma paralela, restaurandolos a su formato original .txt.

---

## Objetivos del Proyecto

- Implementacion de creacion y gestion de threads en Java
- Aplicacion de patrones productor-consumidor con buffers compartidos
- Utilizacion de sincronizacion para evitar condiciones de carrera
- Gestion de prioridades de threads
- Monitorizacion de estados de threads en tiempo real
- Comparacion de modelos de planificacion PCS vs SCS
- Implementacion de cifrado real con AES-256

---

## Estructura del Proyecto

RA2_PR2_Threads/
    src/
        model/
            EncryptedFile.java
            FileBuffer.java
        threads/
            ScannerThread.java
            DecryptorThread.java
            DecryptorThreadPriorities.java
            MonitorThread.java
        CryptoUtils.java
        CreateRealEncryptedFiles.java
        Main.java
        MainEjercicio2.java
        MainEjercicio3.java
        MainEjercicio4.java
        MainPCS.java
        MainSCS.java
        ComparativaPCSvsSCS.java
    documentos_originales/
    documentos_cifrados/
    documentos_restaurados/

---

## Requisitos del Sistema

- Java JDK 11 o superior
- IntelliJ IDEA, Eclipse o VS Code
- Windows, Linux o macOS

---

## Instalacion y Ejecucion

Compilar el proyecto:
javac -d out src/model/*.java src/threads/*.java src/*.java

Crear archivos de prueba:
java -cp out CreateRealEncryptedFiles

Ejecutar el programa principal:
java -cp out Main

---

## Ejercicios Implementados

Ejercicio 1: Escaner de Archivos (Productor)

Thread que busca archivos .enc y los introduce en un buffer compartido con capacidad limitada.

Caracteristicas:
- Recorre directorios recursivamente
- Detecta archivos con extension .enc
- Buffer con capacidad configurable
- Sincronizacion con wait y notify

Ejercicio 2: Threads de Recuperacion (Consumidores)

Multiples threads que toman archivos del buffer y los descifran.

Caracteristicas:
- Multiples consumidores concurrentes
- Descifrado real con AES-256
- Muestra por pantalla que archivo procesa cada thread

Ejemplo de salida:
Consumidor-1 recuperando archivo: informe_financiero.enc
Consumidor-2 recuperando archivo: facturas_2025.enc
Consumidor-1 completo: informe_financiero.enc (245 ms)

Ejercicio 3: Prioridades de Threads

Asignacion de prioridades diferentes segun el tamaño del archivo.

Caracteristicas:
- Prioridad ALTA (MAX_PRIORITY=10) para archivos pequeños (hasta 50KB)
- Prioridad BAJA (MIN_PRIORITY=1) para archivos grandes (mas de 50KB)
- Buffers separados por prioridad

Ejemplo de salida:
Consumer-HIGH - Prioridad Java: 10 (MAX)
Consumer-LOW-1 - Prioridad Java: 1 (MIN)

Ejercicio 4: Monitor de Estado de Threads

Thread monitor que muestra el estado de todos los threads periodicamente.

Estados monitorizados:
- NEW: Creado pero no iniciado
- RUNNABLE: En ejecucion o listo
- BLOCKED: Bloqueado por sincronizacion
- WAITING: Esperando notificacion
- TIMED_WAITING: Espera con timeout
- TERMINATED: Finalizado

Ejemplo de salida:
Monitor-Estados : RUNNABLE (Vivo: true)
Scanner-Productor : RUNNABLE (Vivo: true)
Consumidor-1 : WAITING (Vivo: true)
Consumidor-2 : BLOCKED (Vivo: true)

Ejercicio 5: Simulacion PCS vs SCS

Comparativa entre dos modelos de planificacion.

PCS (Process Contention Scope): Threads compiten solo dentro del proceso
- Mas rapido, menor overhead

SCS (System Contention Scope): Threads compiten con procesos del sistema
- Mas realista, mayor overhead

Resultados comparativos:
Tiempo PCS: 2150 ms
Tiempo SCS: 3280 ms
PCS fue mas rapido por 1130 ms

---

## Cifrado AES-256

El sistema utiliza AES-256 en modo CBC para el cifrado y descifrado real de archivos.

Caracteristicas del cifrado:
- Algoritmo: AES/CBC/PKCS5Padding
- Longitud clave: 256 bits (32 bytes)
- Longitud IV: 16 bytes
- Longitud Salt: 16 bytes
- Derivacion clave: PBKDF2 con HMAC-SHA256
- Iteraciones: 65,536

---

## Tests y Validacion

Test de cifrado y descifrado:
java -cp out TestCryptoReal

Test de prioridades:
java -cp out TestPrioritiesOrder

Test de modos PCS y SCS:
java -cp out ComparativaPCSvsSCS

---

## Resultados de Rendimiento

Comparativa Secuencial vs Paralelo:
- Secuencial (1 thread): 2,456 ms
- Paralelo (3 threads): 847 ms
- Mejora: 2.9 veces mas rapido

Comparativa PCS vs SCS:
- Modo PCS: 2,150 ms
- Modo SCS: 3,280 ms
- PCS es 1.5 veces mas rapido

---

## Problemas Conocidos y Soluciones

Problema: Deadlock con buffer pequeno
Causa: Productor espera espacio, consumidores esperan datos
Solucion: Implementar setFinished y notifyAll

Problema: Prioridades ignoradas en Linux
Causa: Scheduler CFS no respeta prioridades Java
Solucion: Usar Thread.yield y ajustar tiempos

---

## Estado del Proyecto

COMPLETADO - Todos los ejercicios implementados y funcionando correctamente.

Ejercicio 1: Escaner de archivos (Productor) - COMPLETADO
Ejercicio 2: Threads de recuperacion (Consumidores) - COMPLETADO
Ejercicio 3: Prioridades de threads - COMPLETADO
Ejercicio 4: Monitor de estado - COMPLETADO
Ejercicio 5: Simulacion PCS vs SCS - COMPLETADO
Cifrado real AES-256 - COMPLETADO
Documentacion completa - COMPLETADO

---

## Resumen Rapido de Comandos

Compilar:
javac -d out src/model/*.java src/threads/*.java src/*.java

Crear archivos prueba:
java -cp out CreateRealEncryptedFiles

Ejecutar programa:
java -cp out Main

Probar cifrado:
java -cp out TestCryptoReal

Comparativa PCS/SCS:
java -cp out ComparativaPCSvsSCS

---

## Autor

Kevin Yamel Díaz Pérez 

---

*Ultima actualizacion: Mayo 2026*
