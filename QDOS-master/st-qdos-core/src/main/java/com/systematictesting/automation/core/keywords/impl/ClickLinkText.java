/*
 * Copyright (c) Jan 11, 2017 Systematic Testing Ltd. (www.systematictesting.com) to Present..
 * All rights reserved. 
 */
package com.systematictesting.automation.core.keywords.impl;

import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import com.systematictesting.automation.core.constants.ElementType;
import com.systematictesting.automation.core.constants.Result;
import com.systematictesting.automation.core.framework.Browser;
import com.systematictesting.automation.core.keywords.Process;
import com.systematictesting.qdos.beans.TestStepData;

public class ClickLinkText implements Process {

	@Override
	public String execute(Map<String, String> elementKeyValuePairs, TestStepData testStep) {
		if (StringUtils.isBlank(elementKeyValuePairs.get(testStep.getElementKey()))){
			return Result.ABORTED + " : ELEMENT_KEY not found.";
		}
		
		WebElement element = null;
		try {
			if (testStep.getElementType().equals(ElementType.PARTIAL_LINK_TEXT)) {
				System.out.println(ElementType.PARTIAL_LINK_TEXT+" IS PICKED");
				element = Browser.getInstance().getDriver().findElement(By.partialLinkText(elementKeyValuePairs.get(testStep.getElementKey())));
			}
			if (testStep.getElementType().equals(ElementType.LINK_TEXT)) {
				System.out.println(ElementType.LINK_TEXT+" IS PICKED");
				element = Browser.getInstance().getDriver().findElement(By.linkText(elementKeyValuePairs.get(testStep.getElementKey())));
			}
			if (element!=null){
				element.click();
			} else {
				return Result.FAIL+" - Element is NULL";
			}
		} catch (Throwable t) {
			return Result.FAIL+" - Link Not Found";
		}

		return Result.PASS;
	}

}
