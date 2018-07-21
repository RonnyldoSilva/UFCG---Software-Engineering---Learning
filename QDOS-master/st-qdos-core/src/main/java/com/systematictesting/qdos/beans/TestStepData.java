package com.systematictesting.qdos.beans;

public class TestStepData implements Cloneable{
	private String dataSetId;//it will be generated dynamically based on number of rows in Test Step Data Sheet.
	private String dataSetDescription;
	private String stepId;
	private String stepDescription;
	private String stepKeyword;
	private String proceedOnFail;
	private String elementType;
	private String elementKey;//it will contain the key which will be mapped to KeyValuePairs HashMap.
	private String elementValue;//it will contain the actual value of Test Step element from TC-1-DATA sheet.
	public String getDataSetId() {
		return dataSetId;
	}
	public void setDataSetId(String dataSetId) {
		this.dataSetId = dataSetId;
	}
	public String getStepId() {
		return stepId;
	}
	public void setStepId(String stepId) {
		this.stepId = stepId;
	}
	public String getStepDescription() {
		return stepDescription;
	}
	public void setStepDescription(String stepDescription) {
		this.stepDescription = stepDescription;
	}
	public String getStepKeyword() {
		return stepKeyword;
	}
	public void setStepKeyword(String stepKeyword) {
		this.stepKeyword = stepKeyword;
	}
	public String getProceedOnFail() {
		return proceedOnFail;
	}
	public void setProceedOnFail(String proceedOnFail) {
		this.proceedOnFail = proceedOnFail;
	}
	public String getElementType() {
		return elementType;
	}
	public void setElementType(String elementType) {
		this.elementType = elementType;
	}
	public String getElementKey() {
		return elementKey;
	}
	public void setElementKey(String elementKey) {
		this.elementKey = elementKey;
	}
	public String getElementValue() {
		return elementValue;
	}
	public void setElementValue(String elementValue) {
		this.elementValue = elementValue;
	}
	public String getDataSetDescription() {
		return dataSetDescription;
	}
	public void setDataSetDescription(String dataSetDescription) {
		this.dataSetDescription = dataSetDescription;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((dataSetDescription == null) ? 0 : dataSetDescription.hashCode());
		result = prime * result + ((dataSetId == null) ? 0 : dataSetId.hashCode());
		result = prime * result + ((elementKey == null) ? 0 : elementKey.hashCode());
		result = prime * result + ((elementType == null) ? 0 : elementType.hashCode());
		result = prime * result + ((elementValue == null) ? 0 : elementValue.hashCode());
		result = prime * result + ((proceedOnFail == null) ? 0 : proceedOnFail.hashCode());
		result = prime * result + ((stepDescription == null) ? 0 : stepDescription.hashCode());
		result = prime * result + ((stepId == null) ? 0 : stepId.hashCode());
		result = prime * result + ((stepKeyword == null) ? 0 : stepKeyword.hashCode());
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
		TestStepData other = (TestStepData) obj;
		if (dataSetDescription == null) {
			if (other.dataSetDescription != null)
				return false;
		} else if (!dataSetDescription.equals(other.dataSetDescription))
			return false;
		if (dataSetId == null) {
			if (other.dataSetId != null)
				return false;
		} else if (!dataSetId.equals(other.dataSetId))
			return false;
		if (elementKey == null) {
			if (other.elementKey != null)
				return false;
		} else if (!elementKey.equals(other.elementKey))
			return false;
		if (elementType == null) {
			if (other.elementType != null)
				return false;
		} else if (!elementType.equals(other.elementType))
			return false;
		if (elementValue == null) {
			if (other.elementValue != null)
				return false;
		} else if (!elementValue.equals(other.elementValue))
			return false;
		if (proceedOnFail == null) {
			if (other.proceedOnFail != null)
				return false;
		} else if (!proceedOnFail.equals(other.proceedOnFail))
			return false;
		if (stepDescription == null) {
			if (other.stepDescription != null)
				return false;
		} else if (!stepDescription.equals(other.stepDescription))
			return false;
		if (stepId == null) {
			if (other.stepId != null)
				return false;
		} else if (!stepId.equals(other.stepId))
			return false;
		if (stepKeyword == null) {
			if (other.stepKeyword != null)
				return false;
		} else if (!stepKeyword.equals(other.stepKeyword))
			return false;
		return true;
	}
	@Override
	public String toString() {
		return "TestStepData [dataSetId=" + dataSetId + ", dataSetDescription=" + dataSetDescription + ", stepId="
				+ stepId + ", stepDescription=" + stepDescription + ", stepKeyword=" + stepKeyword + ", proceedOnFail="
				+ proceedOnFail + ", elementType=" + elementType + ", elementKey=" + elementKey + ", elementValue="
				+ elementValue + "]";
	}
	@Override
	public TestStepData clone() {
		TestStepData returnValue = null;
		try {
			returnValue = (TestStepData) super.clone();
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}
		return returnValue;
	}
}
