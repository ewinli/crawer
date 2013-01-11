package demo1;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class DemoThread {
  public static void main(String[] args) {
	  System.out.println("111 ");
	  ExecutorService pool = Executors.newFixedThreadPool(20);
	  for(int i=0;i<20;i++){
		  pool.submit(new Runnable() {
			
			public void run() {
				// TODO Auto-generated method stub
			
				try {
					Thread.sleep(1000);
					  ExecutorService pool2 = Executors.newFixedThreadPool(20);
					  pool2.submit(new Runnable() {
						
						public void run() {
							try {
								Thread.sleep(10000);
								
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
							System.out.println("hello[2] "+Thread.currentThread());
							
						}
					});
					  pool2.shutdown();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				System.out.println("hello "+Thread.currentThread());
			}
		});
	  }
	  pool.shutdown();
	 while(!pool.isTerminated());
	  System.out.println("ttt ");
  }
}
