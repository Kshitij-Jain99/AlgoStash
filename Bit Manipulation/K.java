// LC. 995. Minimum Number of K Consecutive Bit Flips: https://leetcode.com/problems/minimum-number-of-k-consecutive-bit-flips/description/

import java.util.Deque;
import java.util.LinkedList;

public class K {
    
    //Approach-1 (Using auxiliary array to mark Flipped indices)
    //T.C : O(n); S.C : O(n)
     public int minKBitFlips1(int[] nums, int k) {
        int n = nums.length;

        int flips = 0;                          // total number of flips performed
        boolean[] isFlipped = new boolean[n];  // isFlipped[i] == true means: we started a K-length flip at index i
        int flipCountFromPastForCurri = 0;    // how many flips from the past are currently affecting index i (only the count parity matters)

        for (int i = 0; i < n; i++) {
            if (i >= k && isFlipped[i - k]) {    // Remove flips that no longer affect i
                flipCountFromPastForCurri--;
            }

            if (flipCountFromPastForCurri % 2 == nums[i]) {
                if (i + k > n) {
                    return -1;
                }
                flipCountFromPastForCurri++;
                isFlipped[i] = true;
                flips++;
            }
        }

        return flips;
    }


    //Approach-2 (Using same input to mark Flipped indices - We will be manipulating the input as well)
    //T.C : O(n); S.C : O(1)
        public int minKBitFlips2(int[] nums, int k) {
        int n = nums.length;

        int flips = 0;
        int flipCountFromPastForCurri = 0;

        for (int i = 0; i < n; i++) {

            if (i >= k && nums[i - k] == 2) { // Was it flipped
                flipCountFromPastForCurri--;
            }

            if (flipCountFromPastForCurri % 2 == nums[i]) {
                if (i + k > n) {
                    return -1;
                }
                flipCountFromPastForCurri++;
                nums[i] = 2; // Marking as flipped
                flips++;
            }
        }

        return flips;
    }


    //Approach-3 (Using deque to mark Flipped indices)
    //T.C : O(n); S.C : O(k)
    public int minKBitFlips3(int[] nums, int k) {
        int n = nums.length;

        int flips = 0;
        Deque<Integer> flipQue = new LinkedList<>();
        int flipCountFromPastForCurri = 0;

        for (int i = 0; i < n; i++) {
            if (i >= k) {
                flipCountFromPastForCurri -= flipQue.pollFirst();
            }

            if (flipCountFromPastForCurri % 2 == nums[i]) {
                if (i + k > n) {
                    return -1;
                }
                flipCountFromPastForCurri++;
                flipQue.addLast(1);
                flips++;
            } else {
                flipQue.addLast(0);
            }
        }

        return flips;
    }

}
