package scheduler.data;
/**
 * The Class RelationInternal.
 */
public class Relation {

    /** The first. */
    private Activity first;

    /** The second. */
    private Activity second;

    /** The type (FF,FS,SF,SS) */
    private RelationType type;

    /**
     * Instantiates a new relation internal.
     *
     * @param first
     *            the first
     * @param second
     *            the second
     * @param type
     *            the type
     */
    public Relation(Activity first, Activity second, RelationType type) {
	this.first = first;
	this.second = second;
	this.type = type;
	first.addSuccessor(this);
	second.addPredecessor(this);
    }

    /**
     * Gets the first.
     *
     * @return the first
     */
    public Activity getFirst() {
	return first;
    }

    /**
     * Gets the second.
     *
     * @return the second
     */
    public Activity getSecond() {
	return second;
    }

    /**
     * Gets the type.
     *
     * @return the type
     */
    public RelationType getType() {
	return type;
    }

    public void embedForwards() {
	if (type.equals(RelationType.FS)) {
	    if (second.getEarlyStartDate() <= first.getEarlyEndDate()) {
		second.setEarlyStartDate(first.getEarlyEndDate());
	    }
	} else if (type.equals(RelationType.FF)) {
	    if (second.getEarlyEndDate() <= first.getEarlyEndDate()) {
		second.setEarlyEndDate(first.getEarlyEndDate());
	    }
	} else if (type.equals(RelationType.SS)) {
	    if (second.getEarlyStartDate() <= first.getEarlyStartDate()) {
		second.setEarlyStartDate(first.getEarlyStartDate());
	    }
	} else if (type.equals(RelationType.SF)) {
	    if (second.getEarlyEndDate() <= first.getEarlyStartDate()) {
		second.setEarlyEndDate(first.getEarlyStartDate());
	    }
	}
	for (Relation aob : second.getSuccessors()) {
	    aob.embedForwards();
	}
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
	return "RelationActivity [first=" + first.toString() + ", second=" + second.toString() + ", type=" + type.toString() + "]";
    }
}
