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

    public static HashMap<Node, LinkedList<Edge>> getAdjacencyList() {
        return adjacencyList;
    }
}
