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

    private Node startNodeAddEdge = null;
    private Node endNodeAddEdge = null;

    private Node startNodeRemoveEdge = null;
    private Node endNodeRemoveEdge = null;

    private Node startNodeBFS = null;

    private Node startNodeDFS = null;

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
            if (startNodeAddEdge == null) {
                for (Node node : nodes) {
                    if (node.contains(e.getX(), e.getY())) {
                        startNodeAddEdge = node;
                        System.out.println("Add Edge Start Node: " + startNodeAddEdge + " | " + startNodeAddEdge.getName());
                        break;
                    }
                }
            } else {

                for (Node node : nodes) {
                    if (node.contains(e.getX(), e.getY())) {
                        endNodeAddEdge = node;
                        if (!endNodeAddEdge.equals(startNodeAddEdge)) {
                            System.out.println("Add Edge End Node: " + endNodeAddEdge + " | " + endNodeAddEdge.getName());
                        }
                        break;
                    }
                }

                //makes sure no self edges allowed
                if (startNodeAddEdge.equals(endNodeAddEdge)) {
                    JOptionPane.showMessageDialog(this,
                            "Self-edges not allowed! Please choose another end node.",
                            "Error", JOptionPane.ERROR_MESSAGE);
                    endNodeAddEdge = null;
                } else {
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
                            edges.add(new Edge(startNodeAddEdge, endNodeAddEdge, input, true));

                            repaint();
                            validInput = true;
                            startNodeAddEdge = null;
                            endNodeAddEdge = null;
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
                            edges.add(new Edge(startNodeAddEdge, endNodeAddEdge, input, false));

                            repaint();
                            validInput = true;
                            startNodeAddEdge = null;
                            endNodeAddEdge = null;
                            mode = null;
                            GraphGUI.resetStatusBar();
                        }
                    }
                }

            }
        }

        else if (mode == Mode.REMOVE_EDGE) {
            if (startNodeRemoveEdge == null) {
                for (Node node : nodes) {
                    if (node.contains(e.getX(), e.getY())) {
                        startNodeRemoveEdge = node;
                        System.out.println("Remove Edge Start: " + startNodeRemoveEdge + " | " + startNodeRemoveEdge.getName());
                        break;
                    }
                }
            } else {
                for (Node node : nodes) {
                    if (node.contains(e.getX(), e.getY())) {
                        endNodeRemoveEdge = node;
                        System.out.println("Remove Edge End: " + endNodeRemoveEdge + " | " + endNodeRemoveEdge.getName());
                        break;
                    }
                }

                boolean edgeRemoved = edges.removeIf
                        (edge -> (edge.getPrecedingNode().equals(startNodeRemoveEdge) &&
                                edge.getSucceedingNode().equals(endNodeRemoveEdge)) ||
                                (edge.getPrecedingNode().equals(endNodeRemoveEdge) &&
                                        edge.getSucceedingNode().equals(startNodeRemoveEdge) &&
                                        !edge.getIsDirected()));
                if (edgeRemoved) {
                    System.out.println("Edge Removed");
                }
                else {
                    System.out.println("No edge removed");
                }

                repaint();
                startNodeRemoveEdge = null;
                endNodeRemoveEdge = null;
                mode = null;
                GraphGUI.resetStatusBar();
            }
        }

        // Graph algorithms
        else if (mode == Mode.BFS) {
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
                    JOptionPane.showMessageDialog(this, "There is no path from " +
                                    startNodeShortPath.getName() + " to " + endNodeShortPath.getName() + "!", "Shortest Path",
                            JOptionPane.INFORMATION_MESSAGE);
                    System.out.println("No path from " + startNodeShortPath.getName() + " to " + endNodeShortPath.getName());
                } else {
                    String pathString = "";
                    for (int i = 0; i < path.size(); i++) {
                        pathString += path.get(i).getName();
                        if (i != path.size() - 1) {
                            pathString += " -> ";
                        }
                    }
                    JOptionPane.showMessageDialog(this, pathString, "Shortest Path from " +
                                    startNodeShortPath.getName() + " to " + endNodeShortPath.getName() + ":",
                            JOptionPane.INFORMATION_MESSAGE);
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
        else if(mode == Mode.DFS) {
            if (startNodeDFS == null) {
                for (Node node : nodes) {
                    if (node.contains(e.getX(), e.getY())) {
                        startNodeDFS = node;
                        System.out.println("BFS Start Node: " + startNodeDFS + " | " + startNodeDFS.getName());
                        break;
                    }
                }
            }

            GraphAlgorithms.createAdjacencyList(nodes, edges);
            System.out.println(GraphAlgorithms.getAdjacencyList());

            GraphAlgorithms.DFS(startNodeDFS);
        }
    }

    public void mouseEntered(MouseEvent e) {}

    public void mouseExited(MouseEvent e) {}

    public void mousePressed(MouseEvent e) {}

    public void mouseReleased(MouseEvent e) {}
}