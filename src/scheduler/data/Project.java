package scheduler.data;

import java.util.ArrayList;

public class Project {

	private String name;
	private int wat;
	private int wet;
	private ArrayList<Activity> activities;
	private ArrayList<Relation> relations;
	private ArrayList<Resource> resources;

	public Project(String name, int wat, int wet) {
		this.name = name;
		this.wat = wat;
		this.wet = wet;
		activities = new ArrayList<Activity>();
		relations = new ArrayList<Relation>();
		resources = new ArrayList<Resource>();
	}

	public ArrayList<Activity> getActivities() {
		return activities;
	}

	public ArrayList<Relation> getRelations() {
		return relations;
	}

	public ArrayList<Resource> getResources() {
		return resources;
	}

	public Activity getActivityById(int id) {
		for (Activity activity : activities) {
			if (activity.getId() == id) {
				return activity;
			}
		}
		return null;
	}

	public Resource getResourceById(int id) {
		for (Resource resource : resources) {
			if (resource.getId() == id) {
				return resource;
			}
		}
		return null;
	}

	public String getName() {
		return name;
	}

	public void addActivity(Activity activity) {
		this.activities.add(activity);
	}

	public void addResource(Resource resource) {
		this.resources.add(resource);
	}

	public void addRelation(Relation relation) {
		this.relations.add(relation);
	}

	public int getWat() {
		return wat;
	}

	public int getWet() {
		return wet;
	}

	public void embedForwards(Activity first) {
		for (Relation aob : first.getSuccessors()) {
			aob.embedForwards();
		}
	}

	@Override
	public String toString() {
		return "RCPSPProject [activities=" + activities + ", relations="
				+ relations + ", resources=" + resources + "]";
	}

}
