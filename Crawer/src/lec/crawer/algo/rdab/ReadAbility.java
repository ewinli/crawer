package lec.crawer.algo.rdab;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import cn.uc.lec.dom.NodeUtil;
import jodd.jerry.Jerry;
import jodd.jerry.Jerry.JerryParser;
import jodd.lagarto.dom.Node;
import jodd.lagarto.dom.Node.NodeType;
import jodd.util.StringUtil;
import lec.crawer.algo.ChildNodesTraverser;
import lec.crawer.algo.ElementsTraverser;
import lec.crawer.algo.IAction;
import lec.crawer.algo.ScoredElement;
import lec.crawer.algo.UrlClassifier;
import lec.crawer.algo.rdab.ext.*;
import lec.crawer.model.UrlItem;
import lec.crawer.parse.IParseResult;

public class ReadAbility{

    private final String content;
	
    private final UrlItem currentUrl;
	
    private int relateUrlCount=3;
        	
	public ReadAbility(String content,UrlItem url){

		this.content=content;
		this.currentUrl=url;		
	}
	
	public IParseResult getResult(){
		IParseResult result=new ReadAbilityResult();
		JerryParser jerryParser = Jerry.jerry();	
		jerryParser.getDOMBuilder().setEnableConditionalComments(false);
		jerryParser.getDOMBuilder().setIgnoreWhitespacesBetweenTags(true);
		jerryParser.getDOMBuilder().setIgnoreComments(true);
		Jerry document= jerryParser.parse(content);
		
		prepareDocument(document);		
		
		UrlItem nextPageUrl=ParseUrl.getInstance(document, currentUrl, "next").parse();
		
		UrlItem provPageUrl=ParseUrl.getInstance(document,currentUrl, "prov").parse();
		
		List<UrlItem> relateUrls=ParseUrlList.getInstance(document, currentUrl,"relate").parse();
		
		String title=ParseTitle.getInstance(document,currentUrl).parse();	
		
		String content=ParseContent.getInstance(document).parse();
		
		result.setContent(content);				
		result.setNextPageUrl(nextPageUrl);
		result.setTitle(title);
		result.setProvPageUrl(provPageUrl);
		result.setRelatePageUrls(relateUrls);
		
		
		return result;
	}
	
	/**
	 * 整理整个文档，清除不需要的节点
	 * */
	public void prepareDocument(Jerry document){
		String[] removeTags=new String[]{"meta","script","link","noscript"
				,"nav","iframe","br","style","font","textarea"};
		for(String tag :removeTags){
			document.$(tag).remove();
		}

	}

		

	
	
	
	

	

	public int getRelateUrlCount() {
		return relateUrlCount;
	}

	public void setRelateUrlCount(int relateUrlCount) {
		this.relateUrlCount = relateUrlCount;
	}


	

	
	
	
}
