package ui;

import java.awt.*;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import javax.swing.*;
import model.*;
import util.Config;

public class GamePanel extends JPanel {

    private Maze maze;
    private Player h, c;
    private GameUI gameUI;
    private int cell;
    private boolean isColorBlindMode;

    private Map<Point, Float> cashAnimations = new HashMap<>();
    private Map<Point, Float> wallBumps = new HashMap<>();

    public GamePanel(Maze m, Player h, Player c, GameUI gameUI,
                     int size, boolean isColorBlindMode) {

        this.maze = m;
        this.h = h;
        this.c = c;
        this.gameUI = gameUI;
        this.isColorBlindMode = isColorBlindMode;

        this.cell = Math.min(Config.MAX_CELL_SIZE,
                Config.MIN_WINDOW_SIZE / size);

        setPreferredSize(new Dimension(
                m.size * cell,
                m.size * cell + 60
        ));

        setBackground(new Color(25, 25, 35));
        setFocusable(true);

        new Timer(16, e -> {
            h.smooth();
            c.smooth();
            updateAnimations();
            repaint();
        }).start();
    }

    // ================= Animation Updates =================
    private void updateAnimations() {

        Iterator<Map.Entry<Point, Float>> cashIt =
                cashAnimations.entrySet().iterator();

        while (cashIt.hasNext()) {
            Map.Entry<Point, Float> entry = cashIt.next();
            float value = entry.getValue() - 0.05f;
            if (value <= 0) cashIt.remove();
            else entry.setValue(value);
        }

        Iterator<Map.Entry<Point, Float>> wallIt =
                wallBumps.entrySet().iterator();

        while (wallIt.hasNext()) {
            Map.Entry<Point, Float> entry = wallIt.next();
            float value = entry.getValue() - 0.1f;
            if (value <= 0) wallIt.remove();
            else entry.setValue(value);
        }
    }

    public void animateCash(int x, int y) {
        cashAnimations.put(new Point(x, y), 1.0f);
    }

    public void animateWallBump(int x, int y) {
        wallBumps.put(new Point(x, y), 1.0f);
    }

    // ================= Rendering =================
    @Override
    protected void paintComponent(Graphics g) {

        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;

        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);

        drawGrid(g2);
        drawPlayers(g2);
        drawHUD(g2);
    }

    // ================= Grid =================
    private void drawGrid(Graphics2D g2) {

        for (int r = 0; r < maze.size; r++) {
            for (int col = 0; col < maze.size; col++) {

                int x = col * cell;
                int y = r * cell;

                // Modern tile background
                g2.setColor(new Color(45, 45, 65));
                g2.fillRoundRect(x, y, cell - 2, cell - 2, 14, 14);

                // Wall
                if (maze.grid[r][col] == CellType.WALL) {

                    Point p = new Point(r, col);

                    if (wallBumps.containsKey(p)) {
                        float alpha = wallBumps.get(p);
                        g2.setColor(new Color(1f, 0.3f, 0.3f, alpha));
                    } else {
                        g2.setColor(new Color(90, 90, 110));
                    }

                    g2.fillRoundRect(x, y, cell - 2, cell - 2, 14, 14);
                }

                // Cash / Gem
                if (maze.hasCash(r, col)) {

                    Maze.GemType gem = maze.getGemType(r, col);
                    if (gem != null) {

                        Point p = new Point(r, col);
                        float alpha =
                                cashAnimations.getOrDefault(p, 1.0f);

                        // Glow effect
                        g2.setColor(new Color(1f, 1f, 0f, 0.15f * alpha));
                        g2.fillOval(
                                x + cell / 4,
                                y + cell / 4,
                                cell / 2,
                                cell / 2
                        );

                        Font emojiFont = new Font(
                                "Segoe UI Emoji",
                                Font.PLAIN,
                                (int) (cell * 0.7)
                        );

                        g2.setFont(emojiFont);
                        g2.setColor(new Color(1f, 1f, 1f, alpha));

                        String emoji = gem.emoji;
                        FontMetrics fm = g2.getFontMetrics();

                        int emojiX = x + (cell - fm.stringWidth(emoji)) / 2;
                        int emojiY = y + (cell + fm.getAscent()) / 2;

                        g2.drawString(emoji, emojiX, emojiY);
                    }
                }
            }
        }
    }

    // ================= Players =================
    private void drawPlayers(Graphics2D g2) {

        drawPlayer(g2, h,
                isColorBlindMode ?
                        Config.COLOR_HUMAN_COLORBLIND :
                        Config.COLOR_HUMAN,
                "H");

        drawPlayer(g2, c,
                isColorBlindMode ?
                        Config.COLOR_CPU_COLORBLIND :
                        Config.COLOR_CPU,
                "C");
    }

    private void drawPlayer(Graphics2D g2,
                            Player player,
                            Color color,
                            String label) {

        int size = cell * 3 / 4;
        int px = (int) (player.ry * cell + cell / 8);
        int py = (int) (player.rx * cell + cell / 8);

        // Soft shadow
        g2.setColor(new Color(0, 0, 0, 80));
        g2.fillOval(px + 3, py + 3, size, size);

        g2.setColor(color);
        g2.fillOval(px, py, size, size);

        if (Config.USE_PLAYER_OUTLINES) {

            g2.setColor(Color.WHITE);
            g2.setStroke(new BasicStroke(3));
            g2.drawOval(px, py, size, size);

            g2.setFont(new Font("Arial", Font.BOLD, cell / 3));
            FontMetrics fm = g2.getFontMetrics();

            int labelX = px + (size - fm.stringWidth(label)) / 2;
            int labelY = py + (size + fm.getAscent()) / 2;

            g2.setColor(Color.BLACK);
            g2.drawString(label, labelX, labelY);
        }
    }

    // ================= HUD =================
    private void drawHUD(Graphics2D g2) {

        g2.setColor(Color.WHITE);
        g2.setFont(new Font("Arial", Font.BOLD, 14));

        int movesLeft =
                (gameUI != null) ? gameUI.movesRemaining : 0;

        int gemsLeft = maze.getRemainingCashCount();

        g2.drawString(
                String.format(
                        "Human ₹%d | CPU ₹%d | H Moves: %d | C Moves: %d | Gems Left: %d | Moves Left: %d",
                        h.score, c.score,
                        h.moves, c.moves,
                        gemsLeft, movesLeft
                ),
                10,
                maze.size * cell + 35
        );
    }
}