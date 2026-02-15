package util;

public enum Difficulty {
    EASY(10),
    MEDIUM(20),
    HARD(30);

    private final int mazeSize;
    Difficulty(int size){ this.mazeSize = size; }
    public int getMazeSize(){ return mazeSize; }
}