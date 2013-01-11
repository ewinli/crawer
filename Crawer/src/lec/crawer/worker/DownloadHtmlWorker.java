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
import jodd.util.StringUtil;
import lec.crawer.DownloadManager;
import lec.crawer.model.HtmlItem;
import lec.crawer.model.UrlItem;
import lec.crawer.queue.DownloadQueue;

public class DownloadHtmlWorker implements Runnable {
    public static Logger logger=LoggerFactory.getLogger(DownloadHtmlWorker.class);
    
	public void run() {
		UrlItem item = DownloadQueue.getInstance("html").deQueue();
		int empty = 0;
		while (item != null) {
				HtmlItem html;
				try {
					html = download(item);
					if (html == null) {
						empty++;
						Thread.sleep(1000);
						if (empty < 3)
							continue;
					}else{
					  empty = 0;
					  DownloadQueue.getInstance("html").setVisitedItem(item);
					  System.out.println("save:" + item.getUrl());
					  html.save();
					}
					Thread.sleep(1000);
					item = DownloadQueue.getInstance("html").deQueue();
					if (item == null) {
						logger.info("html队列为空!");
						System.out.println("html队列为空!");
					}

				} catch (Exception e) {
					e.printStackTrace();
				}
			
		}
		DownloadManager.report("htmlWorkerFinished");

	}
		private synchronized HtmlItem download(UrlItem item) throws IOException {
			HttpTransfer response=getResponse(item);	
			int code=response.getStatusCode();
			HtmlItem html=null;
			if(code==200){
               byte[] body=response.getBody();
               if(body==null){
            	return null;
              }
               html=new HtmlItem(response, item);
            }else if(code>400){
            	return null;
            }else if(code>300&&code<400){
            	String location= response.getHeader("Location");
            	System.out.println("code:"+code);
            	for(int i=0;i<3&&code!=200;i++){
            		if(StringUtil.isEmpty(location)){
            			location=item.getUri().getProtocol()+"://"+item.getUri().getHost();
            		}else if(location.startsWith("/")){
            			location=item.getUri().getProtocol()+"://"+item.getUri().getHost()+location;
            		}
            		item.setUrl(location);
            		response=getResponse(item);            	
            		code=response.getStatusCode();
            	}	
            	if(response!=null&&response.getBody()!=null){           		
            	  html=new HtmlItem(response, item);
            	}else{
            		System.out.println("null body");
            	}
            }
	        return html;
		}
		
		private synchronized  HttpTransfer getResponse(UrlItem item) throws IOException{
			logger.info("download:"+item.getUrl());
            System.out.println("download:"+item.getUrl());
			HttpTransfer request = Http.createRequest("GET", item.getUrl());
			request.addHeader("User-Agent", DownloadManager.getUserAgent());			
			Socket socket = new Socket(request.getHost(), request.getPort());		
			OutputStream outputStream = socket.getOutputStream();
			request.send(outputStream);
			InputStream inputStream = socket.getInputStream();
			HttpTransfer response = Http.readResponse(inputStream);	
			return response;
		}
		
		
		
}
