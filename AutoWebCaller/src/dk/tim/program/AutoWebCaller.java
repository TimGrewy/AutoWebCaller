package dk.tim.program;

import java.util.List;

import dk.tim.log.EmailNotifier;
import dk.tim.log.Logger;
import dk.tim.login.LoginViaSubmitForm;
import dk.tim.properties.Properties;
import dk.tim.properties.Site;

public class AutoWebCaller {

	private Properties properties;

	public AutoWebCaller(Properties properties) {
		this.properties = properties;
	}

	public void runProgram() {
		List<Site> sites = properties.getSites();
		for (Site site : sites) {
			try {
				LoginViaSubmitForm.doLogin(site);
			} catch (Exception e) {
				String errorMsg = "Failed to login to " + site.getLoginUrl() + " Error: " + e.getMessage();
				Logger.logToSystemLogAndSystemOut(errorMsg);
				Logger.logToSystemLogAndSystemOut(Logger.parseStackTraceToString(e));
				throw new RuntimeException(errorMsg, e);
			}
		}
	}
}
