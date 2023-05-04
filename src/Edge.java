import java.awt.*;

public class Edge {
    private final Node nodeA;
    private final Node nodeB;
    private final int weight;
    private final boolean directed;

    public Edge (Node nodeA, Node nodeB, int weight, boolean directed) {
        this.nodeA = nodeA;
        this.nodeB = nodeB;
        this.weight = weight;
        this.directed = directed;
    }

    public Node getPrecedingNode() {
        return this.nodeA;
    }

    public Node getSucceedingNode() {
        return this.nodeB;
    }

    public int getWeight() {
        return this.weight;
    }

    public boolean getIsDirected() {
        return this.directed;
    }

    public void draw(Graphics g) {
        Point a = getPrecedingNode().getLocation();
        Point b = getSucceedingNode().getLocation();

        int arrowSize = 10;
        double angle = Math.atan2(b.y - a.y, b.x - a.x);
        double v = arrowSize * Math.cos(angle + Math.PI / 6);
        int x3 = (int) (b.x - v);
        double v1 = arrowSize * Math.sin(angle + Math.PI / 6);
        int y3 = (int) (b.y - v1);
        double v2 = arrowSize * Math.cos(angle - Math.PI / 6);
        int x4 = (int) (b.x - v2);
        double v3 = arrowSize * Math.sin(angle - Math.PI / 6);
        int y4 = (int) (b.y - v3);

        if (getIsDirected()) {
            g.drawLine(a.x, a.y, b.x, b.y);

            g.fillPolygon(new int[]{b.x, x3, x4}, new int[]{b.y, y3, y4}, 3);
        }
        else {
            int x5 = (int) (a.x + v);
            int y5 = (int) (a.y + v1);
            int x6 = (int) (a.x + v2);
            int y6 = (int) (a.y + v3);
            g.drawLine(a.x, a.y, b.x, b.y);

            g.fillPolygon(new int[]{b.x, x3, x4}, new int[]{b.y, y3, y4}, 3);
            g.fillPolygon(new int[]{a.x, x5, x6}, new int[]{a.y, y5, y6}, 3);
        }

        String weightStr = Integer.toString(getWeight());
        int x = (a.x + b.x) / 2;
        int y = (a.y + b.y) / 2;
        g.drawString(weightStr, x - g.getFontMetrics().stringWidth(weightStr) / 2,
                y - g.getFontMetrics().getHeight());
    }
}