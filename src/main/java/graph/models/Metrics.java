// src/main/java/graph/models/Metrics.java
package graph.models;

public class Metrics {
    private long startTime;
    private long endTime;
    private int dfsVisits;
    private int edgeTraversals;
    private int queueOperations;
    private int relaxations;

    public void startTimer() {
        startTime = System.nanoTime();
    }

    public void stopTimer() {
        endTime = System.nanoTime();
    }

    public long getElapsedTime() {
        return endTime - startTime;
    }

    // Геттеры и инкременты для счетчиков
    public int getDfsVisits() { return dfsVisits; }
    public void incrementDfsVisits() { dfsVisits++; }
    public void incrementDfsVisits(int count) { dfsVisits += count; }

    public int getEdgeTraversals() { return edgeTraversals; }
    public void incrementEdgeTraversals() { edgeTraversals++; }
    public void incrementEdgeTraversals(int count) { edgeTraversals += count; }

    public int getQueueOperations() { return queueOperations; }
    public void incrementQueueOperations() { queueOperations++; }

    public int getRelaxations() { return relaxations; }
    public void incrementRelaxations() { relaxations++; }

    public void reset() {
        dfsVisits = 0;
        edgeTraversals = 0;
        queueOperations = 0;
        relaxations = 0;
    }

    @Override
    public String toString() {
        return String.format(
                "Metrics[time=%d ns, dfsVisits=%d, edgeTraversals=%d, queueOps=%d, relaxations=%d]",
                getElapsedTime(), dfsVisits, edgeTraversals, queueOperations, relaxations
        );
    }
}