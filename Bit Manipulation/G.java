//LC.1125. Smallest Sufficient Team: https://leetcode.com/problems/smallest-sufficient-team/description/

import java.util.*;

public class G {
    
    //Approach-1: Top-Down DP (Recursion + Memoization + Bitmasking)
    //TC = O(N * 2^m){recursion overhead}, SC = O(N * 2^m); N = People, m = Skills

    // Maximum constraints — up to 16 skills and up to 64 people
    int[][] dp = new int[1 << 16][64];        
    boolean[][] choice = new boolean[1 << 16][64];  //true if we take i-th person

    int reqMask;   // mask representing all required skills
    int m;         

    public int[] smallestSufficientTeam1(List<String> req_skills, List<List<String>> people_skills) {
        int n = req_skills.size();
        m = people_skills.size();

        // Step 1: Map each skill to a unique bit position (0 to n-1)
        Map<String, Integer> skillIndex = new HashMap<>();
        for (int i = 0; i < n; i++) {
            skillIndex.put(req_skills.get(i), i);
        }

        // Step 2: Convert each person's skill list to a bitmask
        int[] people = new int[m];
        for (int i = 0; i < m; i++) {
            int mask = 0;
            for (String skill : people_skills.get(i)) {
                if (skillIndex.containsKey(skill)) {
                    mask |= 1 << skillIndex.get(skill);
                }
            }
            people[i] = mask; // store skill bitmask for person i
        }

        // Step 3: Required skills mask (all skills set to 1)
        reqMask = (1 << n) - 1;

        // Step 4: Initialize DP and choice arrays
        for (int[] row : dp) Arrays.fill(row, -1);
        for (boolean[] row : choice) Arrays.fill(row, false);

        // Step 5: Start recursive DP from person index 0 and empty mask 0
        solve1(people, 0, 0);

        // Step 6: Build the result from the choice table
        List<Integer> ans = new ArrayList<>();
        int mask = 0;

        for (int i = 0; i < m; i++) {
            // If this person was chosen in optimal path
            if (choice[mask][i]) {
                ans.add(i);
                mask |= people[i]; // update mask with chosen person's skills
            }
            if (mask == reqMask) break; // stop once all skills are covered
        }

        // Convert answer to array
        return ans.stream().mapToInt(i -> i).toArray();
    }

    private int solve1(int[] people, int i, int mask) {
        if (mask == reqMask) return 0;  //All required skills are covered
        if (i == m) return 10000;       //All people are processed but skills are still missing{large number representing invalid path}

        if (dp[mask][i] != -1) return dp[mask][i];

        int ansTake = 1 + solve1(people, i + 1, mask | people[i]); //Take current person i
        int ansSkip = solve1(people, i + 1, mask);                 //Skip current person i

        dp[mask][i] = Math.min(ansTake, ansSkip);

        if (ansTake < ansSkip) {   // Record choice for reconstruction later
            choice[mask][i] = true;
        }

        return dp[mask][i];
    }


    //Approach-2(Optimal): Bottom-Up DP (Iterative + Tabulation + Bitmasking)
    //TC = O(N * 2^m), SC = O(2^m); N = people
    public int[] smallestSufficientTeam2(String[] req_skills, List<List<String>> people) {
        int n = req_skills.length;
        int m = people.size();
        
        // Map each skill to an index (0 to n-1)
        Map<String, Integer> skillIndex = new HashMap<>();
        for (int i = 0; i < n; i++) skillIndex.put(req_skills[i], i);

        // DP map: key = skill mask, value = list of people indices forming that mask
        Map<Integer, List<Integer>> dp = new HashMap<>();
        dp.put(0, new ArrayList<>()); // no skills covered initially

        // Iterate through each person
        for (int i = 0; i < m; i++) {
            int personMask = 0;
            // Build this person's skill mask
            for (String skill : people.get(i))
                if (skillIndex.containsKey(skill))
                    personMask |= 1 << skillIndex.get(skill);

            // For every current DP state, check what happens if we add this person
            Map<Integer, List<Integer>> newDp = new HashMap<>(dp);
            for (Map.Entry<Integer, List<Integer>> entry : dp.entrySet()) {
                int combined = entry.getKey() | personMask; // new mask after adding this person

                List<Integer> currTeam = entry.getValue();

                // If this combination is new or gives a smaller team, update it
                if (!newDp.containsKey(combined) || newDp.get(combined).size() > currTeam.size() + 1) {
                    List<Integer> newTeam = new ArrayList<>(currTeam);
                    newTeam.add(i);
                    newDp.put(combined, newTeam);
                }
            }
            dp = newDp; // update dp
        }

        // Return the team that covers all skills (mask = (1 << n) - 1)
        List<Integer> result = dp.get((1 << n) - 1);
        return result.stream().mapToInt(x -> x).toArray();
    }



    //Approach-3: Fastest(Bitmask DP with Long Encoding (Optimized Memoization))
    // TC = O(2^n * m){bit-level operations only}, SC = O(2^n)
    /*
     1. This approach compresses the entire problem (skills × people combinations) into binary arithmetic, where:
        - Each state = an integer (mask).
        - Each transition = a few bitwise operations.
        - Each result = a minimal-length binary vector (long).
     2. Approach-1: Skills & People as bitmasks; team stored via boolean choice table
        Approach-2: Skills as bitmasks; team stored as dynamic list
        Approach-3: Both skills and team stored as bitmasks (integers & longs)
     3. Benefit: No object creation, no collection overhead, CPU-level bit ops.
     */
        public int[] smallestSufficientTeam3(String[] reqSkills, List<List<String>> people) {
            Map<String, Integer> map = new HashMap<>();
            for (int i = 0; i < reqSkills.length; i++) map.put(reqSkills[i], i);

            int[] masks = new int[people.size()];
            for (int i = 0; i < people.size(); i++) {
                for (String skill : people.get(i)) {
                    Integer idx = map.get(skill);
                    if (idx != null) masks[i] |= 1 << idx;
                }
            }
            return memoization3(reqSkills, masks);
        }

        private int[] memoization3(String[] req, int[] people) {
            Long[] cache = new Long[1 << req.length];
            long teamMask = memoization3((1 << req.length) - 1, people, cache);
            return toArray3(teamMask, people.length);
        }

        private long memoization3(int mask, int[] people, Long[] cache) {
            if (mask == 0) return 0L;
            if (cache[mask] != null) return cache[mask];

            int skill = Integer.numberOfTrailingZeros(mask);
            long res = (1L << people.length) - 1;

            for (int i = 0; i < people.length; i++) {
                if ((people[i] & (1 << skill)) == 0) continue;
                int remaining = mask & ~people[i];
                long tmp = memoization3(remaining, people, cache) | (1L << i);
                if (Long.bitCount(tmp) < Long.bitCount(res)) res = tmp;
            }

            return cache[mask] = res;
        }

        private int[] toArray3(long teamMask, int n) {
            int[] res = new int[Long.bitCount(teamMask)];
            int idx = 0;
            for (int i = 0; i < n; i++) if ((teamMask & (1L << i)) != 0) res[idx++] = i;
            return res;
        }

    }

