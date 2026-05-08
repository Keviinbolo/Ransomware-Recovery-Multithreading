# 🔐 Sistema de Recuperación de Archivos Ransomware con Multithreading

## 📋 Descripción del Proyecto
Este proyecto implementa una herramienta avanzada de recuperación de archivos afectados por un ataque de ransomware. Utiliza **multithreading en Java** para descifrar archivos `.enc` de forma paralela y restaurarlos a su formato original `.txt`. 

La solución aplica patrones de diseño concurrentes para maximizar el rendimiento en la recuperación de grandes volúmenes de datos, simulando un escenario de respuesta ante incidentes real.

---

## 🎯 Objetivos del Proyecto
- **Concurrencia:** Implementación y gestión de ciclos de vida de threads en Java.
- **Patrones:** Aplicación del modelo **Productor-Consumidor** con buffers compartidos.
- **Sincronización:** Uso de monitores (`wait/notify`) para evitar condiciones de carrera.
- **Priorización:** Gestión de hilos basada en la carga de trabajo (tamaño de archivos).
- **Monitorización:** Seguimiento en tiempo real de los estados de los threads.
- **Criptografía:** Implementación de cifrado/descifrado real mediante **AES-256**.

---

## 🏗️ Arquitectura del Sistema
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

## 🔧 Requisitos del Sistema
- **Java**: JDK 11 o superior.
- **IDE Recomendado**: IntelliJ IDEA, Eclipse o VS Code.
- **Sistema Operativo**: Windows, Linux o macOS.

---

## 🚀 Instalación y Ejecución

### 1. Clonar el repositorio

git clone [https://github.com/tu-usuario/ransomware-recovery.git](https://github.com/tu-usuario/ransomware-recovery.git)
cd ransomware-recovery

###2. Compilar el proyecto

javac -d out src/model/*.java src/threads/*.java src/*.java

###3. Crear archivos de prueba
Este paso generará la estructura de carpetas y los archivos cifrados necesarios para probar la herramienta.

java -cp out CreateRealEncryptedFiles

###4. Ejecutar el programa principal

java -cp out Main

## 📊 Ejercicios Implementados

### Ejercicio 1: Escáner de Archivos (Productor)
Thread encargado de buscar archivos `.enc` de forma recursiva e introducirlos en un buffer compartido con capacidad limitada. Implementa el control de flujo mediante:
*   **Bloqueo**: Uso de `wait()` cuando el buffer alcanza su capacidad máxima.
*   **Notificación**: Uso de `notifyAll()` al añadir un nuevo elemento para despertar a los consumidores.

### Ejercicio 2: Threads de Recuperación (Consumidores)
Múltiples hilos concurrentes que extraen objetos `EncryptedFile` del buffer. Realizan el descifrado real utilizando el motor AES-256 y guardan el resultado en el directorio de restaurados.

### Ejercicio 3: Gestión de Prioridades
Implementación de lógica de priorización dinámica basada en el peso de los archivos:
*   **Prioridad ALTA (`Thread.MAX_PRIORITY`)**: Asignada a archivos pequeños ($\le 50KB$) para garantizar una respuesta rápida en archivos ligeros.
*   **Prioridad BAJA (`Thread.MIN_PRIORITY`)**: Asignada a archivos grandes ($> 50KB$) para evitar que monopen el tiempo de CPU.

### Ejercicio 4: Monitor de Estado de Threads
Un hilo de alta prioridad que supervisa la salud del sistema. Muestra una tabla comparativa con los estados de la JVM:
- **NEW**: Hilo creado pero no iniciado.
- **RUNNABLE**: Hilo en ejecución.
- **WAITING / BLOCKED**: Hilo esperando recursos o turno.
- **TERMINATED**: Tarea finalizada con éxito.

### Ejercicio 5: Simulación PCS vs SCS
Estudio del comportamiento del planificador:
- **PCS (Process Contention Scope)**: Los hilos compiten por recursos dentro del proceso.
- **SCS (System Contention Scope)**: Los hilos compiten directamente a nivel de núcleo de sistema operativo.

---

## 🔐 Detalles Técnicos de Cifrado
El sistema emplea **AES-256 en modo CBC** para asegurar un descifrado robusto:
- **Algoritmo**: `AES/CBC/PKCS5Padding`.
- **Derivación de clave**: PBKDF2 con HMAC-SHA256 para transformar la contraseña en una clave de 256 bits.
- **Seguridad**: Cada archivo contiene su propio Salt e IV (Vector de Inicialización) de 16 bytes para evitar ataques de diccionario.

---

## 📈 Resultados de Rendimiento

| Métrica | Secuencial (1 Thread) | Paralelo (3 Threads) |
| :--- | :--- | :--- |
| **Tiempo total** | 2,456 ms | 847 ms |
| **Mejora** | - | **2.9x más rápido** |
| **Uso de CPU** | ~25% | ~75% |

---

## 🤝 Contribuciones
1. Haz un **Fork** del proyecto.
2. Crea una nueva rama (`git checkout -b feature/MejoraIncreible`).
3. Confirma tus cambios (`git commit -m 'Añadida nueva funcionalidad'`).
4. Sube tus cambios (`git push origin feature/MejoraIncreible`).
5. Abre un **Pull Request**.

---

## 👥 Autor
**Kevin Yamel Diaz Perez** - *Lider de Desarrollo* - [GitHub](https://github.com/tu-usuario)

---

## 📄 Licencia
Este proyecto está bajo la Licencia **MIT**. Consulta el archivo `LICENSE` para más detalles.
