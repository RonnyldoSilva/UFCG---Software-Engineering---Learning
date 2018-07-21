/*
 * Copyright (c) Jan 11, 2017 Systematic Testing Ltd. (www.systematictesting.com) to Present..
 * All rights reserved. 
 */
package com.systematictesting.automation.core.bean;

import java.util.HashMap;
import java.util.Map;

public class TestSuiteReportSummary {

	private Map<String,String> reportSummary = new HashMap<String, String>();

	public Map<String, String> getReportSummary() {
		return reportSummary;
	}

	@Override
	public String toString() {
		return "TestSuiteReportSummary [reportSummary=" + reportSummary + "]";
	}
	
	
}
