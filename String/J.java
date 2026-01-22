// LC. 3816. Lexicographically Smallest String After Deleting Duplicate Characters: https://leetcode.com/problems/lexicographically-smallest-string-after-deleting-duplicate-characters/description/

public class J{
    // Approach-1(65 ms): Greedy with Stack(Monotonic):
    // TC = O(n){Each character is pushed and popped from the stack at most once}; SC = O(26) = O(1)
    /*
    1. StringBuilder Functioning as Monotonic stack:
       - push(c) -> sb.append(c)
       - pop() -> sb.setLength(sb.length() - 1)
       - top() -> sb.charAt(sb.length() - 1)
       - A monotonic increasing stack means: from left → right in sb, characters tend to be non-decreasing (a ≤ b ≤ c ≤ ...)
    2. Greedy Choice:
       - at each step, make the best local decision that leads to a global optimum.
       - When you see a new character c, you try to make the result smaller by removing a bigger previous character t if it is safe to remove it.
       - If t > c, then replacing t with c earlier makes the string lexicographically smaller.
       - But only do it if you won’t “lose” t completely.
    3. The final while loop:
       - removing extra duplicates at the end always makes the string lexicographically smaller.
    */
public String lexSmallestAfterDeletion1(String s) {
        char[] a = s.toCharArray();
        int[] rem = new int[26];         // future remaining count of each character
        for (char c : a) rem[c - 'a']++;

        int[] cnt = new int[26];        // current count of each character in the StringBuilder
        StringBuilder sb = new StringBuilder();

        for (char c : a) {
            rem[c - 'a']--;

            while (sb.length() > 0) {
                char t = sb.charAt(sb.length() - 1);

                if (t > c && (rem[t - 'a'] > 0 || cnt[t - 'a'] > 1)) {
                    // rem[t] > 0 -> t will appear later again
                    // cnt[t] > 1 -> t has appeared more than once already, so removing one copy is still okay
                    cnt[t - 'a']--;
                    sb.setLength(sb.length() - 1);
                } else break;
            }
            sb.append(c);
            cnt[c - 'a']++;
        }

        // final cleanup loop
        while (sb.length() > 0) {
            int i = sb.length() - 1;
            char c = sb.charAt(i);
            if (cnt[c - 'a'] > 1) {
                cnt[c - 'a']--;
                sb.setLength(i);
            } else break;
        }

        return sb.toString();
    }


    // Approach-2(24 ms): Greedy + Monotonic Stack(implemented in-place using the input char array as the stack)
    // TC = O(n); SC = O(26) = O(1)
    /*
    1. Working string: stack + remaining suffix combined
    2. Optimization:
       - Instead of maintaining two separate data structures (stack and remaining suffix), we can optimize space by using a single array to represent the working string.
       - We use an integer top to track the end of the working string within the array.
       - no StringBuilder resizing, no extra counting updates
    3. In-place stack = better memory usage
       - input array itself becomes the stack buffer.
    */

    public String lexSmallestAfterDeletion2(String s) {
        char[] a = s.toCharArray();
        int[] f = new int[26];      // current frequency in the current “working string”
        for (char c : a) f[c - 'a']++;

        int top = -1;
        for (int i = 0; i < a.length; i++) {
            char c = a[i];
            while (top >= 0 && c < a[top] && f[a[top] - 'a'] > 1) {
                // c < a[top] → popping makes the string lexicographically smaller
                // f[topChar] > 1 → safe to delete because that char still appears at least twice in the current string
                f[a[top--] - 'a']--;
            }
            a[++top] = c;
        }

        while (top >= 0 && f[a[top] - 'a'] > 1) {
            f[a[top--] - 'a']--;
        }

        return new String(a, 0, top + 1);
    }

}