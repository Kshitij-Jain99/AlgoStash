//LC.14. Longest Common Prefix: https://leetcode.com/problems/longest-common-prefix/description/

import java.util.Arrays;

public class F{
   
    //Approach-1: Horizontal Scanning:
    // TC = O(S), SC = O(1)
/*
 1. LCP shared by a set of strings LCP(S1, S2, S3, ..., Sn) = LCP(LCP( LCP( LCP(S1, S2), S3), ..., Sn)
 2. Itrate through (S1...Sn), finding at each iteration the longest common prefix of strings LCP(S1..Si).
 3. S is sum of all charcters in all strings.
    In worst case if n strings are same then there are S character comparisons.
 */
  public String longestCommonPrefix1(String[] strs){
    if(strs.length == 0) return "";
    String prefix = strs[0];  //assuming first string is the prefix

    for(int i = 1; i<strs.length; i++){
        while(strs[i].indexOf(prefix) != 0){
            prefix = prefix.substring(0, prefix.length()-1);
            if(prefix.isEmpty()) return "";
        }
    }
    return prefix;
  }

    //Approach-2: Vertical Scanning:
    // TC = O(S), SC = O(1)
/*
 1. Compare characters from top to bottom on same column(same character idx of strings) before moving to next column.
 2. If a very short string is common prefix at end of array, horizonatal scanning will do S comparisons but this appraoch is better.
 3. In average case, n.minLen comaprisons will happen.
 */
  public String longestCommonPrefix2(String[] strs){
    if(strs == null || strs.length == 0) return "";

    for(int i = 0; i<strs[0].length(); i++){ //vertical idx comparison
        for(int j = 1; j<strs.length; j++){ //comparing elements
            if(i == strs[j].length() || strs[j].charAt(i) != strs[0].charAt(i)){
                return strs[0].substring(0,i);
            }
        }
    }
    return strs[0];
  }

  //Appraoch-3: Lexiographical Sorting
  // TC = O(n.logn + m) m is len of shortest string, SC = O(1)
  /*
   1. After sorting, no need to check middle strings.
   2. Compare only the first and last strings in the sorted order.
   3. The common prefix of these two will be the longest common prefix of the entire array.
   */
  public String longestCommonPrefix3(String[] strs) {
    if (strs == null || strs.length == 0) return "";

    Arrays.sort(strs); // Sort the array lexicographically
    String first = strs[0];
    String last = strs[strs.length - 1];
    
    int minLen = Math.min(first.length(), last.length());
    int i = 0;
    while (i < minLen && first.charAt(i) == last.charAt(i)) {
        i++;
    }
    
    return first.substring(0, i);
}

    //Approach-4: Divide and Conquer:
    // TC = O(S), SC = O(m.logn)
/*
 1. Associative property of LCP operation:
    LCP(S1...Sn) = LCP(LCP(S1...Sk), LCP(S(k+1)...Sn))
 2. Splitting LCP(Si...Sj) into two subproblems: LCP(Si...Smid) and LCP(S(mid+1)...Sj)
    where, mid = (i+j)/2
    Using solutions of these problems to construct the main problem.
 3. Comparing character by character of lcpLeft and lcpRight, till there is no character match.
 4. The found common prefix of lcpLeft and lcpRight is solution of LCP(Si...Sj)
*/
  public String longestCommonPrefix4(String[] strs){
    if(strs == null || strs.length == 0) return "";
    return longestCommonPrefix(strs, 0, strs.length-1);
  }

  private String longestCommonPrefix(String[] strs, int l, int r){
    if(l == r) return strs[l];
    else {
        int mid = (l+r)/2;
        String lcpLeft = longestCommonPrefix(strs, l, mid);
        String lcpRight = longestCommonPrefix(strs, mid+1, r);
        return commonPrefix(lcpLeft, lcpRight); 
    }
  }

  String commonPrefix(String left, String right){
    int min = Math.min(left.length(), right.length());
    for(int i =0; i<min; i++){
        if(left.charAt(i) != right.charAt(i)) return left.substring(0,i);
    }
    return left.substring(0, min);
  }

    //Approach-5: Binary Search:
    // TC = O(S.logm) {logm iterations for each of them S = m.n comaprisons}, SC = O(1)
/*
 1. The core idea is that if a prefix of length k is common to all strings, then any prefix shorter than k must also be common.
 2.  The longest possible common prefix cannot be longer than the shortest string in the array.
    Each time search space is divided into two equal parts.
    Space: [1..minLen] (the length of the shortest string)
 3. There are two possible cases:
    a. S[1...mid] is not common string. This means for each j > i in S[1..j] is not common string and we discard the second half of search sapce.
    b. S[1...mid] is common string. This means taht for each i < j in S[1...i] is common string and we discard the first half of search space, as we try to find longer prefix.
 4. 
    */
  public String longestCommonPrefix5(String[] strs){
     if(strs == null || strs.length == 0) return "";
     int minLen = Integer.MAX_VALUE;
     for(String str: strs)  minLen = Math.min(minLen, str.length());
        
     int low = 1;
     int high = minLen;
        while(low <= high){
            int middle = (low + high)/2;
            if(isCommonPrefix(strs, middle)) low = middle + 1;
            else high = middle - 1;
        }
         return strs[0].substring(0, (low + high)/2);
  }

  private boolean isCommonPrefix(String[] strs, int len){ //Is a prefix of this specific len common to all strings?
    String str1 = strs[0].substring(0, len);
    for(int i =1; i<strs.length; i++){
        if(!strs[i].startsWith(str1)) return false;
    }
    return true;
  }

  //Approach-6:  Trie
  //TC = O(S), SC = O(S)
  /*
   1. Slighlty different Problem:
      Given a set of keys S = {S1,S2....Sn}, find the longest common prefix among a string q and S.The LCP query will be called frequently.
   2. Optimize LCP queries by storing set of keys in trie.
   3. In Trie, each node descending from root represents a common prefix of some keys.
      But we need to find longest common prefix of a string q and all key strings.   
   4. So find the deepest path from root, which satisfies:
      a. it is a prefix of a query string q
      b. each node along the path must contain only one child element. Otherwise the found path will not be a common prefix among all strings.
      c. The path doesn't comprise of nodes which are marked as end of key. Otherwise the path couldn't be a prefix of a key which is shorter than itself.
   5. TC: Preprocessing O(S), LCP Query O(m) 
   */
  class Solution {
    class Node {
        Node[] children = new Node[26];
        boolean eow = false;
        int freq;
        public Node() {
            for (int i = 0; i < children.length; i++) {
                children[i] = null;
            }
            freq = 1;
        }
    }

    public Node root = new Node();

     public void insert(String word){
        Node curr = root;
        for (int i = 0; i < word.length(); i++) {
            int idx = word.charAt(i) - 'a';
            if (curr.children[idx] == null) {
                curr.children[idx] = new Node();
            } else{
                curr.children[idx].freq++;
            }
            curr = curr.children[idx];
        }
        curr.eow = true;
    }

    public String search(String key,int sz){
        Node curr = root;
        int count = 0;
        for (int level = 0; level < key.length(); level++) {
            int idx = key.charAt(level) - 'a';
            if(curr.children[idx].freq == sz){
                count++;
            } else {
                break;
            }
            curr = curr.children[idx];
        }
        return key.substring(0 , count);
    }
    public String longestCommonPrefix6(String[] strs) {
        for (int i = 0; i < strs.length; i++) {
            insert(strs[i]);
        }
        return search(strs[0],strs.length);
    }
}
}