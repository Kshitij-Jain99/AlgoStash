import java.util.HashMap;
import java.util.Map;

public class B {
//LC.12. Integer to Roman: https://leetcode.com/problems/integer-to-roman/description/

  //Approach-1: Best
  // TC = O(1), SC = O(1)
  /*
   1. Create Mapping for all conversion rules.
   2. Only powers of 10 (I, X, C, M) can be appended consecutively at most 3 times to represent multiples of 10.
      Other symbols also max 3 times.
   */
   public String intToRoman1(int num) {
      final int[] values =     {1000, 900, 500, 400, 100, 90, 50, 40, 10, 9, 5, 4, 1};
      final String[] symbols = {"M", "CM", "D",  "CD", "C", "XC", "L", "XL", "X", "IX", "V", "IV", "I"};

      StringBuilder sb = new StringBuilder();
      for(int i = 0; i<values.length; i++){ //going from 1000's place to 1's palce
        if(num == 0) break;
        while( num >= values[i]) {
            sb.append(symbols[i]);  // eg. 3478 > 1000 -> MMM = 3000, num = 3478-3000
            num -= values[i];
        }
      }
      return sb.toString();
        }  

        //Approach-2: Write all cases
        // TC = O(1), SC = O(1)
        public String intToRoman2(int num) {
        String[] ones = {"", "I", "II", "III", "IV", "V", "VI", "VII", "VIII", "IX"};
        String[] tens = {"", "X", "XX", "XXX", "XL", "L", "LX", "LXX", "LXXX", "XC"};
        String[] hrns = {"", "C", "CC", "CCC", "CD", "D", "DC", "DCC", "DCCC", "CM"};
        String[] ths  = {"", "M", "MM", "MMM"};
        
        return ths[num / 1000] +
               hrns[(num % 1000) / 100] +
               tens[(num % 100) / 10] +
               ones[num % 10];
    }

//------------------------------------------------------------------------------------

//LC.13. Roman to Integer:https://leetcode.com/problems/roman-to-integer/description/

      //Approach-1:
      // TC = O(n), SC = O(1)
     public static int value(char c) {
        switch(c) {
            case 'I':return 1;
            case 'V':return 5;
            case 'X':return 10;
            case 'L':return 50;
            case 'C':return 100;
            case 'D':return 500;
            case 'M':return 1000;
            default:return 0; //for safety
        }
    }
    public int romanToInt1(String s) {
        int total=0, i=0, s1,s2;
        
        while(i < s.length()){  //left to right
            s1 = value(s.charAt(i));

            if(i+1 < s.length()) {
                s2 = value(s.charAt(i+1));
                if(s1>=s2) {  //no subtraction rule
                    total+=s1;
                    i++;
                }
                else {     //subtraction rule
                    total+=s2-s1;
                    i+=2;
                }
            }
            else {
                total+=s1;
                i++;
            }

        }
        return total;
        
    }

      //Approach-2:
      // TC = O(n), SC = O(1)
     public int romanToInt2(String s) {
       Map<Character,Integer> map=   new HashMap<>();
          map.put('I', 1);  
          map.put('V', 5);
          map.put('X', 10);
          map.put('L', 50);
          map.put('C', 100);
          map.put('D', 500);
          map.put('M', 1000);
        
          int total=0;
          int prev=0;
          for(int i=s.length()-1;i>=0;i--){  //right to left
             int curr = map.get(s.charAt(i));
                if(curr<prev) total -=curr;
                else total+=curr;
              prev=curr;
           }
        return total;
    }
}
