import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.HashMap;

class GraphCanvas extends JPanel implements MouseListener {
    enum Mode {ADD_NODE, REMOVE_NODE, ADD_EDGE, REMOVE_EDGE, BFS, DFS, SHORTEST_PATH, TOPOLOGICAL_SORT}

    private Mode mode;
    private ArrayList<Node> nodes;
    private ArrayList<Edge> edges;

    private Node startNode = null;
    private Node endNode = null;

    private Node startRemoveEdgeNode = null;
    private Node endRemoveEdgeNode = null;

    public GraphCanvas() {
        setPreferredSize(new Dimension(400, 400));
        addMouseListener(this);
        nodes = new ArrayList<Node>();
        edges = new ArrayList<Edge>();
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
                        System.out.println("Node Added: " + addedNode);
                        repaint();
                        validInput = true;
                        mode = null;
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
                    System.out.println("Node Removed: " + removed);
                    edges.removeIf
                            (edge -> edge.getPrecedingNode().equals(removed) || edge.getSucceedingNode().equals(removed));
                    repaint();
                    mode = null;
                    break;
                }
            }
        }
        else if (mode == Mode.ADD_EDGE) {
            if (startNode == null) {
                for (Node node : nodes) {
                    if (node.contains(e.getX(), e.getY())) {
                        startNode = node;
                        System.out.println(startNode);
                        break;
                    }
                }
            } else {
                // TODO: No self-loops not working
                for (Node node : nodes) {
                    if (node.contains(e.getX(), e.getY())) {
                        endNode = node;
                        System.out.println(endNode);
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
                        mode = null;

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
                        System.out.println("Edge Added Start Node: " + startNode);
                        System.out.println("Edge Added End Node: " + endNode);

                        repaint();
                        validInput = true;
                        startNode = null;
                        endNode = null;
                        mode = null;
                    }
                }
            }
        }
        else if (mode == Mode.REMOVE_EDGE) {
            if (startRemoveEdgeNode == null) {
                for (Node node : nodes) {
                    if (node.contains(e.getX(), e.getY())) {
                        startRemoveEdgeNode = node;
                        System.out.println("Remove Edge Start: " + startRemoveEdgeNode);
                        break;
                    }
                }
            } else {
                // TODO: No self-loops not working
                for (Node node : nodes) {
                    if (node.contains(e.getX(), e.getY())) {
                        endRemoveEdgeNode = node;
                        System.out.println("Remove Edge End: " + endRemoveEdgeNode);
                        break;
                    }
                }

                edges.removeIf
                        (edge -> edge.getPrecedingNode().equals(startRemoveEdgeNode) && edge.getSucceedingNode().equals(endRemoveEdgeNode));
                System.out.println("Edge Removed");
                repaint();
                startRemoveEdgeNode = null;
                endRemoveEdgeNode = null;
                mode = null;
            }

            /*
            startNode = null;
            endNode = null;

            for (Node node : nodes) {
                if (node.contains(e.getX(), e.getY())) {
                    startNode = node;
                    System.out.println("Remove Edge Start: " + startNode);
                    break;
                }
            }

            for (Node node : nodes) {
                if (node.contains(e.getX(), e.getY())) {
                    endNode = node;
                    System.out.println("Remove Edge End: " + endNode);
                    break;
                }
            }

            edges.removeIf
                    (edge -> edge.getPrecedingNode().equals(startNode) && edge.getSucceedingNode().equals(endNode));
            repaint();
            startNode = null;
            endNode = null;
            mode = null;

             */
        }
    }

    public void mouseEntered(MouseEvent e) {}

    public void mouseExited(MouseEvent e) {}

    public void mousePressed(MouseEvent e) {}

    public void mouseReleased(MouseEvent e) {}
}