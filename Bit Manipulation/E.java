//LC.1915. Number of Wonderful Substrings: https://leetcode.com/problems/number-of-wonderful-substrings/description/
import java.util.*;

public class E{

    //Approach-1: Brute Force
    //TC = O(n^3), SC = O(1)
    /*
     1. Recalculates frequency counts new for each substring
     2. Very slow; repeated recounting is wasteful
     */
    public long wonderfulSubstrings1(String word){
        long result = 0;
        int n = word.length();

        for(int i = 0; i<n; i++){
            for(int j = i; j<n; j++){
                int[] freq = new int[10];
                for(int k =i; k<= j; k++) freq[word.charAt(k) - 'a']++;
            
                int oddCount = 0;
                for(int f: freq) {
                    if(f % 2 != 0) oddCount++;
                }
                if(oddCount <= 1) result++;
            }
        }
        return result;
    }
    
     //Approach-2: Better
     //TC = O(n^2), SC = O(1)
     /*
      1. Not recounting characters from scratch. Instead, maintain frequency while expanding substring.
      */
    public long wonderfulSubstrings2(String word){
        long result = 0;
        int n = word.length();

        for(int i = 0; i<n; i++){
            int[] freq = new int[10];
            for(int j = i; j<n; j++){
                freq[word.charAt(j) - 'a']++;
            
                int oddCount = 0;
                for(int f: freq) {
                    if(f % 2 != 0) oddCount++;
                }
                if(oddCount <= 1) result++;
            }
        }
        return result;
    }

     //Approach-3: Optimal(Prefix XOR): Bitmaksing + Prefix Parity
     //TC = O(n*10){each ch -> 10 iterations}, SC = O(2^10) {At most 1024 different bitmasks stored in the map}
     /*
      1. Each character 'a' to 'j' is represented by a bit position (0–9).
         - Bit = 0 → count of that char is even
         - Bit = 1 → count is odd
      2. prefixPattern[i] = parity pattern of chars in word[0...i]
      3. Consider substring word[l...r] : parity pattern = prefixPattern[r] ^ prefixPattern[l-1]
         - prefixPattern[r] == prefixPattern[l-1] → all even
         - If they differ by only 1 bit → one odd
      */
     public long wonderfulSubstrings3(String word){
        Map<Long, Long> map = new HashMap<>();
        map.put(0L, 1L);   //Base case: pattern 0000000000

        long prefixPattern = 0;
        long ans = 0;
        
        for(char c: word.toCharArray()){
            prefixPattern ^= (1L << (c - 'a'));  //flip the bit corresponding to current character

            // Case-1: Even pattern
            ans += map.getOrDefault(prefixPattern, 0L);

            //Case-2: 1 Odd pattern
            for(char ch = 'a'; ch <= 'j'; ch++){
                long oddPattern = prefixPattern ^ (1L << (ch - 'a'));
                ans += map.getOrDefault(oddPattern, 0L);
            }

            //Update freq. of this pattern
            map.put(prefixPattern, map.getOrDefault(prefixPattern, 0L) + 1);
        }
        return ans;
     }

     //Approach-4: Faster(Array instead of HashMap)
     // TC = O(n*10){each ch -> 10 iterations}, SC = O(2^10) {At most 1024 different bitmasks stored}
     public long wonderfulSubstrings(String word) {
        long[] cnt = new long[1024];  // stores how many times each bitmask pattern occurs
        cnt[0] = 1;                   // empty prefix pattern (all even)
        
        int mask = 0;                 // current parity pattern (10 bits)
        long ans = 0;
        
        for (char c : word.toCharArray()) {
            // Toggle the bit corresponding to the current character
            mask ^= 1 << (c - 'a');
            
            // Case 1: same mask → substring with all even counts
            ans += cnt[mask];
            
            // Case 2: one bit flipped → substring with exactly one odd count
            for (int i = 0; i < 10; i++) {
                ans += cnt[mask ^ (1 << i)];
            }
            
            // Record this prefix pattern
            cnt[mask]++;
        }
        
        return ans;
    }

    //Appraoch-5: Fastest
    // TC = O(n*10), SC = O(2^10) {At most 1024 different bitmasks stored}
    /*
    1. Clever micro-optimization: freq[mask]++ does two things at once:
       - Adds the old count (before increment) to res (→ counts all-even substrings).
       - Then immediately increments the count for future iterations.
       - On hot loops, that’s a measurable speed gain(removes one array access and one temporary variable.)
    2.  for (int i = 1; i <= 512; i <<= 1) iterates only 10 times (1, 2, 4, 8, ..., 512) — one per bit for a–j.
       - Using i <<= 1 is faster than looping from 0–9 and computing (1 << i) each time.
       - Pure integer bit shifts are among the fastest operations on modern CPUs.
    3. static { ... } block runs a small dummy loop when the class is first loaded.
       - Java JIT compiler (Just-In-Time compiler) needs a few runs of a method before it compiles it into native optimized machine code.
       - By pre-calling the method 100 times:
         a. JVM “learns” which branches, loops, and array accesses are hot.
         b. It inlines, unrolls, and optimizes those code paths before real test inputs arrive.
         c. Later, the method runs at native CPU speed (like C/C++).
     */ 
    static {
        for (int i = 0; i < 100; i++) {
            wonderfulSubstrings5("aaa");
        }
    }
    public static long wonderfulSubstrings5(String word) {
        long[] freq = new long[1024];
        freq[0] = 1L;

        int mask = 0;
        long res = 0L;
        for (char c : word.toCharArray()) {
            mask ^= 1 << (c - 'a');

            res += freq[mask]++;                     
             
            for (int i = 1; i <= 512; i <<= 1)        
                res += freq[mask ^ i];
        }

        return res;
    }
}