// src/main/java/graph/models/Graph.java
package graph.models;

import java.util.ArrayList;
import java.util.List;

public class Graph {
    private List<Node> nodes;
    private List<Edge> edges;

    public Graph() {
        this.nodes = new ArrayList<>();
        this.edges = new ArrayList<>();
    }

    // Геттеры и сеттеры
    public List<Node> getNodes() { return nodes; }
    public void setNodes(List<Node> nodes) { this.nodes = nodes; }

    public List<Edge> getEdges() { return edges; }
    public void setEdges(List<Edge> edges) { this.edges = edges; }

    public void addNode(Node node) { nodes.add(node); }
    public void addEdge(Edge edge) { edges.add(edge); }

    public Node getNodeById(String id) {
        return nodes.stream().filter(n -> n.getId().equals(id)).findFirst().orElse(null);
    }
}