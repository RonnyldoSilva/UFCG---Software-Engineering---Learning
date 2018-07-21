/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.systematictesting.automation.core.framework.impl;

import com.systematictesting.automation.core.framework.TraceHandler;
import com.systematictesting.automation.core.framework.Tracer;
import com.systematictesting.automation.core.utils.CommandLineParamsUtils;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

/**
 *
 * @author sharad
 */
public class BrowserBasedTraceHandler implements TraceHandler {

    @Override
    public void logEvents(String log) {
        if (CommandLineParamsUtils.getInstance().getTracerStatus()) {
            try {
                WebElement element = Tracer.getInstance().getDriver().findElement(By.id("tracerLog"));
                if (element != null) {
                    element.sendKeys(log + "\n");
                }
            } catch (Throwable t) {
            }
        }
    }

}
