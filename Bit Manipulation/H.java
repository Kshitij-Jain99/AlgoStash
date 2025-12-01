//LC.3180, 3181. Maximum Total Reward Using Operations I, II: https://leetcode.com/problems/maximum-total-reward-using-operations-ii/description/
import java.util.*;

public class H{
    //Approach-1: Top Down DP + Memoization
    // TC = O(n*maxReward), SC = O(n*maxReward + n{Rec. stack})
    int n;
    int[][] dp;

    public int maxTotalReward1(List<Integer> rewValues){
        n = rewValues.size();
        Collections.sort(rewValues);

        int maxRew = rewValues.get(rewValues.size() - 1);
        dp = new int[n+1][maxRew+1];
        for(int[] row : dp) Arrays.fill(row, -1);

        return solve1(0,0, rewValues);
    }

    int solve1(int i , int rewSum, List<Integer> rewValues){
        if(rewSum > rewValues.get(rewValues.size() - 1)) return rewSum;
        if(i == n) return rewSum;

        if(dp[i][rewSum] != -1) return dp[i][rewSum];

        int take = (rewValues.get(i) > rewSum) ? solve1( i + 1, rewSum + rewValues.get(i), rewValues) : rewSum;
        int notTake = solve1(i + 1, rewSum, rewValues);

        return dp[i][rewSum] = Math.max(take, notTake);
    }

    //Approach-2: BitSet DP(TLE for java but accepted in cpp, python)
    //TC = O(), SC = O()

}