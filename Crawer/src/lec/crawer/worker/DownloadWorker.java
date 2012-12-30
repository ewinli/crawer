package lec.crawer.worker;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jodd.http.Http;
import jodd.http.HttpTransfer;

import lec.crawer.DownloadManager;
import lec.crawer.ParserFactory;
import lec.crawer.model.HtmlItem;
import lec.crawer.model.UrlItem;
import lec.crawer.parse.IHtmlParser;
import lec.crawer.queue.DownloadHtmlQueue;

public class DownloadWorker implements Runnable {
    public static Logger logger=LoggerFactory.getLogger(DownloadWorker.class);
		public void run() {
			UrlItem item = DownloadHtmlQueue.deQueue();
			int empty=0;
			while (item != null) {
					HtmlItem html;
					try {
						html = download(item);
					if(html==null){
						empty++;
						Thread.sleep(1000);
						if(empty<5)
						continue;						
					}
					empty=0;
					DownloadHtmlQueue.setVisitedItem(item);		
					System.out.println("save:"+item.getUrl());
					
					html.Save();
					
					List<UrlItem> list=html.getUrlList();
					for(UrlItem uitem :list){
					    DownloadHtmlQueue.enQueue(uitem);
					}
					
					Thread.sleep(1000);
					
					item = DownloadHtmlQueue.deQueue();
					if(item==null){
						logger.info("队列为空!");
						System.out.println("队列为空!");
					}
				
					} catch (Exception e) {
						e.printStackTrace();
					}
				}

		}
		public static synchronized HtmlItem download(UrlItem item) throws IOException {
            logger.info("download:"+item.getUrl());
            System.out.println("download:"+item.getUrl());
			HttpTransfer request = Http.createRequest("GET", item.getUrl());
			request.addHeader("User-Agent", DownloadManager.getUserAgent());
			Socket socket = new Socket(request.getHost(), request.getPort());
			OutputStream outputStream = socket.getOutputStream();
			request.send(outputStream);
			InputStream inputStream = socket.getInputStream();
			HttpTransfer response = Http.readResponse(inputStream);
            byte[] body=response.getBody();
            if(body==null){
            	return null;
            }
	        return new HtmlItem(response,item);
		}
}
