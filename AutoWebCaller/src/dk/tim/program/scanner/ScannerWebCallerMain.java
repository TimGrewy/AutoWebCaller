package dk.tim.program.scanner;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import dk.tim.file.ReadFile;
import dk.tim.log.EmailNotifier;
import dk.tim.log.Logger;
import dk.tim.properties.Properties;
import dk.tim.scanner.properties.ScannerProperties;
import dk.tim.xml.XMLParser;

/**
 *Launch jar by running command java -jar AutoWebCaller.jar 
 *
 * If you do not specify a location of scannerproperties.xml the program will look in the folder from where you have run it!
 * 		(Look into scannerpropertiex.xml for what the program need to run)
 * 
 * Export: Eclipse->File->Export->java-jar (Runnable jar)- Choose Main.java to be default class - make sure the main project folder is selected.
 * (Remember to choose the right main class since the program now does 2 things :) )
 */
public class ScannerWebCallerMain {
	public static void main(String[] args) {
		String fileLocation = extractPropertiesFileLocation(args);
		ScannerProperties properties = readProperties(Paths.get(fileLocation), true);
		
		List<String> messages = new ArrayList<>(); 
		
		int i = 0;
		messages.add(attemptRun(args, properties));
		while (!isLastElementAnSuccess(messages) &&  i < 3) {
			messages.add(attemptRun(args, properties));
			i++;
			try {
				System.out.println("Failed - trying again in 5 minuttes. Error: " + messages.get(messages.size()-1));
				Thread.sleep(1000*60*5); //5 mins wait
			} catch (InterruptedException e) {}
		}
		if(!isLastElementAnSuccess(messages) && i >= 3 ){
			new EmailNotifier(	properties.getEmailJarLocation(), properties.getEmailConfigurationLocation())
    		.sendNotificationEmail(properties.getErrorEmailSendTo(), "Failed to run @ " + new Date(), "Errors: " + messages);
		}
	}
	
	private static boolean isLastElementAnSuccess(List<String> list) {
		String string = list.get(list.size()-1);
		return "sucess".equals(string);
	}

	private static String attemptRun(String[] args, ScannerProperties properties) {
		try {
			
			long start = System.currentTimeMillis();
			setupLogging(properties.getLogFile());

			ScannerWebCaller scannerWebCaller = new ScannerWebCaller(properties);
			scannerWebCaller.runProgram();
			long spent = System.currentTimeMillis() - start;
			Logger.logToSystemLogAndSystemOut("Program completed in " + spent + " ms");
		} catch (Exception e) {
			String errorMsg = "Failed to run program. " + e.getMessage();
			Logger.logToSystemLogAndSystemOut(errorMsg);
			Logger.logToSystemLogAndSystemOut(Logger.parseStackTraceToString(e));
			return errorMsg;
		} finally {
			Logger.closeLogger();
		}
		return null;
	}

	private static String extractPropertiesFileLocation(String[] args) {
		String settingsFile;
		if (args.length == 0) {
			String curDir = System.getProperty("user.dir");
			settingsFile = curDir + "//scannerproperties.xml";
		} else {
			settingsFile = args[0];
		}
		return settingsFile;
	}

	private static Properties readProperties(Path xmlFile) {
		ReadFile readFile = new ReadFile();
		XMLParser<Properties> xmlParser = new XMLParser<Properties>();
		String xml = readFile.getFileContent(xmlFile);
		Properties properties = xmlParser.parseProperties(xml);
		return properties;
	}

	private static ScannerProperties readProperties(Path xmlFile, boolean fixMe) {
		ReadFile readFile = new ReadFile();
		XMLParser<ScannerProperties> xmlParser = new XMLParser<ScannerProperties>();
		String xml = readFile.getFileContent(xmlFile);
		ScannerProperties properties = xmlParser.parsePropertiesScanner(xml);
		return properties;
	}

	private static void setupLogging(String logFile) {
		System.out.println("Setting up logger: " + logFile);
		Logger.systemLog = new Logger(logFile);
	}
}
