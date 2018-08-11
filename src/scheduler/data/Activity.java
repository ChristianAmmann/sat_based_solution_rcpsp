package scheduler.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * The Class Activity represent a task/activity in a project. Activities with 0
 * duration are although called milestones.
 */
public class Activity {

	/** The id of activity */
	private int id;

	/** The duration of activity (>0). */
	private int duration;

	private String name;

	private long earlyStartDate;
	private long earlyEndDate;

	/** The consumption of resources . */
	private Map<Resource, Integer> resourceConsumption;

	private List<Relation> successor;
	private List<Relation> predecessor;

	/**
	 * Instantiates a new activity.
	 *
	 * @param id
	 *            the id
	 * @param duration
	 *            the duration
	 */
	public Activity(int id, String name, int duration) {
		this.duration = duration;
		this.id = id;
		this.name = name;
		this.successor = new ArrayList<Relation>();
		this.predecessor = new ArrayList<Relation>();
		this.resourceConsumption = new HashMap<Resource, Integer>();
	}

	/**
	 * Instantiates a new activity.
	 *
	 * @param id
	 *            the id
	 * @param duration
	 *            the duration
	 * @param resourceConsumption
	 *            the resource consumption
	 */
	public Activity(int id, int duration,
			HashMap<Resource, Integer> resourceConsumption) {
		this.duration = duration;
		this.id = id;
		this.successor = new ArrayList<Relation>();
		this.predecessor = new ArrayList<Relation>();
		this.resourceConsumption = resourceConsumption;
	}

	/**
	 * Gets the duration.
	 *
	 * @return the duration
	 */
	public long getDuration() {
		return duration;
	}

	/**
	 * Gets the id.
	 *
	 * @return the id
	 */
	public int getId() {
		return id;
	}

	/**
	 * Gets the consumption.
	 *
	 * @param resource
	 *            the resource
	 * @return the consumption
	 */
	public Integer getConsumption(Resource resource) {
		return resourceConsumption.get(resource);
	}

	/**
	 * Gets the consumption.
	 *
	 * @return the consumption
	 */
	public Map<Resource, Integer> getConsumption() {
		return resourceConsumption;
	}

	/**
	 * Adds the consumption.
	 *
	 * @param resource
	 *            the resource
	 * @param consumption
	 *            the consumption
	 */
	public void addConsumption(Resource resource, Integer consumption) {
		resourceConsumption.put(resource, consumption);
	}

	public String getName() {
		return name;
	}

	@Override
	public String toString() {
		return "Activity [id=" + id + ", duration=" + duration + ", name="
				+ name + "]";
	}

	public void addSuccessor(Relation aob) {
		this.successor.add(aob);
	}

	public void addPredecessor(Relation aob) {
		this.predecessor.add(aob);
	}

	public List<Relation> getSuccessors() {
		return this.successor;
	}

	public List<Relation> getPredecessors() {
		return this.predecessor;
	}

	public void setEarlyStartDate(long time) {
		earlyStartDate = time;
		earlyEndDate = time + getDuration();
	}

	public void setEarlyEndDate(long time) {
		earlyEndDate = time;
		earlyStartDate = time - getDuration();
	}

	public long getEarlyStartDate() {
		return earlyStartDate;
	}

	public long getEarlyEndDate() {
		return earlyEndDate;
	}
}
