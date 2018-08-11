package scheduler.encoding;

import java.util.ArrayList;
import java.util.List;

import org.sat4j.core.VecInt;
import org.sat4j.specs.ContradictionException;
import org.sat4j.specs.ISolver;

import scheduler.data.Activity;
import scheduler.data.Project;
import scheduler.data.Relation;
import scheduler.data.Resource;
import scheduler.log.Log;

public class SATEncoder {

	private BCCEncoder counterEncoder;
	private static SATEncoder encoder;
	private VariableFactory variableFactory;

	private SATEncoder() {
		variableFactory = VariableFactory.getVariableFactory();
		counterEncoder = BCCEncoder.getBCCEncoder();
	}

	public static SATEncoder getEncoder() {
		if (encoder != null) {
			return encoder;
		}
		return new SATEncoder();
	}

	public void encode(ISolver solver, Project project, int maxTime,
			boolean bccMode) {
		try {
			encodeUniqueStartInstant(solver, maxTime, project.getActivities());
			encodeStartInTime(solver, maxTime, project.getActivities());
			encodeRuntime(solver, maxTime, project.getActivities());
			encodeWorkLoad(solver, maxTime, project.getActivities());
			encodeRelations(solver, maxTime, project.getRelations());
			/*if (checkResourceScarcity(project.getActivities(),
					project.getResources())) {*/
				Log.d("test");
				if (bccMode) {
					encodeResourcesWithCardinalities(solver, maxTime,
							project.getActivities(), project.getResources());
				} else {
					encodeResourcesWithPowerset(solver, maxTime,
							project.getActivities(), project.getResources());
				}
			//}
		} catch (ContradictionException e) {
			e.printStackTrace();
		}
	}

	private void encodeUniqueStartInstant(ISolver solver, int maxTime,
			List<Activity> activities) throws ContradictionException {
		for (Activity activity : activities) {
			encodeUniqueStartInstant(solver, maxTime, activity);
		}
	}

	private void encodeStartInTime(ISolver solver, int maxTime,
			List<Activity> activities) throws ContradictionException {
		for (Activity activity : activities) {
			encodeStartInTime(solver, maxTime, activity);
		}
	}

	private void encodeRuntime(ISolver solver, int maxTime,
			List<Activity> activities) throws ContradictionException {
		for (Activity activity : activities) {
			encodeRuntime(solver, maxTime, activity);
		}
	}

	/**
	 * Every activity a has a unique start instant.
	 * 
	 * @param activity
	 *            a
	 * @throws ContradictionException
	 */
	private void encodeUniqueStartInstant(ISolver solver, int maxTime,
			Activity activity) throws ContradictionException {
		int[] clause = new int[maxTime];
		// encode "at least one start"
		for (int time = 0; time < maxTime; time++) {
			clause[time] = variableFactory.start(activity.getId(), time);
		}
		solver.addClause(new VecInt(clause));
		// encode "at most one start"
		for (int time1 = 0; time1 < maxTime; time1++) {
			for (int time2 = time1 + 1; time2 < maxTime; time2++) {
				int[] binaryClause = new int[2];
				binaryClause[0] = neg(variableFactory.start(activity.getId(),
						time1));
				binaryClause[1] = neg(variableFactory.start(activity.getId(),
						time2));
				solver.addClause(new VecInt(binaryClause));
			}
		}
	}

	/**
	 * No activity starts to late to finish within the project timespan.
	 * 
	 * @param activity
	 *            a
	 * @throws ContradictionException
	 */
	private void encodeStartInTime(ISolver solver, int maxTime,
			Activity activity) throws ContradictionException {
		// Forbid start between (max - duration) and max
		for (int time = maxTime - (int) activity.getDuration() + 1; time < maxTime; time++) {
			int[] fact = new int[1];
			fact[0] = neg(variableFactory.start(activity.getId(), time));
			solver.addClause(new VecInt(fact));
		}
	}

	/**
	 * For every activity a, if a starts at any instant t then a runs at the
	 * instant t, t+1, ..., (t+duration-1) and does not run otherwise
	 * 
	 * @param activity
	 * @throws ContradictionException
	 */
	private void encodeRuntime(ISolver solver, int maxTime, Activity activity)
			throws ContradictionException {
		for (int time = 0; time < maxTime; time++) {
			Integer literal = variableFactory.start(activity.getId(), time);
			for (int j = 0; j < time; j++) {
				int[] binaryClause = new int[2];
				binaryClause[0] = neg(literal);
				binaryClause[1] = neg(variableFactory.run(activity.getId(), j));
				solver.addClause(new VecInt(binaryClause));
			}
			for (int j = time; j < time + activity.getDuration(); j++) {
				int[] binaryClause = new int[2];
				binaryClause[0] = neg(literal);
				binaryClause[1] = variableFactory.run(activity.getId(), j);
				solver.addClause(new VecInt(binaryClause));
			}
			for (int j = (int) (time + activity.getDuration()); j < maxTime; j++) {
				int[] binaryClause = new int[2];
				binaryClause[0] = neg(literal);
				binaryClause[1] = neg(variableFactory.run(activity.getId(), j));
				solver.addClause(new VecInt(binaryClause));
			}
		}
	}

	/**
	 * There are no time gaps
	 * 
	 * @throws ContradictionException
	 */
	private void encodeWorkLoad(ISolver solver, int maxTime,
			List<Activity> activities) throws ContradictionException {
		// introduce new variables for every time t that encode if any activity
		// runs at t
		int[] assumptions = new int[maxTime];
		for (int time = 0; time < maxTime; time++) {
			// encVar(t) is true iff an activity runs at time t
			int encVar = variableFactory.aux(time);
			assumptions[time] = encVar;
			int[] implicationOne = new int[activities.size() + 1];
			implicationOne[0] = neg(encVar);
			int i = 1;
			for (Activity activity : activities) {
				implicationOne[i] = variableFactory.run(activity.getId(), time);
				i++;
				int[] implicationTwo = new int[2];
				implicationTwo[0] = encVar;
				implicationTwo[1] = neg(variableFactory.run(activity.getId(),
						time));
				solver.addClause(new VecInt(implicationTwo));
			}
			solver.addClause(new VecInt(implicationOne));
		}

		// encode implications: -e(t) -> -e(t+1) == e(t) or -e(t+1)
		for (int i = 0; i < assumptions.length - 1; i++) {
			int[] clause = new int[2];
			clause[0] = assumptions[i];
			clause[1] = neg(assumptions[i + 1]);

			solver.addClause(new VecInt(clause));
		}
	}

	/**
	 * encode all relation constraints
	 * 
	 * @param relation
	 * @throws ContradictionException
	 */
	private void encodeRelations(ISolver solver, int maxTime,
			List<Relation> relations) throws ContradictionException {
		for (Relation relation : relations) {

			Activity first = relation.getFirst();
			Activity second = relation.getSecond();
			switch (relation.getType()) {
			case FS:
				encodeRelationTypeFS(solver, maxTime, first, second);
				break;
			case SS:
				encodeRelationTypeSS(solver, maxTime, first, second);
				break;
			case FF:
				encodeRelationTypeFF(solver, maxTime, first, second);
				break;
			case SF:
				encodeRelationTypeSF(solver, maxTime, first, second);
				break;
			default:
				new IllegalArgumentException("Relationtype is unknown");
			}
		}
	}

	/**
	 * Given two activities A and B, then the activity B does not start before
	 * the activity A has finished
	 * 
	 * s(a) < e(b) => s(a) < s(b) + d(a)
	 * 
	 * @param first
	 *            - Activity
	 * @param second
	 *            - Activity
	 * @throws ContradictionException
	 */
	private void encodeRelationTypeFS(ISolver solver, int maxTime,
			Activity first, Activity second) throws ContradictionException {
		// for each time slot
		for (int time = 0; time < maxTime; time++) {
			Integer literal = variableFactory.start(first.getId(), time);
			for (int k = 0; k <= time + (int) (first.getDuration() - 1); k++) {
				int[] binaryClause = new int[2];
				binaryClause[0] = neg(literal);
				binaryClause[1] = neg(variableFactory.start(second.getId(), k));
				solver.addClause(new VecInt(binaryClause));
			}
		}
	}

	/**
	 * Given two activities A and B, then the activity B does not start before
	 * the activity A has started s(a) <= s(b)
	 * 
	 * @param first
	 *            - Activity
	 * @param second
	 *            - Activity
	 * @throws ContradictionException
	 */
	private void encodeRelationTypeSS(ISolver solver, int maxTime,
			Activity first, Activity second) throws ContradictionException {
		// for each time slot
		for (int time = 0; time < maxTime; time++) {
			Integer literal = variableFactory.start(first.getId(), time);
			for (int k = 0; k < time; k++) {
				int[] binaryClause = new int[2];
				binaryClause[0] = neg(literal);
				binaryClause[1] = neg(variableFactory.start(second.getId(), k));
				solver.addClause(new VecInt(binaryClause));
			}
		}
	}

	/**
	 * Given two activities A and B, then the activity B does not finish before
	 * the activity A has finished.
	 * 
	 * @param first
	 * @param second
	 * @throws ContradictionException
	 */
	private void encodeRelationTypeFF(ISolver solver, int maxTime,
			Activity first, Activity second) throws ContradictionException {
		// for each time slot
		for (int time = 0; time < maxTime
				&& (time + (first.getDuration() - second.getDuration()) > 0); time++) {
			Integer literal = variableFactory.start(first.getId(), time);
			for (int k = 0; k <= time
					+ (int) (first.getDuration() - second.getDuration()) - 1; k++) {
				int[] binaryClause = new int[2];
				binaryClause[0] = neg(literal);
				binaryClause[1] = neg(variableFactory.start(second.getId(), k));
				solver.addClause(new VecInt(binaryClause));
			}
		}
	}

	/**
	 * Given two activities A and B, then the activity B does not finished
	 * before the activity A has finished.
	 * 
	 * 
	 * @param first
	 * @param second
	 * @throws ContradictionException
	 */
	private void encodeRelationTypeSF(ISolver solver, int maxTime,
			Activity first, Activity second) throws ContradictionException {
		// for each time slot
		for (int time = 0; time < maxTime && time - second.getDuration() > 0; time++) {
			Integer literal = variableFactory.start(first.getId(), time);
			for (int k = 0; k <= time - (int) (second.getDuration()) + 2; k++) {
				int[] binaryClause = new int[2];
				binaryClause[0] = neg(literal);
				binaryClause[1] = neg(variableFactory.start(second.getId(), k));
				solver.addClause(new VecInt(binaryClause));
			}
		}
	}

	private boolean checkResourceScarcity(List<Activity> activities,
			List<Resource> resources) {
		for (Resource resource : resources) {
			int sumConsumption = 0;
			for (Activity activity : activities) {
				Integer consumption = activity.getConsumption(resource);
				if (consumption != null) {
					sumConsumption += consumption;
				}
			}
			if (resource.getCapacity() - sumConsumption < 0) {
				return true;
			}
		}
		return false;
	}

	/**
	 * solve subset-sum problem and encode the solution to sat
	 * 
	 * @throws ContradictionException
	 */
	private void encodeResourcesWithPowerset(ISolver solver, int maxTime,
			List<Activity> activities, List<Resource> resources)
			throws ContradictionException {
		List<List<Activity>> powerset = new ArrayList<List<Activity>>();
		powerset.add(new ArrayList<Activity>());
		boolean resourceConflict;
		List<List<Activity>> resourceConflicts = new ArrayList<List<Activity>>();
		for (Activity activity : activities) {
			List<List<Activity>> newPs = new ArrayList<List<Activity>>();
			for (List<Activity> subset : powerset) {
				newPs.add(subset);
				List<Activity> newSubset = new ArrayList<Activity>(subset);
				newSubset.add(activity);
				if (!resourceConflicts.containsAll(newSubset)) {
					resourceConflict = encodeConsumption(solver, maxTime,
							newSubset, resources);
				} else {
					resourceConflict = false;
				}
				if (!resourceConflict) {
					newPs.add(newSubset);
				} else {
					resourceConflicts.add(newSubset);
				}
			}
			powerset = newPs;
		}
	}

	/**
	 * Given a combination of parallel running activities. Runs over all
	 * consumptions of activities to find resource conflicts
	 * 
	 * @param combination
	 * @return if there is a resource conflict
	 * @throws ContradictionException
	 */
	private boolean encodeConsumption(ISolver solver, int maxTime,
			List<Activity> combination, List<Resource> resources)
			throws ContradictionException {
		if (combination.isEmpty()) {
			return false;
		}

		for (Resource resource : resources) {
			int consumptionSum = 0;
			for (Activity activity : combination) {
				Integer consumption = activity.getConsumption(resource);
				if (consumption != null && consumption < 0) {
					consumptionSum += activity.getConsumption(resource);
				}
			}
			// It is assumed consumptionSum is always negative
			if (resource.getCapacity() + consumptionSum < 0) {
				encodeResourceConflict(solver, maxTime, combination);
				return true;
			}
		}
		return false;
	}

	/**
	 * Encode the given combination of parallel running activities that have a
	 * resource conflict.
	 * 
	 * @param combination
	 * @throws ContradictionException
	 */
	private void encodeResourceConflict(ISolver solver, int maxTime,
			List<Activity> combination) throws ContradictionException {
		for (int time = 0; time < maxTime; time++) {
			int[] clause = new int[combination.size()];
			int i = 0;
			for (Activity activity : combination) {
				clause[i] = neg(variableFactory.run(activity.getId(), time));
				i++;
			}
			solver.addClause(new VecInt(clause));
		}
	}

	private void encodeResourcesWithCardinalities(ISolver solver, int maxTime,
			List<Activity> activities, List<Resource> resources)
			throws ContradictionException {
		for (int time = 0; time < maxTime; time++) {
			// Wenn die Aktivität zum Zeitpunkt t läuft dann konsumiert sie ihre
			// Resourcen
			for (Activity activity : activities) {
				List<Integer> consumVariablesForActivityAtInstant = getConsumeVariablesForActivityAtInstant(
						activity, time);
				/*consumToString(neg(variableFactory.run(activity.getId(),
						time)), consumVariablesForActivityAtInstant);*/
				for (Integer consumVariable : consumVariablesForActivityAtInstant) {
					int[] binaryClause = new int[2];
					binaryClause[0] = neg(variableFactory.run(activity.getId(),
							time));
					binaryClause[1] = consumVariable;
					solver.addClause(new VecInt(binaryClause));
				}
			}

			for (Resource resource : resources) {
				List<Integer> consumVariablesForResourceAtInstant = getConsumeVariablesForResourceAtInstant(
						resource, activities, time);
				consumToString(consumVariablesForResourceAtInstant);
				if (!consumVariablesForResourceAtInstant.isEmpty()) {
					int bound = resource.getCapacity();
					counterEncoder.genLessThenConstrait(solver, bound,
							consumVariablesForResourceAtInstant,
							resource.getId(), time);
				}
			}
		}
	}

	public static List<Integer> getConsumeVariablesForActivityAtInstant(
			Activity activity, int instant) {
		List<Integer> consumVariables = new ArrayList<Integer>();
		for (Resource resource : activity.getConsumption().keySet()) {
			// Consumptions are always negativ
			for (int i = 0; i < -1 * activity.getConsumption(resource); i++) {
				consumVariables.add(VariableFactory.getVariableFactory().consume(activity.getId(),
						resource.getId(), instant, i));
			}
		}
		return consumVariables;
	}

	public static List<Integer> getConsumeVariablesForResourceAtInstant(
			Resource resource, List<Activity> activities, int instant) {
		List<Integer> consumVariables = new ArrayList<Integer>();
		for (Activity activity : activities) {
			// Consumptions are always negativ
			if (activity.getConsumption(resource) != null) {
				for (int i = 0; i < -1 * activity.getConsumption(resource); i++) {
					consumVariables.add(VariableFactory.getVariableFactory().consume(
							activity.getId(), resource.getId(), instant, i));
				}
			}
		}
		return consumVariables;
	}

	public int[] getAssumptions(int current, int maxTime) {
		int[] assumptions = new int[maxTime - current];
		int j = 0;
		for (int i = current; i < maxTime; i++) {
			assumptions[j] = neg(variableFactory.aux(i));
			j++;
		}
		return assumptions;
	}
	
	public void reset() {
		variableFactory.clearVariables();
	}

	/**
	 * NOT
	 * 
	 * @param var
	 * @return
	 */
	private Integer neg(Integer var) {
		return -1 * var;
	}
	
	private void consumToString(List<Integer> consums) {
		for(Integer consum : consums) {
			Log.d(variableFactory.getStringFromId(consum));
		}
	}
	
	private void consumToString(Integer x, List<Integer> consums) {
		String result = "\n" + variableFactory.getStringFromId(x);
		for(Integer consum : consums) {
			result += variableFactory.getStringFromId(consum) + ",";
		}
		Log.d(result);
	}
}
