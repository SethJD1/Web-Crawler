package Crawler;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Morphing data structure class, that allows the attributes to change to that
 * of a queue, bag or stack based on the structure type selected on initialization
 * or during operation. In the event of a mid-operation change, the beginning
 * of the QUEUE starts where the last STACK ended. This allows for graph
 * building to search wide (QUEUE) from the last point in deep search (STACK).
 *
 * @author Seth Dovgan
 * @version 18JAN19
 */
public class QueBaStack {

    private List<WebPage> pages;    // List of web pages
    private DataStructureType type; // Type of Data Structure to use
    private Random random;          // Random Number generator for bag
    private int nextRandom;         // Next random number (index)
    private int next;               // Next QUEUE (index)

    /**
     * Creates a new empty QueBaStack morphing data structure
     * @param type of structure to begin with
     */
    public QueBaStack(DataStructureType type){

        this.type = type;
        pages = new ArrayList<WebPage>();
        random = new Random();
        nextRandom = 0;
        next = 0;
    }

    /**
     * Inserts a web page into the data structure based on the selected
     * data structure behavior. Replaces ADD for a QUEUE and BAG and push for
     * a STACK.
     * @param page to insert into the data structure
     */
    public void insert(WebPage page){

        pages.add(page);            // Add to end QUEUE/STACK/BAG

        // Set the next random index for a bag so the get and remove will return
        // the same web page.
        setNextRandom(pages.size());
    }

    /**
     * Returns a web page from the data structure based on the selected data
     * structure behavior. Replaces poll for QUEUE, peek for STACK and
     * contains for BAG
     * @return a web page or null if the structure is empty.
     */
    public WebPage get(){

        // Get a web page from the beginning to replicate QUEUE behavior
        if(type == DataStructureType.QUEUE){

            // Use next index so behavior is left off from the last known
            // STACK position
            if(next >= pages.size()){
                next = pages.size() - 1;
            }

            return pages.get(next);

        // Get a web page from the back to replicate STACK behavior
        } else if(type == DataStructureType.STACK){

            return pages.get(pages.size() - 1);

        // Get a random page from the structure
        } else {
            return pages.get(nextRandom);
        }
    }

    /**
     * Removes and returns a web page from the data structure based on the
     * selected data structure behavior. Replaces remove for QUEUE and BAG,
     * peek for STACK.
     * @return a web page or null if the data structure is empty.
     */
    public WebPage remove(){

        // Remove a web page from the beginning to replicate QUEUE behavior
        if(type == DataStructureType.QUEUE){

            // Use next index so behavior is left off from the last known
            // STACK position
            if(next >= pages.size()){
                next = pages.size() - 1;
            }

            return pages.remove(next);

        // Remove a web page from the back to replicate STACK behavior
        } else if(type == DataStructureType.STACK){

            return pages.remove(pages.size() - 1);  // Remove From End STACK

        // Remove a random page from the structure
        } else {

            // Set the next random index so a get or remove will be looking at
            // the same page.
            int temp = nextRandom;
            setNextRandom(pages.size() - 1);
            return pages.remove(temp);
        }
    }

    /**
     * Checks if the data structure is empty or not.
     * @return true if the data structure is empty and false otherwise.
     */
    public boolean isEmpty(){

        return pages.isEmpty();
    }

    /**
     * Clears all the web pages from the data structure.
     */
    public void clear() {

        pages.clear();
    }

    /**
     * Sets the next random index to use for remove and get operations.
     * @param limit for the next random index.
     */
    private void setNextRandom(int limit){

        if(limit < 1){
            nextRandom = 0;
        } else {
            nextRandom = random.nextInt(limit);
        }
    }

    /**
     * Sets the data structure type for the morphing data structure.
     * @param type to set for the data structure
     */
    public void setDataStructureType(DataStructureType type){

        this.type = type;
    }

    /**
     * Sets the next QUEUE remove position to the last position in which
     * a STACK or QUEUE added web pages. Setting this position, affords
     * BFS searches to begin where they left off at the last STACK or BAG
     * web page. A remove operation must be conducted after calling this
     * method to ensure the positioning is correct.
     */
    public void markLastRemovePosition(){

        // Set a new starting index for a QUEUE to begin at since the last
        // data structure used place all the elements at the end of the structure.
        // This affords searches to begin where they left off.
        if(type == DataStructureType.STACK || type == DataStructureType.BAG){

            if(pages.size() <= 2){
                next = 0;
            } else {
                next = pages.size() - 2;
            }
        }
    }
}