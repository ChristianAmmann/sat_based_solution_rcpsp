package scheduler.data;


/**
 * The Enum RelationType.
 */
public enum RelationType {

    /** The finish-start type. */
    FS("fs"),
    /** The start-start type. */
    SS("ss"),
    /** The start-finish type. */
    SF("sf"),
    /** The finish-finish type. */
    FF("ff");

    /** The text. */
    private String text;

    /**
     * Instantiates a new relation type.
     *
     * @param text
     *            the text
     */
    RelationType(String text) {
	this.text = text;
    }

    /**
     * Gets the text.
     *
     * @return the text
     */
    public String getText() {
	return this.text;
    }

    /**
     * From string.
     *
     * @param text
     *            the text
     * @return the relation type
     */
    public static RelationType fromString(String text) {
	if (text != null) {
	    for (RelationType rel : RelationType.values()) {
		if (text.equalsIgnoreCase(rel.text)) {
		    return rel;
		}
	    }
	}
	return null;
    }
}
