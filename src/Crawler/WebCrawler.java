package Crawler;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Web crawler class that executes three different crawl types include a
 * depth-first, breadth-first and random search. Each crawler incrementally
 * (after each page is indexed) sends a JSON file to stdout of the page index
 * including it's predecessor links. The graph will include cyclic links,
 * as well as, annotating whether a link is bi-directional. After completing
 * a crawl, a graph is returned that captures all the data send to stdout
 * along with other information about the graph itself.
 *
 * @author Seth Dovgan
 * @version 18JAN19
 */
public class WebCrawler {

    private String url;

    // Default search preferences which can be modified by the user prior to
    // a search execution
    private boolean sendJSONtoStdout;
    private boolean sendJSONtoFile;
    private String jsonFileName;
    private boolean randomizeTraversal;
    private boolean useRandomUserAgent;
    private boolean useCustomUserAgent;
    private String customUserAgent;
    private FileProcessor file;

    // Used for Pause/Resume/Control functionality via threads
    private boolean isThreadedCrawl;
    private final AtomicBoolean paused;
    private final AtomicInteger dataStructureType;
    private final AtomicInteger delay;
    private final AtomicBoolean cyclic;
    private final AtomicInteger linkType;
    private final AtomicInteger heightLimit;
    private final AtomicInteger limit;
    private final AtomicBoolean enableSearchTerm;
    private final AtomicReference<String> searchTerm;

    /**
     * Constructor - Sets the source url to start the web crawl from and all
     * options are set to default.
     * @param url to start the search from.
     */
    public WebCrawler(String url){

        this.url = url;

        // Variables - Default Settings
        sendJSONtoStdout = true;                // Pages Incrementally sent to stdout
        sendJSONtoFile = false;                 // Pages NOT sent to file
        jsonFileName = "";
        randomizeTraversal = true;              // Randomize Traversal
        useRandomUserAgent = false;             // Robot User Agent Set
        useCustomUserAgent = false;             // Robot User Agent Set
        customUserAgent = "";
        isThreadedCrawl = false;                // Maintains crawl type

        // Thread Safe Variables - Default Settings
        heightLimit = new AtomicInteger(Integer.MAX_VALUE);
        limit = new AtomicInteger(0);
        enableSearchTerm = new AtomicBoolean(false);
        paused = new AtomicBoolean(false);
        cyclic = new AtomicBoolean(false);
        dataStructureType = new AtomicInteger(DataStructureType.QUEUE.getId());
        delay = new AtomicInteger(0);
        linkType = new AtomicInteger(LinkType.ALL.getId());
        searchTerm = new AtomicReference<String>(null);
    }

    /**
     * Helper function for the web crawl. Eliminates the need
     * to provide an argument to execute a crawl in a thread.
     * @param searchLimit for the search
     * @param type of search traversal structure to use
     * @return graph of the web crawl
     */
    public WebGraph executeCrawl(int searchLimit, DataStructureType type){

        isThreadedCrawl = false;
        final QueBaStack queBaStack = new QueBaStack(type);
        return executeCrawl(searchLimit, queBaStack, null);
    }

    /**
     * Executes a web crawl with pause and resume functionality controlled by
     * commands read in from stdin.
     * @param limit for the search
     * @param type of search traversal structure to use
     */
    public void executeThreadedCrawl(final int limit, DataStructureType type){

        isThreadedCrawl = true;

        final Object lock = new Object();
        final QueBaStack queBaStack = new QueBaStack(type);

        Thread thread = new Thread(new Runnable(){

            @Override
            public void run(){
                executeCrawl(limit, queBaStack, lock);
            }
        });

        thread.start();

        // Start the crawler controller
        CrawlerController controller = new CrawlerController();
        controller.controlCrawler(lock, paused, delay, cyclic, linkType,
                queBaStack, heightLimit, this.limit, enableSearchTerm, searchTerm);

        try { // Wait for the thread to die, before continuing
            thread.join();
        } catch (InterruptedException e) {
            System.err.println("Thread interrupted exception");
        }
    }

    /**
     * Executes a web crawl from a source URL until a page or height limit is
     * reach, the search term was found or no further links exists.
     * If the search term provided is either null or only whitespace, the
     * search will not look for a term.
     * @param limit for the search
     * @param queBaStack data structure to use in crawler, determining its traversal
     * @param lock thread lock
     * @return cyclic graph of the web crawl
     */
    private WebGraph executeCrawl(int limit, final QueBaStack queBaStack, final Object lock) {

        int limitCounter = 0;
        this.limit.set(limit);

        // Create a new graph and source page for the crawl
        WebPage source = new WebPage(url, 0);
        WebGraph graph = new WebGraph(source);
        queBaStack.insert(source); // Add source to the stack

        // Create a web page indexer and set it's values
        WebPageIndexer indexer = new WebPageIndexer();
        defineIndexerUserAgent(indexer);

        // Search through and index all the web pages encountered by the crawl
        // as longs as the pages are less than max pages, the height limit
        // isn't exceeded and there are still pages left to visit
        while(!queBaStack.isEmpty() && queBaStack.get().getHeight() <= heightLimit.get()
                && limitCounter < this.limit.get()) {

            // Location to pause/resume/control the crawler on-the-fly
            allowControllerAccess(lock, paused, queBaStack);

            // Get the next page that hasn't been indexed
            WebPage page = queBaStack.remove();
            indexer.setUrl(page.getUrl());

            // Check if the page can be indexed and if its not an invalid url
            if(!graph.containsInvalidURL(page.getUrl())
                    && indexer.connectAndRetrieveHtml()
                    && !includeOnlyAbsoluteLinks(page, graph)){

                // Get the page's links and set the traversal (sequential or random)
                ArrayList<String> links = indexer.getLinks();
                setLinkTraversal(links);

                // Check if the search was found if searching for a term
                if(enableSearchTerm.get() && indexer.searchTermFound(searchTerm.get())){

                    // Only index the page without discovering it's links
                    // since the search is ended with the search term found
                    indexWebPage(page, indexer, graph);
                    attachLinks(page, graph, links, true);

                    page.setSearchTermFound(true);
                    queBaStack.clear(); // Clear the structure to end the search

                } else { // Search term not found or no search was conducted

                    // Maximum page limit / Height reached
                    if(limitCounter == limit - 1 || page.getHeight() == heightLimit.get()) {

                        // Only index the page without discovering it's links
                        // since the search is ended and only indexing will take
                        // place from here forward.
                        indexWebPage(page, indexer, graph);
                        attachLinks(page, graph, links, true);

                    } else {

                        // Index the page and discover it's links
                        indexWebPage(page, indexer, graph);
                        attachLinks(page, graph, links, false);

                        // Add all the links to the processing data structure
                        for(int i = 0; i < page.getTargetLinkCount(); i++){
                            queBaStack.insert(page.getTargetLink(i));
                        }
                    }
                }

                limitCounter++; // Increment maximum search limit

                // Send the page to an output if option(s) is enabled
                processPageOutput(page);

            } else { // Invalid URL

                // Add to list of invalid URLs; an error/exception was thrown
                // connecting to or retrieving its contents.
                graph.addInvalidURL(page.getUrl());
            }

            // Optional Delay setting to aid easing detection
            startOptionalDelay();
        }

        closeOutputFile();          // Close file if option enabled
        disabledControllerAccess(); // Shutdown access to the controller if enabled

        return graph;
    }

    /**
     * Attaches the given links to the web page object including the target and
     * predecessor links. The boolean option specifies to attach only the
     * backward looking predecessor links , i.e. cyclic and bi-directional.
     * @param source page to attach the link to
     * @param graph the page is added to
     * @param links to attach to the given page
     * @param onlyBackwardLinks option to attach all links or backward looking
     *                          links only
     */
    private void attachLinks(WebPage source, WebGraph graph, ArrayList<String> links,
                             boolean onlyBackwardLinks){

        // Filter absolute or relative links if option is selected
        includeOnlyRelativeLinks(links, source, graph);

        // Loop through all the source links
        for(String link : links) {

            WebPage target = graph.containsWebPage(link);

            // Web Page already exists in the graph, meaning it's cyclic or bi-directional
            if(target != null){

                // Add bi-directional link
                if(WebLink.isBidirectional(source.getPredecessorLinks().get(0), source, target)){

                    source.getPredecessorLinks().get(0).setCyclic(false);
                    source.getPredecessorLinks().get(0).setBidirectional(true);

                } else if(cyclic.get()){ // Add cyclic link

                    WebLink newLink = new WebLink(source, target, true);
                    graph.addWebLink(newLink);
                    source.addPredecessorLink(newLink);
                }

            // Add the link to the parent since it's new
            } else if(!onlyBackwardLinks) {

                target = new WebPage(link, (source.getHeight() + 1));
                target.addPredecessorLink(new WebLink(source, target, false));
                source.addTargetLink(target);
            }
        }
    }

    /**
     * Filters out relative links if the absolute filter option is enabled. If
     * the option is selected and the page is relative in the graph, returns
     * true, otherwise it returns false.
     * @param page to check it's domain is already in the graph
     * @param graph to check the page against
     * @return true if the ABS options is selected
     */
    private boolean includeOnlyAbsoluteLinks(WebPage page, WebGraph graph) {

        return linkType.get() == LinkType.ABS.getId() && graph.containsDomain(page.getHostname());
    }

    /**
     * Filter the links to only include relative links.
     * @param links to filter
     * @param page that contains the domain/hostname
     */
    private void includeOnlyRelativeLinks(ArrayList<String> links, WebPage page, WebGraph graph){

        // If no filtering is necessary, skip this process
        if(linkType.get() != LinkType.ALL.getId()){

            if(linkType.get() == LinkType.REL.getId()){

                Iterator<String> linkItr = links.iterator();

                // Loop though all the links and filter
                while (linkItr.hasNext()) {

                    String link = linkItr.next();

                    // Remove domains that do NOT match
                    if(!page.hasSameDomain(link)){
                        linkItr.remove();
                    }
                }
            }
        }
    }

    /**
     * Indexes the given web page and adds it to the given graph.
     * @param page page to index
     * @param indexer indexer used get the details of the given page
     * @param graph to add the page to
     */
    private void indexWebPage(WebPage page, WebPageIndexer indexer, WebGraph graph){

        // Index the page
        indexer.populateWebPageWithIndexedValues(page);
        page.setState(State.INDEXED);

        // Add the page to the graph and set the domain id
        graph.addWebPage(page);
        page.setGroupId(graph.addDomain(page.getHostname()));

        // Add all predecessor links to the graph.
        for(WebLink webLink: page.getPredecessorLinks()){
            graph.addWebLink(webLink);
        }
    }

    /**
     * Allows the Controller access to the crawler options, enabling the
     * crawler to be paused, resumed and options changed on-the-fly via
     * std-in commands.
     * @param lock for the crawler thread
     * @param queBaStack structure to modify on-the-fly
     */
    private void allowControllerAccess(final Object lock, final AtomicBoolean paused,
                                       final QueBaStack queBaStack){

        // Allow controller access to the crawler thread if a lock was given
        if(lock != null){

            synchronized (lock){

                // Only resume execution if the search is in a paused state
                while(paused.get()) {

                    try {
                        lock.wait(); // Put in search thread in a sleep state
                    } catch(InterruptedException e) {
                        System.err.println("Error with pause function");
                    }
                }
            }

            // Mark last position in the graph before adding more pages
            queBaStack.markLastRemovePosition();
        }
    }

    /**
     * Sets the traversal for a set of links based on whether the user options
     * called for a standard or randomized traversal.
     * @param links to set the traversal for
     */
    private void setLinkTraversal(ArrayList<String> links){

        if(randomizeTraversal){
            Collections.shuffle(links);
        }
    }

    /**
     * Process the output of the web page if any are enabled by the user.
     * @param page to process
     */
    private void processPageOutput(WebPage page){

        // Send the page and predecessor links to stdout if option enabled
        if(sendJSONtoStdout){
            System.out.print(page.toJson());
        }

        // Send the page and predecessor link to file is option enabled
        if (sendJSONtoFile){
            file.writeJSONToFile(page.toJson());
        }
    }

    /**
     * Closes the file open used to capture the results of the crawl.
     */
    private void closeOutputFile(){

        // Teardown the file if one was created
        if(sendJSONtoFile){
            file.teardown();
        }
    }

    /**
     * Start the optional page indexing delay if one was enabled by the user.
     */
    private void startOptionalDelay(){

        // Add a delay before processing the next web page
        if(delay.get() > 0){

            try {

                // Optional Delay setting to aid easing detection
                Thread.sleep(delay.get());

            } catch(InterruptedException e){
                System.err.println("Sleep Exception Thrown");
            }
        }
    }

    /**
     * Notifies the receiving/controlling process the crawl has ended so
     * controller communication between the two processes can end.
     */
    private void disabledControllerAccess(){

        if(isThreadedCrawl){

            System.out.print("REQ_CONT_SHUTDOWN");

            try { // Delay to allow message transfer back through server
                Thread.sleep(200);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Sets the data structure type used in the crawl.
     * Breadth-First Search: QUEUE
     * Depth-First Search: STACK
     * Bag: RANDOM
     * @param type of data structure to use for the crawler. The type specified
     *             will determine what type a search is conducted.
     */
    public void setDataStructureType(DataStructureType type){

        this.dataStructureType.set(type.getId());
    }

    /**
     * Sets the type of links to search for during the crawl.
     * @param type of link to search for
     */
    public void setLinkType(LinkType type){
        this.linkType.set(type.getId());
    }

    /**
     * Sets a term to search for while crawling each page. If the term is valid,
     * a search will be conducted during the crawl. If the term is found,
     * the search ends.
     * @param term to search for
     */
    public void setSearchTerm(String term) {

        if(Validation.isValidString(term)){

            enableSearchTerm.set(true);
            searchTerm.set(term);

        } else {
            enableSearchTerm.set(false);
            searchTerm.set(null);
        }
    }

    /**
     * Sets the search term option to off during a crawl
     */
    public void turnSearchForTermOff(){

        enableSearchTerm.set(false);
    }

    /**
     * Sets a maximum page limit for a breadth-first search.
     * @param limit to set for the breadth first search
     */
    public void setHeightLimit(int limit){

        heightLimit.set(limit);
    }

    /**
     * Sets the maximum page limit to the default value of the max integer value.
     */
    public void setHeightLimitToDefault(){

        heightLimit.set(Integer.MAX_VALUE);
    }

    /**
     * Sets whether to incrementally send JSON strings to stdout
     * @param sendResultsToStdout for each page indexed incrementally
     */
    public void sendJSONtoStdout(boolean sendResultsToStdout){

        sendJSONtoStdout = sendResultsToStdout;
    }

    /**
     * Sets whether to incrementally send JSON strings to file
     * @param fileName to store the JSON Strings in
     */
    public void sendJSONToFile(String fileName){

        if(Validation.isValidString(fileName)){

            sendJSONtoFile = true;
            jsonFileName = fileName;

            file = new FileProcessor(jsonFileName);
            file.setup();

        } else {

            sendJSONtoFile = false;
        }
    }

    /**
     * Sets the JSON file output to false.
     */
    public void turnSendJSONToFileOff(){

        sendJSONtoStdout = false;
    }

    /**
     * Sets a time delay for each page index interval.
     * @param delay in milliseconds to set for each index interval
     */
    public void setDelayInterval(int delay){

        this.delay.set(delay);
    }

    /**
     * Sets whether to randomly select the next link on a page or not. This
     * method will primarily be used for testing purposes.
     * @param randomizeTraversal toggle to on or off
     */
    public void randomizeTraversal(boolean randomizeTraversal){

        this.randomizeTraversal = randomizeTraversal;
    }

    /**
     * Sets a random user agent during each page crawl.
     */
    public void randomizeUserAgent(){

        useRandomUserAgent = true;
        useCustomUserAgent = false;
    }

    /**
     * Sets a user defined custom agent during the crawl.
     * @param userAgent to use for the crawl
     */
    public void setCustomUserAgent(String userAgent){

        if(Validation.isValidString(userAgent)){

            useCustomUserAgent = true;
            customUserAgent = userAgent;
            useRandomUserAgent = false;

        } else {
            setUserAgentToDefault();
            customUserAgent = "";
        }
    }

    /**
     * Sets the user agent for page indexing to the default indexer value.
     */
    public void setUserAgentToDefault() {

        useRandomUserAgent = false;
        useCustomUserAgent = false;
        customUserAgent = "";
    }

    /**
     * Sets the user agent for the indexer.
     * @param indexer to set the user agent in
     */
    private void defineIndexerUserAgent(WebPageIndexer indexer){

        // Define user agents if not default
        if(useCustomUserAgent){

            indexer.setCustomUserAgentForConnection(customUserAgent);

        } else if(useRandomUserAgent){
            indexer.useRandomUserAgent(true);
        }
    }

    /**
     * Sets the cyclic / acyclic graph type based on the parameter
     * @param cyclic type selected
     */
    public void buildCyclicGraph(boolean cyclic){

        this.cyclic.set(cyclic);
    }
}
