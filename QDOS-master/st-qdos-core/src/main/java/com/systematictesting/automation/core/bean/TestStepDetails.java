/*
 * Copyright (c) Jan 11, 2017 Systematic Testing Ltd. (www.systematictesting.com) to Present..
 * All rights reserved. 
 */
package com.systematictesting.automation.core.bean;

public class TestStepDetails {

	private String dataSetId;
	private String stepId;
	private String proceedOnFail;
	private String stepDescription;
	private String stepKeyword;
	private String stepStatus;
	private String stepStatusClass;
	private String stepScreenShot;
	private String stepPageStats;
	private String systemMessage;
	private long startTimeInMilliseconds;
	private long endTimeInMilliseconds;
	
	public String getSystemMessage() {
		return systemMessage;
	}
	public void setSystemMessage(String systemMessage) {
		this.systemMessage = systemMessage;
	}
	public String getProceedOnFail() {
		return proceedOnFail;
	}
	public void setProceedOnFail(String proceedOnFail) {
		this.proceedOnFail = proceedOnFail;
	}
	public String getDataSetId() {
		return dataSetId;
	}
	public void setDataSetId(String dataSetId) {
		this.dataSetId = dataSetId;
	}
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
	public String getStepId() {
		return stepId;
	}
	public void setStepId(String stepId) {
		this.stepId = stepId;
	}
	public String getStepDescription() {
		return stepDescription;
	}
	public void setStepDescription(String stepDescription) {
		this.stepDescription = stepDescription;
	}
	public String getStepKeyword() {
		return stepKeyword;
	}
	public void setStepKeyword(String stepKeyword) {
		this.stepKeyword = stepKeyword;
	}
	public String getStepStatus() {
		return stepStatus;
	}
	public void setStepStatus(String stepStatus) {
		this.stepStatus = stepStatus;
	}
	public String getStepStatusClass() {
		return stepStatusClass;
	}
	public void setStepStatusClass(String stepStatusClass) {
		this.stepStatusClass = stepStatusClass;
	}
	public String getStepScreenShot() {
		return stepScreenShot;
	}
	public void setStepScreenShot(String stepScreenShot) {
		this.stepScreenShot = stepScreenShot;
	}
	public String getStepPageStats() {
		return stepPageStats;
	}
	public void setStepPageStats(String stepPageStats) {
		this.stepPageStats = stepPageStats;
	}
	@Override
	public String toString() {
		return "TestStepDetails [dataSetId=" + dataSetId + ", stepId=" + stepId + ", proceedOnFail=" + proceedOnFail + ", stepDescription=" + stepDescription + ", stepKeyword=" + stepKeyword + ", stepStatus=" + stepStatus + ", stepStatusClass=" + stepStatusClass + ", stepScreenShot=" + stepScreenShot + ", stepPageStats=" + stepPageStats + ", systemMessage=" + systemMessage + ", startTimeInMilliseconds=" + startTimeInMilliseconds + ", endTimeInMilliseconds=" + endTimeInMilliseconds + "]";
	}
}
