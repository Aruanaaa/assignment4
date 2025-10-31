// src/main/java/graph/utils/JsonGraphLoader.java
package graph.utils;

import graph.models.Graph;
import graph.models.Node;
import graph.models.Edge;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;

import java.io.InputStream;
import java.util.List;
import java.util.Map;

public class JsonGraphLoader {

    public static Graph loadGraph(String filename) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        InputStream is = JsonGraphLoader.class.getClassLoader().getResourceAsStream("data/" + filename);

        if (is == null) {
            throw new RuntimeException("File not found: data/" + filename);
        }

        Map<String, Object> graphData = mapper.readValue(is, new TypeReference<Map<String, Object>>() {});

        Graph graph = new Graph();

        // Загрузка узлов
        List<Map<String, Object>> nodesData = (List<Map<String, Object>>) graphData.get("nodes");
        for (Map<String, Object> nodeData : nodesData) {
            String id = (String) nodeData.get("id");
            int duration = ((Number) nodeData.get("duration")).intValue();
            graph.addNode(new Node(id, duration));
        }

        // Загрузка ребер
        List<Map<String, Object>> edgesData = (List<Map<String, Object>>) graphData.get("edges");
        for (Map<String, Object> edgeData : edgesData) {
            String from = (String) edgeData.get("from");
            String to = (String) edgeData.get("to");
            int weight = ((Number) edgeData.get("weight")).intValue();
            graph.addEdge(new Edge(from, to, weight));
        }

        return graph;
    }
}