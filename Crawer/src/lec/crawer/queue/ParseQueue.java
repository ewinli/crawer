package lec.crawer.queue;



import java.util.HashMap;
import java.util.Map;

import lec.crawer.model.UrlItem;

public class ParseQueue {
	
     private  IQueue<UrlItem> waitingQueue;
     private  IQueue<UrlItem> visitedQueue;
 
     private static Map<String,ParseQueue> instances=new HashMap<String,ParseQueue>();
     static{
    	 instances.put("html", new ParseQueue("crawer/parse/html/waiting", "crawer/parse/html/visited"));
    	 instances.put("rss", new ParseQueue("crawer/parse/rss/waiting", "crawer/parse/rss/visited"));    	 
     }
     
     public static ParseQueue getInstance(String queue){
    	 return instances.get(queue);
     }
     
     private ParseQueue(String waiting,String visited){
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
