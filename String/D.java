//LC.68. Text Justification: https://leetcode.com/problems/text-justification/description/
import java.util.*;

public class D{

    //Appraoch-1: Greedy line buildeing + Simulation
    // TC = O(N*maxWidth), SC = O(N*maxWidth)
/*
  1. Long implementation but simple logic.
     a. Each line should have exactly maxWidth characters.
     b. Spaces should be evenly distributed between words.
     c. If the spaces don't divide evenly, the extra spaces are added from the left.
     d. The last line should be left-justified and no extra space is inserted between words.
  2. Greedily add as many words as you can to the current line.
  3. After fixing the words for the current line, adjust spaces between them:
     a. Evenly distribute spaces.
     b. Add extra spaces starting from the left if needed.
  4. For the last line, left-justify the line.
  5. Write logic to create line in sepaarte function and logic for each line in separate.
 */
    int MAX_WIDTH;

    public List<String> fullJustify(String[] words, int maxWidth){
        List<String> result = new ArrayList<>();
        int n = words.length;
        MAX_WIDTH = maxWidth;
        int i = 0;

        while( i< n){
            int lettersCount = words[i].length(); 
            int j = i + 1;
            int spaceSlots = 0;

            //selecting words for current line
            while(j < n && spaceSlots + lettersCount + words[j].length() + 1 <= maxWidth){   //currLength + nextWordLength + 1 space
                lettersCount += words[j].length();
                spaceSlots += 1;
                j++;
            }
            int remainingSlots = maxWidth - lettersCount;

            int eachWordSpace = spaceSlots == 0 ? 0 : remainingSlots / spaceSlots;
            int extraSpace = spaceSlots == 0 ? 0 : remainingSlots % spaceSlots;

            if( j == n){  //means we are on last line- left justified
                eachWordSpace = 1;
                extraSpace = 0;
            }

            result.add(getFinalWord(i, j, eachWordSpace, extraSpace, words));
            i = j; //moving to next set of words for next line
        }
        return result;
    }

    private String getFinalWord(int i, int j, int eachWordSpace, int extraSpace, String[] words){
        StringBuilder s = new StringBuilder();

        for(int k =i; k<j; k++){
            s.append(words[k]);

            if(k == j-1) continue;  //last word of line, so no space

            for(int space = 1; space <= eachWordSpace; space++) s.append(" ");

            if(extraSpace > 0) {
                s.append(" ");
                extraSpace--;
            }
        }
        while(s.length() < MAX_WIDTH) s.append(" ");  //for last line, we need to add spaces to make it equal to maxWidth
        
        return s.toString();
  }


         //Appraoch-2(Compact ver.): Greedy line buildeing + Simulation
         // TC = O(N*maxWidth), SC = O(N*maxWidth)
   public List<String> fullJustify2(String[] words, int maxWidth) {
        List<String> result = new ArrayList<>();
        int i = 0, n = words.length;

        while (i < n) {
            int j = i + 1, lineLength = words[i].length();

            // Greedily add as many words as fit
            while (j < n && lineLength + words[j].length() + (j - i) <= maxWidth) {
                lineLength += words[j].length();
                j++;
            }

            int gaps = j - i - 1;
            StringBuilder line = new StringBuilder();

            // If it's the last line or single word line → Left Justify
            if (j == n || gaps == 0) {
                for (int k = i; k < j; k++) {
                    line.append(words[k]);
                    if (k != j - 1) line.append(' ');
                }
                // Fill remaining spaces at end
                while (line.length() < maxWidth) line.append(' ');
            } else {
                int totalSpaces = maxWidth - lineLength;
                int spacePerGap = totalSpaces / gaps;
                int extraSpaces = totalSpaces % gaps;

                for (int k = i; k < j; k++) {
                    line.append(words[k]);
                    if (k != j - 1) {
                        for (int s = 0; s < spacePerGap; s++) line.append(' ');
                        if (extraSpaces-- > 0) line.append(' ');
                    }
                }
            }
            result.add(line.toString());
            i = j;
        }
        return result;
    }
}