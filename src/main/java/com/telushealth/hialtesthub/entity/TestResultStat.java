package com.telushealth.hialtesthub.entity;

import org.springframework.data.annotation.Id;

public class TestResultStat {
	
	@Id
	private String interactionId;
	private String description;
	private int testCount;
	private int failCount;
	private double successRatio;
	private double minResponseTime;
	private double maxResponseTime;
	private double avgResponseTime;

	public TestResultStat() {
		super();
	}

	public TestResultStat(String interactionId, String description, int testCount, int failCount, double successRatio,
			double minResponseTime, double maxResponseTime, double avgResponseTime) {
		super();
		this.interactionId = interactionId;
		this.description = description;
		this.testCount = testCount;
		this.failCount = failCount;
		this.successRatio = successRatio;
		this.minResponseTime = minResponseTime;
		this.maxResponseTime = maxResponseTime;
		this.avgResponseTime = avgResponseTime;
	}

	public String getInteractionId() {
		return interactionId;
	}

	public void setInteractionId(String interactionId) {
		this.interactionId = interactionId;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public int getTestCount() {
		return testCount;
	}

	public void setTestCount(int testCount) {
		this.testCount = testCount;
	}

	public int getFailCount() {
		return failCount;
	}

	public void setFailCount(int failCount) {
		this.failCount = failCount;
	}

	public double getSuccessRatio() {
		return successRatio;
	}

	public void setSuccessRatio(double successRatio) {
		this.successRatio = successRatio;
	}

	public double getMinResponseTime() {
		return minResponseTime;
	}

	public void setMinResponseTime(double minResponseTime) {
		this.minResponseTime = minResponseTime;
	}

	public double getMaxResponseTime() {
		return maxResponseTime;
	}

	public void setMaxResponseTime(double maxResponseTime) {
		this.maxResponseTime = maxResponseTime;
	}

	public double getAvgResponseTime() {
		return avgResponseTime;
	}

	public void setAvgResponseTime(double avgResponseTime) {
		this.avgResponseTime = avgResponseTime;
	}

}
