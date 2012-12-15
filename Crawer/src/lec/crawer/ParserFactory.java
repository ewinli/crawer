package lec.crawer;

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

import lec.crawer.model.HtmlItem;
import lec.crawer.model.UrlItem;
import lec.crawer.parse.IHtmlParser;

public class ParserFactory {
	public static IHtmlParser getHtmlParser(HttpTransfer response, UrlItem item)
			throws UnsupportedEncodingException {
		return new CnbetaHtmlParser(response, item);
	}

	private static class CnbetaHtmlParser implements IHtmlParser {
		private String baseUrl;
		private String content;
		private String contentType;
		private String encoding;
		private JerryParser jerryParser = Jerry.jerry();
		private Jerry jerry;
		private UrlItem item;
		private HttpTransfer response;

		public CnbetaHtmlParser(HttpTransfer response, UrlItem item)
				throws UnsupportedEncodingException {
			this.response = response;
			this.item = item;
			jerryParser = Jerry.jerry();
			jerryParser.getDOMBuilder().setIgnoreComments(true);
			this.encoding = this.getEncoding();
			jerry = jerryParser.parse(new String(response.getBody(),this.encoding));
			this.contentType = this.getContentType();
			this.content = this.getContent();
			this.baseUrl = this.getBaseUrl();
		}
        
		private Pattern urlPattern=Pattern.compile(".+?/articles/\\w+\\.(html|htm)");
		public List<UrlItem> getUrlList() {
			List<UrlItem> list = new ArrayList<UrlItem>();
			Jerry jerry2 = jerry.$("a");
			Node[] nodes = jerry2.get();
			for (Node node : nodes) {
				String href = node.getAttribute("href");
				if(StringUtil.isEmpty(href)) continue;
				if (href.startsWith("/")) {
					href = this.baseUrl + href;
				}
				Matcher matcher= urlPattern.matcher(href);
				if(matcher.matches()&&href.startsWith("http://")){
				  System.out.println(href);
				  list.add(new UrlItem(href));
				}
			}

			return list;
		}

		public String getEncoding() throws UnsupportedEncodingException {
			if (this.encoding != null)
				return this.encoding;
			String contentType = getContentType();
			System.out.println(contentType);			
			String ecode = null;
			if(contentType.indexOf("charset")!=-1){
		       ecode=contentType.toLowerCase().replaceAll(".+?charset=","");
			}
			System.out.println(ecode);
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

		public String getContent() throws UnsupportedEncodingException {
			if (this.content != null)
				return this.content;
			return new String(response.getBody(),this.getEncoding());
		}

		public String getBaseUrl() {
			if (this.baseUrl != null)
				return this.baseUrl;
			String host = item.getUri().getHost();
			int port = item.getUri().getPort();
			String path=item.getUri().getPath();
			
			StringBuilder sb=new StringBuilder();			
			sb.append("http://").append(host);
			if(port!=-1){
				sb.append(":"+port);
			}
			if(StringUtil.isNotEmpty(path)){
				sb.append(path);
			}
			
			return sb.toString();
		}

	}

}
