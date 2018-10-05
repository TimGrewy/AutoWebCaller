package dk.tim.webclient;

import java.io.IOException;
import java.net.URL;

import com.gargoylesoftware.htmlunit.NicelyResynchronizingAjaxController;
import com.gargoylesoftware.htmlunit.Page;
import com.gargoylesoftware.htmlunit.RefreshHandler;
import com.gargoylesoftware.htmlunit.WebClient;

public class WebClientHelper {
	public static void setupWebClient(final WebClient webClient) {
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
