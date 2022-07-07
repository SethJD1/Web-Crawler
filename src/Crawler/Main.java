package Crawler;

/**
 * Executes the web crawler program given a url, search type, search limit
 * and optional arguments. Results of the search will be sent to stdout by default
 * in the form of JSON objects as each page is indexed. Both the page, and it's
 * predecessor's links are sent in each JSON. This backward looking
 * implementation is used due to the incremental updates necessary to build
 * a live graph.
 *
 * # Command Line Arguments:
 * arg[0] url; String
 * arg[1] search type; "B" for breadth first, "D" for depth first or "X" for random
 * arg[2] search limit; Integer
 * arg[3-10] (optional)
 *  - STDOUT-true/false: send incremental json to STDOUT                        (Default true)
 *  - FILE-"name"/false: send incremental json to file name                     (Default false)
 *  - SEARCH-"term"/false: search for a term during the crawl                   (Default false)
 *  - DELAY-100/false: set a delay in milliseconds between page indexing        (Default false)
 *  - RANDOM-true/false: randomly select next web page link                     (Default true)
 *  - RANDOMUA-true/false: randomly selects user agent for each page search     (Default false)
 *  - CUSTOMUA-"user agent"/false: set a custom user agent for the search       (Default false)
 *  - CYCLIC-true/false: Sets the graph results to cyclic or acyclic            (Default true)
 *  - LINK-ALL/REL/ABS: Sets the link types to search for                       (Default ALL)
 *
 * # Error Code List:
 * 101 - Invalid URL
 * 102 - Invalid Search Type
 * 103 - Invalid Limit, Set To Default
 *
 * # Controller Commands
 *  - Pause Crawler: P
 *  - Resume Crawler: R
 *  - STOP Crawler and Terminate: ACK
 *  - Breadth First Search: B
 *  - Depth First Search: D
 *  - Random Search: X
 *  - Cyclic Graph: C
 *  - Acyclic Graph: A
 *  - Delay: DM
 *  - Absolute Links: ABS
 *  - Relative Links: REL
 *  - Both Links: ALL
 *  - Height Limit: HL
 *  - Search Limit: SL
 *  - Search Term: ST
 * *** USER MUST TYPE IN "ACK" COMMAND TO TERMINATE THE PROGRAM
 *
 * @author Seth Dovgan
 * @version 18JAN19
 */
public class Main {

    private static final int BASE_ARG_COUNT = 3;
    private static final String BREADTH_FIRST = "B";
    private static final String DEPTH_FIRST = "D";
    private static final String RANDOM_SEARCH = "X";

    /**
     * Executes the web crawl program with the given arguments.
     * @param args to use for the crawl
     */
    public static void main(String[] args) {

        // Check if the minimum base arguments are valid
        if(validateBaseArguments(args)){

            WebCrawler crawler = new WebCrawler(args[0]);
            setWebCrawlerOptions(args, crawler);

            int limit = Integer.parseInt(args[2]);

            if(args[1].compareTo(BREADTH_FIRST) == 0){

                crawler.executeThreadedCrawl(limit, DataStructureType.QUEUE);

            } else if(args[1].compareTo(DEPTH_FIRST) == 0){

                crawler.executeThreadedCrawl(limit, DataStructureType.STACK);

            } else if(args[1].compareTo(RANDOM_SEARCH) == 0){

                crawler.executeThreadedCrawl(limit, DataStructureType.BAG);
            }
        }
    }

    /**
     * Sets the options for the web crawler prior to execution using the given
     * command line arguments and parse the variables from within each argument.
     * @param args to parse and get the options from
     * @param crawler to set the options for
     */
    private static void setWebCrawlerOptions(String[] args, WebCrawler crawler){

        final String JSON_TO_STDOUT = "STDOUT-";
        final String JSON_TO_FILE = "FILE-";
        final String SEARCH_FOR_TERM = "SEARCH-";
        final String DELAY_INTERVAL = "DELAY-";
        final String RANDOM_SEARCH = "RANDOM-";
        final String RANDOM_USER_AGENT = "RANDOMUA-";
        final String CUSTOM_USER_AGENT = "CUSTOMUA-";
        final String CYCLIC = "CYCLIC-";
        final String LINK_TYPE = "LINK-";

        // Loop through the remaining argument options, starting from the
        // argument after the base arguments
        for(int i = BASE_ARG_COUNT; i < args.length; i++){

            String option;

            // Stdout Option
            if(args[i].toLowerCase().contains(JSON_TO_STDOUT.toLowerCase())){

                option = Validation.getOptionFromArgument(args[i], JSON_TO_STDOUT);

                if(option != null){

                    Argument type = Validation.getOptionType(option);

                    if(type == Argument.TRUE){
                        crawler.sendJSONtoStdout(true);
                    } else if(type == Argument.FALSE){
                        crawler.sendJSONtoStdout(false);
                    }
                }

            // File Option
            } else if(args[i].toLowerCase().contains(JSON_TO_FILE.toLowerCase())){

                option = Validation.getOptionFromArgument(args[i], JSON_TO_FILE);

                if(option != null){

                    Argument type = Validation.getOptionType(option);

                    if(type == Argument.OPTION){
                        crawler.sendJSONToFile(option);
                    } else if(type == Argument.FALSE){
                        crawler.turnSendJSONToFileOff();
                    }
                }

            // Search Option
            } else if(args[i].toLowerCase().contains(SEARCH_FOR_TERM.toLowerCase())){

                option = Validation.getOptionFromArgument(args[i], SEARCH_FOR_TERM);

                if(option != null){

                    Argument type = Validation.getOptionType(option);

                    if(type == Argument.OPTION){
                        crawler.setSearchTerm(option);
                    } else if(type == Argument.FALSE){
                        crawler.turnSearchForTermOff();
                    }
                }

            // Delay Option
            } else if(args[i].toLowerCase().contains(DELAY_INTERVAL.toLowerCase())){

                option = Validation.getOptionFromArgument(args[i], DELAY_INTERVAL);

                if(option != null){

                    Argument type = Validation.getOptionType(option);

                    if(type == Argument.OPTION){
                        crawler.setDelayInterval(Validation.isValidInteger(option));
                    } else if(type == Argument.FALSE){
                        crawler.setDelayInterval(0);
                    }
                }

            // Random Option
            } else if(args[i].toLowerCase().contains(RANDOM_SEARCH.toLowerCase())){

                option = Validation.getOptionFromArgument(args[i], RANDOM_SEARCH);

                if(option != null){

                    Argument type = Validation.getOptionType(option);

                    if(type == Argument.TRUE){
                        crawler.randomizeTraversal(true);
                    } else if(type == Argument.FALSE){
                        crawler.randomizeTraversal(false);
                    }
                }

            // Random User Agent Option
            } else if(args[i].toLowerCase().contains(RANDOM_USER_AGENT.toLowerCase())){

                option = Validation.getOptionFromArgument(args[i], RANDOM_USER_AGENT);

                if(option != null){

                    Argument type = Validation.getOptionType(option);

                    if(type == Argument.TRUE){
                        crawler.randomizeUserAgent();
                    } else if(type == Argument.FALSE){
                        crawler.setUserAgentToDefault();
                    }
                }

            // Custom User Agent Option
            } else if(args[i].toLowerCase().contains(CUSTOM_USER_AGENT.toLowerCase())){

                option = Validation.getOptionFromArgument(args[i], CUSTOM_USER_AGENT);

                if(option != null){

                    Argument type = Validation.getOptionType(option);

                    if(type == Argument.OPTION){
                        crawler.setCustomUserAgent(option);
                    } else if(type == Argument.FALSE){
                        crawler.setUserAgentToDefault();
                    }
                }

            // Cyclic / Acyclic graph option
            } else if(args[i].toLowerCase().contains(CYCLIC.toLowerCase())){

                option = Validation.getOptionFromArgument(args[i], CYCLIC);

                if(option != null){

                    Argument type = Validation.getOptionType(option);

                    if(type == Argument.TRUE){
                        crawler.buildCyclicGraph(true);
                    } else if(type == Argument.FALSE){
                        crawler.buildCyclicGraph(false);
                    }
                }

            // ABS / REL / ALL Link type option
            } else if(args[i].toLowerCase().contains(LINK_TYPE.toLowerCase())){

                option = Validation.getOptionFromArgument(args[i], LINK_TYPE);

                if(option != null){

                    Argument type = Validation.getOptionType(option);

                    if(type == Argument.OPTION){

                        if(option.toLowerCase().compareTo("ALL".toLowerCase()) == 0){
                            crawler.setLinkType(LinkType.ALL);
                        } else if(option.toLowerCase().compareTo("ABS".toLowerCase()) == 0){
                            crawler.setLinkType(LinkType.ABS);
                        } else if(option.toLowerCase().compareTo("REL".toLowerCase()) == 0){
                            crawler.setLinkType(LinkType.REL);
                        }
                    }
                }

            } else { // Send invalid command to stderr but still conduct crawl
                System.err.print("Invalid Command Line Argument: " + args[i]);
            }
        }
    }

    /**
     * Validates the minimum number of arguments the crawler needs to execute.
     * In the event of an invalid argument, the program will exit with a
     * specified exit code and the argument in question will be sent to
     * stderr.
     * @param args to validate
     * @return true if all the arguments are valid or exit the program in the
     * event of a invalid argument found
     */
    private static boolean validateBaseArguments(String[] args){

        // Validate URL
        if(!Validation.isValidString(args[0])){

            System.err.print("Error: 101 - URL");
            System.exit(104);
        }

        // Validate Search Term
        if(!Validation.isValidString(args[1]) || !isValidSearchType(args[1])){

            System.err.print("Error: 102 - Invalid Search Type");
            System.exit(102);
        }

        // Validate Search Limit
        if(Validation.isValidInteger(args[2]) == -1){

            System.err.print("Error: 103 - Invalid Limit");
            System.exit(105);
        }

        return true;
    }

    /**
     * Validates the search type to ensure it's either of the specific types.
     * @param searchType to validate
     * @return true if the search type is valid and false otherwise.
     */
    private static boolean isValidSearchType(String searchType){

        return searchType.compareTo(BREADTH_FIRST) == 0
                || searchType.compareTo(DEPTH_FIRST) == 0
                || searchType.compareTo(RANDOM_SEARCH) == 0;
    }
}
