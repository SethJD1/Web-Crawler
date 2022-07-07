package Crawler;
import java.util.Map;
import java.util.HashMap;

/**
 * Data Structure enum. Supporting enum used to switch data structure types
 * with concurrency.
 *
 * @author Seth Dovgan
 * @version 18JAN19
 */
public enum DataStructureType {

    STACK(1),
    QUEUE(2),
    BAG(3);

    private int id;
    private static final Map<Integer, DataStructureType> searchTypesByValue = new HashMap<Integer, DataStructureType>();

    static {
        for (DataStructureType type : DataStructureType.values()) {
            searchTypesByValue.put(type.id, type);
        }
    }

    /**
     * Base Constructor.
     * @param id of the data structure type
     */
    DataStructureType(int id){
        this.id = id;
    }

    /**
     * Returns the integer id associated with the Data Structure Type.
     * @return id for the data structure
     */
    public int getId(){
        return id;
    }

    /**
     * Returns the data structure type associated with the id.
     * @param id to get the corresponding data structure type
     * @return data structure type corresponding to the id
     */
    public static DataStructureType getType(int id){
        return searchTypesByValue.get(id);
    }
}
