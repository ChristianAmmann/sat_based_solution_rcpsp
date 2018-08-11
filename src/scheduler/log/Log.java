package scheduler.log;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import scheduler.encoding.VariableFactory;

public class Log {

	public final static Logger logger = LoggerFactory.getLogger("Log");
	public final static boolean debug = true;
	public static String logPath;
	private static VariableFactory variableFactory;

	private Log() {
		variableFactory = VariableFactory.getVariableFactory();
	}

	public static void d(LogMethod logMethod, String log) {
		if (logMethod.getLogStatus()) {
			Log.d(log);
		}
	}

	public static void d(String log) {
		if (debug) {
			logger.debug(log);
		}
	}

	public static void i(String log) {
		logger.info(log);
	}

	public static String clauseToString(int[] clause) {
		String result = "";
		for (int i = 0; i < clause.length; i++) {
			result += variableFactory.getStringFromId(clause[i]) + " ";
		}
		return result;
	}

	public static String clauseToString(List<Integer> clause) {
		String result = "";
		for (int i = 0; i < clause.size(); i++) {
			String variable = variableFactory.getStringFromId(clause.get(i));
			if (!variable.equals("0")) {
				result += variable + " ";
			}
		}
		return result;
	}

	public static void writeLog(String log) {
		try {
			PrintWriter out = new PrintWriter(new BufferedWriter(
					new FileWriter(logPath, true)));
			out.println(log);
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void setLogPath(String path) {
		logPath = path;
	}
}
