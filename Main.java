import javax.swing.*;
import engine.AlgorithmType;
import ui.GameUI;
import util.Difficulty;

public class Main {

    public static void main(String[] args) {

        SwingUtilities.invokeLater(() -> {

            // ===== GRID SIZE INPUT =====
            String sizeInput = JOptionPane.showInputDialog(
                    null,
                    "Enter Grid Size (e.g., 10, 15, 20):",
                    "Game Setup",
                    JOptionPane.QUESTION_MESSAGE
            );

            if (sizeInput == null) return;

            int size;
            try {
                size = Integer.parseInt(sizeInput);
                if (size < 5) size = 10;
            } catch (Exception e) {
                size = 10;
            }

            // ===== DIFFICULTY SELECTION =====
            Difficulty difficulty = (Difficulty) JOptionPane.showInputDialog(
                    null,
                    "Select Difficulty:",
                    "Game Setup",
                    JOptionPane.QUESTION_MESSAGE,
                    null,
                    Difficulty.values(),
                    Difficulty.MEDIUM
            );

            if (difficulty == null) difficulty = Difficulty.MEDIUM;

            // ===== ALGORITHM SELECTION =====
            AlgorithmType algo = (AlgorithmType) JOptionPane.showInputDialog(
                    null,
                    "Select AI Algorithm:",
                    "Game Setup",
                    JOptionPane.QUESTION_MESSAGE,
                    null,
                    AlgorithmType.values(),
                    AlgorithmType.BFS
            );

            if (algo == null) algo = AlgorithmType.BFS;

            // ===== COLOR BLIND MODE =====
            int cb = JOptionPane.showConfirmDialog(
                    null,
                    "Enable Color Blind Mode?",
                    "Accessibility",
                    JOptionPane.YES_NO_OPTION
            );

            boolean isColorBlind = (cb == JOptionPane.YES_OPTION);

            // ===== START GAME =====
            new GameUI(size, algo, difficulty, isColorBlind);
        });
    }
}