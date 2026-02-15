package ui;

import engine.AlgorithmType;
import javax.swing.*;
import util.Difficulty;

public class MainMenu extends JFrame {
    private boolean isColorBlindMode = false;

    public MainMenu() {
        setTitle("Treasure Hunter");
        setSize(320, 400); // Increased height to fit all options comfortably
        setLocationRelativeTo(null);
        setLayout(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        // Grid Size
        JLabel gridLabel = new JLabel("Grid Size:");
        gridLabel.setBounds(20, 40, 100, 25);
        JComboBox<Integer> grid = new JComboBox<>(new Integer[]{10, 15, 20});
        grid.setBounds(130, 40, 140, 25);

        // AI Algorithm
        JLabel algoLabel = new JLabel("AI Algorithm:");
        algoLabel.setBounds(20, 80, 100, 25);
        JComboBox<AlgorithmType> algo = new JComboBox<>(AlgorithmType.values());
        algo.setBounds(130, 80, 140, 25);

        // Difficulty
        JLabel diffLabel = new JLabel("Difficulty:");
        diffLabel.setBounds(20, 120, 100, 25);
        JComboBox<Difficulty> difficulty = new JComboBox<>(Difficulty.values());
        difficulty.setSelectedItem(Difficulty.MEDIUM); // Default to Medium
        difficulty.setBounds(130, 120, 140, 25);


        // Color-Blind Mode
        JCheckBox colorBlindToggle = new JCheckBox("Color-Blind Mode", false);
        colorBlindToggle.setBounds(130, 210, 160, 25);

        // Start Button
        JButton start = new JButton("START");
        start.setBounds(90, 280, 140, 40);

        // Add components
        add(gridLabel);
        add(grid);
        add(algoLabel);
        add(algo);
        add(diffLabel);
        add(difficulty);
        add(colorBlindToggle);
        add(start);


        colorBlindToggle.addActionListener(e -> isColorBlindMode = colorBlindToggle.isSelected());

        start.addActionListener(e -> {
            int selectedSize = (int) grid.getSelectedItem();
            AlgorithmType selectedAlgo = (AlgorithmType) algo.getSelectedItem();
            Difficulty selectedDifficulty = (Difficulty) difficulty.getSelectedItem();

            dispose(); // Close menu
            new GameUI(selectedSize, selectedAlgo, selectedDifficulty, isColorBlindMode);
        });

        setVisible(true);
    }
}