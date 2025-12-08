// LC. 1611. Minimum One Bit Operations to Make Integers Zero: https://leetcode.com/problems/minimum-one-bit-operations-to-make-integers-zero/description/

public class L {
    
    //Approach-1 (Beats 7.6%, 1ms):  
    // TC: O(1); SC: O(32) = O(1)
    public int minimumOneBitOperations1(int n){
        if(n == 0) return 0;

        long[] function = new long[32];
        //function[i] = x -> it will take x operations to make i bits zero
        function[0] = 1;
        for(int i = 1; i <= 31; i++){
            function[i] = 2 * function[i-1] + 1;
        }

        int result = 0;
        int sign = 1;

        for(int i = 30; i>= 0; i--){
            int ithBit = ((1 << i) & n);

            if(ithBit == 0) continue;

            if(sign > 0){
                result += function[i];
            } else {
                result -= function[i];
            }
            sign *= -1;
        }
        return result;
    }

    //Approach-2{ Beats 100%, 0ms}: Gray Code to Binary Conversion {Parallel prefix XOR}
    // TC = O(log(logn)⋅logn) = O(log(5).5) = O(1);  SC = O(1)
    /*
    1. A Gray code, is an ordering of binary numbers such that every successive number in the ordering differs by only one bit.
    2. The problem defines some operations that change exactly one bit at a time.
       Any sequence where consecutive numbers differ in exactly one bit is a Gray code sequence.
    3. This problem generate the standard Gray code order
       “minimum operations to go from n to 0” = “distance from 0 to n in this Gray code sequence” 
                                              = “index of n in the Gray code sequence starting at 0”.
    4. Gray code ↔ binary conversion formula:
       gray = binary ^ (binary >> 1)
       binary = gray ^ (gray >> 1) ^ (gray >> 2) ^ ...
    5. n is treated as a Gray code, and we’re converting it to binary — that result is the minimum number of operations.
    6. For a 32 bit integer:
       - the maximum distance between MSB and LSB is 31.
       - If we do shifts of 16, 8, 4, 2, 1, we ensure each bit is XORed with all bits to its left.
       - This is the same idea as a prefix XOR done in log(32) steps (like a parallel prefix sum).
    */
    public int minimumOneBitOperations2(int n){
        int ans = n;
        // Take the current value, shift it to the right, and XOR it back into itself
        ans ^= ans >> 16;
        ans ^= ans >> 8;
        ans ^= ans >> 4;
        ans ^= ans >> 2;
        ans ^= ans >> 1;
        return ans;
    }


    // Approach-3( Beats 100%, 0ms, space optimized): Gray Code to Binary Conversion {Iterative XOR with shifted copies}
    // TC: O(1); SC: O(1)
    /*
     1. This is a space optimized version of Approach-2.
     2. Instead of storing intermediate results, we directly update n in each step.
     3. This directly accumulates: 
        binary = gray ^ (gray >> 1) ^ (gray >> 2) ^ ...
     4. Works for any number of bits, not just 32-bit integers.
    */
    public int minimumOneBitOperations3(int n) {
        long k = 0;
        long g = n;

        while(g > 0){
            k = k ^ g;
            g = g >> 1;
        }

        return (int)k;
    }
}

