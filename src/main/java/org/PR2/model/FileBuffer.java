package org.PR2.model;

import org.PR2.model.EncryptedFile;

import java.util.LinkedList;
import java.util.Queue;

public class FileBuffer {
    private final Queue<EncryptedFile> buffer;
    private final int capacity;
    private boolean finished = false;

    public FileBuffer(int capacity) {
        this.buffer = new LinkedList<>();
        this.capacity = capacity;
    }

    public synchronized void put(EncryptedFile file) throws InterruptedException {
        while (buffer.size() >= capacity) {
            System.out.println("Buffer lleno. Productor esperando...");
            wait();
        }
        buffer.add(file);
        System.out.println("Producido: " + file.getName() + " | Buffer: " + buffer.size() + "/" + capacity);
        notifyAll();
    }

    public synchronized EncryptedFile take() throws InterruptedException {
        while (buffer.isEmpty() && !finished) {
            wait();
        }
        if (buffer.isEmpty() && finished) {
            return null;
        }
        EncryptedFile file = buffer.poll();
        System.out.println("Consumido: " + file.getName() + " | Buffer: " + buffer.size() + "/" + capacity);
        notifyAll();
        return file;
    }

    public synchronized void setFinished(boolean finished) {
        this.finished = finished;
        notifyAll();
    }
    // Añadir estos métodos a la clase FileBuffer

    public synchronized int getSize() {
        return buffer.size();
    }

    public synchronized void printContents() {
        if (buffer.isEmpty()) {
            System.out.println("  (buffer vacío)");
        } else {
            for (EncryptedFile file : buffer) {
                System.out.println( file.getName() + " (" + file.getSize() + " bytes)");
            }
        }
    }

    public synchronized boolean isEmpty() {
        return buffer.isEmpty();
    }

}