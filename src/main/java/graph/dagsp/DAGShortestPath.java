// src/main/java/graph/dagsp/DAGShortestPath.java
package graph.dagsp;

import graph.models.Graph;
import graph.models.Node;
import graph.models.Edge;
import graph.models.Metrics;
import graph.topo.TopologicalSort;

import java.util.*;

/**
 * Реализация алгоритмов поиска кратчайших и самых длинных путей в DAG
 * Использует топологическую сортировку для эффективного вычисления путей
 *
 * Выбор модели весов: используем веса ребер (edge weights) для вычисления путей,
 * так как это соответствует стандартным алгоритмам поиска путей в графах
 */
public class DAGShortestPath {
    private Metrics metrics;

    public DAGShortestPath() {
        this.metrics = new Metrics();
    }

    /**
     * Вычисляет кратчайшие пути от исходной вершины до всех остальных в DAG
     */
    public ShortestPathResult shortestPath(Graph graph, Node source) {
        metrics.startTimer();
        metrics.reset();

        // Получаем топологический порядок
        TopologicalSort topo = new TopologicalSort();
        List<Node> topologicalOrder = topo.topologicalOrder(graph);

        // Инициализация расстояний
        Map<Node, Integer> distances = new HashMap<>();
        Map<Node, Node> predecessors = new HashMap<>();

        for (Node node : graph.getNodes()) {
            distances.put(node, Integer.MAX_VALUE);
            predecessors.put(node, null);
        }
        distances.put(source, 0);

        // Обрабатываем вершины в топологическом порядке
        for (Node node : topologicalOrder) {
            metrics.incrementDfsVisits();

            // Если до вершины еще не дошли, пропускаем
            if (distances.get(node) == Integer.MAX_VALUE) {
                continue;
            }

            // Релаксация всех исходящих ребер
            for (Edge edge : graph.getEdges()) {
                if (edge.getFrom().equals(node.getId())) {
                    metrics.incrementEdgeTraversals();
                    Node neighbor = graph.getNodeById(edge.getTo());
                    int newDistance = distances.get(node) + edge.getWeight();

                    if (newDistance < distances.get(neighbor)) {
                        distances.put(neighbor, newDistance);
                        predecessors.put(neighbor, node);
                        metrics.incrementRelaxations();
                    }
                }
            }
        }

        metrics.stopTimer();
        return new ShortestPathResult(distances, predecessors, source);
    }

    /**
     * Вычисляет самые длинные пути (критический путь) в DAG
     * Использует инвертирование весов и поиск кратчайшего пути
     */
    public CriticalPathResult longestPath(Graph graph) {
        metrics.startTimer();
        metrics.reset();

        // Создаем копию графа с инвертированными весами
        Graph invertedGraph = createGraphWithInvertedWeights(graph);

        // Находим все вершины с входящей степенью 0 (источники)
        List<Node> sources = findSourceNodes(graph);

        // Вычисляем самый длинный путь из каждого источника
        CriticalPathResult bestResult = null;

        for (Node source : sources) {
            ShortestPathResult result = shortestPath(invertedGraph, source);

            // Инвертируем расстояния обратно
            Map<Node, Integer> originalDistances = new HashMap<>();
            for (Map.Entry<Node, Integer> entry : result.getDistances().entrySet()) {
                originalDistances.put(entry.getKey(),
                        entry.getValue() == Integer.MAX_VALUE ?
                                Integer.MIN_VALUE : -entry.getValue());
            }

            // Находим максимальное расстояние и соответствующую вершину
            for (Map.Entry<Node, Integer> entry : originalDistances.entrySet()) {
                if (bestResult == null || entry.getValue() > bestResult.getLength()) {
                    // Восстанавливаем путь
                    List<Node> path = reconstructPath(entry.getKey(), result.getPredecessors());
                    bestResult = new CriticalPathResult(path, entry.getValue());
                }
            }
        }

        metrics.stopTime();
        return bestResult;
    }

    /**
     * Альтернативная реализация поиска критического пути без инвертирования весов
     */
    public CriticalPathResult criticalPath(Graph graph) {
        metrics.startTimer();
        metrics.reset();

        TopologicalSort topo = new TopologicalSort();
        List<Node> topologicalOrder = topo.topologicalOrder(graph);

        Map<Node, Integer> longestDistances = new HashMap<>();
        Map<Node, Node> predecessors = new HashMap<>();

        // Инициализация
        for (Node node : graph.getNodes()) {
            longestDistances.put(node, 0); // Для longest path инициализируем 0
            predecessors.put(node, null);
        }

        // Вычисляем самые длинные пути
        for (Node node : topologicalOrder) {
            metrics.incrementDfsVisits();

            for (Edge edge : graph.getEdges()) {
                if (edge.getFrom().equals(node.getId())) {
                    metrics.incrementEdgeTraversals();
                    Node neighbor = graph.getNodeById(edge.getTo());
                    int newDistance = longestDistances.get(node) + edge.getWeight();

                    if (newDistance > longestDistances.get(neighbor)) {
                        longestDistances.put(neighbor, newDistance);
                        predecessors.put(neighbor, node);
                        metrics.incrementRelaxations();
                    }
                }
            }
        }

        // Находим вершину с максимальным расстоянием
        Node target = null;
        int maxDistance = Integer.MIN_VALUE;

        for (Map.Entry<Node, Integer> entry : longestDistances.entrySet()) {
            if (entry.getValue() > maxDistance) {
                maxDistance = entry.getValue();
                target = entry.getKey();
            }
        }

        // Восстанавливаем путь
        List<Node> path = reconstructPath(target, predecessors);

        metrics.stopTimer();
        return new CriticalPathResult(path, maxDistance);
    }

    private Graph createGraphWithInvertedWeights(Graph original) {
        Graph inverted = new Graph();

        // Копируем узлы
        for (Node node : original.getNodes()) {
            inverted.addNode(new Node(node.getId(), node.getDuration()));
        }

        // Копируем ребра с инвертированными весами
        for (Edge edge : original.getEdges()) {
            inverted.addEdge(new Edge(edge.getFrom(), edge.getTo(), -edge.getWeight()));
        }

        return inverted;
    }

    private List<Node> findSourceNodes(Graph graph) {
        Set<Node> hasIncoming = new HashSet<>();

        for (Edge edge : graph.getEdges()) {
            Node toNode = graph.getNodeById(edge.getTo());
            hasIncoming.add(toNode);
        }

        List<Node> sources = new ArrayList<>();
        for (Node node : graph.getNodes()) {
            if (!hasIncoming.contains(node)) {
                sources.add(node);
            }
        }

        return sources;
    }

    private List<Node> reconstructPath(Node target, Map<Node, Node> predecessors) {
        List<Node> path = new ArrayList<>();
        Node current = target;

        while (current != null) {
            path.add(0, current); // Добавляем в начало
            current = predecessors.get(current);
        }

        return path;
    }

    public Metrics getMetrics() {
        return metrics;
    }

    // Классы результатов
    public static class ShortestPathResult {
        private Map<Node, Integer> distances;
        private Map<Node, Node> predecessors;
        private Node source;

        public ShortestPathResult(Map<Node, Integer> distances, Map<Node, Node> predecessors, Node source) {
            this.distances = distances;
            this.predecessors = predecessors;
            this.source = source;
        }

        public Map<Node, Integer> getDistances() { return distances; }
        public Map<Node, Node> getPredecessors() { return predecessors; }
        public Node getSource() { return source; }

        public List<Node> getPathTo(Node target) {
            return reconstructPath(target, predecessors);
        }
    }

    public static class CriticalPathResult {
        private List<Node> path;
        private int length;

        public CriticalPathResult(List<Node> path, int length) {
            this.path = path;
            this.length = length;
        }

        public List<Node> getPath() { return path; }
        public int getLength() { return length; }
    }
}