package com.systematictesting.qdos.beans;

public class SingleTestSuite {
	public interface FIELDS {
		String ID = "id";
		String EMAIL = "email";
		String FILE_NAME = "fileName";
		String SITE_NAME = "siteName";
		String SOURCE_TYPE = "sourceType";
		String SOURCE_EMAIL = "sourceEmail";
		String FILE_LOCATION = "fileLocation";
		String TEST_SUITE = "testSuite";
		String LAST_MODIFIED_TIME = "lastmodifiedtime";
		String CREATE_TIME = "createtime";
	}
	
	private String id;

	private String fileName;
	
	private String email;

	private String siteName;

	private String sourceType;
	
	private String completeStatus;
	
	private String fileLocation;
	private String sourceEmail;
	private TestSuite testSuite;
	private Long lastmodifiedtime;
	private Long createtime;
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getFileName() {
		return fileName;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getSiteName() {
		return siteName;
	}
	public void setSiteName(String siteName) {
		this.siteName = siteName;
	}
	public String getSourceType() {
		return sourceType;
	}
	public void setSourceType(String sourceType) {
		this.sourceType = sourceType;
	}
	public String getFileLocation() {
		return fileLocation;
	}
	public void setFileLocation(String fileLocation) {
		this.fileLocation = fileLocation;
	}
	public Long getLastmodifiedtime() {
		return lastmodifiedtime;
	}
	public void setLastmodifiedtime(Long lastmodifiedtime) {
		this.lastmodifiedtime = lastmodifiedtime;
	}
	public Long getCreatetime() {
		return createtime;
	}
	public void setCreatetime(Long createtime) {
		this.createtime = createtime;
	}
	public TestSuite getTestSuite() {
		return testSuite;
	}
	public void setTestSuite(TestSuite testSuite) {
		this.testSuite = testSuite;
	}
	public String getSourceEmail() {
		return sourceEmail;
	}
	public void setSourceEmail(String sourceEmail) {
		this.sourceEmail = sourceEmail;
	}
	public String getCompleteStatus() {
		return completeStatus;
	}
	public void setCompleteStatus(String completeStatus) {
		this.completeStatus = completeStatus;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((completeStatus == null) ? 0 : completeStatus.hashCode());
		result = prime * result + ((createtime == null) ? 0 : createtime.hashCode());
		result = prime * result + ((email == null) ? 0 : email.hashCode());
		result = prime * result + ((fileLocation == null) ? 0 : fileLocation.hashCode());
		result = prime * result + ((fileName == null) ? 0 : fileName.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((lastmodifiedtime == null) ? 0 : lastmodifiedtime.hashCode());
		result = prime * result + ((siteName == null) ? 0 : siteName.hashCode());
		result = prime * result + ((sourceEmail == null) ? 0 : sourceEmail.hashCode());
		result = prime * result + ((sourceType == null) ? 0 : sourceType.hashCode());
		result = prime * result + ((testSuite == null) ? 0 : testSuite.hashCode());
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
		SingleTestSuite other = (SingleTestSuite) obj;
		if (completeStatus == null) {
			if (other.completeStatus != null)
				return false;
		} else if (!completeStatus.equals(other.completeStatus))
			return false;
		if (createtime == null) {
			if (other.createtime != null)
				return false;
		} else if (!createtime.equals(other.createtime))
			return false;
		if (email == null) {
			if (other.email != null)
				return false;
		} else if (!email.equals(other.email))
			return false;
		if (fileLocation == null) {
			if (other.fileLocation != null)
				return false;
		} else if (!fileLocation.equals(other.fileLocation))
			return false;
		if (fileName == null) {
			if (other.fileName != null)
				return false;
		} else if (!fileName.equals(other.fileName))
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (lastmodifiedtime == null) {
			if (other.lastmodifiedtime != null)
				return false;
		} else if (!lastmodifiedtime.equals(other.lastmodifiedtime))
			return false;
		if (siteName == null) {
			if (other.siteName != null)
				return false;
		} else if (!siteName.equals(other.siteName))
			return false;
		if (sourceEmail == null) {
			if (other.sourceEmail != null)
				return false;
		} else if (!sourceEmail.equals(other.sourceEmail))
			return false;
		if (sourceType == null) {
			if (other.sourceType != null)
				return false;
		} else if (!sourceType.equals(other.sourceType))
			return false;
		if (testSuite == null) {
			if (other.testSuite != null)
				return false;
		} else if (!testSuite.equals(other.testSuite))
			return false;
		return true;
	}
	@Override
	public String toString() {
		return "SingleTestSuite [id=" + id + ", fileName=" + fileName + ", email=" + email + ", siteName=" + siteName
				+ ", sourceType=" + sourceType + ", completeStatus=" + completeStatus + ", fileLocation=" + fileLocation
				+ ", sourceEmail=" + sourceEmail + ", testSuite=" + testSuite + ", lastmodifiedtime=" + lastmodifiedtime
				+ ", createtime=" + createtime + "]";
	}
}
