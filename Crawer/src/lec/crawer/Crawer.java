package lec.crawer;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import lec.crawer.model.UrlItem;
import lec.crawer.queue.DownloadQueue;
import lec.crawer.worker.DownloadWorker;

import cn.uc.lec.cache.CacheOperator;

public class Crawer {
    public static void main(String[] args) {
		try {
			DownloadManager.setSavePath("D:\\crawer\\dwonload\\");
			CacheOperator.initCache(new String[]{"127.0.0.1:11211"});
			DownloadQueue.enQueue(new UrlItem("http://www.cnbeta.com"));
			ExecutorService pool = Executors.newFixedThreadPool(20);
			for(int i=0;i<20;i++){
				pool.execute(new DownloadWorker());
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
