package lec.crawer.parse;

import java.io.UnsupportedEncodingException;
import java.util.List;

import lec.crawer.model.UrlItem;

public interface IHtmlParser {
	public String getEncoding() throws UnsupportedEncodingException;
	public String getContentType();
	public String getContent() throws UnsupportedEncodingException;
    public List<UrlItem> getUrlList();
}
