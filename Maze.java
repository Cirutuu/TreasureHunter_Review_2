package model;

import java.util.*;
import util.Difficulty;

public class Maze {

    public int size;
    public CellType[][] grid;
    public int[][] cash;
    private int[][] initialCash;
    private CellType[][] initialGrid;
    public GemType[][] gems;

    private Difficulty difficulty = Difficulty.MEDIUM;

    // NEW: probabilities based on difficulty
    private double wallProbability;
    private double cashProbability;

    public Maze(int size) {
        this(size, Difficulty.MEDIUM);
    }

    public Maze(int size, Difficulty difficulty) {
        this.size = size;
        this.difficulty = difficulty;

        grid = new CellType[size][size];
        cash = new int[size][size];
        initialGrid = new CellType[size][size];
        initialCash = new int[size][size];
        gems = new GemType[size][size];

        setDifficultyParameters();

        Random r = new Random();
        generateMaze(r);
        removeUnreachableGems();
        saveInitialState();
        ensureConnectivity();
    }

    // ================= Difficulty Settings =================
    private void setDifficultyParameters() {
        switch (difficulty) {
            case EASY -> {
                wallProbability = 0.15;
                cashProbability = 0.30;
            }
            case MEDIUM -> {
                wallProbability = 0.25;
                cashProbability = 0.25;
            }
            case HARD -> {
                wallProbability = 0.35;
                cashProbability = 0.20;
            }
        }
    }

    // ================= Gem Types =================
    public enum GemType {
        GOLD(100, "🪙"),
        SAPPHIRE(200, "💠"),
        EMERALD(300, "💚"),
        RUBY(400, "❤️"),
        DIAMOND(500, "💎");

        public final int value;
        public final String emoji;

        GemType(int value, String emoji) {
            this.value = value;
            this.emoji = emoji;
        }

        public static GemType fromValue(int v) {
            for (GemType t : values()) {
                if (t.value == v) return t;
            }
            return GOLD;
        }
    }

    // ================= Maze Generation =================
    private void generateMaze(Random r) {

        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {

                double rand = r.nextDouble();

                if (rand < wallProbability) {

                    grid[i][j] = CellType.WALL;
                    gems[i][j] = null;

                } else if (rand < wallProbability + cashProbability) {

                    grid[i][j] = CellType.CASH;
                    int multiplier = r.nextInt(5) + 1;
                    cash[i][j] = multiplier * 100;
                    gems[i][j] = GemType.fromValue(cash[i][j]);

                } else {

                    grid[i][j] = CellType.EMPTY;
                    gems[i][j] = null;
                }
            }
        }

        // Ensure start and end are empty
        grid[0][0] = CellType.EMPTY;
        grid[size - 1][size - 1] = CellType.EMPTY;
        gems[0][0] = null;
        gems[size - 1][size - 1] = null;
    }

    // ================= Save Initial State =================
    private void saveInitialState() {
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                initialGrid[i][j] = grid[i][j];
                initialCash[i][j] = cash[i][j];
            }
        }
    }

    // ================= Restore State =================
    public void restoreState() {
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                grid[i][j] = initialGrid[i][j];
                cash[i][j] = initialCash[i][j];
                gems[i][j] = (grid[i][j] == CellType.CASH)
                        ? GemType.fromValue(cash[i][j])
                        : null;
            }
        }
    }

    // ================= Connectivity Check =================
    private void ensureConnectivity() {

        boolean[][] visited = new boolean[size][size];
        Queue<int[]> queue = new LinkedList<>();
        queue.add(new int[]{0, 0});
        visited[0][0] = true;

        int[] dx = {-1, 1, 0, 0};
        int[] dy = {0, 0, -1, 1};

        while (!queue.isEmpty()) {

            int[] cur = queue.poll();
            int x = cur[0], y = cur[1];

            for (int d = 0; d < 4; d++) {
                int nx = x + dx[d];
                int ny = y + dy[d];

                if (valid(nx, ny) && !visited[nx][ny]) {
                    visited[nx][ny] = true;
                    queue.add(new int[]{nx, ny});
                }
            }
        }

        if (!visited[size - 1][size - 1]) {
            clearPath();
        }
    }

    private void clearPath() {
        int x = 0, y = 0;
        Random r = new Random();

        while (x < size - 1 || y < size - 1) {

            grid[x][y] = CellType.EMPTY;
            gems[x][y] = null;

            if (x < size - 1 && y < size - 1) {
                if (r.nextBoolean()) x++;
                else y++;
            } else if (x < size - 1) {
                x++;
            } else {
                y++;
            }
        }

        grid[size - 1][size - 1] = CellType.EMPTY;
        gems[size - 1][size - 1] = null;
    }

    // ================= Utility Methods =================
    public boolean valid(int x, int y) {
        return x >= 0 && y >= 0 &&
               x < size && y < size &&
               grid[x][y] != CellType.WALL;
    }

    public boolean hasCash(int x, int y) {
        return gems[x][y] != null;
    }

    public int collect(int x, int y) {

        GemType type = gems[x][y];

        if (type != null) {
            int value = type.value;
            cash[x][y] = 0;
            gems[x][y] = null;
            grid[x][y] = CellType.EMPTY;
            return value;
        }

        return 0;
    }

    public boolean anyCashLeft() {
        for (int i = 0; i < size; i++)
            for (int j = 0; j < size; j++)
                if (gems[i][j] != null)
                    return true;

        return false;
    }

    public int getRemainingCashCount() {
        int count = 0;
        for (int i = 0; i < size; i++)
            for (int j = 0; j < size; j++)
                if (gems[i][j] != null)
                    count++;
        return count;
    }

    public GemType getGemType(int x, int y) {
        return gems[x][y];
    }
    private void removeUnreachableGems() {

    boolean[][] visited = new boolean[size][size];
    Queue<int[]> queue = new LinkedList<>();

    queue.add(new int[]{0, 0});
    visited[0][0] = true;

    int[] dx = {-1, 1, 0, 0};
    int[] dy = {0, 0, -1, 1};

    while (!queue.isEmpty()) {
        int[] cur = queue.poll();
        int x = cur[0], y = cur[1];

        for (int d = 0; d < 4; d++) {
            int nx = x + dx[d];
            int ny = y + dy[d];

            if (valid(nx, ny) && !visited[nx][ny]) {
                visited[nx][ny] = true;
                queue.add(new int[]{nx, ny});
            }
        }
    }

    // Remove unreachable gems
    for (int i = 0; i < size; i++) {
        for (int j = 0; j < size; j++) {
            if (gems[i][j] != null && !visited[i][j]) {
                gems[i][j] = null;
                cash[i][j] = 0;
                grid[i][j] = CellType.EMPTY;
            }
        }
    }
}
}