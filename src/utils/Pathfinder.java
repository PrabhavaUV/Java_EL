package utils;

import java.awt.Point;
import java.util.List;
import java.util.ArrayList;
import java.util.PriorityQueue;
import java.util.HashSet;
import java.util.HashMap;

public class Pathfinder {
    private static final int[][] DIRECTIONS = {
            { 0, 1 }, { 1, 0 }, { 0, -1 }, { -1, 0 },
            { 1, 1 }, { 1, -1 }, { -1, 1 }, { -1, -1 }
    };
    private static final float STRAIGHT_COST = 1.0f;
    private static final float DIAGONAL_COST = 1.414f;

    private static class Node {
        Point point;
        float gScore;
        float hScore;
        float fScore;

        Node(Point point, float gScore, float hScore) {
            this.point = point;
            this.gScore = gScore;
            this.hScore = hScore;
            this.fScore = gScore + hScore;
        }
    }

    public Pathfinder() {
    }

    public List<Point> findPath(Point start, Point goal, int[][] collisionMap) {
        List<Point> path = new ArrayList<>();
        if (collisionMap == null || start == null || goal == null)
            return path;

        int mapHeight = collisionMap.length;
        int mapWidth = collisionMap[0].length;

        int startX = Math.max(0, Math.min(start.x, mapWidth - 1));
        int startY = Math.max(0, Math.min(start.y, mapHeight - 1));
        int goalX = Math.max(0, Math.min(goal.x, mapWidth - 1));
        int goalY = Math.max(0, Math.min(goal.y, mapHeight - 1));

        if (collisionMap[startY][startX] != 0 || collisionMap[goalY][goalX] != 0)
            return path;

        Point startPoint = new Point(startX, startY);
        Point goalPoint = new Point(goalX, goalY);

        PriorityQueue<Node> openSet = new PriorityQueue<>((n1, n2) -> Float.compare(n1.fScore, n2.fScore));
        HashSet<String> closedSet = new HashSet<>();
        HashMap<String, Node> cameFrom = new HashMap<>();

        Node startNode = new Node(startPoint, 0, heuristic(startPoint, goalPoint));
        openSet.add(startNode);

        while (!openSet.isEmpty()) {
            Node current = openSet.poll();
            if (current.point.equals(goalPoint)) {
                return reconstructPath(cameFrom, current);
            }

            String currentKey = current.point.x + "," + current.point.y;
            closedSet.add(currentKey);

            for (int i = 0; i < DIRECTIONS.length; i++) {
                int newX = current.point.x + DIRECTIONS[i][0];
                int newY = current.point.y + DIRECTIONS[i][1];

                if (newX < 0 || newX >= mapWidth || newY < 0 || newY >= mapHeight)
                    continue;
                if (collisionMap[newY][newX] != 0)
                    continue;

                String neighborKey = newX + "," + newY;
                if (closedSet.contains(neighborKey))
                    continue;

                float cost = (i < 4) ? STRAIGHT_COST : DIAGONAL_COST;
                float gScore = current.gScore + cost;
                Point neighborPoint = new Point(newX, newY);
                float hScore = heuristic(neighborPoint, goalPoint);
                Node neighbor = new Node(neighborPoint, gScore, hScore);

                boolean isNewNode = true;
                for (Node node : openSet) {
                    if (node.point.equals(neighborPoint)) {
                        if (gScore < node.gScore) {
                            openSet.remove(node);
                            openSet.add(neighbor);
                            cameFrom.put(neighborKey, current);
                        }
                        isNewNode = false;
                        break;
                    }
                }

                if (isNewNode) {
                    openSet.add(neighbor);
                    cameFrom.put(neighborKey, current);
                }
            }
        }
        return path;
    }

    private List<Point> reconstructPath(HashMap<String, Node> cameFrom, Node current) {
        List<Point> path = new ArrayList<>();
        path.add(current.point);
        String currentKey = current.point.x + "," + current.point.y;
        while (cameFrom.containsKey(currentKey)) {
            Node previous = cameFrom.get(currentKey);
            path.add(0, previous.point);
            currentKey = previous.point.x + "," + previous.point.y;
        }
        return path;
    }

    private float heuristic(Point a, Point b) {
        return Math.abs(a.x - b.x) + Math.abs(a.y - b.y);
    }
}
