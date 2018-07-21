/*
 * Copyright (c) Jan 11, 2017 Systematic Testing Ltd. (www.systematictesting.com) to Present..
 * All rights reserved. 
 */
package com.systematictesting.automation.core.reporting;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.config.RequestConfig.Builder;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.util.EntityUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.systematictesting.automation.core.bean.SuiteReport;
import com.systematictesting.automation.core.bean.TestCaseDetails;
import com.systematictesting.automation.core.bean.TestReport;
import com.systematictesting.automation.core.bean.TestStepDetails;
import com.systematictesting.automation.core.constants.FrameworkParams;
import com.systematictesting.automation.core.constants.Result;
import com.systematictesting.automation.core.constants.SReportReqParams;
import com.systematictesting.automation.core.constants.SystemParams;
import com.systematictesting.automation.core.utils.CommandLineParamsUtils;

public class Report {
	private static final Logger log = Logger.getLogger(Report.class.getName());

	public static void publish(SuiteReport suiteReport) {
		String testSuitesLocation = ""; // JAIMATADI CommandLineParamsUtils.getInstance().getTestSuitesLocation();
		if (suiteReport != null && suiteReport.getTestSuiteReports().size() > 0 && testSuitesLocation != null
				&& testSuitesLocation.length() > 0) {
			Set<String> suitesName = suiteReport.getTestSuiteReports().keySet();
			for (String suiteName : suitesName) {
				if (!testSuitesLocation.endsWith("/")) {
					testSuitesLocation = testSuitesLocation + "/";
				}
				String reportFolderLocation = createReportLocation(testSuitesLocation, suiteName);
				if (reportFolderLocation == null) {
					return;
				}
				createReportSkeletonIndexFile(reportFolderLocation);
				writeReportSummaryJsonFile(suiteReport, suiteName, reportFolderLocation);
			}
		}
	}

	public static void publishReportOnServer(SuiteReport suiteReport) {
		log.log(Level.INFO, "PUBLISHING REPORT : TO SERVER...");
		String testReportLocation = CommandLineParamsUtils.getInstance().getTestReportLocation();
		int sizeOfTestSuitesWhichWereProcessed = CommandLineParamsUtils.getInstance().getSizeOfTestSuitesToProcess();
		String siteName = CommandLineParamsUtils.getInstance().getSiteName();
		String email = CommandLineParamsUtils.getInstance().getEmail();
		String apiKey = CommandLineParamsUtils.getInstance().getApiKey();
		String versionNumber = CommandLineParamsUtils.getInstance().getBuildVersion();
		log.log(Level.INFO, "PUBLISHING REPORT : WITH VERSION : " + versionNumber);
		if (suiteReport != null && suiteReport.getTestSuiteReports().size() > 0 && testReportLocation != null
				&& testReportLocation.length() > 0 && testReportLocation.startsWith(FrameworkParams.HTTP_PROTOCOL)
				&& siteName != null && siteName.length() != 0) {
			Set<String> suitesName = suiteReport.getTestSuiteReports().keySet();
			for (String suiteName : suitesName) {
				PostMethod post = new PostMethod(testReportLocation);
				try {
					log.log(Level.INFO, "PUBLISHING REPORT : FOR SUITE NAME : " + suiteName);
					JSONObject testSuiteReport = createTestReportInJsonFormat(suiteReport, suiteName);
					HttpClient client = new HttpClient();
					post.addRequestHeader(FrameworkParams.HEADER_KEY_USER_AGENT, FrameworkParams.USER_AGENT);
					post.addParameter(SystemParams.TEST_SITE_NAME, siteName);
					post.addParameter(SystemParams.TEST_SUITE_NAME, suiteName);
					post.addParameter(FrameworkParams.REQ_PARAM_JSON_REPORT, testSuiteReport.toJSONString());
					post.addParameter(SystemParams.EMAIL, email);
					post.addParameter(SystemParams.API_KEY_TO_POST_REPORT, apiKey);
					post.addParameter(SystemParams.TOTAL_REPORT_SUITES,
							Integer.toString(sizeOfTestSuitesWhichWereProcessed));

					if (versionNumber != null) {
						post.addParameter(SystemParams.VERSION_NUMBER, versionNumber);
					}
					int status = client.executeMethod(post);
					log.log(Level.INFO, "Sending 'POST' request to URL = " + testReportLocation);
					log.log(Level.INFO,
							"Sending 'POST' request to TOTAL REPORT SUITES = " + sizeOfTestSuitesWhichWereProcessed);
					log.log(Level.INFO, "POST Parameters = " + post.getRequestEntity());
					log.log(Level.INFO, "RESPONSE CODE = " + status);
					log.log(Level.INFO, "RESPONSE = " + post.getResponseBodyAsString());
				} catch (MalformedURLException e) {
					log.log(Level.SEVERE, "URL is not correct for Publishing REPORT on Server.", e);
				} catch (IOException e) {
					log.log(Level.SEVERE,
							"Exception occured while opening connection with Reporting Server for publishing report on Server.",
							e);
				} finally {
					post.releaseConnection();
				}

			}
		}
	}

	private static void createReportSkeletonIndexFile(String reportFolderLocation) {

		File indexHTML = new File(reportFolderLocation + "/index.html");
		try {
			File srcFile = new File(Report.class.getResource("reportSkeleton.html").toURI());
			FileUtils.copyFile(srcFile, indexHTML);
		} catch (FileNotFoundException e) {
			log.log(Level.SEVERE,
					"Report index.html file can not be created. Please consult system administrator. Exception is FileNotFoundException.");
		} catch (IOException e) {
			log.log(Level.SEVERE,
					"Report index.html file can not be created. Please consult system administrator. Execption is IOException.");
		} catch (URISyntaxException e) {
			log.log(Level.SEVERE,
					"Report index.html file can not be created. Please consult system administrator. Execption is IOException.");
			e.printStackTrace();
		}
	}

	private static void writeReportSummaryJsonFile(SuiteReport suiteReport, String suiteName,
			String reportFolderLocation) {
		String reportFileLocation = reportFolderLocation + "/data/summary.json";
		JSONObject testSuiteReport = createTestReportInJsonFormat(suiteReport, suiteName);
		FileWriter file = null;
		try {
			File actualDataFile = new File(reportFileLocation);
			actualDataFile.getParentFile().mkdirs();
			file = new FileWriter(actualDataFile);

			file.write(testSuiteReport.toJSONString());
			log.log(Level.INFO, "Successfully Created JSON File as Report.");
			log.log(Level.INFO, "\nReport in JSON Object: " + testSuiteReport);
		} catch (IOException e) {
			log.log(Level.SEVERE, "Report JSON file can not be created because of IOException on file system.");
		} finally {
			if (file != null) {
				try {
					file.flush();
					file.close();
				} catch (IOException e) {
					log.log(Level.SEVERE, "Exception in closing the report JSON file.");
				}

			}
		}
	}

	@SuppressWarnings("unchecked")
	public static JSONObject createTestReportInJsonFormat(SuiteReport suiteReport, String suiteName) {
		TestReport testReport = suiteReport.getTestSuiteReports().get(suiteName);
		JSONObject testSuiteReport = new JSONObject();

		testSuiteReport.put("testSuiteDetails", createTestSuiteDetailsJsonReport(testReport));
		testSuiteReport.put("testCaseArray", createTestCasesArray(testReport));
		return testSuiteReport;
	}

	private static String createReportLocation(String testSuitesLocation, String suiteName) {
		String reportCreationTime = "/" + TestReportUtils.now("dd-MM-yyyy-HH-mm-ss");
		String reportFolderLocation = testSuitesLocation + suiteName + reportCreationTime;

		File theDir = new File(reportFolderLocation);
		if (!theDir.exists()) {
			log.log(Level.INFO, "Creating Report Directory : " + reportFolderLocation);
			boolean result = false;
			try {
				theDir.mkdirs();
				result = true;
			} catch (SecurityException se) {
				log.log(Level.SEVERE,
						"Failed to create reporting directory. Therefore report can not be published. Please contact system administrator of system.");
				return null;
			}
			if (result) {
				log.log(Level.INFO, "Reporting Directory created successfully.");
			}
		}
		return reportFolderLocation;
	}

	@SuppressWarnings("unchecked")
	private static JSONArray createTestCasesArray(TestReport testReport) {
		JSONArray testCaseArray = new JSONArray();

		for (TestCaseDetails tcd : testReport.getTestCases()) {
			JSONObject jsonTCD = new JSONObject();
			jsonTCD.put("testCaseId", tcd.getTestCaseId());
			jsonTCD.put("testCaseName", tcd.getTestCaseName());
			jsonTCD.put("status", tcd.getStatus());
			jsonTCD.put("startTime", tcd.getStartTime());
			jsonTCD.put("endTime", tcd.getEndTime());
			jsonTCD.put("duration",
					calculateTimeInSeconds(tcd.getStartTimeInMilliseconds(), tcd.getEndTimeInMilliseconds()));
			jsonTCD.put("statusClass", tcd.getStatusClass());

			JSONArray jsonTestStepsArray = new JSONArray();

			for (TestStepDetails tsd : tcd.getTestStepsData()) {
				JSONObject jsonTSD = new JSONObject();
				jsonTSD.put("dataSetId", tsd.getDataSetId());
				jsonTSD.put("stepId", tsd.getStepId());
				jsonTSD.put("stepDescription", tsd.getStepDescription());
				jsonTSD.put("stepKeyword", tsd.getStepKeyword());
				jsonTSD.put("stepStatus", tsd.getStepStatus());
				jsonTSD.put("proceedOnFail", tsd.getProceedOnFail());
				jsonTSD.put("stepStatusClass", tsd.getStepStatusClass());
				jsonTSD.put("stepScreenShot", tsd.getStepScreenShot());
				jsonTSD.put("systemMessage", tsd.getSystemMessage());
				jsonTSD.put("stepPageStats", tsd.getStepPageStats());
				jsonTSD.put("duration",
						calculateTimeInSeconds(tsd.getStartTimeInMilliseconds(), tsd.getEndTimeInMilliseconds()));
				jsonTestStepsArray.add(jsonTSD);
			}
			jsonTCD.put("testStepsData", jsonTestStepsArray);
			testCaseArray.add(jsonTCD);
		}
		return testCaseArray;
	}

	@SuppressWarnings("unchecked")
	private static JSONObject createTestSuiteDetailsJsonReport(TestReport testReport) {
		JSONObject testSuiteDetails = new JSONObject();
		testSuiteDetails.put("testSuiteName", testReport.getTestSuiteDetails().getTestSuiteName());
		testSuiteDetails.put("startTime", testReport.getTestSuiteDetails().getStartTime());
		testSuiteDetails.put("endTime", testReport.getTestSuiteDetails().getEndTime());
		testSuiteDetails.put("duration",
				calculateTimeInSeconds(testReport.getTestSuiteDetails().getStartTimeInMilliseconds(),
						testReport.getTestSuiteDetails().getEndTimeInMilliseconds()));
		testSuiteDetails.put("environment", testReport.getTestSuiteDetails().getEnvironment());
		testSuiteDetails.put("version", testReport.getTestSuiteDetails().getVersion());
		testSuiteDetails.put("summary", createSummaryArray(testReport));
		return testSuiteDetails;
	}

	@SuppressWarnings("unchecked")
	private static JSONArray createSummaryArray(TestReport testReport) {
		JSONArray summaryArray = new JSONArray();

		JSONArray headerArray = new JSONArray();
		headerArray.add("Test Case Status");
		headerArray.add("Count");

		JSONArray passArray = new JSONArray();
		passArray.add(Result.PASS);
		passArray.add(
				Integer.parseInt(testReport.getTestSuiteDetails().getSummary().getReportSummary().get(Result.PASS)));

		JSONArray failArray = new JSONArray();
		failArray.add(Result.FAIL);
		failArray.add(
				Integer.parseInt(testReport.getTestSuiteDetails().getSummary().getReportSummary().get(Result.FAIL)));

		JSONArray disabledArray = new JSONArray();
		disabledArray.add(Result.MANUAL);
		disabledArray.add(
				Integer.parseInt(testReport.getTestSuiteDetails().getSummary().getReportSummary().get(Result.MANUAL)));

		JSONArray abortedArray = new JSONArray();
		abortedArray.add(Result.ABORTED);
		abortedArray.add(
				Integer.parseInt(testReport.getTestSuiteDetails().getSummary().getReportSummary().get(Result.ABORTED)));

		summaryArray.add(headerArray);
		summaryArray.add(passArray);
		summaryArray.add(failArray);
		summaryArray.add(disabledArray);
		summaryArray.add(abortedArray);
		return summaryArray;
	}

	private static float calculateTimeInSeconds(long startTimeInMilliseconds, long endTimeInMilliseconds) {
		long timeInSeconds = endTimeInMilliseconds - startTimeInMilliseconds;
		float time = (float) timeInSeconds;
		float result = time / 1000;
		return result;
	}

	public static void uploadFile(String testSuiteName, File file, String requestFileName, String requestVersionNumber,
			String requestStepId, String requestTestCaseId, String requestDataSetId) {
		if (CommandLineParamsUtils.getInstance().getScreenshotUploadLocation() != null) {
			CloseableHttpClient httpclient = createHttpClient();
			try {
				HttpPost httppost = new HttpPost(CommandLineParamsUtils.getInstance().getScreenshotUploadLocation());

				FileBody bin = new FileBody(file);
				StringBody comment = new StringBody("A binary file of some kind", ContentType.TEXT_PLAIN);

				StringBody testSuiteNameBody = new StringBody(testSuiteName, ContentType.TEXT_PLAIN);
				StringBody fileNameBody = new StringBody(requestFileName, ContentType.TEXT_PLAIN);

				StringBody emailBody = new StringBody(CommandLineParamsUtils.getInstance().getEmail(),
						ContentType.TEXT_PLAIN);
				StringBody apiKeyBody = new StringBody(CommandLineParamsUtils.getInstance().getApiKey(),
						ContentType.TEXT_PLAIN);

				StringBody siteNameBody = new StringBody(CommandLineParamsUtils.getInstance().getSiteName(),
						ContentType.TEXT_PLAIN);
				StringBody versionBody = new StringBody(requestVersionNumber, ContentType.TEXT_PLAIN);

				StringBody stepId = new StringBody(requestStepId, ContentType.TEXT_PLAIN);
				StringBody dataSetId = new StringBody(requestDataSetId, ContentType.TEXT_PLAIN);
				StringBody testCaseId = new StringBody(requestTestCaseId, ContentType.TEXT_PLAIN);

				HttpEntity reqEntity = MultipartEntityBuilder.create().addPart("bin", bin).addPart("comment", comment)
						.addPart(SReportReqParams.TEST_SUITE_NAME, testSuiteNameBody)
						.addPart(SReportReqParams.SITE_NAME, siteNameBody)
						.addPart(SReportReqParams.SCREENSHOT_FILENAME, fileNameBody)
						.addPart(SReportReqParams.EMAIL, emailBody).addPart(SReportReqParams.ACTIVE_API_KEY, apiKeyBody)
						.addPart(SReportReqParams.TEST_STEP_ID, stepId)
						.addPart(SReportReqParams.TEST_STEP_DATA_SET_ID, dataSetId)
						.addPart(SReportReqParams.TEST_CASE_ID, testCaseId)
						.addPart(SReportReqParams.VERSION_NUMBER, versionBody).build();
				httppost.setEntity(reqEntity);

				log.log(Level.INFO, "executing request " + httppost.getRequestLine());
				CloseableHttpResponse response = httpclient.execute(httppost);
				try {
					log.log(Level.INFO, "----------------------------------------");
					log.log(Level.INFO, response.getStatusLine() + "");
					HttpEntity resEntity = response.getEntity();
					if (resEntity != null) {
						log.log(Level.INFO, "Response content length: " + resEntity.getContentLength());
					}
					EntityUtils.consume(resEntity);
				} finally {
					response.close();
				}
			} catch (ClientProtocolException e) {
				log.log(Level.SEVERE, "ClientProtocolException for SCREENSHOT UPLOAD : ", e);
			} catch (IOException e) {
				log.log(Level.SEVERE, "ClientProtocolException for SCREENSHOT UPLOAD : ", e);
			} finally {
				try {
					httpclient.close();
				} catch (IOException e) {
					log.log(Level.SEVERE, "IO Exception for SCREENSHOT UPLOAD : ", e);
				}
			}
		}
	}

	private static CloseableHttpClient createHttpClient() {
		CloseableHttpClient httpClient;
		PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager();
		cm.setMaxTotal(306);
		cm.setDefaultMaxPerRoute(108);
		@SuppressWarnings("deprecation")
		Builder builder = RequestConfig.custom()
				.setSocketTimeout(30000)
			    .setConnectTimeout(30000)
			    .setConnectionRequestTimeout(30000)
			    .setStaleConnectionCheckEnabled(true);
		RequestConfig requestConfig = null;
			    
		if (StringUtils.isNotBlank(CommandLineParamsUtils.getInstance().getProxyForSReport())){
			String proxyString = CommandLineParamsUtils.getInstance().getProxyForSReport();
			String proxyHost = proxyString.substring(0, proxyString.indexOf(":"));
			String proxyPort = proxyString.substring(proxyString.indexOf(":")+1);
			requestConfig = builder.setProxy(new HttpHost(proxyHost,Integer.parseInt(proxyPort))).build();
		} else {
			requestConfig = builder.build();
		}
		httpClient = HttpClients.custom().setConnectionManager(cm).setDefaultRequestConfig(requestConfig).build();
		return httpClient;
	}
}
