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

import lec.crawer.ParserFactory;
import lec.crawer.model.HtmlItem;
import lec.crawer.model.UrlItem;
import lec.crawer.parse.IHtmlParser;
import lec.crawer.queue.DownloadQueue;

public class DownloadWorker implements Runnable {
    public static Logger logger=LoggerFactory.getLogger(DownloadWorker.class);
		public void run() {
			UrlItem item = DownloadQueue.deQueue();
			while (item != null) {
					HtmlItem html;
					try {
						html = download(item);
					logger.info("save:"+item.getUrl());
					html.Save();
					DownloadQueue.setVisitedItem(item);		
					List<UrlItem> list=html.getUrlList();
					for(UrlItem uitem :list){
					    DownloadQueue.enQueue(uitem);
					}
					Thread.sleep(1000);
					item = DownloadQueue.deQueue();
					if(item==null){
						logger.info("队列为空!");
					}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}

		}
		public  synchronized HtmlItem download(UrlItem item) throws IOException {
            logger.info("download:"+item.getUrl());
            
			HttpTransfer request = Http.createRequest("GET", item.getUrl());
			Socket socket = new Socket(request.getHost(), request.getPort());
			OutputStream outputStream = socket.getOutputStream();
			request.send(outputStream);
			InputStream inputStream = socket.getInputStream();
			HttpTransfer response = Http.readResponse(inputStream);
			
	        return new HtmlItem(response,item);
		}
}
