/*
 * Copyright (c) Jan 11, 2017 Systematic Testing Ltd. (www.systematictesting.com) to Present..
 * All rights reserved. 
 */
package com.systematictesting.automation.core.bean;

import java.util.HashMap;
import java.util.Map;

public class SuiteReport {

	private Map<String, TestReport> testSuiteReports = new HashMap<String,TestReport>();

	public Map<String, TestReport> getTestSuiteReports() {
		return testSuiteReports;
	}

	@Override
	public String toString() {
		return "SuiteReport [testSuiteReports=" + testSuiteReports + "]";
	}

}
