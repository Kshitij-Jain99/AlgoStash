// LC. 3922. Minimum Flips to Make Binary String Coherent: https://leetcode.com/problems/minimum-flips-to-make-binary-string-coherent/description/

public class E{

    // Approach-1: Constructive Greedy(Enumerate Valid Structures)
    // TC = O(N), SC = O(1)
    /*
       1. Instead of fixing the string blindly, Finding always safe strings
       2. Safe strings: 
          A. All 1's: 111...11
          B. All 0's: 00.....00
          C. Exatly cntOne is 1: 000..1000
          D. Two 1's at boundaries: 100...0001
       3. Convert the given string into each valid form and take the minimum cost.          
    */
    public int minFlips1(String s) {
        int n = s.length();
        if (n < 3) return 0;

        int ones = 0;
        for (char c : s.toCharArray()) {
            if (c == '1') ones++;
        }
        int zeros = n - ones;

        // Case 1,2: all 1s/0s
        int ans = zeros;

        // Case 3: exactly one '1'
        ans = Math.min(ans, ones - 1);

        // Case 4: 1...0...1
        int cost = 0;
        if (s.charAt(0) == '0') cost++; // first 0 flip to 1
        if (s.charAt(n - 1) == '0') cost++;   // last 0 flip to 1
        for (int i = 1; i < n - 1; i++) {     // mid 1's flip to 0's
            if (s.charAt(i) == '1') cost++;
        }

        ans = Math.min(ans, cost);

        return ans;
    }


    // Approach-2: Greedy (Count-Based Optimization)
    // TC = O(N), SC = O(1)
    /*
        1. Instead of explicitly trying all shapes
           - count 1s and 0s
           - encode all cases into a single formula
        2. Instead of calculating for one 1 and Two 1's at boundaries, Combine them.
    */
    public int minFlips2(String s) {
        int n = s.length();
        int ones = 0;

        for (char c : s.toCharArray()) {
            if (c == '1') ones++;
        }
        int zeros = n - ones;

        int endsAre1 = (s.charAt(0) == '1' && s.charAt(n - 1) == '1') ? 1 : 0; // keep <=2 ones

        return Math.min(zeros, ones - 1 - endsAre1);
    }
}