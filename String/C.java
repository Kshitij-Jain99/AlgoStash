//LC.273. Integer to English Words{Multiple Case handling and Human Error Possibility}
//Solution: https://leetcode.com/problems/integer-to-english-words/solutions/5599635/integer-to-english-words/

import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class C{

    //Approach-1: Simple Recursion Story
    // TC = O(Log10(nums)){Rec. depth}, SC = O(Log10(nums)){Rec. Stack}
/*
    1. 0 is mapped to an empty string (used to avoid extra "Zero" in combinations).
    2. recursively breaks down the number and returns the English word form.
    3. 0-9 and 10-19 through direct lookup in map.
    4. 20–99: Use belowHundred for the tens digit (num/10
    5. Next Levels (Thousands, Millions, Billions): Each of the following cases divides the number and recursively processes both:
       the current part and the remainder.
    6. 
 */
    public static final Map<Integer, String> belowTen = Map.of(
       0, "", 1, "One", 2, "Two", 3, "Three", 4, "Four", 5, "Five",
       6, "Six", 7, "Seven", 8, "Eight", 9, "Nine"
    );
    public static final Map<Integer, String> belowTwenty = Map.of(
        10, "Ten", 11, "Eleven", 12, "Twelve", 13, "Thirteen", 14, "Fourteen",
        15, "Fifteen", 16, "Sixteen", 17, "Seventeen", 18, "Eighteen", 19, "Nineteen"
    );
    private static final Map<Integer, String> belowHundred = Map.of(  //(position key, word) keys are the tens digit, not actual values like 20.
        2, "Twenty", 3, "Thirty", 4, "Forty", 5, "Fifty",
        6, "Sixty", 7, "Seventy", 8, "Eighty", 9, "Ninety"
    );
    
    public String solve(int num){
        if(num < 10) return belowTen.get(num);
        if(num < 20) return belowTwenty.get(num);
        if(num < 100) return belowHundred.get(num/10) + (num%10 != 0 ? " " + belowTen.get(num%10): "");
        if(num < 1000)  return solve(num / 100) + " Hundred" + (num % 100 != 0 ? " " + solve(num % 100) : "");
        if(num < 1000000) return solve(num / 1000) + " Thousand" + (num % 1000 != 0 ? " " + solve(num % 1000) : "");
        if(num < 1000000000) return solve(num / 1000000) + " Million" + (num % 1000000 != 0 ? " " + solve(num % 1000000) : "");
        return solve(num / 1000000000) + " Billion" + (num % 1000000000 != 0 ? " " + solve(num % 1000000000) : "");
     }
    public String numberToWords(int num){
        if(num == 0) return "zero";  //since it's not in any map and shouldn't trigger recursion
        return solve(num);
      }

    //Approach-2: Iterative Solution
    // TC = O(Log10(nums)){number of iterations proportional to the number of chunks}, SC = O(1){No Recursion Stack}
/*
 1. convert a number into English words by processing it in chunks of three digits
 2.  initialize arrays for place value words (like thousand, million, billion) and for digit and tens names. 
 3.  A loop processes the number from the least significant chunk (ones, tens, hundreds) to the most significant chunk (thousands, millions, billions).
 4. To convert each chunk to english:
    a. Handle the hundreds place if present (e.g., 567 becomes "Five Hundred").
    b.  Process the tens and ones (e.g., 67 becomes "Sixty-Seven").
    c. Append the appropriate scale word (e.g., thousand, million) based on the chunk's position (e.g., 234 becomes "Two Hundred Thirty-Four Thousand").
 5. track the scale by using an index (groupIndex) that increments with each chunk processed. This index is used to fetch the correct scale word (thousand, million, billion) from the thousands array.
 */
    public String numberToWords2(int num) {
        // Handle the special case where the number is zero
        if (num == 0) return "Zero";

        // Arrays to store words for single digits, tens, and thousands
        String[] ones = {"", "One", "Two", "Three", "Four", "Five", "Six", "Seven", "Eight", "Nine", "Ten", "Eleven", "Twelve", "Thirteen", "Fourteen", "Fifteen", "Sixteen", "Seventeen", "Eighteen", "Nineteen"};
        String[] tens = {"", "", "Twenty", "Thirty", "Forty", "Fifty", "Sixty", "Seventy", "Eighty", "Ninety"};
        String[] thousands = {"", "Thousand", "Million", "Billion"};

        // StringBuilder to accumulate the result
        StringBuilder result = new StringBuilder();
        int groupIndex = 0;

        // Process the number in chunks of 1000
        while (num > 0) {
            // Process the last three digits
            if (num % 1000 != 0) {
                StringBuilder groupResult = new StringBuilder();
                int part = num % 1000;

                // Handle hundreds
                if (part >= 100) {
                    groupResult.append(ones[part / 100]).append(" Hundred ");
                    part %= 100;
                }

                // Handle tens and units
                if (part >= 20) {
                    groupResult.append(tens[part / 10]).append(" ");
                    part %= 10;
                }

                // Handle units
                if (part > 0) {
                    groupResult.append(ones[part]).append(" ");
                }

                // Append the scale (thousand, million, billion) for the current group
                groupResult.append(thousands[groupIndex]).append(" ");
                // Insert the group result at the beginning of the final result
                result.insert(0, groupResult);
            }
            // Move to the next chunk of 1000
            num /= 1000;
            groupIndex++;
        }

        return result.toString().trim();
    }

    //Approach-3: Pair-Based Approach
    // TC = O(K){the loop iterates through the pairs until it finds a match. This complexity is linear with respect to the number of pairs, which is constant in practice as the number of pairs is fixed.},
    // SC = O(Log10(N)){recursion stack}
/*
 1. Use a predefined list of numeric values and their corresponding English words to convert a number. 
 2. Process the number by matching it against these pairs from largest to smallest, dividing the number, and converting each part recursively.
 3.  start by defining a list of pairs where each pair consists of a numeric value and its English word, such as 1000000000 for "Billion", 1000000 for "Million", and down to 1 for "One".
 4. For a number like 1234567, we iterate through the list from the largest value to the smallest. We check if the number is greater than or equal to each value. If it is:
    Divide the Number: Determine how many times the value fits into the number (the quotient) and calculate the remainder. For 1234567, we match 1 Million, resulting in "One Million", and then process the remainder (234567).
    Recursive Conversion: Convert the quotient to words and recursively process the remainder using the same list of pairs.
 5. We concatenate the word for the current pair with the results from the recursive call for the remainder. This process builds the final English word representation from the largest units (like billion) to the smallest (like one)
 6. Iterate over the numberToWordsMap.
    For each pair (value, word) in numberToWordsMap, check if the number num is greater than or equal to value.
    If num is greater than or equal to value:
      a. Compute the prefix: If num is 100 or greater, recursively convert the quotient (num / value) to words and append " " (a space). If num is less than 100, set prefix to an empty string.
      b. Get the unit as the current word from numberToWordsMap
      c. Compute the suffix: If the remainder (num % value) is zero, set suffix to an empty string. Otherwise, recursively convert the remainder to words and prepend " " (a space).
      d. Return the combined result: prefix + unit + suffix.

 */

    // List to store words for numbers
    private static final List<NumberWord> numberToWordsList = Arrays.asList(
        new NumberWord(1000000000, "Billion"), new NumberWord(1000000, "Million"),
        new NumberWord(1000, "Thousand"), new NumberWord(100, "Hundred"),
        new NumberWord(90, "Ninety"), new NumberWord(80, "Eighty"),
        new NumberWord(70, "Seventy"), new NumberWord(60, "Sixty"),
        new NumberWord(50, "Fifty"), new NumberWord(40, "Forty"),
        new NumberWord(30, "Thirty"), new NumberWord(20, "Twenty"),
        new NumberWord(19, "Nineteen"), new NumberWord(18, "Eighteen"),
        new NumberWord(17, "Seventeen"), new NumberWord(16, "Sixteen"),
        new NumberWord(15, "Fifteen"), new NumberWord(14, "Fourteen"),
        new NumberWord(13, "Thirteen"), new NumberWord(12, "Twelve"),
        new NumberWord(11, "Eleven"), new NumberWord(10, "Ten"),
        new NumberWord(9, "Nine"), new NumberWord(8, "Eight"),
        new NumberWord(7, "Seven"), new NumberWord(6, "Six"),
        new NumberWord(5, "Five"), new NumberWord(4, "Four"),
        new NumberWord(3, "Three"), new NumberWord(2, "Two"),
        new NumberWord(1, "One")
    );

    public String numberToWords3(int num) {
        if (num == 0) {
            return "Zero";
        }

        for (NumberWord nw : numberToWordsList) {
            // Check if the number is greater than or equal to the current unit
            if (num >= nw.value) {
                // Convert the quotient to words if the current unit is 100 or greater
                String prefix = (num >= 100) ? numberToWords(num / nw.value) + " " : "";

                // Get the word for the current unit
                String unit = nw.word;

                // Convert the remainder to words if it's not zero
                String suffix = (num % nw.value == 0) ? "" : " " + numberToWords(num % nw.value);

                return prefix + unit + suffix;
            }
        }

        return "";
    }
}
   class NumberWord {
    int value;
    String word;

    NumberWord(int value, String word) {
        this.value = value;
        this.word = word;
    }
}

