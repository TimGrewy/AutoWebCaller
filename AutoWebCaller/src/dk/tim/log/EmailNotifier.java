package dk.tim.log;

import java.io.IOException;

import dk.tim.common.StringUtils;
import dk.tim.properties.Properties;

public class EmailNotifier {
	private String jarLocation;
	private String configurationLocation;

	public EmailNotifier(Properties properties) {
		if (properties != null) {
			this.jarLocation = properties.getEmailJarLocation();
			this.configurationLocation = properties.getEmailConfigurationLocation();
		}
	}

	public void sendNotificationEmail(String to, String header, String body) {
		if (isSetup()) {
			String launchCommand = String.format("java -jar %s %s \"%s\" \"%s\" %s", jarLocation, to, header, body, configurationLocation);
			Logger.logToSystemLogAndSystemOut("Sending email using command: " + launchCommand);
			try {
				Process proc = Runtime.getRuntime().exec(launchCommand);
				proc.waitFor();
				java.io.InputStream is = proc.getInputStream();
				byte b[] = new byte[is.available()];
				is.read(b, 0, b.length);
				System.out.println("msg: " + new String(b));
			} catch (IOException | InterruptedException e) {
				Logger.logToSystemLog("Failed to send email! Command: " + launchCommand + "Error: " + e.getMessage());
				System.out.println("Failed to send email! Command: " + launchCommand + "Error: " + e.getMessage());
			}
		} else {
			Logger.logToSystemLog("Email has not been set up, skipping sending email");
			System.out.println("Email has not been set up, skipping sending email");
		}
	}

	private boolean isSetup() {
		return StringUtils.isNotEmpty(jarLocation) && StringUtils.isNotEmpty(configurationLocation);
	}
}
