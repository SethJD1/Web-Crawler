package Crawler;

import java.util.ArrayList;
import java.util.Arrays;
import java.io.IOException;
import java.util.Set;
import java.util.HashSet;

// HTML Parser Imports
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 * Connects to and parses a web page for various bits of information including
 * it's links, keywords, title, word count, byte count, etc. The default user
 * agent is the adheres to the Robots.txt file. However, the user can specific
 * a different user agent as they see fit.
 *
 * @author Seth Dovgan
 * @version 18JAN19
 */
public class WebPageIndexer {

    private String url;
    private Document htmlDoc;
    private String userAgent;
    private boolean useRandomUserAgent;
    private boolean isDeadEnd;

    /**
     * Constructor - sets the url of the page to index, as well as, setting the
     * user setting to default.
     */
    public WebPageIndexer(){

        setUserAgentToRobot();
        this.useRandomUserAgent = false;
    }

    /**
     * Sets the url to connection to and retrieve information from.
     * @param url to connection to and retrieve information from
     */
    public void setUrl(String url) {

        this.url = url;
        isDeadEnd = false;
    }

    /**
     * Sets a user agent to use for page indexing.
     * @param userAgent to set for indexing
     */
    public void setCustomUserAgentForConnection(String userAgent){

        this.useRandomUserAgent = false;
        this.userAgent = userAgent;
    }

    /**
     * Sets a random user agent to use for each page indexing.
     * @param useRandomUserAgent during each search
     */
    public void useRandomUserAgent(boolean useRandomUserAgent){

        this.useRandomUserAgent = useRandomUserAgent;
        this.userAgent = "*";
    }

    /**
     * Sets the user agent to the * symbol.
     */
    public void setUserAgentToRobot(){

        this.userAgent = "*";
    }

    /**
     * Connects to the given url, retrieves the html document and returns a
     * boolean value on whether the operation was successful.
     * @return true if the connection and html document retrieval was successful
     * and false otherwise
     */
    public boolean connectAndRetrieveHtml(){

        // Set a new agent for each connection if selected.
        if(useRandomUserAgent){
            userAgent = UserAgent.getRandomUserAgent();
        }

        // Attempt to connect to the url and retrieve the html document
        try {

            Connection connection = Jsoup.connect(url).userAgent(userAgent);
            htmlDoc = connection.get();

            // Server error, send status code to stderr
            if(connection.response().statusCode() != 200){

                System.err.print("Error Status Code" + connection.response().statusCode() + " @" + url);
                return false;
            }

            // No html web page
            if(!connection.response().contentType().contains("text/html")){

                isDeadEnd = true;
                return true;
            }

            return true;

        // Unable to connect to the url
        } catch(IOException ioe){

            isDeadEnd = true;
            htmlDoc = null;
            return true;

        // Invalid url
        } catch (IllegalArgumentException e){

            isDeadEnd = true;
            htmlDoc = null;
            return false;

        } catch (NullPointerException e){

            isDeadEnd = true;
            htmlDoc = null;
            return false;
        }
    }

    /**
     * Returns the links found in the html document
     * @return web page links found at the url
     */
    public ArrayList<String> getLinks() {

        ArrayList<String> links = new ArrayList<String>();

        try {

            Elements elements = htmlDoc.select("a[href]");

            // Loop through all the links to check for duplicates and links to itself
            for(Element link: elements) {

                // Do not add any duplicate links
                if(!links.contains(link.attr("abs:href"))){

                    // Do not add a link to itself
                    if(url.toLowerCase().compareTo(link.attr("abs:href").toLowerCase()) != 0){

                        // Check if the link is blank.
                        if(!link.attr("abs:href").trim().isEmpty()){
                            links.add(link.attr("abs:href"));
                        }
                    }
                }
            }

            return links;

        } catch (NullPointerException e){

            return links;
        }
    }

    /**
     * Searches for the term provided in the html document text tags and
     * returns a boolean value if its found.
     * @param searchTerm to find in the html document
     * @return true if the term was found and false otherwise
     */
    public boolean searchTermFound(String searchTerm){

        try { // Attempt a search, catch any null strings

            String htmlText = htmlDoc.body().text().toLowerCase();

            // Split up the text to search each word
            Set<String> textSet = new HashSet<String>(Arrays.asList(htmlText.split(" ")));

            // Search the set for the word
            return textSet.contains(searchTerm.toLowerCase());

        } catch(NullPointerException e){

            return false;
        }
    }

    /**
     * Returns the web page title derived from the title tag in the html
     * document.
     * @return web page title
     */
    private String getTitle() {

        try {
            return htmlDoc.title();
        } catch (NullPointerException e) {
            return null;
        }
    }

    /**
     * Returns a list of key words derived from the keywords meta tag in the
     * html document.
     * @return list of keywords
     */
    private ArrayList<String> getKeywords() {

        try {

            Elements keywords = htmlDoc.select("meta[name=keywords]");

            // Key the key words string and split them up into separate entities
            if(keywords != null && keywords.size() > 0){

                return new ArrayList<String>(Arrays.asList(keywords.first().attr("content").split(" ")));
            }

            return null;

        } catch (NullPointerException e) {

            System.err.print("Error NullPointerException @" + url);
            return null;
        }
    }

    /**
     * Returns the word count of the web page located in the html text tags.
     * @return html document word count
     */
    private int getWordCount() {

        try {
            String text = htmlDoc.body().text();

            if(text.length() == 0){ // Check and make sure the text is not blank
                return 0;
            } else {
                return text.split(" ").length;
            }
        } catch (NullPointerException e) {
            return 0;
        }
    }

    /**
     * Returns the char count of the web page located in the html text tags.
     * @return html document char count
     */
    private int getCharCount() {

        try {
            return htmlDoc.body().text().length();
        } catch (NullPointerException e) {
            return 0;
        }
    }

    /**
     * Returns the byte count of the html document.
     * @return html document byte count
     */
    private int getByteCount() {

        try {
            return htmlDoc.toString().length();
        } catch (NullPointerException e) {
            return 0;
        }
    }

    /**
     * Returns the number of images contained in the web page.
     * @return number of images on the web page
     */
    private int getImageCount() {

        try {
            return htmlDoc.select("img[src~=(?i)\\.(png|jpe?g|gif)]").size();
        } catch (NullPointerException e) {
            return 0;
        }
    }

    /**
     * Returns the user agent used during the connection and html retrieval.
     * @return user agent used for the connection
     */
    private String getUserAgent() {

        return userAgent;
    }

    /**
     * Populate all of the indexed values for the web page.
     * @param page to populate with indexed values
     */
    public void populateWebPageWithIndexedValues(WebPage page){

        page.setUserAgent(getUserAgent());

        if(isDeadEnd){

            page.setDeadEnd(true);

        } else {

            page.setTitle(getTitle());
            page.setKeywords(getKeywords());
            page.setWordCount(getWordCount());
            page.setCharCount(getCharCount());
            page.setByteCount(getByteCount());
            page.setNumberOfImages(getImageCount());
            page.setDeadEnd(false);
        }
    }
}
