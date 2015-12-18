package hr.fer.zemris.ppj.util;

/**
 * @author fhrenic
 */
public class Util {

    private Util() {
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
            case '0':
                return '\0';
            default:
                return symbol;
        }
    }

    /**
     * Creates a string that contains only spaces, exactly the given number of
     * them.
     * 
     * @param numberOfSpaces
     * @return string with only spaces
     */
    public static String spaces(int numberOfSpaces) {
        return new String(new char[numberOfSpaces]).replace('\0', ' ');
    }

}
