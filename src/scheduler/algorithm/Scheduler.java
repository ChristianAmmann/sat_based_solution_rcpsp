package scheduler.algorithm;

import scheduler.data.Project;
import scheduler.log.Log;
import scheduler.mapping.Mapper;

public class Scheduler {

	private static Project project;
	private static Algorithm rcpsp;
	private static Algorithm fwd;
	
	public static void main(String[] args) {
		boolean bccMode = true;
		String projectPath = "";
		for (int i = 0; i + 1 < args.length; i++) {
			switch (args[i]) {
			case "-bcc":
				bccMode = true;
				break;
			case "-pow":
				bccMode = false;
				break;
			case "-logPath":
				Log.setLogPath(args[++i]);
				break;
			case "-project":
				projectPath = args[++i];
				break;
			}
		}
		Log.i(projectPath);
		Mapper mapper = Mapper.getMapper();
		project = mapper.readProject(projectPath);
		fwd = new FowardAlgorithm(project);
		rcpsp = new RCPSPAlgorithm(project, bccMode);
		fwd.calculate();
		rcpsp.calculate();
	}
}
