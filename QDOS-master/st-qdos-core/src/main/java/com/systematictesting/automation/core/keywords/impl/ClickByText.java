/*
 * Copyright (c) Jan 11, 2017 Systematic Testing Ltd. (www.systematictesting.com) to Present..
 * All rights reserved. 
 */

package com.systematictesting.automation.core.keywords.impl;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import com.systematictesting.automation.core.constants.Result;
import com.systematictesting.automation.core.framework.Browser;
import com.systematictesting.automation.core.keywords.Process;
import com.systematictesting.qdos.beans.TestStepData;

/*keyword : ClickByText
 * Object : text with which user want to identify that element (value of corresponding key in object column)
 * proceed on fail: leave blank
 * data column: give 1,2,3 etc on whichever occurrence you are working of that text containing element 
*/
public class ClickByText implements Process {

	@Override
	public String execute(Map<String, String> elementKeyValuePairs, TestStepData testStep) {
		if (StringUtils.isBlank(elementKeyValuePairs.get(testStep.getElementKey()))){
			return Result.ABORTED + " : ELEMENT_KEY not found.";
		}
		if (StringUtils.isBlank(testStep.getElementValue())){
			return Result.ABORTED + " : ELEMENT_DATA not found.";
		}
		try {
			int idata=Integer.parseInt(testStep.getElementValue());
			
			List<WebElement> elements = Browser.getInstance().getDriver().findElements(By.linkText(elementKeyValuePairs.get(testStep.getElementKey())));
			if (elements!=null){
				elements.get(idata).click();
			} else {
				return Result.FAIL;
			}
		} catch (Throwable t) {
			return Result.FAIL+" : Link Not Found";
		}

		return Result.PASS;
	}

}
