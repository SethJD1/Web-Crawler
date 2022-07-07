package Crawler;

/**
 * General static validation class to validate strings, integers, and commands
 * entered via command line
 *
 * @author Seth Dovgan
 * @version 21JAN19
 */
public class Validation {

    /**
     * Validates an integer extracted from a string. Returns the value of
     * a integer if it can be extracted from the string, is a positive
     * number and return -1 otherwise.
     * @param integer to validate
     * @return int value if the string is an integer and -1 otherwise
     */
    public static int isValidInteger(String integer){

        try { // Check if the search limit is a number

            int temp = Integer.parseInt(integer);

            if(temp >= 0){
                return temp;
            } else {
                return -1;
            }

            // String provided is not a valid integer
        } catch(NumberFormatException e) {

            return -1;
        }
    }

    /**
     * Validates the string argument and returns a boolean value on it's
     * validity. The string is said to be valid if it's not null and does not
     * only contain whitespace.
     * @param argument to validate
     * @return true if the search term is valid and false otherwise
     */
    public static boolean isValidString(String argument){

        // Check if the string is not null or blank
        return argument != null && argument.compareTo("") != 0;
    }

    /**
     * Returns the option from a specific argument type if the type exists.
     * @param argument to extract the option from
     * @param type of argument used for the extraction
     * @return argument option if valid or null if invalid.
     */
    public static String getOptionFromArgument(String argument, String type){

        // Extract the type and make sure it's in the correct position
        String identifier = argument.substring(0, type.length());

        // Check if the type is contained in the string again
        if(identifier.toLowerCase().compareTo(type.toLowerCase()) == 0
                && argument.length() > type.length()) {

            // Extract the option from the type
            String option = argument.substring(identifier.length());

            // Check if it's valid and return
            if(Validation.isValidString(option)){
                return option;
            }
        }

        return null;
    }

    /**
     * Returns the option type from the argument.
     * @param argument to extract the option type from
     * @return argument option type
     */
    public static Argument getOptionType(String argument){

        final String TRUE = "true";
        final String FALSE = "false";

        // Check for null then compare types
        if(argument != null){

            if(argument.toLowerCase().compareTo(TRUE) == 0){

                return Argument.TRUE;

            } else if(argument.toLowerCase().compareTo(FALSE) == 0){

                return Argument.FALSE;

            } else {
                return Argument.OPTION;
            }
        }

        return Argument.OTHER;
    }
}
