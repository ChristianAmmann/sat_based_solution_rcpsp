package scheduler.encoding;

import java.util.ArrayList;

import scheduler.data.Activity;
import scheduler.data.Project;
import scheduler.log.Log;

public class SATDecoder {

	private static SATDecoder decoder;
	private VariableFactory variableFactory;
	
	private SATDecoder() {
		variableFactory = VariableFactory.getVariableFactory();
	}
	
	public static SATDecoder getDecoder() {
		if(decoder != null) {
			return decoder;
		}
		decoder = new SATDecoder();
		return decoder;
	}

	public void decode(Project project, int[] model) {
		for (int i = 0; model != null && i < model.length; i++) {
			if (model[i] > 0) {
				ArrayList<Integer> variable = variableFactory
						.getKeyByValue(model[i]);
				if (variable.get(0) == VariableFactory.VARIABLE_START) {
					Activity activity = project
							.getActivityById(variable.get(1));
					Log.i(activity.toString() + " Startpunkt: "
							+ variable.get(2));
				}
			}
		}
	}
}
