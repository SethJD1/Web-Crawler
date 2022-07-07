package Crawler;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * Web Link class to capture the details of a link between web pages which
 * includes both the source and target web page, and is the link is cyclic
 * or bi-directional.
 *
 * @author Seth Dovgan
 * @version 18JAN19
 */
public class WebLink {

    @JsonIgnore
    private WebPage source;
    @JsonIgnore
    private WebPage target;
    private boolean isCyclic;
    private boolean isBidirectional;

    /**
     * Constructor - build the object with the given parameters and initializes
     * the bi-directional value to it's default of false.
     * @param source web page to add to the link
     * @param target web page to add to the link
     * @param isCyclic link
     */
    public WebLink(WebPage source, WebPage target, boolean isCyclic) {

        this.source = source;
        this.target = target;
        this.isCyclic = isCyclic;
        this.isBidirectional = false;
    }

    /**
     * Returns the source id of the link.
     * @return link's source id
     */
    public int getSourceId() {

        return this.source.getId();
    }

    /**
     * Returns the source web page for the link.
     * @return source web page
     */
    @JsonIgnore
    public WebPage getSource() {

        return source;
    }

    /**
     * Returns the target id of the link.
     * @return link's target id
     */
    public int getTargetId() {

        return this.target.getId();
    }

    /**
     * Returns the target web page for the link.
     * @return target web page
     */
    @JsonIgnore
    public WebPage getTarget() {

        return source;
    }

    /**
     * Returns whether the link is cyclic or not.
     * @return true if the link is cyclic and false otherwise
     */
    public boolean isCyclic(){

        return isCyclic;
    }

    /**
     * Sets whether the link is cyclic or not.
     * @param isCyclic value to set for the link
     */
    public void setCyclic(boolean isCyclic) {

        this.isCyclic = isCyclic;
    }

    /**
     * Returns whether the link is bidirectional or not.
     * @return true if the link is bidirectional and false otherwise
     */
    public boolean isBidirectional() {

        return isBidirectional;
    }

    /**
     * Sets whether the link is bidirectional or not.
     * @param isBidirectional value to set for the link
     */
    public void setBidirectional(boolean isBidirectional) {

        this.isBidirectional = isBidirectional;
    }

    /**
     * Compares a given link and two web pages for a bidirectional link, i.e.
     * the link and the two web pages point to each other.
     * @param link that already exists to compare against
     * @param source page to check against the current link
     * @param target page to check against the current link
     * @return true of the link is bidirectional with the source and target or
     * false otherwise
     */
    public static boolean isBidirectional(WebLink link, WebPage source, WebPage target){

        return link.getSourceId() == target.getId() && link.getTargetId() == source.getId();
    }

    /**
     * Returns a string representation of a link; cyclic links include a hash
     * tag, and bidirectional links include an asterisk.
     * @return a string representation of a link
     */
    @Override
    public String toString(){

        String link = "";

        if(isCyclic){
            link += "\t#";
        } else if (isBidirectional) {
            link += "\t*";
        }

        link += "[" + getSourceId() + " -> "
                + getTargetId() + "]["
                + this.source.getAbbreviatedURL() + " -> "
                + this.target.getAbbreviatedURL() + "]";

        return link;
    }
}
