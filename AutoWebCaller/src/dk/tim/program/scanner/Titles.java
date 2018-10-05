package dk.tim.program.scanner;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Titles {
	private List<String> titles = new ArrayList<String>();
	private Date earliestPost;
	private String earliestPostTitle;
	
	public Titles(List<String> titles, Date earliestPost, String earliestPostTitle) {
		this.titles = titles;
		this.earliestPost = earliestPost;
		this.earliestPostTitle = earliestPostTitle;
	}
	public List<String> getTitles() {
		return titles;
	}
	public void setTitles(List<String> titles) {
		this.titles = titles;
	}
	public Date getEarliestPost() {
		return earliestPost;
	}
	public void setEarliestPost(Date earliestPost) {
		this.earliestPost = earliestPost;
	}
	
	public boolean isEarliestPostOneHourOlderThanNow(Date d) {
		ZonedDateTime oneHourAgo = d.toInstant().atZone(ZoneId.systemDefault()).minusHours(1);
		ZonedDateTime post = earliestPost.toInstant().atZone(ZoneId.systemDefault());
		System.out.println("Comparing one hour ago ("+oneHourAgo+") with ("+earliestPostTitle+"-"+post+"). Got: " + oneHourAgo.isBefore(post));
		return oneHourAgo.isAfter(post);
	}
	
	@Override
	public String toString() {
		return String.format("Titles [titles=%s, earliestPost=%s]", titles, earliestPost);
	}
	
}
