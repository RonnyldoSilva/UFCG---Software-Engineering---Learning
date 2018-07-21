package com.systematictesting.automation.core.keywords.impl;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.systematictesting.automation.core.constants.ElementType;
import com.systematictesting.automation.core.constants.Result;
import com.systematictesting.automation.core.constants.SystemParams;
import com.systematictesting.automation.core.framework.Browser;
import com.systematictesting.automation.core.keywords.Process;
import com.systematictesting.qdos.beans.TestStepData;

public class PdfReader implements Process {

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
				
				String pdfFileName = element.getText();
				String fileLocation = SystemParams.DOWNLOAD_FILE_LOCATION + File.separator + pdfFileName;
				File pdfFile = new File(fileLocation);
				if (pdfFile.exists()){
					JsonElement jelement = new JsonParser().parse(testStep.getElementValue());
				    JsonObject  jsonDataValidationRules = jelement.getAsJsonObject();
				    Map<Integer,List<String>> pdfContentInMap = new HashMap<Integer, List<String>>();
				    try {
						PDDocument document = PDDocument.load(pdfFile);
						document.getClass();
			            if (!document.isEncrypted()) {
			            	PDFTextStripper tStripper = new PDFTextStripper();
			                int count = document.getNumberOfPages();
			                populateLinesInArrayListAndPutThemInHashMap(pdfContentInMap, document, tStripper, count);
			            }
				    } catch (IOException e) {
						e.printStackTrace();
						return Result.FAIL + " - Failed to read PDF file";
					}
					
				    JsonArray jsonPages = jsonDataValidationRules.getAsJsonArray("pages");
				    for(int index=0;index<jsonPages.size();index++){
				    	boolean isDataPresent = true;
				    	isDataPresent = verifyExpectedDataWithActualData(pdfContentInMap, jsonPages, index,
								isDataPresent);
				    	if (!isDataPresent){
				    		return Result.FAIL + " - Data doesn't match with PDF File. Please check logs.";
				    	}
				    }
					
				} else {
					return Result.FAIL+" - PDF File does not exists on download location : "+fileLocation;
				}
			}
			
		} catch (Throwable t) {
			return Result.FAIL+" - " + t.getMessage();
		}

		return Result.PASS;
	}

	private boolean verifyExpectedDataWithActualData(Map<Integer, List<String>> pdfContentInMap, JsonArray jsonPages,
			int index, boolean isDataPresent) {
		JsonObject page = jsonPages.get(index).getAsJsonObject();
		Integer pageNumber = page.get("pageNumber").getAsInt();
		List<String> listOfLinesInPDF = pdfContentInMap.get(pageNumber);
		JsonArray expectedDataList = page.getAsJsonArray("dataList");
		for(int dataindex=0;dataindex<expectedDataList.size();dataindex++){
			String dataLine = expectedDataList.get(dataindex).getAsString();
			if (!listOfLinesInPDF.contains(dataLine)){
				System.out.println("ERROR :: Expected Line is not Present in Page Number  :"+pageNumber+" \n Data Line :"+dataLine+"\n\n");
				System.out.println("ERROR :: ACTUAL LINES STARTING------");
				System.out.println("ERROR :: ACTUAL LINES IN PDFs ARE : \n"+listOfLinesInPDF);
				System.out.println("ERROR :: ACTUAL LINES ENDING------");
				
				isDataPresent = false;
				break;
			}
		}
		return isDataPresent;
	}

	private void populateLinesInArrayListAndPutThemInHashMap(Map<Integer, List<String>> pdfContentInMap,
			PDDocument document, PDFTextStripper tStripper, int count) throws IOException {
		for (int pageNumber=1;pageNumber<=count;pageNumber++){
			tStripper.setStartPage(pageNumber);
		    tStripper.setEndPage(pageNumber);
		    String pageData = tStripper.getText(document);
		    String linesInPage[] = pageData.split("\\r?\\n");
		    List<String> linesInList = new ArrayList<String>();
		    Collections.addAll(linesInList, linesInPage);
		    pdfContentInMap.put(pageNumber, linesInList);
		}
	}
	
	public static void main(String[] args) {
		PdfReader pdfReader = new PdfReader();
		String dataFromExcelSheet = "{  'pages': [    {      'pageNumber': 1,      'dataList': [        'August',        'Apprenticeships for starts before 1 May 2018',        'Advanced Learner Loans Bursary 0.00 0.00 0.00'      ]    },    {      'pageNumber': 2,      'dataList': [        'September',        'Apprenticeships for starts before 1 May 2017',        'Advanced Learner Loans Bursary 0.00 0.00 0.00'      ]    },    {      'pageNumber': 3,      'dataList': [        'October',        'Apprenticeships for starts before 1 May 2017',        'Advanced Learner Loans Bursary 55.00 55.00 55.00'      ]    }  ]}";
		JsonElement jelement = new JsonParser().parse(dataFromExcelSheet);
		JsonObject  jsonDataValidationRules = jelement.getAsJsonObject();
		System.out.println(jsonDataValidationRules);
		
		File pdfFile = new File("/var/folders/q_/2jf5sj894hjgdsffpmn2djcc0000gn/T//downloads/EAS Submission Report 20171022-024147.pdf");
		if (pdfFile.exists()){
			System.out.println("FILE IS PRESENT");
			Map<Integer,List<String>> pdfContentInMap = new HashMap<Integer, List<String>>();
			try {
				PDDocument document = PDDocument.load(pdfFile);
				document.getClass();
	            if (!document.isEncrypted()) {
	                PDFTextStripper tStripper = new PDFTextStripper();
	                int count = document.getNumberOfPages();
	                System.out.println("TOTAL PAGES : "+count);
	                pdfReader.populateLinesInArrayListAndPutThemInHashMap(pdfContentInMap, document, tStripper, count);	                
	            }
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			JsonArray jsonPages = jsonDataValidationRules.getAsJsonArray("pages");
		    for(int index=0;index<jsonPages.size();index++){
		    	boolean isDataPresent = true;
		    	isDataPresent = pdfReader.verifyExpectedDataWithActualData(pdfContentInMap, jsonPages, index,
						isDataPresent);
		    	if (!isDataPresent){
		    		System.out.println(Result.FAIL + " - Data doesn't match with PDF File. Please check logs.");
		    	}
		    }
		}
	}

}

/*
 * Sample data validation rules which will go inside the Excel Sheet are:
 * 
{
  "pages": [
    {
      "pageNumber": 1,
      "dataList": [
        "August",
        "UKPRN : 10004180",
        "Collection Period: R03 Year: 2017/18",
        "Apprenticeships for starts on or after 1 May 2017",
        "Training (excluding English & Maths) 0.00 0.00",
        "Additional payments for employers 0.00 0.00",
        "Additional payments for providers 0.00 0.00 0.00 0.00",
        "Advanced Learner Loans",
        "Advanced Learner Loans Bursary 0.00 0.00 0.00"
      ]
    },
    {
      "pageNumber": 2,
      "dataList": [
        "September",
        "UKPRN : 10004180",
        "Apprenticeships for starts on or after 1 May 2017",
        "16-18 Levy contracted",
        "Collection Period: R03 Year: 2017/18",
        "Apprenticeships for starts before 1 May 2017",
        "Training (excluding English & Maths) 0.00 0.00",
        "Additional payments for employers 0.00 0.00",
        "Additional payments for providers 0.00 0.00 0.00 0.00",
        "Advanced Learner Loans",
        "Advanced Learner Loans Bursary 0.00 0.00 0.00"
      ]
    },
    {
      "pageNumber": 3,
      "dataList": [
        "October",
        "UKPRN : 10004180",
        "Collection Period: R03 Year: 2017/18",
        "Apprenticeships for starts before 1 May 2017",
        "Training (excluding English & Maths) -55.00 -555.00",
        "Additional payments for providers 1000.00 1000.00 4000.00 60000.00",
        "Additional payments for employers 10000.00 200000.00",
        "Training (excluding English & Maths) 15000.00 1.00",
        "Additional payments for providers 21.00 88.00 49.00 -888.00",
        "Additional payments for employers 100000.00 71.00",
        "Advanced Learner Loans Bursary 9.00 10.00 19.00",
        "Advanced Learner Loans"
      ]
    }
  ]
}
 */
