package com.systematictesting.automation.core.keywords.impl;

import java.util.Map;

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

public class InputFieldValidation implements Process {

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
			    JsonObject  jsonFieldValidationRules = jelement.getAsJsonObject();
			    
				boolean expectdRequiredValue = jsonFieldValidationRules.get("required")!=null ? jsonFieldValidationRules.get("required").getAsBoolean():false;
				boolean isFieldRequired = true;
				if (expectdRequiredValue){
					String actualRequiredValue = element.getAttribute("required");
					System.out.println("expectdRequiredValue = "+expectdRequiredValue);
					System.out.println("actualRequiredValue = "+actualRequiredValue);
					if (actualRequiredValue==null){
						isFieldRequired = false;
					}
				}
				
				String expectedPatternValue = jsonFieldValidationRules.get("pattern")!=null?jsonFieldValidationRules.get("pattern").getAsString():null;
				boolean isPatternValid = true;
				if (expectedPatternValue!=null){
					String actualPatternValue = element.getAttribute("pattern");
					System.out.println("actualPatternValue = "+actualPatternValue);
					System.out.println("expectedPatternValue = "+expectedPatternValue);
					isPatternValid = actualPatternValue.equalsIgnoreCase(expectedPatternValue);
				}
				
				String expectedJavascriptCodeOnInvalidEvent = jsonFieldValidationRules.get("oninvalid")!=null?jsonFieldValidationRules.get("oninvalid").getAsString():null;
				boolean isOnInvalidEventCodeValid = true;
				if (expectedJavascriptCodeOnInvalidEvent!=null){
					String actualJavascriptCodeOnInvalidEvent = element.getAttribute("oninvalid");
					actualJavascriptCodeOnInvalidEvent = actualJavascriptCodeOnInvalidEvent.replaceAll("\\P{Print}", "");
					expectedJavascriptCodeOnInvalidEvent = expectedJavascriptCodeOnInvalidEvent.replaceAll("\\P{Print}", "");
					System.out.println("actualJavascriptCodeOnInvalidEvent = "+actualJavascriptCodeOnInvalidEvent);
					System.out.println("expectedJavascriptCodeOnInvalidEvent = "+expectedJavascriptCodeOnInvalidEvent);
					isOnInvalidEventCodeValid = actualJavascriptCodeOnInvalidEvent.equalsIgnoreCase(expectedJavascriptCodeOnInvalidEvent);
				}
				
				
				String expectedJavascriptCodeOnChangeEvent = jsonFieldValidationRules.get("onchange")!=null?jsonFieldValidationRules.get("onchange").getAsString():null;
				boolean isOnChangeEventCodeValid = true;
				if (expectedJavascriptCodeOnChangeEvent!=null){
					String actualJavascriptCodeOnChangeEvent = element.getAttribute("onchange");
					System.out.println("actualJavascriptCodeOnInvalidEvent = "+actualJavascriptCodeOnChangeEvent);
					System.out.println("expectedJavascriptCodeOnChangeEvent = "+expectedJavascriptCodeOnChangeEvent);
					isOnChangeEventCodeValid = actualJavascriptCodeOnChangeEvent.equalsIgnoreCase(expectedJavascriptCodeOnChangeEvent);
				}
				
				int expectedMaxLengthAllowedOnField = jsonFieldValidationRules.get("maxlength")!=null?jsonFieldValidationRules.get("maxlength").getAsInt():-1;
				boolean isMaxLengthOnField = true;
				if (expectedMaxLengthAllowedOnField!=-1){
					String actualMaxLengthOnField = element.getAttribute("maxlength");
					int actualMaxLengthAllowedOnField = actualMaxLengthOnField!=null? Integer.parseInt(actualMaxLengthOnField): -1;
					System.out.println("actualMaxLengthAllowedOnField = "+actualMaxLengthAllowedOnField);
					System.out.println("expectedMaxLengthAllowedOnField = "+expectedMaxLengthAllowedOnField);
					isMaxLengthOnField = expectedMaxLengthAllowedOnField == actualMaxLengthAllowedOnField;
				}
				
				if (!isFieldRequired || !isPatternValid || !isOnInvalidEventCodeValid || !isOnChangeEventCodeValid || !isMaxLengthOnField){
					String fieldRequiredMessage = !isFieldRequired ? "Field is required": "";
					String patternErrorMessage = !isPatternValid ? "Pattern of field is not matching, " : "";
					String invalidValueErrorMessage = !isOnInvalidEventCodeValid? "Invalid value is assigned in field, " : "";
					String changeEventErrorMessage = !isOnChangeEventCodeValid ? "Change event codebase is not matching, " : "";
					String maxLengthErrorMessage = !isMaxLengthOnField ? "Max length is not matching the expected value, " : "";
					return Result.FAIL + " - " +fieldRequiredMessage+patternErrorMessage+invalidValueErrorMessage+changeEventErrorMessage+maxLengthErrorMessage;
				}
			}
			
		} catch (Throwable t) {
			return Result.FAIL+" - " + t.getMessage();
		}

		return Result.PASS;
	}
}
