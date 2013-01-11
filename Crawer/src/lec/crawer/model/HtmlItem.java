package lec.crawer.model;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;

import cn.uc.easy.utils.MD5Utils;

import jodd.http.HttpTransfer;
import jodd.io.FileUtil;
import jodd.util.StringUtil;
import jodd.util.URLCoder;
import lec.crawer.DownloadManager;
import lec.crawer.ParserManager;
import lec.crawer.parse.IHtmlParser;
import lec.crawer.parse.IParseResult;


public class HtmlItem implements IItem {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public static interface HtmlSaveHandler{
		void save(HtmlItem item) throws IOException;
	}

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
        parser= ParserManager.getHtmlParser(response, item);
		this.contentType=parser.getContentType(); 
        this.encoding=parser.getEncoding();
        this.content=parser.getContent();
	}
    
	public HtmlItem(String content,String encoding,String contentType){
		this.content=content;
		this.contentType=contentType;
		this.encoding=encoding;
	}
	public HtmlItem(String content,String encoding){
		this(content,encoding,"text/html");
	}
	
    

	
	public void save() throws IOException{
	   List<HtmlSaveHandler> handlers=DownloadManager.getHtmlSaveHandlers();
	   for(HtmlSaveHandler handler:handlers){
		   handler.save(this);
	   }
	}
	
	
	public IHtmlParser getParser() {
		return parser;
	}

	public String getKey() {
		return this.urlItem.getKey();
	}


	
}
