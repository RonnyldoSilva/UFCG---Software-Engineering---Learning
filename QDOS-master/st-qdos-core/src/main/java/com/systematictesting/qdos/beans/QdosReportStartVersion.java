package com.systematictesting.qdos.beans;

import java.util.ArrayList;
import java.util.List;

public class QdosReportStartVersion {

	private String nextQdosVersionNumber;
	private String catalogVersionNumber;
	private List<SingleTestSuite> catalogSuites = new ArrayList<SingleTestSuite>();
	private AwsCreds awsCredentials;
	
	public String getNextQdosVersionNumber() {
		return nextQdosVersionNumber;
	}
	public void setNextQdosVersionNumber(String nextQdosVersionNumber) {
		this.nextQdosVersionNumber = nextQdosVersionNumber;
	}
	public String getCatalogVersionNumber() {
		return catalogVersionNumber;
	}
	public void setCatalogVersionNumber(String catalogVersionNumber) {
		this.catalogVersionNumber = catalogVersionNumber;
	}
	public List<SingleTestSuite> getCatalogSuites() {
		return catalogSuites;
	}
	public void setCatalogSuites(List<SingleTestSuite> catalogSuites) {
		this.catalogSuites = catalogSuites;
	}
	public AwsCreds getAwsCredentials() {
		return awsCredentials;
	}
	public void setAwsCredentials(AwsCreds awsCredentials) {
		this.awsCredentials = awsCredentials;
	}
	@Override
	public String toString() {
		return "QdosReportStartVersion [nextQdosVersionNumber=" + nextQdosVersionNumber + ", catalogVersionNumber="
				+ catalogVersionNumber + ", catalogSuites=" + catalogSuites + ", awsCredentials=" + awsCredentials
				+ "]";
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((awsCredentials == null) ? 0 : awsCredentials.hashCode());
		result = prime * result + ((catalogSuites == null) ? 0 : catalogSuites.hashCode());
		result = prime * result + ((catalogVersionNumber == null) ? 0 : catalogVersionNumber.hashCode());
		result = prime * result + ((nextQdosVersionNumber == null) ? 0 : nextQdosVersionNumber.hashCode());
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
		QdosReportStartVersion other = (QdosReportStartVersion) obj;
		if (awsCredentials == null) {
			if (other.awsCredentials != null)
				return false;
		} else if (!awsCredentials.equals(other.awsCredentials))
			return false;
		if (catalogSuites == null) {
			if (other.catalogSuites != null)
				return false;
		} else if (!catalogSuites.equals(other.catalogSuites))
			return false;
		if (catalogVersionNumber == null) {
			if (other.catalogVersionNumber != null)
				return false;
		} else if (!catalogVersionNumber.equals(other.catalogVersionNumber))
			return false;
		if (nextQdosVersionNumber == null) {
			if (other.nextQdosVersionNumber != null)
				return false;
		} else if (!nextQdosVersionNumber.equals(other.nextQdosVersionNumber))
			return false;
		return true;
	}
}
