package graph.io;

import graph.Graph;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

public class JSONReader {

    public static Graph readGraph(String filename) throws Exception {
        BufferedReader reader = new BufferedReader(new FileReader(filename));
        StringBuilder jsonContent = new StringBuilder();
        String line;

        while ((line = reader.readLine()) != null) {
            jsonContent.append(line);
        }
        reader.close();

        String content = jsonContent.toString().replaceAll("\\s+", "");
        return parseGraph(content);
    }

    private static Graph parseGraph(String content) {
        // Extract basic information
        int n = extractIntValue(content, "\"n\":");
        String weightModel = extractStringValue(content, "\"weight_model\":");

        // Extract nodes
        String nodesSection = extractSection(content, "\"nodes\":[", "]");
        List<Graph.Node> nodes = parseNodes(nodesSection);

        // Create graph
        Graph graph = new Graph(n, nodes, weightModel);

        // Extract and add edges
        String edgesSection = extractSection(content, "\"edges\":[", "]");
        parseEdges(edgesSection, graph);

        return graph;
    }

    private static List<Graph.Node> parseNodes(String nodesSection) {
        List<Graph.Node> nodes = new ArrayList<>();
        String[] nodeStrings = nodesSection.split("\\},\\{");

        for (String nodeStr : nodeStrings) {
            nodeStr = nodeStr.replaceAll("[{}\"]", "");
            String[] parts = nodeStr.split(",");

            int id = -1;
            String label = "";
            double duration = 0;

            for (String part : parts) {
                if (part.startsWith("id:")) {
                    id = Integer.parseInt(part.substring(3));
                } else if (part.startsWith("label:")) {
                    label = part.substring(6);
                } else if (part.startsWith("duration:")) {
                    duration = Double.parseDouble(part.substring(9));
                }
            }

            if (id != -1) {
                nodes.add(new Graph.Node(id, label, duration));
            }
        }

        return nodes;
    }

    private static void parseEdges(String edgesSection, Graph graph) {
        if (edgesSection == null || edgesSection.isEmpty()) return;

        String[] edgeStrings = edgesSection.split("\\},\\{");

        for (String edgeStr : edgeStrings) {
            edgeStr = edgeStr.replaceAll("[{}\"]", "");
            String[] parts = edgeStr.split(",");

            int u = -1, v = -1;
            double w = 0;

            for (String part : parts) {
                if (part.startsWith("u:")) {
                    u = Integer.parseInt(part.substring(2));
                } else if (part.startsWith("v:")) {
                    v = Integer.parseInt(part.substring(2));
                } else if (part.startsWith("w:")) {
                    w = Double.parseDouble(part.substring(2));
                }
            }

            if (u != -1 && v != -1) {
                graph.addEdge(u, v, w);
            }
        }
    }

    private static int extractIntValue(String content, String key) {
        int start = content.indexOf(key) + key.length();
        int end = content.indexOf(",", start);
        if (end == -1) end = content.indexOf("}", start);
        return Integer.parseInt(content.substring(start, end).trim());
    }

    private static String extractStringValue(String content, String key) {
        int start = content.indexOf(key) + key.length();
        start = content.indexOf("\"", start) + 1;
        int end = content.indexOf("\"", start);
        return content.substring(start, end);
    }

    private static String extractSection(String content, String startKey, String endChar) {
        int start = content.indexOf(startKey);
        if (start == -1) return "";
        start += startKey.length();

        int bracketCount = 1;
        int end = start;
        while (end < content.length() && bracketCount > 0) {
            char c = content.charAt(end);
            if (c == '[' || c == '{') bracketCount++;
            else if (c == ']' || c == '}') bracketCount--;
            end++;
        }

        return content.substring(start, end - 1);
    }
}