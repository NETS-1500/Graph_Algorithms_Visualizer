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
    private final ArrayList<Node> nodes;
    private final ArrayList<Edge> edges;

    private Node startNodeAddEdge = null;
    private Node endNodeAddEdge = null;

    private Node startNodeRemoveEdge = null;
    private Node endNodeRemoveEdge = null;

    private Node startNodeBFS = null;

    private Node startNodeDFS = null;

    private Node startNodeShortPath = null;
    private Node endNodeShortPath = null;

    private Node startNodeTopoSort = null;

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
                        GraphGUI.updateStatusBarNode(mode, addedNode);
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
                    GraphGUI.updateStatusBarNode(mode, removed);
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
                        GraphGUI.updateStatusBarNode(mode, startNodeAddEdge);
                        break;
                    }
                }
            } else {

                for (Node node : nodes) {
                    if (node.contains(e.getX(), e.getY())) {
                        endNodeAddEdge = node;
                        if (!endNodeAddEdge.equals(startNodeAddEdge)) {
                            GraphGUI.updateStatusBarNode(mode, endNodeAddEdge);
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
                            Edge addedEdge = new Edge(startNodeAddEdge, endNodeAddEdge, input, true);
                            edges.add(addedEdge);
                            GraphGUI.updateStatusBarEdge(mode, addedEdge);

                            repaint();
                            validInput = true;
                            startNodeAddEdge = null;
                            endNodeAddEdge = null;
                            mode = null;
                            GraphGUI.resetStatusBar();

                        } else if (n == JOptionPane.NO_OPTION) {
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
                            Edge addedEdge = new Edge(startNodeAddEdge, endNodeAddEdge, input, false);
                            edges.add(addedEdge);
                            GraphGUI.updateStatusBarEdge(mode, addedEdge);

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
                        GraphGUI.updateStatusBarNode(mode, startNodeRemoveEdge);
                        break;
                    }
                }
            } else {
                for (Node node : nodes) {
                    if (node.contains(e.getX(), e.getY())) {
                        endNodeRemoveEdge = node;
                        GraphGUI.updateStatusBarNode(mode, endNodeRemoveEdge);
                        break;
                    }
                }

                ArrayList<Edge> removedEdges = new ArrayList<>();
                edges.removeIf(edge -> {
                    if ((edge.getPrecedingNode().equals(startNodeRemoveEdge) &&
                            edge.getSucceedingNode().equals(endNodeRemoveEdge)) ||
                            (edge.getPrecedingNode().equals(endNodeRemoveEdge) &&
                                    edge.getSucceedingNode().equals(startNodeRemoveEdge) &&
                                    !edge.getIsDirected())) {
                        removedEdges.add(edge);
                        return true;
                    }
                    return false;
                });

                GraphGUI.updateStatusBarEdge(mode, removedEdges);

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
                        GraphGUI.updateStatusBarNode(mode, startNodeBFS);
                        break;
                    }
                }
            }

            GraphAlgorithms.createAdjacencyList(nodes, edges);

            GraphAlgorithms.resetAlgorithmVariables();
            GraphAlgorithms.BFS(startNodeBFS);
            ArrayList<Node> bfs = GraphAlgorithms.getBFSOrdering();

            String bfsString = "";
            for (int i = 0; i < bfs.size(); i++) {
                bfsString += bfs.get(i).getName();
                if (i != bfs.size() - 1) {
                    bfsString += " -> ";
                }
            }

            JOptionPane.showMessageDialog(this, bfsString, "BFS Ordering",
                    JOptionPane.INFORMATION_MESSAGE);

            startNodeBFS = null;
            mode = null;
            GraphGUI.resetStatusBar();
        }

        else if(mode == Mode.DFS) {
            if (startNodeDFS == null) {
                for (Node node : nodes) {
                    if (node.contains(e.getX(), e.getY())) {
                        startNodeDFS = node;
                        GraphGUI.updateStatusBarNode(mode, startNodeDFS);
                        break;
                    }
                }
            }

            GraphAlgorithms.createAdjacencyList(nodes, edges);

            GraphAlgorithms.resetAlgorithmVariables();
            GraphAlgorithms.DFS(startNodeDFS);

            ArrayList<Node> dfs = GraphAlgorithms.getDFSOrdering();
            StringBuilder dfsString = new StringBuilder();
            for (int i = 0; i < dfs.size(); i++) {
                Node n = dfs.get(i);
                dfsString.append(n.getName()).
                        append(": D/F time = ").
                        append(GraphAlgorithms.getStartFinishTimes().get(n)[0]).
                        append("/").append(GraphAlgorithms.getStartFinishTimes().get(n)[1]);

                if (i != dfs.size() - 1) {
                    dfsString.append("\n");
                }
            }

            JOptionPane.showMessageDialog(this, dfsString.toString(),
                    "DFS Order of Discovery:", JOptionPane.INFORMATION_MESSAGE);

            startNodeDFS = null;
            mode = null;
            GraphGUI.resetStatusBar();

        }

        else if (mode == Mode.SHORTEST_PATH) {
            if (startNodeShortPath == null) {
                for (Node node : nodes) {
                    if (node.contains(e.getX(), e.getY())) {
                        startNodeShortPath = node;
                        GraphGUI.updateStatusBarNode(mode, startNodeShortPath);
                        break;
                    }
                }
            } else {
                for (Node node : nodes) {
                    if (node.contains(e.getX(), e.getY())) {
                        endNodeShortPath = node;
                        GraphGUI.updateStatusBarNode(mode, endNodeShortPath);
                        break;
                    }
                }

                GraphAlgorithms.createAdjacencyList(nodes, edges);
                GraphAlgorithms.resetAlgorithmVariables();

                LinkedList<Node> path = GraphAlgorithms.shortestPath(startNodeShortPath, endNodeShortPath);
                if (path.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "There is no path from " +
                                    startNodeShortPath.getName() + " to " + endNodeShortPath.getName() + "!", "Shortest Path",
                            JOptionPane.INFORMATION_MESSAGE);
                } else {
                    StringBuilder pathString = new StringBuilder();
                    for (int i = 0; i < path.size(); i++) {
                        pathString.append(path.get(i).getName());
                        if (i != path.size() - 1) {
                            pathString.append(" -> ");
                        }
                    }
                    JOptionPane.showMessageDialog(this, pathString.toString(), "Shortest Path from " +
                                    startNodeShortPath.getName() + " to " + endNodeShortPath.getName() + ":",
                            JOptionPane.INFORMATION_MESSAGE);
                }

                GraphGUI.resetStatusBar();
                startNodeShortPath = null;
                endNodeShortPath = null;
            }
        }

        else if(mode == Mode.TOPOLOGICAL_SORT) {
            if (startNodeTopoSort == null) {
                for (Node node : nodes) {
                    if (node.contains(e.getX(), e.getY())) {
                        startNodeTopoSort = node;
                        GraphGUI.updateStatusBarNode(mode, startNodeTopoSort);
                        break;
                    }
                }
            }

            GraphAlgorithms.createAdjacencyList(nodes, edges);

            GraphAlgorithms.resetAlgorithmVariables();
            GraphAlgorithms.topologicalSort(startNodeTopoSort);

            if (!GraphAlgorithms.getIsDAG()) {
                JOptionPane.showMessageDialog(this, "This graph is not a DAG! " +
                        "Please remove any cycles and try again :)", "Invalid graph!", JOptionPane.ERROR_MESSAGE);
            } else {
                ArrayList<Node> topoSort = GraphAlgorithms.getTopologicalSort();
                StringBuilder topoSortString = new StringBuilder();
                for (int i = 0; i < topoSort.size(); i++) {
                    topoSortString.append(topoSort.get(i).getName());
                    if (i != topoSort.size() - 1) {
                        topoSortString.append(" -> ");
                    }
                }

                JOptionPane.showMessageDialog(this, topoSortString.toString(),
                        "Topological sorting from " + startNodeTopoSort.getName() + ":",
                        JOptionPane.INFORMATION_MESSAGE);
            }


            startNodeTopoSort = null;
            mode = null;
            GraphGUI.resetStatusBar();
        }
    }

    public void mouseEntered(MouseEvent e) {}

    public void mouseExited(MouseEvent e) {}

    public void mousePressed(MouseEvent e) {}

    public void mouseReleased(MouseEvent e) {}
}