package lec.crawer.test;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.junit.*;
import lec.crawer.model.UrlItem;
import lec.crawer.queue.DownloadHtmlQueue;
import lec.crawer.queue.DownloadQueue;

import cn.uc.lec.cache.CacheOperator;

public class DownloadQueueTest {

	@Before
	public void testInit() {
		try {
			CacheOperator.initCache(new String[] { "127.0.0.1:11211" });
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Test
	public  void testBase() throws MalformedURLException {

		Random rand = new Random();
		int count = rand.nextInt(20) + 100;
		int decount = rand.nextInt(20) + 50;

		for (int i = 0; i < count; i++) {
			DownloadQueue.getInstance("html").enQueue(new UrlItem("http://www.baidu.com/" + i));
		}

		for (int i = 0; i < decount; i++) {
			UrlItem item= DownloadQueue.getInstance("html").deQueue();

		}
		
		boolean val = (DownloadQueue.getInstance("html").getWaitingCount() == (count - decount));
		System.out.println("======" + (index++) + "======="
				+ DownloadQueue.getInstance("html").getWaitingCount() + "||"
				+ (DownloadQueue.getInstance("html").getVisitedCount())+"||"+(count - decount));

		if (!val)
			error++;
		System.out.println("error:"+error);
	}

	static int error = 0;
	static int index = 0;

	@Test
	public void testThread() {
		ExecutorService pool = Executors.newFixedThreadPool(20);
		
		
		for (int i = 0; i < 50; i++) {
			
			pool.execute(new Runnable() {

				public void run() {
					try {
						testBase();
					} catch (MalformedURLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			});
		}
		try {
			Thread.sleep(10000);
			Assume.assumeTrue(error == 0);
			System.out.println("error:" + error);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
