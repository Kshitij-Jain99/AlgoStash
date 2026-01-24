# Design DS Problem Framework

A compact, reusable framework for “Design Data Structure” interview problems.

---

## A) Translate the problem into Operations
- First thing: **ignore story** (“auction system”, “twitter”, “food ratings”).
- Just extract the API:
  - `add(...)`, `update(...)`, `remove(...)`, `query(...)`
- Then find: **What must be fast?**
  - time constraints + number of calls tells you what you can afford
  - e.g. updates/removals must be fast, queries must be fast

---

## B) Identify the “Hard Operation”
- Usually **one operation** is the real challenge.
- Which one forces structure?

| Hard Query / Need | Typical DS |
|---|---|
| get max | heap / TreeSet |
| get min | heap / TreeSet |
| get median | two heaps |
| get most recent | linked list / deque |
| get top K | heap / bucket |
| range query | segment tree / BIT |
| prefix search | trie |
| random element | array + hashmap |
| LRU | hashmap + doubly linked list |

---

## C) Decide your “Source of Truth”
- What data must always be correct instantly?
- Usually: mapping from **entity → current value/state**
  - **HashMap as the truth store**
- Rule of thumb:
  - If an operation says **“it is guaranteed to exist”**, HashMap is expected.

---

## D) Add an “Index” to support queries
- Hashmap gives correctness, but queries like max/min/topK need extra structure.
- Build a second structure (index):

**Standard pattern**
- `Truth = HashMap`
- `Query index = Heap / TreeSet / etc`
- Heaps contain **history**, Hashmap contains **Truth**, queries do **clean up**.

Index choices:
- MAX/MIN → Heap OR TreeSet
- ORDERED by multiple keys → TreeSet / SortedList OR Heap with comparator
- “latest” → Linked list / stack / deque

---

## E) Handle Updates/Deletes (choose one strategy)
You have 2 choices:

### 1) Lazy Deletion (most common + easiest)
- Used when: you have a **heap**, deletions/updates are hard inside heap
- Idea:
  - never delete from heap directly
  - push new values
  - when querying: pop stale entries until valid
- Pattern: **Stock Price Fluctuation / Auction System**

### 2) Direct Deletion (TreeSet / SortedMap)
- Used when: you can remove/update efficiently, you need always-clean ordering
- Used in:
  - TreeSet (Java/C++)
  - SortedContainers (Python)
- More complex, but clean.

---

## F) Write the “Validity Check” function
- If using lazy deletion, ALWAYS define: **when is a heap entry valid?**
- Example (Auction):
  - heap has `(amount, userId)`
  - valid if: `currentBid[itemId][userId] == amount`
    - if user removed bid → invalid
    - if user updated bid → old entry invalid

---

## G) Complexity sanity check (before coding)
Confirm:
- update ops: `O(1)` or `O(log n)`
- query: `O(log n)` amortized
- total calls ≤ 50k → safe

If approach is `O(n)` per query/update → too slow.

---

# Most Reusable “Design Problem Templates”

### Template 1: “Max/Min with updates” (Auction, Stock Price, Leaderboard)
- Use: **HashMap for truth + Heap for max/min + Lazy deletion**

### Template 2: “Top K frequent”
- Use: **HashMap counts + heap / bucket sort**

### Template 3: “LRU / LFU”
- Use: **HashMap + Doubly Linked List**
- or **HashMap + frequency buckets**

### Template 4: “Median stream”
- Use: **two heaps** (maxHeap + minHeap)

### Template 5: “Random insert/delete/getRandom”
- Use: **array/list + hashmap(index)**

---

# 10 Problems to Practice (Same Core Patterns)

## A) HashMap + Heap + Lazy Deletion (closest to Auction System)
- 2034. Stock Price Fluctuation ⭐ (must-do)
- 2353. Design a Food Rating System ⭐
- 1845. Seat Reservation Manager
- 981. Time Based Key-Value Store (map + time queries)
- 895. Maximum Frequency Stack (stack + freq map, very “design-y”)

## B) “Top K / Ordering / Ranking” (indexing mindset)
- 347. Top K Frequent Elements
- 692. Top K Frequent Words
- 1244. Design A Leaderboard ⭐

## C) “Classic design patterns”
- 146. LRU Cache ⭐
- 380. Insert Delete GetRandom O(1) ⭐
