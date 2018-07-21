package com.systematictesting.qdos.beans;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TestSuite {
	private String suiteName;
	private String suiteDescription;
	private List<TestCaseData> testCaseArray = new ArrayList<TestCaseData>();
	private Map<String,String> keyValuePairs = new HashMap<String,String>();
	public String getSuiteName() {
		return suiteName;
	}
	public void setSuiteName(String suiteName) {
		this.suiteName = suiteName;
	}
	public List<TestCaseData> getTestCaseArray() {
		return testCaseArray;
	}
	public void setTestCaseArray(List<TestCaseData> testCaseArray) {
		this.testCaseArray = testCaseArray;
	}
	public Map<String, String> getKeyValuePairs() {
		return keyValuePairs;
	}
	public void setKeyValuePairs(Map<String, String> keyValuePairs) {
		this.keyValuePairs = keyValuePairs;
	}
	public String getSuiteDescription() {
		return suiteDescription;
	}
	public void setSuiteDescription(String suiteDescription) {
		this.suiteDescription = suiteDescription;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((keyValuePairs == null) ? 0 : keyValuePairs.hashCode());
		result = prime * result + ((suiteDescription == null) ? 0 : suiteDescription.hashCode());
		result = prime * result + ((suiteName == null) ? 0 : suiteName.hashCode());
		result = prime * result + ((testCaseArray == null) ? 0 : testCaseArray.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		TestSuite other = (TestSuite) obj;
		if (keyValuePairs == null) {
			if (other.keyValuePairs != null)
				return false;
		} else if (!keyValuePairs.equals(other.keyValuePairs))
			return false;
		if (suiteDescription == null) {
			if (other.suiteDescription != null)
				return false;
		} else if (!suiteDescription.equals(other.suiteDescription))
			return false;
		if (suiteName == null) {
			if (other.suiteName != null)
				return false;
		} else if (!suiteName.equals(other.suiteName))
			return false;
		if (testCaseArray == null) {
			if (other.testCaseArray != null)
				return false;
		} else if (!testCaseArray.equals(other.testCaseArray))
			return false;
		return true;
	}
	@Override
	public String toString() {
		return "TestSuite [suiteName=" + suiteName + ", suiteDescription=" + suiteDescription + ", testCaseArray="
				+ testCaseArray + ", keyValuePairs=" + keyValuePairs + "]";
	}	
}
