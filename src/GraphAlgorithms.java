import java.util.*;

public class GraphAlgorithms {

    private static ArrayList<Node> bfsOrdering;
    private static HashMap<Node, LinkedList<Edge>> adjacencyList;

    static void createAdjacencyList(ArrayList<Node> nodes, ArrayList<Edge> edges) {
        HashMap<Node, LinkedList<Edge>> adjacencyList = new HashMap<>();
        for (Node node : nodes) {
            adjacencyList.put(node, new LinkedList<>());
        }
        for (Edge edge : edges) {
            adjacencyList.get(edge.getPrecedingNode()).add(edge);
            if (!edge.getIsDirected()) {
                adjacencyList.get(edge.getSucceedingNode()).add(new Edge(edge.getSucceedingNode()
                        , edge.getPrecedingNode(),
                        edge.getWeight(), false));
            }
        }

        GraphAlgorithms.adjacencyList = adjacencyList;
    }

    static void BFS(Node startingNode) {
        Queue<Node> queue = new ArrayDeque<>();
        HashSet<Node> visited = new HashSet<>();

        queue.add(startingNode);
        visited.add(startingNode);

        while (!queue.isEmpty()) {
            Node current = queue.poll();
            bfsOrdering.add(current);

            for (Edge edge : adjacencyList.get(current)) {
                Node neighbor = edge.getSucceedingNode();
                if (!visited.contains(neighbor)) {
                    queue.add(neighbor);
                    visited.add(neighbor);
                }
            }
        }

        System.out.println("BFS Order: ");
        for (Node node : bfsOrdering) {
            System.out.print(node.getName() + " ");
        }
        System.out.println();
    }

    public static ArrayList<Node> getBfsOrdering() {
        return bfsOrdering;
    }

    static HashMap<Node, Node> Dijkstra(Node source) {
        HashMap<Node, Integer> distances = new HashMap<>();
        HashMap<Node, Node> parents = new HashMap<>();
        Queue<Node> minQ = new PriorityQueue<>(new Comparator<Node>() {
            @Override
            public int compare(Node o1, Node o2) {
                int dist1 = distances.get(o1);
                int dist2 = distances.get(o2);

                if (dist1 < dist2) {
                    return -1;
                } else if (dist1 > dist2) {
                    return 1;
                } else {
                    return 0;
                }
            }
        });

        Set<Node> nodes = adjacencyList.keySet();
        for (Node node : nodes) {
            distances.put(node, Integer.MAX_VALUE);
            parents.put(node, null);
            minQ.add(node);
        }

        minQ.remove(source);
        distances.replace(source, 0);
        minQ.add(source);

        while (!minQ.isEmpty()) {
            Node u = minQ.poll();

            LinkedList<Edge> neighbors = adjacencyList.get(u);
            for (Edge edge : neighbors) {
                Node v = edge.getSucceedingNode();
                int distU = distances.get(u);
                int distV = distances.get(v);

                if (minQ.contains(v) && distV > distU + edge.getWeight()) {
                    minQ.remove(v);
                    distances.replace(v, distU + edge.getWeight());
                    minQ.add(v);
                    parents.put(v, u);
                }
            }
        }
        return parents;
    }

    public static LinkedList<Node> shortestPathTo(Node source, Node target) {
        LinkedList<Node> path = new LinkedList<>();
        if (source.equals(target)) {
            path.add(source);
            return path;
        }

        HashMap<Node, Node> parents = Dijkstra(source);

        Node curr = target;
        while (curr != null) {
            path.add(0, curr);

            if (curr.equals(source)) {
                return path;
            } else {
                curr = parents.get(curr);
            }
        }

        path.clear();
        return path;
    }

    public static HashMap<Node, LinkedList<Edge>> getAdjacencyList() {
        return adjacencyList;
    }
}
