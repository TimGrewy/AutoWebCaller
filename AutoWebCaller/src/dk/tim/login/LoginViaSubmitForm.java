package dk.tim.login;

import com.gargoylesoftware.htmlunit.Page;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlInput;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

import dk.tim.log.Logger;
import dk.tim.properties.Site;

public class LoginViaSubmitForm {

	public static boolean doLogin(Site site) throws Exception {
		try (final WebClient webClient = new WebClient()) {

			String loginUrl = site.getLoginUrl();
			String loginButtonId = site.getLoginButtonId();
			String usernameFieldId = site.getUsernameFieldId();
			String passwordFieldId = site.getPasswordFieldId();
			String username = site.getUsername();
			String password = site.getPasswordDecrypted();
			String expetedUrlAfterLogin = site.getExpetedUrlAfterLogin();

			// Get the first page
			final HtmlPage page1 = webClient.getPage(loginUrl);

			// find the submit button and the field that we want to change.
			HtmlElement loginElement = page1.getHtmlElementById(loginButtonId);
			HtmlInput loginInput = page1.getHtmlElementById(usernameFieldId);
			HtmlInput passwordInput = page1.getHtmlElementById(passwordFieldId);

			// Change the value of the text field
			loginInput.setValueAttribute(username);
			passwordInput.setValueAttribute(password);

			// Now submit the form by clicking the button and get back the second page.
			Page page2 = loginElement.click();
			Logger.logToSystemLog("Success: Loaded " + page2.getUrl() + " with status: " + page2.getWebResponse().getStatusCode() + " " + page2.getWebResponse().getStatusMessage());
			boolean success = expetedUrlAfterLogin.equals(page2.getUrl());
			if (!success) {
				Logger.logToSystemLogAndSystemOut("Failed to load site: " + site.getLoginUrl() + " the returned site after login was not as expected. Expected: " + expetedUrlAfterLogin + " but got " + page2.getUrl());
			}
			return success;
		}
	}
}