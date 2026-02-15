🏆 Treasure Hunt Game – Review 2

📌 Overview

Treasure Hunt is a grid-based AI game developed in Java using Swing.
In Review 2, the system was enhanced with advanced algorithm integration, Divide & Conquer logic, leaderboard sorting, and improved maze validation.

The game now demonstrates multiple algorithmic paradigms within an interactive environment.

⸻

🎮 What’s New in Review 2

Compared to Review 1, the following major improvements were implemented:

✅ 1. Dynamic Game Configuration
	•	User selects:
	•	Grid size
	•	Difficulty level
	•	AI algorithm
	•	Color blind mode
	•	Maze generation now adapts to difficulty using probability scaling.

⸻

✅ 2. Maze Connectivity Validation
	•	BFS is used to validate reachability of all gems.
	•	Unreachable gems are removed automatically.
	•	Prevents infinite or unwinnable game states.

Time Complexity:
O(n^2)

⸻

✅ 3. Divide & Conquer AI Pathfinding

Implemented in:

AIEngine.divideAndConquerPath()

Algorithm Logic
	1.	Divide → Compute midpoint between source and target
	2.	Conquer → Use BFS to solve:
	•	Source → Midpoint
	•	Midpoint → Target
	3.	Combine → Merge both paths

Recurrence:

T(n) = 2T(n/2) + O(n)

Using Master Theorem:

O(n \log n)

⸻

✅ 4. Leaderboard Sorting using Merge Sort

Implemented in:

GameStats.mergeSort()

Whenever the game ends:
	•	Score is added
	•	Merge Sort sorts leaderboard
	•	Top 5 scores are displayed in EndCard

Recurrence:

T(n) = 2T(n/2) + O(n)

Time Complexity:

O(n \log n)

Space Complexity:

O(n)

⸻

🧠 Algorithms Used

🔵 1. Breadth-First Search (BFS)
	•	Used for shortest path calculation.
	•	Time Complexity:
O(V + E) = O(n^2)
	•	Guarantees optimal path.

⸻

🔵 2. Depth-First Search (DFS)
	•	Explores deeply before backtracking.
	•	Time Complexity:
O(n^2)
	•	Does not guarantee shortest path.

⸻

🔵 3. Greedy Strategy
	•	Selects locally optimal direction using Manhattan distance.
	•	Time Complexity:
O(n)

⸻

🟣 4. Divide & Conquer (DNC)
	•	Splits pathfinding problem into two subproblems.
	•	Merges results.
	•	Demonstrates recursive problem decomposition.

⸻

🟠 5. Merge Sort
	•	Used to maintain sorted leaderboard.
	•	Recursive divide and merge approach.
	•	Ensures efficient ranking.

⸻

⚙️ System Architecture

src/
│
├── ui/
│   ├── GameUI.java
│   ├── GamePanel.java
│   └── EndCard.java
│
├── engine/
│   ├── AIEngine.java
│   └── AlgorithmType.java
│
├── model/
│   ├── Maze.java
│   ├── Player.java
│   └── CellType.java
│
├── analytics/
│   └── GameStats.java
│
├── util/
│   ├── Config.java
│   └── Difficulty.java
│
└── sound/
    └── SoundManager.java


⸻

📊 Complexity Summary

Component	Time Complexity
Maze Generation	O(n²)
Connectivity Validation	O(n²)
BFS / DFS	O(n²)
Greedy	O(n)
Divide & Conquer	O(n log n)
Merge Sort	O(n log n)
Rendering	O(n²)


⸻

🎯 Rubric Mapping

Rubric Requirement	Implementation
Improvement from Previous Version	Dynamic configuration + validation
Divide & Conquer	DNC pathfinding
Sorting	Merge Sort leaderboard
Algorithm Analysis	Complexity explanation included
Individual Contribution	AI logic, sorting, validation, UI improvements


⸻

🚀 How to Run
	1.	Clone the repository:

git clone <repo-link>

	2.	Open project in IDE.
	3.	Run:

Main.java

	4.	Select configuration options.

⸻

🎓 Learning Outcomes

This project demonstrates:
	•	Graph traversal algorithms
	•	Divide & Conquer paradigm
	•	Recursive sorting
	•	Algorithm runtime analysis
	•	Modular system design
	•	AI strategy comparison

⸻

📌 Conclusion

Review 2 successfully integrates advanced algorithmic techniques into a playable game system.

The project demonstrates practical application of:
	•	Graph algorithms
	•	Recursive divide and conquer
	•	Sorting algorithms
	•	Algorithm complexity analysis
	•	Clean software architecture
