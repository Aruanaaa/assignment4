// src/test/java/graph/topo/TopologicalSortTest.java
package graph.topo;

import graph.models.Graph;
import graph.models.Node;
import graph.models.Edge;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

public class TopologicalSortTest {

    @Test
    public void testSimpleDAG() {
        Graph graph = new Graph();
        graph.addNode(new Node("A", 1));
        graph.addNode(new Node("B", 2));
        graph.addNode(new Node("C", 3));

        graph.addEdge(new Edge("A", "B", 1));
        graph.addEdge(new Edge("B", "C", 1));
        graph.addEdge(new Edge("A", "C", 1));

        TopologicalSort topo = new TopologicalSort();
        List<Node> order = topo.topologicalOrder(graph);

        assertEquals(3, order.size());
        // A должен быть перед B и C, B перед C
        assertTrue(order.indexOf(graph.getNodeById("A")) < order.indexOf(graph.getNodeById("B")));
        assertTrue(order.indexOf(graph.getNodeById("B")) < order.indexOf(graph.getNodeById("C")));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCyclicGraph() {
        Graph graph = new Graph();
        graph.addNode(new Node("A", 1));
        graph.addNode(new Node("B", 2));

        graph.addEdge(new Edge("A", "B", 1));
        graph.addEdge(new Edge("B", "A", 1)); // Создает цикл

        TopologicalSort topo = new TopologicalSort();
        topo.topologicalOrder(graph); // Должен бросить исключение
    }

    @Test
    public void testMultipleSources() {
        Graph graph = new Graph();
        graph.addNode(new Node("A", 1));
        graph.addNode(new Node("B", 2));
        graph.addNode(new Node("C", 3));
        graph.addNode(new Node("D", 4));

        // A и B - источники (нет входящих ребер)
        graph.addEdge(new Edge("A", "C", 1));
        graph.addEdge(new Edge("B", "C", 1));
        graph.addEdge(new Edge("C", "D", 1));

        TopologicalSort topo = new TopologicalSort();
        List<Node> order = topo.topologicalOrder(graph);

        assertEquals(4, order.size());
        // D должен быть последним
        assertEquals("D", order.get(3).getId());
        // C должен быть перед D
        assertTrue(order.indexOf(graph.getNodeById("C")) < order.indexOf(graph.getNodeById("D")));
    }
}