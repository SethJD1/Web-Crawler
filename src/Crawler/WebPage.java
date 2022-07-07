package Crawler;

import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.net.InetAddress;

// JSON converting imports
import com.fasterxml.jackson.annotation.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Web Page class to capture the details of a web page which includes attributes
 * of it's size, word count, char count, hostname, etc. The Web Page class is
 * specifically set for a JSON format excluding the target links, since this
 * class was built to be displayed in a backward looking manner.
 *
 * @author Seth Dovgan
 * @version 18JAN19
 */
@JsonPropertyOrder({"id", "url", "hostname", "ipAddress", "groupId", "title", "userAgent", "height",
        "searchTermFound", "keywords", "wordCount", "charCount", "byteCount",
        "numberOfImages", "isDeadEnd", "targetLinkCount", "predecessorLinks" })
@JsonIdentityInfo(generator=ObjectIdGenerators.PropertyGenerator.class, property="id")
public class WebPage {

    private static int idCounter = 0; // Used to assign unique id(s)

    // Graph tracking variables
    @JsonIgnore
    private Enum state;
    private int height;

    // Web page attributes
    private int id;
    private String url;
    private String hostname;
    private int groupId;
    private String title;
    private String userAgent;
    private Boolean searchTermFound;
    private ArrayList<String> keywords;
    private int wordCount;
    private int charCount;
    private int byteCount;
    private int numberOfImages;
    private boolean isDeadEnd;

    @JsonIgnore
    private ArrayList<WebPage> targetLinks;
    private ArrayList<WebLink> predecessorLinks;

    /**
     * Constructor - Sets the URL variable and sets all other options to default.
     * @param url of the web page
     */
    public WebPage(String url){

        setDefaultValues();

        this.url = url;
        this.id = idCounter;
        this.hostname = getHostName(url);

        idCounter++; // Increment id for next web page
    }

    /**
     * Constructor - Sets the URL and height variables to the parameters given
     * and sets all other options to default.
     * @param url of the web page
     * @param height of the page in the graph
     */
    public WebPage(String url, int height){

        setDefaultValues();

        this.url = url;
        this.id = idCounter;
        this.height = height;
        this.hostname = getHostName(url);

        idCounter++; // Increment id for next web page
    }

    /**
     * Returns the state of the web page in the discovery and indexing process.
     * @return state of the web page
     */
    public Enum getState() {

        return state;
    }

    /**
     * Sets the state of the web page in the discovery process.
     * @param state to set for the web page
     */
    public void setState(Enum state) {

        this.state = state;
    }

    /**
     * Sets the height of the web page in the graph.
     * @param height to set for the web page
     */
    public void setHeight(int height) {

        this.height = height;
    }

    /**
     * Returns the height of the web page in the graph.
     * @return web page height in the graph
     */
    public int getHeight() {

        return height;
    }

    /**
     * Returns the web page id.
     * @return web page id
     */
    public int getId() {

        return id;
    }

    /**
     * Returns the web page URL.
     * @return web page URL
     */
    public String getUrl() {

        return url;
    }

    /**
     * Returns the web page hostname.
     * @return web page hostname
     */
    public String getHostname() {

        return hostname;
    }

    /**
     * Returns the ip address for the web page host
     * @return web page host ip address
     */
    public String getIpAddress() {

        try {

            InetAddress ipAddress = InetAddress.getByName(new URL(url).getHost());
            return ipAddress.getHostAddress();

        } catch(UnknownHostException e) {

            return null;

        } catch(MalformedURLException e){

            return null;
        }
    }

    /**
     * Returns the web page domain group id derived from the graph.
     * @return domain group id
     */
    public int getGroupId() {

        return groupId;
    }

    /**
     * Sets the web page domain group id to the parameter.
     * @param groupId to set for the web page domain
     */
    public void setGroupId(int groupId) {

        this.groupId = groupId;
    }

    /**
     * Returns the web page title.
     * @return web page title
     */
    public String getTitle() {

        return title;
    }

    /**
     * Sets the web page title to the parameter.
     * @param title to set for the web page
     */
    public void setTitle(String title) {

        this.title = title;
    }

    /**
     * Returns the user agent used to retrieve the contents of the page.
     * @return user agent used to retrieve the contents of the page.
     */
    public String getUserAgent() {

        return userAgent;
    }

    /**
     * Sets the user agent used to retrieve the contents of the page.
     * @param userAgent to set for the page retrieval
     */
    public void setUserAgent(String userAgent) {

        this.userAgent = userAgent;
    }

    /**
     * Returns whether the search term was found in the web page during a
     * web crawl.
     * @return true if the search term was found or false otherwise
     */
    @JsonGetter("searchTermFound")
    public Boolean searchTermFound() {

        return searchTermFound;
    }

    /**
     * Sets whether the search term was found in the web page or not.
     * @param searchTermFound in the web page
     */
    public void setSearchTermFound(Boolean searchTermFound) {

        this.searchTermFound = searchTermFound;
    }

    /**
     * Returns the keywords found in the web page
     * @return keywords found in the web page
     */
    public ArrayList<String> getKeywords() {

        return keywords;
    }

    /**
     * Sets the web page keywords to the parameter.
     * @param keywords to set for the web page
     */
    public void setKeywords(ArrayList<String> keywords) {

        this.keywords = keywords;
    }

    /**
     * Returns the word count of the web page derived from the text on the page.
     * @return web page word count
     */
    public int getWordCount() {

        return wordCount;
    }

    /**
     * Sets the word count of the web page.
     * @param wordCount of the web page to set
     */
    public void setWordCount(int wordCount) {

        this.wordCount = wordCount;
    }

    /**
     * Returns the char count of the web page derived from the text on the page
     * @return web page char count
     */
    public int getCharCount() {

        return charCount;
    }

    /**
     * Sets the char count of the web page.
     * @param charCount of the web page to set
     */
    public void setCharCount(int charCount) {

        this.charCount = charCount;
    }

    /**
     * Returns the byte count of the web page html document.
     * @return web page byte count
     */
    public int getByteCount() {

        return byteCount;
    }

    /**
     * Sets the web page byte count.
     * @param byteCount of the web page to set
     */
    public void setByteCount(int byteCount) {

        this.byteCount = byteCount;
    }

    /**
     * Returns the number of images the web page contains.
     * @return number of web page images
     */
    public int getNumberOfImages() {

        return numberOfImages;
    }

    /**
     * Set the number of images contained in the web page.
     * @param numberOfImages of the web page to set
     */
    public void setNumberOfImages(int numberOfImages) {

        this.numberOfImages = numberOfImages;
    }

    /**
     * Returns whether the web pgae is a dead end, i.e. an image, file, password
     * protected or contains no html/text such as blogs and feeds.
     * @return true if the page is a dead end and false otherwise.
     */
    @JsonGetter("isDeadEnd")
    public boolean isDeadEnd() {
        return isDeadEnd;
    }

    /**
     * Sets whether the web pgae is a dead end, i.e. an image, file, password
     * protected or contains no html/text such as blogs and feeds.
     * @param deadEnd true if the page is a dead end and false otherwise.
     */
    public void setDeadEnd(boolean deadEnd) {
        isDeadEnd = deadEnd;
    }

    /**
     * Adds a link found in the web page.
     * @param link to add to the web page target list
     */
    public void addTargetLink(WebPage link){

        targetLinks.add(link);
    }

    /**
     * Returns the list of target links in the web page.
     * @return target links in the web page
     */
    @JsonIgnore
    public ArrayList<WebPage> getTargetLinks(){

        return targetLinks;
    }

    /**
     * Returns a target link at the specified index.
     * @param index of the target link in the list
     * @return the target link at the specified index
     */
    @JsonIgnore
    public WebPage getTargetLink(int index) {

        return targetLinks.get(index);
    }

    /**
     * Returns the number of target links in the web page.
     * @return number of target links
     */
    public int getTargetLinkCount(){

        return targetLinks.size();
    }

    /**
     * Checks if another url is a relative link / same domain as the current
     * web page.
     * @param url to check if its in the same domain
     * @return true if it's in the same domain and false otherwise.
     */
    @JsonIgnore
    public boolean hasSameDomain(String url){

        String hostname = getHostName(url);

        return hostname != null && this.hostname != null
                && hostname.toLowerCase().compareTo(this.hostname.toLowerCase()) == 0;
    }

    /**
     * Returns the hostname for a given url. In the event a URL doesn't exist
     * for the given url, null will be returned.
     * @param url to get the host name for.
     * @return the hostname for the given url or null otherwise.
     */
    @JsonIgnore
    public String getHostName(String url){

        try {

            URL temp = new URL(url);

            // Remove the www. portion of the domain name if it exists
            if(temp.getHost().length() > 4 && temp.getHost().substring(0, 4).toLowerCase().compareTo("www.") == 0){
                return temp.getHost().substring(4, temp.getHost().length());
            } else {
                return temp.getHost();
            }

        } catch(MalformedURLException e){

            return null;
        }
    }

    /**
     * Returns the list of predecessor (backwards looking) links for the web page
     * @return web page list of predecessor links
     */
    public ArrayList<WebLink> getPredecessorLinks() {

        return predecessorLinks;
    }

    /**
     * Adds a web page predecessor in the form of a link.
     * @param parentId web link
     */
    public void addPredecessorLink(WebLink parentId) {

        predecessorLinks.add(parentId);
    }

    /**
     * Sets all the class variables to default values.
     */
    private void setDefaultValues() {

        this.state = State.DISCOVERED;
        this.height = -1;
        this.predecessorLinks = new ArrayList<WebLink>();
        this.groupId = 0;
        this.title = "";
        this.searchTermFound = false;
        this.keywords = new ArrayList<String>();
        this.wordCount = 0;
        this.charCount = 0;
        this.byteCount = 0;
        this.numberOfImages = 0;
        this.targetLinks = new ArrayList<WebPage>();
        this.isDeadEnd = false;
    }

    /**
     * Returns the web page into a JSON formatted string.
     * @return web page JSON formatted string
     */
    @JsonIgnore
    public String toJson() {

        ObjectMapper mapper = new ObjectMapper();

        try {

            return mapper.writeValueAsString(this) + "#!#";

        } catch (JsonProcessingException e) {

            System.err.println("JSON processing Exception @" + url);
            return "{}#!#";
        }
    }

    /**
     * Returns an abbreviated version of the web page URL to aid in printing
     * URLs to the console.
     * @return abbreviate URL string
     */
    @JsonIgnore
    public String getAbbreviatedURL(){

        int LIMIT = 50;

        if(url.length() > LIMIT){
            return url.substring(0, LIMIT) + "... (" + (url.length()) + ")";
        }

        return url;
    }

    /**
     * String representation of the web page
     * @return string representation of the web page
     */
    @JsonIgnore
    @Override
    public String toString() {

        return id + " - " + getAbbreviatedURL();
    }
}
