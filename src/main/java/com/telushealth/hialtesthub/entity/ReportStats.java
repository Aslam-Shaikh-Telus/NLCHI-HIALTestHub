package com.telushealth.hialtesthub.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

public class ReportStats {
	
	private String durationRange;
	private long interactions;
	private double percentageTotal;
	private double avgDuration;

	public ReportStats() {
		super();
		// TODO Auto-generated constructor stub
	}

	public ReportStats(String durationRange, long interactions, double percentageTotal, double avgDuration) {
		super();
		this.durationRange = durationRange;
		this.interactions = interactions;
		this.percentageTotal = percentageTotal;
		this.avgDuration = avgDuration;
	}

	public String getDurationRange() {
		return durationRange;
	}

	public void setDurationRange(String durationRange) {
		this.durationRange = durationRange;
	}

	public long getInteractions() {
		return interactions;
	}

	public void setInteractions(long interactions) {
		this.interactions = interactions;
	}

	public double getPercentageTotal() {
		return percentageTotal;
	}

	public void setPercentageTotal(double percentageTotal) {
		this.percentageTotal = percentageTotal;
	}

	public double getAvgDuration() {
		return avgDuration;
	}

	public void setAvgDuration(double avgDuration) {
		this.avgDuration = avgDuration;
	}

}
