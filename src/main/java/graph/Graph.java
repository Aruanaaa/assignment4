package graph;

import java.util.*;

public class Graph {
    private final int n;
    private final List<List<Edge>> adj;
    private final List<Node> nodes;
    private final String weightModel;

    public Graph(int n, List<Node> nodes, String weightModel) {
        this.n = n;
        this.nodes = nodes;
        this.weightModel = weightModel;
        this.adj = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            adj.add(new ArrayList<>());
        }
    }

    public void addEdge(int u, int v, double weight) {
        adj.get(u).add(new Edge(u, v, weight));
    }

    public List<Edge> getEdges(int u) {
        return adj.get(u);
    }

    public int getN() { return n; }
    public List<Node> getNodes() { return nodes; }
    public String getWeightModel() { return weightModel; }

    public Graph getTranspose() {
        Graph transpose = new Graph(n, nodes, weightModel);
        for (int u = 0; u < n; u++) {
            for (Edge edge : adj.get(u)) {
                transpose.addEdge(edge.v, edge.u, edge.weight);
            }
        }
        return transpose;
    }

    public static class Node {
        public final int id;
        public final String label;
        public final double duration;

        public Node(int id, String label, double duration) {
            this.id = id;
            this.label = label;
            this.duration = duration;
        }
    }

    public static class Edge {
        public final int u, v;
        public final double weight;

        public Edge(int u, int v, double weight) {
            this.u = u;
            this.v = v;
            this.weight = weight;
        }
    }
}