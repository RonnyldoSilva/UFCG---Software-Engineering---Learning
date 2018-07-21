/*
 * Copyright (c) Jan 11, 2017 Systematic Testing Ltd. (www.systematictesting.com) to Present..
 * All rights reserved. 
 */
package com.systematictesting.automation.core.framework;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.Proxy;
import org.openqa.selenium.Proxy.ProxyType;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.phantomjs.PhantomJSDriver;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.support.events.EventFiringWebDriver;

import com.systematictesting.automation.core.constants.FrameworkParams;
import com.systematictesting.automation.core.constants.SystemParams;
import com.systematictesting.automation.core.utils.CommandLineParamsUtils;

public class Browser {

	public static WebDriver wbdv = null;
	public static EventFiringWebDriver driver = null;
	private static final Browser browser = new Browser();

	private Browser() {
	}

	public static Browser getInstance() {
		return browser;
	}

	public EventFiringWebDriver getDriver() {
		if (wbdv == null) {
			if (CommandLineParamsUtils.getInstance().getBrowserName().equals(FrameworkParams.BROWSER_FIREFOX)) {
				FirefoxProfile profile = new FirefoxProfile();
				DesiredCapabilities capabilities = DesiredCapabilities.firefox();
				if (StringUtils.isNotBlank(CommandLineParamsUtils.getInstance().getProxyUrl())) {
					setProxyServerInBrowserCapabilities(capabilities);
				}
				capabilities.setCapability(FirefoxDriver.PROFILE, profile);
				wbdv = new FirefoxDriver(capabilities);
				driver = new EventFiringWebDriver(wbdv);
				driver.manage().window();
				String heightOfScreen = CommandLineParamsUtils.getInstance().getScreenHeightInPixel();
				String widthOfScreen = CommandLineParamsUtils.getInstance().getScreenWidthInPixel();
				if (heightOfScreen != null && widthOfScreen!=null && heightOfScreen.length() > 0 && widthOfScreen.length() > 0){
					int width = getWidthOfScreen(widthOfScreen);
					int height = getHeightOfScreen(heightOfScreen);
					driver.manage().window().setSize(new Dimension(width,height));
				} else {
					driver.manage().window().maximize();
				}
				driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
			}
			if (CommandLineParamsUtils.getInstance().getBrowserName().equals(FrameworkParams.BROWSER_CHROME)) {
				DesiredCapabilities capabilities = DesiredCapabilities.chrome();
				
				Map<String, Object> chromePrefs = new HashMap<String, Object>();
				chromePrefs.put("profile.default_content_settings.popups", 0);
				chromePrefs.put("download.default_directory", SystemParams.DOWNLOAD_FILE_LOCATION);
				ChromeOptions options = new ChromeOptions();
				options.setExperimentalOption("prefs", chromePrefs);
				capabilities.setCapability(CapabilityType.ACCEPT_SSL_CERTS, true);
				capabilities.setCapability(ChromeOptions.CAPABILITY, options);
				capabilities.setCapability(CapabilityType.UNEXPECTED_ALERT_BEHAVIOUR, "ignore");
				
				if (StringUtils.isNotBlank(CommandLineParamsUtils.getInstance().getProxyUrl())) {
					setProxyServerInBrowserCapabilities(capabilities);
				}
				wbdv = new ChromeDriver(capabilities);
				driver = new EventFiringWebDriver(wbdv);
				driver.manage().window();
				String heightOfScreen = CommandLineParamsUtils.getInstance().getScreenHeightInPixel();
				String widthOfScreen = CommandLineParamsUtils.getInstance().getScreenWidthInPixel();
				if (heightOfScreen != null && widthOfScreen!=null && heightOfScreen.length() > 0 && widthOfScreen.length() > 0){
					System.out.println("@@@@ SCREEN SIZE : "+widthOfScreen+"X"+heightOfScreen);
					int width = getWidthOfScreen(widthOfScreen);
					int height = getHeightOfScreen(heightOfScreen);
					driver.manage().window().setSize(new Dimension(width,height));
				} else {
					System.out.println("@@@@ SCREEN SIZE : Falling back to Maximize Window Functionality. ");
					driver.manage().window().maximize();
				}
				
				driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
			}
//			if (CommandLineParamsUtils.getInstance().getBrowserName().equals(FrameworkParams.BROWSER_IE)) {
//				if (StringUtils.isNotBlank(CommandLineParamsUtils.getInstance().getProxyUrl())){
//					DesiredCapabilities capabilities = DesiredCapabilities.internetExplorer();
//					capabilities.setCapability(InternetExplorerDriver.INTRODUCE_FLAKINESS_BY_IGNORING_SECURITY_DOMAINS, true);
//					wbdv = new InternetExplorerDriver(capabilities);
//				} else {
//					wbdv = new InternetExplorerDriver();
//				}
//				
//				driver = new EventFiringWebDriver(wbdv);
//				driver.manage().window();
//				driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
//			}
			if (CommandLineParamsUtils.getInstance().getBrowserName().equals(FrameworkParams.BROWSER_HEADLESS)) {
				DesiredCapabilities capabilities = DesiredCapabilities.phantomjs();
				if (StringUtils.isNotBlank(CommandLineParamsUtils.getInstance().getProxyUrl())) {
					setProxyServerInBrowserCapabilities(capabilities);
				}
				capabilities.setCapability("phantomjs.binary.path", System.getProperty(SystemParams.PATH_PHANTOMJS_DRIVER));
				capabilities.setJavascriptEnabled(true);
				wbdv = new PhantomJSDriver(capabilities);
				driver = new EventFiringWebDriver(wbdv);
				driver.manage().window();
				String heightOfScreen = CommandLineParamsUtils.getInstance().getScreenHeightInPixel();
				String widthOfScreen = CommandLineParamsUtils.getInstance().getScreenWidthInPixel();
				if (heightOfScreen != null && widthOfScreen!=null && heightOfScreen.length() > 0 && widthOfScreen.length() > 0){
					int width = getWidthOfScreen(widthOfScreen);
					int height = getHeightOfScreen(heightOfScreen);
					driver.manage().window().setSize(new Dimension(width,height));
				} else {
					driver.manage().window().maximize();
				}
				driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
			}
		}

		return driver;
	}

	private int getHeightOfScreen(String heightOfScreen) {
		try{
			return Integer.parseInt(heightOfScreen);
		} catch(Exception e){
			throw new RuntimeException("Supplied width of screen is not a valid integer.");
		}
	}

	private int getWidthOfScreen(String widthOfScreen) {
		try{
			return Integer.parseInt(widthOfScreen);
		} catch(Exception e){
			throw new RuntimeException("Supplied width of screen is not a valid integer.");
		}
	}

	private void setProxyServerInBrowserCapabilities(DesiredCapabilities capabilities) {
		Proxy proxy = new Proxy();
		proxy.setProxyType(ProxyType.MANUAL);
		proxy.setHttpProxy(CommandLineParamsUtils.getInstance().getProxyUrl());
		proxy.setSslProxy(CommandLineParamsUtils.getInstance().getProxyUrl());
		capabilities.setCapability(CapabilityType.PROXY, proxy);
	}

	public WebDriver getWebDriver() {
		return wbdv;
	}

	public void resetWebDriver() {
		if (wbdv != null && driver != null) {
			this.close();
		}
		driver = null;
		wbdv = null;
	}

	public void close() {
		if (CommandLineParamsUtils.getInstance().getBrowserName().equals(FrameworkParams.BROWSER_FIREFOX)) {
			driver.quit();
		}
		if (CommandLineParamsUtils.getInstance().getBrowserName().equals(FrameworkParams.BROWSER_CHROME)) {
			driver.close();
			wbdv.quit();
		}
		if (CommandLineParamsUtils.getInstance().getBrowserName().equals(FrameworkParams.BROWSER_HEADLESS)){
			driver.close();
			wbdv.quit();
		}
//		if (CommandLineParamsUtils.getInstance().getBrowserName().equals(FrameworkParams.BROWSER_IE)) {
//			wbdv.quit();
//			driver.quit();
//		}
	}

}
