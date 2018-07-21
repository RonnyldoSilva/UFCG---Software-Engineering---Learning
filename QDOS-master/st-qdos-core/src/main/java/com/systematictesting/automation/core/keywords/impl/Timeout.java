/*
 * Copyright (c) Jan 11, 2017 Systematic Testing Ltd. (www.systematictesting.com) to Present..
 * All rights reserved. 
 */
package com.systematictesting.automation.core.keywords.impl;

import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.systematictesting.automation.core.constants.Result;
import com.systematictesting.automation.core.keywords.Process;
import com.systematictesting.qdos.beans.TestStepData;

public class Timeout implements Process {

	@Override
	public String execute(Map<String, String> elementKeyValuePairs, TestStepData testStep) {
		if (StringUtils.isBlank(testStep.getElementValue())){
			return Result.ABORTED + " : ELEMENT_DATA not found.";
		}
		String data = "0";
		if (testStep.getElementValue().indexOf(".")!=-1){
			data = testStep.getElementValue().substring(0,testStep.getElementValue().indexOf("."));
		}
		try {
			Thread.sleep(Long.parseLong(data));
		} catch (NumberFormatException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return Result.PASS;
	}

}
