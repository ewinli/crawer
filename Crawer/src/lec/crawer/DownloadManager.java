package lec.crawer;

import java.io.*;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import jodd.io.FileUtil;
import lec.crawer.model.HtmlItem;
import lec.crawer.model.UrlItem;
import lec.crawer.model.HtmlItem.HtmlSaveHandler;
import lec.crawer.model.RssList.RssSaveHandler;
import lec.crawer.parse.IParseResult;
import lec.crawer.queue.DownloadQueue;
import lec.crawer.saver.HtmlSaveService;
import lec.crawer.saver.RssSaveService;
import lec.crawer.worker.DownloadHtmlWorker;
import lec.crawer.worker.DownloadRssWorker;

import cn.uc.lec.cache.CacheOperator;

public class DownloadManager {
    private static String sourcePath;
    private static String userAgent=UserAgent.IPad.getUa();
	private static ExecutorService htmlPool = Executors.newFixedThreadPool(20);
	private static ExecutorService rssPool = Executors.newFixedThreadPool(5);
	private static ScheduledExecutorService scheduledService=Executors.newScheduledThreadPool(1);	
	private static Set<UrlItem> urlInit=new HashSet<UrlItem>();	
	//declare the variable as static volatile and this will force the thread to read each time the global value
	private static volatile int htmlWorker=0;	
	private static volatile int rssWorker=0;
    public synchronized static void report(String msg){
    	
    	
    	if("htmlQueueReady".equals(msg)&&htmlWorker<20){
    		htmlPool.execute(new DownloadHtmlWorker());
    		  htmlWorker++;
    	}
    	else if("rssQueueReady".equals(msg)&&rssWorker<5){
    		rssPool.execute(new DownloadRssWorker());
    		  rssWorker++;
    	}else if("htmlWorkerFinished".equals(msg)&&htmlWorker>0){
    		  htmlWorker--;
    	}else if("rssWorkerFinished".equals(msg)&&rssWorker>0){
    		  rssWorker--;
    	}
    	
    	
    	
    	System.out.println(Thread.currentThread().getName()+":"+msg);
    	
    }
    
    public static void run(long initialDelay,long period){
    	scheduledService.scheduleAtFixedRate(new Runnable() {		
			public void run() {				
				for(UrlItem item:urlInit){
				    DownloadQueue.getInstance(item.getType()).enQueue(item);
				    DownloadManager.report(item.getType()+"QueueReady");
				}
			}
		}, initialDelay, period, TimeUnit.MILLISECONDS);
    }
    
    public static void addInitUrl(String url,String type) throws MalformedURLException{
    	UrlItem urlitem=new UrlItem(url);
    	urlitem.setType(type);
    	urlInit.add(urlitem);
    }
    
    public static String getUserAgent() {
		return userAgent;
	}

	public static void setUserAgent(String userAgent) {
		DownloadManager.userAgent = userAgent;
	}



	public static void setSource(String path) throws IOException{
    	sourcePath=path;
        Util.mkdirs(path);
    }
	

    
    public static String getSourcePath(){
    	return sourcePath;
    }
    
    public static List<RssSaveHandler> getRssSaveHandlers(){
    	return RssSaveService.getSaveHandlers();
    }
    
 
    
    public static List<HtmlSaveHandler> getHtmlSaveHandlers(){
   	  return HtmlSaveService.getSaveHandlers();
    }
    



}
