package lec.crawer;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.slf4j.LoggerFactory;

import lec.crawer.model.UrlItem;

import lec.crawer.queue.DownloadQueue;
import lec.crawer.worker.DownloadHtmlWorker;
import lec.crawer.worker.DownloadRssWorker;

import cn.uc.lec.cache.CacheOperator;

public class Crawer {
    public static void main(String[] args) {
		try {
			
			DownloadManager.setSource("D:\\crawer\\dwonload\\");
			CacheOperator.initCache(new String[]{"127.0.0.1:11211"});
			String[] rsses=new String[]{
					"http://cnbeta.feedsportal.com/c/34306/f/624776/index.rss",
					"http://www.cnbeta.com/commentrss.php"
			};
			for(String rss:rsses){		
			  DownloadManager.addInitUrl(rss, "rss");		
			}
			
			DownloadManager.run(1000,10*6000);						
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
