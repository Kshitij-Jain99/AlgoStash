// LC. 3821. Find Nth Smallest Integer With K One Bits: https://leetcode.com/problems/find-nth-smallest-integer-with-k-one-bits/description/

public class Q{

    // Approach-1: Combinatorial + Greedy Bit-by-Bit Construction
    // TC = O (50 * 50), SC = O(50 * 50)
    /*
    1. We want the n-th smallest positive integer whose binary representation contains exactly k ones.
    2. There are exactly C(i, k) numbers that can be formed using only the lower i bits and having exactly k ones.
    3. ans < 2^50, so max bits = 50. So  decide each bit from most significant to least significant.
       This is like constructing the number in lexicographically smallest order.
    4. Combinatorial Precomputation (Pascal Triangle):
       - We compute comb[i][j] = C(i, j) = number of ways to place j ones in i bit positions.
       - comb[i][0] = 1
       - comb[i][j] = comb[i-1][j-1] + comb[i-1][j]
       - This is Pascal’s identity: C(i,j) = C(i−1,j−1) + C(i−1,j)
    5. Greedy Bit-by-Bit Construction:
       - At bit position i, decide: Should bit i be 0 or 1
       - Case 1: If we keep bit i = 0
           - Then we must place all k ones in the remaining lower i bits (0..i-1)
           - How many such numbers exist?  c = C(i,k) = comb[i][k];
           - So c is the number of valid integers that start with bit i = 0 (given remaining requirement of k ones).
        -  Case 2: If n > c
           - The first c valid numbers are all in the group where bit i = 0
           - But we want the n-th number, and it is after those c numbers.
           - So the answer must be in the next group: bit i = 1. 
           - Thus we set bit i: res |= (1L << i);
           - Now, we skip the first c numbers, so: n -= c
           - Also, since we used one 1 bit already, we reduce k: k -= 1
        - Case 3: If n <= c
           - Then the desired number lies inside the first c numbers (bit i = 0 group).
           - So we keep bit i = 0 and move on (nothing changes).
    6. Why this greedy works?
       - Because we are always deciding the smallest possible prefix.
       - Keeping a high bit 0 makes the number smaller than keeping it 1.
       - So we only set a bit to 1 when we are forced to (because n is too large to fit in the 0 group).
       - This is exactly how “k-th lexicographic combination” / “ranking/unranking” works.
    7. Termination condition: when k = 0
       - If k becomes 0, we already placed all required ones.
       - Remaining bits must all be 0.
       - So we can stop early:  if (--k == 0) break;
    8. Combinatorial greedy is the unranking method: it jumps directly to the n-th element of that same sequence.  
    */
    public long nthSmallest1(long n, int k){
        
        // Build combinatorial table
        long[][] comb = new long[51][51];
        for(int i = 0; i <= 50; i++){
            comb[i][0] = 1;
            for(int j = 1; j <= i; j++){
                comb[i][j] = comb[i-1][j-1] + comb[i-1][j];
            }
        }

        // Greedy Bit-by-Bit Construction(MSB -> LSB)
        long res = 0;
        for(int i = 49; i >= 0; i--){
            long c = comb[i][k];
            if(n > c){
                res |= (1L << i);
                n -= c;  // skip the whole block of size c
                if( --k == 0) break;
            }
        }
        return res;
    }

}