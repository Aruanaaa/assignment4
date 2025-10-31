// src/main/java/graph/scc/TarjanSCC.java
package graph.scc;

import graph.models.Graph;
import graph.models.Node;
import graph.models.Edge;
import graph.models.SCCResult;
import graph.models.Metrics;

import java.util.*;

public class TarjanSCC {
    private int index;
    private Stack<Node> stack;
    private Map<Node, Integer> indices;
    private Map<Node, Integer> lowLinks;
    private Set<Node> onStack;
    private List<List<Node>> components;
    private Metrics metrics;

    public TarjanSCC() {
        this.metrics = new Metrics();
    }

    public SCCResult findSCC(Graph graph) {
        metrics.startTimer();
        metrics.reset();

        index = 0;
        stack = new Stack<>();
        indices = new HashMap<>();
        lowLinks = new HashMap<>();
        onStack = new HashSet<>();
        components = new ArrayList<>();

        // Инициализация
        for (Node node : graph.getNodes()) {
            indices.put(node, -1);
            lowLinks.put(node, -1);
        }

        // Запуск DFS для каждого непосещенного узла
        for (Node node : graph.getNodes()) {
            if (indices.get(node) == -1) {
                strongConnect(node, graph);
            }
        }

        metrics.stopTimer();

        SCCResult result = new SCCResult();
        result.setComponents(components);
        result.setCondensationGraph(buildCondensationGraph(graph));

        return result;
    }

    private void strongConnect(Node node, Graph graph) {
        metrics.incrementDfsVisits();

        indices.put(node, index);
        lowLinks.put(node, index);
        index++;
        stack.push(node);
        onStack.add(node);

        // Ищем всех соседей
        for (Edge edge : graph.getEdges()) {
            if (edge.getFrom().equals(node.getId())) {
                metrics.incrementEdgeTraversals();
                Node neighbor = graph.getNodeById(edge.getTo());

                if (indices.get(neighbor) == -1) {
                    strongConnect(neighbor, graph);
                    lowLinks.put(node, Math.min(lowLinks.get(node), lowLinks.get(neighbor)));
                } else if (onStack.contains(neighbor)) {
                    lowLinks.put(node, Math.min(lowLinks.get(node), indices.get(neighbor)));
                }
            }
        }

        // Если node - корень SCC
        if (lowLinks.get(node).equals(indices.get(node))) {
            List<Node> component = new ArrayList<>();
            Node popped;

            do {
                popped = stack.pop();
                onStack.remove(popped);
                component.add(popped);
            } while (!popped.equals(node));

            components.add(component);
        }
    }

    private Graph buildCondensationGraph(Graph originalGraph) {
        Graph condensation = new Graph();
        Map<List<Node>, Node> componentToNode = new HashMap<>();

        // Создаем узлы для каждой компоненты
        for (List<Node> component : components) {
            String componentId = "SCC_" + components.indexOf(component);
            Node condenseNode = new Node(componentId, 0);
            condensation.addNode(condenseNode);
            componentToNode.put(component, condenseNode);
        }

        // Создаем ребра между компонентами
        Set<String> addedEdges = new HashSet<>();

        for (Edge edge : originalGraph.getEdges()) {
            Node fromNode = originalGraph.getNodeById(edge.getFrom());
            Node toNode = originalGraph.getNodeById(edge.getTo());

            List<Node> fromComponent = findComponent(fromNode);
            List<Node> toComponent = findComponent(toNode);

            if (!fromComponent.equals(toComponent)) {
                Node fromCondense = componentToNode.get(fromComponent);
                Node toCondense = componentToNode.get(toComponent);

                String edgeKey = fromCondense.getId() + "->" + toCondense.getId();
                if (!addedEdges.contains(edgeKey)) {
                    condensation.addEdge(new Edge(fromCondense.getId(), toCondense.getId(), edge.getWeight()));
                    addedEdges.add(edgeKey);
                }
            }
        }

        return condensation;
    }

    private List<Node> findComponent(Node node) {
        for (List<Node> component : components) {
            if (component.contains(node)) {
                return component;
            }
        }
        return null;
    }

    public Metrics getMetrics() {
        return metrics;
    }
}