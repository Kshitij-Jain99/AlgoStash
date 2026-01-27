// LC. 35. Non-overlapping Intervals: https://leetcode.com/problems/non-overlapping-intervals/description/
import java.util.*;
public class A {

    // Approach-1(46 ms): Greedy + Sorting by end Time
    // TC = O(n.log(n)), SC = O(1)
    /*
        1. Sort the intervals by their end time.
            - Keeping the one that ends earlier: Blocks less future space, Maximizes chances of fitting more intervals later
            - Sorting by start time does not guarantee this.
        2. Iterate and select intervals that do not overlap with the last selected interval.
        3. If an ovelap exist with previous interval, always drop current interval.
        4. Cases: (after sorting)
           a. No overlap -> All intervals are compatible
           b. Partial overlap -> choose the one that ends earlier
           c. One interval fully inside another -> Shorter intervals survive, long one is removed
           d. Same end time -> Any one is fine — they all block the same future space.
           e. Touching intervals -> no overlap, both can be included (currentStart >= lastEnd)
              If your problem treats touching as overlap, change condition to: currentStart > lastEnd
    */
    public int eraseOverlapIntervals1(int[][] intervals){
        int n = intervals.length;
        if(n == 0) return 0;

        Arrays.sort(intervals, (a,b) -> Integer.compare(a[1], b[1])); //sort by end time

        int maxAct = 1;
        int endLast = intervals[0][1];
        for(int i = 1; i < n; i++){
            if(intervals[i][0] >= endLast){
                maxAct++;
                endLast = intervals[i][1];
            }
            }            
        return intervals.length - maxAct;
    }

    
    // Approach-2(48 ms): Greedy + Sorting by Start Time
    // TC = O(n.log n), SC = O(1)
/*
    Key difference from end-time sorting:
    - Sorting by start time alone is NOT sufficient.
    - When an overlap occurs, we must explicitly decide which interval to remove.

    1. Sort intervals by start time.
    2. Keep track of the previously selected interval (prev).
    3. When overlap occurs:
        - Remove one interval.
        - Always keep the interval that ends earlier
          (blocks less future space).
    4. Count how many intervals are removed.

    Why "keep smaller end" works:
    - Between two overlapping intervals, the one with
      the smaller end leaves more room for future intervals.
*/
public int eraseOverlapIntervals2(int[][] intervals) {
    int n = intervals.length;
    if (n == 0) return 0;

    // Step 1: sort by start time
    Arrays.sort(intervals, (a, b) -> Integer.compare(a[0], b[0]));

    int removals = 0;
    int prev = 0;                 // index of interval currently kept

    // Step 2: iterate from second interval
    for (int i = 1; i < n; i++) {

        // Case: overlap exists
        if (intervals[i][0] < intervals[prev][1]) {
            removals++;

            // Step 3: keep the interval that ends earlier
            if (intervals[i][1] < intervals[prev][1]) {
                prev = i;
            }
        }
        // Case: no overlap → safely move forward
        else {
            prev = i;
        }
    }
    return removals;
}


    // Appraoch-3(6 ms): Greedy + Counting Sort
    // TC = O(n + R), SC = O(R), where R[maxEnd - minEnd]
    /*
        1. This approach avoids sorting altogether.
        2. Find global min & max end times to define range.
           - Interval end points can be negative or large
           - We want to compress them into array indices
        3. Shift coordinates to make indices non-negative
           - smallest end time maps to index 1
           - all indices are ≥ 1  
           - Ends: [-3, 2, 5], shift = 1 - (-3) = 4 => Shifted ends: [1, 6, 9]
        4. Create the “rightEnds” bucket array
           -  index i represents end time = i
           -  It stores the maximum start time among intervals that end at i
        5. Populate the buckets
           - Multiple intervals may end at the same time
           - Among them, we only care about the one with the largest start, because it overlaps less with previous intervals
           -  This is a local greedy optimization.
        6. Greedy selection over end times
           - We scan end times in increasing order
           - If we can start before or at rightEnds[i], we select this interval
           - Then update start to the current end time          
        7.  This approach:
            - Faster than sorting when range is small (R << n log n) -> O(n + R)  <<  O(n log n)
            - Has No comparator, no sorting overhead
            - Still optimal greedy, Just via counting + compression
            - When this is NOT ideal:
              * When range R is huge compared to n (e.g., intervals with very large end times but few intervals)
              * In such cases, O(n log n) sorting may be better than O(n + R) counting sort
    */
    public int eraseOverlapIntervals3(int[][] intervals) {
        int max = intervals[0][1];
        int min = max;
        for (int i = 1; i < intervals.length; i++) {
            max = Math.max(max, intervals[i][1]);
            min = Math.min(min, intervals[i][1]);
        }

        int shift = 1 - min;

        int[] rightEnds = new int[max - min + 2];

        for (int[] interval : intervals) {
            int left = interval[0] + shift;
            int right = interval[1] + shift;
            if (rightEnds[right] < left) rightEnds[right] = left;
        }

        int count = 0;  // max number of non-overlapping intervals
        int start = 0;
        for (int i = 1; i < rightEnds.length; i++) {
            if (start <= rightEnds[i]) {
                count++;
                start = i;
            }
        }
        return intervals.length - count;  // removals
    }
}

