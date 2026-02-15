package util;

import java.awt.Color;

public class Config {
    public static final float SMOOTHING_FACTOR = 0.35f;
    public static final int MAX_CELL_SIZE = 40;
    public static final int MIN_WINDOW_SIZE = 800;
    public static final int BFS_MAX_DEPTH = 100;
    public static final String SOUND_COLLECT = "collect.wav";
    public static final String SOUND_MOVE = "move.wav";
    public static final String SOUND_WIN = "win.wav";
    public static final String SOUND_GOLD = "gold.wav";
    public static final String SOUND_SAPPHIRE = "sapphire.wav";
    public static final String SOUND_EMERALD = "emerald.wav";
    public static final String SOUND_RUBY = "ruby.wav";
    public static final String SOUND_DIAMOND = "diamond.wav";

    public static final Color COLOR_HUMAN = Color.BLUE;
    public static final Color COLOR_CPU = Color.RED;
    public static final Color COLOR_HUMAN_COLORBLIND = new Color(0, 120, 215);
    public static final Color COLOR_CPU_COLORBLIND = new Color(255, 140, 0);
    public static final boolean USE_PLAYER_OUTLINES = true;
}