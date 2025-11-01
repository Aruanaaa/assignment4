package graph.scc;

import graph.Graph;
import graph.metrics.Metrics;

import java.util.*;

public class TarjanSCC {
    private final Graph graph;
    private final Metrics metrics;


    private int index;
    private int[] indices;
    private int[] lowlinks;
    private boolean[] onStack;
    private Stack<Integer> stack;
    private List<List<Integer>> sccs;

    public TarjanSCC(Graph graph, Metrics metrics) {
        this.graph = graph;
        this.metrics = metrics;
    }

    // поиск сильно связанных компонент ---
    public List<List<Integer>> findSCCs() {
        int n = graph.getN();
        indices = new int[n];
        lowlinks = new int[n];
        onStack = new boolean[n];
        stack = new Stack<>();
        sccs = new ArrayList<>();
        index = 0;
        Arrays.fill(indices, -1); // -1 означает, что вершина ещё не посещена


        for (int i = 0; i < n; i++) {
            metrics.incrementOperation("DFS visits");
            if (indices[i] == -1) {
                strongConnect(i);
            }
        }
        return sccs;
    }

    //Рекурсивная функция для алгоритма
    private void strongConnect(int v) {
        indices[v] = index;
        lowlinks[v] = index;
        index++;
        stack.push(v);
        onStack[v] = true;

        for (Graph.Edge edge : graph.getEdges(v)) {
            metrics.incrementOperation("Edge traversals");
            int w = edge.v;
            if (indices[w] == -1) {

                strongConnect(w);
                lowlinks[v] = Math.min(lowlinks[v], lowlinks[w]);
            } else if (onStack[w]) {

                lowlinks[v] = Math.min(lowlinks[v], indices[w]);
            }
        }

        if (lowlinks[v] == indices[v]) {
            List<Integer> scc = new ArrayList<>();
            int w;
            do {
                w = stack.pop();
                onStack[w] = false;
                scc.add(w);
            } while (w != v);

            sccs.add(scc);
        }
    }

    public Graph buildCondensationGraph() {
        List<List<Integer>> components = findSCCs();
        int componentCount = components.size();


        int[] componentId = new int[graph.getN()];
        for (int i = 0; i < components.size(); i++) {
            for (int node : components.get(i)) {
                componentId[node] = i;
            }
        }


        List<Graph.Node> condNodes = new ArrayList<>();
        for (int i = 0; i < componentCount; i++) {
            double maxDuration = components.get(i).stream()
                    .mapToDouble(node -> graph.getNodes().get(node).duration)
                    .max().orElse(0);
            condNodes.add(new Graph.Node(i, "C" + i, maxDuration));
        }


        Graph condensation = new Graph(componentCount, condNodes, graph.getWeightModel());


        Set<String> edgesAdded = new HashSet<>();
        for (int u = 0; u < graph.getN(); u++) {
            for (Graph.Edge edge : graph.getEdges(u)) {
                int compU = componentId[u];
                int compV = componentId[edge.v];

                if (compU != compV) {
                    String edgeKey = compU + "-" + compV;
                    if (!edgesAdded.contains(edgeKey)) {
                        condensation.addEdge(compU, compV, edge.weight);
                        edgesAdded.add(edgeKey);
                    }
                }
            }
        }

        return condensation;
    }
}
