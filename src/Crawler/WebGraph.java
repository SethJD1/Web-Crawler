package Crawler;

import java.util.ArrayList;

// Object to JSON Conversion Imports
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

/**
 * Graph of the web built by a web crawler. Contents of the graph include
 * the web pages, link and domains. The graph is stored in an adjacency list
 * for the web pages. The links are maintained separately in their own list
 * to allow a specific data structure that supports the intended output.
 *
 * @author Seth Dovgan
 * @version 18JAN19
 */
public class WebGraph {

    @JsonIgnore
    private int domainIDCounter = 0; // Used to keep track of each domain

    @JsonIgnore
    private WebPage source;
    private ArrayList<WebPage> pages;
    private ArrayList<WebLink> links;
    @JsonIgnore
    private ArrayList<Domain> domains;
    @JsonIgnore
    private ArrayList<String> invalidURLs;

    /**
     * Constructor - builds a empty graph and sets the source page in the graph.
     * @param source to set in the graph
     */
    public WebGraph(WebPage source) {

        this.source = source;
        this.pages = new ArrayList<WebPage>();
        this.links = new ArrayList<WebLink>();
        this.domains = new ArrayList<Domain>();
        this.invalidURLs = new ArrayList<String>();
    }

    /**
     * Returns the current graph source web page.
     * @return graph's source web page
     */
    public WebPage getSource() {

        return source;
    }

    /**
     * Returns the adjacency list of web pages in the graph.
     * @return graph's web page adjacency list
     */
    public ArrayList<WebPage> getWebPages(){

        return pages;
    }

    /**
     * Adds the given web page to the graph.
     * @param page to add to the graph
     */
    public void addWebPage(WebPage page){

        pages.add(page);
    }

    /**
     * Returns a list of links in the graph.
     * @return graph's list of links
     */
    public ArrayList<WebLink> getWebLinks(){

        return links;
    }

    /**
     * Adds the given web link to the graph.
     * @param link to add to the graph
     */
    public void addWebLink(WebLink link){

        links.add(link);
    }

    /**
     * Returns a list of domains in the graph.
     * @return graph's domain list
     */
    public ArrayList<Domain> getDomains(){

        return domains;
    }

    /**
     * Adds all the given domains to the graph.
     * @param domains to add to the graph.
     */
    public void addDomains(ArrayList<Domain> domains){

        for(Domain domain: domains){
            addDomain(domain.getDomainName());
        }
    }

    /**
     * Adds the given domain name to the graph and returns and id for it. If the
     * domain already exists in the graph, the domain's id will be returned.
     * @param domainName to add to the graph
     * @return domain's id
     */
    public int addDomain(String domainName){

        // Search through all the graph's domains
        for (Domain domain : domains) {

            // If it exists already, return it's id
            if (domain.getDomainName().toLowerCase().compareTo(domainName.toLowerCase()) == 0) {
                return domain.getDomainId();
            }
        }

        // Add the domain to the graph, and generate a new id for it
        Domain newDomain = new Domain(domainName, domainIDCounter);
        domains.add(newDomain);
        domainIDCounter++;

        return newDomain.getDomainId(); // Return the newly created id
    }

    /**
     * Checks if a domain already exists in the graph.
     * @param domainName to check for it's existence in the graph
     * @return true if it exists and false otherwise
     */
    public boolean containsDomain(String domainName){

        // Search through all the graph's domains
        for (Domain domain : domains) {

            // Check if it exists
            if (domain.getDomainName().toLowerCase().compareTo(domainName.toLowerCase()) == 0) {
                return true;
            }
        }

        return false;
    }

    /**
     * Returns a list of invalid URLs that were not included in the graph.
     * @return list of invalid URLs not included in the graph
     */
    public ArrayList<String> getInvalidURLs(){

        return invalidURLs;
    }

    /**
     * Add an invalid URL to the list.
     * @param url to add to the invalid URL list
     */
    public void addInvalidURL(String url){

        invalidURLs.add(url);
    }


    /**
     * Check if a URL is already present in the graph. If it is, returns the
     * that page. If not, returns null.
     * @param url to check for it's existence in the graph
     * @return a matching web page if it exists or null otherwise
     */
    public WebPage containsWebPage(String url){

        // Search through all the web pages and check for a url match
        for (WebPage page : pages) {

            // If a match is found, return the page
            if (page.getUrl().toLowerCase().compareTo(url.toLowerCase()) == 0) {
                return page;
            }
        }

        return null; // No match was found, return null
    }

    /**
     * Check if a link is already present in the graph. If it is, returns the
     * that link. If not, returns null.
     * @param sourceId to look for in the links
     * @param targetId to look for in the links
     * @return a matching link if it exists or null otherwise
     */
    public WebLink containsWebLink(int sourceId, int targetId){

        // Search through all the web links and check for an id match
        for (WebLink link : links) {

            // If a match is found, return the link
            if (link.getSourceId() == sourceId && link.getTargetId() == targetId) {
                return link;
            }
        }

        return null; // No match was found, return null
    }

    /**
     * Checks if a url already exists in the invalid URL list. If it is, returns
     * true and false otherwise.
     * @param url to check for its existence in the invalid urls list
     * @return true if it exists and false otherwise
     */
    public boolean containsInvalidURL(String url){

        for(String string : invalidURLs){
            if(string.toLowerCase().compareTo(url.toLowerCase()) == 0){
                return true;
            }
        }

        return false;
    }

    /**
     * Returns the number of links in the graph.
     * @return number of links in the graph
     */
    public int getLinkCount() {

        return links.size();
    }

    /**
     * Returns the number of Acyclic links in the graph.
     * @return Acyclic link count in the graph
     */
    public int getAcyclicLinkCount(){

        return links.size() - getCyclicLinkCount();
    }

    /**
     * Returns the number of cyclic links that exist in the graph.
     * @return the number of cyclic links in the graph.
     */
    public int getCyclicLinkCount(){

        int counter = 0;

        // Search through all the links
        for(WebLink link: links){

            // If it's cyclic, count it
            if(link.isCyclic()){
                counter++;
            }
        }

        return counter;
    }

    /**
     * Returns the number of bidirectional links that exist in the graph.
     * @return the number of bidirectional links in the graph.
     */
    public int getBidirectionalLinkCount(){

        int counter = 0;

        // Search through all the links
        for(WebLink link: links){

            // If it's bi-directional, count it
            if(link.isBidirectional()){
                counter++;
            }
        }

        return counter;
    }

    /**
     * Returns the number of domains in the graph
     * @return number of domains in the graph
     */
    public int getDomainCount(){

        return domains.size();
    }

    /**
     * Returns the number of invalid urls encountered in the graph
     * @return invalid url count
     */
    public int getInvalidURLCount() {

        return invalidURLs.size();
    }

    /**
     * Returns the web page the search term was found in during the search. If
     * no term was found, returns null.
     * @return web page the search term was found in
     */
    public WebPage searchTermFoundIn(){

        // Search through all the web pages
        for(WebPage page: pages){

            // If the term was found in this web page, return it
            if(page.searchTermFound()){
                return page;
            }
        }

        return null;
    }

    /**
     * Prints the information transmission sequence in the form of JSON strings
     * in the sequence they were sent to the stdout or the file if selected.
     */
    public void printTransmissionInformation(){

        System.out.println("\n###    INFORMATION TRANSMISSION SEQUENCE    ###\n");

        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(SerializationFeature.INDENT_OUTPUT, true);

        // Print each web pages indexed in the sequence it was indexed
        for(int i = 0; i < pages.size(); i++){

            System.out.println("\nTRANSMISSION # " + (i + 1) + "\n");

            try {

                System.out.println(mapper.writeValueAsString(pages.get(i)));

            } catch (JsonProcessingException e) {

                e.printStackTrace();
            }
        }
    }

    /**
     * Prints the web graph contents with statistical information used in
     * understanding it's contents from an overarching view.
     */
    public void printGraphSummary(){

        System.out.println("\n###    COMPLETE GRAPH    ###\n");

        System.out.println("Number of Pages: " + pages.size());
        System.out.println("Number of Domains: " + domains.size());
        System.out.println("Number of Invalid URLs: " + invalidURLs.size());
        System.out.println("Number of Links: " + links.size());

        int cyclicLinkCount = getCyclicLinkCount();

        System.out.println("Number of Acyclic Links: " + (links.size() - cyclicLinkCount));
        System.out.println("Number of Cyclic Links: " + cyclicLinkCount);
        System.out.println("Number of Bidirectional Links: " + getBidirectionalLinkCount());

        WebPage page = searchTermFoundIn();

        if(page != null){
            System.out.println("Search Term Found (" + page.getId() + "): "
                    + page.getAbbreviatedURL());
        }
    }

    /**
     * Prints a string representation of all the web pages, links and domains
     * in the graph.
     */
    public void printGraphContents(){

        System.out.println("\nWeb Pages\n");

        for (WebPage page : pages) {
            System.out.println("\t" + page.toString());
        }

        System.out.println("\nWeb Links\n");

        for (WebLink link : links) {
            System.out.println("\t" + link.toString());
        }

        System.out.println("\nDomains\n");

        for (Domain domain : domains) {
            System.out.println("\t" + domain.toString());
        }

        System.out.println("\nInvalid URLs\n");

        for (String invalidURL : invalidURLs) {
            System.out.println("\t" + invalidURL);
        }
    }
}
