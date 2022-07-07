package Crawler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Crawler Controller which runs on the main process and controls a threaded
 * crawler execution. Once a threaded crawl is executed, commands from stdin
 * will control option features such as the search type, pause / resume,
 * cyclic / acyclic links, and implementing a delay.
 *
 * @author Seth Dovgan
 * @version 18JAN19
 */
public class CrawlerController {

    private BufferedReader reader;

    /**
     * Constructor. Setups the stdin communication reader used to read in and
     * process commands.
     */
    public CrawlerController(){

        reader = new BufferedReader(new InputStreamReader(System.in));
    }

    /**
     * Controls the crawler through changing the given thread safe options.
     *
     * @param lock object used to maintain states between thread and main process
     * @param paused option for the crawler
     * @param delay option for the crawler
     * @param cyclic option for the crawler
     * @param linkType option for the crawler
     * @param queBaStack morphing data structure for changing search types
     */
    public void controlCrawler(final Object lock,
                               final AtomicBoolean paused,
                               final AtomicInteger delay,
                               final AtomicBoolean cyclic,
                               final AtomicInteger linkType,
                               final QueBaStack queBaStack,
                               final AtomicInteger height,
                               final AtomicInteger limit,
                               final AtomicBoolean enableSearchTerm,
                               final AtomicReference<String> searchTerm){

        final String PAUSE_CRAWL = "P";
        final String RESUME_CRAWL = "R";
        final String STOP_CONTROLLER = "ACK";
        final String BREADTH_FIRST = "B";
        final String DEPTH_FIRST = "D";
        final String RANDOM = "X";
        final String CYCLIC = "C";
        final String ACYCLIC = "A";
        final String DELAY = "DM";
        final String ABS_LINKS = "ABS";
        final String REL_LINKS = "REL";
        final String ALL_LINKS = "ALL";
        final String HEIGHT_LIMIT = "HL";
        final String SEARCH_LIMIT = "SL";
        final String SEARCH_TERM = "ST";

        boolean stopController = false;

        // Check to make sure the thread is not destroyed before reading in
        // another command
        while(!stopController){

            try {

                // Read in the command from stdin
                String command = reader.readLine();

                if(command != null){

                    if(command.compareTo(PAUSE_CRAWL) == 0 && !paused.get()){

                        paused.set(true); // Pause Crawler Thread

                        synchronized (lock){
                            lock.notifyAll(); // Notify search to resume execution
                        }

                    } else if(command.compareTo(RESUME_CRAWL) == 0 && paused.get()){

                        paused.set(false); // Resume Crawler Thread

                        synchronized (lock){
                            lock.notifyAll(); // Notify search to resume execution
                        }

                    } else if(command.compareTo(STOP_CONTROLLER) == 0){

                        // Modify crawler search limit
                        synchronized (limit){
                            limit.set(0);
                        }

                        // Exit the controlling loop
                        stopController = true;

                    } else if(command.compareTo(BREADTH_FIRST) == 0){

                        // Change graph search to Breadth-first search
                        synchronized (queBaStack){
                            queBaStack.setDataStructureType(DataStructureType.QUEUE);
                        }

                    } else if(command.compareTo(DEPTH_FIRST) == 0){

                        // Change graph search to Depth-first search
                        synchronized (queBaStack){
                            queBaStack.setDataStructureType(DataStructureType.STACK);
                        }

                    } else if(command.compareTo(RANDOM) == 0){

                        // Change graph search to Random Search
                        synchronized (queBaStack){
                            queBaStack.setDataStructureType(DataStructureType.BAG);
                        }

                    } else if(command.compareTo(CYCLIC) == 0){

                        // Toggle on cyclic links
                        synchronized (cyclic){
                            cyclic.set(true);
                        }

                    } else if(command.compareTo(ACYCLIC) == 0){

                        // Toggle off cyclic links
                        synchronized (cyclic){
                            cyclic.set(false);
                        }

                    } else if(command.compareTo(ABS_LINKS) == 0){

                        // Toggle ABS Links
                        synchronized (linkType){
                            linkType.set(LinkType.ABS.getId());
                        }

                    } else if(command.compareTo(REL_LINKS) == 0){

                        // Toggle REL Links
                        synchronized (linkType){
                            linkType.set(LinkType.REL.getId());
                        }

                    } else if(command.compareTo(ALL_LINKS) == 0){

                        // Toggle ALL Links
                        synchronized (linkType){
                            linkType.set(LinkType.ALL.getId());
                        }

                    } else if(command.substring(0, 2).compareTo(DELAY) == 0){

                        // Add crawling delay to the crawler
                        synchronized (delay){
                            int temp = Validation.isValidInteger(command.substring(2, command.length()));

                            if(temp >= 0){
                                delay.set(temp);
                            }
                        }

                    } else if(command.substring(0, 2).compareTo(HEIGHT_LIMIT) == 0) {

                        // Add height limit to the crawler
                        synchronized (height){
                            int temp = Validation.isValidInteger(command.substring(2, command.length()));

                            if(temp >= 0){
                                height.set(temp);
                            }
                        }

                    } else if(command.substring(0, 2).compareTo(SEARCH_LIMIT) == 0){

                        // Modify crawler search limit
                        synchronized (limit){
                            int temp = Validation.isValidInteger(command.substring(2, command.length()));

                            if(temp >= 0){
                                limit.set(temp);
                            }
                        }

                    } else if(command.substring(0, 2).compareTo(SEARCH_TERM) == 0){

                        String option = Validation.getOptionFromArgument(command, SEARCH_TERM);

                        if(option != null){

                            Argument type = Validation.getOptionType(option);

                            // Modify Search Term
                            synchronized (enableSearchTerm){

                                if(type == Argument.OPTION){
                                    searchTerm.set(option);
                                    enableSearchTerm.set(true);
                                } else {
                                    enableSearchTerm.set(false);
                                }
                            }
                        }
                    }
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
