//LC.287. Find the Duplicate Number: https://leetcode.com/problems/find-the-duplicate-number/description/
//Constraint: Without modifying the array nums & using only constant extra space.


public class D{
    //Approach-1: Brute Force
    // TC = O(n^2), SC = O(1)
    public int findDuplicate1(int[] nums) {
        int n = nums.length;
        for(int i=0; i<n; i++){
            for(int j=i+1; j<n; j++){
                if(nums[i] == nums[j]){
                    return nums[i];
                }
            }
        }
        return -1; // This line will never be reached as per problem constraints
    }


    //Approach-2: Binary Search on answer space
    // TC = O(m.logm), SC = O(1)
    /*
     1. Finding integer in [1...n] instead of input array or indexs.
     2. Applying pigenhole principle.
        {
         - If you have more than k numbers that are ≤ k,
           then at least one of those numbers must be duplicated.
        }
        - If all numbers from 1 to mid were distinct,then there can be at most mid numbers ≤ mid.
        - If count ≤ mid → No duplicate in [1 … mid] → search right half→ low = mid + 1
        - If count > mid → Duplicate must be in [1 … mid] → search left half→ high = mid
     */
     public int findDuplicate2(int[] nums){
        int m = nums.length;

        int low = 1, high = m-1;
        while(low < high){            //answer space search
            int mid = low + (high - low) / 2;
            int count = 0;
            for(int num : nums){
                if(num <= mid){
                    count++;
                }
            }
            if(count <= mid) low = mid+1;
            else high = mid;
        }
        return low;
     }

     
    //Approach-3: Bit Manipulation
    // TC = O(N.log(maxNum)), SC = O(1)
    /*
     1. Convert all the numbers to binary form.
     2. Find the duplicate number by comparing the bit patterns of the actual array with the expected bit patterns of numbers 1 to n.
     3. For each bit position, count 1s: actual array and ideal array [1...n].
     4. If actual count > expected count, that bit is set in the duplicate number.
     e.g. Get ith bit sum for nums[] and for range [1...n]. Then subract both sum.
          nums[3,1,3,3,3] {m=5, n=4}        [1,2,3,4] range
          3 -> 011                           1 -> 001
          1 -> 001                           2 -> 010
          3 -> 011                           3 -> 011
          3 -> 011                           4 -> 100  
          3 -> 011
ith bitSum:  [0,4,5]           -                [1,2,2]   -> [-1,2,3] => Equvialant to [0,1,1]{3} which is duplicate number.
     */
    public static int findDuplicate3(int[] nums){
        int n = nums.length-1;    //1...n range
        int ans = 0;

        //Step-1: Find max bit required to represent maxNum in nums[]
        int maxNum = findMaxNum3(nums);
        int maxBit = 31;
        while((maxNum >> maxBit) == 0){
            maxBit--;
        }

        //Step-2: For each bit position, count bits in nums[] and in range [1...n]
        for(int bit = 0; bit <= maxBit; bit++){
            int countNums = 0;   //bits in nums[]
            int countRange = 0;  //bits in range [1...n]

            for(int num: nums){
                if ((num & (1 << bit)) !=0 ) countNums++;
            }
            for(int i=1; i<=n; i++){
                if((i & (1 << bit)) !=0) countRange++;
            }

            //Step-3: If nums[] has more 1s for this bit, that bit is part of duplicate
            if (countNums > countRange) ans |= (1 << bit);  //rebuilt number bit by bit
          }
            return ans;
        }

        static int findMaxNum3(int[] nums){
            int maxNum = 0;
            for(int num: nums) maxNum = Math.max(maxNum, num);
            return maxNum;
        }
     
        
        //Approach-4: Slow and Fast Pointer/Floyd's Tortoise and Hare (Cycle Detection)
        //TC = O(n), SC = O(1)
        /*
         1. Duplicate element will create a cycle in the linked list representation of the array.
         2. The duplicate element will have: 2 Incoming and 1 outgoing edges.{Intersection element}
         3. 
         */
        public int finDuplciates4(int[] nums){
            int slow = 0, fast = 0;
            while(slow != fast){
                slow = nums[slow];  // 1 step
                fast = nums[nums[fast]]; //2 steps
            }

            slow = 0;
            while(slow != fast){
                slow = nums[slow];
                fast = nums[fast];
            }
            return slow;
        }
    }

