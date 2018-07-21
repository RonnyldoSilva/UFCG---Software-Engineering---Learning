/**
 * Copyright (c) Jan 11, 2017 Systematic Testing Ltd. (www.systematictesting.com) to Present.
 * All rights reserved. 
 */
package com.systematictesting.automation.core.utils;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.httpclient.HostConfiguration;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.lang3.StringUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.json.JSONException;
import org.json.JSONObject;

import com.systematictesting.automation.core.bean.TestCaseDetails;
import com.systematictesting.automation.core.bean.TestStepDetails;
import com.systematictesting.automation.core.constants.FrameworkParams;
import com.systematictesting.automation.core.constants.SReportReqParams;
import com.systematictesting.automation.core.reporting.TestReportUtils;
import com.systematictesting.qdos.beans.QdosReportStartVersion;
 
public class SReportCommsUtils {
	private static final Logger log = Logger.getLogger(SReportCommsUtils.class.getName());
	private static final String TIME_FORMAT = "dd-MMM-yyyy HH:mm:ss";

	private static long calculateTimeInSeconds(long startTimeInMilliseconds, long endTimeInMilliseconds) {
		long timeInSeconds = endTimeInMilliseconds - startTimeInMilliseconds;
		float time = (float) timeInSeconds;
		float result = time / 1000;
		return (long)result;
	}
	
	public static boolean finishSiteVersion(String versionNumber, Long siteDurationStartTime) {
		String finishSiteVersionURL = CommandLineParamsUtils.getInstance().getFinishSiteVersionURL();
		String email = CommandLineParamsUtils.getInstance().getEmail();
		String activeAPIkey = CommandLineParamsUtils.getInstance().getApiKey();
		String siteName = CommandLineParamsUtils.getInstance().getSiteName();
		String browser = CommandLineParamsUtils.getInstance().getBrowserName();
		String currentTime = TestReportUtils.now(TIME_FORMAT);
		String siteDuration = (calculateTimeInSeconds(siteDurationStartTime, System.currentTimeMillis()))+"";
		
		log.log(Level.INFO, "finishSiteVersion() :: Finishing Site Version with parameters : "
				+ "\n\t email = "+email
				+"\n\t activeAPIkey = "+activeAPIkey
				+"\n\t siteName = "+siteName
				+"\n\t browser = "+browser
				+"\n\t startTime = "+currentTime);
		
		PostMethod post = new PostMethod(finishSiteVersionURL);
		try {
			HttpClient client = new HttpClient();
			
			addProxyForSReportApp(client);
			
			post.addRequestHeader(FrameworkParams.HEADER_KEY_USER_AGENT, FrameworkParams.USER_AGENT);
			post.addParameter(SReportReqParams.EMAIL, email);
			post.addParameter(SReportReqParams.ACTIVE_API_KEY, activeAPIkey);
			post.addParameter(SReportReqParams.SITE_NAME, siteName);
			post.addParameter(SReportReqParams.BROWSER, browser);
			post.addParameter(SReportReqParams.VERSION_NUMBER, versionNumber);
			post.addParameter(SReportReqParams.END_TIME, currentTime);
			post.addParameter(SReportReqParams.SITE_DURATION, siteDuration);
			
			
			int status = client.executeMethod(post);
			String responseBody = post.getResponseBodyAsString();
			log.log(Level.INFO, "Sending 'POST' request to URL = " + finishSiteVersionURL);
			log.log(Level.INFO, "POST Parameters = " + post.getRequestEntity());
			log.log(Level.INFO, "RESPONSE CODE = " + status);
			log.log(Level.INFO, "RESPONSE = " + responseBody);
			if (responseBody!=null){
				JSONObject jsonResponse = new JSONObject(responseBody);
				if (status == 200 && jsonResponse.getString("status").equals("200")) {
					return true;
				}
			}
		} catch (MalformedURLException e) {
			log.log(Level.SEVERE, "URL is not correct for validating email and apikey on Server.", e);
		} catch (IOException e) {
			log.log(Level.SEVERE, "Exception occured while opening connection with Reporting Server for validating email and apikey on Server.", e);
		} catch (JSONException e) {
			log.log(Level.SEVERE, "No JSON Response received from Server", e);
			e.printStackTrace();
		} finally {
			post.releaseConnection();
		}
		return false;
	}

	private static void addProxyForSReportApp(HttpClient client) {
		if (StringUtils.isNotBlank(CommandLineParamsUtils.getInstance().getProxyForSReport())){
			HostConfiguration hostConfiguration = client.getHostConfiguration();
			String proxyString = CommandLineParamsUtils.getInstance().getProxyForSReport();
			String proxyHost = proxyString.substring(0, proxyString.indexOf(":"));
			String proxyPort = proxyString.substring(proxyString.indexOf(":")+1);
			hostConfiguration.setProxy(proxyHost, Integer.parseInt(proxyPort));
			client.setHostConfiguration(hostConfiguration);
		}
	}
	
	public static QdosReportStartVersion startSiteVersion() {
		String startSiteVersionURL = CommandLineParamsUtils.getInstance().getStartSiteVersionURL();
		String email = CommandLineParamsUtils.getInstance().getEmail();
		String activeAPIkey = CommandLineParamsUtils.getInstance().getApiKey();
		String siteName = CommandLineParamsUtils.getInstance().getSiteName();
		String operatingSystem = CommandLineParamsUtils.getInstance().getOperatingSystem();
		String browser = CommandLineParamsUtils.getInstance().getBrowserName();
		String startTime = TestReportUtils.now(TIME_FORMAT);
		String catalogVersion = CommandLineParamsUtils.getInstance().getVersionNumber();
		
		log.log(Level.INFO, "startSiteVersion() :: Starting Site Version with parameters : "
				+ "\n\t email = "+email
				+"\n\t activeAPIkey = "+activeAPIkey
				+"\n\t siteName = "+siteName
				+"\n\t operatingSystem = "+operatingSystem
				+"\n\t browser = "+browser
				+"\n\t startTime = "+startTime
				+"\n\t catalogVersion = "+catalogVersion);
		
		PostMethod post = new PostMethod(startSiteVersionURL);
		try {
			HttpClient client = new HttpClient();
			addProxyForSReportApp(client);
			post.addRequestHeader(FrameworkParams.HEADER_KEY_USER_AGENT, FrameworkParams.USER_AGENT);
			post.addParameter(SReportReqParams.EMAIL, email);
			post.addParameter(SReportReqParams.ACTIVE_API_KEY, activeAPIkey);
			post.addParameter(SReportReqParams.SITE_NAME, siteName);
			post.addParameter(SReportReqParams.OPERATING_SYSTEM, operatingSystem);
			post.addParameter(SReportReqParams.BROWSER, browser);
			post.addParameter(SReportReqParams.START_TIME, startTime);
			post.addParameter(SReportReqParams.CATALOG_VERSION, catalogVersion);
			
			int status = client.executeMethod(post);
			String responseBody = post.getResponseBodyAsString();
			log.log(Level.INFO, "Sending 'POST' request to URL = " + startSiteVersionURL);
			log.log(Level.INFO, "POST Parameters = " + post.getRequestEntity());
			log.log(Level.INFO, "RESPONSE CODE = " + status);
			log.log(Level.INFO, "RESPONSE = " + responseBody);
			if (responseBody!=null){
				ObjectMapper mapper = new ObjectMapper();
				QdosReportStartVersion objQdosReportStartVersion = mapper.readValue(responseBody, QdosReportStartVersion.class);
				return objQdosReportStartVersion;
			}
		} catch (MalformedURLException e) {
			log.log(Level.SEVERE, "URL is not correct for validating email and apikey on Server.", e);
		} catch (IOException e) {
			log.log(Level.SEVERE, "Exception occured while opening connection with Reporting Server for validating email and apikey on Server.", e);
		} finally {
			post.releaseConnection();
		}
		return null;
	}
	
	public static boolean startTestSuite(String versionNumber, String testSuiteName, Long suiteDurationStartTime, Long siteDurationStartTime) {
		log.log(Level.INFO,"startTestSuite() :: "
				+ "\n\t testSuiteName = "+testSuiteName
				+ "\n\t versionNumber = "+versionNumber);
		return updateTestSuite(true, versionNumber, testSuiteName, suiteDurationStartTime, siteDurationStartTime);
	}
	
	public static boolean finishTestSuite(String versionNumber, String testSuiteName, Long suiteDurationStartTime, Long siteDurationStartTime) {
		log.log(Level.INFO,"finishTestSuite() :: "
				+ "\n\t testSuiteName = "+testSuiteName
				+ "\n\t versionNumber = "+versionNumber);
		return updateTestSuite(false, versionNumber, testSuiteName, suiteDurationStartTime, siteDurationStartTime);
	}
	

	public static boolean updateTestSuite(boolean isNew, String versionNumber, String testSuiteName, Long suiteDurationStartTime, Long siteDurationStartTime) {
		String updateSuiteVersionURL = null;
		if (isNew){
			updateSuiteVersionURL = CommandLineParamsUtils.getInstance().getStartSuiteVersionURL();
		} else {
			updateSuiteVersionURL = CommandLineParamsUtils.getInstance().getFinishSuiteVersionURL();
		}
		String email = CommandLineParamsUtils.getInstance().getEmail();
		String activeAPIkey = CommandLineParamsUtils.getInstance().getApiKey();
		String siteName = CommandLineParamsUtils.getInstance().getSiteName();
		String browser = CommandLineParamsUtils.getInstance().getBrowserName();
		String currentTime = TestReportUtils.now(TIME_FORMAT);
		String suiteDuration = (calculateTimeInSeconds(suiteDurationStartTime, System.currentTimeMillis()))+"";
		String siteDuration = (calculateTimeInSeconds(siteDurationStartTime, System.currentTimeMillis()))+"";
		
		log.log(Level.INFO, "Updating Suite Version with parameters : "
				+ "\n\t email = "+email
				+"\n\t activeAPIkey = "+activeAPIkey
				+"\n\t siteName = "+siteName
				+"\n\t browser = "+browser
				+"\n\t startTime = "+currentTime
				+"\n\t suiteDuration = "+suiteDuration
				+"\n\t siteDuration = "+siteDuration
				+"\n\t testSuiteName = "+testSuiteName
				+"\n\t versionNumber = "+versionNumber);
		
		PostMethod post = new PostMethod(updateSuiteVersionURL);
		try {
			HttpClient client = new HttpClient();
			addProxyForSReportApp(client);
			post.addRequestHeader(FrameworkParams.HEADER_KEY_USER_AGENT, FrameworkParams.USER_AGENT);
			post.addParameter(SReportReqParams.EMAIL, email);
			post.addParameter(SReportReqParams.ACTIVE_API_KEY, activeAPIkey);
			post.addParameter(SReportReqParams.SITE_NAME, siteName);
			post.addParameter(SReportReqParams.VERSION_NUMBER, versionNumber);
			post.addParameter(SReportReqParams.TEST_SUITE_NAME, testSuiteName);
			post.addParameter(SReportReqParams.SUITE_DURATION, suiteDuration);
			post.addParameter(SReportReqParams.SITE_DURATION, siteDuration);
			
			if (isNew){
				post.addParameter(SReportReqParams.BROWSER, browser);
				post.addParameter(SReportReqParams.START_TIME, currentTime);	
			} else {
				post.addParameter(SReportReqParams.END_TIME, currentTime);
			}
			
			int status = client.executeMethod(post);
			String responseBody = post.getResponseBodyAsString();
			log.log(Level.INFO, "Sending 'POST' request to URL = " + updateSuiteVersionURL);
			log.log(Level.INFO, "POST Parameters = " + post.getRequestEntity());
			log.log(Level.INFO, "RESPONSE CODE = " + status);
			log.log(Level.INFO, "RESPONSE = " + responseBody);
			if (responseBody!=null){
				JSONObject jsonResponse = new JSONObject(responseBody);
				if (status == 200 && jsonResponse.getString("status").equals("200")) {
					return true;
				}
			}
		} catch (MalformedURLException e) {
			log.log(Level.SEVERE, "URL is not correct for validating email and apikey on Server.", e);
		} catch (IOException e) {
			log.log(Level.SEVERE, "Exception occured while opening connection with Reporting Server for validating email and apikey on Server.", e);
		} catch (JSONException e) {
			log.log(Level.SEVERE, "No JSON Response received from Server", e);
			e.printStackTrace();
		} finally {
			post.releaseConnection();
		}
		return false;
		
	}

	public static boolean startTestCase(String versionNumber, String testSuiteName, Long suiteDurationStartTime, Long siteDurationStartTime, Long testCaseDurationStartTime, TestCaseDetails objTestCaseDetails) {
		log.log(Level.INFO,"startTestCase() :: "
				+ "\n\t testSuiteName = "+testSuiteName
				+ "\n\t objTestCaseDetails = "+objTestCaseDetails);
		return updateTestCase(true,versionNumber, testSuiteName, suiteDurationStartTime, siteDurationStartTime, testCaseDurationStartTime, objTestCaseDetails);
	}
	
	public static boolean finishTestCase(String versionNumber, String testSuiteName, Long suiteDurationStartTime, Long siteDurationStartTime, Long testCaseDurationStartTime, TestCaseDetails objTestCaseDetails) {
		log.log(Level.INFO,"finishTestCase() :: "
				+ "\n\t testSuiteName = "+testSuiteName
				+ "\n\t objTestCaseDetails = "+objTestCaseDetails);
		return updateTestCase(false,versionNumber, testSuiteName, suiteDurationStartTime, siteDurationStartTime, testCaseDurationStartTime, objTestCaseDetails);
	}
	
	public static boolean updateTestCase(boolean isNew, String versionNumber, String testSuiteName, Long suiteDurationStartTime, Long siteDurationStartTime, Long testCaseDurationStartTime, TestCaseDetails objTestCaseDetails) {
		String updateTestCaseURL = null;
		if (isNew){
			updateTestCaseURL = CommandLineParamsUtils.getInstance().getStartTestCaseURL();
		} else {
			updateTestCaseURL = CommandLineParamsUtils.getInstance().getFinishTestCaseURL();
		}
		String email = CommandLineParamsUtils.getInstance().getEmail();
		String activeAPIkey = CommandLineParamsUtils.getInstance().getApiKey();
		String siteName = CommandLineParamsUtils.getInstance().getSiteName();
		String suiteDuration = (calculateTimeInSeconds(suiteDurationStartTime, System.currentTimeMillis()))+"";
		String siteDuration = (calculateTimeInSeconds(siteDurationStartTime, System.currentTimeMillis()))+"";
		Long testCaseDuration = calculateTimeInSeconds(testCaseDurationStartTime, System.currentTimeMillis());
		
		PostMethod post = new PostMethod(updateTestCaseURL);
		try {
			JSONObject jsonTCD = new JSONObject();
			jsonTCD.put("testCaseId", objTestCaseDetails.getTestCaseId());
			jsonTCD.put("testCaseName", objTestCaseDetails.getTestCaseName());
			jsonTCD.put("startTime", objTestCaseDetails.getStartTime());
			jsonTCD.put("duration", testCaseDuration);
			jsonTCD.put("statusClass", objTestCaseDetails.getStatusClass());
			if (!isNew){
				jsonTCD.put("status", objTestCaseDetails.getStatus());
				jsonTCD.put("endTime", objTestCaseDetails.getEndTime());
				jsonTCD.put("videoFile", objTestCaseDetails.getVideoFile()==null?"DISABLED":objTestCaseDetails.getVideoFile());
			}
			
			log.log(Level.INFO, "Updating Test Case with parameters : "+
					"\n\t email = "+email+
					"\n\t activeAPIkey = "+activeAPIkey+
					"\n\t siteName = "+siteName+
					"\n\t suiteDuration = "+suiteDuration+
					"\n\t siteDuration = "+siteDuration+
					"\n\t testSuiteName = "+testSuiteName+
					"\n\t versionNumber = "+versionNumber+
					"\n\t "+SReportReqParams.TEST_CASE_DATA+" = "+jsonTCD);
			
			HttpClient client = new HttpClient();
			addProxyForSReportApp(client);
			post.addRequestHeader(FrameworkParams.HEADER_KEY_USER_AGENT, FrameworkParams.USER_AGENT);
			post.addParameter(SReportReqParams.EMAIL, email);
			post.addParameter(SReportReqParams.ACTIVE_API_KEY, activeAPIkey);
			post.addParameter(SReportReqParams.SITE_NAME, siteName);
			post.addParameter(SReportReqParams.VERSION_NUMBER, versionNumber);
			post.addParameter(SReportReqParams.TEST_SUITE_NAME, testSuiteName);
			post.addParameter(SReportReqParams.SUITE_DURATION, suiteDuration);
			post.addParameter(SReportReqParams.SITE_DURATION, siteDuration);
			post.addParameter(SReportReqParams.TEST_CASE_DATA, jsonTCD.toString());
			
			int status = client.executeMethod(post);
			String responseBody = post.getResponseBodyAsString();
			log.log(Level.INFO, "Sending 'POST' request to URL = " + updateTestCaseURL);
			log.log(Level.INFO, "POST Parameters = " + post.getRequestEntity());
			log.log(Level.INFO, "RESPONSE CODE = " + status);
			log.log(Level.INFO, "RESPONSE = " + responseBody);
			if (responseBody!=null){
				JSONObject jsonResponse = new JSONObject(responseBody);
				if (status == 200 && jsonResponse.getString("status").equals("200")) {
					return true;
				}
			}
		} catch (MalformedURLException e) {
			log.log(Level.SEVERE, "URL is not correct for validating email and apikey on Server.", e);
		} catch (IOException e) {
			log.log(Level.SEVERE, "Exception occured while opening connection with Reporting Server for validating email and apikey on Server.", e);
		} catch (JSONException e) {
			log.log(Level.SEVERE, "No JSON Response received from Server", e);
			e.printStackTrace();
		} finally {
			post.releaseConnection();
		}
		return false;
		
	}

	public static boolean pushTestStep(String versionNumber, String testSuiteName, Long suiteDurationStartTime, Long siteDurationStartTime, Long testCaseDurationStartTime, String testCaseId, TestStepDetails objTestStepDetails) {
		String pushTestStepURL = CommandLineParamsUtils.getInstance().getPushTestStepURL();
		String email = CommandLineParamsUtils.getInstance().getEmail();
		String activeAPIkey = CommandLineParamsUtils.getInstance().getApiKey();
		String siteName = CommandLineParamsUtils.getInstance().getSiteName();
		String browser = CommandLineParamsUtils.getInstance().getBrowserName();
		String startTime = TestReportUtils.now(TIME_FORMAT);
		String suiteDuration = (calculateTimeInSeconds(suiteDurationStartTime, System.currentTimeMillis()))+"";
		String siteDuration = (calculateTimeInSeconds(siteDurationStartTime, System.currentTimeMillis()))+"";
		String testCaseDuration = calculateTimeInSeconds(testCaseDurationStartTime, System.currentTimeMillis())+"";
		
		PostMethod post = new PostMethod(pushTestStepURL);
		try {
			JSONObject jsonTSD = new JSONObject();
			jsonTSD.put("dataSetId", objTestStepDetails.getDataSetId());
			jsonTSD.put("stepId", objTestStepDetails.getStepId());
			jsonTSD.put("stepDescription", objTestStepDetails.getStepDescription());
			jsonTSD.put("stepKeyword", objTestStepDetails.getStepKeyword());
			jsonTSD.put("stepStatus", objTestStepDetails.getStepStatus());
			jsonTSD.put("proceedOnFail", objTestStepDetails.getProceedOnFail());
			jsonTSD.put("stepStatusClass", objTestStepDetails.getStepStatusClass());
			jsonTSD.put("stepScreenShot", objTestStepDetails.getStepScreenShot());
			jsonTSD.put("systemMessage", objTestStepDetails.getSystemMessage());
			jsonTSD.put("stepPageStats", objTestStepDetails.getStepPageStats());
			jsonTSD.put("duration", calculateTimeInSeconds(objTestStepDetails.getStartTimeInMilliseconds(), objTestStepDetails.getEndTimeInMilliseconds()));
			
			log.log(Level.INFO, "Pusing Test Step with parameters : \n\t email = "+email
					+"\n\t activeAPIkey = "+activeAPIkey
					+"\n\t siteName = "+siteName
					+"\n\t browser = "+browser
					+"\n\t startTime = "+startTime
					+"\n\t suiteDuration = "+suiteDuration
					+"\n\t siteDuration = "+siteDuration
					+"\n\t suiteName = "+testSuiteName
					+"\n\t versionNumber = "+versionNumber
					+"\n\t testCaseDuration = "+testCaseDuration
					+"\n\t testCaseId = "+testCaseId
					+"\n\t testStepData = "+jsonTSD.toString());
			
			HttpClient client = new HttpClient();
			addProxyForSReportApp(client);
			post.addRequestHeader(FrameworkParams.HEADER_KEY_USER_AGENT, FrameworkParams.USER_AGENT);
			post.addParameter(SReportReqParams.EMAIL, email);
			post.addParameter(SReportReqParams.ACTIVE_API_KEY, activeAPIkey);
			post.addParameter(SReportReqParams.SITE_NAME, siteName);
			post.addParameter(SReportReqParams.VERSION_NUMBER, versionNumber);
			post.addParameter(SReportReqParams.TEST_SUITE_NAME, testSuiteName);
			post.addParameter(SReportReqParams.SUITE_DURATION, suiteDuration);
			post.addParameter(SReportReqParams.SITE_DURATION, siteDuration);
			post.addParameter(SReportReqParams.TEST_CASE_DURATION, testCaseDuration);
			post.addParameter(SReportReqParams.TEST_CASE_ID, testCaseId);
			post.addParameter(SReportReqParams.TEST_STEP_DATA, jsonTSD.toString());
			
			int status = client.executeMethod(post);
			String responseBody = post.getResponseBodyAsString();
			log.log(Level.INFO, "Sending 'POST' request to URL = " + pushTestStepURL);
			log.log(Level.INFO, "POST Parameters = " + post.getRequestEntity());
			log.log(Level.INFO, "RESPONSE CODE = " + status);
			log.log(Level.INFO, "RESPONSE = " + responseBody);
			if (responseBody!=null){
				JSONObject jsonResponse = new JSONObject(responseBody);
				if (status == 200 && jsonResponse.getString("status").equals("200")) {
					return true;
				}
			}
		} catch (MalformedURLException e) {
			log.log(Level.SEVERE, "URL is not correct for validating email and apikey on Server.", e);
		} catch (IOException e) {
			log.log(Level.SEVERE, "Exception occured while opening connection with Reporting Server for validating email and apikey on Server.", e);
		} catch (JSONException e) {
			log.log(Level.SEVERE, "No JSON Response received from Server", e);
			e.printStackTrace();
		} finally {
			post.releaseConnection();
		}
		return false;

	}
}
