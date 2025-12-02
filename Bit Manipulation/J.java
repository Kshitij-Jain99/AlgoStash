// LC. 3444. Minimum Increments for Target Multiples in an Array: https://leetcode.com/problems/minimum-increments-for-target-multiples-in-an-array/description/

import java.util.*;

public class J {

    // Approach-1(Recursive): Bitmask DP over subsets {166 ms}
    // TC = O(t * 2^t + n * 4^t) = O(n * 4^t), where n = nums.length, t = target.length
    // SC = O(n * 2^t)
    /*
     1. finMinOperations1:
        - tries every non-empty subset for every state
        - It relies on mask | subset to avoid duplication
        - if elements in subset were already in mask, mask | subset has same bits and that path simply doesn't make progress on the mask.
    */
    private long[][] dp; //dp[i][mask] stores the minimum increments using numbers from index i onward given that mask of targets are already covered
    private long[] lcmPrecomputed; // LCM of targets for each subset index.
    private int[] numsArr;
    private int[] targetArr;
    private int numCount, targetCount;  //n and t
    private static final long INF = (long)1e15;  //large sentinel used to represent impossible/very large cost
    
    private long gcd1(long a, long b){
        while(b != 0){
            long t = a % b;
            a = b;
            b = t;
        }
        return Math.abs(a);
    }

    // safer LCM that detects overflow and returns 0 if overflow happens or x/y is 0
    private long computeLCM1(long x, long y){
        if(x == 0 || y == 0) return 0;
        long g = gcd1(x, y);
        long a = x / g;
        // check overflow: a * y > Long.MAX_VALUE  --> overflow
        if (a > Long.MAX_VALUE / y) {
            return 0; // signal invalid/overflow LCM
        }
        return a * y;
    }
    
    private void precomputeLCM1(){
        int limit = 1 << targetCount;  // No. of subsets = 2^t
        lcmPrecomputed = new long[limit];
        Arrays.fill(lcmPrecomputed, 1L); // subset 0 will remain 1 as we never use subset 0
        for(int subset = 1; subset < limit; subset++){   // subset 0 means "no targets"
            long cur = 1L;            //store the LCM
            for(int j =0; j<targetCount; j++){   //Build up LCM for this subset
                if((subset & (1 << j)) != 0){  // For each target bit j set in the subset
                    cur = computeLCM1(cur, targetArr[j]);
                    // Note: if cur becomes 0 (due to overflow or 0 target), we can keep it 0, but here target elements assumed positive.
                    if(cur == 0) break; // no need to continue, subset LCM invalid/overflow
                }
            }
            lcmPrecomputed[subset] = cur;
        }
    }

    private long findMinOperations1(int i, int mask){
        int fullMask = (1 << targetCount) - 1; // all target bits set
        if(mask == fullMask) return 0;         //if all targets already satisfied
        if(i == numCount) return INF;          // if we've used all numbers and not all targets satisfied
        if(dp[i][mask] != -1L) return dp[i][mask];

        long minOperations = INF;

        // Option-1: Skip nums[i]
        minOperations = Math.min(minOperations, findMinOperations1(i+1, mask));

        // Option-2: Try applying each non-empty subset of target to this number
        int limit = 1 << targetCount;
        for(int subset = 1; subset < limit; subset++){
            long subsetLCM = lcmPrecomputed[subset];
            if(subsetLCM == 0) continue; // skip invalid LCMs
            long curNum = numsArr[i];

            // compute next multiple of subsetLCM >= curNum
            long nextMultiple = ((curNum + subsetLCM - 1) / subsetLCM) * subsetLCM; //integer division trick to round up for ceil
            long costToMake = nextMultiple - curNum;
            long res = findMinOperations1(i+1, mask | subset);
            if(res >= INF) continue; // avoid overflow
            long total = costToMake + res;
            if(total < minOperations) minOperations = total;
        }

        dp[i][mask] = minOperations;
        return minOperations;
    }

    public int minimumIncrements1(int[] nums, int[] target) {
        // Use internal names to avoid shadowing class-level fields
        this.numsArr = Arrays.copyOf(nums, nums.length);
        this.targetArr = Arrays.copyOf(target, target.length);
        this.numCount = numsArr.length;
        this.targetCount = targetArr.length;

        if (targetCount == 0) return 0; // nothing to satisfy

        int maskSize = 1 << targetCount;
        dp = new long[numCount + 1][maskSize];
        for (int i = 0; i <= numCount; i++) {
            Arrays.fill(dp[i], -1L);
        }

        precomputeLCM1();

        long ans = findMinOperations1(0, 0);
        if (ans >= INF) return -1; // no feasible solution (adjust if you prefer another sentinel)
        return (int) ans;
    }


    //------------------------------------------------------------------------------------------------------------------------------------

    // Approach-2(Iterative): In-Place Bitmask DP with Submask Transitions(subsets with rolling mask) {20 ms}
    // TC = O(t*2^t + n*3^t) = O (n*3^t), where n = nums.length, t = target.length
    // SC = O(2^t)
    /*
      1. Precompute lcms[mask] = LCM of the targets in each subset.
      2. Maintain a single array dp[mask] = minimum increments to satisfy exactly that subset using numbers processed so far.
      3. For each num:
         - compute costs[sub] = increments to make num a multiple of lcms[sub] (for every subset). -> O(2^t).
         - update dp in-place from high mask → low mask:  -> Θ(3^t)
           for each currently reachable mask, iterate only submasks of the remaining targets and 
           update dp[mask | sub] = min(dp[mask | sub], dp[mask] + costs[sub]).
       4. Optimization made:
          - In-place (rolling) updates avoid storing all prefix DP rows; cache locality improves.
          - Precompute costs per num reduces recomputation inside inner loops.
          - Enumerating only submasks of remain cuts the inner branching dramatically versus enumerating all subsets.
              -> This is the well-known algorithmic pattern called: submask enumeration” or “iterate over all submasks of a bitmask”.
              -> This is what reduces the work from O(4^t) in DP to O(3^t).
          - Iterating masks high→low ensures you don’t reuse the same num more than once per update (so each num can be applied at most once), 
            matching semantics of your original DP.
    */
        public int minimumIncrements2(int[] nums, int[] target) {
            int m = target.length;
            int limit = 1 << m;
            
            // Precompute LCMs for all subset masks
            long[] lcms = new long[limit];
            lcms[0] = 1;
            for (int i = 1; i < limit; i++) {
                int bit = Integer.numberOfTrailingZeros(i);
                int prev = i ^ (1 << bit);
                lcms[i] = lcm2(lcms[prev], target[bit]);
            }

            // dp[mask] stores the minimum increments to satisfy the subset of targets in mask
            long[] dp = new long[limit];
            Arrays.fill(dp, -1);
            dp[0] = 0;
            
            // Reusable array for costs of current num
            long[] costs = new long[limit];

            for (int num : nums) {
                // Calculate costs to satisfy each subset 'sub' using current 'num'
                for (int sub = 1; sub < limit; sub++) {
                    long l = lcms[sub];
                    costs[sub] = (l - num % l) % l;
                }

                // Iterate masks from high to low to allow in-place updates
                for (int mask = limit - 1; mask >= 0; mask--) {  //Rolling / In-place DP over masks
                    if (dp[mask] == -1) continue;
                    
                    long currentVal = dp[mask];
                    int remain = (limit - 1) ^ mask;
                    
                    // Iterate over all non-empty subsets of the remaining targets
                    for (int sub = remain; sub > 0; sub = (sub - 1) & remain) {  //submask enumeration pattern
                        int nextMask = mask | sub;
                        long nextVal = currentVal + costs[sub];
                        
                        if (dp[nextMask] == -1 || nextVal < dp[nextMask]) {
                            dp[nextMask] = nextVal;
                        }
                    }
                }
            }

            return (int) dp[limit - 1];
        }

        private long gcd2(long a, long b) {
            while (b != 0) {
                long t = b;
                b = a % b;
                a = t;
            }
            return a;
        }

        private long lcm2(long a, long b) {
            if (a == 0 || b == 0) return 0;
            return (a / gcd2(a, b)) * b;
        }
    }

