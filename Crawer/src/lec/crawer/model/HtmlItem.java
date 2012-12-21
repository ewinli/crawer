package lec.crawer.model;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;

import cn.uc.easy.utils.MD5Utils;

import jodd.http.HttpTransfer;
import jodd.io.FileUtil;
import jodd.util.StringUtil;
import jodd.util.URLCoder;
import lec.crawer.DownloadManager;
import lec.crawer.ParserFactory;
import lec.crawer.parse.IHtmlParser;
import lec.crawer.parse.IParseResult;
import lec.crawer.queue.DownloadHtmlQueue;

public class HtmlItem implements IItem {
	private String content;
	private HttpTransfer response;
	private UrlItem urlItem;
    private IHtmlParser parser;
    private String encoding;
    private String contentType;
	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getContentType() {
		return contentType;
	}

	public void setContentType(String contentType) {
		this.contentType = contentType;
	}

	public String getEncoding() {
		return encoding;
	}

	public void setEncoding(String encoding) {
		this.encoding = encoding;
	}



	public HttpTransfer getResponse() {
		return response;
	}

	public void setResponse(HttpTransfer response) {
		this.response = response;
	}

	public UrlItem getUrlItem() {
		return urlItem;
	}

	public void setUrlItem(UrlItem urlItem) {
		this.urlItem = urlItem;
	}

	public HtmlItem(HttpTransfer response,UrlItem item) throws UnsupportedEncodingException{
		this.urlItem=item;
		this.response=response;
        parser= ParserFactory.getHtmlParser( response, item);
		this.contentType=parser.getContentType(); 
        this.encoding=parser.getEncoding();
        this.content=parser.getContent();
	}

	
	public List<UrlItem> getUrlList(){
		List<UrlItem> list= parser.getHtmlUrlList();
		return list;
	}
	
	public void Save() throws IOException{
		Save(DownloadManager.getSourcePath(),DownloadManager.getResultPath());
	}
	
	
	public void Save(String sourcePath,String resultPath) throws IOException{
		IParseResult result=parser.parse();
		if(result!=null){
		 String name= URLCoder.encodePath( result.getTitle());	
		  name=name.replace("/","");
		  if(name.length()>10){
			  name=name.substring(0,10);
		  }
	     FileUtil.writeString(sourcePath+"/"+name+".html", content, encoding); 
	     FileUtil.writeString(resultPath+"/"+name+".html", result.getOutput(), encoding);
	    }
	}

	public String getKey() {
		return "html/"+this.urlItem.getKey();
	}
	
}
