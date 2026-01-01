// LC.3171. Find Subarray With Bitwise OR Closest to K: https://leetcode.com/problems/find-subarray-with-bitwise-or-closest-to-k/description/
import java.util.*;

public class O{
//---------------------------------------------------------------------------------------------------------------------------------------------------------
// Find Subarray With Bitwise AND Closest to K:

// Approach-1: Sliding Window + BitFreq Array
// TC = O(n*32), SC = O(32) 
    /*
      1. Why Sliding Window? 
         - Because AND decreases as the window grows, and we can adjust the window
      2. Each element enters the window once and exits the window once
    */ 
    private static final int BITS_AND = 32;

    // Adds or removes a number’s bits from the current window
    // Also tracks how many numbers in the window have each bit set
    private void updateBitFrequency(int value, int delta, int[] bitFreq){
        int bitIdx = 0;
        while(value > 0){
            if((value & 1) == 1){
                bitFreq[bitIdx] += delta;  // delta = +1 (add) or -1 (remove)
            }
            value >>= 1;
            bitIdx++;
        }
    }

    // Rebuilds the bitwise AND of the current window
    private int computeWindowAnd(int[] bitFreq, int windowSize){
        int andValue = 0;
        for(int bit = 0; bit < BITS_AND; bit++){
            if(bitFreq[bit] == windowSize){
                andValue |= (1 << bit);
            }
        }
        return andValue;
    }
    
    public int minimumDifferenceAnd1(int[] nums, int k){
        int[] bitFreq = new int[BITS_AND];
        int n = nums.length;

        int left = 0, right = 0;
        int windowAnd = nums[0];
        int res = Integer.MAX_VALUE;

        while(right < n){
            windowAnd &= nums[right];
            updateBitFrequency(nums[right], +1, bitFreq);
            res = Math.min(res, Math.abs(windowAnd - k));

            if (windowAnd < k) { //Window expand
                right++;
            }
            else if (windowAnd == k){ //Best possible answer
                return 0;
            }
            else {   //windowAnd > k => Window Shrink
                while(left <= right && windowAnd < k){
                    updateBitFrequency(nums[left], -1, bitFreq);
                    left++;

                    windowAnd = computeWindowAnd(bitFreq, right-left+1);
                    res = Math.min(res, Math.abs(windowAnd - k));
                }
                right++;
            }
        }
        return res;

    }

    // Approach-2: Using HashSet, AND property(32-Unique Values/Frontier Set)
    // TC = O(32*n), SC = O(32)
    /*
        1. The number of distinct AND values ending at any index is very small.
           At most 32 values (because each bit can flip from 1 → 0 only once)
        2. So, Maintain a compressed list of unique ANDs and update it as we move through the array
    */
    public int minumunDifferenceAnd2(int[] nums, int k){
        int res = Integer.MAX_VALUE;
        Set<Integer> prev = new HashSet<>();  // all distinct AND values for subarrays ending at i-1

        for(int num: nums){
            Set<Integer> curr = new HashSet<>();
            curr.add(num);

            for(int val : prev){
                curr.add(val & num);
            }

            for(int andValue : curr){
                res = Math.min(res, Math.abs(andValue - k));
                if(res == 0) return 0;
            }
            prev = curr;
        }
        return res;
    }

    // Approach-3: God Mode (AND Variant)
    // TC = O(N * 30), SC = O(1)
    /*
        1. In-Place Update: A[j] becomes the AND of subarray nums[j...i].
        2. Reverse Propagation: We update from i-1 backwards to 0.
        3. Pruning Logic: (A[j] & A[i]) != A[j]
           - For AND, values only get smaller (lose 1s).
           - If ANDing A[j] with A[i] doesn't change A[j], it means A[j]
             didn't lose any bits.
           - Since A[j-1] is a subset of A[j] (it has even fewer bits), 
             it won't lose bits either. We can stop early.
    */
    public int minumunDifferenceAnd3(int[] A, int k) { // distinct function name
        int res = Integer.MAX_VALUE;
        
        for (int i = 0; i < A.length; i++) {
            // Check single element subarray
            res = Math.min(res, Math.abs(A[i] - k));
            
            // Reverse propagation
            // Stop if: Index < 0 OR The value stops changing
            for (int j = i - 1; j >= 0 && (A[j] & A[i]) != A[j]; j--) {
                A[j] &= A[i]; // Update history with current value
                res = Math.min(res, Math.abs(A[j] - k));
            }
        }
        return res;
    }

//---------------------------------------------------------------------------------------------------------------
//---------------------------------------------------------------------------------------------------------------
// Find Subarray With Bitwise OR Closest to K:
// Approach-1: Sliding Window + BitFreq Array
// TC = O(N*32), SC = O(32)
    private static final int BITS = 32;

    // Update bit frequencies (delta = +1 add, -1 remove)
    private void updateBits(int[] bits, int num, int delta) {
        for (int i = 0; i < BITS; i++) {
            bits[i] += delta * ((num >> i) & 1);
        }
    }

    // Recompute OR from bit frequencies
    private int computeOR(int[] bits) {
        int res = 0;
        for (int i = 0; i < BITS; i++) {
            if (bits[i] > 0) {
                res |= (1 << i);
            }
        }
        return res;
    }

    public int minimumDifference1(int[] nums, int k) {
        int[] bits = new int[BITS];
        int left = 0;
        int ans = Integer.MAX_VALUE;

        for (int right = 0; right < nums.length; right++) {
            // 1. Expand window (add nums[right])
            updateBits(bits, nums[right], 1);
            
            // 2. Compute current OR
            int currOR = computeOR(bits);
            
            // 3. Update minimum difference for the current window
            ans = Math.min(ans, Math.abs(currOR - k));

            // 4. Shrink window ONLY while currOR > k
            //    We want to remove bits to see if we can get closer to k.
            //    If currOR <= k, removing bits will only make it smaller (further from k), so we stop.
            while (left <= right && currOR > k) {
                updateBits(bits, nums[left], -1);
                left++;
                
                // Recompute after removal
                currOR = computeOR(bits);
                
                // Only update ans if the window is not empty
                if (left <= right) {
                    ans = Math.min(ans, Math.abs(currOR - k));
                }
            }
        }

        return ans;
    }

    //Approach-2: Using ArrayList, AND property(32-Unique Values/Frontier Set)
    //TC = O(30.n), SC = O(32)
    /*
        1. HashSet Version is Slow due to Object Creation, Hashing Overhead.
        2. Fix:
           - Use an ArrayList (or simple array) and perform manual deduplication.
           - Since the values are always sorted (monotonic), you can just check if (newVal != lastVal) to remove duplicates.
    */
    public int minimumDifference2(int[] nums, int k) {
        // We use Lists instead of Sets for speed. 
        // The size of these lists is bounded by ~32 (integer bits).
        List<Integer> prev = new ArrayList<>();
        int minDiff = Integer.MAX_VALUE;

        for (int num : nums) {
            List<Integer> curr = new ArrayList<>();
            
            // 1. Start a new subarray with just the current number
            curr.add(num);
            minDiff = Math.min(minDiff, Math.abs(num - k));

            // 2. Extend previous subarrays: (prevVal | num)
            for (int val : prev) {
                int newVal = val | num;
                
                // PERFORMANCE OPTIMIZATION: 
                // Since OR is monotonic, duplicates will be adjacent.
                // We only add if it's different from the last inserted value.
                if (curr.get(curr.size() - 1) != newVal) {
                    curr.add(newVal);
                    minDiff = Math.min(minDiff, Math.abs(newVal - k));
                }
            }
            
            // 3. Move forward
            prev = curr;
        }

        return minDiff;
    }

    // Approach-3: Reverse Propagation with In-Place Modification
    // TC = O(n*30), SC = O(1)
    /*
        1. The Core Concept: "The Wave":
           - Normally, to find the OR of a subarray nums[j...i], you would iterate from j to i. This algorithm does the reverse.
           - As it moves i forward, it sends a "wave" backward to update all previous start positions.
           - A[i]: Always represents the single element subarray [i...i]
             A[j] (after update): Represents the OR sum of the subarray [j...i]
         2. The "Aggressive Pruning" Logic:
            - (A[j] | A[i]) != A[j] -> "Does adding the current number A[i] to the existing subarray A[j] actually change anything?"
    */
    public int minimumDifference3(int[] A, int k) {
        int res = Integer.MAX_VALUE;
        for (int i = 0; i < A.length; i++) {
            res = Math.min(res, Math.abs(A[i] - k));
            for (int j = i - 1; j >= 0 && (A[j] | A[i]) != A[j]; j--) {
                A[j] |= A[i];
                res = Math.min(res, Math.abs(A[j] - k));
            }
        }
        return res;
    }
}

