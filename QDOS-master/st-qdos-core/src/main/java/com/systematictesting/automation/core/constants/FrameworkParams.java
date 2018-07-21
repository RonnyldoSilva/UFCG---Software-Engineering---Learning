/*
 * Copyright (c) Jan 11, 2017 Systematic Testing Ltd. (www.systematictesting.com) to Present..
 * All rights reserved. 
 */
package com.systematictesting.automation.core.constants;

public interface FrameworkParams {
	
	String BROWSER_FIREFOX = "Firefox";
	String BROWSER_CHROME = "Chrome";
	String BROWSER_HEADLESS = "PhantomJS";

	String OS_WINDOWS_10 = "Microsoft Windows 10";
	String OS_WINDOWS_7 = "Microsoft Windows 7";
	String OS_MACOSX_10_12_1 = "Mac OSX 10.12.1";

	
	String HTTP_PROTOCOL = "http";
	String METHOD_POST = "POST";
	String USER_AGENT = "Mozilla/5.0";
	String HEADER_KEY_USER_AGENT = "User-Agent";
	String HEADER_ACCEPT_LANGUAGE = "Accept-Language";
	String LANGUAGE = "en-US,en;q=0.5";
	String REQ_PARAM_DEBUG = "debug";
	String REQ_PARAM_JSON_REPORT = "jsonReport";
	String REQ_PARAM_SEPARATOR = "&";
	String REQ_PARAM_VALUE_SEPARATOR = "=";
	String MESSAGE_ERROR_PREFIX = "EXCEPTION IN";
	String MESSAGE_PASS = "OK";
	String MESSAGE_FAIL = "Test Step has failed. Please check logs.";
	String MESSAGE_ABORTED = "Test Step is aborted.";
	String FILE_SEPARATOR = "/";
	String DISABLED = "DISABLED";
	
}
