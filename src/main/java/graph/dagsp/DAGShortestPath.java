package graph.dagsp;

import graph.Graph;
import graph.metrics.Metrics;

import java.util.*;

public class DAGShortestPath {
    private final Graph graph;
    private final Metrics metrics;

    public DAGShortestPath(Graph graph, Metrics metrics) {
        this.graph = graph;
        this.metrics = metrics;
    }

    public double[] shortestPaths(int source, List<Integer> topologicalOrder) {
        int n = graph.getN();
        double[] dist = new double[n];
        Arrays.fill(dist, Double.POSITIVE_INFINITY);
        dist[source] = 0;

        // Follow topological order
        for (int u : topologicalOrder) {
            metrics.incrementOperation("Relaxations");
            if (dist[u] != Double.POSITIVE_INFINITY) {
                for (Graph.Edge edge : graph.getEdges(u)) {
                    metrics.incrementOperation("Edge relaxations");
                    double newDist = dist[u] + getEdgeWeight(edge);
                    if (newDist < dist[edge.v]) {
                        dist[edge.v] = newDist;
                    }
                }
            }
        }

        return dist;
    }

    public CriticalPathResult findCriticalPath(List<Integer> topologicalOrder) {
        int n = graph.getN();
        double[] longest = new double[n];
        int[] prev = new int[n];
        Arrays.fill(prev, -1);
        Arrays.fill(longest, Double.NEGATIVE_INFINITY);

        // Initialize all nodes
        for (int i = 0; i < n; i++) {
            longest[i] = getNodeDuration(i);
        }

        // Find longest paths
        for (int u : topologicalOrder) {
            metrics.incrementOperation("Longest path relaxations");
            for (Graph.Edge edge : graph.getEdges(u)) {
                double newLength = longest[u] + getEdgeWeight(edge) + getNodeDuration(edge.v);
                if (newLength > longest[edge.v]) {
                    longest[edge.v] = newLength;
                    prev[edge.v] = u;
                }
            }
        }

        // Find the node with maximum distance
        int endNode = 0;
        double maxLength = Double.NEGATIVE_INFINITY;
        for (int i = 0; i < n; i++) {
            if (longest[i] > maxLength) {
                maxLength = longest[i];
                endNode = i;
            }
        }

        // Reconstruct path
        List<Integer> path = reconstructPath(prev, endNode);
        return new CriticalPathResult(path, maxLength);
    }

    private List<Integer> reconstructPath(int[] prev, int endNode) {
        List<Integer> path = new ArrayList<>();
        for (int u = endNode; u != -1; u = prev[u]) {
            path.add(u);
        }
        Collections.reverse(path);
        return path;
    }

    private double getEdgeWeight(Graph.Edge edge) {
        return "node".equals(graph.getWeightModel()) ? 0 : edge.weight;
    }

    private double getNodeDuration(int node) {
        return "node".equals(graph.getWeightModel()) ?
                graph.getNodes().get(node).duration : 0;
    }

    public static class CriticalPathResult {
        public final List<Integer> path;
        public final double length;

        public CriticalPathResult(List<Integer> path, double length) {
            this.path = path;
            this.length = length;
        }
    }
}