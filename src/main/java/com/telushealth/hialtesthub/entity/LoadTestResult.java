package com.telushealth.hialtesthub.entity;

import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
public class LoadTestResult {
	@Id
	private String id;

	@Indexed(unique = true)
	private String startTestTime;
	private String endTestTime;
	private int numThreads;
	private long testDurationSeconds;

	List<TestResultStat> testResultStats;

	public LoadTestResult() {
		super();
	}

	public LoadTestResult(String startTestTime, int numThreads, long testDurationSeconds,
			List<TestResultStat> testResultStats) {
		super();
		this.startTestTime = startTestTime;
		this.numThreads = numThreads;
		this.testDurationSeconds = testDurationSeconds;
		this.testResultStats = testResultStats;
	}

	public String getStartTestTime() {
		return startTestTime;
	}

	public void setStartTestTime(String startTestTime) {
		this.startTestTime = startTestTime;
	}

	public String getEndTestTime() {
		return endTestTime;
	}

	public void setEndTestTime(String endTestTime) {
		this.endTestTime = endTestTime;
	}

	public int getNumThreads() {
		return numThreads;
	}

	public void setNumThreads(int numThreads) {
		this.numThreads = numThreads;
	}

	public long getTestDurationSeconds() {
		return testDurationSeconds;
	}

	public void setTestDurationSeconds(long testDurationSeconds) {
		this.testDurationSeconds = testDurationSeconds;
	}

	public List<TestResultStat> getTestResultStats() {
		return testResultStats;
	}

	public void setTestResultStats(List<TestResultStat> testResultStats) {
		this.testResultStats = testResultStats;
	}

}
