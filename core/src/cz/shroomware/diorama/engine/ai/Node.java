package cz.shroomware.diorama.engine.ai;

public class Node {
    public boolean canWalk;
    public int x_index, y_index;

    public Node parent;
    public double f = 0;
    public double g = 0;
    public double h = 0;

    public Node(int x, int y, boolean canWalk) {
        this.x_index = x;
        this.y_index = y;
        this.canWalk = canWalk;
    }

    public void setHeuristic(double h) {
        this.parent = null;
        this.f = 0;
        this.g = Double.MAX_VALUE;
        this.h = h;
    }

    public boolean observe(Node parent, double g) {
        if (canWalk && g < this.g) {
//            Gdx.app.log("Observed", "x:" + x_index + " y:" + y_index);
            this.parent = parent;
            this.g = g;
            this.f = this.g + this.h;

            return true;
        }
        return false;
    }

    public float getX() {
        return x_index + 0.5f;
    }

    public float getY() {
        return y_index + 0.5f;
    }
}
