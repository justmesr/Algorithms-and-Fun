import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Stream;

/*
test.txt:

1 2 2
2 3 2
2 4 1
3 5 2
4 6 5
5 7 2
5 8 3
7 8 1
6 9 2
8 9 1

Test2.txt:

1 2 2
2 3 2
2 4 1
3 5 2
4 6 5
5 7 2
5 8 3
7 8 1
6 9 2
8 9 1
1 9 11

test3.txt:

1 2 2
2 3 2
2 4 1
3 5 2
4 6 5
5 7 2
5 8 3
7 8 1
6 9 2
8 9 1
1 9 10
2 9 4
 */

class Edge implements Comparable<Edge> {

    final int from;
    final int to;
    final int weight;

    Edge(int from, int to, int weight) {
        this.from = from;
        this.to = to;
        this.weight = weight;
    }

    @Override
    public int compareTo(Edge edge) {
        return Integer.compare(weight, edge.weight);
    }

}

class Node {

    List<Edge> edges = new ArrayList<>();

}

class Main {

    public static void main(String[] args) {
        int from = 1;
        int to = 9;

        Map<Integer, Node> graph = loadGraph(new File("test.txt"));
        System.out.println("Expected: 3 Got: " + shortestPathsCount(graph, from, to));

        Map<Integer, Node> graph2 = loadGraph(new File("test2.txt"));
        System.out.println("Expected: 3 Got: " + shortestPathsCount(graph2, from, to));

        Map<Integer, Node> graph3 = loadGraph(new File("test3.txt"));
        System.out.println("Expected: 1 Got: " + shortestPathsCount(graph3, from, to));
    }

    private static Map<Integer, Node> loadGraph(File file) {
        Map<Integer, Node> graph = new HashMap<>();

        try (Stream<String> stream = Files.lines(Paths.get(file.toURI()))) {
            stream.forEach(line -> parseLine(line, graph));
        } catch (IOException e) {
            e.printStackTrace();
        }

        return graph;
    }

    private static void parseLine(String line, Map<Integer, Node> graph) {
        try (Scanner parser = new Scanner(line)) {
            int from = parser.nextInt();
            int to = parser.nextInt();
            int weight = parser.nextInt();

            addEdge(graph, from, to, weight);

        } catch (Exception e) {
            throw new RuntimeException("File in wrong format, " +
                    "please format each line such that it has 3 integers: from to weight. " +
                    "Instead of " + line, e);
        }
    }

    private static void addEdge(Map<Integer, Node> graph, int from, int to, int weight) {
        graph.putIfAbsent(from, new Node());
        graph.putIfAbsent(to, new Node());

        graph.get(from).edges.add(new Edge(from, to, weight));
        graph.get(to).edges.add(new Edge(to, from, weight));
    }

    private static int shortestPathsCount(Map<Integer, Node> graph, int from, int to) {
        PriorityQueue<Edge> paths = new PriorityQueue<>();
        Map<Integer, Integer> distances = new HashMap<>();
        Map<Integer, Integer> shortestPaths = new HashMap<>();
        Set<Integer> visited = new HashSet<>();

        shortestPaths.put(from, 1);
        distances.put(from, 0);
        visited.add(from);

        for (Edge edge : graph.get(from).edges) {
            paths.offer(new Edge(from, edge.to, edge.weight));
        }

        while (!paths.isEmpty()) {
            Edge current = paths.poll();

            if (distances.containsKey(to) && current.weight > distances.get(to)) {
                // From now on, only longer than shortest paths follow
                break;
            }

            shortestPaths.putIfAbsent(current.to, 0);
            distances.putIfAbsent(current.to, current.weight);

            if (distances.get(current.to) == current.weight) {
                // Found another shortest path
                shortestPaths.put(current.to, shortestPaths.get(current.to) + shortestPaths.get(current.from));
            }

            if (distances.get(current.to) != current.weight || visited.contains(current.to)){
                continue;
            }

            visited.add(current.to);
            List<Edge> neighbours = graph.get(current.to).edges;
            for (Edge edge : neighbours) {
                int newDistance = current.weight + edge.weight;
                paths.offer(new Edge(current.to, edge.to, newDistance));
            }
        }

        return shortestPaths.get(to);
    }

}
