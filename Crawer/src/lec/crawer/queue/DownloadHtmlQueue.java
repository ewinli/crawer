package lec.crawer.queue;



import lec.crawer.model.UrlItem;

public class DownloadHtmlQueue {
	/*
     private static IQueue<UrlItem> waitingQueue=StorageQueueFactory.getInstance(UrlItem.class,"crawer/download/html/waiting");
     private static IQueue<UrlItem> visitedQueue=StorageQueueFactory.getInstance(UrlItem.class,"crawer/download/html/visited");
 
     public synchronized static void enQueue(UrlItem item){
    	 if(!waitingQueue.contains(item)&&!visitedQueue.contains(item))
    	    waitingQueue.add(item);
     }
     
     public synchronized static UrlItem getHead(){
    	return  waitingQueue.getFirst();
     }
          
     public synchronized static UrlItem deQueue(){
    	 return waitingQueue.removeFirst();
     }
     
     public synchronized static void setVisitedItem(UrlItem item){
    	 visitedQueue.add(item);
     }
       
     public synchronized static boolean isVisited(UrlItem item){
    	 return visitedQueue.contains(item);   	 
     }
     
     public synchronized static long getWaitingCount(){
    	return waitingQueue.getCount();    	 
     }
     
     public synchronized static long getVisitedCount(){
     	return visitedQueue.getCount();    	 
      }
     */
}
