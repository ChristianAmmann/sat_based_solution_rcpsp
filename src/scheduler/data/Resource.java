package scheduler.data;

/**
 * The Class ResourceInternal represent the resource in context of the encoding.
 */
public class Resource {

    /** The id of the resource. */
    private int id;

    private String name;

    /** The capacity. */
    private int capacity;

    /**
     * Instantiates a new resource internal.
     *
     * @param id
     *            the id
     * @param capacity
     *            the capacity
     */
    public Resource(int id, String name, int capacity) {
	this.id = id;
	this.name = name;
	this.capacity = capacity;
    }

    /**
     * Gets the id.
     *
     * @return the id
     */
    public int getId() {
	return this.id;
    }

    /**
     * Gets the capacity.
     *
     * @return the capacity
     */
    public int getCapacity() {
	return this.capacity;
    }

    public String getName() {
	return name;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
	return "Resource [id=" + id + ", capacity=" + capacity + "]";
    }
}
