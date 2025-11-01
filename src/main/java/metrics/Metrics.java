package graph.metrics;

import java.util.HashMap;
import java.util.Map;

public class Metrics {
    private final Map<String, Integer> operations;
    private long startTime;
    private long endTime;

    public Metrics() {
        this.operations = new HashMap<>();
    }

    public void startTimer() {
        startTime = System.nanoTime();
    }

    public void stopTimer() {
        endTime = System.nanoTime();
    }

    public long getElapsedTime() {
        return endTime - startTime;
    }

    public void incrementOperation(String operation) {
        operations.put(operation, operations.getOrDefault(operation, 0) + 1);
    }

    public int getOperationCount(String operation) {
        return operations.getOrDefault(operation, 0);
    }

    public Map<String, Integer> getAllOperations() {
        return new HashMap<>(operations);
    }

    public void reset() {
        operations.clear();
        startTime = 0;
        endTime = 0;
    }
}