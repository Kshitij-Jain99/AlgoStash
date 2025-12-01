//LC.318. Maximum Product of Word Lengths: https://leetcode.com/problems/maximum-product-of-word-lengths/description/
import java.util.*;
public class C{

    //Approach-1: Brute Force
    //TC = O(N^2 * f(L1,L2)), SC = O(n)
     public int maxProduct(String[] words){
        int n = words.length;
        
        int maxProduct = 0;
        for(int i=0; i<n; i++){
            for(int j = i+1; j<n; j++){
                if(noCommonChars2(words[i], words[j])){
                    maxProduct = Math.max(maxProduct, words[i].length() * words[j].length());
                }
            }
        }
        return maxProduct;
     }
    
     //Approach-A: Navie Solution: O(L1 * L2)
     /*
      1. Checking for each charcter, ensure that this character is not in second word.
      */
     public boolean noCommonChars1(String s1, String s2){ //O( f(L1,L2) )
        for(char ch: s1.toCharArray()){
            if(s2.indexOf(ch) != -1) return false;
        }
        return true;
    }

    //Approach-B: Using Bitmasks: O(L1 + L2)
    /*
     1. words only has lower case english letters -> Each word presence can be encoded with a bitmask of 26 characters
     2. If character is present set bit 1 else 0.
     3. To set nth bit: bitmask |= (1 << n)
        - n is the bit number of that character
     4. This way we can compute two bitmasks, charcacter by character.
     */
     public boolean noCommonChars2(String s1, String s2){
        int bitmask1=0, bitmask2=0;
        for(char ch: s1.toCharArray()) bitmask1 |= 1 << bitNumber2(ch);
        for(char ch: s2.toCharArray()) bitmask2 |= 1 << bitNumber2(ch);

        return (bitmask1 & bitmask2) == 0;
     }

     public int bitNumber2(char ch){
        return (int)ch - (int)'a';
     }


      //Approach-2: Better
      //TC = O(N^2 * n*L), SC = O(n); n*L represents total length of all words together
        //Approach-C: Using Bitmasks + Precomputation: O(1)
        /*
        1. Approach-B computes bitmask of each word N times.
        2. Instead each bitmask can be precomputed once, then memorised and used for runtime comparison in a constant time.
        3. Store bitmasks and string lengths in 2 integer arrays.
        4. Java works faster with arrays than hashmaps.
        5. Compare each word with all following word one by one.
        6. If two words have no common letter, update maxProduct.
        7. Perform "no common letter check" in constant time with help of precomputed masks array.
        */
         public int bitNumber3(char ch){
            return (int)ch - (int)'a';
        }

        public int maxProduct2(String[] words){
            int n = words.length;
            int[] masks = new int[n];   //precomputed bitmasks for all words
            int[] lens = new int[n];

            int bitmask;
            for(int i = 0; i<n; ++i){
                bitmask = 0;
                for(char ch: words[i].toCharArray()){
                    //add character to bitmask
                    bitmask |= 1 << bitNumber3(ch);
                }
                masks[i] = bitmask;
                lens[i] = words[i].length();
            }

           int maxProduct = 0;
           for(int i=0; i<n; i++){
            for(int j = i+1; j<n; j++){
                if((masks[i] & masks[j]) == 0){ //no common letters
                    maxProduct = Math.max(maxProduct, words[i].length() * words[j].length());
                }
            }
        }
        return maxProduct; 
        }


        //Appraoch-3: Using Bitmasks + Precomputation + HashMaps
        //TC = O(U^2 + L); U << n, U is unique bitmasks and  L is total length of all words together
        // SC=O(min(n,2^26))=O(1) (constant upper bound in practice)
        /*
         Idea: 
            - Instead of comparing every pair of words, you first compress all words with the same set of letters into one record — the longest word with that bitmask.
            - That drastically reduces the number of comparisons, because:
                Many words share the same bitmask (e.g. "ab" and "aabb" → same set of letters {a,b}).
                You only need to keep the longest word for that set.
         0. No. of bitmasks are at most 2²⁶, hence O(L) complexity.
            instead of n entries, you get ≤ 2²⁶ possible masks (one per unique character set)
         1. There is no need to perform N^2 comparisons. 
            Among all strings, with same set of letters, it's enough to keep the longest one.
         2. Instead of 2 arrays of len N, using hashmaps
         3. Precompute bitmaks for all words and save them in hashmap bitmask -> max word length with such a bitmask.
         4. Comapre each word with others one by one.
         */

         public int bitNumber4(char ch){
            return (int)ch - (int)'a';
         }

         public int maxProduct4(String[] words){
            Map<Integer, Integer> mpp = new HashMap<>();

            int bitmask = 0, bitNum = 0;
            for(String word: words){
                bitmask = 0;
                for(char ch: word.toCharArray()){
                    //add bitNum in bitmask
                    bitmask |= 1 << bitNumber4(ch);
                }
                //there could be different words with same bitmasks, eg:ab and aabb, so keep max length only
                mpp.put(bitmask, Math.max(mpp.getOrDefault(bitmask,0), word.length()));
            }

            int maxProd = 0;
            for(int x: mpp.keySet()){
                for(int y: mpp.keySet())
                //compare only distinct bitmasks, not every pair
                if((x&y) == 0) maxProd = Math.max(maxProd, mpp.get(x) * mpp.get(y));
            }
            return maxProd;
         }
         /*
          Approach-3 is theoritically faster but practically slower than approach-2 for JAVA.
           - real-world runtime ≠ pure asymptotic performance
           - Why It’s Often Slower in Java:
             1. Java HashMap Overhead (Object-Based)
                - Java’s HashMap<Integer, Integer> stores boxed types:
                  a. Every int key/value is wrapped in an Integer object.
                  b. So each insert and lookup does: Boxing/unboxing conversions, Memory allocations (object headers, references), Hash computation + equality checks on objects
                - In contrast, in C++:
                  a. unordered_map<int, int> stores raw integers → no boxing, no GC overhead.
                  b. Hashing and equality are direct integer operations in native memory.
                - HashMap operations are much cheaper in C++ or primitive-optimized languages than in Java.
             2. Java’s Garbage Collection (GC) and Memory Pressure
                - Because each (bitmask → length) entry involves objects:
                  a. Each Integer key/value is a separate heap object.
                  b. With thousands of entries, you create many short-lived objects.
                  c. GC kicks in frequently to clean them up.
                - This adds small but consistent pauses and CPU work.
                - By contrast, in C++ or Rust, data structures live on the stack or use contiguous memory — no GC overhead.
             3. HashMap Iteration Cost in Java:
                - Java’s keySet() returns a view of keys backed by the HashMap.
                - Iterating it twice like this means:
                  a. Each loop creates an iterator object.
                  b. Iterators are also objects on the heap (unless optimized by JIT).
                  c. Nested loops → more iterator churn.
                - Meanwhile, C++ for(auto &x : mpp) is compiled into direct pointer iteration — zero object creation.
             4. JIT (Just-In-Time) Compilation Behavior:
                - Java’s JIT compiler optimizes “hot” code paths after some iterations.
                - But:
                  a. If the dataset is small, or loops run few times, JIT might not fully optimize.
                  b. HashMap lookups may not inline completely because of virtual method dispatch and boxing.
                - So for short test runs, C++ (compiled ahead of time with full optimizations) or Python (C-implemented dicts) often appear faster.
             5. Smaller Constant Factors in Other Languages:
                - Even though both Java and C++ run in O(U²) time:
                  a. Java has higher constant overhead from object management, GC, and method dispatch.
                  b. C++ and Python’s built-in maps (dict / unordered_map) are lower-level, fewer indirections.
                - So the raw number of CPU instructions per comparison is much smaller outside Java.
            - Thus, in Java, O(U²) comparisons may still take longer wall-clock time even though asymptotically better.
          */

}