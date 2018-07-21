/*
 * Copyright (c) Jan 11, 2017 Systematic Testing Ltd. (www.systematictesting.com) to Present..
 * All rights reserved. 
 */
package com.systematictesting.automation.core.main;

import java.awt.AWTException;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsEnvironment;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.UnhandledAlertException;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.systematictesting.automation.core.bean.TestCaseDetails;
import com.systematictesting.automation.core.bean.TestStepDetails;
import com.systematictesting.automation.core.constants.FrameworkParams;
import com.systematictesting.automation.core.constants.Result;
import com.systematictesting.automation.core.constants.SReportReqParams;
import com.systematictesting.automation.core.constants.SystemParams;
import com.systematictesting.automation.core.framework.Browser;
import com.systematictesting.automation.core.framework.Tracer;
import com.systematictesting.automation.core.framework.impl.SwingLoggingBasedTraceHandler;
import com.systematictesting.automation.core.reporting.Report;
import com.systematictesting.automation.core.reporting.TestReportUtils;
import com.systematictesting.automation.core.utils.CommandLineParamsUtils;
import com.systematictesting.automation.core.utils.DataStructureConverter;
import com.systematictesting.automation.core.utils.GeneralFileUtils;
import com.systematictesting.automation.core.utils.SReportCommsUtils;
import com.systematictesting.media.Format;
import com.systematictesting.media.FormatKeys.MediaType;
import com.systematictesting.media.Registry;
import com.systematictesting.media.VideoFormatKeys;
import com.systematictesting.media.math.Rational;
import com.systematictesting.qdos.beans.AwsCreds;
import com.systematictesting.qdos.beans.QdosReportStartVersion;
import com.systematictesting.qdos.beans.SingleTestSuite;
import com.systematictesting.qdos.beans.TestCaseData;
import com.systematictesting.qdos.beans.TestStepData;
import com.systematictesting.screenrecorder.ScreenRecorder;

public class ActivateFramework implements Framework {

	private static final Logger log = Logger.getLogger(ActivateFramework.class.getName());

	private static final String LOGGGING_TIME_FORMAT = "HH:mm:ss";
	private static final String TEST_CASE_TIME_FORMAT = "dd-MMM-yyyy HH:mm:ss";
	private static final String PROCEED_ON_FAIL_NO = "NO";
	private static final String PROCEED_ON_FAIL_YES = "YES";
	private static final String TEST_CASE_MODE_AUTOMATE = "AUTOMATE";
	private String PACKAGE_NAME_FOR_KEYWORDS = "com.systematictesting.automation.core.keywords.impl";

	private QdosReportStartVersion objQdosReportStartVersion = null;
	private AwsCreds awsCreds = null;
	private AmazonS3 s3client = null;
	private long siteDurationStartTime;
	private long suiteDurationStartTime;
	private long testCaseDurationStartTime;

	private ScreenRecorder screenRecorder;
	private GraphicsConfiguration gc;
	private Format fileFormat;
	private Format screenFormat;
	private Format mouseFormat;

	public static String TextStored = null;// to store and track the text stored
											// variable.
	public static String ParentWindowHandler = null;// window handler for parent
													// window
	public static String ChildWindowHandler = null;// window handler for child
													// window

	public static void main(String[] args) {
		ActivateFramework main = new ActivateFramework();
		main.activateTestingFramework();
	}

	public void activateTestingFramework() {
		startTesting();
		if (this.startSiteVersion() != null) {
			this.setupAWSClient();
			this.setupVideoRecorder();
			this.executeTest();
			this.finishSiteVersion();
		}
		endScript();
	}

	private void setupVideoRecorder() {
		gc = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration();
		fileFormat = new Format(VideoFormatKeys.MediaTypeKey, MediaType.FILE, VideoFormatKeys.MimeTypeKey,
				VideoFormatKeys.MIME_AVI);
		screenFormat = new Format(VideoFormatKeys.MediaTypeKey, MediaType.VIDEO, VideoFormatKeys.EncodingKey,
				VideoFormatKeys.ENCODING_AVI_TECHSMITH_SCREEN_CAPTURE, VideoFormatKeys.CompressorNameKey,
				VideoFormatKeys.ENCODING_AVI_TECHSMITH_SCREEN_CAPTURE, VideoFormatKeys.DepthKey, 24,
				VideoFormatKeys.FrameRateKey, Rational.valueOf(15), VideoFormatKeys.QualityKey, 1.0f,
				VideoFormatKeys.KeyFrameIntervalKey, 15 * 60);
		mouseFormat = new Format(VideoFormatKeys.MediaTypeKey, MediaType.VIDEO, VideoFormatKeys.EncodingKey, "black",
				VideoFormatKeys.FrameRateKey, Rational.valueOf(30));
	}

	private void setupAWSClient() {
		awsCreds = objQdosReportStartVersion.getAwsCredentials();
		if (awsCreds != null) {
			AWSCredentials credentials = new BasicAWSCredentials(awsCreds.getAccessKey(), awsCreds.getSecretKey());
			s3client = AmazonS3ClientBuilder.standard().withRegion(awsCreds.getRegion())
					.withCredentials(new AWSStaticCredentialsProvider(credentials)).build();
		}
	}

	public static void startTesting() {
		String javaTmpLocation = System.getProperty(SystemParams.JAVA_TMP_DIR);
		GeneralFileUtils.extractResourcesToTempFolder(javaTmpLocation);

		log.log(Level.INFO,
				"Browser Name from CommandLineParamsUtils : " + CommandLineParamsUtils.getInstance().getBrowserName());
		log.log(Level.INFO, "Operating System from CommandLineParamsUtils : "
				+ CommandLineParamsUtils.getInstance().getOperatingSystem());
		log.log(Level.INFO, "System Property for " + SystemParams.PATH_WINDOWS_CHROME_DRIVER + " : "
				+ System.getProperty(SystemParams.PATH_WINDOWS_CHROME_DRIVER));
		log.log(Level.INFO, "System Property for " + SystemParams.PATH_MAC_CHROME_DRIVER + " : "
				+ System.getProperty(SystemParams.PATH_MAC_CHROME_DRIVER));

		log.log(Level.INFO, "System Property for " + SystemParams.PATH_WINDOWS_FIREFOX_DRIVER + " : "
				+ System.getProperty(SystemParams.PATH_WINDOWS_FIREFOX_DRIVER));
		log.log(Level.INFO, "System Property for " + SystemParams.PATH_MAC_FIREFOX_DRIVER + " : "
				+ System.getProperty(SystemParams.PATH_MAC_FIREFOX_DRIVER));

		log.log(Level.INFO, "System Property for " + SystemParams.PATH_PHANTOMJS_DRIVER + " : "
				+ System.getProperty(SystemParams.PATH_PHANTOMJS_DRIVER));

		// log.log(Level.INFO, "System Property for
		// "+SystemParams.IE_WINDOWS_DRIVER+" : " +
		// System.getProperty(SystemParams.IE_WINDOWS_DRIVER));

		if (CommandLineParamsUtils.getInstance().getOperatingSystem().equals(FrameworkParams.OS_WINDOWS_7)
				|| CommandLineParamsUtils.getInstance().getOperatingSystem().equals(FrameworkParams.OS_WINDOWS_10)) {
			if (CommandLineParamsUtils.getInstance().getBrowserName().equals(FrameworkParams.BROWSER_CHROME)
					&& StringUtils.isBlank(System.getProperty(SystemParams.PATH_WINDOWS_CHROME_DRIVER))) {
				String chromeDriverPath = javaTmpLocation + File.separator + "SystematicTesting" + File.separator
						+ SystemParams.DRIVERS_DIR + File.separator + SystemParams.FILE_CHROME_DRIVER_OS_WINDOWS;
				log.log(Level.INFO, "CHROME DRIVER PATH : " + chromeDriverPath);
				System.setProperty(SystemParams.PATH_WINDOWS_CHROME_DRIVER, chromeDriverPath);
				log.log(Level.INFO, "CHROME DRIVER PATH AFTER Setting PROPERTY : "
						+ System.getProperty(SystemParams.PATH_WINDOWS_CHROME_DRIVER));
			}
			if (CommandLineParamsUtils.getInstance().getBrowserName().equals(FrameworkParams.BROWSER_FIREFOX)
					&& StringUtils.isBlank(System.getProperty(SystemParams.PATH_WINDOWS_FIREFOX_DRIVER))) {
				String chromeDriverPath = javaTmpLocation + File.separator + "SystematicTesting" + File.separator
						+ SystemParams.DRIVERS_DIR + File.separator + SystemParams.FILE_FIREFOX_DRIVER_OS_WINDOWS;
				log.log(Level.INFO, "CHROME DRIVER PATH : " + chromeDriverPath);
				System.setProperty(SystemParams.PATH_WINDOWS_FIREFOX_DRIVER, chromeDriverPath);
				log.log(Level.INFO, "CHROME DRIVER PATH AFTER Setting PROPERTY : "
						+ System.getProperty(SystemParams.PATH_WINDOWS_FIREFOX_DRIVER));
			}
			if (CommandLineParamsUtils.getInstance().getBrowserName().equals(FrameworkParams.BROWSER_HEADLESS)
					&& StringUtils.isBlank(System.getProperty(SystemParams.PATH_PHANTOMJS_DRIVER))) {
				String phantomJSDriverPath = javaTmpLocation + File.separator + "SystematicTesting" + File.separator
						+ SystemParams.DRIVERS_DIR + File.separator + SystemParams.FILE_PHANTOMJS_DRIVER_OS_WINDOWS;
				log.log(Level.INFO, "PHANTOMJS DRIVER PATH : " + phantomJSDriverPath);
				System.setProperty(SystemParams.PATH_PHANTOMJS_DRIVER, phantomJSDriverPath);
				log.log(Level.INFO, "PHANTOMJS DRIVER PATH AFTER Setting PROPERTY : "
						+ System.getProperty(SystemParams.PATH_PHANTOMJS_DRIVER));
			}
		}

		if (CommandLineParamsUtils.getInstance().getOperatingSystem().equals(FrameworkParams.OS_MACOSX_10_12_1)) {
			if (CommandLineParamsUtils.getInstance().getBrowserName().equals(FrameworkParams.BROWSER_FIREFOX)
					&& StringUtils.isBlank(System.getProperty(SystemParams.PATH_MAC_FIREFOX_DRIVER))) {
				String firefoxDriverPath = javaTmpLocation + File.separator + "SystematicTesting" + File.separator
						+ SystemParams.DRIVERS_DIR + File.separator + SystemParams.FILE_FIREFOX_DRIVER_OS_MAC;
				log.log(Level.INFO, "FIREFOX DRIVER PATH ON MAC : " + firefoxDriverPath);
				System.setProperty(SystemParams.PATH_MAC_FIREFOX_DRIVER, firefoxDriverPath);
				log.log(Level.INFO, "FIREFOX DRIVER PATH ON MAC AFTER Setting PROPERTY : "
						+ System.getProperty(SystemParams.PATH_MAC_FIREFOX_DRIVER));
			}
			if (CommandLineParamsUtils.getInstance().getBrowserName().equals(FrameworkParams.BROWSER_CHROME)
					&& StringUtils.isBlank(System.getProperty(SystemParams.PATH_MAC_CHROME_DRIVER))) {
				String chromeDriverPath = javaTmpLocation + File.separator + "SystematicTesting" + File.separator
						+ SystemParams.DRIVERS_DIR + File.separator + SystemParams.FILE_CHROME_DRIVER_OS_MAC;
				log.log(Level.INFO, "CHROME DRIVER PATH ON MAC : " + chromeDriverPath);
				System.setProperty(SystemParams.PATH_MAC_CHROME_DRIVER, chromeDriverPath);
				log.log(Level.INFO, "CHROME DRIVER PATH ON MAC AFTER Setting PROPERTY : "
						+ System.getProperty(SystemParams.PATH_MAC_CHROME_DRIVER));
			}
			if (CommandLineParamsUtils.getInstance().getBrowserName().equals(FrameworkParams.BROWSER_HEADLESS)
					&& StringUtils.isBlank(System.getProperty(SystemParams.PATH_PHANTOMJS_DRIVER))) {
				String phantomJSDriverPath = javaTmpLocation + File.separator + "SystematicTesting" + File.separator
						+ SystemParams.DRIVERS_DIR + File.separator + SystemParams.FILE_PHANTOMJS_DRIVER_OS_MAC;
				log.log(Level.INFO, "PHANTOMJS DRIVER PATH : " + phantomJSDriverPath);
				System.setProperty(SystemParams.PATH_PHANTOMJS_DRIVER, phantomJSDriverPath);
				log.log(Level.INFO, "PHANTOMJS DRIVER PATH AFTER Setting PROPERTY : "
						+ System.getProperty(SystemParams.PATH_PHANTOMJS_DRIVER));
			}
		}

		// if
		// (CommandLineParamsUtils.getInstance().getBrowserName().equals(FrameworkParams.BROWSER_IE)
		// &&
		// StringUtils.isBlank(System.getProperty(SystemParams.IE_WINDOWS_DRIVER)))
		// {
		// String ieDriverPath = javaTmpLocation + File.separator +
		// "SystematicTesting" + File.separator + SystemParams.DRIVERS_DIR +
		// File.separator + SystemParams.IE_DRIVER_EXE_FILE_NAME;
		// log.log(Level.INFO, "IE DRIVER PATH : " + ieDriverPath);
		// System.setProperty(SystemParams.IE_WINDOWS_DRIVER, ieDriverPath);
		// log.log(Level.INFO, "IE DRIVER PATH AFTER Setting PROPERTY : " +
		// System.getProperty(SystemParams.IE_WINDOWS_DRIVER));
		// }

		log.log(Level.INFO, "*********** Automation Testing Started ***********");
		log.log(Level.INFO, "URL for trace.html = " + Report.class.getResource("tracer.html"));
		if (CommandLineParamsUtils.getInstance().getTracerStatus()) {
			Tracer.getInstance().getDriver().navigate().to(Report.class.getResource("tracer.html"));
		}
		Tracer.getInstance().logEvents(TestReportUtils.now(LOGGGING_TIME_FORMAT) + ":* Automation Testing Started *");
		Tracer.getInstance()
				.logEvents(TestReportUtils.now(LOGGGING_TIME_FORMAT) + ":* Checking Dependencies of libraries : *");

	}

	public QdosReportStartVersion startSiteVersion() {
		siteDurationStartTime = System.currentTimeMillis();
		log.log(Level.INFO, "*********** VALIDATING EMAIL & API KEY ***********");
		Tracer.getInstance()
				.logEvents(TestReportUtils.now(LOGGGING_TIME_FORMAT) + ":-- Validating Email and ApiKey --");
		objQdosReportStartVersion = SReportCommsUtils.startSiteVersion();
		if (objQdosReportStartVersion != null) {
			log.log(Level.INFO, "Email and APIKey validation is passed.");
			Tracer.getInstance()
					.logEvents(TestReportUtils.now(LOGGGING_TIME_FORMAT) + ":-- Validation successfully done. --");
			log.log(Level.INFO, "Site Version started successfully.");
			Tracer.getInstance()
					.logEvents(TestReportUtils.now(LOGGGING_TIME_FORMAT)
							+ ":-- Site Version has started with Version Number = "
							+ objQdosReportStartVersion.getNextQdosVersionNumber());
			log.log(Level.INFO, "*********** WARMING UP SCRIPTS ***********");
		} else {
			Tracer.getInstance().logEvents(TestReportUtils.now(LOGGGING_TIME_FORMAT)
					+ ":-- Validation Failed. Please check email address and API Key. --");
			Tracer.getInstance().logEvents(TestReportUtils.now(LOGGGING_TIME_FORMAT)
					+ ":-- No version number received from SReport Application. Please check your parameters. --");
			log.log(Level.INFO, "No version number received from SReport Application. Please check your parameters.");
		}
		return objQdosReportStartVersion;
	}

	public void executeTest() {
		log.log(Level.INFO, "Singleton Instance Controller is created successfully.");
		String testSuiteName = CommandLineParamsUtils.getInstance().getTestSuiteName();

		log.log(Level.INFO, "======================================");
		log.log(Level.INFO, "Supplied Suite Name : {0}", testSuiteName);
		log.log(Level.INFO, "======================================");
		if (testSuiteName == null || testSuiteName.length() == 0) {
			log.log(Level.INFO, "Test Suite Name is not supplied, therefore running all test suites.");
			int sizeOfTotalReportSuites = objQdosReportStartVersion.getCatalogSuites().size();
			CommandLineParamsUtils.getInstance().setSizeOfTestSuitesToProcess(sizeOfTotalReportSuites);
			for (SingleTestSuite testSuite : objQdosReportStartVersion.getCatalogSuites()) {
				testSuiteName = testSuite.getTestSuite().getSuiteName();
				log.log(Level.INFO, "======================================");
				log.log(Level.INFO, "Running Suite Name : {0}", testSuiteName);
				log.log(Level.INFO, "======================================");
				Tracer.getInstance()
						.logEvents(TestReportUtils.now(LOGGGING_TIME_FORMAT) + ":Running suiteName : " + testSuiteName);
				suiteDurationStartTime = System.currentTimeMillis();
				SReportCommsUtils.startTestSuite(objQdosReportStartVersion.getNextQdosVersionNumber(), testSuiteName,
						suiteDurationStartTime, siteDurationStartTime);
				runTestSuite(testSuite);
				SReportCommsUtils.finishTestSuite(objQdosReportStartVersion.getNextQdosVersionNumber(), testSuiteName,
						suiteDurationStartTime, siteDurationStartTime);
				Tracer.getInstance().logEvents(
						TestReportUtils.now(LOGGGING_TIME_FORMAT) + ":Finishing Test Suite:" + testSuiteName);
			}
		} else {
			CommandLineParamsUtils.getInstance().setSizeOfTestSuitesToProcess(1);
			log.log(Level.INFO, "======================================");
			log.log(Level.INFO, "Running Suite Name : {0}", testSuiteName);
			log.log(Level.INFO, "======================================");
			Tracer.getInstance()
					.logEvents(TestReportUtils.now(LOGGGING_TIME_FORMAT) + ":Running suiteName : " + testSuiteName);
			boolean isTestSuiteAvailable = false;
			for (SingleTestSuite testSuite : objQdosReportStartVersion.getCatalogSuites()) {
				if (testSuite.getTestSuite().getSuiteName().equals(testSuiteName)) {
					isTestSuiteAvailable = true;
					suiteDurationStartTime = System.currentTimeMillis();
					SReportCommsUtils.startTestSuite(objQdosReportStartVersion.getNextQdosVersionNumber(),
							testSuiteName, suiteDurationStartTime, siteDurationStartTime);
					runTestSuite(testSuite);
					SReportCommsUtils.finishTestSuite(objQdosReportStartVersion.getNextQdosVersionNumber(),
							testSuiteName, suiteDurationStartTime, siteDurationStartTime);
					Tracer.getInstance().logEvents(
							TestReportUtils.now(LOGGGING_TIME_FORMAT) + ":Finishing Test Suite:" + testSuiteName);
				}
			}
			if (!isTestSuiteAvailable) {
				Tracer.getInstance().logEvents(TestReportUtils.now(LOGGGING_TIME_FORMAT) + ": Supplied Test Suite :"
						+ testSuiteName + " does not exist in Test Catalog. Terminating all threads.");
			}
		}
	}

	protected void runTestSuite(SingleTestSuite testSuite) {
		Integer summaryPassCount = new Integer(0);
		Integer summaryFailCount = new Integer(0);
		Integer summaryAbortedCount = new Integer(0);
		Integer summaryDisabledCount = new Integer(0);

		for (int tcid = 0; tcid < testSuite.getTestSuite().getTestCaseArray().size(); tcid++) {
			TestCaseData activeTestCase = testSuite.getTestSuite().getTestCaseArray().get(tcid);
			String currentTestID = activeTestCase.getTestCaseId();
			if (StringUtils.isNotBlank(currentTestID)) {
				boolean isTestCaseAborted = false;
				boolean isTestCaseFailed = false;
				log.log(Level.INFO, "Running Test Case : {0}", currentTestID);
				String testCaseMode = activeTestCase.getTestCaseMode();
				log.log(Level.INFO, "RunMode of Test Case : " + currentTestID + " is " + testCaseMode);
				String testCaseIdName = activeTestCase.getTestCaseName();

				TestCaseDetails objTestCaseDetails = new TestCaseDetails();
				objTestCaseDetails.setTestCaseId(currentTestID);
				objTestCaseDetails.setTestCaseName(testCaseIdName);
				objTestCaseDetails.setStatusClass(Result.PASS.toLowerCase());
				objTestCaseDetails.setStartTime(TestReportUtils.now(TEST_CASE_TIME_FORMAT));

				// STARTING TEST CASE START HERE
				testCaseDurationStartTime = System.currentTimeMillis();
				SReportCommsUtils.startTestCase(objQdosReportStartVersion.getNextQdosVersionNumber(),
						testSuite.getTestSuite().getSuiteName(), suiteDurationStartTime, siteDurationStartTime,
						testCaseDurationStartTime, objTestCaseDetails);
				// STARTING TEST CASE END HERE

				Tracer.getInstance().logEvents(
						TestReportUtils.now(LOGGGING_TIME_FORMAT) + ": **" + currentTestID + ":" + testCaseIdName);

				if (testCaseMode.equals(TEST_CASE_MODE_AUTOMATE)) {

					String testCaseVideoFileName = startTestCaseVideoRecording(testSuite, currentTestID);
					objTestCaseDetails.setVideoFile(testCaseVideoFileName);

					boolean isProceedOnFail = true;
					Map<String, HashMap<String, String>> dataSetResult = DataStructureConverter
							.convertTestCaseDataSets(activeTestCase.getDataSets());
					Set<String> dataSetIds = dataSetResult.keySet();
					for (String dataSetId : dataSetIds) {
						for (int tsid = 0; tsid < activeTestCase.getTestStepsData().size(); tsid++) {
							TestStepData testStepFromMainTestCase = activeTestCase.getTestStepsData().get(tsid);
							TestStepData testStep = testStepFromMainTestCase.clone();
							
							testStep.setElementValue(dataSetResult.get(dataSetId).get(testStep.getElementValue()));
							testStep.setDataSetId(dataSetId);
							String currentTSID = testStep.getStepId();
							log.log(Level.INFO, "@@CHECKING : CURRENT TEST ID : " + currentTSID);
							log.log(Level.INFO, "@@CHECKING : CURRENT DATA ROW FOR CURRENT TEST ID : " + dataSetId);
							if (StringUtils.isNotBlank(currentTSID)) {
								String stepDescription = testStep.getStepDescription();
								log.log(Level.INFO, "Test Step ID : " + currentTSID);
								log.log(Level.INFO, "Test Step DESCRIPTION : " + stepDescription);
								Tracer.getInstance().logEvents(TestReportUtils.now(LOGGGING_TIME_FORMAT) + ":-->"
										+ dataSetId + ":-->" + currentTSID + ":" + stepDescription);
								String proceedOnFail = testStep.getProceedOnFail();
								TestStepDetails objTestStepDetails = new TestStepDetails();
								objTestStepDetails.setDataSetId(testStep.getDataSetId());
								objTestStepDetails.setStepId(currentTSID);
								objTestStepDetails.setStepDescription(stepDescription);
								objTestStepDetails.setStepKeyword(testStep.getStepKeyword());
								objTestStepDetails.setProceedOnFail(StringUtils.isBlank(proceedOnFail) || proceedOnFail.equalsIgnoreCase(PROCEED_ON_FAIL_YES) ? PROCEED_ON_FAIL_YES : PROCEED_ON_FAIL_NO);
								
								try {
									Class<?> keywordClass = getClass().getClassLoader().loadClass(getPackageNameForKeywords() + "." + testStep.getStepKeyword());
									com.systematictesting.automation.core.keywords.Process instanceOfProcess = (com.systematictesting.automation.core.keywords.Process) keywordClass.newInstance();
									objTestStepDetails.setStartTimeInMilliseconds(System.currentTimeMillis());
									String result = Result.ABORTED;
									if (isProceedOnFail) {
										log.log(Level.INFO, "\n@@@@@@@@@@@@@@@ SETTING UP PARAMETERS @@@@@@@@@@@@@@@"
												+ "\n\t JAVA CLASS : " + testStep.getStepKeyword() 
												+ "\n\t STEP ELEMENT KEY : " + testStep.getElementKey()
												+ "\n\t STEP ELEMENT VALUE : " + testSuite.getTestSuite().getKeyValuePairs().get(testStep.getElementKey())
												+ "\n\t STEP ELEMENT DATA : " + testStep.getElementValue()
												+ "\n\t STEP ELEMENT TYPE : " + testStep.getElementType());
										result = instanceOfProcess.execute(testSuite.getTestSuite().getKeyValuePairs(),testStep);
										log.log(Level.INFO, "\n@@@@@@@@@@@@@@@ RUNNING RESULT START @@@@@@@@@@@@@@@"
												+ "\n\t KEYWORD : " + testStep.getStepKeyword() 
												+ "\n\t DATA SET ID : " + dataSetId
												+ "\n\t TEST STEP ID : " + currentTSID
												+ "\n\t RESULT : " + result 
												+ "\n@@@@@@@@@@@@@@@ RUNNING RESULT END @@@@@@@@@@@@@@@");
									}
									objTestStepDetails.setEndTimeInMilliseconds(System.currentTimeMillis());

									handleScreenShotsOfSteps(testSuite, objTestCaseDetails, objTestStepDetails, result);

									if (result.startsWith(Result.FAIL)) {
										Tracer.getInstance().logEvents(TestReportUtils.now(LOGGGING_TIME_FORMAT)
												+ ":-->" + dataSetId + ":-->" + currentTSID + ":" + result);
										setTestStepStatus(objTestStepDetails, Result.FAIL);
										if (proceedOnFail.equalsIgnoreCase(PROCEED_ON_FAIL_NO)) {
											isProceedOnFail = false;
										}
										isTestCaseFailed = true;
									} else if (result.startsWith(Result.PASS)) {
										Tracer.getInstance().logEvents(TestReportUtils.now(LOGGGING_TIME_FORMAT)
												+ ":-->" + dataSetId + ":-->" + currentTSID + ":" + Result.PASS);
										setTestStepStatus(objTestStepDetails, Result.PASS);

									} else {
										Tracer.getInstance().logEvents(TestReportUtils.now(LOGGGING_TIME_FORMAT)
												+ ":-->" + dataSetId + ":-->" + currentTSID + ":" + result);
										setTestStepStatus(objTestStepDetails, Result.ABORTED);
										if (isTestCaseFailed == false) {
											isTestCaseAborted = true;
										}

									}

								} catch (UnhandledAlertException e) {
									log.log(Level.INFO,
											"Unhandled Alert Detected. Please manage this alert in next test step. Here is the stack trace details: \n ",
											e);
								} catch (Exception e) {
									e.printStackTrace();
									log.log(Level.SEVERE, "Exception Occured while running test case : \n ", e);
									Tracer.getInstance().logEvents(TestReportUtils.now(LOGGGING_TIME_FORMAT) + ":-->"
											+ dataSetId + ":-->" + currentTSID + ":" + Result.ABORTED);
									Tracer.getInstance()
											.logEvents(TestReportUtils.now(LOGGGING_TIME_FORMAT) + ":-->" + dataSetId
													+ ":-->" + currentTSID + ":" + FrameworkParams.MESSAGE_ERROR_PREFIX
													+ "+" + e.getMessage());
									objTestStepDetails.setStartTimeInMilliseconds(System.currentTimeMillis());
									objTestStepDetails.setEndTimeInMilliseconds(System.currentTimeMillis());
									setTestStepStatus(objTestStepDetails, Result.ABORTED);
									isProceedOnFail = false;
									isTestCaseAborted = true;
									objTestStepDetails.setSystemMessage(
											FrameworkParams.MESSAGE_ERROR_PREFIX + " : " + e.getMessage());
								}
								// PUSH TEST STEP START HERE
								SReportCommsUtils.pushTestStep(objQdosReportStartVersion.getNextQdosVersionNumber(),
										testSuite.getTestSuite().getSuiteName(), suiteDurationStartTime,
										siteDurationStartTime, testCaseDurationStartTime,
										objTestCaseDetails.getTestCaseId(), objTestStepDetails);
								// PUSH TEST STEP END HERE
								log.log(Level.INFO, "-----Complete Test Step Result-----",
										objTestStepDetails.toString());
							}
						}
					}
					// }
					if (isTestCaseAborted) {
						Tracer.getInstance().logEvents(TestReportUtils.now(LOGGGING_TIME_FORMAT) + ":**" + currentTestID
								+ ":" + Result.ABORTED);
						objTestCaseDetails.setStatus(Result.ABORTED);
						objTestCaseDetails.setStatusClass(Result.ABORTED.toLowerCase());
						summaryAbortedCount++;
					} else if (isTestCaseFailed) {
						Tracer.getInstance().logEvents(
								TestReportUtils.now(LOGGGING_TIME_FORMAT) + ":**" + currentTestID + ":" + Result.FAIL);
						objTestCaseDetails.setStatus(Result.FAIL);
						objTestCaseDetails.setStatusClass(Result.FAIL.toLowerCase());
						summaryFailCount++;
					}
					if (!isTestCaseFailed && !isTestCaseAborted) {
						Tracer.getInstance().logEvents(
								TestReportUtils.now(LOGGGING_TIME_FORMAT) + ":**" + currentTestID + ":" + Result.PASS);
						objTestCaseDetails.setStatus(Result.PASS);
						objTestCaseDetails.setStatusClass(Result.PASS.toLowerCase());
						summaryPassCount++;
					}
					stopTestCaseVideoRecording(testSuite, objTestCaseDetails);
				} else {
					Tracer.getInstance().logEvents(
							TestReportUtils.now(LOGGGING_TIME_FORMAT) + ":**" + currentTestID + ":" + Result.MANUAL);
					objTestCaseDetails.setStatus(Result.MANUAL);
					objTestCaseDetails.setStatusClass(Result.MANUAL.toLowerCase());
					summaryDisabledCount++;
				}
				objTestCaseDetails.setEndTime(TestReportUtils.now(TEST_CASE_TIME_FORMAT));
				objTestCaseDetails.setEndTimeInMilliseconds(System.currentTimeMillis());

				// FINISH TEST CASE START HERE
				SReportCommsUtils.finishTestCase(objQdosReportStartVersion.getNextQdosVersionNumber(),
						testSuite.getTestSuite().getSuiteName(), suiteDurationStartTime, siteDurationStartTime,
						testCaseDurationStartTime, objTestCaseDetails);
				// FINISH TEST CASE END HERE
			}
		}
	}

	private void stopTestCaseVideoRecording(SingleTestSuite testSuite, TestCaseDetails objTestCaseDetails) {
		String testSuiteName = testSuite.getTestSuite().getSuiteName();
		if (s3client != null && awsCreds != null) {
			try {
				screenRecorder.stop();
				File destinationDir = screenRecorder.getMovieFolder();
				String fileName = screenRecorder.getVideoFileName();
				File scrFile = new File(destinationDir, fileName);
				String absoluteFileName = awsCreds.getFolder() + FrameworkParams.FILE_SEPARATOR
						+ CommandLineParamsUtils.getInstance().getApiKey() + FrameworkParams.FILE_SEPARATOR
						+ CommandLineParamsUtils.getInstance().getSiteName() + FrameworkParams.FILE_SEPARATOR
						+ objQdosReportStartVersion.getNextQdosVersionNumber() + FrameworkParams.FILE_SEPARATOR
						+ testSuiteName + FrameworkParams.FILE_SEPARATOR + fileName;
				if (CommandLineParamsUtils.getInstance().getVideoRecorderAlwaysFlag().equals("true")
						|| (!objTestCaseDetails.getStatus().equals(Result.PASS) && CommandLineParamsUtils.getInstance()
								.getVideoRecorderOnlyIfTestCaseFailed().equals("true"))) {
					Tracer.getInstance()
							.logEvents(TestReportUtils.now(LOGGGING_TIME_FORMAT) + ":**"
									+ objTestCaseDetails.getTestCaseId() + ": Uploading Video File to Server : "
									+ objTestCaseDetails.getVideoFile());
					log.log(Level.INFO, "NOW, UPLOADING VIDEO FILE : " + objTestCaseDetails.getVideoFile());
					s3client.putObject(new PutObjectRequest(awsCreds.getBucketName(), absoluteFileName, scrFile)
							.withCannedAcl(CannedAccessControlList.PublicRead));
					log.log(Level.INFO,
							"VIDEO FILE : " + objTestCaseDetails.getVideoFile() + " : Uploaded SUCCESSFULLY!");
					scrFile.delete();
				} else {
					scrFile.delete();
					objTestCaseDetails.setVideoFile("DISABLED");
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private String startTestCaseVideoRecording(SingleTestSuite testSuite, String currentTestID) {
		String testSuiteName = testSuite.getTestSuite().getSuiteName();
		String testCaseVideoFileName = currentTestID + "." + Registry.getInstance().getExtension(fileFormat);
		if (s3client != null && awsCreds != null) {
			try {
				String javaTmpLocation = System.getProperty(SystemParams.JAVA_TMP_DIR);
				String testCaseVideoDir = javaTmpLocation + File.separator + "SystematicTesting" + File.separator
						+ "VIDEOS" + File.separator + testSuiteName + File.separator;
				File destPathDirectory = new File(testCaseVideoDir);
				if (!destPathDirectory.isDirectory()) {
					destPathDirectory.mkdirs();
				}
				File testCaseVideoFile = new File(destPathDirectory, testCaseVideoFileName);
				if (testCaseVideoFile.exists()) {
					testCaseVideoFile.delete();
				}
				screenRecorder = new ScreenRecorder(gc, gc.getBounds(), fileFormat, screenFormat, mouseFormat,
						destPathDirectory, testCaseVideoFileName);
				screenRecorder.start();
				String awsAbsoluteFileName = awsCreds.getFolder() + FrameworkParams.FILE_SEPARATOR
						+ CommandLineParamsUtils.getInstance().getApiKey() + FrameworkParams.FILE_SEPARATOR
						+ CommandLineParamsUtils.getInstance().getSiteName() + FrameworkParams.FILE_SEPARATOR
						+ objQdosReportStartVersion.getNextQdosVersionNumber() + FrameworkParams.FILE_SEPARATOR
						+ testSuiteName + FrameworkParams.FILE_SEPARATOR + testCaseVideoFileName;
				return awsCreds.getHostingDomain() + FrameworkParams.FILE_SEPARATOR + awsAbsoluteFileName;
			} catch (IOException | AWTException e) {
				e.printStackTrace();
				return "NOT SUPPORTED";
			}
		} else {
			return "NOT SUPPORTED";
		}
	}

	private void handleScreenShotsOfSteps(SingleTestSuite testSuite, TestCaseDetails objTestCaseDetails,
			TestStepDetails objTestStepDetails, String result) {
		String testSuiteName = testSuite.getTestSuite().getSuiteName();
		boolean takeScreenShot = false;
		if (CommandLineParamsUtils.getInstance().getCaptureScreenshotOnFailedEvent().equals("true")
				&& !result.equals(Result.PASS)) {
			takeScreenShot = true;
		} else if (CommandLineParamsUtils.getInstance().getAlwaysCaptureScreenshot().equals("true")) {
			takeScreenShot = true;
		}

		if (takeScreenShot) {
			String testCaseScreenShotFileName = objTestCaseDetails.getTestCaseId() + "_"
					+ objTestStepDetails.getDataSetId() + "_" + objTestStepDetails.getStepId() + "_"
					+ CommandLineParamsUtils.getInstance().getBuildVersion();
			testCaseScreenShotFileName = testCaseScreenShotFileName.replaceAll("[^\\w\\s\\-_]", "") + ".jpg";
			log.log(Level.INFO, "SCREEN SHOT FILE NAME : " + testCaseScreenShotFileName);
			log.log(Level.INFO, "TAKING SCREEN SHOT NOW with FILE NAME : " + testCaseScreenShotFileName);
			File scrFile = ((TakesScreenshot) Browser.getInstance().getDriver()).getScreenshotAs(OutputType.FILE);

			if (s3client != null && awsCreds != null) {
				String absoluteFileName = awsCreds.getFolder() + FrameworkParams.FILE_SEPARATOR
						+ CommandLineParamsUtils.getInstance().getApiKey() + FrameworkParams.FILE_SEPARATOR
						+ CommandLineParamsUtils.getInstance().getSiteName() + FrameworkParams.FILE_SEPARATOR
						+ objQdosReportStartVersion.getNextQdosVersionNumber() + FrameworkParams.FILE_SEPARATOR
						+ testSuiteName + FrameworkParams.FILE_SEPARATOR + objTestCaseDetails.getTestCaseId()
						+ FrameworkParams.FILE_SEPARATOR + objTestStepDetails.getDataSetId()
						+ FrameworkParams.FILE_SEPARATOR + objTestStepDetails.getStepId()
						+ FrameworkParams.FILE_SEPARATOR + testCaseScreenShotFileName;
				log.log(Level.INFO, "Now, UPLOADING FILE NAME : " + testCaseScreenShotFileName);
				s3client.putObject(new PutObjectRequest(awsCreds.getBucketName(), absoluteFileName, scrFile)
						.withCannedAcl(CannedAccessControlList.PublicRead));
				log.log(Level.INFO,
						"SCREENSHOT FILE NAME : " + testCaseScreenShotFileName + " : Uploaded Successfully!");
				objTestStepDetails.setStepScreenShot(
						awsCreds.getHostingDomain() + FrameworkParams.FILE_SEPARATOR + absoluteFileName);
			} else {
				log.log(Level.INFO, "Now, UPLOADING FILE NAME to Application Server : " + testCaseScreenShotFileName);
				Report.uploadFile(testSuiteName, scrFile, testCaseScreenShotFileName,
						objQdosReportStartVersion.getNextQdosVersionNumber(), objTestStepDetails.getStepId(),
						objTestCaseDetails.getTestCaseId(), objTestStepDetails.getDataSetId());
				log.log(Level.INFO,
						"SCREENSHOT FILE NAME : " + testCaseScreenShotFileName + " : Uploaded Successfully!");
				String screenshotUrl = SReportReqParams.ACTIVE_API_KEY + FrameworkParams.REQ_PARAM_VALUE_SEPARATOR
						+ CommandLineParamsUtils.getInstance().getApiKey() + FrameworkParams.REQ_PARAM_SEPARATOR
						+ SReportReqParams.SITE_NAME + FrameworkParams.REQ_PARAM_VALUE_SEPARATOR
						+ CommandLineParamsUtils.getInstance().getSiteName() + FrameworkParams.REQ_PARAM_SEPARATOR
						+ SReportReqParams.VERSION_NUMBER + FrameworkParams.REQ_PARAM_VALUE_SEPARATOR
						+ objQdosReportStartVersion.getNextQdosVersionNumber() + FrameworkParams.REQ_PARAM_SEPARATOR
						+ SReportReqParams.TEST_SUITE_NAME + FrameworkParams.REQ_PARAM_VALUE_SEPARATOR + testSuiteName
						+ FrameworkParams.REQ_PARAM_SEPARATOR + SReportReqParams.TEST_CASE_ID
						+ FrameworkParams.REQ_PARAM_VALUE_SEPARATOR + objTestCaseDetails.getTestCaseId()
						+ FrameworkParams.REQ_PARAM_SEPARATOR + SReportReqParams.TEST_STEP_DATA_SET_ID
						+ FrameworkParams.REQ_PARAM_VALUE_SEPARATOR + objTestStepDetails.getDataSetId()
						+ FrameworkParams.REQ_PARAM_SEPARATOR + SReportReqParams.TEST_STEP_ID
						+ FrameworkParams.REQ_PARAM_VALUE_SEPARATOR + objTestStepDetails.getStepId()
						+ FrameworkParams.REQ_PARAM_SEPARATOR + SReportReqParams.SCREENSHOT_FILENAME
						+ FrameworkParams.REQ_PARAM_VALUE_SEPARATOR + testCaseScreenShotFileName
						+ FrameworkParams.REQ_PARAM_SEPARATOR + SReportReqParams.SOURCE
						+ FrameworkParams.REQ_PARAM_VALUE_SEPARATOR + SReportReqParams.APP_SERVER;
				objTestStepDetails.setStepScreenShot(screenshotUrl);
			}

		} else {
			objTestStepDetails.setStepScreenShot(FrameworkParams.DISABLED);
		}
	}

	public String getPackageNameForKeywords() {
		return PACKAGE_NAME_FOR_KEYWORDS;
	}

	protected void setTestStepStatus(TestStepDetails objTestStepDetails, String status) {
		if (status.equals(Result.PASS)) {
			objTestStepDetails.setStepStatus(Result.PASS);
			objTestStepDetails.setStepStatusClass(Result.PASS.toLowerCase());
			objTestStepDetails.setSystemMessage(FrameworkParams.MESSAGE_PASS);
		}
		if (status.equals(Result.FAIL)) {
			objTestStepDetails.setStepStatus(Result.FAIL);
			objTestStepDetails.setStepStatusClass(Result.FAIL.toLowerCase());
			objTestStepDetails.setSystemMessage(FrameworkParams.MESSAGE_FAIL);
		}
		if (status.equals(Result.ABORTED)) {
			objTestStepDetails.setStepStatus(Result.ABORTED);
			objTestStepDetails.setStepStatusClass(Result.ABORTED.toLowerCase());
			objTestStepDetails.setSystemMessage(FrameworkParams.MESSAGE_ABORTED);
		}
	}

	public void finishSiteVersion() {
		SReportCommsUtils.finishSiteVersion(objQdosReportStartVersion.getNextQdosVersionNumber(),
				siteDurationStartTime);
	}

	public static void endScript() {
		if (Tracer.getInstance().getTraceHandler() instanceof SwingLoggingBasedTraceHandler) {
			Tracer.getInstance().logEvents(TestReportUtils.now(LOGGGING_TIME_FORMAT) + ":Closing Browser now...");
			Browser.getInstance().resetWebDriver();
		} else {
			Tracer.getInstance()
					.logEvents(TestReportUtils.now(LOGGGING_TIME_FORMAT) + ":Closing All browsers in 20 seconds ...");
			try {
				Thread.sleep(20000);
			} catch (NumberFormatException e) {
				e.printStackTrace();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			Browser.getInstance().resetWebDriver();
			Tracer.getInstance().resetWebDriver();
			if (CommandLineParamsUtils.getInstance().getExecutedFromCommandPrompt().equalsIgnoreCase("true")) {
				log.log(Level.INFO, "Exiting from QDOS Client, as its been executed from command line parameters.");
				System.exit(0);
			}
		}
	}

}
