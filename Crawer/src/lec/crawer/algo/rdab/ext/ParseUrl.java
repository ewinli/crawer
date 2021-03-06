package lec.crawer.algo.rdab.ext;

import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import jodd.jerry.Jerry;
import jodd.lagarto.dom.Node;
import jodd.util.StringUtil;

import lec.crawer.Util;
import lec.crawer.algo.ScoredElement;
import lec.crawer.algo.UrlClassifier;
import lec.crawer.algo.rdab.ReadAbilityConfig;
import lec.crawer.algo.rdab.ReadAbilityUtil;
import lec.crawer.model.UrlItem;

public class ParseUrl implements IParseExt<UrlItem> {

	private UrlItem currentUrl;
	private Jerry document;
	private String type;
	private static Map<String, Pattern> pageTypePattern = new HashMap<String, Pattern>();
	static {
		pageTypePattern.put("nextLink", ReadAbilityConfig.getNextLink());
		pageTypePattern.put("nextText", ReadAbilityConfig.getNextText());
		pageTypePattern.put("provText", ReadAbilityConfig.getPrevText());
		pageTypePattern.put("provLink", ReadAbilityConfig.getPrevLink());
		pageTypePattern.put("relateLink", ReadAbilityConfig.getNextStoryLink());
		pageTypePattern.put("relatePositive",
				ReadAbilityConfig.getPositiveWeightRegex());
		pageTypePattern.put("relateText", ReadAbilityConfig.getRelateText());
	}
    
	 private static final ThreadLocal<ParseUrl> holder=new ThreadLocal<ParseUrl>();
		
	 public static ParseUrl getInstance(Jerry document,UrlItem currentUrl,String type){
		 ParseUrl parser=holder.get();
		 if(parser==null){
			 parser=new ParseUrl(document, currentUrl,type);
			 holder.set(parser);
		 }
		 parser.document=document;
		 parser.currentUrl=currentUrl;
		 parser.type=type;
		 return parser;
	 }
	
	private ParseUrl(Jerry document,UrlItem currentUrl,String type){
		this.document=document;
		this.currentUrl=currentUrl;
		this.type=type;
	}
	 
	/**
	 * 查找页面url
	 * @throws MalformedURLException 
	 * */
	public UrlItem parse() throws MalformedURLException  {
		UrlItem nextPageUrl = null;
		Node[] allLink = document.$("a").get();
		if (allLink == null || allLink.length == 0) {
			return nextPageUrl;
		}
		Map<String, ScoredElement<UrlItem>> possibleLinks = getPossiblePageUrls(
				allLink, type);
		nextPageUrl = new ScoredElement<UrlItem>(0, nextPageUrl).getTopElement(
				possibleLinks.values(), 0);

		return nextPageUrl;
	}

	public Map<String, ScoredElement<UrlItem>> getPossiblePageUrls(
			Node[] allLink, String type) throws MalformedURLException {
		Map<String, ScoredElement<UrlItem>> possibleLinks = new HashMap<String, ScoredElement<UrlItem>>();

		for (Node link : allLink) {

			String href = link.getAttribute("href");
			if (StringUtil.isEmpty(href)) {
				continue;
			}
			href = href.replaceAll("#.*$", "");
			href = href.replaceAll("/$", "");
			href = Util.getAbsouluteUrl(href,currentUrl);
			if (currentUrl.getUrl().equals(href)) {
				continue;
			}
			String base = "://" + currentUrl.getUri().getHost();
			if (href.indexOf(base) == -1) {
				continue;
			}

			int score = getPageUrlScore(href, link, type);
			UrlItem url = new UrlItem(href);
			ScoredElement<UrlItem> element = new ScoredElement<UrlItem>(score,
					url);
			possibleLinks.put(url.getKey(), element);
		}
		return possibleLinks;
	}

	private int getNextOrProvPageScore(String href, Node link, String type) {
		int score = 0;
		String[] types = new String[] { "next", "prov" };

		for (String pageType : types) {
			if (ReadAbilityUtil.match(pageTypePattern.get(pageType + "Link"),
					href)) {
				if (pageType.equals(type))
					score += 25;
				else
					score -= 25;
				// System.out.println(link.getTextContent()+"[1] "+score);
			}

			if (link.getTextContent() == null) {
				score -= 50;
			}
			if (ReadAbilityUtil.match(pageTypePattern.get(pageType + "Text"),
					link.getTextContent())) {
				if (pageType.equals(type))
					score += 50;
				else
					score -= 50;
				// System.out.println(link.getTextContent()+"[2] "+score);
			}

			Node parent = link.getParentNode();
			if (parent != null && parent.getChildElementsCount() == 1) {
				if (ReadAbilityUtil.match(
						pageTypePattern.get(pageType + "Text"),
						parent.getTextContent())) {
					if (pageType.equals(type))
						score += 50;
					else
						score -= 50;
				}
			}
		}
		// System.out.println(link.getTextContent()+" "+score);
		return score;
	}



	private int getPageUrlScore(String href, Node link, String type) {

		if ("next".equals(type) || "prov".equals(type))
			return getNextOrProvPageScore(href, link, type);
		return getPossiblePageScore(href, link, type);
	}

	private int getPossiblePageScore(String href, Node link, String type) {
		int score = 0;

		try {
			try {
				return (int) UrlClassifier
						.getUrlSimScore(currentUrl.getUrl(), href);
			} catch (MalformedURLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return 0;
	}
}
