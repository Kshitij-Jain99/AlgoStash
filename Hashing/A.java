// LC. 3843. First Element with Unique Frequency: https://leetcode.com/problems/first-element-with-unique-frequency/description/
import java.util.*;

public class A{
    // Approach-1(112 ms): Map of Map (Works for any set of values)
    // TC = O(3.N), SC = O(2.N)
     public int firstUniqueFreq1(int[] arr) {

        // 1: Count frequency of each element (preserve insertion order)
        Map<Integer, Integer> elementCount = new LinkedHashMap<>();
        for (int num : arr) {
            elementCount.put(num, elementCount.getOrDefault(num, 0) + 1);
        }

        // 2: Count how many elements have the same frequency
        Map<Integer, Integer> frequencyCount = new HashMap<>();
        for (int count : elementCount.values()) {
            frequencyCount.put(count, frequencyCount.getOrDefault(count, 0) + 1);
        }

        // 3: Find first element whose frequency is unique
        for (int num : elementCount.keySet()) {
            int freq = elementCount.get(num);
            if (frequencyCount.get(freq) == 1) {
                return num;
            }
        }

        return -1; // No element has a unique frequency
    }


    // Approach-2(110 ms): Using arrays
    // TC = O(2.N + K), SC = O(2.K)
    /*
        1. Arrays version supposed to be musch faster but 2ms difference here:
           - Both solutions are O(n)
           - Input size is not very large(For moderate n, hashing overhead is small.)
           - JIT & runtime noise(Online judges (like LeetCode) have runtime fluctuations of ±2–5 ms naturally.)
           - It runs over size 100001 every time, even if input is small, reducing expected performance.
           - The array approach is theoretically faster (no hashing, better cache locality, primitives instead of objects)
    */
    public int firstUniqueFreq2(int[] nums) {

        // 1: Count frequency of each number
        int[] elementFreq = new int[100001];
        for (int num : nums) {
            elementFreq[num]++;
        }

        // 2: Count how many numbers have the same frequency
        int[] frequencyFreq = new int[100001];
        for (int freq : elementFreq) {
            if (freq > 0) {
                frequencyFreq[freq]++;
            }
        }

        // 3: Traverse original array to maintain order
        for (int num : nums) {
            if (frequencyFreq[elementFreq[num]] == 1) {
                return num;
            }
        }

        return -1;
    }

    // Approach-3(9 ms): Fastest on LC(fails for very large value ranges)
    // TC = O(4.N), SC = O(N + 2.K)
    /*
     1. It is much faster, because:
        - Dynamic Memory Allocation
        - Less Wasted Iteration, only upto max values not 100001 times
        - Better Space Efficiency for Small Inputs
        - It adapts to actual input range.
    */ 
     public int firstUniqueFreq(int[] nums) {

        // 1: Find maximum value to size frequency array properly
        int max = 0;
        for (int num : nums) {
            max = Math.max(max, num);
        }

        // 2: Count frequency of each number
        int[] elementFreq = new int[max + 1];
        for (int num : nums) {
            elementFreq[num]++;
        }

        // 3: Count how many numbers have the same frequency
        int[] frequencyFreq = new int[nums.length + 1];
        boolean[] visited = new boolean[max + 1]; // ensures we count each number only once.

        for (int num : nums) {
            if (!visited[num]) {
                frequencyFreq[elementFreq[num]]++;
                visited[num] = true;
            }
        }

        // 4: Return first number whose frequency is unique
        for (int num : nums) {
            if (frequencyFreq[elementFreq[num]] == 1) {
                return num;
            }
        }

        return -1;
    }
}