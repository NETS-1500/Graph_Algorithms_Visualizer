import java.util.*;

public class GraphAlgorithms {

    private static ArrayList<Node> bfsOrdering;
    private static ArrayList<Node> dfsOrdering;
    private static HashMap<Node,int[]> startFinishTimes;
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
        bfsOrdering.clear();

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
        return GraphAlgorithms.bfsOrdering;
    }

    static HashMap<Node, Node> Dijkstra(Node source) {
        HashMap<Node, Integer> distances = new HashMap<>();
        HashMap<Node, Node> parents = new HashMap<>();
        Queue<Node> minQ = new PriorityQueue<>((o1, o2) -> {
            int dist1 = distances.get(o1);
            int dist2 = distances.get(o2);

            if (dist1 < dist2) {
                return -1;
            } else if (dist1 > dist2) {
                return 1;
            } else {
                return 0;
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

    public static void DFS(Node startingNode) {
        //initializing stack to implement DFS
        Stack<Node> stack = new Stack<>();

        //keeping track of nodes visited to not have repeats in the stack
        HashSet<Node> visited = new HashSet<>();

        //a clock to keep track of the current time
        int time = 0;

        //clearing the dfs arraylist in case user has called dfs before
        dfsOrdering.clear();

        stack.add(startingNode);
        visited.add(startingNode);

        while (!stack.isEmpty()) {
            Node current = stack.lastElement();
            dfsOrdering.add(current);

            //when we see the node for the first time, allocate start time
            int[] temp = new int[2];
            temp[0] = time++;
            startFinishTimes.put(current,temp);

            //boolean to check whether all neighbors have been visited
            boolean visitedAllNeighbors = true;
            for (Edge edge : adjacencyList.get(current)) {
                Node neighbor = edge.getSucceedingNode();
                if (!visited.contains(neighbor)) {
                    visitedAllNeighbors = false;
                    stack.add(neighbor);
                    visited.add(neighbor);
                }
            }
            //if there are no neigbors that we haven't already visited for this node,
            // allocate finish time
            //assumption: the start time has already been given
            if(visitedAllNeighbors == true) {
                int[] end = startFinishTimes.get(current);
                end[1] = time++;
                startFinishTimes.put(current,end);
                //now we need to go back to the nodes that we have already assigned start times to
                //and see if we need to give them a finish time
                for (int i = dfsOrdering.size() - 2; i >= 0; i--) {
                    int[] times = startFinishTimes.get(dfsOrdering.get(i));
                    //if there has not been an end time assigned, the second value should be 0
                    //this is insurance that we do not overwrite any previous assignments
                    if(times[1] == 0) {
                        for (Edge edge : adjacencyList.get(dfsOrdering.get(i))) {
                            Node neighbor = edge.getSucceedingNode();
                            //if all neighbors of this node have been removed from the stack, this means
                            //we have exhausted its neighbors already, we need to assign end time
                            if (!stack.contains(neighbor)) {
                                int[] prevEnd = startFinishTimes.get(dfsOrdering.get(i));
                                prevEnd[1] = time++;
                            }
                        }
                    }
                }
            }
        }

        System.out.println("DFS Order: ");
        for (Node node : dfsOrdering) {
            System.out.print(node.getName() + " , Start/Finish: " + startFinishTimes.get(node)[0] + "/"
                    + startFinishTimes.get(node)[1]);
        }
        System.out.println();
    }
}
