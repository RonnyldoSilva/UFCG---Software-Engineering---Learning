/*
 * Copyright (c) May 13, 2017 Systematic Testing Ltd. (www.systematictesting.com) to Present..
 * All rights reserved. 
 */

package com.systematictesting.automation.core.keywords.impl;

import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.systematictesting.automation.core.constants.ElementType;
import com.systematictesting.automation.core.constants.Result;
import com.systematictesting.automation.core.framework.Browser;
import com.systematictesting.automation.core.keywords.Process;
import com.systematictesting.qdos.beans.TestStepData;

public class WaitToFinishAjaxCall implements Process {

	@Override
	public String execute(Map<String, String> elementKeyValuePairs, TestStepData testStep) {
		if (StringUtils.isBlank(elementKeyValuePairs.get(testStep.getElementKey()))){
			return Result.ABORTED + " : ELEMENT_KEY not found.";
		}
		if (StringUtils.isBlank(testStep.getElementValue())){
			return Result.ABORTED + " : ELEMENT_DATA not found.";
		}
		By container = null;
		try {
			int timeInSeconds = Integer.parseInt(testStep.getElementValue());
			if (testStep.getElementType().equals(ElementType.XPATH)) {
				container = By.xpath(elementKeyValuePairs.get(testStep.getElementKey()));
			}
			if (testStep.getElementType().equals(ElementType.ID)) {
				container = By.id(elementKeyValuePairs.get(testStep.getElementKey()));
			}
			if (testStep.getElementType().equals(ElementType.CSS_SELECTOR)) {
				container = By.cssSelector(elementKeyValuePairs.get(testStep.getElementKey()));
			}
			if (testStep.getElementType().equals(ElementType.PARTIAL_LINK_TEXT)) {
				System.out.println(ElementType.PARTIAL_LINK_TEXT+" IS PICKED");
				container = By.partialLinkText(elementKeyValuePairs.get(testStep.getElementKey()));
			}
			if (testStep.getElementType().equals(ElementType.LINK_TEXT)) {
				System.out.println(ElementType.LINK_TEXT+" IS PICKED");
				container = By.linkText(elementKeyValuePairs.get(testStep.getElementKey()));
			}
			
			WebDriverWait wait = new WebDriverWait(Browser.getInstance().getDriver(), timeInSeconds);
			wait.until(ExpectedConditions.presenceOfElementLocated(container));
			
		} catch (Throwable t) {
			return Result.FAIL+" - " + t.getMessage();
		}
		return Result.PASS;

	}

}
