/*
 * Copyright (c) Jan 11, 2017 Systematic Testing Ltd. (www.systematictesting.com) to Present..
 * All rights reserved. 
 */
package com.systematictesting.automation.core.bean;

public class TestSuiteDetails {

	private String testSuiteName;
	private String startTime;
	private String endTime;
	private long startTimeInMilliseconds;
	private long endTimeInMilliseconds;
	private String environment;
	private String version;
	private TestSuiteReportSummary summary = new TestSuiteReportSummary();;
	
	public long getStartTimeInMilliseconds() {
		return startTimeInMilliseconds;
	}
	public void setStartTimeInMilliseconds(long startTimeInMilliseconds) {
		this.startTimeInMilliseconds = startTimeInMilliseconds;
	}
	public long getEndTimeInMilliseconds() {
		return endTimeInMilliseconds;
	}
	public void setEndTimeInMilliseconds(long endTimeInMilliseconds) {
		this.endTimeInMilliseconds = endTimeInMilliseconds;
	}
	public String getTestSuiteName() {
		return testSuiteName;
	}
	public void setTestSuiteName(String testSuiteName) {
		this.testSuiteName = testSuiteName;
	}
	public String getStartTime() {
		return startTime;
	}
	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}
	public String getEndTime() {
		return endTime;
	}
	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}
	public String getEnvironment() {
		return environment;
	}
	public void setEnvironment(String environment) {
		this.environment = environment;
	}
	public String getVersion() {
		return version;
	}
	public void setVersion(String version) {
		this.version = version;
	}
	public TestSuiteReportSummary getSummary() {
		return summary;
	}
	@Override
	public String toString() {
		return "TestSuiteDetails [testSuiteName=" + testSuiteName + ", startTime=" + startTime + ", endTime=" + endTime + ", startTimeInMilliseconds=" + startTimeInMilliseconds + ", endTimeInMilliseconds=" + endTimeInMilliseconds + ", environment=" + environment + ", version=" + version + ", summary=" + summary + "]";
	}
}
