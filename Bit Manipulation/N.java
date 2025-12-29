// LC. 3209. Number of Subarrays With AND Value of K: https://leetcode.com/problems/number-of-subarrays-with-and-value-of-k/description/

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class N {
    // Approach-1(180ms): Subarrays AND DP 
    // Time Complexity: O(32 * n); SC = O(32*2)
    public long countSubarrays1(int[] nums, int k){
        long ans = 0;
        Map<Integer, Integer> prevAND = new HashMap<>();       // Storing AND values ending at previous index and their counts

        for(int num: nums){
            Map<Integer, Integer> currAND = new HashMap<>();  // Storing AND values ending at current index and their counts
            
            // Extend previous subarrays
            for(Map.Entry<Integer, Integer> entry : prevAND.entrySet()){
                int andVal = entry.getKey();
                int cnt = entry.getValue();

                int newAnd = andVal & num;
                currAND.put(newAnd, currAND.getOrDefault(newAnd, 0) + cnt);
            }
             currAND.put(num, currAND.getOrDefault(num, 0) + 1);    // Single element subarray [num]

              // Add counts where AND == k
              if (currAND.containsKey(k)) ans += currAND.get(k);
              prevAND = currAND;  // Move to next index
        }
             return ans;
        }

        
    // Approach-2(76ms): Optimized Subarray AND DP
    // Time Complexity: O(32 * n); SC = O(32)
        /*
        Why List + Merge Is Better?
        1. AND Values Appear in Sorted Order (Implicitly)
           - AND values monotonically decrease as subarrays extend
           - While iterating prev, new AND values: Appear in non-increasing order
           - This allows: Adjacent identical AND values and Easy merging without hashing
        2. Eliminates HashMap Overhead -> Faster constant factors and Lower memory usage
        3. Automatic Deduplication (State Compression)
           - The code block ensures: 
             a. Only distinct AND values are stored
             b. Counts are merged immediately
             c. No extra pass required
           - Equivalent to: map[newAnd] += count, but cheaper
        */
    public long countSubarrays2(int[] nums, int k) {
        long ans = 0;

        // Holds pairs: {AND_value, count_of_subarrays_ending_here_with_this_AND}
        List<int[]> prev = new ArrayList<>();

        for (int x : nums) {
            List<int[]> curr = new ArrayList<>();

            // Add subarray of only [x]
            curr.add(new int[]{x, 1});

            // Extend all previous subarrays
            for (int[] p : prev) {
                int newAnd = p[0] & x;

                // State Compression
                if (curr.size() > 0 && curr.get(curr.size() - 1)[0] == newAnd) {
                    // Merge with last entry
                    curr.get(curr.size() - 1)[1] += p[1];
                } else {
                    curr.add(new int[]{newAnd, p[1]});
                }
            }

            // Count how many AND == k
            for (int[] c : curr) {
                if (c[0] == k)
                    ans += c[1];
            }

            prev = curr;
        }

        return ans;
    }

    // Approach-3(91ms): Sliding Window with Bitwise AND using Bit-Frequency Tracking
    // Time Complexity: O(32 * n); SC = O(32)
    /*
     1. This is not segment tree based approach.
        - This problem deals with range AND, but Segment trees are commonly used for range AND queries
        - Segment Tree answers arbitrary range queries but this approach only maintains a dynamic contiguous window
    */
    public long countSubarrays3(int[] nums, int k) {
        int n = nums.length;
        long ans = 0;

        int low = 0;

        for (int high = 0; high < n; high++) {
            // Split segments where AND can never become k
            if ((nums[high] & k) != k) {
                ans += countSegment(nums, low, high - 1, k);
                low = high + 1;
            }
        }
        ans += countSegment(nums, low, n - 1, k);

        return ans;
    }

    private long countSegment(int[] nums, int L, int R, int k) {
        if (L > R) return 0;

        long res = 0;

        int[] bitCnt = new int[32];
        int i = L;
        int currAND = ~0; // all bits set

        for (int j = L; j <= R; j++) {
            // add nums[j]
            currAND &= nums[j];
            addBits(bitCnt, nums[j]);

            // shrink window while AND == k
            while (i <= j && currAND == k) {
                res += (R - j + 1);

                removeBits(bitCnt, nums[i]);
                i++;
                currAND = buildAND(bitCnt, j - i + 1);
            }
        }
        return res;
    }

    private void addBits(int[] bitCnt, int x) {
        for (int b = 0; b < 32; b++) {
            if ((x & (1 << b)) != 0) bitCnt[b]++;
        }
    }

    private void removeBits(int[] bitCnt, int x) {
        for (int b = 0; b < 32; b++) {
            if ((x & (1 << b)) != 0) bitCnt[b]--;
        }
    }

    private int buildAND(int[] bitCnt, int len) {
        int val = 0;
        for (int b = 0; b < 32; b++) {
            if (bitCnt[b] == len) val |= (1 << b);
        }
        return val;
    }
}
