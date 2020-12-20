package cz.shroomware.diorama.engine.ai;

import cz.shroomware.diorama.engine.level.Floor;

public class Grid {
    protected Node[][] nodes;
    protected int width;
    protected int height;

    public Grid(Floor floor) {
        width = floor.getWidth();
        height = floor.getHeight();
        nodes = new Node[width][height];

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                Node node = new Node(x, y, floor.getTileAtIndex(x, y));

                nodes[x][y] = node;
            }
        }
    }

    public void calculateHeuristics(Node targetNode) {
        Node node;
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                node = nodes[x][y];

//                node.setHeuristic(Math.sqrt(
//                        Math.pow(node.x_index - targetNode.x_index, 2)
//                                + Math.pow(node.y_index - targetNode.y_index, 2)));
                node.setHeuristic(node.x_index - targetNode.x_index + node.y_index - targetNode.y_index);
            }
        }
    }

    public Node getNode(int x, int y) {
        if (x < 0 || y < 0 || x >= width || y >= height) {
            return null;
        }

        return nodes[x][y];
    }

    public boolean canWalk(int x, int y) {
        Node node = nodes[x][y];

        if (node == null) {
            return false;
        }

        return node.canWalk();
    }
}
