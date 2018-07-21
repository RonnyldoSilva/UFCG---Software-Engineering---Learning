/*
 * Copyright (c) Jan 11, 2017 Systematic Testing Ltd. (www.systematictesting.com) to Present..
 * All rights reserved. 
 */
package com.systematictesting.automation.core.bean;

import java.util.ArrayList;
import java.util.List;

public class TestReport {

	private TestSuiteDetails testSuiteDetails;
	private List<TestCaseDetails> testCases = new ArrayList<TestCaseDetails>();
	public TestSuiteDetails getTestSuiteDetails() {
		return testSuiteDetails;
	}
	public void setTestSuiteDetails(TestSuiteDetails testSuiteDetails) {
		this.testSuiteDetails = testSuiteDetails;
	}
	public List<TestCaseDetails> getTestCases() {
		return testCases;
	}
	@Override
	public String toString() {
		return "TestReport [testSuiteDetails=" + testSuiteDetails + ", testCases=" + testCases + "]";
	}
}
