package dk.tim.program;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;

import dk.tim.file.ReadFile;
import dk.tim.log.EmailNotifier;
import dk.tim.log.Logger;
import dk.tim.properties.Properties;
import dk.tim.xml.XMLParser;

/**
 *	Launch by running command java -jar AutoWebCaller.jar 
 *
 * If you do not specify a location of properties.xml the program will look in the folder from where you have run it!
 * 
 * Export: Eclipse->File->Export->java-jar (Runnable jar)- Choose Main.java to be default class - make sure the main project folder is selected.
 */
public class AutoWebCallerMain {
	public static void main(String[] args) {
		Properties properties = null;
		try {
			long start = System.currentTimeMillis();
			String fileLocation = extractPropertiesFileLocation(args);
			properties = readProperties(Paths.get(fileLocation));
			setupLogging(properties.getLogFile());

			AutoWebCaller autoWebCaller = new AutoWebCaller(properties);
			autoWebCaller.runProgram();
			long spent = System.currentTimeMillis() - start;
			Logger.logToSystemLogAndSystemOut("Program completed in " + spent + " ms");
		} catch (Exception e) {
			String errorMsg = "Failed to run program. " + e.getMessage();
			Logger.logToSystemLogAndSystemOut(errorMsg);
			Logger.logToSystemLogAndSystemOut(Logger.parseStackTraceToString(e));

			new EmailNotifier(properties).sendNotificationEmail(properties.getErrorEmailSendTo(), "Failed to run @ " + new Date(), errorMsg);
			throw new RuntimeException(e);
		} finally {
			Logger.closeLogger();
		}
	}

	private static String extractPropertiesFileLocation(String[] args) {
		String settingsFile;
		if (args.length == 0) {
			String curDir = System.getProperty("user.dir");
			settingsFile = curDir + "//properties.xml";
		} else {
			settingsFile = args[0];
		}
		return settingsFile;
	}

	private static Properties readProperties(Path xmlFile) {
		ReadFile readFile = new ReadFile();
		XMLParser xmlParser = new XMLParser();
		String xml = readFile.getFileContent(xmlFile);
		Properties properties = xmlParser.parseProperties(xml);
		return properties;
	}

	private static void setupLogging(String logFile) {
		System.out.println("Setting up logger: " + logFile);
		Logger.systemLog = new Logger(logFile);
	}
}
