package lec.crawer.queue;



import java.util.HashMap;
import java.util.Map;

import lec.crawer.model.UrlItem;

public class DownloadQueue {
	
     private  IQueue<UrlItem> waitingQueue;
     private  IQueue<UrlItem> visitedQueue;
 
     private static Map<String,DownloadQueue> instances=new HashMap<String,DownloadQueue>();
     static{
    	 instances.put("html", new DownloadQueue("crawer/download/html/waiting", "crawer/download/html/visited"));
    	 instances.put("rss", new DownloadQueue("crawer/download/rss/waiting", "crawer/download/rss/visited"));    	 
     }
     
     public static DownloadQueue getInstance(String queue){
    	 return instances.get(queue);
     }
     
     private DownloadQueue(String waiting,String visited){
    	 waitingQueue=StorageQueueFactory.getInstance(UrlItem.class,waiting);
    	 visitedQueue=StorageQueueFactory.getInstance(UrlItem.class,visited);
     }
     
     public synchronized void enQueue(UrlItem item){
    	 if(!waitingQueue.contains(item)&&!visitedQueue.contains(item)){
    	      waitingQueue.add(item);
    	  }
     }
     
     public  UrlItem getHead(){
    	return  waitingQueue.getFirst();
     }
          
     public synchronized UrlItem deQueue(){
    	 return waitingQueue.removeFirst();
     }
     
     public  void setVisitedItem(UrlItem item){
    	 visitedQueue.add(item);
     }
       
     public boolean isVisited(UrlItem item){
    	 return visitedQueue.contains(item);   	 
     }
     
     public  long getWaitingCount(){
    	return waitingQueue.getCount();    	 
     }
     
     public  long getVisitedCount(){
     	return visitedQueue.getCount();    	 
      }
     
}
