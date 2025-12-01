//LC.137. Single Number II: https://leetcode.com/problems/single-number-ii/description/

import java.util.Arrays;
import java.util.HashMap;

public class A{

    //Approach-1: Brute Force
    //TC = O(n.logm + m), SC = O(m)  where m = n/3 + 1
     public int singleNumber1(int[] nums) {
        HashMap<Integer, Integer> mpp = new HashMap<>();
        for(int num: nums) mpp.put(num, mpp.getOrDefault(num, 0) + 1);

        for(int num: nums){
            if(mpp.get(num) == 1) return num;
        }
        return 0;
    }

    //Approach-2: Bit Manipulation(Counting Bit column wise)
    //TC = O(32.n), SC = O(1)
    /*
     1. Each bit position acts independetly.
     2. So you can “peel off” each bit position, look at its total contribution modulo 3, and reconstruct the number.
   
       Enxtensions: k>=2
       1. If each number appears k times,then the sum of 1s in any bit position will also be a multiple of k,
          except for the bits that belong to the unique number (which appears once).
       2. change:  if (count % k != 0)   // the unique number has this bit
       3. You can even generalize it for a case like:
          - “every element appears k times except one that appears p times”
          - if (count % k == p) ans |= (1 << bitIdx);
     */
     public int singleNumber2(int[] nums) {
      int ans = 0;
      int n = nums.length;

      for(int bitIdx = 0; bitIdx < 32; bitIdx++){
        int cnt = 0;
        for(int i = 0; i<n; i++){
            if((nums[i] & (1 << bitIdx)) != 0) cnt++;
        }
        if(cnt % 3 == 1) {  //the unique number has this bit set
            ans = ans | (1 << bitIdx);   //set ith bit in the answer to construct number
        } 
      }
      return ans;
    }

    //Approach-3: Sorting (Distorting the input)
    //TC = O(n.logn + n/3), SC = O(1)
    /*
     1. Sort array, cluster of same elements formed together, so unique element will be alone.
     2. This is better appraoch as we are not always working on 32 bit sized elements array.
     */
     public int singleNumber3(int[] nums) {
      Arrays.sort(nums);
      int n = nums.length;

      for(int i = 1; i<n; i+=3){
            if(nums[i] != nums[i-1]) return nums[i-1];
      }
      return nums[n-1];
    }

    
    //Approach-4: Using Bit Buckets / Two-registers Trick
    //TC = O(n), SC = O(1)
    /*
     1. XOR: To detetct bits which appears odd number of times(here 1 and 3 times)
     2. ones and twos, to keep track of the count of each bit position(ones and twos are 32-bit masks.)
     3. For each individual bit position (0..31) we want to count how many times that bit has appeared across the array, mod 3.
     4. A 2-bit counter suffices to count 0,1,2 (we’ll represent 3 as 0 again because of modulo 3).
     5. ones and twos together form that 2-bit counter for every bit position simultaneously:
        - (ones_bit, twos_bit) = (0,0) means count 0 (mod 3)
        - (1,0) means count 1 (mod 3)
        - (0,1) means count 2 (mod 3)
        - then next 1 returns it to (0,0) (count 3 ≡ 0 mod 3)
     6. After processing the whole array, the bits that have count ≡ 1 (mod 3) will be set in ones. So ones is the unique number.
     7. Why the two update lines implement "The per-bit state machine":
        - ones first toggles the bit if b=1( for nums[i]) (ones ^ b)
        - then clears any bit that is already in twos (& ~twos) to keep ones and twos disjoint.
        - twos is then computed similarly and uses the updated ones to avoid overlap.
        - So the pair of expressions enforce:
          a. the 3-state cycle per bit, and
          b. the invariant ones & twos == 0 (no bit is simultaneously marked as “seen once” and “seen twice”).
     8. Incorrect variant: ones ^= (x & ~twos)
        - ones = (ones ^ x) & ~twos first toggles ones with the new input, then clears bits that appear twice.
        - The other variant only XORs the portion of x not in twos, which breaks the required transition sequencing and the ones & twos == 0 invariant in some scenarios.
        - The order of operations and using the updated ones when computing twos is important.

        Extensions:
        1. The same idea generalizes to other small k but requires more bit buckets (log₂k bits per counter or special logic). For k=3 this 2-register trick is minimal and elegant.
     */
    public int singleNumber4(int[] nums) {
        int n = nums.length;
        int ones = 0, twos = 0;  //Tracks the bits that have appeared once and twice

        for(int i = 0; i<n; i++){
            ones = (ones ^ nums[i]) & ~twos;
            twos = (twos ^ nums[i]) & ~ones;
        }
        return ones;
    }
}