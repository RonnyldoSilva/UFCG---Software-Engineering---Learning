/*
 * Copyright (c) Jan 11, 2017 Systematic Testing Ltd. (www.systematictesting.com) to Present..
 * All rights reserved. 
 */
package com.systematictesting.automation.core.bean;

import java.util.ArrayList;
import java.util.List;

public class TestCaseDetails {

	private String testCaseId;
	private String testCaseName;
	private String status;
	private String startTime;
	private String endTime;
	private long startTimeInMilliseconds;
	private long endTimeInMilliseconds;
	private String statusClass;
	private List<TestStepDetails> testStepsData = new ArrayList<TestStepDetails>();
	private String videoFile;
	
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
	public String getTestCaseId() {
		return testCaseId;
	}
	public void setTestCaseId(String testCaseId) {
		this.testCaseId = testCaseId;
	}
	public String getTestCaseName() {
		return testCaseName;
	}
	public void setTestCaseName(String testCaseName) {
		this.testCaseName = testCaseName;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
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
	public String getStatusClass() {
		return statusClass;
	}
	public void setStatusClass(String statusClass) {
		this.statusClass = statusClass;
	}
	public List<TestStepDetails> getTestStepsData() {
		return testStepsData;
	}
	public String getVideoFile() {
		return videoFile;
	}
	public void setVideoFile(String videoFile) {
		this.videoFile = videoFile;
	}
	@Override
	public String toString() {
		return "TestCaseDetails [testCaseId=" + testCaseId + ", testCaseName=" + testCaseName + ", status=" + status
				+ ", startTime=" + startTime + ", endTime=" + endTime + ", startTimeInMilliseconds="
				+ startTimeInMilliseconds + ", endTimeInMilliseconds=" + endTimeInMilliseconds + ", statusClass="
				+ statusClass + ", testStepsData=" + testStepsData + ", videoFile=" + videoFile + "]";
	}
}
