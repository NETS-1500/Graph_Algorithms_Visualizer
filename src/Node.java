import java.awt.*;

class Node {
    private static final int RADIUS = 15;

    private String name;
    private int x;
    private int y;

    public Node(String name, int x, int y) {
        this.name = name;
        this.x = x;
        this.y = y;
    }

    public String getName() {
        return name;
    }

    public Point getLocation() {
        return new Point(this.x, this.y);
    }

    public boolean contains(int x, int y) {
        int dx = this.x - x;
        int dy = this.y - y;
        return dx * dx + dy * dy <= RADIUS * RADIUS;
    }

    public void draw(Graphics g) {
        g.setColor(Color.BLACK);
        g.drawOval(x - RADIUS, y - RADIUS, RADIUS * 2, RADIUS * 2);
        Font originalFont = g.getFont();
        Font newFont = originalFont.deriveFont(originalFont.getSize() * 1.5F).deriveFont(Font.BOLD);
        g.setFont(newFont);
        g.drawString(name, x - g.getFontMetrics().stringWidth(name) / 2, y + g.getFontMetrics().getAscent() / 2);
        g.setFont(originalFont);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }

        if (this == obj) {
            return true;
        }

        if (!(obj instanceof Node)) {
            return false;
        }

        Node o = (Node) obj;

        if (!o.name.equals(this.name)) {
            return false;
        }

        if (o.x != this.x) {
            return false;
        }

        if (o.y != this.y) {
            return false;
        }

        return true;

    }
}