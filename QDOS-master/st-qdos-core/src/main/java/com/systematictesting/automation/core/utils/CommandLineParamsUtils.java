/*
 * Copyright (c) Jan 11, 2017 Systematic Testing Ltd. (www.systematictesting.com) to Present..
 * All rights reserved. 
 */
package com.systematictesting.automation.core.utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.lang3.StringUtils;

import com.systematictesting.automation.core.constants.FrameworkParams;
import com.systematictesting.automation.core.constants.SystemParams;
import com.systematictesting.automation.core.exceptions.MandatoryParameterException;

public class CommandLineParamsUtils {
	private static final Logger log = Logger.getLogger(CommandLineParamsUtils.class.getName());
	private static CommandLineParamsUtils frameworkUtils = new CommandLineParamsUtils();
	private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd-hh-mm-ss");
	private int sizeOfTestSuitesToProcess = 0;

	//private String environment = System.getProperty(SystemParams.TEST_ENVIRONMENT);
	private String operatingSystem = System.getProperty(SystemParams.OPERATING_SYSTEM);
	private String siteName = System.getProperty(SystemParams.TEST_SITE_NAME);
	private String versionNumber = System.getProperty(SystemParams.VERSION_NUMBER);
	private String reportCollectionServerName = System.getProperty(SystemParams.TEST_REPORTING_APP_SERVER_NAME);
	private String testReportLocation = "/UploadJsonTestReport.file";
	private String screenshotUploadLocation = "/servlet/module/qdos/UploadScreenShot.file";
	private String startSiteVersionURL = "/module/qdos/user/startSiteVersion.rest";
	private String finishSiteVersionURL = "/module/qdos/user/finishSiteVersion.rest";
	private String startSuiteVersionURL = "/module/qdos/client/startTestSuite.rest";
	private String finishSuiteVersionURL = "/module/qdos/client/finishTestSuite.rest";
	private String startTestCaseURL = "/module/qdos/client/startTestCase.rest";
	private String finishTestCaseURL = "/module/qdos/client/finishTestCase.rest";
	private String pushTestStepURL = "/module/qdos/client/pushTestStep.rest";
	private String testSuiteName = System.getProperty(SystemParams.TEST_SUITE_NAME);
	private String screenHeightInPixel = System.getProperty(SystemParams.SCREEN_HEIGHT);
	private String screenWidthInPixel = System.getProperty(SystemParams.SCREEN_WIDTH);
	private String browserName = System.getProperty(SystemParams.BROWSER_NAME);
	private String tracerStatus = System.getProperty(SystemParams.TRACER_STATUS);
	private String email = System.getProperty(SystemParams.EMAIL);
	private String apiKey = System.getProperty(SystemParams.API_KEY);
	private String proxyUrl = System.getProperty(SystemParams.PROXY_URL);
	private String proxyForSReport = System.getProperty(SystemParams.PROXY_SREPORT_URL);
	private String alwaysCaptureScreenshot = System.getProperty(SystemParams.ALWAYS_CAPTURE_SCREENSHOT);
	private String videoRecorderAlwaysFlag = System.getProperty(SystemParams.ALWAYS_RECORD_VIDEO_OF_TESTCASES);
	private String videoRecorderOnlyIfTestCaseFailed = System.getProperty(SystemParams.RECORD_VIDEO_OF_FAILED_TESTCASES);
	private String captureScreenshotOnFailedEvent = System.getProperty(SystemParams.CAPTURE_SCREENSHOT_ON_FAILED_EVENT);
	private String executedFromCommandPrompt = System.getProperty(SystemParams.EXECUTED_FROM_COMMAND_PROMPT);

	private CommandLineParamsUtils() {
	}

	public static CommandLineParamsUtils getInstance() {
		return frameworkUtils;
	}
	
	public boolean getTracerStatus() {
		if (StringUtils.isBlank(tracerStatus)) {
			return false;
		} else {
			boolean status = false;
			try {
				status = Boolean.parseBoolean(tracerStatus);
				return status;
			} catch (Exception e) {
				log.log(Level.SEVERE, "tracerStatus value is not correct. SWITCHING IT OFF by Default.");
				return status;
			}
		}
	}

	public String getProxyUrl() {
		if (StringUtils.isBlank(proxyUrl)) {
			proxyUrl = "";
		}
		return proxyUrl;
	}

	public String getProxyForSReport() {
		if (StringUtils.isBlank(proxyForSReport)) {
			proxyForSReport = "";
		}
		return proxyForSReport;
	}

	public String getAlwaysCaptureScreenshot() {
		if (StringUtils.isBlank(alwaysCaptureScreenshot)) {
			alwaysCaptureScreenshot = "";
		}
		return alwaysCaptureScreenshot;
	}
	
	public String getVideoRecorderAlwaysFlag(){
		if (StringUtils.isBlank(videoRecorderAlwaysFlag)){
			videoRecorderAlwaysFlag = "";
		}
		return videoRecorderAlwaysFlag;
	}
	
	public String getVideoRecorderOnlyIfTestCaseFailed(){
		if (StringUtils.isBlank(videoRecorderOnlyIfTestCaseFailed)){
			videoRecorderOnlyIfTestCaseFailed = "";
		}
		return videoRecorderOnlyIfTestCaseFailed;
	}

	public String getCaptureScreenshotOnFailedEvent() {
		if (StringUtils.isBlank(captureScreenshotOnFailedEvent)) {
			captureScreenshotOnFailedEvent = "";
		}
		return captureScreenshotOnFailedEvent;
	}

	public String getExecutedFromCommandPrompt() {
		if (StringUtils.isBlank(executedFromCommandPrompt)) {
			executedFromCommandPrompt = "";
		}
		return executedFromCommandPrompt;
	}

	public int getSizeOfTestSuitesToProcess() {
		if (StringUtils.isNotBlank(this.getTestSuiteName())) {
			sizeOfTestSuitesToProcess = 1;
		}
		return sizeOfTestSuitesToProcess;
	}

	public void setSizeOfTestSuitesToProcess(int sizeOfTestSuitesToProcess) {
		this.sizeOfTestSuitesToProcess = sizeOfTestSuitesToProcess;
	}

	public String getScreenshotUploadLocation() {
		if (StringUtils.isBlank(getReportCollectionServerName())) {
			throw new MandatoryParameterException("TEST INITIALIZATION FAILED : " + SystemParams.TEST_REPORTING_APP_SERVER_NAME + " Parameter is mandatory.");
		}
		if (this.screenshotUploadLocation.startsWith("http")) {
			return this.screenshotUploadLocation;
		} else {
			this.screenshotUploadLocation = getReportCollectionServerName() + this.screenshotUploadLocation;
		}
		return screenshotUploadLocation;
	}

	public String getStartSiteVersionURL() {
		if (StringUtils.isBlank(getReportCollectionServerName())) {
			throw new MandatoryParameterException("TEST INITIALIZATION FAILED : " + SystemParams.TEST_REPORTING_APP_SERVER_NAME + " Parameter is mandatory.");
		}
		if (this.startSiteVersionURL.startsWith("http")) {
			return this.startSiteVersionURL;
		} else {
			this.startSiteVersionURL = getReportCollectionServerName() + this.startSiteVersionURL;
		}
		return startSiteVersionURL;
	}

	public String getFinishSiteVersionURL() {
		if (StringUtils.isBlank(getReportCollectionServerName())) {
			throw new MandatoryParameterException("TEST INITIALIZATION FAILED : " + SystemParams.TEST_REPORTING_APP_SERVER_NAME + " Parameter is mandatory.");
		}
		if (this.finishSiteVersionURL.startsWith("http")) {
			return this.finishSiteVersionURL;
		} else {
			this.finishSiteVersionURL = getReportCollectionServerName() + this.finishSiteVersionURL;
		}
		return finishSiteVersionURL;
	}

	public String getStartSuiteVersionURL() {
		if (StringUtils.isBlank(getReportCollectionServerName())) {
			throw new MandatoryParameterException("TEST INITIALIZATION FAILED : " + SystemParams.TEST_REPORTING_APP_SERVER_NAME + " Parameter is mandatory.");
		}
		if (this.startSuiteVersionURL.startsWith("http")) {
			return this.startSuiteVersionURL;
		} else {
			this.startSuiteVersionURL = getReportCollectionServerName() + this.startSuiteVersionURL;
		}
		return startSuiteVersionURL;
	}

	public String getFinishSuiteVersionURL() {
		if (StringUtils.isBlank(getReportCollectionServerName())) {
			throw new MandatoryParameterException("TEST INITIALIZATION FAILED : " + SystemParams.TEST_REPORTING_APP_SERVER_NAME + " Parameter is mandatory.");
		}
		if (this.finishSuiteVersionURL.startsWith("http")) {
			return this.finishSuiteVersionURL;
		} else {
			this.finishSuiteVersionURL = getReportCollectionServerName() + this.finishSuiteVersionURL;
		}
		return finishSuiteVersionURL;
	}

	public String getStartTestCaseURL() {
		if (StringUtils.isBlank(getReportCollectionServerName())) {
			throw new MandatoryParameterException("TEST INITIALIZATION FAILED : " + SystemParams.TEST_REPORTING_APP_SERVER_NAME + " Parameter is mandatory.");
		}
		if (this.startTestCaseURL.startsWith("http")) {
			return this.startTestCaseURL;
		} else {
			this.startTestCaseURL = getReportCollectionServerName() + this.startTestCaseURL;
		}
		return startTestCaseURL;
	}

	public String getFinishTestCaseURL() {
		if (StringUtils.isBlank(getReportCollectionServerName())) {
			throw new MandatoryParameterException("TEST INITIALIZATION FAILED : " + SystemParams.TEST_REPORTING_APP_SERVER_NAME + " Parameter is mandatory.");
		}
		if (this.finishTestCaseURL.startsWith("http")) {
			return this.finishTestCaseURL;
		} else {
			this.finishTestCaseURL = getReportCollectionServerName() + this.finishTestCaseURL;
		}
		return finishTestCaseURL;
	}

	public String getPushTestStepURL() {
		if (StringUtils.isBlank(getReportCollectionServerName())) {
			throw new MandatoryParameterException("TEST INITIALIZATION FAILED : " + SystemParams.TEST_REPORTING_APP_SERVER_NAME + " Parameter is mandatory.");
		}
		if (this.pushTestStepURL.startsWith("http")) {
			return this.pushTestStepURL;
		} else {
			this.pushTestStepURL = getReportCollectionServerName() + this.pushTestStepURL;
		}
		return pushTestStepURL;
	}

	public String getBrowserName() {
		if (StringUtils.isBlank(browserName)) {
			browserName = FrameworkParams.BROWSER_FIREFOX;
		}
		return browserName;
	}

	public String getVersionNumber() {
		if (StringUtils.isBlank(versionNumber)) {
			throw new MandatoryParameterException("TEST INITIALIZATION FAILED : " + SystemParams.VERSION_NUMBER + " Parameter is mandatory.");
		}
		return versionNumber;
	}

	public String getTestReportLocation() {
		if (StringUtils.isBlank(getReportCollectionServerName())) {
			throw new MandatoryParameterException("TEST INITIALIZATION FAILED : " + SystemParams.TEST_REPORTING_APP_SERVER_NAME + " Parameter is mandatory.");
		}
		if (this.testReportLocation.startsWith("http")) {
			return this.testReportLocation;
		} else {
			this.testReportLocation = getReportCollectionServerName() + this.testReportLocation;
		}
		return testReportLocation;
	}

	public String getTestSuiteName() {
		return testSuiteName;
	}

//	public String getEnvironment() {
//		if (StringUtils.isBlank(environment)) {
//			environment = getSiteName();
//		}
//		return environment;
//	}

	public String getScreenHeightInPixel() {
		return screenHeightInPixel;
	}

	public String getScreenWidthInPixel() {
		return screenWidthInPixel;
	}

	public String getSiteName() {
		if (StringUtils.isBlank(siteName)) {
			siteName = "NOT-SPECIFIED";
		} else if (siteName.contains("@")) {
			throw new MandatoryParameterException("TEST INITIALIZATION FAILED : " + SystemParams.TEST_SITE_NAME + " can not have \"@\" symbol.");
		}
		return siteName;
	}

	public String getBuildVersion() {
		String buildVersion = dateFormat.format(new Date());
		if (StringUtils.isNotBlank(this.getBrowserName())) {
			if (this.getBrowserName().equals(FrameworkParams.BROWSER_FIREFOX)) {
				buildVersion = "FF-" + buildVersion;
			} else if (this.getBrowserName().equals(FrameworkParams.BROWSER_CHROME)) {
				buildVersion = "CH-" + buildVersion;
			} else if (this.getBrowserName().equals(FrameworkParams.BROWSER_HEADLESS)) {
				buildVersion = "HL-" + buildVersion;
			}
		}
		return buildVersion;
	}

	public String getEmail() {
		if (StringUtils.isBlank(email)) {
			throw new MandatoryParameterException("TEST INITIALIZATION FAILED : " + SystemParams.EMAIL + " Parameter is mandatory.");
		}
		return email;
	}
	
	public String getOperatingSystem() {
		if (StringUtils.isBlank(operatingSystem)) {
			throw new MandatoryParameterException("TEST INITIALIZATION FAILED : " + SystemParams.OPERATING_SYSTEM + " Parameter is mandatory.");
		}
		return operatingSystem;
	}

	public String getApiKey() {
		if (StringUtils.isBlank(apiKey)) {
			throw new MandatoryParameterException("TEST INITIALIZATION FAILED : " + SystemParams.API_KEY + " Parameter is mandatory.");
		}
		return apiKey;
	}

	public String getReportCollectionServerName() {
		if (StringUtils.isBlank(reportCollectionServerName)) {
			throw new MandatoryParameterException("REPORT COLLECTOR SERVER NAME : " + SystemParams.TEST_REPORTING_APP_SERVER_NAME + " Parameter is mandatory.");
		}
		return reportCollectionServerName;
	}

	public void setReportCollectionServerName(String reportCollectionServerName) {
		System.setProperty(SystemParams.TEST_REPORTING_APP_SERVER_NAME, reportCollectionServerName);
		this.reportCollectionServerName = reportCollectionServerName;
	}

//	public void setEnvironment(String environment) {
//		System.setProperty(SystemParams.TEST_ENVIRONMENT, environment);
//		this.environment = environment;
//	}

	public void setSiteName(String siteName) {
		System.setProperty(SystemParams.TEST_SITE_NAME, siteName);
		this.siteName = siteName;
	}

	public void setVersionNumber(String versionNumber) {
		System.setProperty(SystemParams.VERSION_NUMBER, versionNumber);
		this.versionNumber = versionNumber;
	}

	public void setTestSuiteName(String testSuiteName) {
		System.setProperty(SystemParams.TEST_SUITE_NAME, testSuiteName);
		this.testSuiteName = testSuiteName;
	}
	
	public void setScreenHeightInPixel(String screenHeightInPixel) {
		System.setProperty(SystemParams.SCREEN_HEIGHT, screenHeightInPixel);
		this.screenHeightInPixel = screenHeightInPixel;
	}
	
	public void setScreenWidthInPixel(String screenWidthInPixel) {
		System.setProperty(SystemParams.SCREEN_WIDTH, screenWidthInPixel);
		this.screenWidthInPixel = screenWidthInPixel;
	}

	public void setBrowserName(String browserName) {
		System.setProperty(SystemParams.BROWSER_NAME, browserName);
		this.browserName = browserName;
	}

	public void setProxyUrl(String proxyUrl) {
		System.setProperty(SystemParams.PROXY_URL, proxyUrl);
		this.proxyUrl = proxyUrl;
	}

	public void setProxyForSReport(String sReportProxy) {
		System.setProperty(SystemParams.PROXY_SREPORT_URL, sReportProxy);
		this.proxyForSReport = sReportProxy;
	}

	public void setAlwaysCaptureScreenshot(String alwaysCaptureScreenshot) {
		System.setProperty(SystemParams.ALWAYS_CAPTURE_SCREENSHOT, alwaysCaptureScreenshot);
		this.alwaysCaptureScreenshot = alwaysCaptureScreenshot;
	}

	public void setCaptureScreenshotOnFailedEvent(String captureScreenshotOnFailedEvent) {
		System.getProperty(SystemParams.CAPTURE_SCREENSHOT_ON_FAILED_EVENT, captureScreenshotOnFailedEvent);
		this.captureScreenshotOnFailedEvent = captureScreenshotOnFailedEvent;
	}

	public void setTracerStatus(String tracerStatus) {
		this.tracerStatus = tracerStatus;
	}

	public void setEmail(String email) {
		System.setProperty(SystemParams.EMAIL, email);
		this.email = email;
	}
	
	public void setOperatingSystem(String operatingSystem) {
		System.setProperty(SystemParams.OPERATING_SYSTEM, operatingSystem);
		this.operatingSystem = operatingSystem;
	}

	public void setApiKey(String apiKey) {
		System.setProperty(SystemParams.API_KEY, apiKey);
		this.apiKey = apiKey;
	}

	public void setVideoRecorderAlwaysFlag(String videoRecorderAlwaysFlag) {
		System.setProperty(SystemParams.ALWAYS_RECORD_VIDEO_OF_TESTCASES, videoRecorderAlwaysFlag);
		this.videoRecorderAlwaysFlag = videoRecorderAlwaysFlag;
	}
	
	public void setVideoRecorderOnlyIfTestCaseFailed(String videoRecorderOnlyIfTestCaseFailed) {
		System.setProperty(SystemParams.RECORD_VIDEO_OF_FAILED_TESTCASES, videoRecorderOnlyIfTestCaseFailed);
		this.videoRecorderOnlyIfTestCaseFailed = videoRecorderOnlyIfTestCaseFailed;
	}
	

}
