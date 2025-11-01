package graph;

import graph.dagsp.DAGShortestPath;
import graph.topo.TopologicalSort;
import graph.metrics.Metrics;
import org.junit.Test;
import org.junit.Before;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;

public class DAGShortestPathTest {

    private Graph simpleDAG;
    private Graph weightedDAG;
    private Graph singleNodeGraph;
    private Metrics metrics;

    @Before
    public void setUp() {
        metrics = new Metrics();

        // Simple DAG for shortest path testing
        List<Graph.Node> nodes = Arrays.asList(
                new Graph.Node(0, "A", 1),
                new Graph.Node(1, "B", 1),
                new Graph.Node(2, "C", 1),
                new Graph.Node(3, "D", 1)
        );
        simpleDAG = new Graph(4, nodes, "edge");
        simpleDAG.addEdge(0, 1, 1);
        simpleDAG.addEdge(0, 2, 2);
        simpleDAG.addEdge(1, 3, 1);
        simpleDAG.addEdge(2, 3, 1);

        // Weighted DAG for critical path testing
        List<Graph.Node> weightedNodes = Arrays.asList(
                new Graph.Node(0, "A", 5),
                new Graph.Node(1, "B", 3),
                new Graph.Node(2, "C", 2),
                new Graph.Node(3, "D", 4)
        );
        weightedDAG = new Graph(4, weightedNodes, "edge");
        weightedDAG.addEdge(0, 1, 2);
        weightedDAG.addEdge(0, 2, 1);
        weightedDAG.addEdge(1, 3, 3);
        weightedDAG.addEdge(2, 3, 2);

        // Single node graph
        List<Graph.Node> singleNode = Arrays.asList(
                new Graph.Node(0, "A", 5)
        );
        singleNodeGraph = new Graph(1, singleNode, "edge");
    }

    @Test
    public void testShortestPathsSimpleDAG() {
        TopologicalSort topo = new TopologicalSort(simpleDAG, metrics);
        List<Integer> order = topo.topologicalOrder();

        DAGShortestPath dagSP = new DAGShortestPath(simpleDAG, metrics);
        double[] distances = dagSP.shortestPaths(0, order);

        assertEquals(0.0, distances[0], 0.001);
        assertEquals(1.0, distances[1], 0.001);
        assertEquals(2.0, distances[2], 0.001);
        assertEquals(2.0, distances[3], 0.001); // 0->1->3 = 2
    }

    @Test
    public void testCriticalPathWeightedDAG() {
        TopologicalSort topo = new TopologicalSort(weightedDAG, metrics);
        List<Integer> order = topo.topologicalOrder();

        DAGShortestPath dagSP = new DAGShortestPath(weightedDAG, metrics);
        DAGShortestPath.CriticalPathResult result = dagSP.findCriticalPath(order);

        // Critical path should be 0->1->3 with total duration:
        // node0(5) + edge0-1(2) + node1(3) + edge1-3(3) + node3(4) = 17
        assertEquals(17.0, result.length, 0.001);
        assertEquals(3, result.path.size());
        assertEquals(0, (int)result.path.get(0));
        assertEquals(1, (int)result.path.get(1));
        assertEquals(3, (int)result.path.get(2));
    }

    @Test
    public void testCriticalPathSingleNode() {
        TopologicalSort topo = new TopologicalSort(singleNodeGraph, metrics);
        List<Integer> order = topo.topologicalOrder();

        DAGShortestPath dagSP = new DAGShortestPath(singleNodeGraph, metrics);
        DAGShortestPath.CriticalPathResult result = dagSP.findCriticalPath(order);

        assertEquals(5.0, result.length, 0.001); // Only node duration
        assertEquals(1, result.path.size());
        assertEquals(0, (int)result.path.get(0));
    }

    @Test
    public void testShortestPathsDifferentSource() {
        TopologicalSort topo = new TopologicalSort(simpleDAG, metrics);
        List<Integer> order = topo.topologicalOrder();

        DAGShortestPath dagSP = new DAGShortestPath(simpleDAG, metrics);
        double[] distances = dagSP.shortestPaths(1, order);

        assertEquals(Double.POSITIVE_INFINITY, distances[0], 0.001);
        assertEquals(0.0, distances[1], 0.001);
        assertEquals(Double.POSITIVE_INFINITY, distances[2], 0.001);
        assertEquals(1.0, distances[3], 0.001);
    }

    @Test
    public void testCriticalPathWithNodeWeights() {
        // Test with node weight model
        List<Graph.Node> nodes = Arrays.asList(
                new Graph.Node(0, "A", 5),
                new Graph.Node(1, "B", 3),
                new Graph.Node(2, "C", 7),
                new Graph.Node(3, "D", 2)
        );
        Graph nodeWeightedDAG = new Graph(4, nodes, "node");
        nodeWeightedDAG.addEdge(0, 1, 0); // weights ignored in node model
        nodeWeightedDAG.addEdge(0, 2, 0);
        nodeWeightedDAG.addEdge(1, 3, 0);
        nodeWeightedDAG.addEdge(2, 3, 0);

        TopologicalSort topo = new TopologicalSort(nodeWeightedDAG, metrics);
        List<Integer> order = topo.topologicalOrder();

        DAGShortestPath dagSP = new DAGShortestPath(nodeWeightedDAG, metrics);
        DAGShortestPath.CriticalPathResult result = dagSP.findCriticalPath(order);

        // Critical path should be 0->2->3 with total: 5 + 7 + 2 = 14
        assertEquals(14.0, result.length, 0.001);
        assertEquals(3, result.path.size());
        assertEquals(0, (int)result.path.get(0));
        assertEquals(2, (int)result.path.get(1));
        assertEquals(3, (int)result.path.get(2));
    }

    @Test
    public void testShortestPathsUnreachableNodes() {
        // Graph with disconnected components
        List<Graph.Node> nodes = Arrays.asList(
                new Graph.Node(0, "A", 1),
                new Graph.Node(1, "B", 1),
                new Graph.Node(2, "C", 1),
                new Graph.Node(3, "D", 1)
        );
        Graph disconnectedDAG = new Graph(4, nodes, "edge");
        disconnectedDAG.addEdge(0, 1, 1); // Component 1: 0-1
        disconnectedDAG.addEdge(2, 3, 1); // Component 2: 2-3

        TopologicalSort topo = new TopologicalSort(disconnectedDAG, metrics);
        List<Integer> order = topo.topologicalOrder();

        DAGShortestPath dagSP = new DAGShortestPath(disconnectedDAG, metrics);
        double[] distances = dagSP.shortestPaths(0, order);

        assertEquals(0.0, distances[0], 0.001);
        assertEquals(1.0, distances[1], 0.001);
        assertEquals(Double.POSITIVE_INFINITY, distances[2], 0.001);
        assertEquals(Double.POSITIVE_INFINITY, distances[3], 0.001);
    }

    @Test
    public void testDAGShortestPathMetrics() {
        TopologicalSort topo = new TopologicalSort(simpleDAG, metrics);
        List<Integer> order = topo.topologicalOrder();

        DAGShortestPath dagSP = new DAGShortestPath(simpleDAG, metrics);
        dagSP.shortestPaths(0, order);

        // Should have relaxation operations recorded
        assertTrue(metrics.getOperationCount("Relaxations") > 0);
        assertTrue(metrics.getOperationCount("Edge relaxations") > 0);
    }

    @Test
    public void testCriticalPathMetrics() {
        TopologicalSort topo = new TopologicalSort(weightedDAG, metrics);
        List<Integer> order = topo.topologicalOrder();

        DAGShortestPath dagSP = new DAGShortestPath(weightedDAG, metrics);
        dagSP.findCriticalPath(order);

        // Should have longest path operations recorded
        assertTrue(metrics.getOperationCount("Longest path relaxations") > 0);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInvalidSourceNode() {
        TopologicalSort topo = new TopologicalSort(simpleDAG, metrics);
        List<Integer> order = topo.topologicalOrder();

        DAGShortestPath dagSP = new DAGShortestPath(simpleDAG, metrics);
        // This should work without exception due to infinity propagation
        double[] distances = dagSP.shortestPaths(10, order); // Invalid source
        // All distances should be infinity
        for (double dist : distances) {
            assertEquals(Double.POSITIVE_INFINITY, dist, 0.001);
        }
    }
}