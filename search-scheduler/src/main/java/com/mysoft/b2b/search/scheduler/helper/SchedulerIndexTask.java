package com.mysoft.b2b.search.scheduler.helper;

import com.mysoft.b2b.search.spi.SearchModel;
import org.apache.log4j.Logger;

import java.util.concurrent.Callable;

public class SchedulerIndexTask implements Callable<SchedulerIndexTask> {
	private static final Logger logger = Logger.getLogger(SchedulerIndexTask.class);	
	
	private SchedulerThreadData scheduler;	
	
	
	private String id;
	private SearchModel newObject ;
	
	private TaskMonitor monitor;
	
	public SchedulerIndexTask call() throws Exception {
		try {
			boolean result = run();
			if (!result) {
				return null;
			}
			return this;
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("execute index task error:" );
		}
		finally {
			monitor.setCompleted(1);
		}
		
		return this;
	}
	
	private boolean run(){
		this.newObject = scheduler.getSingleDataObj(id);
		return newObject != null;
	}
	
	public void setScheduler(SchedulerThreadData scheduler) {
		this.scheduler = scheduler;
	}

	public void setMonitor(TaskMonitor monitor) {
		this.monitor = monitor;
	}

	public void setId(String id) {
		this.id = id;
	}

	public SearchModel getNewObject() {
		return newObject;
	}

}
