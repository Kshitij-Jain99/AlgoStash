// LC. 3806. Maximum Bitwise AND After Increment Operations: https://leetcode.com/problems/maximum-bitwise-and-after-increment-operations/description/
import java.util.Arrays;
public class P{

    // Approach-1(74ms): Greedy on bits
    // TC = O(31 * N.logN), SC = O(n)
    /*
    1. Aim: Maximize the bitwise AND of m numbers after at most k increments.
    2. a. Build the AND value bit by bit from MSB → LSB (Higher bits contribute more value)
       b. For each bit, check if it’s possible to make at least m numbers have that bit = 1 using ≤ k operations
       c. If yes → keep the bit
    3. Final cost formula: ops[i] = (target & mask) - (x & mask);
       - It calculates: How much we need to increment x so that all missing bits ≤ j-1 match target
       - To fix the highest missing bit, lower bits must align first
       - This formula gives the minimum y ≥ x satisfying (y & target) == target
    4. Greedy approach is used twice:
       a. Bit-by-bit construction of AND value (MSB → LSB decision making)
       b. Selecting m numbers with the smallest operation costs (Locally optimal → globally optimal)
    */

    public int maximumAND(int[] nums, int k, int m){
        // Find maximum possible bit width
        int mx = 0;
        for(int x : nums) mx = Math.max(mx, x);
        int maxWidth = 32 - Integer.numberOfLeadingZeros(mx + k); // Finds how many bits are needed to represent mx + k

        int[] ops = new int[nums.length];     // store cost to make nums[i] satisfy the current target AND
        int ans = 0;

        // Greedy bit-by-bit construction
        for(int bit = maxWidth - 1; bit >= 0; bit--){
            int target = ans | (1 << bit);

            for(int i =0; i<nums.length; i++){
                // Cost computation
                int x = nums[i];
                int j = 32 - Integer.numberOfLeadingZeros(target & ~x);  // target & ~x: bits that target wants = 1, but x currently has = 0
                
                // Find highest missing bit
                // j-1 is the highest bit where target is 1 and x is 0
                int mask = (1 << j) - 1;  // This mask isolates all bits below the highest missing bit.
                ops[i] = (target & mask) - (x & mask); // final cost formula
            }

            // Greedy: pick the smallest m operation count
            Arrays.sort(ops);
            long sum = 0;
            for(int i =0; i<m; i++) sum += ops[i];
            if(sum <= k) ans = target;  // This bit of ans can be set to 1
        }
        return ans;
    }
}