package lec.crawer.parse;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import jodd.http.HttpTransfer;
import jodd.jerry.Jerry;
import jodd.jerry.Jerry.JerryParser;
import jodd.lagarto.dom.Node;
import jodd.util.StringUtil;
import lec.crawer.model.UrlItem;

public class CnbetaHtmlParser extends BaseHtmlParser {
	

	public CnbetaHtmlParser(HttpTransfer response, UrlItem item)
			throws UnsupportedEncodingException {
		super(response, item);
	}
    
	private Pattern urlPattern=Pattern.compile(".+?/articles/\\w+\\.(html|htm)");
	public List<UrlItem> getHtmlUrlList() {
		List<UrlItem> list = new ArrayList<UrlItem>();
		Jerry jerry2 = this.getJerry().$("a");
		Node[] nodes = jerry2.get();
		for (Node node : nodes) {
			String href = node.getAttribute("href");
			if(StringUtil.isEmpty(href)) continue;
			if (href.startsWith("/")) {
				href = this.getBaseUrl() + href;
			}
			Matcher matcher= urlPattern.matcher(href);
			if(matcher.matches()&&href.startsWith("http://")){
			  list.add(new UrlItem(href));
			}
		}

		return list;
	}
	public List<UrlItem> getImageUrlList() {
		return null;
	}
	public IParseResult parse() {
	   
		return null;
	}
    




}