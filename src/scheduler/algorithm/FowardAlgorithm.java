package scheduler.algorithm;

import java.util.ArrayList;
import java.util.List;

import scheduler.data.Activity;
import scheduler.data.Project;
import scheduler.data.Relation;
import scheduler.data.RelationType;

/**
 *
 */
public class FowardAlgorithm extends Algorithm {

	private static final String VIRTUAL_MILESTONE_START = "project_start_virtual";
	private static final String VIRTUAL_MILESTONE_END = "project_end_virtual";
	private int virtualStartId = -1;
	private int virtualEndId = -1;
	private Activity first;
	private Activity last;

	
	public FowardAlgorithm(Project project) {
		super(project);
	}
	
	@Override
	public void calculate() {
		addVirtualMilestones();
		first.setEarlyStartDate(project.getWat());
		project.embedForwards(first);
		removeVirtualMilestones(first, last);
	}

	/**
	 * Add virtual begin- and end-milestones, to have them unique for the
	 * algorithm.
	 */
	private void addVirtualMilestones() {
		List<Activity> activities = project.getActivities();
		List<Activity> starts = new ArrayList<Activity>();
		List<Activity> ends = new ArrayList<Activity>();
		int maxId = 0;

		for (Activity activity : activities) {
			if (activity.getPredecessors().size() == 0) {
				starts.add(activity);
			}
			if (activity.getSuccessors().size() == 0) {
				ends.add(activity);
			}
			if (activity.getId() > maxId)
				maxId = activity.getId();
		}

		this.virtualStartId = maxId + 1;
		this.virtualEndId = maxId + 2;
		first = new Activity(virtualStartId, VIRTUAL_MILESTONE_START, 0);
		project.addActivity(first);
		last = new Activity(virtualEndId, VIRTUAL_MILESTONE_END, 0);
		project.addActivity(last);

		for (Activity activity : starts) {
			new Relation(first, activity, RelationType.FS);
		}

		for (Activity activity : ends) {
			new Relation(first, activity, RelationType.FS);
		}
	}

	private void removeVirtualMilestones(Activity first, Activity last) {
		project.getActivities().remove(first);
		project.getActivities().remove(last);
	}
}
