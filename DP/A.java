// LC. 3830. Longest Alternating Subarray After Removing At Most One Element: https://leetcode.com/problems/longest-alternating-subarray-after-removing-at-most-one-element/description/

import java.util.Arrays;

public class A{

    /* Brute Force Approach(TLE): TC = O(n^4), SC= O(n)(array copy)
        1. Try everything:
           - Remove no element or remove one element
             - For each index k = -1 .. n-1
                - k = -1 → remove nothing, otherwise remove nums[k]
           - From the resulting array, try all subarrays
           - Check if each subarray is alternating
    */

    /* Better Approach(TLE): TC = O(n^2), SC= O(1)
        1. Instead of all subarrays:
           - Fix a left index l, Extend right r and Allow at most one violation (simulating one removal)
        2. One removal can fix only one broken alternation   
    */

    /* Better(but incomplete)-DP without removal: TC = O(N), SC = O(N)
        1. Precompute exact alternating lengths:
           - left[i] = length of longest alternating subarray ending at i
           - right[i] = length of longest alternating subarray starting at i
           - These arrays give us exact alternating chains, with no removal involved.
        2. Alternation is just sign flips of comparisons.
           - +1 → increasing (>), -1 → decreasing (<), 0 → equal
           - Alternation holds if: cmp(A[i-1], A[i-2]) == -cmp(A[i], A[i-1])
        3. Problem and Observation:
           -  It doesn’t handle removal
           -  Also Naively merging l + r after removal is wrong
           -  Removal creates a new comparison, that comparison must fit the pattern.
    */

    // Approach-1(43 ms): DP + Merging with removal
    // TC = O(N), SC= O(N)
    /*
        1. Removing one element deletes two comparisons and creates one new one.
           That new comparison must be compatible with both sides of the alternating pattern.
        2. Some patterns:
            - Alternating / Wiggle Pattern (Sign-Flip Pattern)
              a. +, -, +, -, ... or -, +, -, +, ...
              b. We reduce the array to comparisons
              c. Only the sign flip matters, not actual values
              d. Equal values (==) break the pattern
              e. Recognition: The condition is based on < and > alternating
            - Prefix–Suffix DP (Left–Right Precomputation)
              a. Compute: best result - ending at i (prefix DP) / starting at i (suffix DP)
              b. l[i] → alternating ending at i; r[i] → alternating starting at i  
            - DP with Conditional Merge (Safe Merge Pattern)
              a. When merging two DP segments:
                 - You cannot always merge, You must verify compatibility constraints
                 - Deletion creates a new relationship between neighbors.
              b. For this problem, after removing index i:
                 - A new comparison is created
                 - That comparison must:  fit the left DP’s ending pattern and the right DP’s starting pattern        
    */
    public int longestAlternating1(int[] A){
        int n = A.length;

        int[] left = new int[n];
        int[] right = new int[n];
        Arrays.fill(left, 1);
        Arrays.fill(right, 1);

        // Build left DP
        for(int i = 1; i < n; i++){
            int d = cmp(A[i], A[i-1]);
            if(d != 0){
                if(i > 1 && cmp(A[i-1], A[i-2]) == -d){
                    left[i] = left[i-1] + 1;
                } else {
                    left[i] = 2;
                }
            }
        }

        // Build right DP
        for(int i = n-2; i >= 0; i--){
            int d = cmp(A[i+1], A[i]);
            if(d != 0){
                if(i < n-2 && cmp(A[i+2], A[i+1]) == -d){
                    right[i] = right[i+1] + 1;
                } else {
                    right[i] = 2;
                }
            }
        }

        int res = 1;

        // No removal case
        for (int i = 0; i < n; i++){
            res = Math.max(res, left[i]);
        }

        // Remove one element case
        for(int i = 1; i<n-1; i++){
            int d = cmp(A[i+1], A[i-1]);
            if(d != 0){
                int L = (i > 1 && cmp(A[i-1], A[i-2]) == -d) ? left[i-1] : 1;
                int R = (i < n-2 && cmp(A[i+2], A[i+1]) == -d) ? right[i+1] : 1;
                res = Math.max(res, L + R);
            }
        }
        return res;
    }

    private int cmp(int a, int b){
        return (a > b ? 1 : 0) - (a < b ? 1 : 0);
    }

}