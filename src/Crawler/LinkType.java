package Crawler;

import java.util.HashMap;
import java.util.Map;

/**
 * Link Type enum. Supporting enum used to switch abs, rel and all link types
 * with concurrency.
 *
 * @author Seth Dovgan
 * @version 18JAN19
 */
public enum LinkType {

    ALL(1),
    ABS(2),
    REL(3);

    private int id;
    private static final Map<Integer, LinkType> searchTypesByValue = new HashMap<Integer, LinkType>();

    static {
        for (LinkType type : LinkType.values()) {
            searchTypesByValue.put(type.id, type);
        }
    }

    /**
     * Base Constructor.
     * @param id of the link type
     */
    LinkType(int id){
        this.id = id;
    }

    /**
     * Returns the integer id associated with the link type.
     * @return id for the data structure
     */
    public int getId(){
        return id;
    }

    /**
     * Returns the link type associated with the id.
     * @param id to get the corresponding link type
     * @return link type corresponding to the id
     */
    public static LinkType getType(int id){
        return searchTypesByValue.get(id);
    }
}
