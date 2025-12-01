//3116. Kth Smallest Amount With Single Denomination Combination: https://leetcode.com/problems/kth-smallest-amount-with-single-denomination-combination/description/
import java.util.*;
public class F {
        
        //Approach-1: Binary Search on Answer Space + Inclusion–Exclusion Principle(Bit masking)
        //TC = O(n . 2^n . logM); SC = O(1)
        public long findKthSmallest1(List<Integer> coins, int k){   //Binary Search: LogM values, per mid value: n.2^n
            long left = 1;
            long right = 25L * 2 * (long) 1e9 + 25;
            long ans = 0;

            while(left <= right){
                long mid = (left + right)/2;
                long cnt = cntLessThanEqualToX1(coins, mid);

                if(cnt < k) left = mid + 1;
                else{
                    ans = mid;
                    right = mid - 1;
                }
            }
            return ans;
        }
        
           // Function to count numbers <= x that are divisible by any of the given coins
        private long cntLessThanEqualToX1(List<Integer> coins, long x){
            int n = coins.size();
            int allOnes = (1 << n) -1;
            long cnt = 0;

            for(int mask = 1; mask <= allOnes; mask++){
                long setBitsLcm = 1;
                for(int j = 0; j < n; j++){
                    if((mask & (1 << j)) != 0){
                        setBitsLcm = lcm1(setBitsLcm, coins.get(j));
                        if(setBitsLcm > x || setBitsLcm == 0) break;
                    }
                }
                int bits = Integer.bitCount(mask);
                
                if((bits & 1) == 1) cnt += x/setBitsLcm;   //odd subset LCM
                else cnt -= x/setBitsLcm;                  //even subset LCM
            }             
            return cnt;
        }

        // Combined GCD + LCM helper
        private long lcm1(long a, long b){
            if(a == 0 || b == 0) return 0;
            long x = a, y = b;

            //Compute GCD iteratively (Euclidean algorithm)
            while(y != 0){
                long temp = y;
                y = x%y;
                x = temp;
            }
            long gcd = Math.abs(x);

            //LCM calculation: LCM(a, b) = (a / GCD(a, b)) * b
            return Math.abs((a/gcd) * b);
        } 
        
        
        //Approach-2: Faster(n ≤ 15)
        //TC = O(2^n . logM + 2^n); SC = O(2^n)
        /*
         1. LCM Precomputation Instead of On-the-Fly Calculation
            - Previously we recomputes the LCM for each subset, every time binary search tests a new mid value.
            - If binary search runs ~60 iterations and you have 10 coins,
               then you compute LCM for 1023 subsets × 60 times = ~60,000 LCM operations — each involving multiple GCDs.
            - Now precomputes all LCMs once (for each subset) before the binary search.
            - O(2ⁿ) instead of O(2ⁿ × logM) for LCMs.
         2. Redundant Coin Removal Optimization
            - If coins = [2, 4, 6], then 4 and 6 are multiples of 2, and don’t change the set of possible multiples
            - So we can just keep [2] — because any multiple of 4 or 6 is already a multiple of 2.
            - Fewer denominations ⇒ fewer subsets ⇒ smaller 2ⁿ factor
         3. Efficient LCM Generation Using Bitmask Decomposition
            - DP-like subset composition to build the LCM of a new subset from smaller ones:
              a. Each subset can be split into two smaller disjoint parts.
              b. Since their LCMs are already known, it computes the new LCM using those.
            - This avoids recomputing multiple GCDs for each subset.
         4. Tighter Binary Search Range:
            - The smallest possible k-th number is k (if smallest coin = 1).
            - The largest possible is k * minCoin (since each step must be at least minCoin apart).
         5 . Small Constant-Factor Wins:
             - Bitwise operations (>>>, & -x) → faster than arithmetic.
             - Array-based processing (int[]) → faster than List<Integer>.
             - Local variable caching (no boxing/unboxing).
             - Single-pass sort and filter instead of repeated checks.
         */
        public long findKthSmallest2(int[] coinDenominations, int k) {
            // Sort coin denominations for easier processing
            Arrays.sort(coinDenominations);
            int effectiveCount = coinDenominations.length;

            // Remove redundant coin denominations
            for (int i = 0; i < coinDenominations.length - 1; i++) {
                if (coinDenominations[i] == Integer.MAX_VALUE) continue;
                for (int j = i + 1; j < coinDenominations.length; j++) {
                    if (coinDenominations[j] != Integer.MAX_VALUE && coinDenominations[j] % coinDenominations[i] == 0) {
                        coinDenominations[j] = Integer.MAX_VALUE;
                        effectiveCount--;
                    }
                }
            }
            Arrays.sort(coinDenominations);
            coinDenominations = Arrays.copyOf(coinDenominations, effectiveCount);

            // Binary search bounds
            long lowerBound = k;
            long upperBound = (long) k * coinDenominations[0];
            
            // Calculate least common multiples (LCMs) for subsets of coin denominations
            long[] lcmArray = new long[1 << coinDenominations.length];
            lcmArray[0] = 1;
            for (int i = 0; i < coinDenominations.length; i++) {
                lcmArray[1 << i] = coinDenominations[i];
            }
            computeLCMs2(coinDenominations, lcmArray);

            // Perform binary search to find the k-th smallest amount
            while (lowerBound < upperBound) {
                long mid = (lowerBound + upperBound) >>> 1;
                if (countLCMsLessThanOrEqual2(lcmArray, mid) >= k) {
                    upperBound = mid;
                } else {
                    lowerBound = mid + 1;
                }
            }
            return lowerBound;
    }

        private void computeLCMs2(int[] coinDenominations, long[] lcmArray) {
            for (int subset = 1; subset < lcmArray.length; subset++) {
                int bitmaskA = subset & -subset;
                int bitmaskB = subset - bitmaskA;
                lcmArray[subset] = lcmArray[bitmaskA] / gcd2(lcmArray[bitmaskA], lcmArray[bitmaskB]) * lcmArray[bitmaskB];
            }
        }

        private long countLCMsLessThanOrEqual2(long[] lcmArray, long value) {
            long count = 0;
            for (int subset = 1; subset < lcmArray.length; subset++) {
                if (lcmArray[subset] <= value) {
                    if (Integer.bitCount(subset) % 2 == 1) {
                        count += value / lcmArray[subset];
                    } else {
                        count -= value / lcmArray[subset];
                    }
                }
            }
            return count;
        }

        private long gcd2(long a, long b) {
            while (b > 0) {
                long temp = b;
                b = a % b;
                a = temp;
            }
            return a;
        }
    }
