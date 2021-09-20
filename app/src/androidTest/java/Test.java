import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class Test {

    @org.junit.Test
    public int solution(int n) {
        String stringValue = String.valueOf(n);
        int maximumValue = Integer.MIN_VALUE;

        int index = (n < 0) ? 1 : 0; //we use this to handle negative numbers

        for (; index < stringValue.length(); index++) {
            StringBuilder builder = new StringBuilder(stringValue);
            StringBuilder inserted = builder.insert(index, "5");
            try {
                int number = Integer.parseInt(inserted.toString());
                if (maximumValue < number) {
                    maximumValue = number;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return maximumValue;
    }

//    public int maxLength(List<String> arr) {
//
//        return maxValue.get();
//    }

    boolean hasAllUniqueCharacters(String word) {
        HashSet<String> set = new HashSet<>();
        for(int i = 0; i < word.length(); i++) {
            char c = word.charAt(i);
            if(!set.add(String.valueOf(c))) return false;
        }
        return true;
    }

//    public int solution(String[] A) {
//        //["co", "dil", "ity"]
//        int length = 0;
//        for (int i = 0; i < A.length; i++) {
//            StringBuilder start = new StringBuilder(A[i]);
//            for (int j = i + 1; j < A.length; j++) {
//                String next = A[j];
//                if(!uniqueCharacters(next) || !uniqueCharacters(start.toString())) {
//                    continue;
//                }
//                if (!letterContains(start.toString(), next)) {
//                    start.append(next);
//                    int stringLength = start.toString().length();
//                    if (stringLength > length) {
//                        length = stringLength;
//                    }
//                }
//            }
//        }
//        return length;
//    }
//
//    boolean uniqueCharacters(String str) {
//        for (int i = 0; i < str.length(); i++)
//            for (int j = i + 1; j < str.length(); j++)
//                if (str.charAt(i) == str.charAt(j))
//                    return false;
//        return true;
//    }
//
//
//        boolean letterContains(String words, String word) {
//            String[] letters = words.split("");
//            boolean contains = false;
//            for (String letter : letters) {
//                if (word.contains(letter)) {
//                    contains = true;
//                    break;
//                }
//            }
//            return contains;
//        }

}
