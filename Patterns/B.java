// LC. 3900. Longest Balanced Substring After One Swap: https://leetcode.com/problems/longest-balanced-substring-after-one-swap/description/
// Prefix Sum 
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class B{
    
    // Approach-1(386 ms): Prefix Sum + HashMap (with modified condition)
    // TC = O(2N), SC = O(6N)
    /*
    This is a prefix sum + hashmap problem where we extend the classic zero-sum subarray idea
    by allowing a deviation of ±2 to simulate one swap.
        1. Breaking into 2 parts:
           A. No Swap Needed:
              - Store in Map: diff → first index(where this sum seen)
              - '1' -> +1; '0' -> -1
              - diff[i] = count of 1s up to i − count of 0s up to i
              - If diff[i] == diff[j], the substring from j+1 to i is perfectly balanced
              - Length of this window : i - (j+1) + 1 = i-j
           B. One Swap Allowed:
              - Inside substring: swapping 1 outside with 0 inside → increases diff by +2
                                  swapping 0 outside with 1 inside → decreases diff by -2
                                  diff = +1(inside loses one 0) + 1(inside gains one 1) = +2
                                  One swap changes diff by ±2
              - CASE-1: diff = -2 (more 0s in SS)
                -> To fix it we need +2 change → bring 1 inside        
                -> To detect:
                             At index i, current prefix = pref
                             We want a previous index j such that: pref[j] = pref + 2
                             Because: subarray sum = pref[i] - pref[j] = -2
                -> Check: total_1 > ones_inside_subarray           
              - CASE-2: diff = +2 (more 1s in SS)
                -> Need -2 → bring 0 inside
                -> Check: pref[j] = pref - 2
                -> Check: total_0 > zeros_inside_subarray
        2. What are ind, ind0, ind1?
              - ind (main hashmap)    
              - ind1 -> Stores prefix indices only after at least one '1' has appeared (ensures: we can bring a '1' from outside)
              - ind0 -> Stores diff only if we have seen at least one '0' before (ensures: we can bring a '0' from outside)
        3. So, we try 3 things:
           A. No swap: find j where diff[j] == diff[i]
           B. Fix -2 case: find j where diff[j] == diff[i] + 2   AND we have extra '1' outside
           C. Fix +2 case: find j where diff[j] == diff[i] - 2   AND we have extra '0' outside         
        4. Store ONLY the FIRST occurrence of this prefix sum:
              - First occurance -> longest subarray = i - earliest index
              - putIfAbsent -> store earliest index for max length
              - Fallback Maps ensure swap is actually possible
              - Prefix sum tells you "this substring COULD be fixed"
              - But fallback maps ensure: "this substring CAN ACTUALLY be fixed"
        5. Sliding window may not work because the condition is not locally maintainable, So prefix sum is used:
           A. Sliding Window:
              - Works when condition is LOCAL (window-validity based)
           B. Prefix Sum:
              - Works when condition is GLOBAL (depends on whole subarray)
    */
    public int longestBalanced1(String s){
        int n = s.length();
        // Prefix arrays: z[i] -> total 0s till index i; o[i] -> total 1s till index i
        int[] z = new int[n], o = new int[n], diff = new int[n];
        int cnt0 = 0, cnt1 = 0;

        for(int i = 0; i<n; i++){
            if(s.charAt(i) == '0') cnt0++;
            else cnt1++;
            z[i] = cnt0; o[i] = cnt1; diff[i] = o[i] - z[i];
        }

        int maxLen = 0;
        Map<Integer, Integer> ind = new HashMap<>(), ind0 = new HashMap<>(), ind1 = new HashMap<>();
        ind.put(0, -1);            // prefix sum BEFORE array = 0 at index = -1 -> So that subarrays starting from index 0 are handled.

        for(int i = 0; i<n; i++){
            int pref = diff[i]; // curr prefix

            // A. No swap needed
            if(ind.containsKey(pref)) maxLen = Math.max(maxLen, i - ind.get(pref));

            // B. diff = -2 (more 0s)
            if(ind.containsKey(pref + 2)) {
                int j = ind.get(pref + 2); // need: pref[i] - pref[j] = -2  → pref[j] = pref[i] + 2
                int sub1s = o[i] - (j >= 0 ? o[j] : 0);   // Count 1s inside substring -> number of 1s in (j+1 → i); if j = -1, there is no prefix → subtract 0
                if(cnt1 > sub1s) maxLen = Math.max(maxLen, i-j); // Check if swap is possible
                else if (ind1.containsKey(pref + 2)) maxLen = Math.max(maxLen, i - ind1.get(pref + 2)); // else fallback to those j where at least one '1' exists before -> guaranteed swap
            }

            // C. diff = +2 (more 1s)
            if (ind.containsKey(pref - 2)) {
                int j = ind.get(pref - 2);
                int sub0s = z[i] - (j >= 0 ? z[j] : 0);
                if (cnt0 > sub0s) maxLen = Math.max(maxLen, i - j);
                else if (ind0.containsKey(pref - 2)) maxLen = Math.max(maxLen, i - ind0.get(pref - 2));
            }

            ind.putIfAbsent(pref, i);
            // Fallback Maps
            if (z[i] > 0) ind0.putIfAbsent(pref, i); // before seeing any 0, you cannot swap in a 0
            if (o[i] > 0) ind1.putIfAbsent(pref, i); // before seeing any 1, you cannot swap in a 1
        }
        return maxLen;
    }


    // Approach-2(16 ms): Prefix Sum + Array(Two-Pass Optimization)
    // TC = O(2N), SC = O(3N + 3)
    /*
        1. Same core idea and logic
        2. Using arrays instead of HashMap:
              - balIndex[X] = first index where balance = X ; bal = ones - zeros 
                Size = 2*n + 3, because: range: [-n, n] -> shifting start bal = n + 1, make range: [1 ... 2n+1]
              - nextIndex[i] = next index after i with SAME balance
              - Faster because: HashMap → O(1) but high constant
                                Array → O(1) very low constant(much faster)
        3. Two pass structure:
           A. PASS 1 (RIGHT → LEFT):
              - Build: balIndex, nextIndex
              - Bitwise trick to replace if-else: 
                -> bal += (('0' ^ arr[i]) << 1) - 1;
                -> Same logic: if(arr[i] == '1') bal++; else bal--;
                -> '0' = 48, '1' = 49 
                -> '0' ^ arr[i] --> 48 ^ 48 = 0; '1' ^ arr[i] --> 48 ^ 49 = 1
                -> Multiply by 2 (and << 1) -> 0 and 2
                -> Subtract 1 -> -1, +1
                -> Equivalent to: bal += (arr[i] == '1') ? 1 : -1
              - After this loop: bal = n + 1 --> bal = (n + 1) + (#1 - #0)   
              - Return early if the whole string is balanced i.e. (n + 1) + (#1 - #0) = (n + 1)
              - deriving counts from the final balance:
                -> bal = n + 1 + (ones - zeros)
                -> ones = n - zeros -> ones - zeros = n - 2 * zeros
                -> bal = n + 1 + (n - 2 * zeros) --> bal = 2n + 1 - 2 * zeros
                -> zeros = (2n + 1 - bal) / 2 and ones = n - zeros 
              - We ignore substrings longer than possible:
                -> Balanced substring: #0 == #1  -->  maxLen = 2 * Math.min(zeros, ones);
           B. PASS 2 (LEFT → RIGHT)
              - For every pos, try: Find the longest valid substring ending at i
              - Update balance using Bitwise trick but in reverse
              - CASE 1: No swap needed:
                -> Find earliest index j where balance was same → substring (j → i-1) has sum = 0
                -> same balance ⇒ equal 0s and 1s
              - CASE 2: Fix using swap (0 → 1)
                -> diff = +2 → too many 1s → need to bring 0 || diff = -2 → too many 0s → need to bring 1
                -> here bal - 2 → means substring has diff = +2
                -> balIndex[bal - 2] < i - maxLength --> Start index is TOO far left(substring length > max possible)
                -> So, Jump to next valid index
                -> This replaces: checking: is swap possible?, is there element outside?       
                -> Now compute answer: Substring from balIndex[...] to i is FIXABLE using 1 swap
              - Case 3: Opposite swap (1 → 0)    
                -> Check diff = -2 case
        4. Optimzations:
              - Appraoch-1: check counts inside and outside, use extra maps
              - Approach-2: Pre-filter invalid indices using: maxLength, nextIndex jumping  
              - Why i - maxLength works?
                -> Any substring longer than maxLen is IMPOSSIBLE
                -> So instead of checking: "do we have enough outside elements?", Ensure: "we never consider impossible substrings"
                -> [bad index] → [bad index] → [valid index]    {jump using nextIndex}
              - Instead of verifying swap feasibility per substring, we pre-limit substring size and skip invalid starting indices using next pointers.
              - instead of checking counts (like old solution) -> SKIP bad indices using nextIndex
                This replaces: cnt1 > sub1s; cnt0 > sub0s; ind0, ind1 maps
    */
    public int longestBalanced2(String s){
        char[] arr = s.toCharArray();
        int n = arr.length;
        int bal = n + 1, ans = 0;

        int[] nextIndex = new int[n];
        int[] balIndex = new int[2*n + 3];
        Arrays.fill(balIndex, n+1);

        // Pass-1: Right -> Left
        for(int i = n-1; i >= 0; i--){
            bal += (('0' ^ arr[i]) << 1) - 1; // Bitwise trick to replace if-else
            nextIndex[i] = balIndex[bal];    //update the nextIndex for each index
            balIndex[bal] = i;
        }

        if(bal == n + 1) return n;                              // Early return
        int zeros = (2 * n + 1 - bal) / 2, ones = n - zeros;    // deriving counts
        int maxLength = 2 * Math.min(zeros, ones);              // max possible ans
        
        // Pass-2: Left -> Right
        for(int i = 1; i <= n && ans < maxLength; i++) {
            bal += (('1' ^ arr[i - 1]) << 1) - 1; //update bal in reverse
            // CASE 1: No swap needed
            if(i - balIndex[bal] > ans) ans = i - balIndex[bal];                                       
            
            // CASE 2: Fix using swap (0 → 1)
            if(balIndex[bal - 2] < i - maxLength) balIndex[bal - 2] = nextIndex[balIndex[bal - 2]];    
            if(i - balIndex[bal - 2] > ans) ans = i - balIndex[bal - 2];
            
            // CASE 3: Opposite swap (1 → 0)
            if(balIndex[bal + 2] < i - maxLength) balIndex[bal + 2] = nextIndex[balIndex[bal + 2]];
            if(i - balIndex[bal + 2] > ans) ans = i - balIndex[bal + 2];
        }

        return ans;
    }
}