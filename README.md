# 🔐 Recuperación Ransomware - Multithreading
Proyecto de recuperación de archivos `.enc` mediante **AES-256** y **Java Threads**.

## 🏗️ Arquitectura
* **Productor (Scanner):** Busca archivos y llena el buffer.
* **Consumidor (Decryptor):** Descifra archivos en paralelo.
* **Monitor:** Supervisa estados de los hilos (`RUNNABLE`, `WAITING`).
```text
┌─────────────────────────────────────────────────────────────┐
│                      SISTEMA PRINCIPAL                      │
├─────────────────────────────────────────────────────────────┤
│                                                             │
│  ┌──────────────┐      ┌─────────────┐      ┌──────────────┐│
│  │   SCANNER    │      │    BUFFER   │      │  CONSUMIDOR  ││
│  │ (Productor)  │─────▶│(Compartido) │◀─────│   (Threads)  ││
│  └──────────────┘      └─────────────┘      └──────────────┘│
│          │                    │                    │        │
│          ▼                    ▼                    ▼        │
│  ┌──────────────┐      ┌─────────────┐      ┌──────────────┐│
│  │  Busca .enc  │      │  Capacidad  │      │  Descifra a  ││
│  │ en directorio│      │   limitada  │      │.txt original ││
│  └──────────────┘      └─────────────┘      └──────────────┘│
│                                                             │
│  ┌──────────────────────────────────────────────────────┐   │
│  │             MONITOR DE ESTADOS (Thread)              │   │
│  │      Muestra estado de threads cada N segundos       │   │
│  └──────────────────────────────────────────────────────┘   │
└─────────────────────────────────────────────────────────────┘
```
---

## 🚀 Ejecución Rápida
1. **Compilar:** `javac -d out src/model/*.java src/threads/*.java src/*.java`
2. **Pruebas:** `java -cp out CreateRealEncryptedFiles`
3. **Ejecutar:** `java -cp out Main`

---

## 📊 Ejercicios e Implementación
* **E1 (Productor):** Uso de `synchronized`, `wait()` y `notifyAll()`.
* **E2 (Consumidor):** Descifrado concurrente AES-256.
* **E3 (Prioridades):** Alta ($\le 50KB$) y Baja ($> 50KB$).
* **E4 (Monitor):** Reporte de estados de la JVM por cada hilo.
* **E5 (Modelos):** Simulación comparativa PCS vs SCS.

---

## 🔐 Detalles de Seguridad
* **Algoritmo:** AES/CBC/PKCS5Padding (256 bits).
* **Clave:** Derivación PBKDF2 con Salt e IV únicos.

---

## 📈 Rendimiento
| Métrica | Secuencial | Paralelo (3 Hilos) |
| :--- | :--- | :--- |
| Tiempo | 2,456 ms | 847 ms |
| Mejora | - | **2.9x más rápido** |

---

## 👥 Autor
**Kevin Yamel Diaz Perez** - *Líder de Desarrollo* - [GitHub](https://github.com/tu-usuario)

## 📄 Licencia
Distribuido bajo licencia **MIT**.
