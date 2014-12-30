package com.mysoft.b2b.search.scheduler.helper;

import org.apache.log4j.Logger;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class TaskMonitor {
	
	private static final Logger logger = Logger.getLogger(TaskMonitor.class);	

	private Date startTime;
	private int total;
	private int completedCount;
	

	public Date getStartTime() {
		return startTime;
	}


	public void start() {
		this.startTime = new Date();
	}
	
	public void stop() {
		Date now = new Date();
		long costSecond = (now.getTime() - this.startTime.getTime())/1000;
		logger.info("stop. total cost time(second):" + costSecond);
	}


	public int getTotal() {
		return total;
	}


	public void setTotal(int total) {
		this.total = total;
	}


	public int getCompletedCount() {
		return completedCount;
	}


	public void setCompleted(int cnt) {		
		this.completedCount += cnt;
		if(this.completedCount == 0) {
			logger.info("No task executed.");
			return;
		}
		logger.info("Total:" + this.total + " and complted is:" + this.completedCount);
		
		Date now = new Date();
		long costTime = now.getTime() - this.startTime.getTime();
		double perCost = costTime / completedCount;
		double leftCost = (this.total - this.completedCount) / perCost;
		
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.MILLISECOND, (int)leftCost);
		SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		
		
		
		//logger.info("per cost:" + perCost + " and will finished at:" + sf.format(cal.getTime()));
	}
	
	
	
	
}
