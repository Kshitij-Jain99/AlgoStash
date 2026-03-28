// LC. 3825 Longest Strictly Increasing Subsequence With Non-Zero Bitwise AND: https://leetcode.com/problems/longest-strictly-increasing-subsequence-with-non-zero-bitwise-and/description/

import java.util.*;

public class R {

    // Approach-1(1084 ms): Greedy LIS with Patience Sorting + Bitwise Decomposition
    // TC = O(32 * n.log(n)), SC = O(n)
    /*
        1. Longest strictly increasing subsequence whose bitwise AND is non-zero
           - Two independent constraints:
             a. Order + increasing → LIS variant
             b. AND ≠ 0 → some bitwise property 
        2. What does it mean for AND to be non-zero?
           -  If There exists at least one bit position where all numbers in the subsequence have that bit set.
           - So, “Pick one bit” and  “Use only numbers that have this bit”
           - Now the problem splits into 32 independent subproblems.
        3. For a fixed bit i:
           - Filter numbers where bit i is set
           - On that filtered list → find LIS
           - The best answer is the maximum LIS across all bits
        4. Core LIS Mechanism:
           - dp[k] = minimum possible tail value of an increasing subsequence of length k+1
           -   dp = [2, 5, 9] means:
               - length 1 subsequence ending at 2
               - length 2 subsequence ending at 5
               - length 3 subsequence ending at 9
        5. Take maximum across all bits         

     */
    public int longestSubsequence1(int[] nums) {
        int result = 0;                                          // store global best LIS length
        var dp = new ArrayList<Integer>();

        for (int i = 0; i < 32; i++) {                              // Each iteration assumes: this bit must survive in AND
            dp.clear();                                          // reset LIS tracker
            int mask = 1 << i;                                   // bitmask to filter numbers that have this bit set
            for (int n : nums) {
                if ((n & mask) == 0) {
                    continue;                    // Filtering step: only consider numbers with this bit set
                }
                int pos = Collections.binarySearch(dp, n);      // This finds: First position where dp[pos] >= n
                if (pos >= 0) {
                    continue;                          // skip as LIS is strictly increasing

                }
                pos = ~pos;                                     // If not found, Java returns -(insertionPoint + 1), ~pos converts it back to insertion index

                if (pos == dp.size()) {
                    dp.add(n);                 // Case-1: Extend LIS → append
                } else {
                    dp.set(pos, n);                            // Case-2: Improve tail → replace

                }
            }
            result = Math.max(result, dp.size());
        }
        return result;
    }

    // 
}
