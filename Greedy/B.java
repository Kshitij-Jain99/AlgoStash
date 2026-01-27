// Meeting Rooms II: https://neetcode.io/problems/meeting-schedule-ii/question
import java.util.*;

 //Definition of Interval:
  class Interval {
     public int start, end;
     public Interval(int start, int end) {
         this.start = start;
         this.end = end;
     }
 }

public class B {
    
    // Approach-1: Greedy
    // TC = O(n.log(n)), SC = O(n)
    /*
        1. Finding "maximum number of meetings happening at the same time"
        2. This greedy approach works by:
           - converting each meeting into two events (start and end)
           - sorting all events by time
           - sweeping from left to right while counting active meetings
    */
     public int minMeetingRooms1(List<Interval> intervals){
        List<int[]> time = new ArrayList<>();
        for(Interval i : intervals){
            time.add(new int[]{i.start, 1});   // Meeting starts
            time.add(new int[]{i.end, -1});    // Meeting ends
        }

        time.sort((a,b) -> a[0] == b[0] ? a[1] - b[1] : a[0] - b[0]);

        int res = 0;         // current number of ongoing meetings
        int count = 0;       // maximum number of rooms needed
        for(int[] t : time){
            count += t[1];
            res = Math.max(res, count);
        }
        return res;
     }

    
    // Approach-2: Min Heap
    // TC = O(n.log(n)), SC = O(n)
    /*
        1. Minimum number of meeting rooms required so that no meetings overlap.
        2. To efficiently track room availability, we use a min heap:
           - the heap stores the end times of meetings currently occupying rooms
           - the smallest end time is always at the top, representing the room that frees up the earliest
        3. As we process meetings in order of start time:
           - if the earliest-ending meeting finishes before the current one starts, we can reuse that room
           - otherwise, we must allocate a new room
        4.  The maximum size the heap reaches is the number of rooms needed.
        5. Activity Selection : Select the maximum number of non-overlapping meetings using ONE room -> End time sorting
           Meeting Rooms : Schedule ALL meetings using the minimum number of rooms -> Start time sorting + Min-Heap
        6. End-time greedy works when you choose meetings.
           Start-time greedy is required when meetings choose you.   
    */
    public int minMeetingRooms2(List<Interval> intervals){
        intervals.sort((a,b) -> a.start - b.start);                 // Sort by start time
        PriorityQueue<Integer> minHeap = new PriorityQueue<>();    // store meeting end times
        
        for(Interval curr: intervals){
            if(!minHeap.isEmpty() && minHeap.peek() <= curr.start){     // earliest end time <= current start time
                minHeap.poll();
            }
            minHeap.offer(curr.end);                                    // Push the current meeting’s end time (occupy a room)
        }
        return minHeap.size();                                        // minimum number of rooms required
    }
    

    // Approach-3: Sweeping Line
    // TC = O(n.log(n)), SC = O(n)
    /*
        1. At any point in time, the number of rooms required is simply: the number of meetings happening at that moment
        2. The sweep line technique helps us track how this number changes over time by processing all start and end events in order.
    */
    public int minMeetingRooms3(List<Interval> intervals){
        TreeMap<Integer, Integer> mp = new TreeMap<>();        // Record changes in sorted order
        for(Interval i : intervals){
            mp.put(i.start, mp.getOrDefault(i.start, 0) + 1);  // Meeting starts, increment
            mp.put(i.end, mp.getOrDefault(i.end, 0) - 1);     // Meeting ends, decrement 
        }

        int prev = 0;   // track the number of ongoing meetings
        int res = 0;    // store the maximum number of simultaneous meetings
        for(int key : mp.keySet()){
            prev += mp.get(key);         // as line sweeps through events, we update ongoing meetings count
            res = Math.max(res, prev);  // at a time maximum rooms needed
        }
        return res;
    }


    // Approach-4: Two Pointers
    // TC = O(n.log(n)), SC = O(n)
    /*
        1. Separate the problem into two simpler timelines: one list of all start times and one list of all end times.
        2. By moving two pointers over the sorted start and end times, we can track how many meetings are happening at the same time.
    */
     public int minMeetingRooms4(List<Interval> intervals){
            int n = intervals.size();
            int[] start = new int[n];
            int[] end = new int[n];
             for (int i = 0; i < n; i++) {
            start[i] = intervals.get(i).start;
            end[i] = intervals.get(i).end;
            }
        
        Arrays.sort(end);
        Arrays.sort(start);

            int res = 0, count = 0, s = 0, e = 0;
             while (s < n) {
            if (start[s] < end[e]) {  // a new meeting starts before the earliest one ends
                s++;
                count++;              // A meeting starts, need a room
            } else {
                e++;
                count--;              // A meeting ends, free a room
            }
            res = Math.max(res, count); 
        }
        return res;
     }
}
