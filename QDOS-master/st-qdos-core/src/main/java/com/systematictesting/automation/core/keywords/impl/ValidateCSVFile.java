package com.systematictesting.automation.core.keywords.impl;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import com.systematictesting.automation.core.constants.ElementType;
import com.systematictesting.automation.core.constants.Result;
import com.systematictesting.automation.core.constants.SystemParams;
import com.systematictesting.automation.core.framework.Browser;
import com.systematictesting.automation.core.keywords.Process;
import com.systematictesting.qdos.beans.TestStepData;

public class ValidateCSVFile implements Process {
	
	private final String WILDCARD = "{IGNORE}";

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
			if (testStep.getElementType().equals(ElementType.PARTIAL_LINK_TEXT)) {
				System.out.println(ElementType.PARTIAL_LINK_TEXT+" IS PICKED");
				element = Browser.getInstance().getDriver().findElement(By.partialLinkText(elementKeyValuePairs.get(testStep.getElementKey())));
			}
			if (testStep.getElementType().equals(ElementType.LINK_TEXT)) {
				System.out.println(ElementType.LINK_TEXT+" IS PICKED");
				element = Browser.getInstance().getDriver().findElement(By.linkText(elementKeyValuePairs.get(testStep.getElementKey())));
			}
			if (element!=null){
				
				String csvFileName = element.getText();
				String fileLocation = SystemParams.DOWNLOAD_FILE_LOCATION + File.separator + csvFileName;
				File csvFile = new File(fileLocation);
				if (csvFile.exists()){
				    Map<Integer,String> actualCSVContent = new HashMap<Integer, String>();
				    populateCSVFileContent(actualCSVContent, fileLocation);
				    System.out.println("\n@@@ ValidateCSVFile : ACTUAL : actualCSVContent:\n"+actualCSVContent);
				    
				    
				    String expectedCSVString = testStep.getElementValue();
				    System.out.println("\n@@@ ValidateCSVFile : EXPECTED : String expectedCSVString:\n"+expectedCSVString);
				    
				    Map<Integer,String> expectedCSVContent = new HashMap<Integer, String>();
				    populateCSVFromString(expectedCSVContent, expectedCSVString);
				    System.out.println("\n@@@ ValidateCSVFile : EXPECTED : MAP expectedCSVContent:\n"+expectedCSVContent);
				    
				    boolean isExpectedPresentInActual = true;
				    List<String> listOfUnExpectedLines = new ArrayList<String>();
				    
				    if (expectedCSVContent.size() == actualCSVContent.size()){
					    for (int index=0;index<expectedCSVContent.size();index++){
					    	String lineFromExpectedFileWithWildCard = expectedCSVContent.get(index);
					    	
					    	int totalWildCardCount = getCountOfWildCardInExpectedLine(lineFromExpectedFileWithWildCard);
					    	
					    	if (totalWildCardCount == 0){
					    		String expectedLine = expectedCSVContent.get(index).replaceAll("[^a-zA-Z0-9\\s+]", "");
						    	String actualLine = actualCSVContent.get(index).replaceAll("[^a-zA-Z0-9\\s+]", "");
						    	if (!expectedLine.equals(actualLine)){
						    		isExpectedPresentInActual = false;
						    		listOfUnExpectedLines.add(actualLine);
						    		System.out.println("\n@@@ ValidateCSVFile : ACTUAL LINE :"+actualLine);
						    		System.out.println("\n@@@ ValidateCSVFile : EXPECTED LINE :"+expectedLine);
						    		System.out.println("\n@@@ ValidateCSVFile : EQUAL DECISON :"+expectedLine.equals(actualLine));
						    	}
					    	} else {
					    		//ACTUAL : W,,Header_2,2018-07-22 2018-03-25 00:02:03,The file preparation date is after today's date,0,,,,,,,,,,,
					    		//EXPECTED : W,,Header_2,{IGNORE},The file preparation date is after {IGNORE} date,0,,,,,,,,,,,
					    		String actualLine = actualCSVContent.get(index).replaceAll("[^a-zA-Z0-9\\s+]", "");
					    		int startIndexOfWildCard = 0;
					    		int endIndexOfWildCard = 0;
					    		for (int indexOfWildCard=1;indexOfWildCard<=totalWildCardCount;indexOfWildCard++){
					    			endIndexOfWildCard = lineFromExpectedFileWithWildCard.indexOf(WILDCARD,startIndexOfWildCard);
					    			String expectedStringToBeCompared = lineFromExpectedFileWithWildCard.substring(startIndexOfWildCard,endIndexOfWildCard);
					    			String expectedLine = expectedStringToBeCompared.replaceAll("[^a-zA-Z0-9\\s+]", "");
							    	
							    	if (actualLine.indexOf(expectedLine)!=-1){
							    		endIndexOfWildCard = endIndexOfWildCard + WILDCARD.length();
							    		startIndexOfWildCard = endIndexOfWildCard;
							    	} else {
							    		isExpectedPresentInActual = false;
							    		listOfUnExpectedLines.add(actualLine);
							    		System.out.println("\n@@@ ValidateCSVFile : ACTUAL LINE :"+actualLine);
							    		System.out.println("\n@@@ ValidateCSVFile : EXPECTED LINE with in ACTUAL LINE Between wildcard {IGNORE} :"+expectedLine);
							    		System.out.println("\n@@@ ValidateCSVFile : INDEX VALUE :"+expectedLine.indexOf(actualLine));
							    		break;
							    	}
					    		}
					    	}
					    }
					    if (isExpectedPresentInActual){
					    	return Result.PASS;
					    } else {
					    	int index=0;
					    	for(String unexpectedLines : listOfUnExpectedLines){
					    		System.out.println("\nUNEXPECTED LINE at line="+(index+1)+" : "+unexpectedLines);
					    		index++;
					    	}
					    	return Result.FAIL + " - Some unexpected lines found in CSV File.";
					    }
				    } else {
				    	return Result.FAIL+" - CSV File contains different number of lines between downloaded and expected. So failed to do comparision.";
				    }
				} else {
					return Result.FAIL+" - CSV File does not exists on download location : "+fileLocation;
				}
			}
			
		} catch (Throwable t) {
			return Result.FAIL+" - " + t.getMessage();
		}

		return Result.PASS;
	}

	private int getCountOfWildCardInExpectedLine(String lineFromExpectedFileWithWildCard) {
		int count = 0;
		int lastIndex = 0;
		while(lastIndex != -1){

		    lastIndex = lineFromExpectedFileWithWildCard.indexOf(WILDCARD,lastIndex);

		    if(lastIndex != -1){
		        count ++;
		        lastIndex += WILDCARD.length();
		    }
		}
		
		return count;
	}

	private void populateCSVFileContent(Map<Integer,String> csvContentInMap, String csvFileNameWithPath) {
        String line = "";

        try (BufferedReader br = new BufferedReader(new FileReader(csvFileNameWithPath))) {
        	int index=0;
            while ((line = br.readLine()) != null) {
                csvContentInMap.put(index, line);
                index++;
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
	}
	
	private void populateCSVFromString(Map<Integer,String> csvContentInMap, String csvContentInString) {
        String line = System.getProperty("line.separator");

        String[] dataInLine = csvContentInString.split(line);
        
        int index=0;
        for (String expectedLine : dataInLine){
        	csvContentInMap.put(index, expectedLine);
        	index++;
        }
	}
	
	public static void main(String[] args) {
		String csvFileNameWithPath = "/Users/sharadkumar/Downloads/Rule Violation Report 20171116-141514.csv";
		Map<Integer,String> csvContentInMap = new HashMap<Integer, String>();
		ValidateCSVFile objValidateCSVFile = new ValidateCSVFile();
		objValidateCSVFile.populateCSVFileContent(csvContentInMap, csvFileNameWithPath);
		System.out.println("DATA : "+csvContentInMap);		
	}

}


