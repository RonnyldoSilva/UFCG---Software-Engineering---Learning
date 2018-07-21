/*
 * Copyright (c) Jan 11, 2017 Systematic Testing Ltd. (www.systematictesting.com) to Present..
 * All rights reserved. 
 */
package com.systematictesting.automation.core.keywords;

import java.util.Map;

import com.systematictesting.qdos.beans.TestStepData;

public interface Process {

	String execute(Map<String, String> elementKeyValuePairs, TestStepData testStep);
}
