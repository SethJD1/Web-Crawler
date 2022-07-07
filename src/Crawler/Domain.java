package Crawler;

/**
 * Domain class to capture the details of a domain such as the host name and
 * generated id.
 *
 * @author Seth Dovgan
 * @version 18JAN19
 */
public class Domain {

    private String domainName;
    private int domainId;

    /**
     * Default Constructor - builds a base class with default values for the
     * domain name and domainId.
     */
    public Domain(){

        this.domainName = "";
        this.domainId = -1;
    }

    /**
     * Constructor - builds the base class with the given domain name and id
     * values.
     *
     * @param domainName to set
     * @param domainId to set
     */
    public Domain(String domainName, int domainId) {

        this.domainName = domainName;
        this.domainId = domainId;
    }

    /**
     * Returns the domain name for the domain object.
     * @return domain name for the object
     */
    public String getDomainName() {

        return domainName;
    }

    /**
     * Set the domain name for the domain object.
     * @param domainName to set for the object
     */
    public void setDomainName(String domainName) {

        this.domainName = domainName;
    }

    /**
     * Returns the domain id for the domain object.
     * @return domain ig for the object
     */
    public int getDomainId() {

        return domainId;
    }

    /**
     * Set the domain id for the domain object.
     * @param domainId to set for the object
     */
    public void setDomainId(int domainId) {

        this.domainId = domainId;
    }

    /**
     * Returns a string representation of the domain.
     * @return string representation of the domain
     */
    @Override
    public String toString() {

        return "Group " + domainId + " - " + domainName;
    }
}
