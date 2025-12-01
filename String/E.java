//LC.76. Minimum Window Substring: https://leetcode.com/problems/minimum-window-substring/description/

import java.util.*;

public class E {
    
    //Approach-1: Sliding Window + HashMap
    // TC = O(M+N), SC = O(N)
public String minWindow(String s, String t){
    int n = s.length();
    if(t.length() > n) return "";

    Map<Character, Integer> mp = new HashMap<>();
    for(char ch: t.toCharArray()) mp.put(ch, mp.getOrDefault(ch, 0) + 1);

    int requiredCount = t.length(); //Number of required characters yet to be matched.
    int i = 0, j = 0;   //left and right boundary of window
    int minWindowSize = Integer.MAX_VALUE;
    int start_i = 0;  //window start index

    while(j < n){
        char ch = s.charAt(j);

        if(mp.containsKey(ch) && mp.get(ch) > 0) requiredCount--;
        mp.put(ch, mp.getOrDefault(ch,0) - 1);

        while(requiredCount == 0){   //found a valid window
            //start shrinking window
            int currWindowSize = j - i + 1;
            if(minWindowSize > currWindowSize){
                minWindowSize = currWindowSize;
                start_i = i;
            }

            //shrinking from i
            char startChar = s.charAt(i);
            mp.put(startChar, mp.getOrDefault(startChar, 0) + 1);
            if(mp.containsKey(startChar) && mp.get(startChar) > 0) requiredCount++;
            i++;
        }
        j++;
    }
    return minWindowSize == Integer.MAX_VALUE ? "" : s.substring(start_i, start_i + minWindowSize);
  }


  //Approach-2(Optimal): Sliding Window + Array
  // TC = O(M+N), SC = O(1) (since t has at most 128 characters)
  public String minWindow2(String s, String t) {
        int[] charCount = new int[128];  // ASCII character count

        // Populate charCount with frequencies of t
        for (char c : t.toCharArray()) {
            charCount[c]++;
        }

        int left = 0, right = 0;
        int required = t.length();  // Total required characters to match
        int minLength = Integer.MAX_VALUE;
        int startIndex = 0;

        while (right < s.length()) {
            char c = s.charAt(right);

            if (charCount[c] > 0) {
                required--;
            }
            charCount[c]--;  // Include c in current window
            right++;

            // Try to contract window from left while it's valid
            while (required == 0) {
                if (right - left < minLength) {
                    minLength = right - left;
                    startIndex = left;
                }

                char leftChar = s.charAt(left);
                charCount[leftChar]++;  // Exclude s[left] from window

                if (charCount[leftChar] > 0) {
                    required++;  // We've lost a needed character
                }
                left++;
            }
        }

        return minLength == Integer.MAX_VALUE ? "" : s.substring(startIndex, startIndex + minLength);
    }


    //Appraoch-3: Sliding Window + Binary search on Answer space 
    //TC = O(M.LogM)(Not optimal), SC = O(1)
    /*
     1. Count frequency of characters in t.
     2. Perform Binary Search on window length: Range: low = t.length() to high = s.length().
        For each mid-point len, check if a valid window exists.
     3. Window Check Function (check(int len))
        - Use sliding window of size len over s
        - Maintain a have[] array to count frequencies of current window.
        - Maintain a count variable to check how many characters have been fully satisfied.
        - If at any point count == t.length(), return the start index of this valid window.
     4. Binary Search narrows down to the smallest possible valid window.
     */
    public class MinimumWindowBinarySearch {
    int n;
    String s, t;
    int[] need = new int[128];

    private int check(int len) {
        int[] have = new int[128];
        int count = 0;

        // Initialize first window
        for (int i = 0; i < len; i++) {
            have[s.charAt(i)]++;
            if (have[s.charAt(i)] <= need[s.charAt(i)]) count++;
        }

        if (count == t.length()) return 0;

        // Slide the window
        for (int i = len; i < n; i++) {
            // Add new character
            have[s.charAt(i)]++;
            if (have[s.charAt(i)] <= need[s.charAt(i)]) count++;

            // Remove old character
            have[s.charAt(i - len)]--;
            if (have[s.charAt(i - len)] < need[s.charAt(i - len)]) count--;

            if (count == t.length()) return i - len + 1;
        }

        return -1; // No valid window found
    }

    public String minWindow3(String S, String T) {
        s = S;
        t = T;
        n = s.length();

        if (t.length() > n) return "";

        // Frequency count of t
        for (int i = 0; i < 128; i++) need[i] = 0;
        for (char c : t.toCharArray()) need[c]++;

        int low = t.length();
        int high = n;
        int ansLen = Integer.MAX_VALUE;
        int ansStart = -1;

        while (low <= high) {
            int mid = (low + high) / 2;
            int start = check(mid);

            if (start != -1) {
                if (mid < ansLen) {
                    ansLen = mid;
                    ansStart = start;
                }
                high = mid - 1; // Search smaller window
            } else {
                low = mid + 1; // Search larger window
            }
        }

        return ansStart == -1 ? "" : s.substring(ansStart, ansStart + ansLen);
    }
}

}
