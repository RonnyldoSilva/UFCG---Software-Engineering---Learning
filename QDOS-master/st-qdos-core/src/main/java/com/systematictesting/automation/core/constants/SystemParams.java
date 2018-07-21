/*
 * Copyright (c) Jan 11, 2017 Systematic Testing Ltd. (www.systematictesting.com) to Present..
 * All rights reserved. 
 */
package com.systematictesting.automation.core.constants;

import java.io.File;

public interface SystemParams {

	//String TEST_SUITES_LOCATION = "testSuitesLocation";
	String TEST_REPORTING_APP_SERVER_NAME = "testReportingAppServerName";
	String TEST_SUITE_NAME = "testSuiteName";
	String BROWSER_NAME = "browserName";
	String TEST_ENVIRONMENT = "environment";
	String OPERATING_SYSTEM = "operatingSystem";
	String VERSION_NUMBER = "versionNumber";
	String TEST_SITE_NAME = "siteName";
	String TOTAL_REPORT_SUITES = "totalReportSuites";
	String TRACER_STATUS = "tracerStatus";
	String EMAIL = "email";
	String API_KEY = "apiKey";
	String API_KEY_TO_POST_REPORT = "activeAPIkey";
	
	String SCREEN_HEIGHT = "screen.height";
	String SCREEN_WIDTH = "screen.width";
	
	String DOWNLOAD_FILE_LOCATION = System.getProperty(SystemParams.JAVA_TMP_DIR) + File.separator + "downloads";
	
	String PATH_WINDOWS_CHROME_DRIVER = "webdriver.chrome.driver";
	String PATH_WINDOWS_FIREFOX_DRIVER = "webdriver.gecko.driver";
	
	String PATH_MAC_FIREFOX_DRIVER = "webdriver.gecko.driver";
	String PATH_MAC_CHROME_DRIVER = "webdriver.chrome.driver";
	
	//String PATH_IE_WINDOWS_DRIVER = "webdriver.ie.driver";
	String PATH_PHANTOMJS_DRIVER = "webdriver.ghostdriver.driver";
	
	String JAVA_TMP_DIR = "java.io.tmpdir";
	String PROXY_URL = "proxy.url";
	String PROXY_SREPORT_URL = "proxy.sreport.url";
	String ALWAYS_CAPTURE_SCREENSHOT = "always.capture.screenshot";
	String CAPTURE_SCREENSHOT_ON_FAILED_EVENT = "capture.screenshot.on.failed.event";
	String EXECUTED_FROM_COMMAND_PROMPT = "executed.via.commandline";
	
	String FILE_CHROME_DRIVER_OS_WINDOWS = "chromedriver.exe";
	String FILE_CHROME_DRIVER_OS_MAC = "chromedriver";
	
	String FILE_FIREFOX_DRIVER_OS_WINDOWS = "geckodriver.exe";
	String FILE_FIREFOX_DRIVER_OS_MAC = "geckodriver";
	
	//String FILE_IE_DRIVER_EXE_FILE_NAME = "IEDriverServer.exe";
	String FILE_PHANTOMJS_DRIVER_OS_WINDOWS = "phantomjs.exe";
	String FILE_PHANTOMJS_DRIVER_OS_MAC = "phantomjs";
	
	String DRIVERS_DIR = "drivers";
	String ALWAYS_RECORD_VIDEO_OF_TESTCASES = "always.record.testcase.video";
	String RECORD_VIDEO_OF_FAILED_TESTCASES = "record.failed.testcase.video";
}
