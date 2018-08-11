package scheduler.encoding;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Objects;

/**
 * A factory for creating Variable objects.
 */
public class VariableFactory {

	/** The Constant VARIABLE_START. */
	public static final int VARIABLE_START = 1;

	/** The Constant VARIABLE_RUN. */
	private final int VARIABLE_RUN = 2;

	/** The Constant VARIABLE_AUX. */
	private final int VARIABLE_AUX = 3;

	/** The Constant VARIABLE_SUM. */
	private final int VARIABLE_SUM = 4;

	/** The Constant VARIABLE_CARRY. */
	private final int VARIABLE_CARRY = 5;

	/** The Constant VARIABLE_CONSUMPTION. */
	private final int VARIABLE_CONSUMPTION = 6;

	/** The variables. */
	private static HashMap<ArrayList<Integer>, Integer> variables = new HashMap<ArrayList<Integer>, Integer>();

	/** The count. */
	private static Integer count = 2;

	private static VariableFactory factory;

	/**
	 * Instantiates a new variable factory.
	 */
	private VariableFactory() {
	}

	public static VariableFactory getVariableFactory() {
		if (factory != null) {
			return factory;
		}
		factory = new VariableFactory();
		return factory;
	}

	/**
	 * Gets the variable.
	 *
	 * @param args
	 *            the args
	 * @return the variable
	 */
	private Integer getVariable(Integer... args) {
		ArrayList<Integer> id = new ArrayList<Integer>();
		for (int i = 0; i < args.length; i++) {
			id.add(args[i]);
		}
		Integer variable = variables.get(id);
		if (variable == null) {
			variable = count;
			count++;
			variables.put(id, variable);
		}
		return variable;
	}

	/**
	 * Start.
	 *
	 * @param activityId
	 *            the activity id
	 * @param time
	 *            the time
	 * @return the integer
	 */
	public Integer start(int activityId, int time) {
		return getVariable(VARIABLE_START, activityId, time);
	}

	/**
	 * Run.
	 *
	 * @param activityId
	 *            the activity id
	 * @param time
	 *            the time
	 * @return the integer
	 */
	public Integer run(int activityId, int time) {
		return getVariable(VARIABLE_RUN, activityId, time);
	}

	/**
	 * Aux.
	 *
	 * @param id
	 *            the id
	 * @return the integer
	 */
	public Integer aux(int id) {
		return getVariable(VARIABLE_AUX, id, id);
	}

	/**
	 * Sum.
	 *
	 * @param resourceId
	 *            the resource id
	 * @param time
	 *            the time
	 * @param id
	 *            the id
	 * @return the integer
	 */
	public Integer sum(int resourceId, int time, int id) {
		return getVariable(VARIABLE_SUM, resourceId, time, id);
	}

	/**
	 * Carry.
	 *
	 * @param resourceId
	 *            the resource id
	 * @param time
	 *            the time
	 * @param id
	 *            the id
	 * @return the integer
	 */
	public Integer carry(int resourceId, int time, int id) {
		return getVariable(VARIABLE_CARRY, resourceId, time, id);
	}

	/**
	 * Consume.
	 *
	 * @param activityId
	 *            the activity id
	 * @param resourceId
	 *            the resource id
	 * @param time
	 *            the time
	 * @param consumeId
	 *            the consume id
	 * @return the integer
	 */
	public Integer consume(int activityId, int resourceId, int time,
			int consumeId) {
		return getVariable(VARIABLE_CONSUMPTION, activityId, resourceId, time,
				consumeId);
	}

	/**
	 * Clear variables.
	 */
	public void clearVariables() {
		variables = new HashMap<ArrayList<Integer>, Integer>();
		count = 2;
	}

	/**
	 * Gets the key by value.
	 *
	 * @param value
	 *            the value
	 * @return the key by value
	 */
	public ArrayList<Integer> getKeyByValue(Integer value) {
		for (Entry<ArrayList<Integer>, Integer> entry : variables.entrySet()) {
			if (Objects.equals(value, entry.getValue())) {
				return entry.getKey();
			}
		}
		return null;
	}

	/**
	 * Gets the string from id.
	 *
	 * @param id
	 *            the id
	 * @return the string from id
	 */
	public String getStringFromId(int id) {
		String result = "";
		if (id < 0) {
			id *= -1;
			result += "!";
		}
		ArrayList<Integer> entry = factory.getKeyByValue(id);
		if (entry == null) {
			return "keine Variable gefunden";
		}

		switch (entry.get(0)) {
		case VARIABLE_START:
			result += "s" + "(" + entry.get(1) + ", " + entry.get(2) + ")";
			break;
		case VARIABLE_RUN:
			result += "x" + "(" + entry.get(1) + ", " + entry.get(2) + ")";
			break;
		case VARIABLE_AUX:
			result += "e" + "(" + entry.get(1) + ")";
			break;
		case VARIABLE_CARRY:
			result += "carry" + "(" + entry.get(1) + ", " + entry.get(2) + ", "
					+ entry.get(3) + ")";
			break;
		case VARIABLE_SUM:
			result += "sum" + "(" + entry.get(1) + ", " + entry.get(2) + ", "
					+ entry.get(3) + ")";
			break;
		case VARIABLE_CONSUMPTION:
			result += "consume" + "(" + entry.get(1) + ", " + entry.get(2)
					+ ", " + entry.get(3) + ", v(" + entry.get(1) + ", "
					+ entry.get(2) + ") = " + entry.get(4) + ")";
			break;
		default:
			result += "0";
			break;
		}
		return result;
	}

	/**
	 * Amount of variables in the factory.
	 *
	 * @return the int
	 */
	public int size() {
		return variables.size();
	}
}
