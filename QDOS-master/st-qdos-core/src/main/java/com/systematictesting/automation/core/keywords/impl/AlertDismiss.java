/*
 * Copyright (c) Jan 11, 2017 Systematic Testing Ltd. (www.systematictesting.com) to Present..
 * All rights reserved. 
 */

package com.systematictesting.automation.core.keywords.impl;

import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.systematictesting.automation.core.constants.Result;
import com.systematictesting.automation.core.framework.Browser;
import com.systematictesting.automation.core.keywords.Process;
import com.systematictesting.qdos.beans.TestStepData;

/* 
 * input parameters:: 
 * keyword -> AlertDismiss
 * object -> nothing, keep it blank
 * proceed on fail -> blank 
 * Data_Column_Name -> blank
 * 
 * Output parameters::
 * Used to accept the alert ie click on "No","Cancel" or like this button
*/
public class AlertDismiss implements Process {
	private static final Logger log = Logger.getLogger(AlertDismiss.class.getName());

	@Override
	public String execute(Map<String, String> elementKeyValuePairs, TestStepData testStep) {
		log.log(Level.INFO,"Alert Dismiss");
		
		try{
			Browser.getInstance().getDriver().switchTo().alert().dismiss();;
		} catch (Exception t){
			return Result.FAIL+"--"+t.getMessage();
		}
		
		return Result.PASS;
	}

}
