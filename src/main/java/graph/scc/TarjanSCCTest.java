// src/test/java/graph/scc/TarjanSCCTest.java
package graph.scc;

import graph.models.Graph;
import graph.models.Node;
import graph.models.Edge;
import graph.models.SCCResult;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;

public class TarjanSCCTest {

    @Test
    public void testSingleNode() {
        Graph graph = new Graph();
        graph.addNode(new Node("A", 1));

        TarjanSCC tarjan = new TarjanSCC();
        SCCResult result = tarjan.findSCC(graph);

        assertEquals(1, result.getComponents().size());
        assertEquals(1, result.getComponents().get(0).size());
        assertEquals("A", result.getComponents().get(0).get(0).getId());
    }

    @Test
    public void testTwoNodesNoEdges() {
        Graph graph = new Graph();
        graph.addNode(new Node("A", 1));
        graph.addNode(new Node("B", 2));

        TarjanSCC tarjan = new TarjanSCC();
        SCCResult result = tarjan.findSCC(graph);

        assertEquals(2, result.getComponents().size());
    }

    @Test
    public void testTwoNodesWithEdge() {
        Graph graph = new Graph();
        Node a = new Node("A", 1);
        Node b = new Node("B", 2);
        graph.addNode(a);
        graph.addNode(b);
        graph.addEdge(new Edge("A", "B", 1));

        TarjanSCC tarjan = new TarjanSCC();
        SCCResult result = tarjan.findSCC(graph);

        assertEquals(2, result.getComponents().size());
        // Должны быть две отдельные компоненты, так как нет обратного ребра
        assertTrue(result.getComponents().stream().anyMatch(comp -> comp.contains(a) && comp.size() == 1));
        assertTrue(result.getComponents().stream().anyMatch(comp -> comp.contains(b) && comp.size() == 1));
    }

    @Test
    public void testCycle() {
        Graph graph = new Graph();
        Node a = new Node("A", 1);
        Node b = new Node("B", 2);
        Node c = new Node("C", 3);
        graph.addNode(a);
        graph.addNode(b);
        graph.addNode(c);
        graph.addEdge(new Edge("A", "B", 1));
        graph.addEdge(new Edge("B", "C", 1));
        graph.addEdge(new Edge("C", "A", 1));

        TarjanSCC tarjan = new TarjanSCC();
        SCCResult result = tarjan.findSCC(graph);

        assertEquals(1, result.getComponents().size());
        assertEquals(3, result.getComponents().get(0).size());
        assertTrue(result.getComponents().get(0).contains(a));
        assertTrue(result.getComponents().get(0).contains(b));
        assertTrue(result.getComponents().get(0).contains(c));
    }

    @Test
    public void testComplexGraph() {
        Graph graph = new Graph();
        // Создаем граф с несколькими SCC
        for (char c = 'A'; c <= 'F'; c++) {
            graph.addNode(new Node(String.valueOf(c), 1));
        }

        // Цикл A->B->C->A
        graph.addEdge(new Edge("A", "B", 1));
        graph.addEdge(new Edge("B", "C", 1));
        graph.addEdge(new Edge("C", "A", 1));

        // Цикл D->E->D
        graph.addEdge(new Edge("D", "E", 1));
        graph.addEdge(new Edge("E", "D", 1));

        // Отдельная вершина F
        graph.addEdge(new Edge("C", "F", 1));

        TarjanSCC tarjan = new TarjanSCC();
        SCCResult result = tarjan.findSCC(graph);

        // Должно быть 3 компоненты: {A,B,C}, {D,E}, {F}
        assertEquals(3, result.getComponents().size());

        // Проверяем размеры компонент
        boolean foundSize3 = false, foundSize2 = false, foundSize1 = false;
        for (List<Node> component : result.getComponents()) {
            if (component.size() == 3) foundSize3 = true;
            if (component.size() == 2) foundSize2 = true;
            if (component.size() == 1) foundSize1 = true;
        }

        assertTrue("Should have component of size 3", foundSize3);
        assertTrue("Should have component of size 2", foundSize2);
        assertTrue("Should have component of size 1", foundSize1);
    }
}