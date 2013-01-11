package lec.crawer.saver;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import jodd.io.FileUtil;
import lec.crawer.DownloadManager;
import lec.crawer.ParserManager;
import lec.crawer.Util;

import lec.crawer.model.HtmlItem;
import lec.crawer.model.HtmlItem.HtmlSaveHandler;
import lec.crawer.parse.IParseResult;

public class HtmlSaveService {
	
	
	public static List<HtmlSaveHandler> getSaveHandlers(){
		return htmlSaveHandlerList;
	}
	
	private static List<HtmlSaveHandler> htmlSaveHandlerList=new ArrayList<HtmlSaveHandler>();
    static{
    	htmlSaveHandlerList.add(new SaveRaw());
    }
	
    public static class SaveRaw implements HtmlSaveHandler{

		public void save(HtmlItem item) throws IOException {
			String host= item.getUrlItem().getUri().getHost();
			String src=DownloadManager.getSourcePath();
			String path=src+"/"+host;
			Util.mkdirs(path);
			FileUtil.writeString(path+"/"+item.getKey()+".html", item.getContent(),item.getEncoding());	
			ParserManager.report("parse",item);
		}
    	
    }
    /*
    
    public static class SaveHtml implements HtmlSaveHandler{

		public void save(HtmlItem item) throws IOException {
			String host= item.getUrlItem().getUri().getHost();
			String src=DownloadManager.getResultPath();
			String path=src+"/html/"+host;
			Util.mkdirs(path);
			IParseResult result= item.getParser().parse();
			FileUtil.writeString(path+"/"+item.getKey()+".html", result.getOutput(),item.getEncoding());		
			
		}
    	
    }
    
    public static class SaveText implements HtmlSaveHandler{

		public void save(HtmlItem item) throws IOException {
			String host= item.getUrlItem().getUri().getHost();
			String src=DownloadManager.getResultPath();
			String path=src+"/text/"+host;
			DownloadManager.mkdirs(path);
			IParseResult result= item.getParser().parse();
			FileUtil.writeString(path+"/"+item.getKey()+".txt", result.getCleanContent(),item.getEncoding());
		}
    	
    }*/
}
