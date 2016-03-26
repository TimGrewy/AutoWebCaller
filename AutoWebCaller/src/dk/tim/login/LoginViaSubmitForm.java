package dk.tim.login;

import java.io.IOException;
import java.net.URL;

import com.gargoylesoftware.htmlunit.NicelyResynchronizingAjaxController;
import com.gargoylesoftware.htmlunit.Page;
import com.gargoylesoftware.htmlunit.RefreshHandler;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlInput;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

import dk.tim.common.StringUtils;
import dk.tim.log.Logger;
import dk.tim.properties.Site;

public class LoginViaSubmitForm {

	public static boolean doLogin(Site site) throws Exception {
		try (final WebClient webClient = new WebClient()) {
			setupWebClient(webClient);

			String loginUrl = site.getLoginUrl();
			String loginButtonId = site.getLoginButtonId();
			String loginButtonName = site.getLoginButtonName();
			String usernameFieldId = site.getUsernameFieldId();
			String passwordFieldId = site.getPasswordFieldId();
			String username = site.getUsername();
			String password = site.getPasswordDecrypted();
			String expetedUrlAfterLogin = site.getExpetedUrlAfterLogin();

			// Get the first page
			final HtmlPage page1 = webClient.getPage(loginUrl);

			// find the submit button and the field that we want to change.
			HtmlElement loginElement;
			if (StringUtils.isNotEmpty(loginButtonId)) {
				loginElement = page1.getHtmlElementById(loginButtonId);
			} else {
				loginElement = page1.getElementByName(loginButtonName);
			}
			HtmlInput loginInput = page1.getHtmlElementById(usernameFieldId);
			HtmlInput passwordInput = page1.getHtmlElementById(passwordFieldId);

			// Change the value of the text field
			loginInput.setValueAttribute(username);
			passwordInput.setValueAttribute(password);

			// Now submit the form by clicking the button and get back the second page.
			Page page2 = loginElement.click();
			boolean success = expetedUrlAfterLogin.equals(page2.getUrl().toString());
			if (success) {
				Logger.logToSystemLogAndSystemOut("Success: Loaded " + page2.getUrl() + " with status: " + page2.getWebResponse().getStatusCode() + " " + page2.getWebResponse().getStatusMessage());
			} else {
				Logger.logToSystemLogAndSystemOut("Failed: failed to load site: " + site.getLoginUrl() + " the returned site after login was not as expected. Expected: " + expetedUrlAfterLogin + " but got " + page2.getUrl().toString());
				Logger.logToSystemLogAndSystemOut("Site: " + page2.getWebResponse().getContentAsString());
			}
			return success;
		} catch (Exception e) {
			Logger.logToSystemLogAndSystemOut(e.getMessage());
			Logger.logToSystemLogAndSystemOut(e.getStackTrace() + "");
			throw new RuntimeException(e);
		}
	}

	private static void setupWebClient(final WebClient webClient) {
		//Fix for exception  - http://stackoverflow.com/questions/12057650/htmlunit-failure-attempted-immediaterefreshhandler-outofmemoryerror-use-wait
		//I don't know why this works :/
		webClient.setRefreshHandler(new RefreshHandler() {
			@Override
			public void handleRefresh(Page arg0, URL arg1, int arg2) throws IOException {
			}
		});
		webClient.getOptions().setJavaScriptEnabled(true);
		webClient.getOptions().setCssEnabled(false);
		webClient.getOptions().setUseInsecureSSL(true);
		webClient.getOptions().setThrowExceptionOnFailingStatusCode(false);
		webClient.getCookieManager().setCookiesEnabled(true);
		webClient.setAjaxController(new NicelyResynchronizingAjaxController());
		webClient.getOptions().setThrowExceptionOnScriptError(false);
	}
}