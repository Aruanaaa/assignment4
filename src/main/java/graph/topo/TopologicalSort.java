package graph.topo;

import graph.Graph;
import graph.metrics.Metrics;

import java.util.*;

public class TopologicalSort {
    private final Graph graph;
    private final Metrics metrics;

    public TopologicalSort(Graph graph, Metrics metrics) {
        this.graph = graph;
        this.metrics = metrics;
    }

    public List<Integer> topologicalOrder() {
        int n = graph.getN();
        int[] inDegree = new int[n];

        // Calculate in-degreess
        for (int u = 0; u < n; u++) {
            for (Graph.Edge edge : graph.getEdges(u)) {
                inDegree[edge.v]++;
                metrics.incrementOperation("Degree calculations");
            }
        }

        Queue<Integer> queue = new LinkedList<>();
        for (int i = 0; i < n; i++) {
            if (inDegree[i] == 0) {
                queue.add(i);
                metrics.incrementOperation("Queue pushes");
            }
        }

        List<Integer> result = new ArrayList<>();
        while (!queue.isEmpty()) {
            int u = queue.poll();
            metrics.incrementOperation("Queue pops");
            result.add(u);

            for (Graph.Edge edge : graph.getEdges(u)) {
                inDegree[edge.v]--;
                if (inDegree[edge.v] == 0) {
                    queue.add(edge.v);
                    metrics.incrementOperation("Queue pushes");
                }
            }
        }

        return result;
    }
}