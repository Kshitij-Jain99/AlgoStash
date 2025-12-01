//LC.2166. Design Bitset: https://leetcode.com/problems/design-bitset/description/
//Classic object design and state management problem

public class I {
    static class Bitset {
        private final int size;
        private final boolean[] bits;  // stores raw (unflipped) bits
        private boolean flipped;
        private int count;  // number of bits that are logically 1

        public Bitset(int size) {
            this.size = size;
            this.bits = new boolean[size];
            this.flipped = false;
            this.count = 0;
        }

        public void fix(int idx) {
            // logical value of bit
            if (bits[idx] == flipped) {  // means logical 0
                bits[idx] = !flipped;    // set to logical 1
                count++;
            }
        }

        public void unfix(int idx) {
            if (bits[idx] != flipped) {  // means logical 1
                bits[idx] = flipped;     // set to logical 0
                count--;
            }
        }

        public void flip() {
            flipped = !flipped;
            count = size - count;
        }

        public boolean all() {
            return count == size;
        }

        public boolean one() {
            return count > 0;
        }

        public int count() {
            return count;
        }

        @Override
        public String toString() {
            // More efficient toString using bitwise logic
            char[] arr = new char[size];
            for (int i = 0; i < size; i++) {
                arr[i] = (bits[i] ^ flipped) ? '1' : '0';
            }
            return new String(arr);
        }
    }
}