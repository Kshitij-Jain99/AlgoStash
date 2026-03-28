// LC. 3877. Minimum Removals to Achieve Target XOR: https://leetcode.com/problems/minimum-removals-to-achieve-target-xor/description/
import java.util.*;

public class S {
    
    // Approach-1(765 ms): Meet in the Middle
    // TC = O((N/2). 2^(N/2) * 2), SC = O(2^(N/2)) {Hashmap mainly}
    /*
    1. It is a specialized optimization technique used when a brute-force approach is too slow
       for the full input size N, but perfectly fine for N/2.
    2. By dividing the array in half, generating all possibilities for each half, and then "meeting in the middle" to combine
       them, we drastically reduce the computation time.     
    */
    public int minRemovals1(int[] nums, int target) {
        int n = nums.length;
        int mid = n/2;

        // Split arrays:
        int[] left = Arrays.copyOfRange(nums, 0, mid);
        int[] right = Arrays.copyOfRange(nums, mid, n);

        // Map: xor -> max subset size
        Map<Integer, Integer> mpp = new HashMap<>();

        // Process left half
        int lSize = left.length;
        for(int mask = 0; mask < (1 << lSize); mask++){  // mask < 2^lSize
            int xor = 0, count = 0;
            for(int i = 0; i < lSize; i++){
                if((mask & (1 << i)) != 0){
                    xor ^= left[i];
                    count++;
                }
            }
            mpp.put(xor, Math.max(mpp.getOrDefault(xor, 0), count));
        }

        int maxKeep = -1;

        // Process right half
        int rSize = right.length;
        for(int mask = 0; mask < (1 << rSize); mask++){
            int xor = 0, count = 0;
            for(int i = 0; i < rSize; i++){
                if((mask & (1 << i)) != 0){
                    xor ^= right[i];
                    count++;
                }
            }
            
            int needed = target ^ xor;
            if(mpp.containsKey(needed)){
                maxKeep = Math.max(maxKeep, count + mpp.get(needed));
            }
        }

        if(maxKeep == -1) return -1;

        return n - maxKeep;
    }


    // Appraoch-2(21 ms): Top Down DP on XOR States/Subset XOR DP
    // TC = O(n * 2^k), SC = O(n * 2^k); (k is number of bits)
    /*
    1. For each element -> Pick or Not pick
    2. solve : dp[idx][target]
        -> Minimum removals needed from nums[0..idx] so that XOR = target
        -> If no element left, XOR = 0 valid else impossible.
    3.  maxi:
        -> Compute max. possible XOR value.
        -> If it is 0, all elements and target = 0
        -> Convert maxi -> 2^k - 1, to cover all possible XOR states 
    */
    int solve(int idx, int target, int[] nums, int[][] dp){
        if(idx < 0) return target == 0 ? 0 : Integer.MAX_VALUE;
        if(dp[idx][target] != -1) return dp[idx][target];

        int pick = solve(idx -1, target ^ nums[idx], nums, dp);

        int notPick = solve(idx - 1, target, nums, dp);
        if(notPick != Integer.MAX_VALUE) notPick++;

        return dp[idx][target] = Math.min(pick, notPick);

    }

    public int minRemovals2(int[] nums, int target){
        int n = nums.length;

        int maxi = target;
        for(int x: nums) maxi |= x;

        if(maxi == 0) {
            return target == 0 ? 0 : -1;
        }

        maxi = (Integer.highestOneBit(maxi) << 1) - 1;

        int[][] dp = new int[n][maxi + 1];
        for(int[] it: dp) Arrays.fill(it, -1);

        int ans = solve(n-1, target, nums, dp);
        return ans == Integer.MAX_VALUE ? -1 : ans;
    }

}
