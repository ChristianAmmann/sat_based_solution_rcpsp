package scheduler.algorithm;

import java.util.List;

import org.sat4j.core.VecInt;
import org.sat4j.minisat.SolverFactory;
import org.sat4j.specs.IProblem;
import org.sat4j.specs.ISolver;
import org.sat4j.specs.IVecInt;
import org.sat4j.specs.TimeoutException;

import scheduler.data.Activity;
import scheduler.data.Project;
import scheduler.encoding.SATDecoder;
import scheduler.encoding.SATEncoder;
import scheduler.log.Log;

public class RCPSPAlgorithm extends Algorithm {

	private SATEncoder encoder;
	private SATDecoder decoder;
	private ISolver solver;
	private boolean bccMode;
	private long encodeTimeStart;
	private long encodeTimeEnd;

	public RCPSPAlgorithm(Project project, boolean bccMode) {
		super(project);
		this.bccMode = bccMode;
	}
	
	@Override
	public void calculate() {
		solver = initSolver();
		int minTime = getMinTime(project.getActivities()) - 1;
		int maxTime = getMaxTime(project.getActivities());
		if (maxTime > 0) {
			encoder = SATEncoder.getEncoder();
			Log.d("Kodierung startet ...");
			encodeTimeStart = System.currentTimeMillis();
			encoder.encode(solver, project, maxTime, bccMode);
			encodeTimeEnd = System.currentTimeMillis();
			decoder = SATDecoder.getDecoder();
			try {
				solveProblem(minTime, maxTime);
			} catch (TimeoutException e) {
				resetAlgorithm("TimeoutException");
			} catch (OutOfMemoryError e) {
				resetAlgorithm("OutOfMemoryError");
			}
		} else {
			Log.i("Keine Kodierung notwendig! Projektdauer: " + maxTime);
			Log.writeLog(project.getName()
					+ " - Keine Kodierung notwendig. Minimale Projektdauer: 0");
		}
		resetAlgorithm();
	}
	
	public void setMode(boolean bccMode) {
		this.bccMode = bccMode;
	}

	/**
	 * The bisection method begins by setting an initial range, like sequential
	 * numbers, where the optimal time span is guaranteed to exist
	 * 
	 * @param cnf
	 * @param maxTime
	 */
	private void solveProblem(int minTime, int maxTime) throws TimeoutException {
		Log.d("Suchalgorihtmus startet ...");
		int initialMaxTime = maxTime;
		IProblem problem = solver;
		boolean sat = false;
		int midTime;
		long satTimeStart = System.currentTimeMillis();
		while (maxTime - minTime > 1) {
			midTime = getMidTime(minTime, maxTime);
			IVecInt assumptions = new VecInt(encoder.getAssumptions(midTime,
				    initialMaxTime));
			sat = problem.isSatisfiable(assumptions);
			if (sat) {
				maxTime = midTime;
			} else {
				minTime = midTime;
			}
		}
		long satTimeEnd = System.currentTimeMillis();

		if (sat) {
			decoder.decode(project, solver.model());
			Log.i("Projektdauer: " + maxTime);
		} else {
			int[] lastSatSolution = solveProblemForInitialMaxTime(initialMaxTime,
					problem, solver);
			if (lastSatSolution != null) {
				decoder.decode(project, lastSatSolution);
				Log.i("Projektdauer: " + initialMaxTime);
			}
			
		}
		Runtime runtime = Runtime.getRuntime();
		Log.writeLog(project.getName() + ";" + (encodeTimeEnd - encodeTimeStart) + " ms;"
				+ (satTimeEnd - satTimeStart) + " ms;"
				+ (runtime.totalMemory() - runtime.freeMemory())
				/ (1024 * 1024) + " mb");
		Log.i("Done");
	}

	private ISolver initSolver() {
		ISolver solver = SolverFactory.newDefault();
		solver.setTimeout(3600); // 1 hour timeout
		return solver;
	}

	private int getMinTime(List<Activity> activities) {
		int minTimeOverDuration = 0;
		int minTimeOverEarlyEnd = 0;
		for (Activity activity : activities) {
			long duration = activity.getDuration();
			int earlyEnd = (int) activity.getEarlyEndDate();
			if (duration > minTimeOverDuration) {
				minTimeOverDuration = (int) duration;
			} else if (earlyEnd > minTimeOverEarlyEnd) {
				minTimeOverEarlyEnd = earlyEnd;
			}
		}
		return (minTimeOverEarlyEnd > minTimeOverDuration) ? minTimeOverEarlyEnd
				: minTimeOverDuration;
	}

	private int getMaxTime(List<Activity> activities) {
		int maxTime = 0;
		for (Activity activity : activities) {
			maxTime += activity.getDuration();
		}
		return maxTime;
	}

	private int getMidTime(long minTime, long maxTime) {
		double midTime = (minTime + maxTime) / 2;
		return (int) Math.floor(midTime);
	}

	private int[] solveProblemForInitialMaxTime(int maxTime, IProblem problem,
			ISolver solver) throws TimeoutException {
		boolean sat = false;
		sat = problem.isSatisfiable();
		if (sat) {
			return solver.model();
		}
		return null;
	}

	private void resetAlgorithm() {
		solver.reset();
		encoder.reset();
		//System.gc();
	}

	public void resetAlgorithm(String cause) {
		Log.writeLog(project.getName() + " - " + cause);
		resetAlgorithm();
	}
}
