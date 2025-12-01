//LC.29. Divide Two Integers: https://leetcode.com/problems/divide-two-integers/description/

public class B {
    //Appraoch-1(TLE): Math
    //TC = O(quotient), SC = O(1)
     public int divide1(int dividend, int divisor){
        //Handle overflow
        if(dividend == Integer.MIN_VALUE && divisor == -1){
            return Integer.MAX_VALUE;   //2^31 -> return 2^31 -1
        }
        
        boolean negative = (dividend < 0) ^ (divisor < 0); //XOR to check if signs are different

        long a = Math.abs((long) dividend);
        long b = Math.abs((long) divisor);

        int cnt = 0;

        while(a >= b){
            a -= b;
            cnt++;
        }
        return negative ? -cnt : cnt;
     }

       //Appraoch-2: Bitwise Doubling
       //TC = O(logn), SC = O(1)
       /*
        1. Every number can be written as sum of powers of 2:
            Divident = 22, Divisor = 3
            (3 * 7) = (3 * (2^0 + 2^1 + 2^2)) = (3 * 2^0) + (3 * 2^1) + (3 * 2^2)
        2. Remove 3*2^2 from 22, then remove 3*2^1 from the remaining, and so on. Stop when remaining < divisor.
        3. Count how many 3's removed, that is the quotient(4 + 2 + 1)
        4. Neagtive sign handling and overflow handling as before.
            - Assume both are positive, divide and then add sign to quotient.
        5.  We are doubling temp and cnt until temp (i.e. divisor * 2^cnt) becomes too large for a.
            - temp <<= 1 means temp = temp * 2
            - cnt <<= 1 means cnt = cnt * 2
        6. This approach skips repetitive subtraction (like a = a - b many times) by jumping faster via powers of two.
        */
    public int divide2(int dividend, int divisor) {
        // Handle overflow edge case
        if (dividend == Integer.MIN_VALUE && divisor == -1)
            return Integer.MAX_VALUE;

        if (dividend == divisor)
            return 1;

        boolean negative = (dividend < 0) ^ (divisor < 0);  // captures all 4 sign combinations

        long a = Math.abs((long) dividend);
        long b = Math.abs((long) divisor);
        int ans = 0;

        while (a >= b) {  
            long temp = b;
            int cnt = 1;
            while (a >= (temp << 1)) {  // double until too big
                temp <<= 1;
                cnt <<= 1;
            } //After this loop, temp is the largest multiple of divisor that can fit into current a.
            a -= temp;   // reduce dividend by that large multiple
            ans += cnt;  // add that multiple to quotient
        }

        if (negative) ans = -ans;

        return ans;
    }


    //Appraoch-3(Optimal): Bitwise Shift(Long Division Simulated in Binary)
    //TC = O(32) = O(1), SC = O(1)
    /*
     1. We check from most significant bit down to least, to see if divisor × 2^shift fits into dividend.
     2.  We check for each shift (from 31 → 0) if (3 × 2^shift) ≤ remaining a.
     3. E.g.: dividend = 43 (binary: 101011); divisor  = 3  (binary: 11)
        1. a = 43, b = 3, result = 0
           We loop shift from 31 → 0.
        2. High Bits (shift = 31 → 4)
           For large shifts, (b << shift) is huge.
           So (a >> shift) is tiny → always < b.
           No subtraction, no change to result.
        3. shift = 3 (testing 2³ = 8)
           (b << 3) = 3 * 8 = 24 ≤ 43
           (a >> 3) = 43 / 8 = 5 (integer division) ≥ 3
           a -= (b << 3) → 43 - 24 = 19 and result += (1 << 3) → 0 + 8 = 8
        4. shift = 2 (testing 2² = 4)
           (b << 2) = 3 * 4 = 12
           (a >> 2) = 19 / 4 = 4 ≥ 3
            a -= 12 → 7;  result += 4 → 8 + 4 = 12
        5. shift = 1 (testing 2¹ = 2)
           (b << 1) = 6
           (a >> 1) = 7 / 2 = 3 ≥ 3
           a -= 6 → 1; result += 2 → 12 + 2 = 14
        6. shift = 0 (testing 2⁰ = 1)
           (b << 0) = 3
            (a >> 0) = 1 < 3
            No update.
    4. Binary quotient built bit by bit:
       Bit 3 → 1; Bit 2 → 1; Bit 1 → 1; Bit 0 → 0
       Result = 8 + 4 + 2 + 0 = 14
     */
    public int divide3(int dividend, int divisor) {
        // Handle overflow
        if (dividend == Integer.MIN_VALUE && divisor == -1)
            return Integer.MAX_VALUE;

        // Determine sign
        boolean negative = (dividend < 0) ^ (divisor < 0);

        // Work with positive longs to avoid overflow
        long a = Math.abs((long) dividend);
        long b = Math.abs((long) divisor);
        int result = 0;

        // Go from highest possible bit down to 0
        for (int shift = 31; shift >= 0; shift--) {
            if ((a >> shift) >= b) {   //(a >> shift) = a / (2^shift)
                a -= (b << shift);     // subtract that chunk
                result += (1 << shift); // add to quotient
            }
        }

        return negative ? -result : result;
    }
}
