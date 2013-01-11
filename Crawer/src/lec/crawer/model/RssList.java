package lec.crawer.model;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;

import lec.crawer.DownloadManager;
import lec.crawer.queue.DownloadQueue;
import lec.crawer.worker.DownloadHtmlWorker;

import com.sun.syndication.feed.synd.SyndEntry;
import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.io.XmlReader;

public class RssList implements IItem{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public static interface RssSaveHandler{
		public void save(RssList item) throws MalformedURLException ;
	}
	
	private UrlItem url;
	private SyndFeed feed;
	private String  encoding;
    private static List<RssSaveHandler> saveHandlers=DownloadManager.getRssSaveHandlers(); 

	public RssList(UrlItem item,SyndFeed feed,XmlReader reader) {
       this.url=item;
       this.feed=feed;
       this.encoding=reader.getEncoding();
	}

	public String getEncoding(){
		return encoding;
	}
	
	public List<SyndEntry> getEntries(){
		return this.feed.getEntries();
	}
	

	
	public String getKey() {
		return url.getKey();
	}
	
	
	public void save() throws IOException{
		
	    for(RssSaveHandler handler:saveHandlers){
	    	handler.save(this);
	    }
	}
	
	public UrlItem getUrl() {
		return url;
	}

	public SyndFeed getFeed() {
		return feed;
	}


}
