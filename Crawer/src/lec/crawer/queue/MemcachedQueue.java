package lec.crawer.queue;


import java.util.LinkedList;
import java.util.concurrent.LinkedBlockingQueue;

import jodd.util.StringUtil;
import lec.crawer.model.IItem;
import cn.uc.lec.cache.CacheOperator;
import cn.uc.lec.cache.ICache;
import org.slf4j.*;

public class MemcachedQueue<T extends IItem> implements IQueue<T> {

	private Logger logger = LoggerFactory.getLogger(MemcachedQueue.class);

	private String key;

	private String itemKey;

	private  LinkedList<String> keyQueue;

	private int time = 3600 * 24 * 7;

	private Class<T> clazz;

	private ICache cache = CacheOperator.getCache();

	public MemcachedQueue(Class<T> clazz, String key) {
		this.clazz = clazz;
		this.key = key;
		this.itemKey = key + "/item/";

		try{
			System.out.println(key);
		this.keyQueue = cache.get(LinkedList.class, key);
		}catch(Exception ex){
		  
		}

		if (this.keyQueue == null) {
			this.keyQueue = new LinkedList<String>();
			cache.safeSet(LinkedList.class, key, this.keyQueue, time);
		}
	}

	public T getFirst() {
		String key=keyQueue.getFirst();
		if(key==null) return null;
		return cache.get(clazz, key);
	}

	

	public T removeFirst() {
	     synchronized (keyQueue) {
			if (keyQueue.isEmpty()) {
			  return null;
			}
			String key = keyQueue.removeFirst();
			if(key==null) return null;
			T item = cache.get(clazz,key);
			cache.safeDelete(key);
			return item;
	     }
	}

	public void add(T item) {
	     synchronized (keyQueue) {
		    keyQueue.add(itemKey + item.getKey());
		    cache.safeSet(clazz, itemKey + item.getKey(), item, time);
	     }
	}

	public long getCount() {
		return (long) keyQueue.size();
	}

	public boolean contains(T item) {
		return keyQueue.contains(itemKey + item.getKey());
	}

}
