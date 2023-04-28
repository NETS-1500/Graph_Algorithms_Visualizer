import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.LinkedList;

class GraphCanvas extends JPanel implements MouseListener {
    enum Mode {ADD_NODE, REMOVE_NODE, ADD_EDGE, REMOVE_EDGE, BFS, DFS, SHORTEST_PATH,
        TOPOLOGICAL_SORT}

    private Mode mode;
    private ArrayList<Node> nodes;
    private ArrayList<Edge> edges;

    private Node startNode = null;
    private Node endNode = null;

    private Node startRemoveEdgeNode = null;
    private Node endRemoveEdgeNode = null;

    private Node startNodeBFS = null;

    private Node startNodeShortPath = null;
    private Node endNodeShortPath = null;

    public GraphCanvas() {
        setPreferredSize(new Dimension(400, 400));
        addMouseListener(this);
        nodes = new ArrayList<>();
        edges = new ArrayList<>();
    }

    public void setMode(Mode mode) {
        this.mode = mode;
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        for (Node node : nodes) {
            node.draw(g);
        }
        for (Edge edge : edges) {
            edge.draw(g);
        }
    }

    public ArrayList<Node> getNodes() {
        return this.nodes;
    }

    public ArrayList<Edge> getEdges() {
        return this.edges;
    }

    public void mouseClicked(MouseEvent e) {
        // Add and remove nodes/edges
        if (mode == Mode.ADD_NODE) {
            boolean validInput = false;
            while (!validInput) {
                String nodeName = JOptionPane.showInputDialog(this, "Enter a letter from a to z:");
                if (nodeName != null && nodeName.length() == 1 && nodeName.charAt(0) >= 'a' && nodeName.charAt(0) <= 'z') {
                    boolean nodeExists = false;
                    for (Node node : nodes) {
                        if (node.getName().equals(nodeName)) {
                            nodeExists = true;
                            break;
                        }
                    }
                    if (!nodeExists) {
                        Node addedNode = new Node(nodeName, e.getX(), e.getY());
                        nodes.add(addedNode);
                        System.out.println("Node Added: " + addedNode + " | " + addedNode.getName());
                        repaint();
                        validInput = true;
                        mode = null;
                        GraphGUI.resetStatusBar();
                    } else {
                        JOptionPane.showMessageDialog(this, "A node with that letter already exists.");
                        validInput = true;
                    }
                }
                else if (nodeName != null) {
                    int result = JOptionPane.showConfirmDialog(this, "Invalid input. Try again?", "Invalid Input", JOptionPane.YES_NO_OPTION);
                    if (result == JOptionPane.NO_OPTION) {
                        validInput = true;
                    }
                } else {
                    validInput = true;
                }
            }
        }
        else if (mode == Mode.REMOVE_NODE) {
            int x = e.getX();
            int y = e.getY();
            for (int i = nodes.size() - 1; i >= 0; i--) {
                Node node = nodes.get(i);
                if (node.contains(x, y)) {
                    Node removed = nodes.remove(i);
                    System.out.println("Node Removed: " + removed + " | " + removed.getName());
                    edges.removeIf
                            (edge -> edge.getPrecedingNode().equals(removed) || edge.getSucceedingNode().equals(removed));
                    repaint();
                    mode = null;
                    GraphGUI.resetStatusBar();
                    break;
                }
            }
        }
        else if (mode == Mode.ADD_EDGE) {
            if (startNode == null) {
                for (Node node : nodes) {
                    if (node.contains(e.getX(), e.getY())) {
                        startNode = node;
                        System.out.println("Add Edge Start Node: " + startNode + " | " + startNode.getName());
                        break;
                    }
                }
            } else {
                // TODO: No self-loops not working
                for (Node node : nodes) {
                    if (node.contains(e.getX(), e.getY())) {
                        endNode = node;
                        System.out.println("Add Edge End Node: " + endNode + " | " + endNode.getName());
                        break;
                    }
                }

                // Display the directed/undirected popup and input the edge weight
                boolean validInput = false;
                while (!validInput) {
                    Object[] options = {"Directed", "Undirected"};
                    int n = JOptionPane.showOptionDialog(this,
                            "Is this a directed or undirected edge?",
                            "Edge Type",
                            JOptionPane.YES_NO_OPTION,
                            JOptionPane.QUESTION_MESSAGE,
                            null,
                            options,
                            options[0]);
                    if (n == JOptionPane.YES_OPTION) {
                        System.out.println("Directed");
                        int input = -1;
                        while (input < 0) {
                            String inputStr = JOptionPane.showInputDialog("Please enter a non-negative edge weight:");
                            try {
                                input = Integer.parseInt(inputStr);
                                if (input < 0) {
                                    JOptionPane.showMessageDialog(this,
                                            "Please enter a non-negative edge weight:",
                                            "Error", JOptionPane.ERROR_MESSAGE);
                                }
                            } catch (NumberFormatException ex) {
                                JOptionPane.showMessageDialog(this,
                                        "Please enter a non-negative edge weight.",
                                        "Error", JOptionPane.ERROR_MESSAGE);
                            }
                        }
                        edges.add(new Edge(startNode, endNode, input, true));

                        repaint();
                        validInput = true;
                        startNode = null;
                        endNode = null;
                        mode = null;
                        GraphGUI.resetStatusBar();

                    } else if (n == JOptionPane.NO_OPTION) {
                        System.out.println("Undirected");
                        int input = -1;
                        while (input < 0) {
                            String inputStr = JOptionPane.showInputDialog("Please enter a non-negative edge weight:");
                            try {
                                input = Integer.parseInt(inputStr);
                                if (input < 0) {
                                    JOptionPane.showMessageDialog(this,
                                            "Please enter a non-negative edge weight:",
                                            "Error", JOptionPane.ERROR_MESSAGE);
                                }
                            } catch (NumberFormatException ex) {
                                JOptionPane.showMessageDialog(this,
                                        "Please enter a non-negative edge weight.",
                                        "Error", JOptionPane.ERROR_MESSAGE);
                            }
                        }
                        edges.add(new Edge(startNode, endNode, input, false));

                        repaint();
                        validInput = true;
                        startNode = null;
                        endNode = null;
                        mode = null;
                        GraphGUI.resetStatusBar();
                    }
                }
            }
        }
        else if (mode == Mode.REMOVE_EDGE) {
            if (startRemoveEdgeNode == null) {
                for (Node node : nodes) {
                    if (node.contains(e.getX(), e.getY())) {
                        startRemoveEdgeNode = node;
                        System.out.println("Remove Edge Start: " + startRemoveEdgeNode + " | " + startRemoveEdgeNode.getName());
                        break;
                    }
                }
            } else {
                // TODO: No self-loops not working
                for (Node node : nodes) {
                    if (node.contains(e.getX(), e.getY())) {
                        endRemoveEdgeNode = node;
                        System.out.println("Remove Edge End: " + endRemoveEdgeNode + " | " + endRemoveEdgeNode.getName());
                        break;
                    }
                }

                boolean edgeRemoved = edges.removeIf
                        (edge -> (edge.getPrecedingNode().equals(startRemoveEdgeNode) &&
                                edge.getSucceedingNode().equals(endRemoveEdgeNode)) ||
                                (edge.getPrecedingNode().equals(endRemoveEdgeNode) &&
                                        edge.getSucceedingNode().equals(startRemoveEdgeNode) &&
                                        !edge.getIsDirected()));
                if (edgeRemoved) {
                    System.out.println("Edge Removed");
                }
                else {
                    System.out.println("No edge removed");
                }

                repaint();
                startRemoveEdgeNode = null;
                endRemoveEdgeNode = null;
                mode = null;
                GraphGUI.resetStatusBar();
            }
        }

        // Graph algorithms
        else if (mode == Mode.BFS) {
            /*
            if (nodes.isEmpty() || edges.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                        "Please add nodes and/or edges to run BFS.",
                        "Error", JOptionPane.ERROR_MESSAGE);
                mode = null;
            }

            System.out.println("test");

             */

            if (startNodeBFS == null) {
                for (Node node : nodes) {
                    if (node.contains(e.getX(), e.getY())) {
                        startNodeBFS = node;
                        System.out.println("BFS Start Node: " + startNodeBFS + " | " + startNodeBFS.getName());
                        break;
                    }
                }
            }

            GraphAlgorithms.createAdjacencyList(nodes, edges);
            System.out.println(GraphAlgorithms.getAdjacencyList());

            GraphAlgorithms.BFS(startNodeBFS);
        }


        else if (mode == Mode.SHORTEST_PATH) {

            if (startNodeShortPath == null) {
                for (Node node : nodes) {
                    if (node.contains(e.getX(), e.getY())) {
                        startNodeShortPath = node;
                        System.out.println("Shortest Path Start Node: " + startNodeShortPath + " | " + startNodeShortPath.getName());
                        break;
                    }
                }
            } else {
                for (Node node : nodes) {
                    if (node.contains(e.getX(), e.getY())) {
                        endNodeShortPath = node;
                        System.out.println("Shortest Path End Node: " + endNodeShortPath + " | " + endNodeShortPath.getName());
                        break;
                    }
                }

                GraphAlgorithms.createAdjacencyList(nodes, edges);
                LinkedList<Node> path = GraphAlgorithms.shortestPathTo(startNodeShortPath, endNodeShortPath);
                if (path.isEmpty()) {
                    System.out.println("No path from " + startNodeShortPath.getName() + " to " + endNodeShortPath.getName());
                } else {
                    System.out.println("Shortest path from " + startNodeShortPath.getName() + " to " + endNodeShortPath.getName() + ":");
                    for (Node node : path) {
                        System.out.println(node.getName());
                    }

                }

                GraphGUI.resetStatusBar();
                startNodeShortPath = null;
                endNodeShortPath = null;


            }

        }
    }

    public void mouseEntered(MouseEvent e) {}

    public void mouseExited(MouseEvent e) {}

    public void mousePressed(MouseEvent e) {}

    public void mouseReleased(MouseEvent e) {}
}