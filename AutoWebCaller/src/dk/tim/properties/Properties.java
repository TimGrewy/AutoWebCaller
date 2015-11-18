package dk.tim.properties;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.apache.commons.io.IOUtils;

@XmlRootElement(name = "Properties")
public class Properties {

	private List<Site> sites = new ArrayList<Site>();
	private String logFile;
	private String emailJarLocation;
	private String emailConfigurationLocation;
	private String errorEmailSendTo;

	@XmlElement(name = "Site")
	public List<Site> getSites() {
		return sites;
	}

	public void setSites(List<Site> sites) {
		this.sites = sites;
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

	@Override
	public String toString() {
		return String.format("Properties [sites=%s, logFile=%s, emailJarLocation=%s, emailConfigurationLocation=%s, errorEmailSendTo=%s]", sites, logFile, emailJarLocation, emailConfigurationLocation, errorEmailSendTo);
	}

	/**
	 *	Prints out a sample properties file for reverse engineering purposes 
	 */
	public static void main(String[] args) throws Exception {
		JAXBContext jc = JAXBContext.newInstance(new Class[] { Properties.class });
		Marshaller marshaller = jc.createMarshaller();
		marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
		ByteArrayOutputStream stream = new ByteArrayOutputStream();

		Properties properties = new Properties();
		properties.sites.add(new Site());
		properties.sites.add(new Site());

		marshaller.marshal(properties, stream);
		String string = stream.toString("UTF-8");
		System.out.println("Result: " + string);

		Unmarshaller unmarshaller = jc.createUnmarshaller();
		Object unmarshal = unmarshaller.unmarshal(IOUtils.toInputStream(string));
		System.out.println("Un: " + unmarshal.getClass());
	}
}