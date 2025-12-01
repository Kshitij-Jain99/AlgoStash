//LC.3720. Lexicographically Smallest Permutation Greater Than Target: https://leetcode.com/problems/lexicographically-smallest-permutation-greater-than-target/description/

public class I {
    // Approach-1{Optimal}: Greedy Backtracking Using Frequency Counting
    // TC: O(26 * n) ≈ O(n), SC: O(26) = O(1)
    /*
     1. Count available characters(frequency array of "s").
     2. Start matching target from left to right.
        - a. If target[i] is available → use it and continue.
        - b. Else → try to pick the next larger letter available (this ensures we exceed target lexicographically).
     3. If a larger letter is found:
        - Place it. Then fill all remaining positions with the smallest possible letters left in the bag.
        - This ensures the resulting string is the smallest greater permutation.
     4.  If no letter ≥ target[i] is possible:
        - Backtrack to previous position and try the next larger letter there.
     5. If even backtracking fails:
        - Return empty string (no greater permutation exists).
     6. Why greedy works:
        - Once you exceed at some position, you should make all later positions as small as possible to ensure the overall string is the smallest possible greater one.
     */
    public String lexGreaterPermutation1(String s, String target) {
        //1
        int[] freq = new int[26];
        for (char ch : s.toCharArray()) freq[ch - 'a']++;

        StringBuilder ans = new StringBuilder(); //Output

        //2
        for (int i = 0; i < target.length(); i++) {
            char ch = target.charAt(i);

            // 2a. if current character exists
            if (freq[ch - 'a'] > 0) {
                ans.append(ch);
                freq[ch - 'a']--;
                continue;
            }

            // 2b, 3. find if there is any larger element
            int idx = ch - 'a' + 1;
            for (; idx < 26; idx++) {
                if (freq[idx] > 0) {  // found a bigger character
                    freq[idx]--;
                    ans.append((char) (idx + 'a'));

                    // fill remaining characters in sorted order
                    for (int j = 0; j < 26; j++) {
                        while (freq[j]-- > 0)
                            ans.append((char) (j + 'a'));
                    }
                    return ans.toString();
                }
            }

            //4. could not find a bigger or equal character -> backtrack
            while (ans.length() > 0) {
                int lastIdx = ans.length() - 1;
                char lastChar = ans.charAt(lastIdx);
                ans.deleteCharAt(lastIdx);

                freq[lastChar - 'a']++;

                idx = target.charAt(lastIdx) - 'a' + 1;
                for (; idx < 26; idx++) {
                    if (freq[idx] > 0) {
                        freq[idx]--;
                        ans.append((char) (idx + 'a'));

                        for (int j = 0; j < 26; j++) {
                            while (freq[j]-- > 0)
                                ans.append((char) (j + 'a'));
                        }
                        return ans.toString();
                    }
                }
            }
            // 5. If we reach here, impossible to form a valid permutation
            return "";
        }

        // Equal string case: reached end, ans equals target — try to make next lexicographic
        while (ans.length() > 0) {
            int lastIdx = ans.length() - 1;
            char lastChar = ans.charAt(lastIdx);
            ans.deleteCharAt(lastIdx);

            freq[lastChar - 'a']++;

            int idx = target.charAt(lastIdx) - 'a' + 1;
            for (; idx < 26; idx++) {
                if (freq[idx] > 0) {
                    freq[idx]--;
                    ans.append((char) (idx + 'a'));

                    for (int j = 0; j < 26; j++) {
                        while (freq[j]-- > 0)
                            ans.append((char) (j + 'a'));
                    }
                    return ans.toString();
                }
            }
        }

        return "";
    }

    //Appraoch-2: DFS with Pruning (Recursive Backtracking Search)
    // TC: O(26^n)(ususally much faster due to pruning); Avg O(n), SC: O(n+26) {recursion stack + freq + result}
    /*
     Approach-1: 
        - Iteratively builds the answer left-to-right, trying to match the target as far as possible, and backtracks only when needed.
        - this approach is manually simulating a DFS but in a very controlled, optimized, greedy manner.
        - Intuition: Like computing next greater permutation manually
     Approach-2:
        - Recursively explores all valid letter placements with pruning-stops early once a valid permutation strictly greater than target is found.
        - Intuition: Like generating permutations recursively
    
    1. We build the result string character by character, from left to right.
    2.  At every position idx:
       - If we have not yet exceeded the target so far, we can only place a character ≥ target[idx].
       - If we already exceeded the target at some earlier position, we can place any available smallest letter.
       - This ensures:
          a. We never go below the target prefix lexicographically.
          b. The first valid complete string we form will automatically be the smallest possible greater permutation (because we try smaller letters first).
     */

    private int[] freq;
    private char[] targetArr;
    private char[] result;
    private int n;

       public String lexGreaterPermutation2(String s, String target) {
        n = s.length();
        result = new char[n];
        targetArr = target.toCharArray();
        freq = new int[26];

        //1. Frequency counting
        for (char c : s.toCharArray()) {
            freq[c - 'a']++;
        }

        //2. Recursive search (dfs)
        //deciding what letter to put at position idx
        //exceeded == true means the prefix we’ve built so far is already strictly greater than target[0..idx-1].
        //If we reach the end (idx == n) and exceeded == true, then the built string is valid.
        boolean success = dfs(0, false);
        return success ? new String(result) : "";
    }

    //3. DFS with pruning
    //a.If we haven’t exceeded yet (exceeded == false):
    private boolean dfs(int idx, boolean exceeded) {
        if (idx == n) {
            return exceeded;  // only valid if we've strictly exceeded
        }

        int start = 0;
        if (!exceeded) {
            start = targetArr[idx] - 'a'; // we must be ≥ target[idx]
        }

        for (int c = start; c < 26; c++) {
            if (freq[c] == 0) continue;

            freq[c]--;
            result[idx] = (char) (c + 'a');

            boolean nextExceeded = exceeded || (c > targetArr[idx] - 'a');
            if (dfs(idx + 1, nextExceeded)) {
                return true;
            }

            // backtrack
            freq[c]++;
        }

        return false;
    }
}