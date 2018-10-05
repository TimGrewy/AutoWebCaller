package dk.tim.program.scanner;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Paths;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.DomElement;
import com.gargoylesoftware.htmlunit.html.DomNode;
import com.gargoylesoftware.htmlunit.html.HtmlAnchor;
import com.gargoylesoftware.htmlunit.html.HtmlDivision;
import com.gargoylesoftware.htmlunit.html.HtmlHeading2;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlSpan;

import dk.tim.file.ReadFile;
import dk.tim.log.EmailNotifier;
import dk.tim.log.Logger;
import dk.tim.scanner.properties.ScannerProperties;
import dk.tim.webclient.WebClientHelper;

public class ScannerWebCaller {
	static String[] date_suffixes =
	     {  "0th",  "1st",  "2nd",  "3rd",  "4th",  "5th",  "6th",  "7th",  "8th",  "9th",
	       "10th", "11th", "12th", "13th", "14th", "15th", "16th", "17th", "18th", "19th",
	       "20th", "21st", "22nd", "23rd", "24th", "25th", "26th", "27th", "28th", "29th",
	       "30th", "31st" };
	private ScannerProperties properties;

	public ScannerWebCaller(ScannerProperties properties) {
		this.properties = properties;
	}

	public void runProgram() {
		String site = properties.getSite();
		try (final WebClient webClient = new WebClient()) {
			List<String> lines = new ReadFile().getLines(Paths.get(properties.getTargetListLocation()));
			System.out.println("Calling page 1");
			Titles titles = getTitles(site, webClient);
			boolean b = titles.isEarliestPostOneHourOlderThanNow(new Date());
			for (int i = 2; !b && i <= 10; i++) {
				Thread.sleep(3000);
				System.out.println("Calling page " + i);
				Titles titles2 = getTitles(site+"/page/"+i, webClient);
				b = titles2.isEarliestPostOneHourOlderThanNow(new Date());
				titles.getTitles().addAll(titles2.getTitles());
			}
				
			for (String target: lines) {
				for (String title : titles.getTitles()) {
					Pattern pattern = Pattern.compile(".*"+target+".*", Pattern.CASE_INSENSITIVE);
					Matcher matcher = pattern.matcher(title);
					System.out.println("Matching title "+title+ " with target " +target+ " got " + matcher.matches());
					if(matcher.matches()) {
						//Notify
						new EmailNotifier(	properties.getEmailJarLocation(), properties.getEmailConfigurationLocation())
						                 .sendNotificationEmail(properties.getErrorEmailSendTo(), "Found match for target " + target, "Found match for target " + target + " in title " + title + " at @ " + new Date());
						
						//Log title found
						ReadFile.addLine(properties.getCompletedListLocation(), Logger.createTimeStamp() + ": " + target + " matched " + title);
						
						//remove from target
						new ReadFile().removeLine(target, properties.getTargetListLocation());
						System.out.println("FOUND ONE: " + target + ". Removed from target file");
						break; //Stop looking at titles - work on next target

					}
				}
			}
		} catch (Exception e) {
			String errorMsg = "Failed to scan for titles on " + site + " Error: " + e.getMessage();
			Logger.logToSystemLogAndSystemOut(errorMsg);
			Logger.logToSystemLogAndSystemOut(Logger.parseStackTraceToString(e));
			new EmailNotifier(properties.getEmailJarLocation(), properties.getEmailConfigurationLocation()).sendNotificationEmail(properties.getErrorEmailSendTo(), "Failed to login at a site ", errorMsg);
			throw new RuntimeException(errorMsg, e);
		}
	}
	
	/**
	 *	Find overskriften for hver element der hedder noget med "post-"
	 *	herunder finder vi alle h2'er
	 *	Også i dem er der et link hvor i vi extracter titlen 
	 */
	private Titles getTitles(String site, final WebClient webClient) throws IOException, MalformedURLException {
		try {
			WebClientHelper.setupWebClient(webClient);
			List<String> titles = new ArrayList<>();
			final HtmlPage page = webClient.getPage(site);
			List<?> byXPath = page.getByXPath("//*[starts-with(@id, 'post-')]");
			Date earliestPost = null; //Gem den sidste post på denne side (det er den dermed ældste)
			String earliestPostTitle = null;
			for (Object object : byXPath) {
				HtmlDivision hd = (HtmlDivision) object;

				//Test if element is a title title - if true, find title
				Iterable<DomElement> childElements = hd.getChildElements();
				for (DomElement domElement : childElements) {
					if(domElement instanceof HtmlHeading2) {
						HtmlHeading2 h2 = (HtmlHeading2) domElement;
						Iterable<DomNode> children = h2.getChildren();
						for (DomNode domNode : children) {
							if(domNode instanceof HtmlAnchor) {
								HtmlAnchor ha = (HtmlAnchor) domNode;
								String title = ha.getAttribute("title");
								title = org.apache.commons.lang3.StringUtils.removeStart(title, "GOTO");
								earliestPostTitle = title;
								System.out.println("Title: " + title);
								titles.add(title);
							}
						}
					}
					//Test if element is a div - hvis true find timestamp
					if(domElement instanceof HtmlDivision) {
						HtmlDivision hd1 = (HtmlDivision) domElement;
						Iterable<DomNode> children2 = hd1.getChildren();
						for (DomNode domNode : children2) {
							if(domNode instanceof HtmlSpan) {
								HtmlSpan hs = (HtmlSpan) domNode;
								Iterable<DomNode> children = hs.getChildren();
								for (DomNode domNode1 : children) {
									if(domNode1 instanceof HtmlSpan) {
										HtmlSpan hs1 = (HtmlSpan) domNode1;
										if (hs1.getAttribute("class").equals("date")) {
											String textContent = hs1.getTextContent();
											earliestPost = parseToDate(textContent);
										}
									}
								}
							}
						
						}
					}
				}
			}
			return new Titles(titles, earliestPost, earliestPostTitle);
		} catch (Exception e) {
			Logger.logToSystemLogAndSystemOut("Failed to index site: " +site+ ". message: " + e.getMessage());
			e.printStackTrace();
			return new Titles(new ArrayList<>(), null, null);
		}
	}

	/*
	 * sample date Sep 7th, 2018, at 8:38 am
	 * - I wondoer how long this format holds :)
	 * 
	 * 2018 Oct 5 3:49 am
	 */
	private Date parseToDate(String textDate) throws ParseException {
		try {
			String[] split = textDate.split(",");
			
			//Parse the month and day
			String dateStr = split[0];//Sep 7th,
			String monthStr = dateStr.substring(0, 3);
			String dayStr = dateStr.substring(4);
			int dayValue = Arrays.asList(date_suffixes).indexOf(dayStr);
			
			//Parse year
			String year = split[1];// 2018
			year = year.trim();
			
			//Parse clock
			String clock = split[2];// at 8:38 am
			clock = clock.replace(" at ", "");
			
			
			
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy MMM dd HH:mm a", Locale.ENGLISH);
		    Date date = sdf.parse(year + " " + monthStr + " " + dayValue + " " + clock);
			return date;
		} catch (Exception e) {
			throw new IllegalStateException("Failed to parse: " + textDate);
		}
	}
}