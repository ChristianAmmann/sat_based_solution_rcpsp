package scheduler.log;


public enum LogMethod {
	UNIQUE_START_INSTANT (false),
	START_IN_TIME (false),
	RUNTIME (false),
	WORKLOAD (false),
	RELATION_TYPE_FS(false),
	RELATION_TYPE_SS(false),
	RELATION_TYPE_FF(false),
	RELATION_TYPE_SF(false),
	RESOURCE_CARDI(false),
	RESOURCE_POWERSET(false),
	BCC(false),
	IMPLIES(false);
	
	
	private final boolean log;
	
	LogMethod(boolean log) {
	    this.log  = log;
	}
	
	public boolean getLogStatus() {
	    return this.log;
	}
}
