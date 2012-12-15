package lec.crawer.model;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;

import cn.uc.easy.utils.MD5Utils;

import jodd.http.HttpTransfer;
import jodd.io.FileUtil;
import jodd.util.StringUtil;
import lec.crawer.DownloadManager;
import lec.crawer.ParserFactory;
import lec.crawer.parse.IHtmlParser;
import lec.crawer.queue.DownloadQueue;

public class HtmlItem implements IItem {
	private String content;
	private HttpTransfer response;
	private UrlItem urlItem;
    private  IHtmlParser parser;
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
		List<UrlItem> list= parser.getUrlList();
		return list;
	}
	
	public void Save() throws IOException{
		Save(DownloadManager.getSavePath());
	}
	
	
	public void Save(String path) throws IOException{
		String name=urlItem.getKey()+".html";		
	    FileUtil.writeString(path+"/"+name, content, encoding);
	}

	public String getKey() {
		return "html/"+this.urlItem.getKey();
	}
	
}
