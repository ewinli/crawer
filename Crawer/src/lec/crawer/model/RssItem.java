package lec.crawer.model;

import java.net.MalformedURLException;
import java.util.Date;

import com.sun.syndication.feed.synd.SyndEntry;


public class RssItem extends UrlItem{

	private static final long serialVersionUID = 1L;
	private SyndEntry entry;
	private RssList source;
	public RssItem(String url,RssList source,SyndEntry entry) throws MalformedURLException {
		super(url);		
		this.entry=entry;
		this.source=source;
	}
    
	public String getTitle(){
		return entry.getTitle();
	}
	
	public String getLink(){
		return entry.getLink();
	}
	
	public String getDescription(){
		return entry.getDescription().getValue();
	}
	
	public String getAuthor(){
		return entry.getAuthor();
	}
	
	public Date getPublishedDate(){		
		return entry.getPublishedDate();
	}
	
	public RssList getSoruce(){
	    return source;
	}
	
	

}
