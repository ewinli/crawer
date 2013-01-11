package lec.crawer.saver;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;

import com.sun.syndication.feed.synd.SyndEntry;

import lec.crawer.DownloadManager;
import lec.crawer.model.RssItem;
import lec.crawer.model.RssList;
import lec.crawer.model.UrlItem;
import lec.crawer.model.RssList.RssSaveHandler;
import lec.crawer.queue.DownloadQueue;

public class RssSaveService {
	
    private static List<RssSaveHandler> rssSaveHandlerList=new ArrayList<RssSaveHandler>();
	static {
		rssSaveHandlerList.add(new SaveUrl());
	}
	   
	public static List<RssSaveHandler> getSaveHandlers(){
		return rssSaveHandlerList;
	}
	
	
   public static class SaveUrl implements RssSaveHandler{

    	public void save(RssList source) throws MalformedURLException {
		 List<SyndEntry> ents=source.getEntries();
		  for(SyndEntry sync:ents){
			 String link=sync.getLink();
			 DownloadQueue.getInstance("html").enQueue(new RssItem(link,source,sync));
		  }		
	      System.out.println( "wc:"+DownloadQueue.getInstance("html").getWaitingCount());
		  DownloadManager.report("htmlQueueReady");
	  }
	   
   }
         
}
