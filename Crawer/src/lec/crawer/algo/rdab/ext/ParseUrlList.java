package lec.crawer.algo.rdab.ext;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import jodd.jerry.Jerry;
import jodd.lagarto.dom.Node;

import lec.crawer.algo.ScoredElement;
import lec.crawer.algo.rdab.ReadAbilityConfig;
import lec.crawer.model.UrlItem;

public class ParseUrlList implements IParseExt<List<UrlItem>> {

	private int relateUrlCount=3;
	
	private ParseUrl parseBase;
	
	private UrlItem currentUrl;
	
	private Jerry document;
	
	private String type;
	
	private final static ThreadLocal<ParseUrlList> holder=new ThreadLocal<ParseUrlList>();
	
	 public static ParseUrlList getInstance(Jerry document,UrlItem currentUrl,String type){
		 ParseUrlList parser= holder.get();
		 if(parser==null){
			 parser=new ParseUrlList(document, currentUrl,type);
			 holder.set(parser);
		 }
		 parser.document=document;
		 parser.currentUrl=currentUrl;
		 parser.type=type;
		 return parser;
	 }
	
	 private ParseUrlList(Jerry document,UrlItem currentUrl,String type){
		 this.document=document;
		 this.currentUrl=currentUrl;
		 this.type=type;
		 parseBase=ParseUrl.getInstance(document, currentUrl, type);
	 }
	 
	/**
	 * 查找页面url列表
	 * */
	public List<UrlItem> parse() {
		List<UrlItem> result = new ArrayList<UrlItem>();
		Node[] allLink = document.$("a").get();
		if (allLink == null || allLink.length == 0) {
			return result;
		}
		Map<String, ScoredElement<UrlItem>> possibleLinks = parseBase
				.getPossiblePageUrls(allLink, type);
		result = new ScoredElement<UrlItem>(0, null).getTopElements(
				possibleLinks.values(), ReadAbilityConfig.getMinReleateScore(),
				relateUrlCount);
		return result;
	}
	
	
	

	

	
	
}
