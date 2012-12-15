package lec.crawer.test;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.junit.*;
import lec.crawer.model.UrlItem;
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
	public  void testBase() {

		Random rand = new Random();
		int count = rand.nextInt(20) + 100;
		int decount = rand.nextInt(20) + 50;

		for (int i = 0; i < count; i++) {
			DownloadQueue.enQueue(new UrlItem("http://www.baidu.com/" + i));
		}

		for (int i = 0; i < decount; i++) {
			UrlItem item= DownloadQueue.deQueue();

		}
		
		boolean val = (DownloadQueue.getWaitingCount() == (count - decount));
		System.out.println("======" + (index++) + "======="
				+ DownloadQueue.getWaitingCount() + "||"
				+ (DownloadQueue.getVisitedCount())+"||"+(count - decount));

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
					testBase();
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
