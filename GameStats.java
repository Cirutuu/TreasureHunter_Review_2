package analytics;

import java.util.*;

public class GameStats {

    // Stores all game scores
    private static final List<Integer> leaderboard = new ArrayList<>();

    // ================= ADD SCORE =================
    public static void addScore(int score) {

        if (score < 0) return;   // Safety check

        leaderboard.add(score);

        if (leaderboard.size() > 1) {
            mergeSort(leaderboard, 0, leaderboard.size() - 1);
        }
    }

    // ================= GET TOP SCORES =================
    public static List<Integer> getTopScores() {

        int end = Math.min(5, leaderboard.size());

        // Return a copy (not the original subList)
        return new ArrayList<>(leaderboard.subList(0, end));
    }

    // ================= CLEAR LEADERBOARD (Optional) =================
    public static void resetLeaderboard() {
        leaderboard.clear();
    }

    // ================= MERGE SORT =================
    private static void mergeSort(List<Integer> arr, int left, int right) {

        if (left >= right) return;

        int mid = left + (right - left) / 2;

        // Divide
        mergeSort(arr, left, mid);
        mergeSort(arr, mid + 1, right);

        // Conquer (Merge)
        merge(arr, left, mid, right);
    }

    private static void merge(List<Integer> arr, int left, int mid, int right) {

        List<Integer> temp = new ArrayList<>();

        int i = left;
        int j = mid + 1;

        // Merge in DESCENDING order (highest score first)
        while (i <= mid && j <= right) {

            if (arr.get(i) >= arr.get(j)) {
                temp.add(arr.get(i));
                i++;
            } else {
                temp.add(arr.get(j));
                j++;
            }
        }

        // Remaining elements
        while (i <= mid) {
            temp.add(arr.get(i));
            i++;
        }

        while (j <= right) {
            temp.add(arr.get(j));
            j++;
        }

        // Copy back into original list
        for (int k = 0; k < temp.size(); k++) {
            arr.set(left + k, temp.get(k));
        }
    }
}