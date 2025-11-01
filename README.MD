 Graph Algorithms Project – SCC and Critical Path Analysis

This project combines two main concepts from graph theory that we studied in class: **Strongly Connected Components (SCC)** and **Shortest Paths in Directed Acyclic Graphs (DAGs)**.
The goal was to apply these algorithms to real-like datasets that simulate scheduling or maintenance tasks in a “Smart City” or “Smart Campus” scenario.



## 1. Project Structure

```
graph/
 ├── Graph.java
 ├── metrics/
 │    └── Metrics.java
 ├── scc/
 │    └── TarjanSCC.java
 ├── topo/
 │    └── TopologicalSort.java
 ├── dagsp/
 │    └── DAGShortestPath.java
 ├── Main.java
 └── tests/
      ├── DAGShortestPathTest.java
      ├── SCCTest.java
      └── TopologicalSortTest.java
```

Each package handles a separate topic:

* scc/ — Tarjan’s algorithm for finding strongly connected components.
* topo/ — topological sorting using Kahn’s algorithm.
* dagsp/ — shortest path and critical path analysis in DAGs.
* metrics/ — counts different operations during algorithm execution (for analysis and performance tracking).

---

## 2. How It Works

Each JSON file represents a directed graph with nodes, durations, and dependencies.
The program first detects SCCs to identify cycles. If cycles exist, they are grouped and replaced by single nodes in the condensation graph, which is guaranteed to be acyclic.
Then, on that DAG, the program computes the critical path — the longest sequence of dependent tasks that determines the overall project duration.


## 3. Running the Program



Results were automatically saved to:


results/analysis_results.csv


---

## 4. Results Summary

Below are the summaries for all nine datasets exactly as they appeared during program execution.

| File        | Nodes | Edges | SCCs found | SCC sizes                                     | Condensation graph nodes | Critical path length | Critical path            |
| ----------- | ----- | ----- | ---------- | --------------------------------------------- | ------------------------ | -------------------- | ------------------------ |
| large_1.json | 22    | 33    | 22         | [1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1] | 22                       | 18.0                 | [10, 9, 8, 5, 4]         |
| large_2.json | 30    | 47    | 4          | [7,7,8,8]                                     | 4                        | 15.0                 | [3, 2, 1, 0]             |
| large_3.json| 40    | 546   | 1          | [40]                                          | 1                        | 0.0                  | [0]                      |
| medium_1.json | 12    | 18    | 12         | [1,1,1,1,1,1,1,1,1,1,1,1]                     | 12                       | 14.0                 | [10, 7, 5, 4, 2]         |
| medium_2.json | 15    | 24    | 9          | [1,1,7,1,1,1,1,1,1]                           | 9                        | 25.0                 | [8, 7, 6, 5, 4, 2, 1, 0] |
| medium_3.json| 18    | 107   | 1          | [18]                                          | 1                        | 0.0                  | [0]                      |
| small_1.json | 6     | 9     | 6          | [1,1,1,1,1,1]                                 | 6                        | 10.0                 | [5, 4, 3, 1, 0]          |
| small_2.json | 8     | 13    | 5          | [1,4,1,1,1]                                   | 5                        | 13.0                 | [4, 3, 2, 1, 0]          |
| small_3.json | 10    | 31    | 1          | [10]                                          | 1                        | 0.0                  | [0]                      |

---

## 5. Observations and Notes

While testing, I noticed several interesting patterns:

* Datasets like large_3.json and medium_3.json were fully cyclic, which means they formed a single SCC (all nodes mutually reachable).
  In such cases, the condensation graph had just one node, so the critical path was 0 with length 0.0.

* Graphs such as medium_2.json and large_2.json contained clusters of cycles. After SCC compression, the DAG became smaller (only 4 or 9 nodes), which made it easier to compute the critical path.

* In all purely acyclic graphs (small_1.json, medium_1.json, large_1.json), the topological order and the critical path were computed normally, confirming that the algorithms handled edge relaxations correctly.

---

## 6. Testing

I wrote several JUnit test classes:

* SCCTest.java – verifies that Tarjan’s algorithm correctly identifies SCCs, handles cycles, multiple components, and builds condensation graphs properly.
* *TopologicalSortTest.java – checks correctness of topological order on various DAGs (simple, linear, complex, and empty).
* DAGShortestPathTest.java – validates shortest paths, critical paths, and metrics collection. It includes cases for disconnected graphs and single-node graphs.

All tests passed successfully after several adjustments. I also verified that the Metrics class properly counts algorithmic operations (like relaxations and DFS traversals), which was helpful for performance comparison.

---

## 7. Conclusion

This project helped me understand the connection between cycle detection and task scheduling in directed graphs.
By first identifying SCCs and then performing a topological sort and critical path analysis on the reduced DAG, we can handle both cyclic and acyclic data efficiently.

Among the nine datasets:

* 3 were fully cyclic (single SCCs),
* 3 were acyclic (no cycles),
* 3 had mixed structures with multiple SCCs.

The algorithms performed correctly in all scenarios, and the final CSV summary matched the printed results.
