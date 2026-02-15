package ui;

import engine.*;
import java.awt.*;
import javax.swing.*;
import model.*;
import sound.SoundManager;
import util.Config;
import util.Difficulty;

public class GameUI extends JFrame {

    private Maze maze;
    private Player human, cpu;
    private AIEngine ai;
    private GamePanel panel;

    private int size;
    private AlgorithmType algo;
    private Difficulty difficulty;
    private boolean isColorBlindMode;

    private int moveLimit = 200;
    public int movesRemaining;

    private JLabel movesLabel;

    public GameUI(int size, AlgorithmType algo,
                  Difficulty difficulty,
                  boolean isColorBlindMode) {

        this.size = size;
        this.algo = algo;
        this.difficulty =
                (difficulty == null) ? Difficulty.MEDIUM : difficulty;
        this.isColorBlindMode = isColorBlindMode;

        setMoveLimit();
        initGame();
    }

    private void setMoveLimit() {
        switch (difficulty) {
            case EASY   -> movesRemaining = (int)(moveLimit * 1.5);
            case MEDIUM -> movesRemaining = moveLimit;
            case HARD   -> movesRemaining = (int)(moveLimit * 0.75);
        }
    }

    private void initGame() {

        maze = new Maze(size, difficulty);
        human = new Player(0, 0);
        cpu = new Player(size - 1, size - 1);
        ai = new AIEngine(maze);

        panel = new GamePanel(maze, human, cpu, this,
                              size, isColorBlindMode);

        JScrollPane scrollPane = new JScrollPane(panel);
        scrollPane.setBorder(null);

        setLayout(new BorderLayout());
        add(createTopBar(), BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);

        setTitle("Treasure Hunt");
        pack();
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setVisible(true);

        setupKeyBindings();   // 🔥 Proper movement handling
    }

    // ================= KEY BINDINGS (FIXED MOVEMENT) =================
    private void setupKeyBindings() {

        InputMap im = panel.getInputMap(
                JComponent.WHEN_IN_FOCUSED_WINDOW);
        ActionMap am = panel.getActionMap();

        im.put(KeyStroke.getKeyStroke("UP"), "moveUp");
        im.put(KeyStroke.getKeyStroke("DOWN"), "moveDown");
        im.put(KeyStroke.getKeyStroke("LEFT"), "moveLeft");
        im.put(KeyStroke.getKeyStroke("RIGHT"), "moveRight");

        im.put(KeyStroke.getKeyStroke("W"), "moveUp");
        im.put(KeyStroke.getKeyStroke("S"), "moveDown");
        im.put(KeyStroke.getKeyStroke("A"), "moveLeft");
        im.put(KeyStroke.getKeyStroke("D"), "moveRight");

        am.put("moveUp", new AbstractAction() {
            public void actionPerformed(java.awt.event.ActionEvent e) {
                handleHumanMove(-1, 0);
            }
        });

        am.put("moveDown", new AbstractAction() {
            public void actionPerformed(java.awt.event.ActionEvent e) {
                handleHumanMove(1, 0);
            }
        });

        am.put("moveLeft", new AbstractAction() {
            public void actionPerformed(java.awt.event.ActionEvent e) {
                handleHumanMove(0, -1);
            }
        });

        am.put("moveRight", new AbstractAction() {
            public void actionPerformed(java.awt.event.ActionEvent e) {
                handleHumanMove(0, 1);
            }
        });
    }

    // ================= TOP BAR =================
    private JPanel createTopBar() {

    JPanel bar = new JPanel(new BorderLayout());
    bar.setBackground(new Color(15, 18, 35));
    bar.setBorder(BorderFactory.createEmptyBorder(12, 25, 12, 25));

    // LEFT: Moves
    JPanel left = new JPanel();
    left.setOpaque(false);
    left.setLayout(new BoxLayout(left, BoxLayout.Y_AXIS));

    JLabel title = new JLabel("Treasure Hunt");
    title.setFont(new Font("SansSerif", Font.BOLD, 18));
    title.setForeground(new Color(200, 220, 255));

    movesLabel = new JLabel("Moves Left: " + movesRemaining);
    movesLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
    movesLabel.setForeground(Color.WHITE);

    left.add(title);
    left.add(movesLabel);

    // RIGHT: Buttons
    JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
    right.setOpaque(false);

    JButton reset = styledButton("Reset");
    JButton restart = styledButton("Restart");
    JButton fullscreen = styledButton("Fullscreen");
    JButton exit = styledButton("Exit");

    reset.addActionListener(e -> resetPositions());
    restart.addActionListener(e -> restartGame());
    fullscreen.addActionListener(e -> toggleFullscreen());
    exit.addActionListener(e -> System.exit(0));

    right.add(reset);
    right.add(restart);
    right.add(fullscreen);
    right.add(exit);

    bar.add(left, BorderLayout.WEST);
    bar.add(right, BorderLayout.EAST);

    return bar;
}

    private JButton styledButton(String text) {
        JButton btn = new JButton(text);
        btn.setFocusPainted(false);
        btn.setFont(new Font("SansSerif", Font.BOLD, 13));
        btn.setBackground(new Color(70, 130, 255));
        btn.setForeground(Color.WHITE);
        btn.setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }

    private void toggleFullscreen() {
        if (getExtendedState() == JFrame.MAXIMIZED_BOTH) {
            setExtendedState(JFrame.NORMAL);
            pack();
            setLocationRelativeTo(null);
        } else {
            setExtendedState(JFrame.MAXIMIZED_BOTH);
        }
    }

    private void resetPositions() {
        human.x = human.y = 0;
        cpu.x = cpu.y = size - 1;
        human.rx = human.x;
        human.ry = human.y;
        cpu.rx = cpu.x;
        cpu.ry = cpu.y;
        human.score = cpu.score = 0;
        human.moves = cpu.moves = 0;

        maze.restoreState();
        setMoveLimit();
        updateMoveLabel();
        panel.repaint();
    }

    private void restartGame() {
        dispose();
        new GameUI(size, algo, difficulty, isColorBlindMode);
    }

    private void updateMoveLabel() {
        movesLabel.setText("Moves Left: " + movesRemaining);
    }

    // ================= MOVEMENT LOGIC =================
    private void handleHumanMove(int dx, int dy) {

        if (movesRemaining <= 0) return;

        int nx = human.x + dx;
        int ny = human.y + dy;

        boolean moved = false;

        if (maze.valid(nx, ny)) {

            human.x = nx;
            human.y = ny;
            human.moves++;
            moved = true;

            SoundManager.play(Config.SOUND_MOVE);

            if (maze.hasCash(nx, ny)) {
                int value = maze.collect(nx, ny);
                human.score += value;
                panel.animateCash(nx, ny);
            }

        } else {
            panel.animateWallBump(nx, ny);
        }

        if (moved) {
            movesRemaining--;
            updateMoveLabel();
        }

        panel.repaint();

        if (movesRemaining > 0) {

            new SwingWorker<Void, Void>() {
                protected Void doInBackground() {
                    ai.move(cpu, algo);
                    return null;
                }

                protected void done() {

                    if (maze.hasCash(cpu.x, cpu.y)) {
                        int value = maze.collect(cpu.x, cpu.y);
                        cpu.score += value;
                        panel.animateCash(cpu.x, cpu.y);
                    }

                    cpu.moves++;
                    panel.repaint();

                    if (!maze.anyCashLeft() ||
                        movesRemaining <= 0) {
                        endGame();
                    }
                }
            }.execute();

        } else {
            endGame();
        }
    }

    private void endGame() {
        SoundManager.play(Config.SOUND_WIN);
        dispose();
        new EndCard(human, cpu, size, algo, isColorBlindMode);
    }
}