import java.util.*;

public class GraphAlgorithms {

    private static ArrayList<Node> bfsOrdering = new ArrayList<>();
    private static HashMap<Node, LinkedList<Edge>> adjacencyList;
    private static int pathWeight;
    private static HashMap<Node, Integer> distances;
    private static ArrayList<Node> dfsOrdering = new ArrayList<>();
    private static ArrayList<Node> topologicalSort = new ArrayList<>();
    private static HashMap<Node,int[]> startFinishTimes = new HashMap<>();
    private static boolean isDAG = true;

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

    static void resetAlgorithmVariables() {
        bfsOrdering = new ArrayList<>();
        dfsOrdering = new ArrayList<>();
        topologicalSort = new ArrayList<>();
        startFinishTimes = new HashMap<>();
        isDAG = true;
    }

    static void BFS(Node startingNode) {
        Queue<Node> queue = new LinkedList<>();
        Set<Node> visited = new HashSet<>();

        queue.add(startingNode);
        visited.add(startingNode);

        while (!queue.isEmpty()) {
            Node current = queue.poll();
            bfsOrdering.add(current);

            LinkedList<Edge> edges = adjacencyList.get(current);
            if (edges != null) {
                for (Edge edge : edges) {
                    Node neighbor = edge.getSucceedingNode();
                    if (!visited.contains(neighbor)) {
                        queue.add(neighbor);
                        visited.add(neighbor);
                    }
                }
            }

            // Check for unvisited nodes
            if (queue.isEmpty()) {
                for (Node node : adjacencyList.keySet()) {
                    if (!visited.contains(node)) {
                        queue.add(node);
                        visited.add(node);
                        break;
                    }
                }
            }
        }
    }

    public static ArrayList<Node> getBFSOrdering() {
        return bfsOrdering;
    }

    static void DFS(Node startingNode) {
        //initializing stack to implement DFS
        Stack<Node> stack = new Stack<>();

        //keeping track of nodes visited to not have repeats in the stack
        HashSet<Node> visited = new HashSet<>();

        //a clock to keep track of the current time
        int time = 1;

        //clearing the dfs arraylist in case user has called dfs before
        dfsOrdering.clear();

        stack.add(startingNode);
        visited.add(startingNode);

        while (!stack.isEmpty()) {
            Node current = stack.pop();
            dfsOrdering.add(current);

            //when we see the node for the first time, allocate start time
            int[] temp = new int[2];
            temp[0] = time++;
            startFinishTimes.put(current,temp);

            //boolean to check whether all neighbors have been visited
            boolean visitedAllNeighbors = true;
            ArrayList<Node> neighborsToAdd = new ArrayList<>();
            for (Edge edge : adjacencyList.get(current)) {
                Node neighbor = edge.getSucceedingNode();
                if (!visited.contains(neighbor)) {
                    visitedAllNeighbors = false;
                    //instead of adding to stack immediately, add to temporary holder
                    neighborsToAdd.add(neighbor);
                    visited.add(neighbor);
                }
                // if we were able to reach a neighbor that we already allocated a start time to or if there
                // exists an undirected edge, then it is not
                //a DAG. Do check if this logic holds.
                if(!edge.getIsDirected()) {
                    isDAG = false;
                }
            }
            //sort in alphabetical order, then add backwards due to nature of stack
            Collections.sort(neighborsToAdd);
            for(int i = neighborsToAdd.size() - 1; i >= 0; i--) {
                stack.add(neighborsToAdd.get(i));
            }
            //don't forget to clear before next iteration
            neighborsToAdd.clear();

            //if there are no neigbors that we haven't already visited for this node,
            // allocate finish time
            if(visitedAllNeighbors) {
                int[] end = startFinishTimes.get(current);
                end[1] = time++;
                startFinishTimes.put(current,end);
                //now we need to go back to the nodes that we have already assigned start times to
                //and see if we need to give them a finish time
                for (int i = dfsOrdering.size() - 1; i >= 0; i--) {
                    int[] times = startFinishTimes.get(dfsOrdering.get(i));
                    //if there has not been an end time assigned, the second value should be 0
                    //this is insurance that we do not overwrite any previous assignments
                    boolean neighborsNotInStack = true;
                    boolean haveFinishTimes = true;
                    if(times[1] == 0) {
                        for (Edge edge : adjacencyList.get(dfsOrdering.get(i))) {
                            Node neighbor = edge.getSucceedingNode();
                            //if all neighbors of this node have been removed from the stack, this means
                            //we have exhausted its neighbors already, we need to assign end time
                            if (stack.contains(neighbor)) {
                                neighborsNotInStack = false;
                            }
                            if(startFinishTimes.containsKey(neighbor)) {
                                if(startFinishTimes.get(neighbor)[1] == 0) {
                                    haveFinishTimes = false;
                                }
                            }
                        }
                        if(neighborsNotInStack && haveFinishTimes) {
                            int[] prevEnd = startFinishTimes.get(dfsOrdering.get(i));
                            prevEnd[1] = time++;
                        }
                    }
                }
            }
            if (stack.isEmpty()) {
                for (Node node : adjacencyList.keySet()) {
                    if (!visited.contains(node)) {
                        stack.add(node);
                        visited.add(node);
                        break;
                    }
                }
            }
        }
    }

    public static ArrayList<Node> getDFSOrdering() {
        return dfsOrdering;
    }

    public static HashMap<Node, int[]> getStartFinishTimes() {
        return startFinishTimes;
    }

    static HashMap<Node, Node> Dijkstra(Node source) {
        distances = new HashMap<>();
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

    static LinkedList<Node> shortestPath(Node source, Node target) {
        pathWeight = 0;

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
                pathWeight = distances.get(target);
                return path;
            } else {
                curr = parents.get(curr);
            }
        }

        path.clear();
        return path;
    }

    static int getPathWeight() {
        return pathWeight;
    }

    static void checkIfDAG(Node source, ArrayList<Node> visited, Stack<Node> recursion) {
        if(recursion.contains(source)) {
            isDAG = false;
            return;
        }
        visited.add(source);
        recursion.add(source);
        for (Edge edge : adjacencyList.get(source)) {
            Node neighbor = edge.getSucceedingNode();
            checkIfDAG(neighbor,visited,recursion);
        }
        recursion.pop();
    }

    public static boolean getIsDAG() {
        return isDAG;
    }

    static void topologicalSort(Node sourceNode) {
        checkIfDAG(sourceNode, new ArrayList<>(), new Stack<>());
        if(isDAG) {
            DFS(sourceNode);
            int max = 0;
            ArrayList<Integer> prevMaxes = new ArrayList<>();
            while (topologicalSort.size() != startFinishTimes.size()) {
                for (Map.Entry<Node, int[]> entry : startFinishTimes.entrySet()) {
                    if (entry.getValue()[1] > max && !prevMaxes.contains(entry.getValue()[1])) {
                        max = entry.getValue()[1];
                    }
                }
                prevMaxes.add(max);
                for (Map.Entry<Node, int[]> entry : startFinishTimes.entrySet()) {
                    if (entry.getValue()[1] == max && !topologicalSort.contains(entry.getKey())) {
                        topologicalSort.add(entry.getKey());
                    }
                }
                max = 0;
            }
        }
    }

    public static ArrayList<Node> getTopologicalSort() {
        return topologicalSort;
    }
}