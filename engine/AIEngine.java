package engine;

import java.util.*;
import model.*;
import util.Config;

public class AIEngine {
    private Maze maze;
    private final int[] dx = {-1, 1, 0, 0};
    private final int[] dy = {0, 0, -1, 1};

    public AIEngine(Maze maze) {
        this.maze = maze;
    }

    public void move(Player cpu, AlgorithmType type) {
        Node target = selectBestCoin(cpu);
        if (target == null) return;

        List<Node> path;
        switch (type) {
            case BFS:
                path = bfs(cpu.x, cpu.y, target.x, target.y);
                break;
            case DFS:
                path = dfs(cpu.x, cpu.y, target.x, target.y);
                break;
            case GREEDY:
                path = greedy(cpu.x, cpu.y, target.x, target.y);
                break;
            // case ASTAR:
            //     path = astar(cpu.x, cpu.y, target.x, target.y);
            //     break;
            case DNC:
                path = divideAndConquerPath(cpu.x, cpu.y, target.x, target.y);
                break;
            default:
                path = Collections.emptyList();
        }

        if (path.size() > 1) {
            Node next = path.get(1);
            cpu.x = next.x;
            cpu.y = next.y;
            // cpu.moves++;
        }
    }

    private Node selectBestCoin(Player cpu) {
        double bestScore = -1;
        Node best = null;
        for (int i = 0; i < maze.size; i++) {
            for (int j = 0; j < maze.size; j++) {
                if (maze.hasCash(i, j)) {
                    int value = maze.cash[i][j];
                    List<Node> path = bfs(cpu.x, cpu.y, i, j); // Real path distance
                    int dist = path.isEmpty() ? Integer.MAX_VALUE : path.size();
                    if (dist == 0) dist = 1;
                    double score = (double) value / dist;
                    if (score > bestScore) {
                        bestScore = score;
                        best = new Node(i, j);
                    }
                }
            }
        }
        return best;
    }

    private List<Node> bfs(int sx, int sy, int tx, int ty) {
        boolean[][] visited = new boolean[maze.size][maze.size];
        Node[][] parent = new Node[maze.size][maze.size];
        Queue<Node> q = new LinkedList<>();
        q.add(new Node(sx, sy));
        visited[sx][sy] = true;
        int depth = 0;

        while (!q.isEmpty() && depth < Config.BFS_MAX_DEPTH) {
            Node cur = q.poll();
            if (cur.x == tx && cur.y == ty)
                return reconstruct(parent, cur);
            for (int d = 0; d < 4; d++) {
                int nx = cur.x + dx[d];
                int ny = cur.y + dy[d];
                if (maze.valid(nx, ny) && !visited[nx][ny]) {
                    visited[nx][ny] = true;
                    parent[nx][ny] = cur;
                    q.add(new Node(nx, ny));
                }
            }
            depth++;
        }
        return Collections.emptyList();
    }

    private List<Node> dfs(int sx, int sy, int tx, int ty) {
        boolean[][] visited = new boolean[maze.size][maze.size];
        Stack<Node> stack = new Stack<>();
        Node[][] parent = new Node[maze.size][maze.size];
        stack.push(new Node(sx, sy));
        visited[sx][sy] = true;
        int depth = 0;

        while (!stack.isEmpty() && depth < Config.BFS_MAX_DEPTH) {
            Node cur = stack.pop();
            if (cur.x == tx && cur.y == ty)
                return reconstruct(parent, cur);
            for (int d = 0; d < 4; d++) {
                int nx = cur.x + dx[d];
                int ny = cur.y + dy[d];
                if (maze.valid(nx, ny) && !visited[nx][ny]) {
                    visited[nx][ny] = true;
                    parent[nx][ny] = cur;
                    stack.push(new Node(nx, ny));
                }
            }
            depth++;
        }
        return Collections.emptyList();
    }

    private List<Node> greedy(int sx, int sy, int tx, int ty) {
        List<Node> path = new ArrayList<>();
        path.add(new Node(sx, sy));
        int cx = sx, cy = sy;
        int depth = 0;
        while ((cx != tx || cy != ty) && depth < Config.BFS_MAX_DEPTH) {
            int bestD = -1;
            double minDist = Double.MAX_VALUE;
            for (int d = 0; d < 4; d++) {
                int nx = cx + dx[d];
                int ny = cy + dy[d];
                if (maze.valid(nx, ny)) {
                    double dist = Math.abs(nx - tx) + Math.abs(ny - ty);
                    if (dist < minDist) {
                        minDist = dist;
                        bestD = d;
                    }
                }
            }
            if (bestD == -1) break;
            cx += dx[bestD];
            cy += dy[bestD];
            path.add(new Node(cx, cy));
            depth++;
        }
        return path;
    }

    private List<Node> divideAndConquerPath(int sx, int sy, int tx, int ty) {

    // If very close → use BFS directly
    if (Math.abs(sx - tx) + Math.abs(sy - ty) <= 4) {
        return bfs(sx, sy, tx, ty);
    }

    int midX = (sx + tx) / 2;
    int midY = (sy + ty) / 2;

    // If midpoint invalid, fallback to BFS
    if (!maze.valid(midX, midY)) {
        return bfs(sx, sy, tx, ty);
    }

    // Solve first half
    List<Node> first = bfs(sx, sy, midX, midY);

    // If failed → fallback
    if (first == null || first.isEmpty()) {
        return bfs(sx, sy, tx, ty);
    }

    // Solve second half
    List<Node> second = bfs(midX, midY, tx, ty);

    if (second == null || second.isEmpty()) {
        return bfs(sx, sy, tx, ty);
    }

    // Merge paths
    first.remove(first.size() - 1);
    first.addAll(second);

    return first;
}

    private List<Node> astar(int sx, int sy, int tx, int ty) {
        PriorityQueue<Node> pq = new PriorityQueue<>((a, b) -> {
            int costA = Math.abs(a.x - tx) + Math.abs(a.y - ty) + a.cost;
            int costB = Math.abs(b.x - tx) + Math.abs(b.y - ty) + b.cost;
            return costA - costB;
        });
        boolean[][] visited = new boolean[maze.size][maze.size];
        Node[][] parent = new Node[maze.size][maze.size];
        pq.add(new Node(sx, sy, 0));
        int depth = 0;

        while (!pq.isEmpty() && depth < Config.BFS_MAX_DEPTH) {
            Node cur = pq.poll();
            if (cur.x == tx && cur.y == ty)
                return reconstruct(parent, cur);
            if (visited[cur.x][cur.y]) continue;
            visited[cur.x][cur.y] = true;
            for (int d = 0; d < 4; d++) {
                int nx = cur.x + dx[d];
                int ny = cur.y + dy[d];
                if (maze.valid(nx, ny) && !visited[nx][ny]) {
                    parent[nx][ny] = cur;
                    pq.add(new Node(nx, ny, cur.cost + 1));
                }
            }
            depth++;
        }
        return Collections.emptyList();
    }

    private List<Node> reconstruct(Node[][] parent, Node end) {
        List<Node> path = new ArrayList<>();
        Node cur = end;
        while (cur != null) {
            path.add(cur);
            cur = parent[cur.x][cur.y];
        }
        Collections.reverse(path);
        return path;
    }

    private static class Node {
        int x, y, cost;
        Node(int x, int y) {
            this(x, y, 0);
        }
        Node(int x, int y, int cost) {
            this.x = x;
            this.y = y;
            this.cost = cost;
        }
    }
}