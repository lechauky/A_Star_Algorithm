package com.mycompany.astaralgorithm;

import java.util.*;
import java.io.*;

import org.graphstream.graph.*;
import org.graphstream.graph.implementations.*;

class Edge {
    int to, weight;

    Edge(int to, int weight) {
        this.to = to;
        this.weight = weight;
    }
}

class MyNode implements Comparable<MyNode> {
    int vertex, cost;

    MyNode(int vertex, int cost) {
        this.vertex = vertex;
        this.cost = cost;
    }

    @Override
    public int compareTo(MyNode other) {
        return Integer.compare(this.cost, other.cost);
    }
}

public class AStarAlgorithm {
    private static List<List<Edge>> adjList;
    private static List<Integer> heuristic;
    private static List<Integer> finalPath;

    public static int AStar(int start, int end) {
        PriorityQueue<MyNode> openSet = new PriorityQueue<>();
        int[] g = new int[adjList.size()];
        int[] f = new int[adjList.size()];
        int[] prev = new int[adjList.size()];
        Arrays.fill(g, Integer.MAX_VALUE);
        Arrays.fill(f, Integer.MAX_VALUE);
        Arrays.fill(prev, -1);
        Map<Integer, Boolean> inOpenSet = new HashMap<>();
        Map<Integer, Boolean> inCloseSet = new HashMap<>();

        g[start] = 0;
        f[start] = heuristic.get(start);
        openSet.add(new MyNode(start, f[start]));
        inOpenSet.put(start, true);

        while (!openSet.isEmpty()) {
            int current = openSet.poll().vertex;
            inOpenSet.put(current, false);

            if (current == end) {
                int totalCost = g[end];
                List<Integer> path = new ArrayList<>();
                while (current != -1) {
                    path.add(current);
                    current = prev[current];
                }
                Collections.reverse(path);
                finalPath = path;

                System.out.print("Shortest path: ");
                for (int v : path) {
                    System.out.print(v + " ");
                }
                System.out.println("\nPath cost: " + totalCost);
                return totalCost;
            }

            inCloseSet.put(current, true);

            for (Edge edge : adjList.get(current)) {
                int neighbor = edge.to;
                int tentative_g = g[current] + edge.weight;

                if (!inCloseSet.getOrDefault(neighbor, false) && tentative_g < g[neighbor]) {
                    prev[neighbor] = current;
                    g[neighbor] = tentative_g;
                    f[neighbor] = g[neighbor] + heuristic.get(neighbor);

                    if (!inOpenSet.getOrDefault(neighbor, false)) {
                        openSet.add(new MyNode(neighbor, f[neighbor]));
                        inOpenSet.put(neighbor, true);
                    }
                }
            }
        }

        return -1; 
    }

    public static void main(String[] args) {
        try {
            File inputFile = new File("src/main/java/com/mycompany/astaralgorithm/input.txt");
            Scanner scanner = new Scanner(inputFile);
            int n = scanner.nextInt();
            int m = scanner.nextInt();
            int s = scanner.nextInt();
            int t = scanner.nextInt();

            adjList = new ArrayList<>();
            heuristic = new ArrayList<>();
            for (int i = 0; i <= n; i++) {
                adjList.add(new ArrayList<>());
                heuristic.add(0);
            }

            List<int[]> edges = new ArrayList<>();

            for (int i = 0; i < m; i++) {
                int u = scanner.nextInt();
                int v = scanner.nextInt();
                int w = scanner.nextInt();
                adjList.get(u).add(new Edge(v, w));
                edges.add(new int[]{u, v});
            }

            for (int i = 1; i <= n; i++) {
                heuristic.set(i, scanner.nextInt());
            }

            scanner.close();

            AStar(s, t);
            drawGraph(n, edges);
        } catch (FileNotFoundException e) {
            System.err.println("Error opening input file");
            e.printStackTrace();
        }
    }

    public static void drawGraph(int n, List<int[]> edges) {
        Graph graph = new SingleGraph("A* Algorithm");

        // Add nodes
        for (int i = 1; i <= n; i++) {
            org.graphstream.graph.Node node = graph.addNode(Integer.toString(i));
            node.setAttribute("ui.label", i + "");
        }

        // Add edges
        for (int[] e : edges) {
            String from = Integer.toString(e[0]);
            String to = Integer.toString(e[1]);
            graph.addEdge(from + "-" + to, from, to, true);
        }

        // Set style
        graph.setAttribute("ui.stylesheet",
            "node { fill-color: #4CAF50; size: 25px; text-size: 18px; text-alignment: under; text-color: black; }" +
            "edge { fill-color: #888; arrow-shape: arrow; arrow-size: 10px, 6px; size: 2px; }"
        );
        graph.setAttribute("ui.quality");
        graph.setAttribute("ui.antialias");

        // Highlight the final path
        if (finalPath != null) {
            for (int i = 0; i < finalPath.size() - 1; i++) {
                String from = Integer.toString(finalPath.get(i));
                String to = Integer.toString(finalPath.get(i + 1));

                org.graphstream.graph.Edge graphEdge = graph.getEdge(from + "-" + to);

                if (graph.getEdge(from + "-" + to) != null) {
                    graph.getEdge(from + "-" + to).setAttribute("ui.style", "fill-color: red; size: 4px;");
                } else if (graph.getEdge(to + "-" + from) != null) {
                    graph.getEdge(to + "-" + from).setAttribute("ui.style", "fill-color: red; size: 4px;");
                }
            }
        }
        
        System.setProperty("org.graphstream.ui", "swing");
        
        graph.display();
    }
}
