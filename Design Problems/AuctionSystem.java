// LC. 3815. Design Auction System: https://leetcode.com/problems/design-auction-system/description/
import java.util.*;
    // Approach: Beats 95% (86ms)
    /*
    A. Translate the problem into Operations
        - What must be fast?
           We have ≤ 50k calls, so we need around O(log n) per operation.
           getHighestBidder must be fast.
           updates/removals must also be efficient.
    B. Identify the “Hard Operation”
        - Hard op = getHighestBidder(itemId)
        - “get max” → heap / TreeSet
        - Since we need fast updates/removals too → heap is easiest with lazy cleanup.
    C. Decide your “Source of Truth”
        - We need instant correctness for: “what is the current bid of user u on item i?”
        - truth = HashMap mapping: (itemId, userId) -> currentBidAmount     
    D. Add an “Index” to support queries:
        - HashMap alone can’t answer max quickly.
        - So index: For each itemId, maintain a max-heap of bid entries:
            entry = (bidAmount, userId)
            max by bidAmount, then by userId
    E. Handle Updates/Deletes: Lazy Deletion
       -  On add/update: push new (amount, userId) into heap
       -  On remove: delete from truth map only
       -  On query: pop stale heap entries until top matches truth    
    F.  Write the “Validity Check” function
        - Heap entry (amt, uid) is valid for itemId if:
          bids[itemId] contains uid
          and bids[itemId][uid] == amt
        - Otherwise stale (removed or updated) → pop it.
    G. Complexity sanity check:
       - addBid/updateBid ->O(log n); removeBid → O(1)
       - getHighestBidder → O(log n) amortized (each stale entry popped once)
       - Total calls ≤ 50k → safe.
    */

class AuctionSystem {
    private Map<Integer, Map<Integer, Integer>> bids;
    private Map<Integer, PriorityQueue<Bid>> heaps;

    private static class Bid {
        int amount;
        int userId;

        Bid(int amount, int userId) {
            this.amount = amount;
            this.userId = userId;
        }
    }

    public AuctionSystem() {
        bids = new HashMap<>();
        heaps = new HashMap<>();
    }

    public void addBid(int userId, int itemId, int bidAmount) {
        bids.computeIfAbsent(itemId, k -> new HashMap<>()).put(userId, bidAmount);

        heaps.computeIfAbsent(itemId, k -> new PriorityQueue<>(
                (a, b) -> {
                    if (a.amount != b.amount) return b.amount - a.amount; 
                    return b.userId - a.userId; 
                }
        )).offer(new Bid(bidAmount, userId));
    }

    public void updateBid(int userId, int itemId, int newAmount) {
        bids.get(itemId).put(userId, newAmount);
        heaps.get(itemId).offer(new Bid(newAmount, userId));
    }

    public void removeBid(int userId, int itemId) {
        Map<Integer, Integer> map = bids.get(itemId);
        map.remove(userId);
        if (map.isEmpty()) bids.remove(itemId);
    }

    public int getHighestBidder(int itemId) {
        PriorityQueue<Bid> pq = heaps.get(itemId);
        if (pq == null) return -1;

        while (!pq.isEmpty()) {
            Bid top = pq.peek();

            Map<Integer, Integer> map = bids.get(itemId);
            if (map != null) {
                Integer cur = map.get(top.userId);
                if (cur != null && cur == top.amount) {
                    return top.userId;
                }
            }

            pq.poll();
        }

        return -1;
    }
}
