package lec.crawer.worker;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;

import lec.crawer.ParserManager;
import lec.crawer.model.HtmlItem;
import lec.crawer.parse.IHtmlParser;
import lec.crawer.parse.IParseResult;
import lec.crawer.parse.SmartHtmlParser;

public class ParserHtmlWorker implements Runnable{

	private final HtmlItem item;
	public ParserHtmlWorker(HtmlItem item){
		this.item=item;
	}
		
	public void run() {
	      try {
			IHtmlParser parser= ParserManager.getHtmlParser(item);
			IParseResult result= parser.parse();
	        result.getImageList();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}  
		
	}

}
