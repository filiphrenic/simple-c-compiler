package hr.fer.zemris.ppj.automaton;

import java.util.ArrayList;
import java.util.List;

/**
 * This is a singleton class used only to provide utility functions to the
 * {@link AutomatonHandler} class. To keep the code more organized.
 * 
 * @author fhrenic
 */
public class AutomatonUtility {

    /**
     * Can't be instantiated
     */
    private AutomatonUtility() {
    }

    /**
     * Splits the given regex into smaller regexes. regex r1|r2|r3 is split into
     * three regexes: r1, r2, r3 if there is no '|' symbol, than the whole regex
     * is return as a single element of a list
     * 
     * @param regex regex to split
     * @return list of smaller regexes
     */
    public static List<String> splitChoices(String regex) {
        List<String> expressions = new ArrayList<>();
        int lastIdx = -1;
        int len = regex.length();
        int numOfBrackets = 0;

        for (int idx = 0; idx < len; idx++) {
            if (!isOperator(regex, idx)) {
                continue;
            }
            char current = regex.charAt(idx);
            if (current == '(') {
                numOfBrackets++;
            } else if (current == ')') {
                numOfBrackets--;
            } else if (current == '|' && numOfBrackets == 0) {
                expressions.add(regex.substring(lastIdx + 1, idx));
                lastIdx = idx;
            }
        }
        expressions.add(regex.substring(lastIdx + 1));
        return expressions;
    }

    /**
     * Returns true if the symbol at position index isn't prefixed with an odd
     * number of '\' symbols
     * 
     * @param regex regex to test
     * @param index index in the regex
     * @return true if symbol at index is an operator
     */
    public static boolean isOperator(String regex, int index) {
        int numOfPref = 0;
        int i = index - 1;
        while (i >= 0 && regex.charAt(i) == '\\') {
            numOfPref++;
            i--;
        }
        return numOfPref % 2 == 0;
    }

    /**
     * This method finds the next closing operator starting from given index in
     * a regex.
     * 
     * @param regex expression in which we search the operator
     * @param closer operator to search for
     * @param startFrom starting index
     * @return index of a first found operator, -1 if none found
     */
    public static int findCloser(String regex, char closer, int startFrom) {
        int len = regex.length();
        for (int idx = startFrom; idx < len; idx++) {
            if (regex.charAt(idx) == closer && isOperator(regex, idx)) {
                return idx;
            }
        }
        return -1;
    }

    /**
     * This method is used to un-escape escaped symbols in a regex
     * 
     * @param symbol escaped symbol
     * @return un-escaped symbol
     */
    public static char unescape(char symbol) {
        switch (symbol) {
            case 't':
                return '\t';
            case 'n':
                return '\n';
            case '_':
                return ' ';
            default:
                return symbol;
        }
    }

}
