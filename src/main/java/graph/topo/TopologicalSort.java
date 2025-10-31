// src/main/java/graph/topo/TopologicalSort.java
package graph.topo;

import graph.models.Graph;
import graph.models.Node;
import graph.models.Edge;
import graph.models.Metrics;

import java.util.*;

public class TopologicalSort {
    private Metrics metrics;

    public TopologicalSort() {
        this.metrics = new Metrics();
    }

    public List<Node> topologicalOrder(Graph graph) {
        metrics.startTimer();
        metrics.reset();

        Map<Node, Integer> inDegree = new HashMap<>();
        Queue<Node> queue = new LinkedList<>();
        List<Node> result = new ArrayList<>();

        // Инициализация in-degree
        for (Node node : graph.getNodes()) {
            inDegree.put(node, 0);
        }

        // Вычисление in-degree
        for (Edge edge : graph.getEdges()) {
            Node toNode = graph.getNodeById(edge.getTo());
            inDegree.put(toNode, inDegree.get(toNode) + 1);
            metrics.incrementEdgeTraversals();
        }

        // Добавление узлов с in-degree = 0 в очередь
        for (Node node : graph.getNodes()) {
            if (inDegree.get(node) == 0) {
                queue.offer(node);
                metrics.incrementQueueOperations();
            }
        }

        // Обработка очереди
        while (!queue.isEmpty()) {
            Node current = queue.poll();
            metrics.incrementQueueOperations();
            result.add(current);

            // Уменьшаем in-degree соседей
            for (Edge edge : graph.getEdges()) {
                if (edge.getFrom().equals(current.getId())) {
                    metrics.incrementEdgeTraversals();
                    Node neighbor = graph.getNodeById(edge.getTo());
                    inDegree.put(neighbor, inDegree.get(neighbor) - 1);

                    if (inDegree.get(neighbor) == 0) {
                        queue.offer(neighbor);
                        metrics.incrementQueueOperations();
                    }
                }
            }
        }

        metrics.stopTimer();

        // Проверка на циклы
        if (result.size() != graph.getNodes().size()) {
            throw new IllegalArgumentException("Graph has cycles - topological sort not possible");
        }

        return result;
    }

    public Metrics getMetrics() {
        return metrics;
    }
}