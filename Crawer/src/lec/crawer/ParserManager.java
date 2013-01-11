package lec.crawer;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import jodd.http.HttpTransfer;
import lec.crawer.model.HtmlItem;
import lec.crawer.model.IItem;
import lec.crawer.model.UrlItem;
import lec.crawer.parse.IHtmlParser;
import lec.crawer.parse.SimpleHtmlParser;
import lec.crawer.parse.SmartHtmlParser;
import lec.crawer.worker.ParserHtmlWorker;

public class ParserManager {
	
	private static ExecutorService htmlParsePool = Executors.newFixedThreadPool(20);
    private static String resultPath;
    
	public static String getResultPath() {
		return resultPath;
	}

	public static void setResultPath(String result) throws IOException {
		resultPath = result;
	    Util.mkdirs(result);
	}
	
	public static IHtmlParser getHtmlParser(HtmlItem item)
			throws UnsupportedEncodingException{
		return new SmartHtmlParser(item);
	}
	
	public static IHtmlParser getHtmlParser(HttpTransfer response, UrlItem item)
			throws UnsupportedEncodingException {
		return new SimpleHtmlParser(response, item);
	}
	
	public static void report(String msg,IItem item){	
	   if("parse".equals(msg)){	
	     if(item instanceof HtmlItem){
		   htmlParsePool.execute(new ParserHtmlWorker((HtmlItem)item));
	     }	   
	   }
	}
	
}
