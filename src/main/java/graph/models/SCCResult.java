// src/main/java/graph/models/SCCResult.java
package graph.models;

import java.util.List;
import java.util.ArrayList;

public class SCCResult {
    private List<List<Node>> components;
    private Graph condensationGraph;
    private List<Node> topologicalOrder;

    public SCCResult() {
        this.components = new ArrayList<>();
    }

    // Геттеры и сеттеры
    public List<List<Node>> getComponents() { return components; }
    public void setComponents(List<List<Node>> components) { this.components = components; }

    public Graph getCondensationGraph() { return condensationGraph; }
    public void setCondensationGraph(Graph condensationGraph) { this.condensationGraph = condensationGraph; }

    public List<Node> getTopologicalOrder() { return topologicalOrder; }
    public void setTopologicalOrder(List<Node> topologicalOrder) { this.topologicalOrder = topologicalOrder; }

    public void addComponent(List<Node> component) {
        components.add(component);
    }
}