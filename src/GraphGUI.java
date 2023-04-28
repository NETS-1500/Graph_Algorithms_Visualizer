import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class GraphGUI extends JFrame {
    private GraphCanvas canvas;
    private JButton addNodeButton;
    private JButton removeNodeButton;
    private JButton addEdgeButton;
    private JButton removeEdgeButton;
    private JButton BFS;
    private JButton DFS;
    private JButton shortestPath;
    private JButton topologicalSort;
    private JButton reset;
    private static JLabel statusBar;

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

        addNodeButton = new JButton("Add Node");
        addNodeButton.addActionListener(e -> {
            statusBar.setText("Click on the Canvas to ADD a node.");
            statusBar.setForeground(Color.BLUE);
            canvas.setMode(GraphCanvas.Mode.ADD_NODE);
        });
        buttonPanel.add(addNodeButton);

        removeNodeButton = new JButton("Remove Node");
        removeNodeButton.addActionListener(e -> {
            canvas.setMode(GraphCanvas.Mode.REMOVE_NODE);
            statusBar.setText("Click on a node to DELETE it.");
            statusBar.setForeground(Color.RED);
        });
        buttonPanel.add(removeNodeButton);

        addEdgeButton = new JButton("Add Edge");
        addEdgeButton.addActionListener(e -> {
            canvas.setMode(GraphCanvas.Mode.ADD_EDGE);
            statusBar.setText("Click on two nodes to add an edge between them. Order matters for directed edges.");
            statusBar.setForeground(Color.BLUE);
        });
        buttonPanel.add(addEdgeButton);

        removeEdgeButton = new JButton("Remove Edge");
        removeEdgeButton.addActionListener(e -> {
            canvas.setMode(GraphCanvas.Mode.REMOVE_EDGE);
            statusBar.setText("Click on two nodes to remove the edge between them. Order matters for directed edges.");
            statusBar.setForeground(Color.RED);
        });
        buttonPanel.add(removeEdgeButton);

        BFS = new JButton("BFS");
        BFS.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                canvas.setMode(GraphCanvas.Mode.BFS);
                statusBar.setText("Click on the starting node.");
                statusBar.setForeground(Color.BLUE);
            }
        });
        algorithmButtonPanel.add(BFS);

        DFS = new JButton("DFS");
        DFS.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                canvas.setMode(GraphCanvas.Mode.DFS);
                statusBar.setText("Click graph to get started");
                statusBar.setForeground(Color.BLUE);

            }
        });
        algorithmButtonPanel.add(DFS);

        shortestPath = new JButton("Shortest Path");
        shortestPath.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                canvas.setMode(GraphCanvas.Mode.SHORTEST_PATH);
                statusBar.setText("Click on two nodes to get the shortest path between them.");
                statusBar.setForeground(Color.BLUE);
            }
        });
        algorithmButtonPanel.add(shortestPath);

        topologicalSort = new JButton("Topological Sort");
        topologicalSort.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                canvas.setMode(GraphCanvas.Mode.TOPOLOGICAL_SORT);
            }
        });
        algorithmButtonPanel.add(topologicalSort);

        JPanel southPanel = new JPanel();
        southPanel.setLayout(new BoxLayout(southPanel, BoxLayout.Y_AXIS));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        southPanel.add(buttonPanel);
        southPanel.add(algorithmButtonPanel);
        add(southPanel, BorderLayout.SOUTH);

        statusBar = new JLabel(" ");
        statusBar.setHorizontalAlignment(SwingConstants.CENTER);
        Font font = statusBar.getFont();
        statusBar.setFont(new Font(font.getName(), Font.BOLD, font.getSize()));
        statusBar.setText("Click on a button at the bottom.");
        add(statusBar, BorderLayout.NORTH);

        pack();
        setVisible(true);
    }

    public static void resetStatusBar() {
        statusBar.setText("Click on a button at the bottom.");
        statusBar.setForeground(Color.BLACK);
    }

    public static void main(String[] args) {
        new GraphGUI();
    }
}