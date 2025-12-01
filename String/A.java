//LC.8. String to Integer (atoi): https://leetcode.com/problems/string-to-integer-atoi/description/

/*
 1. Important design principles learned from this atoi algorithm:
    a. Step-by-Step Processing: Break complex problems into smaller steps
    b. Edge Case Consideration: Account for various input patterns
    c. Efficient Overflow Detection: Real-time detection rather than pre-checking
    d. Single-Pass Processing: Achieve O(n) time complexity with one string traversal
 2. This algorithm demonstrates fundamental patterns in string processing and is applicable to many similar problems.
    The state management and boundary condition handling are particularly important concepts that appear in many algorithmic challenges.
    */
class A {
    // TC = O(N), SC = O(1)
    public int myAtoi(String s) {
        int n = s.length();
        if(s == null || s.length() == 0) return 0;

        //Constants for 32 bit signed integer range
        final int INT_MAX = Integer.MAX_VALUE;
        final int INT_MIN = Integer.MIN_VALUE;

        int i = 0;
        while(i<n && s.charAt(i) == ' ') i++;
        if(i==n) return 0;
        
        //only the first sign is valid so no requirement of loop
        //If no sign is present proceed to next step but increase idx only when sign is found here
        int sign = 1;
        if(s.charAt(i) == '+') i++;
        else if(s.charAt(i) == '-'){
            sign = -1;
            i++;
        }

        long res = 0;  //now we start forming the number
        while(i < n && Character.isDigit(s.charAt(i))){
            int digit = s.charAt(i) - '0';
            res = res*10 + digit;  //number construction

            if(sign*res <= INT_MIN) return INT_MIN;
            if(sign*res >= INT_MAX) return INT_MAX;
            i++;
        }
        return (int)(res*sign);

        }
    }
