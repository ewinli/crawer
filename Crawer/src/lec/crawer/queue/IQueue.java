package lec.crawer.queue;

import java.io.Serializable;

public interface IQueue<T> extends Serializable {

 	public boolean contains(T item);
 	
 	public T getFirst();
 		
	public T removeFirst();
	
	public void add(T item);
	
	public long getCount();
	
}
