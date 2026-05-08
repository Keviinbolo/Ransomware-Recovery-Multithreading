package org.PR2.threads;


import java.lang.Thread.State;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class MonitorThread extends Thread {
    private final Map<String, Thread> threadsToMonitor;
    private boolean running = true;
    private final int intervalSeconds;

    public MonitorThread(int intervalSeconds) {
        this.threadsToMonitor = new ConcurrentHashMap<>();
        this.intervalSeconds = intervalSeconds;
    }

    public void addThread(Thread thread) {
        threadsToMonitor.put(thread.getName(), thread);
    }

    public void addThreads(Thread... threads) {
        for (Thread thread : threads) {
            addThread(thread);
        }
    }

    public void stopMonitoring() {
        this.running = false;
        this.interrupt();
    }

    @Override
    public void run() {
        System.out.println("[MONITOR] Iniciado - Actualizacion cada " + intervalSeconds + " segundos");

        while (running) {
            try {
                Thread.sleep(intervalSeconds * 1000);

                System.out.println("\n" + "=".repeat(60));
                System.out.println("[MONITOR] ESTADO DE THREADS - " + new java.util.Date());
                System.out.println("=".repeat(60));

                for (Map.Entry<String, Thread> entry : threadsToMonitor.entrySet()) {
                    Thread thread = entry.getValue();
                    State state = thread.getState();
                    String stateStr = getStateString(state);

                    System.out.printf("%-20s : %-12s (Vivo: %s)%n",
                            entry.getKey(),
                            stateStr,
                            thread.isAlive()
                    );
                }

                System.out.println("=".repeat(60));
                System.out.println("[MONITOR] Threads activos en sistema: " + Thread.activeCount());

            } catch (InterruptedException e) {
                if (!running) {
                    break;
                }
            }
        }

        System.out.println("[MONITOR] Finalizado");
    }

    private String getStateString(State state) {
        switch (state) {
            case NEW: return "NEW";
            case RUNNABLE: return "RUNNABLE";
            case BLOCKED: return "BLOCKED";
            case WAITING: return "WAITING";
            case TIMED_WAITING: return "TIMED_WAITING";
            case TERMINATED: return "TERMINATED";
            default: return "UNKNOWN";
        }
    }
}