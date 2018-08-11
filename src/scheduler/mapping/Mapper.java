package scheduler.mapping;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import scheduler.data.Activity;
import scheduler.data.Project;
import scheduler.data.Relation;
import scheduler.data.RelationType;
import scheduler.data.Resource;

//Deadline written code; so quick and dirty
public class Mapper {

	private Project project;
	private static Mapper mapper;

	private Mapper() {
	}

	public static Mapper getMapper() {
		if (mapper != null) {
			return mapper;
		}
		mapper = new Mapper();
		return mapper;
	}

	public Project readProject(String projectPath) {
		BufferedReader in = null;
		try {
			in = new BufferedReader(new FileReader(new File(projectPath)));
			project = mapProject(in);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return project;
	}

	private Project mapProject(BufferedReader in) throws IOException {
		List<Activity> activities = new ArrayList<Activity>();
		List<Resource> resources = new ArrayList<Resource>();
		List<ConsumptionTriplet> consumptions = new ArrayList<ConsumptionTriplet>();
		List<RelationTriplet> relations = new ArrayList<RelationTriplet>();
		String line = "";
		while ((line = in.readLine()) != null) {
			mapLine(line, activities, resources, relations, consumptions);
		}
		if (project != null) {
			for (Activity activity : activities) {
				project.addActivity(activity);
			}
			for (Resource resource : resources) {
				project.addResource(resource);
			}
			for (RelationTriplet triplet : relations) {
				Activity first = project.getActivityById(triplet.getFirstId());
				Activity second = project
						.getActivityById(triplet.getSecondId());
				RelationType relationType = triplet.getType();
				Relation relation = new Relation(first, second, relationType);
				project.addRelation(relation);
			}
			for (ConsumptionTriplet triplet : consumptions) {
				Activity activity = project.getActivityById(triplet
						.getFirstId());
				Resource resource = project.getResourceById(triplet
						.getSecondId());
				int consumption = triplet.getConsumption();
				activity.addConsumption(resource, consumption);
			}
		}
		return project;
	}

	private void mapLine(String line, List<Activity> activities,
			List<Resource> resources, List<RelationTriplet> relations,
			List<ConsumptionTriplet> consumptions) {
		String[] parts = line.split(";");
		String type = parts[0];
		if (type.equals("project")) {
			project = new Project(parts[3], 0, Integer.MAX_VALUE);
		} else if (type.equals("task")) {
			int id = Integer.parseInt(parts[1]);
			int duration = Integer.parseInt(parts[2]);
			Activity activity = new Activity(id, parts[3], duration);
			activities.add(activity);
		} else if (type.equals("resource")) {
			int id = Integer.parseInt(parts[1]);
			int capacity = Integer.parseInt(parts[2]);
			Resource resource = new Resource(id, parts[3], capacity);
			resources.add(resource);
		} else if (type.equals("consumption")) {
			int activityId = Integer.parseInt(parts[1]);
			int resourceId = Integer.parseInt(parts[2]);
			int consumption = Integer.parseInt(parts[3]);
			ConsumptionTriplet triplet = new ConsumptionTriplet(activityId,
					resourceId, consumption);
			consumptions.add(triplet);
		} else if (type.equals("aob")) {
			int firstId = Integer.parseInt(parts[1]);
			int secondId = Integer.parseInt(parts[2]);
			String rType = parts[3];
			RelationType relationType = null;
			if (rType.equals("aa")) {
				relationType = RelationType.SS;
			} else if (rType.equals("ae")) {
				relationType = RelationType.SF;
			} else if (rType.equals("ee")) {
				relationType = RelationType.FF;
			} else if (rType.equals("ea")) {
				relationType = RelationType.FS;
			}
			RelationTriplet triplet = new RelationTriplet(firstId, secondId,
					relationType);
			relations.add(triplet);
		} else {
			throw new IllegalArgumentException();
		}
	}

	private class RelationTriplet {
		int firstId;
		int secondId;
		RelationType type;

		public RelationTriplet(int firstId, int secondId, RelationType type) {
			this.firstId = firstId;
			this.secondId = secondId;
			this.type = type;
		}

		public int getFirstId() {
			return firstId;
		}

		public int getSecondId() {
			return secondId;
		}

		public RelationType getType() {
			return type;
		}
	}

	private class ConsumptionTriplet {
		int firstId;
		int secondId;
		int consumption;

		public ConsumptionTriplet(int firstId, int secondId, int consumption) {
			this.firstId = firstId;
			this.secondId = secondId;
			this.consumption = consumption;
		}

		public int getFirstId() {
			return firstId;
		}

		public int getSecondId() {
			return secondId;
		}

		public int getConsumption() {
			return consumption;
		}
	}
}
