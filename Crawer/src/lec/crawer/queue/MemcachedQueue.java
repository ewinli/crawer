package lec.crawer.queue;


import java.util.LinkedList;
import java.util.concurrent.ConcurrentLinkedQueue;
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

	private  ConcurrentLinkedQueue<String> keyQueue;

	private int time = 3600 * 24 * 7;

	private Class<T> clazz;

	private ICache cache = CacheOperator.getCache();

	public MemcachedQueue(Class<T> clazz, String key) {
		this.clazz = clazz;
		this.key = key;
		this.itemKey = key + "/item/";

		try{
			System.out.println(key);
		this.keyQueue = cache.get(ConcurrentLinkedQueue.class, key);
		}catch(Exception ex){
		  
		}

		if (this.keyQueue == null) {
			System.out.println("new");
			this.keyQueue = new ConcurrentLinkedQueue<String>();
			cache.safeSet(ConcurrentLinkedQueue.class, key, this.keyQueue, time);
		}
	}

	public T getFirst() {
		String key=keyQueue.peek();
		if(key==null) return null;
		return cache.get(clazz, key);
	}

	

	public T removeFirst() {
	     synchronized (keyQueue) {
			if (keyQueue.isEmpty()) {
			  return null;
			}
			String key = keyQueue.poll();
			if(key==null) return null;
			T item = cache.get(clazz,key);
			cache.safeSet(ConcurrentLinkedQueue.class, this.key, this.keyQueue, time);
			cache.safeDelete(key);
			return item;
	     }
	}

	public void add(T item) {
	     synchronized (keyQueue) {
		    keyQueue.add(itemKey + item.getKey());
			cache.safeSet(ConcurrentLinkedQueue.class, this.key, this.keyQueue, time);
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
