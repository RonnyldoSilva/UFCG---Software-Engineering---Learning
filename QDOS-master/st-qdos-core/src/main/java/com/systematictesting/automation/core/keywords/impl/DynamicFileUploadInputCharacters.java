/*
 * Copyright (c) Jan 11, 2017 Systematic Testing Ltd. (www.systematictesting.com) to Present..
 * All rights reserved. 
 */
package com.systematictesting.automation.core.keywords.impl;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.systematictesting.automation.core.constants.ElementType;
import com.systematictesting.automation.core.constants.Result;
import com.systematictesting.automation.core.framework.Browser;
import com.systematictesting.automation.core.keywords.Process;
import com.systematictesting.qdos.beans.TestStepData;

public class DynamicFileUploadInputCharacters implements Process {

	@Override
	public String execute(Map<String, String> elementKeyValuePairs, TestStepData testStep) {
		if (StringUtils.isBlank(elementKeyValuePairs.get(testStep.getElementKey()))){
			return Result.ABORTED + " : ELEMENT_KEY not found.";
		}
		if (StringUtils.isBlank(testStep.getElementValue())){
			return Result.ABORTED + " : ELEMENT_DATA not found.";
		}
		WebElement element = null;
		
		try {
			if (testStep.getElementType().equals(ElementType.XPATH)) {
				element = Browser.getInstance().getDriver().findElement(By.xpath(elementKeyValuePairs.get(testStep.getElementKey())));
			}
			if (testStep.getElementType().equals(ElementType.ID)) {
				element = Browser.getInstance().getDriver().findElement(By.id(elementKeyValuePairs.get(testStep.getElementKey())));
			}
			if (testStep.getElementType().equals(ElementType.CSS_SELECTOR)) {
				element = Browser.getInstance().getDriver().findElement(By.cssSelector(elementKeyValuePairs.get(testStep.getElementKey())));
			}
			if (element!=null){
				JsonElement jelement = new JsonParser().parse(testStep.getElementValue());
				JsonObject  jsonDataValidationRules = jelement.getAsJsonObject();
				String absoluteFileName = jsonDataValidationRules.get("absoluteFileName").getAsString();
				String newFilePath = jsonDataValidationRules.get("newFilePath").getAsString();
				String newFilenamePattern = jsonDataValidationRules.get("newFilenamePattern").getAsString();
				File actualFile = new File(absoluteFileName);
				if (actualFile.exists()){
					File newDir = new File(newFilePath);
					if (!newDir.exists()){
						newDir.mkdirs();
					} else {
						if (!newDir.isDirectory()){
							newDir.delete();
							newDir.mkdir();
						}
					}
					Calendar cal = Calendar.getInstance();
					cal.setTimeInMillis(System.currentTimeMillis());
					SimpleDateFormat timeFormat = new SimpleDateFormat("HHmmss");
			        String time = timeFormat.format(cal.getTime());
			        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
			        String date = dateFormat.format(cal.getTime());
			        
			        String newFileName = newFilenamePattern.replace("{TIMESTAMP}", time);
					newFileName = newFileName.replace("{DATE}", date);
					
					System.out.println("newFileName = "+newFileName);
					System.out.println("actualFile = "+actualFile.getAbsolutePath());
					System.out.println("newDir = "+newDir.getAbsolutePath());
					
					File targetFile = new File(newDir,newFileName);
					
					FileUtils.copyFile(actualFile, targetFile);
					
					element.sendKeys(targetFile.getAbsolutePath());
				} else {
					return Result.FAIL+" - Absolute file is not present. Here is the path of the file : "+actualFile.getAbsolutePath();
				}
			}
			
		} catch (Throwable t) {
			return Result.FAIL+" - " + t.getMessage();
		}

		return Result.PASS;
	}

	public static void main(String[] args) {
		String jsonData = "{"
				+ "	\"absoluteFileName\":\"/Users/sharadkumar/Downloads/2018_March_Statement.pdf\","
				+ " \"newFilePath\":\"/Users/sharadkumar/DownloadsTest\","
				+ " \"newFilenamePattern\":\"2018_March_Statement_{DATE}_{TIMESTAMP}.pdf\""
				+ "}";
		JsonElement jelement = new JsonParser().parse(jsonData);
		JsonObject  jsonDataValidationRules = jelement.getAsJsonObject();
		String absoluteFileName = jsonDataValidationRules.get("absoluteFileName").getAsString();
		String newFilePath = jsonDataValidationRules.get("newFilePath").getAsString();
		String newFilenamePattern = jsonDataValidationRules.get("newFilenamePattern").getAsString();
		File actualFile = new File(absoluteFileName);
		if (actualFile.exists()){
			File newDir = new File(newFilePath);
			if (!newDir.exists()){
				newDir.mkdirs();
			} else {
				if (!newDir.isDirectory()){
					newDir.delete();
					newDir.mkdir();
				}
			}
			Calendar cal = Calendar.getInstance();
			cal.setTimeInMillis(System.currentTimeMillis());
			SimpleDateFormat timeFormat = new SimpleDateFormat("HHmmss");
	        String time = timeFormat.format(cal.getTime());
	        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
	        String date = dateFormat.format(cal.getTime());
	        
			String newFileName = newFilenamePattern.replace("{TIMESTAMP}", time);
			newFileName = newFileName.replace("{DATE}", date);
			System.out.println("newFileName = "+newFileName);
			System.out.println("actualFile = "+actualFile.getAbsolutePath());
			System.out.println("newDir = "+newDir.getAbsolutePath());
			
			
			try {
				FileUtils.copyFile(actualFile, new File(newDir,newFileName));
			
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
}


/*
 {
 	"absoluteFileName":"/Users/sharadkumar/Downloads/2018_March_Statement.pdf",
 	"newFilePath":"/Users/sharadkumar/Downloads",
 	"newFilenamePattern":"2018_March_Statement_{DATE}_{TIMESTAMP}.pdf"
 }
 
*/
