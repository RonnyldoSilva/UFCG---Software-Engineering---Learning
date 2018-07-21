/*
 * Copyright (c) Jan 11, 2017 Systematic Testing Ltd. (www.systematictesting.com) to Present..
 * All rights reserved. 
 */
package com.systematictesting.automation.core.keywords.impl;

import java.util.Map;

import com.systematictesting.automation.core.constants.Result;
import com.systematictesting.automation.core.framework.Browser;
import com.systematictesting.automation.core.keywords.Process;
import com.systematictesting.automation.core.main.ActivateFramework;
import com.systematictesting.qdos.beans.TestStepData;

/*
keyword name : SwitchToChildWindow
object : blank
proceed on fail : not mandatory
data column : blank

This switches the control from parent window to child window after saving the handlers of parent window automatically which helps us to return back to parent window if required.
*/
public class SwitchToChildWindow implements Process {

	@Override
	public String execute(Map<String, String> elementKeyValuePairs, TestStepData testStep) {

		try {
			// ---------------
			// Get Parent window handle
			String winHandleBefore = Browser.getInstance().getDriver().getWindowHandle();
			ActivateFramework.ParentWindowHandler = winHandleBefore;

			for (String winHandle : Browser.getInstance().getDriver().getWindowHandles()) {
				// Switch to child window
				Browser.getInstance().getDriver().switchTo().window(winHandle);
			}
			// Now child window is activated and can be used
			ActivateFramework.ChildWindowHandler = Browser.getInstance().getDriver().getWindowHandle();

		} catch (Throwable t) {
			return Result.FAIL + " -" + t.getMessage();
		}
		return Result.PASS;
	}

}
