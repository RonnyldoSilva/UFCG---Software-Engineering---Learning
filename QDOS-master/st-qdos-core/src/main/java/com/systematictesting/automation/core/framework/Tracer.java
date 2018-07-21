/*
 * Copyright (c) Jan 11, 2017 Systematic Testing Ltd. (www.systematictesting.com) to Present..
 * All rights reserved. 
 */
package com.systematictesting.automation.core.framework;

import java.util.concurrent.TimeUnit;

import org.openqa.selenium.Dimension;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.phantomjs.PhantomJSDriver;
import org.openqa.selenium.support.events.EventFiringWebDriver;

import com.systematictesting.automation.core.constants.FrameworkParams;
import com.systematictesting.automation.core.framework.impl.BrowserBasedTraceHandler;
import com.systematictesting.automation.core.utils.CommandLineParamsUtils;

public class Tracer {
	public static WebDriver wbdv = null;
	public static EventFiringWebDriver driver = null;
	private static final Tracer tracer = new Tracer();
	private TraceHandler traceHandler = new BrowserBasedTraceHandler();

	private Tracer() {
	}

	public static Tracer getInstance() {
		return tracer;
	}

	public EventFiringWebDriver getDriver() {
		if (wbdv == null) {
			if (CommandLineParamsUtils.getInstance().getBrowserName().equals(FrameworkParams.BROWSER_FIREFOX)) {
				wbdv = new FirefoxDriver();
				driver = new EventFiringWebDriver(wbdv);
				driver.manage().window();
				driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
				driver.manage().window().setSize(new Dimension(280, 310));
			}
			if (CommandLineParamsUtils.getInstance().getBrowserName().equals(FrameworkParams.BROWSER_CHROME)) {
				wbdv = new ChromeDriver();
				driver = new EventFiringWebDriver(wbdv);
				driver.manage().window();
				driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
				driver.manage().window().setSize(new Dimension(280, 310));
			}
//			if(CommandLineParamsUtils.getInstance().getBrowserName().equals(FrameworkParams.BROWSER_IE)){
//				wbdv = new InternetExplorerDriver();
//				driver = new EventFiringWebDriver(wbdv);
//				driver.manage().window();
//				driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
//				driver.manage().window().setSize(new Dimension(280, 310));
//			}
			if(CommandLineParamsUtils.getInstance().getBrowserName().equals(FrameworkParams.BROWSER_HEADLESS)){
				wbdv = new PhantomJSDriver();
				driver = new EventFiringWebDriver(wbdv);
				driver.manage().window();
				driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
				driver.manage().window().setSize(new Dimension(280, 310));
			}
		}
		return driver;
	}

	public WebDriver getWebDriver() {
		return wbdv;
	}

	public void resetWebDriver() {
		if (CommandLineParamsUtils.getInstance().getTracerStatus()) {
			if (wbdv != null && driver != null) {
				driver.close();
				this.close();
			}
			driver = null;
			wbdv = null;
		}
	}

	public void logEvents(String log) {
		traceHandler.logEvents(log);
	}

	public void close() {
		if (CommandLineParamsUtils.getInstance().getTracerStatus()) {
			if (CommandLineParamsUtils.getInstance().getBrowserName().equals(FrameworkParams.BROWSER_FIREFOX)) {
				wbdv.close();
			}
			if (CommandLineParamsUtils.getInstance().getBrowserName().equals(FrameworkParams.BROWSER_CHROME)) {
				wbdv.quit();
			}
//			if(CommandLineParamsUtils.getInstance().getBrowserName().equals(FrameworkParams.BROWSER_IE)){
//				wbdv.quit();
//			}
		}
	}

	public TraceHandler getTraceHandler() {
		return traceHandler;
	}

	public void setTraceHandler(TraceHandler traceHandler) {
		this.traceHandler = traceHandler;
	}
}
