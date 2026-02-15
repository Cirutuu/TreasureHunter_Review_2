package ui;

import analytics.GameStats;
import engine.AlgorithmType;
import java.awt.*;
import javax.swing.*;
import model.Player;
import util.Difficulty;

public class EndCard extends JFrame {

    public EndCard(Player h,
                   Player c,
                   int size,
                   AlgorithmType algo,
                   boolean isColorBlindMode) {

        setTitle("Game Over");
        setSize(420, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // ================= BACKGROUND PANEL =================
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());
        mainPanel.setBackground(new Color(20, 22, 40));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // ================= WINNER SECTION =================
        String winner =
                h.score > c.score ? "🏆 Human Wins!" :
                c.score > h.score ? "🤖 CPU Wins!" :
                "🤝 It's a Draw!";

        String message =
                h.score > c.score * 2 ? "Crushing Victory!" :
                c.score > h.score * 2 ? "CPU Dominates!" :
                "Close Match!";

        JLabel winnerLabel = new JLabel(winner, SwingConstants.CENTER);
        winnerLabel.setFont(new Font("SansSerif", Font.BOLD, 22));
        winnerLabel.setForeground(new Color(255, 215, 0));

        JLabel messageLabel = new JLabel(message, SwingConstants.CENTER);
        messageLabel.setFont(new Font("SansSerif", Font.PLAIN, 16));
        messageLabel.setForeground(Color.WHITE);

        JPanel headerPanel = new JPanel(new GridLayout(2, 1));
        headerPanel.setOpaque(false);
        headerPanel.add(winnerLabel);
        headerPanel.add(messageLabel);

        // ================= SCORE SECTION =================
        JTextArea scoreArea = new JTextArea(
                String.format(
                        "\nHuman Score : ₹%d\nCPU Score   : ₹%d\n\nHuman Moves : %d\nCPU Moves   : %d\n\nAlgorithm Used: %s\n",
                        h.score, c.score,
                        h.moves, c.moves,
                        algo
                )
        );

        scoreArea.setEditable(false);
        scoreArea.setFont(new Font("Monospaced", Font.BOLD, 14));
        scoreArea.setBackground(new Color(30, 32, 55));
        scoreArea.setForeground(Color.WHITE);
        scoreArea.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // ================= LEADERBOARD SECTION =================

        // Add current human score to leaderboard
        GameStats.addScore(h.score);

        StringBuilder leaderboardText = new StringBuilder("Top 5 Leaderboard:\n\n");

        int rank = 1;
        for (Integer score : GameStats.getTopScores()) {
            leaderboardText.append(rank++)
                           .append(".  ₹")
                           .append(score)
                           .append("\n");
        }

        JTextArea leaderboardArea = new JTextArea(leaderboardText.toString());
        leaderboardArea.setEditable(false);
        leaderboardArea.setFont(new Font("SansSerif", Font.BOLD, 14));
        leaderboardArea.setBackground(new Color(25, 28, 50));
        leaderboardArea.setForeground(new Color(180, 220, 255));
        leaderboardArea.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // ================= CENTER PANEL =================
        JPanel centerPanel = new JPanel(new GridLayout(2, 1, 0, 15));
        centerPanel.setOpaque(false);
        centerPanel.add(scoreArea);
        centerPanel.add(leaderboardArea);

        // ================= BUTTON SECTION =================
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        buttonPanel.setOpaque(false);

        JButton restart = styledButton("Restart");
        JButton mainMenu = styledButton("Main Menu");
        JButton quit = styledButton("Quit");

        restart.addActionListener(e -> {
            dispose();
            new GameUI(size, algo, Difficulty.MEDIUM, isColorBlindMode);
        });

        mainMenu.addActionListener(e -> {
            dispose();
            new MainMenu();
        });

        quit.addActionListener(e -> System.exit(0));

        buttonPanel.add(restart);
        buttonPanel.add(mainMenu);
        buttonPanel.add(quit);

        // ================= ADD TO FRAME =================
        mainPanel.add(headerPanel, BorderLayout.NORTH);
        mainPanel.add(centerPanel, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(mainPanel);

        setVisible(true);
    }

    // ================= STYLED BUTTON =================
    private JButton styledButton(String text) {

        JButton btn = new JButton(text);
        btn.setFocusPainted(false);
        btn.setFont(new Font("SansSerif", Font.BOLD, 14));
        btn.setBackground(new Color(70, 130, 255));
        btn.setForeground(Color.WHITE);
        btn.setBorder(BorderFactory.createEmptyBorder(8, 18, 8, 18));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        return btn;
    }
}