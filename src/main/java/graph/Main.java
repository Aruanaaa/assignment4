// src/main/java/graph/Main.java
package graph;

import graph.models.Graph;
import graph.models.Node;
import graph.models.SCCResult;
import graph.models.Metrics;
import graph.scc.TarjanSCC;
import graph.topo.TopologicalSort;
import graph.dagsp.DAGShortestPath;
import graph.dagsp.DAGShortestPath.CriticalPathResult;
import graph.dagsp.DAGShortestPath.ShortestPathResult;
import graph.utils.JsonGraphLoader;

import java.util.*;

/**
 * Главный класс для запуска анализа графов
 * Выполняет все требуемые алгоритмы и выводит результаты
 */
public class Main {

    public static void main(String[] args) {
        try {
            // Анализ всех datasets
            List<String> datasets = Arrays.asList(
                    "small_1.json", "small_2.json", "small_3.json",
                    "medium_1.json", "medium_2.json", "medium_3.json",
                    "large_1.json", "large_2.json", "large_3.json"
            );

            for (String dataset : datasets) {
                System.out.println("=== Analyzing " + dataset + " ===");
                analyzeDataset(dataset);
                System.out.println();
            }

        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void analyzeDataset(String filename) throws Exception {
        // Загрузка графа
        Graph graph = JsonGraphLoader.loadGraph(filename);
        System.out.println("Graph loaded: " + graph.getNodes().size() + " nodes, " +
                graph.getEdges().size() + " edges");

        // 1. Поиск сильно связных компонент
        System.out.println("\n1. STRONGLY CONNECTED COMPONENTS");
        TarjanSCC tarjan = new TarjanSCC();
        SCCResult sccResult = tarjan.findSCC(graph);

        List<List<Node>> components = sccResult.getComponents();
        System.out.println("Found " + components.size() + " SCCs:");
        for (int i = 0; i < components.size(); i++) {
            System.out.println("  SCC " + i + ": " + components.get(i) +
                    " (size: " + components.get(i).size() + ")");
        }
        System.out.println("SCC Metrics: " + tarjan.getMetrics());

        // 2. Топологическая сортировка condensation graph
        System.out.println("\n2. TOPOLOGICAL SORT");
        TopologicalSort topo = new TopologicalSort();
        List<Node> topoOrder = topo.topologicalOrder(sccResult.getCondensationGraph());

        System.out.println("Topological order of condensation graph:");
        System.out.println("  " + topoOrder);
        System.out.println("Topo Sort Metrics: " + topo.getMetrics());

        // 3. Кратчайшие пути в DAG
        System.out.println("\n3. SHORTEST PATHS IN DAG");
        DAGShortestPath dagSp = new DAGShortestPath();

        // Выбираем первый узел condensation graph как источник
        if (!sccResult.getCondensationGraph().getNodes().isEmpty()) {
            Node source = sccResult.getCondensationGraph().getNodes().get(0);

            ShortestPathResult shortestResult = dagSp.shortestPath(
                    sccResult.getCondensationGraph(), source);

            System.out.println("Shortest paths from " + source + ":");
            for (Map.Entry<Node, Integer> entry : shortestResult.getDistances().entrySet()) {
                if (entry.getValue() != Integer.MAX_VALUE) {
                    System.out.println("  to " + entry.getKey() + ": " + entry.getValue());
                }
            }
        }

        // 4. Критический путь
        System.out.println("\n4. CRITICAL PATH (LONGEST PATH)");
        CriticalPathResult criticalPath = dagSp.criticalPath(sccResult.getCondensationGraph());

        System.out.println("Critical path length: " + criticalPath.getLength());
        System.out.println("Critical path: " + criticalPath.getPath());
        System.out.println("DAG SP Metrics: " + dagSp.getMetrics());

        // Статистика по графу
        printGraphStatistics(graph, components);
    }

    private static void printGraphStatistics(Graph graph, List<List<Node>> components) {
        System.out.println("\nGRAPH STATISTICS:");
        System.out.println("  Total nodes: " + graph.getNodes().size());
        System.out.println("  Total edges: " + graph.getEdges().size());

        // Анализ компонент
        int trivialComponents = 0;
        int nonTrivialComponents = 0;
        int largestComponentSize = 0;

        for (List<Node> component : components) {
            if (component.size() == 1) {
                trivialComponents++;
            } else {
                nonTrivialComponents++;
            }
            largestComponentSize = Math.max(largestComponentSize, component.size());
        }

        System.out.println("  Trivial SCCs (size 1): " + trivialComponents);
        System.out.println("  Non-trivial SCCs: " + nonTrivialComponents);
        System.out.println("  Largest SCC size: " + largestComponentSize);

        // Плотность графа
        int n = graph.getNodes().size();
        int maxEdges = n * (n - 1);
        double density = (double) graph.getEdges().size() / maxEdges;
        System.out.println("  Graph density: " + String.format("%.4f", density));
    }
}