package lec.crawer.parse;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.util.List;

import lec.crawer.model.UrlItem;

public interface IHtmlParser {
	
	public String getEncoding() throws UnsupportedEncodingException;
	
	public String getContentType();
	
	public String getContent() throws UnsupportedEncodingException;
	
    public List<UrlItem> getHtmlUrlList();
    
    public List<UrlItem> getImageUrlList();
    
    public IParseResult parse() throws MalformedURLException;
}
