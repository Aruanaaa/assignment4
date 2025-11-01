package graph;

import graph.dagsp.DAGShortestPath;
import graph.io.CSVWriter;
import graph.io.JSONReader;
import graph.metrics.Metrics;
import graph.scc.TarjanSCC;
import graph.topo.TopologicalSort;

import java.io.File;
import java.util.*;

public class Main {
    public static void main(String[] args) {
        try {

            String dataDir = "data/";
            String resultsDir = "results/";
            new File(dataDir).mkdirs();
            new File(resultsDir).mkdirs();


            List<String[]> results = new ArrayList<>();
            results.add(new String[]{
                    "Dataset", "Nodes", "Edges", "WeightModel", "SCCs",
                    "SCC_Time(ns)", "SCC_Operations", "Topo_Time(ns)",
                    "Topo_Operations", "SP_Time(ns)", "SP_Operations",
                    "CriticalPath_Length", "Source_Node"
            });

            // Чтение всех файлов JSON из папки data
            File dataFolder = new File(dataDir);
            File[] files = dataFolder.listFiles((dir, name) -> name.endsWith(".json"));


            if (files != null) {
                for (File file : files) {
                    processDataset(file.getPath(), results);
                }
            }

            // Сохранение всех результатов в CSV
            CSVWriter.writeResults(resultsDir + "analysis_results.csv", results);
            System.out.println("Analysis complete! Results saved to results/analysis_results.csv");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private static void processDataset(String filename, List<String[]> results) {
        try {
            System.out.println("Processing: " + filename);

            // Чтение графа из JSON
            Graph graph = JSONReader.readGraph(filename);

            // Основная информация о графе узлы, рёбра, весовая модель
            List<String> datasetResults = new ArrayList<>();
            String datasetName = new File(filename).getName();
            datasetResults.add(datasetName);
            datasetResults.add(String.valueOf(graph.getN()));
            datasetResults.add(String.valueOf(countEdges(graph)));
            datasetResults.add(graph.getWeightModel());

            // Поиск сильно связанных компонент (SCC) с помощью TARJAN
            Metrics sccMetrics = new Metrics();
            sccMetrics.startTimer();
            TarjanSCC tarjan = new TarjanSCC(graph, sccMetrics);
            List<List<Integer>> sccs = tarjan.findSCCs();
            // Построение графа конденсации
            Graph condensation = tarjan.buildCondensationGraph();
            sccMetrics.stopTimer();

            datasetResults.add(String.valueOf(sccs.size()));
            datasetResults.add(String.valueOf(sccMetrics.getElapsedTime()));
            datasetResults.add(formatOperations(sccMetrics.getAllOperations()));

            // Топологическая сортировка конденсационного графа
            Metrics topoMetrics = new Metrics();
            topoMetrics.startTimer();
            TopologicalSort topo = new TopologicalSort(condensation, topoMetrics);
            List<Integer> topoOrder = topo.topologicalOrder();
            topoMetrics.stopTimer();

            datasetResults.add(String.valueOf(topoMetrics.getElapsedTime()));
            datasetResults.add(formatOperations(topoMetrics.getAllOperations()));

            // Кратчайшие пути
            Metrics spMetrics = new Metrics();
            spMetrics.startTimer();
            DAGShortestPath dagSP = new DAGShortestPath(condensation, spMetrics);


            int source = 0;
            // Поиск кратчайших путей в DAG
            double[] distances = dagSP.shortestPaths(source, topoOrder);
            // Поиск длиннейшего пути
            DAGShortestPath.CriticalPathResult criticalPath = dagSP.findCriticalPath(topoOrder);
            spMetrics.stopTimer();

            datasetResults.add(String.valueOf(spMetrics.getElapsedTime()));
            datasetResults.add(String.valueOf(spMetrics.getAllOperations()));
            datasetResults.add(String.valueOf(criticalPath.length));
            datasetResults.add(String.valueOf(source));

            // Добавление данных по датасету в общий список
            results.add(datasetResults.toArray(new String[0]));

            // Вывод краткого отчёта по датасету
            printSummary(datasetName, graph, sccs, condensation, criticalPath);

        } catch (Exception e) {
            System.err.println("Error processing " + filename + ": " + e.getMessage());
            e.printStackTrace();
        }
    }

    //Подсчёт общего количества рёбер в графе
    private static int countEdges(Graph graph) {
        int count = 0;
        for (int i = 0; i < graph.getN(); i++) {
            count += graph.getEdges(i).size();
        }
        return count;
    }

    //  Форматирование операций для CSV
    private static String formatOperations(Map<String, Integer> operations) {
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, Integer> entry : operations.entrySet()) {
            if (sb.length() > 0) sb.append("; ");
            sb.append(entry.getKey()).append(":").append(entry.getValue());
        }
        return sb.toString();
    }

    // Печать краткого отчёта по текущему графу
    private static void printSummary(String datasetName, Graph graph,
                                     List<List<Integer>> sccs, Graph condensation,
                                     DAGShortestPath.CriticalPathResult criticalPath) {
        System.out.println("=== " + datasetName + " Summary ===");
        System.out.println("Nodes: " + graph.getN() + ", Edges: " + countEdges(graph));
        System.out.println("SCCs found: " + sccs.size());

        // Размеры найденных SCC
        List<Integer> sccSizes = new ArrayList<>();
        for (List<Integer> scc : sccs) {
            sccSizes.add(scc.size());
        }
        System.out.println("SCC sizes: " + sccSizes);

        // Итоговые данные по графу
        System.out.println("Condensation graph nodes: " + condensation.getN());
        System.out.println("Critical path length: " + criticalPath.length);
        System.out.println("Critical path: " + criticalPath.path);
        System.out.println();
    }
}
