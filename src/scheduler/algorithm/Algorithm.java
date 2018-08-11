package scheduler.algorithm;

import scheduler.data.Project;

public abstract class Algorithm {
	Project project;

    public Algorithm(Project project) {
    	this.project = project;
    }

	public abstract void calculate();
}
