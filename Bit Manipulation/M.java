// LC. 2569. Handling Sum Queries After Update: https://leetcode.com/problems/handling-sum-queries-after-update/description/
import java.util.*;

public class M{

    // Approach-1: BitSet class{260 ms}
    // TC = O(n * m * (n/64)), SC = O(n)
    public long[] handleQuery1(int[] nums1, int[] nums2, int[][] queries) {
        List<Long> ls = new ArrayList<>();
        int n = nums1.length;
        BitSet bs = new BitSet(n);   // O(n)
        long sum = 0;                
        for(int i = 0;i < n;i++){    // O(n)
            sum += 1L * nums2[i];
            if(nums1[i] == 1) bs.set(i);
        }
        for(var q:queries){     // O(m)
            if(q[0] == 1){
                bs.flip(q[1],q[2] + 1);   // O(n/64) per flip
            }
            else if(q[0] == 2){
                sum += 1L * q[1] * bs.cardinality();  // O(n/64) per cardinality
            }
            else ls.add(sum);
        }
        long ans[] = new long[ls.size()];
        for(int i = 0;i < ans.length;i++){
            ans[i] = ls.get(i);
        }
        return ans;
    }

    
    // Approach-2: BitSet using long array(70 ms)
    // TC = O(n + m * ceil(n/64)), SC = O(ceil(n/64) + t)  // bits array + answer array (t = number of type-3 queries, t <= m)
     public long[] handleQuery(int[] nums1, int[] nums2, int[][] queries) {
        int n = nums1.length;
        int m = queries.length;

        long[] bits = new long[(n + 63) / 64];
        long sum = 0;
        int currentOnes = 0;

        for (int i = 0; i < n; i++) {
            if (nums1[i] == 1) {
                bits[i >>> 6] |= (1L << (i & 63));
                currentOnes++;
            }
            sum += nums2[i];
        }

        int outCount = 0;
        for (int[] q : queries) {
            if (q[0] == 3) outCount++;
        }
        long[] ans = new long[outCount];
        int ansIdx = 0;

        for (int[] q : queries) {
            int type = q[0];

            if (type == 1) {
                int l = q[1];
                int r = q[2];
                
                int onesInRange = 0;
                
                int startWord = l >>> 6;
                int endWord = r >>> 6;
                int startBit = l & 63;
                int endBit = r & 63;

                if (startWord == endWord) {
                    long mask = (-1L >>> (63 - endBit)) & (-1L << startBit);
                    onesInRange += Long.bitCount(bits[startWord] & mask);
                    bits[startWord] ^= mask;
                } else {
                    long startMask = -1L << startBit;
                    onesInRange += Long.bitCount(bits[startWord] & startMask);
                    bits[startWord] ^= startMask;

                    for (int i = startWord + 1; i < endWord; i++) {
                        onesInRange += Long.bitCount(bits[i]);
                        bits[i] = ~bits[i];
                    }

                    long endMask = -1L >>> (63 - endBit);
                    onesInRange += Long.bitCount(bits[endWord] & endMask);
                    bits[endWord] ^= endMask;
                }

                int totalLen = r - l + 1;
                currentOnes += totalLen - 2 * onesInRange;

            } else if (type == 2) {
                sum += (long) q[1] * currentOnes;
            } else {
                ans[ansIdx++] = sum;
            }
        }
        return ans;
    }


    // Appraoch-3: Segment Tree with Lazy Propagation(24 ms)
    // TC = O(n + m * log n)   // n for building the tree, then each type-1 flip is O(log n), type-2/type-3 are O(1)
    // SC = O(n + t)           // segment tree + lazy arrays ~ O(n); result array stores t type-3 answers (t ≤ m)
     public long[] handleQuery3(int[] nums1, int[] nums2, int[][] queries) {
        int resultSize = 0;
        for (int[] query: queries) {
            if (query[0]==3) resultSize++;
        }

        long[] result = new long[resultSize];

        SegmentTree tree = new SegmentTree(nums1);

        long sum = 0;

        for (int i : nums2) {
            sum+=((long) i);
        }

        int resultIdx = 0;

        for (int[] query: queries) {
            if (query[0]==1) {
                tree.flip(query[1], query[2]);
            } else if(query[0]==2) {
                sum += ((long) tree.query()*query[1]);
            } else if(query[0]==3) {
                result[resultIdx] = sum;
                resultIdx++;
            }
        }
        return result;
    }
}

class SegmentTree {
    long[] tree;
    boolean[] lazy;
    int size;

    public SegmentTree(int[] arr) {
        this.size = arr.length;
        this.tree = new long[size * 4];
        this.lazy = new boolean[size * 4];

        build(arr, 1, 0, size - 1);

    }

    public void build(int[] arr, int node, int l, int r) {
        if (l == r) {
            tree[node] = (long) arr[l];
            return;
        }

        int mid = l + (r - l) / 2;
        build(arr, node * 2, l, mid);
        build(arr, node * 2 + 1, mid + 1, r);
        tree[node] = tree[node * 2] + tree[node * 2 + 1];

    }

    public void flip(int l, int r) {
        flip(1, 0, size - 1, l, r);
    }

    public void flip(int node, int l, int r, int start, int end) {
        if (lazy[node]) {
            tree[node] = (r - l + 1) - tree[node];
            if (l != r) {
                lazy[node * 2] ^= true;
                lazy[node * 2 + 1] ^= true;
            }
            lazy[node]=false;
        }

        if (l > end || r < start) {
            return;
        }

        if (l >= start && r <= end) {
            tree[node] = (r - l + 1) - tree[node];

            if (l != r) {
                lazy[node * 2] ^= true;
                lazy[node * 2 + 1] ^= true;
            }
            return;
        }

        int mid = l + (r-l)/2;
        flip(node*2, l, mid, start, end);
        flip(node*2+1, mid+1, r, start, end);
        tree[node] = tree[node * 2] + tree[node * 2 + 1];
    }

    public long query() {
        return tree[1];
    }
}