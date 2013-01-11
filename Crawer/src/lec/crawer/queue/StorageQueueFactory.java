package lec.crawer.queue;


import java.util.*;

public class StorageQueueFactory {
	
	private static Map<String,IQueue> Map=new HashMap<String,IQueue>();
	
    public  static <T> IQueue<T> getInstance(Class<T> clazz,String key){
    	if(Map.containsKey(key)){
    		return Map.get(key);
    	}
    	return new MemcachedQueue(clazz,key);   	
    	
    }
}
