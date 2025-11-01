package graph;

import graph.scc.TarjanSCC;
import graph.metrics.Metrics;
import org.junit.Test;
import org.junit.Before;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;

public class SCCTest {

    private Metrics metrics;

    @Before
    public void setUp() {
        metrics = new Metrics();
    }

    @Test
    public void testSCCSimpleDAG() {
        List<Graph.Node> nodes = Arrays.asList(
                new Graph.Node(0, "A", 1),
                new Graph.Node(1, "B", 2),
                new Graph.Node(2, "C", 3)
        );
        Graph graph = new Graph(3, nodes, "edge");
        graph.addEdge(0, 1, 1);
        graph.addEdge(1, 2, 1);

        TarjanSCC tarjan = new TarjanSCC(graph, metrics);
        List<List<Integer>> sccs = tarjan.findSCCs();

        assertEquals(3, sccs.size());
        for (List<Integer> scc : sccs) {
            assertEquals(1, scc.size());
        }
    }

    @Test
    public void testSCCWithCycle() {
        List<Graph.Node> nodes = Arrays.asList(
                new Graph.Node(0, "A", 1),
                new Graph.Node(1, "B", 2),
                new Graph.Node(2, "C", 3)
        );
        Graph graph = new Graph(3, nodes, "edge");
        graph.addEdge(0, 1, 1);
        graph.addEdge(1, 2, 1);
        graph.addEdge(2, 0, 1);

        TarjanSCC tarjan = new TarjanSCC(graph, metrics);
        List<List<Integer>> sccs = tarjan.findSCCs();

        assertEquals(1, sccs.size());
        assertEquals(3, sccs.get(0).size());
    }

    @Test
    public void testSCCMultipleComponents() {
        List<Graph.Node> nodes = Arrays.asList(
                new Graph.Node(0, "A", 1),
                new Graph.Node(1, "B", 2),
                new Graph.Node(2, "C", 3),
                new Graph.Node(3, "D", 4)
        );
        Graph graph = new Graph(4, nodes, "edge");
        graph.addEdge(0, 1, 1);
        graph.addEdge(1, 0, 1); // Cycle 0-1
        graph.addEdge(2, 3, 1);
        graph.addEdge(3, 2, 1); // Cycle 2-3

        TarjanSCC tarjan = new TarjanSCC(graph, metrics);
        List<List<Integer>> sccs = tarjan.findSCCs();

        assertEquals(2, sccs.size());
        assertEquals(2, sccs.get(0).size());
        assertEquals(2, sccs.get(1).size());
    }

    @Test
    public void testSCCMetrics() {
        List<Graph.Node> nodes = Arrays.asList(
                new Graph.Node(0, "A", 1),
                new Graph.Node(1, "B", 2)
        );
        Graph graph = new Graph(2, nodes, "edge");
        graph.addEdge(0, 1, 1);

        TarjanSCC tarjan = new TarjanSCC(graph, metrics);
        tarjan.findSCCs();

        assertTrue(metrics.getOperationCount("DFS visits") > 0);
        assertTrue(metrics.getOperationCount("Edge traversals") > 0);
    }

    @Test
    public void testCondensationGraph() {
        List<Graph.Node> nodes = Arrays.asList(
                new Graph.Node(0, "A", 1),
                new Graph.Node(1, "B", 2),
                new Graph.Node(2, "C", 3)
        );
        Graph graph = new Graph(3, nodes, "edge");
        graph.addEdge(0, 1, 1);
        graph.addEdge(1, 0, 1); // Cycle 0-1
        graph.addEdge(1, 2, 1);

        TarjanSCC tarjan = new TarjanSCC(graph, metrics);
        Graph condensation = tarjan.buildCondensationGraph();

        assertEquals(2, condensation.getN()); // Two components: {0,1} and {2}
        assertEquals(1, condensation.getEdges(0).size()); // Edge from component 0 to component 1
    }
}