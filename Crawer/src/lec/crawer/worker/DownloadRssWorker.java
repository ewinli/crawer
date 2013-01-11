package lec.crawer.worker;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.URL;
import java.net.URLConnection;

import jodd.http.Http;
import jodd.http.HttpTransfer;
import lec.crawer.DownloadManager;
import lec.crawer.model.HtmlItem;
import lec.crawer.model.RssList;
import lec.crawer.model.UrlItem;
import lec.crawer.queue.DownloadQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.io.FeedException;
import com.sun.syndication.io.SyndFeedInput;
import com.sun.syndication.io.XmlReader;

public class DownloadRssWorker  implements Runnable {
    public static Logger logger=LoggerFactory.getLogger(DownloadHtmlWorker.class);
		public void run() {
			UrlItem item = DownloadQueue.getInstance("rss").deQueue();
			while (item != null) {
				try {
					System.out.println("down:"+item.getUrl());
					RssList rss= download(item);
					System.out.println("save:"+item.getUrl());
					rss.save();
		            Thread.sleep(1000);					
					item =  DownloadQueue.getInstance("rss").deQueue();
					if(item==null){
						logger.info("rss队列为空!");
						System.out.println("rss队列为空!");
					}
				} catch (IllegalArgumentException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				} catch (FeedException e) {
					e.printStackTrace();
				}catch(Exception e){
					e.printStackTrace();
				}
			}
		    DownloadManager.report("rssWorkerFinished");
		}
		private synchronized RssList download(UrlItem urlitem) throws IOException, IllegalArgumentException, FeedException {
			URL url=new URL(urlitem.getUrl());			
			URLConnection uc=url.openConnection();
			uc.setRequestProperty("User-Agent", DownloadManager.getUserAgent());
			uc.setConnectTimeout(5000);
		    SyndFeedInput input=new SyndFeedInput();
		    XmlReader reader=new XmlReader(uc);
		    SyndFeed feed=input.build(reader);
            RssList item=new RssList(urlitem,feed,reader);            
            return item;
		}
}
