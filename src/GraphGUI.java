import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

public class GraphGUI extends JFrame {
    private final GraphCanvas canvas;
    private static JLabel statusBar;
    private static JLabel statusBarAdditional;

    public GraphGUI() {
        super("Graph GUI");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        canvas = new GraphCanvas();
        add(canvas, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(1, 4));

        JPanel algorithmButtonPanel = new JPanel();
        algorithmButtonPanel.setLayout(new GridLayout(1, 4));

        JButton addNodeButton = new JButton("Add Node");
        addNodeButton.addActionListener(e -> {
            statusBar.setText("Click on the Canvas to ADD a node.");
            statusBar.setForeground(Color.BLUE);
            canvas.setMode(GraphCanvas.Mode.ADD_NODE);
        });
        buttonPanel.add(addNodeButton);

        JButton removeNodeButton = new JButton("Remove Node");
        removeNodeButton.addActionListener(e -> {
            canvas.setMode(GraphCanvas.Mode.REMOVE_NODE);
            statusBar.setText("Click on a node to DELETE it.");
            statusBar.setForeground(Color.RED);
        });
        buttonPanel.add(removeNodeButton);

        JButton addEdgeButton = new JButton("Add Edge");
        addEdgeButton.addActionListener(e -> {
            canvas.setMode(GraphCanvas.Mode.ADD_EDGE);
            statusBar.setText("Click on two nodes to add an edge between them. Order matters for directed edges.");
            statusBar.setForeground(Color.BLUE);
        });
        buttonPanel.add(addEdgeButton);

        JButton removeEdgeButton = new JButton("Remove Edge");
        removeEdgeButton.addActionListener(e -> {
            canvas.setMode(GraphCanvas.Mode.REMOVE_EDGE);
            statusBar.setText("Click on two nodes to remove the edge between them. Order matters for directed edges.");
            statusBar.setForeground(Color.RED);
        });
        buttonPanel.add(removeEdgeButton);

        JButton BFS = new JButton("BFS");
        BFS.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                canvas.setMode(GraphCanvas.Mode.BFS);
                statusBar.setText("Click on the starting node.");
                statusBar.setForeground(Color.BLUE);
            }
        });
        algorithmButtonPanel.add(BFS);

        JButton DFS = new JButton("DFS");
        DFS.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                canvas.setMode(GraphCanvas.Mode.DFS);
                statusBar.setText("Click on a source node");
                statusBar.setForeground(Color.BLUE);
            }
        });
        algorithmButtonPanel.add(DFS);

        JButton shortestPath = new JButton("Shortest Path");
        shortestPath.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                canvas.setMode(GraphCanvas.Mode.SHORTEST_PATH);
                statusBar.setText("Click on two nodes to get the shortest path between them.");
                statusBar.setForeground(Color.BLUE);
            }
        });
        algorithmButtonPanel.add(shortestPath);

        JButton topologicalSort = new JButton("Topological Sort");
        topologicalSort.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                canvas.setMode(GraphCanvas.Mode.TOPOLOGICAL_SORT);
                statusBar.setText("Click on a source node");
                statusBar.setForeground(Color.BLUE);
            }
        });
        algorithmButtonPanel.add(topologicalSort);

        JPanel southPanel = new JPanel();
        southPanel.setLayout(new BoxLayout(southPanel, BoxLayout.Y_AXIS));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        southPanel.add(buttonPanel);
        southPanel.add(algorithmButtonPanel);
        add(southPanel, BorderLayout.SOUTH);

        JPanel statusBarPanel = new JPanel();
        statusBarPanel.setLayout(new BoxLayout(statusBarPanel, BoxLayout.Y_AXIS));
        statusBarPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        statusBar = new JLabel(" ");
        Font font = statusBar.getFont();
        statusBar.setFont(new Font(font.getName(), Font.BOLD, font.getSize()));
        statusBar.setText("Click on a button at the bottom.");
        statusBar.setAlignmentX(Component.CENTER_ALIGNMENT);
        statusBarPanel.add(statusBar);

        statusBarAdditional = new JLabel(" ");
        statusBarAdditional.setFont(new Font(font.getName(), Font.BOLD, font.getSize()));
        statusBarAdditional.setAlignmentX(Component.CENTER_ALIGNMENT);
        statusBarPanel.add(statusBarAdditional);

        add(statusBarPanel, BorderLayout.NORTH);

        pack();
        setVisible(true);
    }

    public static void resetStatusBar() {
        statusBar.setText("Click on a button at the bottom.");
        statusBar.setForeground(Color.BLACK);
    }

    public static void updateStatusBarNode(GraphCanvas.Mode mode, Node node) {
        if (mode == GraphCanvas.Mode.ADD_NODE) {
            statusBarAdditional.setText("Node Added: " + node + " | " + node.getName());
            statusBarAdditional.setForeground(new Color(0, 128, 0));
        }
        else if (mode == GraphCanvas.Mode.REMOVE_NODE) {
            statusBarAdditional.setText("Node Removed: " + node + " | " + node.getName());
            statusBarAdditional.setForeground(Color.RED);
        }
        else if (mode == GraphCanvas.Mode.ADD_EDGE || mode == GraphCanvas.Mode.REMOVE_EDGE ||
                mode == GraphCanvas.Mode.SHORTEST_PATH || mode == GraphCanvas.Mode.TOPOLOGICAL_SORT) {
            statusBarAdditional.setText("Node Selected: " + node + " | " + node.getName());
            statusBarAdditional.setForeground(new Color(255, 96, 0));
        }
        else if (mode == GraphCanvas.Mode.BFS) {
            statusBarAdditional.setText("BFS Start Node Selected: " + node + " | " + node.getName());
            statusBarAdditional.setForeground(new Color(255, 96, 0));
        }
        else if (mode == GraphCanvas.Mode.DFS) {
            statusBarAdditional.setText("DFS Start Node Selected: " + node + " | " + node.getName());
            statusBarAdditional.setForeground(new Color(255, 96, 0));
        }
    }

    public static void updateStatusBarEdge(GraphCanvas.Mode mode, Edge edge) {
        if (mode == GraphCanvas.Mode.ADD_EDGE) {
            if (edge.getIsDirected()) {
                statusBarAdditional.setText("Edge Added: " + edge.getPrecedingNode().getName() + " -> " +
                        edge.getSucceedingNode().getName() + " (" + edge.getWeight() + ")");
            }
            else {
                statusBarAdditional.setText("Edge Added: " + edge.getPrecedingNode().getName() + " <-> " +
                        edge.getSucceedingNode().getName() + " (" + edge.getWeight() + ")");
            }
            statusBarAdditional.setForeground(new Color(0, 128, 0));
        }
    }

    public static void updateStatusBarEdge(GraphCanvas.Mode mode, ArrayList<Edge> edges) {
        if (mode == GraphCanvas.Mode.REMOVE_EDGE) {
            if (edges.size() == 1) {
                if (edges.get(0).getIsDirected()) {
                    statusBarAdditional.setText("Edge Removed: " + edges.get(0).getPrecedingNode().getName() + " -> " +
                            edges.get(0).getSucceedingNode().getName() + " (" + edges.get(0).getWeight() + ")");
                }
                else {
                    statusBarAdditional.setText("Edge Removed: " + edges.get(0).getPrecedingNode().getName() + " <-> " +
                            edges.get(0).getSucceedingNode().getName() + " (" + edges.get(0).getWeight() + ")");
                }
            }
            else if (edges.size() > 1) {
                statusBarAdditional.setText("All edges incident to the selected edges were removed.");
            }
            else {
                statusBarAdditional.setText("There were no edges to remove.");
            }
            statusBarAdditional.setForeground(Color.RED);
        }
    }

    public static void main(String[] args) {
        new GraphGUI();
    }
}