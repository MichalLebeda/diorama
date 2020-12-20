package cz.shroomware.diorama.engine.ai;

import com.badlogic.gdx.utils.Array;

import java.util.ArrayList;

import cz.shroomware.diorama.engine.level.Floor;

public class AStar {
    Grid grid;

    public AStar(Floor floor) {
        this.grid = new Grid(floor);
    }

    public Grid getGrid() {
        return grid;
    }

    public Array<Node> findPath(int fromX, int fromY, int toX, int toY) {
//        int step = 0;
//        int limit = 200;

        ArrayList<Node> open = new ArrayList<>();

        Node to = grid.getNode(toX, toY);
        grid.calculateHeuristics(to);

        Node from = grid.getNode(fromX, fromY);
        from.g = 0;
        open.add(from);

        while (!open.isEmpty()) {
            Node currentNode = open.get(0);

            for (Node anotherNode : open) {
                if (anotherNode.f < currentNode.f) {
                    currentNode = anotherNode;
                }
            }

            if (currentNode == to) {
                Array<Node> path = new Array<Node>();

                while (currentNode != null) {
                    path.add(currentNode);
                    currentNode = currentNode.parent;
                }

                path.reverse();
                path.removeIndex(0);

                return path;
            }

            open.remove(currentNode);

            double newG = currentNode.g + 1;

            Node neighbor = grid.getNode(currentNode.x_index, currentNode.y_index + 1);
            if (neighbor != null) {
                if (neighbor.observe(currentNode, newG)) {
                    if (!open.contains(neighbor)) {
                        open.add(neighbor);
                    }
                }
            }

            neighbor = grid.getNode(currentNode.x_index + 1, currentNode.y_index);
            if (neighbor != null) {
                if (neighbor.observe(currentNode, newG)) {
                    if (!open.contains(neighbor)) {
                        open.add(neighbor);
                    }
                }
            }

            neighbor = grid.getNode(currentNode.x_index, currentNode.y_index - 1);
            if (neighbor != null) {
                if (neighbor.observe(currentNode, newG)) {
                    if (!open.contains(neighbor)) {
                        open.add(neighbor);
                    }
                }
            }

            neighbor = grid.getNode(currentNode.x_index - 1, currentNode.y_index);
            if (neighbor != null) {
                if (neighbor.observe(currentNode, newG)) {
                    if (!open.contains(neighbor)) {
                        open.add(neighbor);
                    }
                }
            }

            newG = currentNode.g + 1.344;

            neighbor = grid.getNode(currentNode.x_index + 1, currentNode.y_index + 1);
            if (neighbor != null) {
                if (grid.canWalk(currentNode.x_index, currentNode.y_index + 1)
                        && grid.canWalk(currentNode.x_index + 1, currentNode.y_index)) {
                    if (neighbor.observe(currentNode, newG)) {
                        if (!open.contains(neighbor)) {
                            open.add(neighbor);
                        }
                    }
                }
            }

            neighbor = grid.getNode(currentNode.x_index - 1, currentNode.y_index - 1);
            if (neighbor != null) {
                if (grid.canWalk(currentNode.x_index, currentNode.y_index - 1)
                        && grid.canWalk(currentNode.x_index - 1, currentNode.y_index)) {
                    if (neighbor.observe(currentNode, newG)) {
                        if (!open.contains(neighbor)) {
                            open.add(neighbor);
                        }
                    }
                }
            }

            neighbor = grid.getNode(currentNode.x_index - 1, currentNode.y_index + 1);
            if (neighbor != null) {
                if (grid.canWalk(currentNode.x_index, currentNode.y_index + 1)
                        && grid.canWalk(currentNode.x_index - 1, currentNode.y_index)) {
                    if (neighbor.observe(currentNode, newG)) {
                        if (!open.contains(neighbor)) {
                            open.add(neighbor);
                        }
                    }
                }
            }

            neighbor = grid.getNode(currentNode.x_index + 1, currentNode.y_index - 1);
            if (neighbor != null) {
                if (grid.canWalk(currentNode.x_index, currentNode.y_index - 1)
                        && grid.canWalk(currentNode.x_index + 1, currentNode.y_index)) {
                    if (neighbor.observe(currentNode, newG)) {
                        if (!open.contains(neighbor)) {
                            open.add(neighbor);
                        }
                    }
                }
            }
        }

        return null;
    }
}
