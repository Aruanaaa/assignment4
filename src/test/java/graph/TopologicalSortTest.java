package graph;

import graph.topo.TopologicalSort;
import graph.metrics.Metrics;
import org.junit.Test;
import org.junit.Before;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;

public class TopologicalSortTest {

    private Graph simpleDAG;
    private Graph linearDAG;
    private Graph singleNodeGraph;
    private Metrics metrics;

    @Before
    public void setUp() {
        metrics = new Metrics();

        // Simple DAG: 0 -> 1 -> 2
        List<Graph.Node> nodes = Arrays.asList(
                new Graph.Node(0, "A", 1),
                new Graph.Node(1, "B", 2),
                new Graph.Node(2, "C", 3)
        );
        simpleDAG = new Graph(3, nodes, "edge");
        simpleDAG.addEdge(0, 1, 1);
        simpleDAG.addEdge(1, 2, 1);

        // Linear DAG: 0 -> 1 -> 2 -> 3
        List<Graph.Node> linearNodes = Arrays.asList(
                new Graph.Node(0, "A", 1),
                new Graph.Node(1, "B", 2),
                new Graph.Node(2, "C", 3),
                new Graph.Node(3, "D", 4)
        );
        linearDAG = new Graph(4, linearNodes, "edge");
        linearDAG.addEdge(0, 1, 1);
        linearDAG.addEdge(1, 2, 1);
        linearDAG.addEdge(2, 3, 1);

        // Single node graph
        List<Graph.Node> singleNode = Arrays.asList(
                new Graph.Node(0, "A", 1)
        );
        singleNodeGraph = new Graph(1, singleNode, "edge");
    }

    @Test
    public void testTopologicalOrderSimpleDAG() {
        TopologicalSort topo = new TopologicalSort(simpleDAG, metrics);
        List<Integer> order = topo.topologicalOrder();

        assertEquals(3, order.size());
        assertTrue(order.indexOf(0) < order.indexOf(1));
        assertTrue(order.indexOf(1) < order.indexOf(2));
    }

    @Test
    public void testTopologicalOrderLinearDAG() {
        TopologicalSort topo = new TopologicalSort(linearDAG, metrics);
        List<Integer> order = topo.topologicalOrder();

        assertEquals(4, order.size());
        // Check linear order
        for (int i = 0; i < order.size() - 1; i++) {
            assertTrue(order.indexOf(i) < order.indexOf(i + 1));
        }
    }

    @Test
    public void testTopologicalOrderSingleNode() {
        TopologicalSort topo = new TopologicalSort(singleNodeGraph, metrics);
        List<Integer> order = topo.topologicalOrder();

        assertEquals(1, order.size());
        assertEquals(0, (int)order.get(0));
    }

    @Test
    public void testTopologicalOrderComplexDAG() {
        // More complex DAG with multiple paths
        List<Graph.Node> nodes = Arrays.asList(
                new Graph.Node(0, "A", 1),
                new Graph.Node(1, "B", 2),
                new Graph.Node(2, "C", 3),
                new Graph.Node(3, "D", 4),
                new Graph.Node(4, "E", 5)
        );
        Graph complexDAG = new Graph(5, nodes, "edge");
        complexDAG.addEdge(0, 1, 1);
        complexDAG.addEdge(0, 2, 1);
        complexDAG.addEdge(1, 3, 1);
        complexDAG.addEdge(2, 3, 1);
        complexDAG.addEdge(3, 4, 1);

        TopologicalSort topo = new TopologicalSort(complexDAG, metrics);
        List<Integer> order = topo.topologicalOrder();

        assertEquals(5, order.size());
        // Verify dependencies
        assertTrue(order.indexOf(0) < order.indexOf(1));
        assertTrue(order.indexOf(0) < order.indexOf(2));
        assertTrue(order.indexOf(1) < order.indexOf(3));
        assertTrue(order.indexOf(2) < order.indexOf(3));
        assertTrue(order.indexOf(3) < order.indexOf(4));
    }

    @Test
    public void testTopologicalOrderEmptyGraph() {
        List<Graph.Node> nodes = Arrays.asList();
        Graph emptyGraph = new Graph(0, nodes, "edge");

        TopologicalSort topo = new TopologicalSort(emptyGraph, metrics);
        List<Integer> order = topo.topologicalOrder();

        assertTrue(order.isEmpty());
    }

    @Test
    public void testTopologicalOrderMetrics() {
        TopologicalSort topo = new TopologicalSort(simpleDAG, metrics);
        topo.topologicalOrder();

        // Should have operations recorded
        assertTrue(metrics.getOperationCount("Queue pushes") > 0);
        assertTrue(metrics.getOperationCount("Queue pops") > 0);
        assertTrue(metrics.getOperationCount("Degree calculations") > 0);
    }
}