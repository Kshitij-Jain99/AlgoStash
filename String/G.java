//LC.127. Word Ladder: https://leetcode.com/problems/word-ladder/description/
import java.util.*;

public class G{
      
    //Appraoch-1: Standard Single direction BFS(Intuition:Level Order Traversal)
    //TC = O(N*M*26 * 1{Set}), SC = O(N)
    /*
     1. Replacing starting word character by character and then check whether the transformed word is present in the wordList.
        If a word is present in the wordList, we try replacing another character in that word by again following similar steps as above, in order to attain the targetWord.
        We do this for all the characters in the startWord and then eventually return the minimum length of transforming the startWord to targetWord.
     2. Implementing BFS to make this efficient.
     3. Set:  In order to mark a word as visited here, we simply delete the word from the hash set. There is no point in visiting someone multiple times during the algorithm. 
     4. pop the first element out of the queue and carry out the BFS traversal
        for each word popped out of the queue, we try to replace every character with ‘a’ - ‘z’, and we get a transformed word and check its presence in set.
     5. If the word is present, we push it in the queue and increase the count of the sequence by 1 and if not, we simply move on to replacing the original character with the next character.
     6. we also need to delete the word from the wordList if it matches with the transformed word to ensure that we do not reach the same point again in the transformation which would only increase our sequence length. 
     7. BFS From begining only.
     */

     class Pair{
        String first;
        int second;
        Pair(String _first, int _second){
            this.first = _first;
            this.second = _second;
        }
     }
     
     public int wordLadderLength(String startWord, String targetWord, String[] wordList){
        Queue<Pair> q = new LinkedList<>();  //Store BFS Transversal(word, steps)
        q.add(new Pair(startWord, 1));  //push start word with 1 length

        Set<String> st = new HashSet<>();  //to store elements present in wordList to carry out search and delete operations in O(1) time
        int len = wordList.length;
        for(int i = 0; i<len; i++) st.add(wordList[i]);
        st.remove(startWord);
        while(!q.isEmpty()){
            String word = q.peek().first;
            int steps = q.peek().second;
            q.remove();

            //return as soon as we reach the targetWord
            if(word.equals(targetWord) == true) return steps;

            //replace each character of the word with every possible character from 'a' to 'z' and check if the new word is present in the set
            for(int i = 0; i<word.length(); i++){
                for(char ch = 'a'; ch <= 'z'; ch++){
                    char replacedCharArray[] = word.toCharArray();
                    replacedCharArray[i] = ch;
                    String replacedWord = new String(replacedCharArray);
                    
                    if(st.contains(replacedWord) == true){
                        st.remove(replacedWord);
                        q.add(new Pair(replacedWord, steps+1));
                    }
                }
            }
        }
        return 0;
     }


    //Approach-2: Bidirectional BFS(Search from both ends): https://leetcode.com/problems/word-ladder/solutions/6987617/video-bidirectional-bfs-solution/
    //TC = O(N*M*26 * 1{Set}), SC = O(N)
    /*
     1. Faster in practice(Shrinks search space, better constant factors,Much faster on large test cases,Meets in the middle, so fewer expansions)
     2. Instead of searching only from the beginWord → endWord, it searches from both ends simultaneously (meet in the middle).
     3. Maintain two sets: beginSet and endSet.
     4. Expand the smaller set at each step (to minimize branching).
        Direction Selection Strategy: When sets have equal size, default to begin_set. Always search from the smaller (or equal) set to maximize efficiency.
     5. Generate all possible one-letter transformations for each word in the current frontier.
        Check for new word:
        a. Check for intersection with end_set
        b. Check for duplicates: Visited or not in wordList
     6. If a generated word exists in the opposite set → path found.
     7. Use a visited set to avoid revisiting.
     8. Termination condition for bidirectional search is finding a word which is already been seen by the parallel search.
     9. The shortest path is sum of levels of the meet point node from both ends.
     */
      public int ladderLength2(String beginWord, String endWord, List<String> wordList) {
        Set<String> wordSet = new HashSet<>(wordList);
        if (!wordSet.contains(endWord)) return 0;

        Set<String> beginSet = new HashSet<>();
        Set<String> endSet = new HashSet<>();
        Set<String> visited = new HashSet<>();

        beginSet.add(beginWord);
        endSet.add(endWord);
        int steps = 1;

        while (!beginSet.isEmpty() && !endSet.isEmpty()) {
            if (beginSet.size() > endSet.size()) {
                Set<String> temp = beginSet;
                beginSet = endSet;
                endSet = temp;
            }

            Set<String> nextSet = new HashSet<>();

            for (String word : beginSet) {
                for (int i = 0; i < word.length(); i++) {
                    for (char c = 'a'; c <= 'z'; c++) {
                        if (c == word.charAt(i)) continue;

                        String newWord = word.substring(0, i) + c + word.substring(i + 1);
                        if (endSet.contains(newWord)) return steps + 1;

                        if (wordSet.contains(newWord) && !visited.contains(newWord)) {
                            visited.add(newWord);
                            nextSet.add(newWord);
                        }
                    }
                }
            }

            beginSet = nextSet;
            steps++;
        }

        return 0;        
    }



    /*----------------------------------------------------------------------------------------------------- */

    //126. Word Ladder II: https://leetcode.com/problems/word-ladder-ii/description/

    //Approach-1{TLE}: BFS with path tracking
    // TC = O(N*L + K*L), SC = O(N*L + K*L)
    /* TC:
       a. Main BFS: 
          In the worst case, we try to transform every word at every step.
          For a word of length L and dictionary size N:
          1 word -> 26*L new words
          Processing each word once per BFS level
          So, upper bound = O(N * L * 26) = O(N * L)
       b. Path Tracking:
          Every time we extend a path, we create a new ArrayList.
          Each path can be up to length N in the worst case (but realistically ≤ shortest path length).
          If there are K shortest sequences, path copying cost = O(K × L).
       where
       N = number of words in the wordList
       L = word length
       K = number of shortest transformation sequences found
       
       K can be large in cases with many equivalent paths.

       The worst-case time and space complexities cannot be pinned to a constant bound because the number of possible sequences (K) depends on the input.
       Since K (the number of shortest paths) is part of the output, the algorithm is output-sensitive.
     */
    /*
     1. Intuition for BFS: we go on replacing the characters one by one which seems just like we’re moving level-wise in order to reach the destination i.e. the targetWord.
     2. Contrary to the previous problem, here we do not stop the traversal on the first occurrence of the targetWord, but rather continue it for as many occurrences of the word as possible as we need all the shortest possible sequences in order to reach the destination word.
     3. The only trick here is that we do not have to delete a particular word immediately from the wordList even if during the replacement of characters it matches with the transformed word. 
        Instead, we delete it after the traversal for a particular level when completed which allows us to explore all possible paths. This allows us to discover multiple sequences in order to reach the targetWord involving similar words. 
     4. 
     */
 public List<List<String>> findSequences(String startWord, String targetWord, String[] wordList) {

        Set<String> dict = new HashSet<>(Arrays.asList(wordList)); //store the elements present in the word list to carry out the search and delete operations in O(1) time. 

        // Early exit if targetWord not in dict
        if (!dict.contains(targetWord)) {
            return new ArrayList<>();
        }

        Queue<List<String>> queue = new LinkedList<>(); //store the level-wise formed sequences(List of strings, which will be representing the path till now.)
        List<String> startPath = new ArrayList<>();
        startPath.add(startWord);
        queue.add(startPath);

        Set<String> usedOnLevel = new HashSet<>();  //store the words which are currently being used for transformation on a particular level
        usedOnLevel.add(startWord);

        int level = 1;
        List<List<String>> ans = new ArrayList<>();

        while (!queue.isEmpty()) {
            List<String> path = queue.poll();

            // If we reached a new level, remove used words
            if (path.size() > level) {
                for (String word : usedOnLevel) {
                    dict.remove(word);
                }
                usedOnLevel.clear();
                level = path.size();
            }

            String lastWord = path.get(path.size() - 1);

            // If target found, add path to results
            if (lastWord.equals(targetWord)) {
                if (ans.isEmpty()) {
                    ans.add(path);
                } else if (ans.get(0).size() == path.size()) {
                    ans.add(path);
                }
            }

            // Generate all possible one-letter transformations
            char[] wordArr = lastWord.toCharArray();
            for (int i = 0; i < wordArr.length; i++) {
                char originalChar = wordArr[i];
                for (char c = 'a'; c <= 'z'; c++) {
                    if (c == originalChar) continue; // skip same char
                    wordArr[i] = c;
                    String newWord = new String(wordArr);

                    if (dict.contains(newWord)) {
                        List<String> newPath = new ArrayList<>(path);
                        newPath.add(newWord);
                        queue.add(newPath);
                        usedOnLevel.add(newWord);
                    }
                }
                wordArr[i] = originalChar; // restore
            }
        }

        // Sort results lexicographically
        ans.sort((a, b) -> {
            for (int i = 0; i < Math.min(a.size(), b.size()); i++) {
                int cmp = a.get(i).compareTo(b.get(i));
                if (cmp != 0) return cmp;
            }
            return Integer.compare(a.size(), b.size());
        });

        return ans;
    }


    //Appraoch-2: BFS + Backtracking: https://leetcode.com/problems/word-ladder-ii/solutions/7121152/easy-solution-bfs-dfs-o-n-m-cpp-java-python/
    // TC = O(), SC = O()
   String b;
    Map<String,Integer> mpp = new HashMap<>();
    List<List<String>> ans = new ArrayList<>();

    private void dfs(String word, List<String> seq) {
        if (word.equals(b)) {
            List<String> temp = new ArrayList<>(seq);
            Collections.reverse(temp);
            ans.add(temp);
            return;
        }
        int steps = mpp.get(word);
        char[] arr = word.toCharArray();
        for (int i = 0; i < arr.length; i++) {
            char org = arr[i];
            for (char ch = 'a'; ch <= 'z'; ch++) {
                arr[i] = ch;
                String nxt = new String(arr);
                if (mpp.containsKey(nxt) && mpp.get(nxt) + 1 == steps) {
                    seq.add(nxt);
                    dfs(nxt, seq);
                    seq.remove(seq.size() - 1);
                }
            }
            arr[i] = org;
        }
    }

    public List<List<String>> findLadders(String st, String tar, List<String> wordList) {
        Set<String> s = new HashSet<>(wordList);
        if (st.equals(tar)) {
            List<List<String>> res = new ArrayList<>();
            res.add(Arrays.asList(st));
            return res;
        }
        if (!s.contains(tar)) return new ArrayList<>();

        Queue<String> q = new LinkedList<>();
        q.add(st);
        mpp.put(st, 1);
        b = st;
        boolean found = false;

        while (!q.isEmpty() && !found) {
            int sz = q.size();
            for (int k = 0; k < sz; k++) {
                String cur = q.poll();
                int steps = mpp.get(cur);
                char[] arr = cur.toCharArray();
                for (int i = 0; i < arr.length; i++) {
                    char org = arr[i];
                    for (char ch = 'a'; ch <= 'z'; ch++) {
                        arr[i] = ch;
                        String nxt = new String(arr);
                        if (s.contains(nxt) && !mpp.containsKey(nxt)) {
                            mpp.put(nxt, steps + 1);
                            if (nxt.equals(tar)) found = true;
                            q.add(nxt);
                        }
                    }
                    arr[i] = org;
                }
            }
        }

        if (mpp.containsKey(tar)) {
            List<String> seq = new ArrayList<>();
            seq.add(tar);
            dfs(tar, seq);
        }
        return ans;
    }

    //Appraoch-3: BFS + Backtracking
    // TC = O(), SC = O()
    
}