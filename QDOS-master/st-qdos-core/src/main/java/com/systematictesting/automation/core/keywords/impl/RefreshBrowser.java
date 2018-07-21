/*
 * Copyright (c) Jan 11, 2017 Systematic Testing Ltd. (www.systematictesting.com) to Present..
 * All rights reserved. 
 */
package com.systematictesting.automation.core.keywords.impl;

import java.util.Map;

import com.systematictesting.automation.core.constants.Result;
import com.systematictesting.automation.core.framework.Browser;
import com.systematictesting.automation.core.keywords.Process;
import com.systematictesting.qdos.beans.TestStepData;

/*
object = blank
data column = blank
proceed on fail = blank 
this is to refresh the browser instance
*/
public class RefreshBrowser implements Process {

	@Override
	public String execute(Map<String, String> elementKeyValuePairs, TestStepData testStep) {
		try {
			Browser.getInstance().getDriver().navigate().refresh();
		} catch (Throwable t) {
			return Result.FAIL+" - " + t.getMessage();
		}
		return Result.PASS;
	}

}
