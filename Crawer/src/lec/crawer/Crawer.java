package lec.crawer;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.slf4j.LoggerFactory;

import lec.crawer.model.UrlItem;
import lec.crawer.queue.DownloadHtmlQueue;
import lec.crawer.worker.DownloadWorker;

import cn.uc.lec.cache.CacheOperator;

public class Crawer {
    public static void main(String[] args) {
		try {
			
			DownloadManager.setSource("D:\\crawer\\dwonload\\");
			DownloadManager.setResultPath("D:\\crawer\\result\\");
			CacheOperator.initCache(new String[]{"127.0.0.1:11211"});
			DownloadHtmlQueue.enQueue(new UrlItem("http://www.cnbeta.com/articles/219473.htm"));

			ExecutorService pool = Executors.newFixedThreadPool(20);
			for(int i=0;i<20;i++){
				pool.execute(new DownloadWorker());
			}
	
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
