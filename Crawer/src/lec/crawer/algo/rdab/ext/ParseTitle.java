package lec.crawer.algo.rdab.ext;

import java.util.HashMap;
import java.util.Map;

import jodd.jerry.Jerry;
import jodd.lagarto.dom.Node;
import jodd.util.StringUtil;
import lec.crawer.algo.ScoredElement;
import lec.crawer.algo.rdab.ReadAbilityConfig;
import lec.crawer.algo.rdab.ReadAbilityUtil;
import lec.crawer.model.UrlItem;

public class ParseTitle implements IParseExt<String> {

	 private static Map<String,String> lastSiteTitle=new HashMap<String, String>();

	 private UrlItem currentUrl;
	 
	 private Jerry document;
	 
     private static final ThreadLocal<ParseTitle> holder=new ThreadLocal<ParseTitle>();
	 
	 public static ParseTitle getInstance(Jerry document,UrlItem currentUrl){
		 ParseTitle parser=holder.get();
		 if(parser==null){
			 parser=new ParseTitle(document, currentUrl);
			 holder.set(parser);
		 }
		 parser.document=document;
		 parser.currentUrl=currentUrl;
		 return parser;
	 }
	 
	 
	 private ParseTitle(Jerry document,UrlItem currentUrl){
		 this.document=document;
		 this.currentUrl=currentUrl;
		 
	 }
	 
	/**
	 * 移除标题后面的网站信息
	 * */
	private String removeTitleSite(String siteSplit,String documentTitle,String lastTitle){


        if(StringUtil.isEmpty(lastTitle)){

    		if(documentTitle.indexOf(siteSplit)!=-1){
   			 String[] titles=documentTitle.split(siteSplit);
   			 String selectTitle=titles[0];
   			 for(String title:titles){
   				 if(selectTitle.length()<title.length()){
   					 selectTitle=title;
   				 }
   			 }
   			 documentTitle=selectTitle;
   			 
   		}
    		
       }else{
    	  documentTitle= StringUtil.reverse(documentTitle);
    	   lastTitle= StringUtil.reverse(lastTitle);
    	   String titleCommon= StringUtil.maxCommonPrefix(documentTitle, lastTitle);
    	   documentTitle= StringUtil.reverse(StringUtil.remove(documentTitle, titleCommon));
       }      
                
		return documentTitle;
	}
	
	/**
	 * 查找标题
	 * */
	public String parse(){
		String currentTitle=null;
		//文档标题
		String host= currentUrl.getUri().getHost();
        String lastTitle= lastSiteTitle.get(host);
		String documentTitle=document.$("title").text();
		lastSiteTitle.put(host, documentTitle);
		documentTitle=removeTitleSite("_", documentTitle,lastTitle);		
		Map<String,ScoredElement<String>> possibTitles=new HashMap<String, ScoredElement<String>>();
		ScoredElement<String> documentTitleScore= new ScoredElement<String>(40, documentTitle);

		if(documentTitle.length()<ReadAbilityConfig.getMaxTitleLength()
				&&documentTitle.length()>ReadAbilityConfig.getMinTitleLength()){
		  documentTitleScore.score+=documentTitle.length()*0.8;
		}

		possibTitles.put(documentTitle,documentTitleScore);
		
		//h1标题		
		Jerry h1=document.$("h1");
		Node[] h1nodes= h1.get();
		addPossibleTitle(h1nodes,45,documentTitle.length(),possibTitles);
		
		//h2标题		
		Jerry h2=document.$("h2");
		Node[] h2nodes= h2.get();
		addPossibleTitle(h2nodes,40,documentTitle.length(),possibTitles);
		
		//h3标题		
		Jerry h3=document.$("h3");
		Node[] h3nodes= h3.get();
		addPossibleTitle(h3nodes,35,documentTitle.length(),possibTitles);

		
		ScoredElement<String> top=new ScoredElement<String>(0, null);
		
		currentTitle=top.getTopElement(possibTitles.values());

		return currentTitle;
	}
	
	/**
	 * 查找可能的标题
	 * */	
	private void addPossibleTitle(Node[] nodes,int baseScore,int baseLength,Map<String,ScoredElement<String>> possibTitles){
		
		for(Node node:nodes){
		  	String idcls=node.getAttribute("id")+" "+node.getAttribute("class");
		  	String tcontent=node.getTextContent();
	  		ScoredElement<String> score=new ScoredElement<String>(baseScore, tcontent);
		  	if(ReadAbilityUtil.match(ReadAbilityConfig.getArticleTitleRegex(), idcls)
		  			&&tcontent.length()<ReadAbilityConfig.getMaxTitleLength()
		  			&&tcontent.length()>ReadAbilityConfig.getMinTitleLength()
		  			)
		  	{		  		
		  		score.score+=(tcontent.length()*(baseScore/50))+(baseLength-tcontent.length());
		  	}

		  	possibTitles.put(tcontent,score);
		}		
	}


	
}
