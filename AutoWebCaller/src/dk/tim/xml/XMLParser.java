package dk.tim.xml;

import java.io.StringReader;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import dk.tim.properties.Properties;
import dk.tim.scanner.properties.ScannerProperties;

public class XMLParser<T> {

	public Properties parseProperties(String xml) {
		validateNotEmpty(xml);
		try {
			JAXBContext jc = JAXBContext.newInstance(new Class[] { Properties.class });
			Unmarshaller um = jc.createUnmarshaller();
			StringReader sr = new StringReader(xml);
			return (Properties) um.unmarshal(sr);
		} catch (JAXBException e) {
			System.err.println("Exception parsing xml " + xml);
			throw new RuntimeException(e);
		}
	}

	public ScannerProperties parsePropertiesScanner(String xml) {//FUCK GENERICS
		validateNotEmpty(xml);
		try {
			JAXBContext jc = JAXBContext.newInstance(new Class[] { ScannerProperties.class });
			Unmarshaller um = jc.createUnmarshaller();
			StringReader sr = new StringReader(xml);
			return (ScannerProperties) um.unmarshal(sr);
		} catch (JAXBException e) {
			System.err.println("Exception parsing xml " + xml);
			throw new RuntimeException(e);
		}
	}
	
	private void validateNotEmpty(String xml) {
		if (xml == null || xml.length() == 0) {
			throw new IllegalArgumentException("XML query is empty!");
		}
	}
}
