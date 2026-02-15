package model;

public class Player {
    public int x, y;
    public float rx, ry;
    public int score = 0;
    public int moves = 0;

    public Player(int x, int y) {
        this.x = x;
        this.y = y;
        rx = x;
        ry = y;
    }

    public void smooth() {
        rx += (x - rx) * 0.35f; 
        ry += (y - ry) * 0.35f;
    }
}
