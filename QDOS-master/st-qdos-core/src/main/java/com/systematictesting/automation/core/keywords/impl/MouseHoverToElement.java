/*
 * Copyright (c) Jan 11, 2017 Systematic Testing Ltd. (www.systematictesting.com) to Present..
 * All rights reserved. 
 */
package com.systematictesting.automation.core.keywords.impl;

import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;

import com.systematictesting.automation.core.constants.ElementType;
import com.systematictesting.automation.core.constants.Result;
import com.systematictesting.automation.core.framework.Browser;
import com.systematictesting.automation.core.keywords.Process;
import com.systematictesting.qdos.beans.TestStepData;

/*
 * keyword : MouseHoverToElement
 * object : value with object name must contain the xpath of the position or item
 * proceed on fail : not mandatory
 * data column: not mandatory. leave blank
 */
public class MouseHoverToElement implements Process {

	@Override
	public String execute(Map<String, String> elementKeyValuePairs, TestStepData testStep) {
		if (StringUtils.isBlank(elementKeyValuePairs.get(testStep.getElementKey()))){
			return Result.ABORTED + " : ELEMENT_KEY not found.";
		}
WebElement element = null;
		
		try {
			if (testStep.getElementType().equals(ElementType.XPATH)) {
				element = Browser.getInstance().getDriver().findElement(By.xpath(elementKeyValuePairs.get(testStep.getElementKey())));
			}
			if (testStep.getElementType().equals(ElementType.ID)) {
				element = Browser.getInstance().getDriver().findElement(By.id(elementKeyValuePairs.get(testStep.getElementKey())));
			}
			if (testStep.getElementType().equals(ElementType.CSS_SELECTOR)) {
				element = Browser.getInstance().getDriver().findElement(By.cssSelector(elementKeyValuePairs.get(testStep.getElementKey())));
			}
			if (element!=null){
				Actions actions = new Actions(Browser.getInstance().getDriver());
				actions.moveToElement(element).build().perform();
				Thread.sleep(3000L);
			} else {
				return Result.FAIL;
			}
		} catch (Throwable t) {
			return Result.FAIL+" - " + t.getMessage();
		}

		return Result.PASS;
	}

}
