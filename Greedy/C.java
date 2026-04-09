// LC. 3891. Minimum Increase to Maximize Special Indices: https://leetcode.com/problems/minimum-increase-to-maximize-special-indices/description/

public class C{

    // Approach-1(4 ms): Alternating Peaks + Prefix-Suffix Optimization
    // TC = O(N), SC = O(N)
    /*
        1. Need non-adjacent peaks. No two adjacent peaks possible.
           Any index can be made a peak by raising it.
        2. Aim: Max.(peak non-adjacent indices) then Min.(cost/operations)
        3. Non adjacent indices -> Odd pattern(1,3,5,...) and Even pattern(2, 4, 6, ...)
        4. Case-1: Odd Length array:
           - Only ONE valid pattern gives max peaks: 1,3,5,...
           - No flexibility → direct compute
           - i = 0, n-1 are not to be used as peak idx
           - e.g [5,2,3,5,7] -> [2,3,5]  
                                    -> Odd Pattern(2,5 can be turned into special idx) -> 2 sp. idx
                                    -> Even Pattern(only 3 can be turned into special idx) -> 1 sp. idx
           - n = 2k + 1 (odd); Valid indices: 1 to (n-2) = 2k-1  → odd count
                               Maximum non-adjacent picks: ceil((2k-1)/2) = k
           - Which pattern gives k?
            -> 1, 3, 5, ..., 2k-1 → k elements
            -> 2, 4, 6, ..., 2k-2 → k-1 elements                       
           - Even + Odd Pattern mix is also not possible:
             -> Any attempt to mix creates adjacency conflict
        5. Case 2: Even length array:
           - Three patterns exist: Odd, Even and Odd + Even mix
           - e.g [2,3,4,2,1,6] -> [3,4,2,1]
                                  -> Odd pattern: (3, 2 can be turned into peaks) -> 2 idx
                                  -> Even pattern: (4,2 can be turned into peaks) -> 2 idx 
           - Both patterns give same number of peaks: odd pattern → k peaks, even pattern → k peaks
           - So we focus on minimizing cost next:
             -> odd pattern → cheap on left
             -> even pattern → cheap on right
             -> Combine them → even cheaper -> Intuition for Prefix-Suffix Optimization 
        6. Split array:
           - LEFT (odd indices) + RIGHT (even indices)
           - Take odd where it's cheap, switch once, then take even where it's cheap.
           - Every valid optimal solution looks like: [odd, odd, odd, ...] + [even, even, even, ...]
           - Only ONE switch allowed to avoid adjacency.
        7. Cost accumulate and Combine: 
           - LEFT(odd prefix) (odd indices): res1 = cost[1] + cost[3] + ...
           - RIGHT(even suffix) (even indices): suf[i] = cost[i] + cost[i+2] + cost[i+4]...
           - At some index i (odd):
                    -> LEFT = res1 (odd indices till i)
                    -> RIGHT = suf[i+3] (even indices after safe gap)
           - Why i+3?
               -> i is odd, idx at which split happens
               -> i+1 → adjacent → can't pick
               -> i+2 → odd → same parity → skip
               -> i+3 → even → next valid pick         
           - Combine them to get ans    
        8. Dry run:  nums = [3,1,4,1,5,1,6,1]
    */
    public long minIncrease1(int[] nums){
        int n = nums.length;

        // If array is odd:
        if(n % 2 != 0){
            long res = 0;
            for(int i = 1; i< n-1; i += 2){
                long target = Math.max(nums[i-1], nums[i+1]) + 1L;
                if(nums[i] < target){
                    res += (target - nums[i]);
                }
            }
            return res;
        }

        // If array is even:
        // Build suffix (even indices)
        long[] suf = new long[n + 2];
        for(int i = n-2; i >= 2; i -= 2){
            int target = Math.max(nums[i-1], nums[i+1]) + 1;
            long cost = Math.max(0L, (long) target - nums[i]);
            suf[i] = suf[i+2] + cost;
        }

        long res1 = 0;
        long ans = suf[2];
        
        // Build prefix (odd indices)
        for(int i = 1; i < n-2; i += 2){
            long target = Math.max(nums[i-1], nums[i+1]) + 1L;
            if(nums[i] < target){
                res1 += (target - nums[i]);
            }
            ans = Math.min(ans, res1 + suf[i+3]);
        }
        return ans;
    }


    // Approach-2(4 ms): Space Optimized(Rolling prefix & suffix)
    // TC = O(N), SC = O(1)
    /*

    */
     public long minIncrease2(int[] nums) {
        int n = nums.length;

        // If the array length is odd (Already O(1) Space)
        if (n % 2 != 0) {
            long res = 0;
            for (int i = 1; i < n - 1; i += 2) {
                int target = Math.max(nums[i - 1], nums[i + 1]) + 1;
                if (nums[i] < target) {
                    res += (target - nums[i]);
                }
            }
            return res;
        }

        // If the array length is even (Optimized to O(1) Space)
        long sufSum = 0;
        
        // 1. Calculate the total suffix sum for indices 2, 4, 6... n-2
        for (int i = 2; i < n - 1; i += 2) {
            int target = Math.max(nums[i - 1], nums[i + 1]) + 1;
            long cost = Math.max(0L, (long) target - nums[i]);
            sufSum += cost;
        }

        long prefixSum = 0;
        long ans = sufSum; // Initial answer assuming we only use the suffix pattern

        // 2. Iterate through odd indices 1, 3, 5... n-3
        for (int i = 1; i < n - 2; i += 2) {
            
            // Add the cost of making the current odd index a peak to the prefix sum
            int targetPrefix = Math.max(nums[i - 1], nums[i + 1]) + 1;
            long costPrefix = Math.max(0L, (long) targetPrefix - nums[i]);
            prefixSum += costPrefix;

            // Subtract the cost of the corresponding even index (i + 1) from the suffix sum
            // because we are shifting our pattern and no longer need it.
            int targetSuffix = Math.max(nums[i], nums[i + 2]) + 1;
            long costSuffix = Math.max(0L, (long) targetSuffix - nums[i + 1]);
            sufSum -= costSuffix;

            // Track the minimum cost found so far
            ans = Math.min(ans, prefixSum + sufSum);
        }

        return ans;
    }

    // Approach-3(2 ms): Best Approach (2-state DP - DP over parity)
    // TC = O(N), SC = O(1)
    /*
    
    */
    public long minIncrease3(int[] nums) {
        int len = nums.length; 

        long ans = 0;

        if(len%2 != 0 ){
            for(int i=1; i<len; i+=2){
                ans += Math.max(Math.max(nums[i-1], nums[i+1]) + 1 - nums[i], 0);
            }
        }
        else {
            long startingFirst = 0;
            long startingSecond = 0;
            for(int i=1; i<len-1 ; i+=2){
                int first = nums[i];
                int second = nums[i+1];

                int firstOp = Math.max(Math.max(nums[i-1], nums[i+1]) + 1 - nums[i], 0);
                int secondOp = Math.max(Math.max(nums[i], nums[i+2]) + 1 - nums[i+1], 0);

                startingSecond = Math.min(startingFirst, startingSecond) + secondOp;
                startingFirst += firstOp;
            }
            ans = Math.min(startingFirst, startingSecond);
        }

        return ans;
    }
    
}