package dk.tim.scanner.properties;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "Properties")
public class ScannerProperties {

	private String site = "";
	private String logFile;
	private String emailJarLocation;
	private String emailConfigurationLocation;
	private String errorEmailSendTo;
	private String targetListLocation;
	private String completedListLocation;

	public String getSite() {
		return site;
	}

	public void setSite(String site) {
		this.site = site;
	}

	public String getLogFile() {
		return logFile;
	}

	public void setLogFile(String logFile) {
		this.logFile = logFile;
	}

	public String getEmailJarLocation() {
		return emailJarLocation;
	}

	public void setEmailJarLocation(String emailJarLocation) {
		this.emailJarLocation = emailJarLocation;
	}

	public String getEmailConfigurationLocation() {
		return emailConfigurationLocation;
	}

	public void setEmailConfigurationLocation(String emailConfigurationLocation) {
		this.emailConfigurationLocation = emailConfigurationLocation;
	}

	public String getErrorEmailSendTo() {
		return errorEmailSendTo;
	}

	public void setErrorEmailSendTo(String errorEmailSendTo) {
		this.errorEmailSendTo = errorEmailSendTo;
	}

	public String getTargetListLocation() {
		return targetListLocation;
	}

	public void setTargetListLocation(String targetListLocation) {
		this.targetListLocation = targetListLocation;
	}

	public String getCompletedListLocation() {
		return completedListLocation;
	}

	public void setCompletedListLocation(String completedListLocation) {
		this.completedListLocation = completedListLocation;
	}

	@Override
	public String toString() {
		return String.format("Properties [site=%s, logFile=%s, emailJarLocation=%s, emailConfigurationLocation=%s, errorEmailSendTo=%s]", site, logFile, emailJarLocation, emailConfigurationLocation, errorEmailSendTo);
	}

}