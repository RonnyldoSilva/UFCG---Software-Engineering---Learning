package com.systematictesting.qdos.beans;

import java.util.List;

public class TestCaseDataSchema {
	private List<FieldName> fieldNames;

	public List<FieldName> getFieldNames() {
		return fieldNames;
	}

	public void setFieldNames(List<FieldName> fieldNameList) {
		this.fieldNames = fieldNameList;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((fieldNames == null) ? 0 : fieldNames.hashCode());
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
		TestCaseDataSchema other = (TestCaseDataSchema) obj;
		if (fieldNames == null) {
			if (other.fieldNames != null)
				return false;
		} else if (!fieldNames.equals(other.fieldNames))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "RequestedDataSchemaFields [fieldNames=" + fieldNames + "]";
	}
	
}