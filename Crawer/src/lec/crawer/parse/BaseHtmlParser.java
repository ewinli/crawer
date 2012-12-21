package lec.crawer.parse;

import java.io.UnsupportedEncodingException;

import jodd.http.HttpTransfer;
import jodd.jerry.Jerry;
import jodd.jerry.Jerry.JerryParser;
import jodd.lagarto.dom.Node;
import jodd.util.StringUtil;
import lec.crawer.model.UrlItem;

public abstract class BaseHtmlParser implements IHtmlParser{
	private String baseUrl;
	private String content;
	private String contentType;
	private String encoding;
	private JerryParser jerryParser = Jerry.jerry();
	private Jerry jerry;

	private UrlItem item;
	private HttpTransfer response;
	
	public BaseHtmlParser(HttpTransfer response, UrlItem item)
			throws UnsupportedEncodingException {
		this.response = response;
		this.item = item;
		jerryParser = Jerry.jerry();
		jerryParser.getDOMBuilder().setIgnoreWhitespacesBetweenTags(true);
		jerryParser.getDOMBuilder().setEnableConditionalComments(true);
		jerryParser.getDOMBuilder().setIgnoreComments(true);
		this.encoding = this.getEncoding();
		jerry = jerryParser.parse(new String(response.getBody(),this.encoding));
		this.contentType = this.getContentType();
		this.content = this.getContent();
		this.baseUrl = this.getBaseUrl();
	}
	
	public String getContent() throws UnsupportedEncodingException {
		if (this.content != null)
			return this.content;
		return new String(response.getBody(),this.getEncoding());
	}

	public Jerry getJerry() {
		return jerry;
	}

	public String getBaseUrl() {
		if (this.baseUrl != null)
			return this.baseUrl;
		String host = item.getUri().getHost();
		int port = item.getUri().getPort();
		String path=item.getUri().getPath();
		String protocol=item.getUri().getScheme();
		StringBuilder sb=new StringBuilder();			
		if(StringUtil.isEmpty(protocol)) protocol="http";
		sb.append(protocol+"://").append(host);
		if(port!=-1){
			sb.append(":"+port);
		}
		if(StringUtil.isNotEmpty(path)){
			sb.append(path);
		}
		
		return sb.toString();
	}
	
	public String getEncoding() throws UnsupportedEncodingException {
		if (this.encoding != null)
			return this.encoding;
		String contentType = getContentType();
	
		String ecode = null;
		if(contentType.indexOf("charset")!=-1){
	       ecode=contentType.toLowerCase().replaceAll(".+?charset=","");
		}
		System.out.println(">>>>>>>>>>>>>ec:"+ecode);
		if (StringUtil.isNotEmpty(ecode)) {
			return ecode;
		}
		
		Jerry jerry = jerryParser.parse(new String(response.getBody(),"utf-8"));
		Jerry jerrymeta = jerry.$("meta");
		Node[] nodes = jerrymeta.get();
		for (Node node : nodes) {
			String content = node.getAttribute("content");
			if (!StringUtil.isEmpty(content)&&content.toLowerCase().indexOf("charset")!=-1) {
				ecode = content.toLowerCase().replaceAll(".+?charset=", "");
				break;
			}
		}
		System.out.println(">>>>>>>>>>>>>ec:"+ecode);
		if (StringUtil.isEmpty(ecode))
			return "utf-8";
		
		
		return ecode;
	}

	public String getContentType() {
		if (this.contentType != null)
			return this.contentType;
		String ctype = response.getHeader("Content-Type");
		if (ctype == null) {
			return "text/html";
		}
		return ctype.toLowerCase();
	}

}
