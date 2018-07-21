/*
 * Copyright (c) Jan 11, 2017 Systematic Testing Ltd. (www.systematictesting.com) to Present..
 * All rights reserved. 
 */
package com.systematictesting.automation.core.client;

import java.util.Properties;

/**
 * 
 * @author sharad
 */
public class SeleniumActivationFrameworkThread implements Runnable {

	private Properties appProperties = new Properties();
	
	
	public Properties getAppProperties() {
		return appProperties;
	}
	public void setAppProperties(Properties appProperties) {
		this.appProperties = appProperties;
	}

	public void run() {
		String mainFrameworkClass = appProperties.getProperty("framework.class");
		try {
			Class<?> frameworkClass = getClass().getClassLoader().loadClass(mainFrameworkClass);
			com.systematictesting.automation.core.main.Framework instanceOfFramework = (com.systematictesting.automation.core.main.Framework) frameworkClass.newInstance();
			instanceOfFramework.activateTestingFramework();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
        
        
	}
}
