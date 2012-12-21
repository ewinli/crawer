package lec.crawer.algo;

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
import lec.crawer.model.UrlItem;
import lec.crawer.parse.IParseResult;

public class ReadAbility{

    private final String content;
	
    private final UrlItem currentUrl;
	
    private int relateUrlCount=3;
    
	private Map<Node, ScoredElement<Node>> elementsScore=new HashMap<Node,ScoredElement<Node>>();
    
    
	
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
		UrlItem nextPageUrl=findPageUrl(document,"next");
		UrlItem provPageUrl=findPageUrl(document, "prov");
		List<UrlItem> relateUrls=findPageUrls(document, "relate");
		String title=findTitle(document);
		String content=findContent(document);
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
	private void prepareDocument(Jerry document){
		String[] removeTags=new String[]{"meta","script","link","noscript","nav","iframe","br","style","font"};
		for(String tag :removeTags){
			document.$(tag).remove();
		}

	}
	
	
	private String findContent(Jerry document){
		String content="";
		stripUnlikelyCandidates(document);
	
		collapseRedundantParagraphDivs(document);

		 elementsScore = findCandidatesForArticleContent(document);
 
		ScoredElement<Node> topContent=determineTopCandidate(document, elementsScore);
		if(topContent!=null){
			content=topContent.element.getHtml();
		}
		

		content=prepareContent(content);
		return content;
	}
	
	/**
	 * 整理内容，清除不需要的节点
	 * */
	private String prepareContent(String content){
		if(StringUtil.isEmpty(content)){
			return "";
		}
		JerryParser jerryParser = Jerry.jerry();
		jerryParser.getDOMBuilder().setIgnoreWhitespacesBetweenTags(true);
		jerryParser.getDOMBuilder().setIgnoreComments(true);
		Jerry document= jerryParser.parse(content);
	    cleanUnlinkA(document);
		clean(document, "from");
		clean(document, "object");
		clean(document, "h1");
		if(document.$("h2").size()==1){
		   clean(document, "h2");
		}

		cleanConditionally(document, "table");
		cleanConditionally(document, "ul");
		cleanConditionally(document, "div");
		List<Node> listToRemove=new ArrayList<Node>();
		Jerry jp= document.$("p,div,span");		
		Node[] jpnodes= jp.get();
		for(Node jpn:jpnodes){
			String jpncontent=jpn.getTextContent();
			if(!StringUtil.isEmpty(jpncontent)){
				continue;
			}
			Jerry jnode= Jerry.jerry(jpn.getHtml());
			int imgCount=jnode.$("img").size();
			if(imgCount>0) continue;
			
			int embedCount=jnode.$("embed").size();
			if(embedCount>0) continue;
			
			int objectCount=jnode.$("object").size();
			if(objectCount>0) continue;
		
			listToRemove.add(jpn);
		}
		NodeUtil.removeNodes(listToRemove);				
		cleanStyles(document);
		StringBuilder sb=new StringBuilder();
		sb.append("<div>");
		sb.append(document.html());
		sb.append("</div>");
		return sb.toString();
	}
	
	
	/**
	 * 清除节点css
	 * */
	private void cleanStyles(Jerry document){
		 new ElementsTraverser(new IAction<Node>() {			
			public Object doAction(Node arg) {
			    String[] attrs=new String[]{"id","class"};
			    for(String attr:attrs){
				  if(ReadAbilityConfig.getStyleClass().containsValue(arg.getAttribute(attr))){
				      continue;
				  }
			      arg.removeAttribute("style");
				  arg.removeAttribute(attr);
			    }
				return null;
			}
		}).traverse(document.get(0));
	}
	
	/**
	 * 清除不合适的a标签
	 * */
	private void cleanUnlinkA(Jerry document){
		Jerry anode= document.$("a");
		Node[] alinks=anode.get();
		for(Node alink :alinks){
			String href=alink.getAttribute("href");
			boolean remove=false;
			Node parent=alink.getParentNode();
			if(StringUtil.isEmpty(href)){
				remove=true;
			}else if(href.startsWith("javascript")){
				remove=true;
			}else{
			  Node[] children=alink.getChildNodes();
			  if(children!=null&&children.length>0){
				for(Node child :children){
					if("img".equalsIgnoreCase(child.getNodeName())){
						remove=true;
						break;
					}
				 }
			  }
			}
			if(parent!=null&&remove)
			   parent.removeChild(alink);
		}
		
	}
	
	
	/**
	 * 获取节点分数
	 * */
	private double getNodeScore(Node node){
		if(elementsScore.containsKey(node)){
			return elementsScore.get(node).score;
		}
		return 0;
	}
	
	/**根据特定条件清除标签*/
	private void cleanConditionally(Jerry document,String tagName){
		Jerry tags=document.$(tagName);
		Node[] tagNodes=tags.get();
		List<Node> listToRemove=new ArrayList<Node>();
		for(Node node:tagNodes){
		  if(node.getNodeName()==null){
				continue;
		  }
		  int weight=getClassAndIdWeight(node);
		  double score=getNodeScore(node);
		  if(weight+score<0){
			  listToRemove.add(node);
		  }
		  if(elementLooksLikeParagraphDiv(node)){
			  continue;
		  }
		  String content=node.getTextContent();
		  if(StringUtil.isEmpty(content)){
			  listToRemove.add(node);
			  continue;
		  }
		  int segmentsCount=StringUtil.count(content, ",")+StringUtil.count(content, "，")+StringUtil.count(content, "。");
		  if(segmentsCount<ReadAbilityConfig.getMinCommaSegments()){
			  Jerry jnode= Jerry.jerry(node.getHtml());
			  int pCount=jnode.$("p").size();
			  int imgCount=jnode.$("img").size();
			  int liCount=jnode.$("li").size();
			  int inputCount=jnode.$("input").size();
			  int embedsCount=0;
			  Node[] ens=jnode.$("embed").get();
			  if(ens!=null&&ens.length>0){
				  for(Node en :ens){
				    if(match(ReadAbilityConfig.getVideoRegex(),en.getAttribute("src"))){
				    	embedsCount++;
				    }
			     }
			  }
			  double linkDensity=getLinksDensity(node);
			  int innerTextLength=content.length();
			  String nodeName=node.getNodeName().toLowerCase();
			  boolean remove=(imgCount>pCount)
					  ||((liCount-ReadAbilityConfig.getLisCountTreshold()>pCount
							  &&!"ul".equals(nodeName)&&!"ol".equals(nodeName))
					  ||(inputCount>pCount/3)
					  ||(innerTextLength<ReadAbilityConfig.getMinInnerTextLength()
							  &&(imgCount==0||imgCount>ReadAbilityConfig.getMaxImagesInShortSegmentsCount()))
					  ||(weight<ReadAbilityConfig.getClassWeightTreshold()
							  &&linkDensity>ReadAbilityConfig.getMaxDensityForElementsWithSmallerClassWeight())
					  || (weight>=ReadAbilityConfig.getClassWeightTreshold()
								  &&linkDensity>ReadAbilityConfig.getMaxDensityForElementsWithGreaterClassWeight())
						||(embedsCount>ReadAbilityConfig.getMaxEmbedsCount()
								||(embedsCount==ReadAbilityConfig.getMaxEmbedsCount()
								&&innerTextLength<ReadAbilityConfig.getMinInnerTextLengthInElementsWithEmbed()))	  
							  );
			  if(remove){
				  listToRemove.add(node);
			  }
		  }
		}		
		NodeUtil.removeNodes(listToRemove);
	}
	/**
	 * 计算节点id和class权重
	 * */
	private int getClassAndIdWeight(Node node){
		int weight=0;
		String nodeCls=node.getAttribute("class");
		if(!StringUtil.isEmpty(nodeCls)){
			if(match(ReadAbilityConfig.getNegativeWeightRegex(),nodeCls)){
				weight-=25;
			}
			if(match(ReadAbilityConfig.getPositiveWeightRegex(),nodeCls)){
				weight+=25;
			}
		}
		String nodeId=node.getAttribute("id");
		if(!StringUtil.isEmpty(nodeId)){
			if(match(ReadAbilityConfig.getNegativeWeightRegex(),nodeId)){
				weight-=25;
			}
			if(match(ReadAbilityConfig.getPositiveWeightRegex(),nodeId)){
				weight+=25;
			}
		}
		return weight;
	}
	
	/**
	 * 看起来像文章的div标签
	 * */
	private boolean elementLooksLikeParagraphDiv(Node node){
		if(!"div".equalsIgnoreCase(node.getNodeName())){
			return false;
		}
		
		if(match(ReadAbilityConfig.getLikelyParagraphDivRegex(),node.getAttribute("class"))){
			 // we'll consider divs only with certain classes as potential paragraph divs
			return false;
		}
		
		Node child=NodeUtil.getSingleOrNoneChild(node);
		if(child!=null&&"p".equalsIgnoreCase(child.getNodeName())){
			return true;
		}
		
		return false;
	}
	
	/**清除标签*/
	private void clean(Jerry document,String tagName){
		Jerry tags=document.$(tagName);
		Node[] tagNodes=tags.get();
		 boolean isEmbed = "object".equalsIgnoreCase(tagName)|| "embed".equalsIgnoreCase(tagName);
		 List<Node> listToRemove=new ArrayList<Node>();
		 for(Node tag:tagNodes){
			if(isEmbed
			&&(match(ReadAbilityConfig.getVideoRegex(),tag.getTextContent()))
			||match(ReadAbilityConfig.getVideoRegex(),NodeUtil.getAttributeString(tag))
			)
			{
				continue;
			}
			listToRemove.add(tag);
		}
		NodeUtil.removeNodes(listToRemove);
	}
	
	/**
	 * 确定分数最高的文章候选
	 * */
	private ScoredElement<Node> determineTopCandidate(Jerry document,Map<Node,ScoredElement<Node>> candidates){
		ScoredElement<Node> determineNode=null;
		for(ScoredElement<Node> snode:candidates.values()){
				double newScore=snode.score*(1.0-getLinksDensity(snode.element));	
				snode.score=newScore;
				if(determineNode==null||newScore>determineNode.score){
					determineNode=snode;
				}

		}
		if(determineNode==null||"body".equalsIgnoreCase(determineNode.element.getNodeName())){
			 Jerry jbody=document.$("body");
			 Node node=jbody.get(0);
			 node=NodeUtil.createNodeFrom(node, "div");
			 determineNode=new ScoredElement<Node>(0, node);
		}
		

		return determineNode;
	}
	
	/**
	 * 取得节点连接文字占文字的比例
	 * */
	private double getLinksDensity(Node node){
		String innerText=node.getTextContent();
		int length=innerText.length();
		if(length==0){
			return 0;
		}
		Jerry jnode= Jerry.jerry().parse(node.getHtml());
		Jerry anode= jnode.$("a");
		Node[] alinks=anode.get();
		int linksLength=0;		
		for(Node alink:alinks){
			String content=alink.getTextContent();
			linksLength+=content==null?0:content.length();
		}
		
		return (double)linksLength/length;
	}
	
	/**
	 * 移除不喜欢的候选标签
	 * */
	private void stripUnlikelyCandidates(Jerry document){
		Jerry body=document.$("body");
		new ElementsTraverser(new IAction<Node>() {			
			public Object doAction(Node node) {				
				String nodeName=node.getNodeName();
				if(!StringUtil.isEmpty(nodeName)){
					  /* Remove unlikely candidates. */
					String unLikeMatchString=node.getAttribute("id")+" "+node.getAttribute("class");
					if(!"body".equalsIgnoreCase(nodeName)
						&&!"a".equalsIgnoreCase(nodeName)
						&&match(ReadAbilityConfig.getUnlikelyCandidatesRegex(), unLikeMatchString)
						&&!match(ReadAbilityConfig.getOkMaybeItsACandidateRegex(), unLikeMatchString)){
						Node parent= node.getParentNode();
						if(parent!=null){
						  	parent.removeChild(node);
						}
						// element has been removed - we can go to the next one
						return null;
					}
				}
				/* Turn all divs that don't have children block level
				 *  elements into p's or replace text nodes within 
				 *  the div with p's. */
				if("div".equalsIgnoreCase(nodeName)){
					if(!match(ReadAbilityConfig.getDivToPElementsRegex(),node.getInnerHtml())){
					      // no block elements inside - change to p 					
						NodeUtil.setNodeName(node,"p");
					}
					else{
						new ChildNodesTraverser(new IAction<Node>() {
							
							public Object doAction(Node child) {								
								String innerHtml=child.getInnerHtml();							
								if(!child.getNodeType().equals(NodeType.TEXT)
										||StringUtil.isEmpty(innerHtml)){	
								
									return null;
								}
								child.removeAttribute("class");
								child.setAttribute("class",ReadAbilityConfig.getStyleClass().get("text"));
								child.setAttribute("style","display: inline;");
								return null;
							}
						}).traverse(node);
					}
				}
				
				return null;
			}
			
		}).traverse(body.get(0));
	}
	
	/**
	 * 把div里面的单个p标签提取到父div里面
	 * */
	private void collapseRedundantParagraphDivs(Jerry document){
		Jerry divs=document.$("div");
		Node[] nodes=divs.get();
		for(Node node :nodes){
			if(node.getNodeName()!=null){
				Node[] childnodes=node.getChildNodes();
				if(childnodes!=null&&childnodes.length==1){
					if("p".equals(childnodes[0].getNodeName())){
					  Node parent= node.getParentNode();
					  if(parent==null){
						  Node newnode= NodeUtil.createNodeFrom(childnodes[0],"p");
						  parent.insertBefore(newnode, node);
						  parent.removeChild(node);
					  }
					}
				}
				/**new add*/
			    new ElementsTraverser(new IAction<Node>() {
					
					public Object doAction(Node arg) {
						if(arg.getNodeType().equals(NodeType.TEXT)){
							Node parent=arg.getParentNode();
							if(parent!=null&&!"a".equals(parent.getNodeName()))
						     NodeUtil.setNodeName(parent, "p");
						}
						return null;
					}
				}).traverse(node);
			    
			}
		}
		
	}
	
	/**
	 * 查找候选文章列表
	 * */	
	private Map<Node,ScoredElement<Node>> findCandidatesForArticleContent(Jerry document){
		Map<Node,ScoredElement<Node>> result=new HashMap<Node,ScoredElement<Node>>();
		Node[] paraNodes=document.$("p").get();//p
		if(paraNodes!=null){
			for(Node node :paraNodes){
				String content= node.getTextContent();
			    int score=1;
			    score+=StringUtil.count(content, ',');
			    score+=StringUtil.count(content, '，');
			    score+=StringUtil.count(content, '。');
			    score+=Math.min(content.length()/ReadAbilityConfig.getParagraphSegmentLength(), 
			    		ReadAbilityConfig.getMaxPointsForSegmentsCount());
			    Node parent=node.getParentNode();
			    Node grandParent=parent==null?null:parent.getParentNode();
			  
			    if(parent!=null&&!"html".equalsIgnoreCase(parent.getNodeName())){
			        ScoredElement<Node> snode= result.get(parent);
			        if(snode==null){
			        	snode=new ScoredElement<Node>(score, parent);
			        }
			        else{
			        	snode.score+=score;
			        }
			        result.put(parent, snode);
			    }
                if(grandParent!=null&&!"html".equalsIgnoreCase(grandParent.getNodeName())){
                    ScoredElement<Node> snode= result.get(grandParent);
			        if(snode==null){
			        	snode=new ScoredElement<Node>(score/2, grandParent);
			        }
			        else{
			        	snode.score+=score;
			        }
			        result.put(grandParent, snode);
			    }
			    
			    
			}
		}
		
		return result;
	}
	
	/**
	 * 移除标题后面的网站信息
	 * */
	private String removeTitleSite(String siteSplit,String documentTitle){

		if(documentTitle.indexOf("_")!=-1){
			 String[] titles=documentTitle.split("_");
			 String selectTitle=titles[0];
			 for(String title:titles){
				 if(selectTitle.length()<title.length()){
					 selectTitle=title;
				 }
			 }
			 documentTitle=selectTitle;
		}
		return documentTitle;
	}
	
	/**
	 * 查找标题
	 * */
	private String findTitle(Jerry document){
		String currentTitle=null;
		//文档标题
		String documentTitle=document.$("title").text();
		documentTitle=removeTitleSite("_", documentTitle);		
		Map<String,ScoredElement<String>> possibTitles=new HashMap<String, ScoredElement<String>>();
		ScoredElement<String> documentTitleScore= new ScoredElement<String>(50, documentTitle);

		if(documentTitle.length()<ReadAbilityConfig.getMaxTitleLength()
				&&documentTitle.length()>ReadAbilityConfig.getMinTitleLength()){
		  documentTitleScore.score+=documentTitle.length();
		}

		possibTitles.put(documentTitle,documentTitleScore);
		
		//h1标题		
		Jerry h1=document.$("h1");
		Node[] h1nodes= h1.get();
		addPossibleTitle(h1nodes,45,possibTitles);
		
		//h2标题		
		Jerry h2=document.$("h2");
		Node[] h2nodes= h2.get();
		addPossibleTitle(h2nodes,40,possibTitles);
		

		
		ScoredElement<String> top=new ScoredElement<String>(0, null);
		
		currentTitle=top.getTopElement(possibTitles.values());

		return currentTitle;
	}
	
	/**
	 * 查找可能的标题
	 * */	
	private void addPossibleTitle(Node[] nodes,int baseScore,Map<String,ScoredElement<String>> possibTitles){
		
		for(Node node:nodes){
		  	String idcls=node.getAttribute("id")+" "+node.getAttribute("class");
		  	String tcontent=node.getTextContent();
	  		ScoredElement<String> score=new ScoredElement<String>(baseScore, tcontent);
		  	if(match(ReadAbilityConfig.getArticleTitleRegex(), idcls)
		  			&&tcontent.length()<ReadAbilityConfig.getMaxTitleLength()
		  			&&tcontent.length()>ReadAbilityConfig.getMinTitleLength()
		  			)
		  	{
		  		score.score+=(tcontent.length()*(baseScore/50));
		  	}

		  	possibTitles.put(tcontent,score);
		}		
	}
		
	/**
	 * 查找页面url
	 * */
	private UrlItem findPageUrl(Jerry document,String type){
		 UrlItem nextPageUrl=null;
		 Node[] allLink=document.$("a").get();
		if(allLink==null||allLink.length==0){
			 return nextPageUrl;
		 }
		Map<String,ScoredElement<UrlItem>> possibleLinks=getPossiblePageUrls(allLink,type);
		 nextPageUrl=new ScoredElement<UrlItem>(0,nextPageUrl).getTopElement(possibleLinks.values(),0);
		 if(nextPageUrl==null){
			 nextPageUrl=new UrlItem("");
		 }
		 return nextPageUrl;
	}

	/**
	 * 查找页面url列表
	 * */
	private List<UrlItem> findPageUrls(Jerry document, String type) {
		List<UrlItem> result = new ArrayList<UrlItem>();
		Node[] allLink = document.$("a").get();
		if (allLink == null || allLink.length == 0) {
			return result;
		}
		Map<String, ScoredElement<UrlItem>> possibleLinks = getPossiblePageUrls(
				allLink, type);
		result = new ScoredElement<UrlItem>(0, null).getTopElements(
				possibleLinks.values(), ReadAbilityConfig.getMinReleateScore(),
				relateUrlCount);
		return result;
	}
	
	private   Map<String,ScoredElement<UrlItem>> getPossiblePageUrls( Node[] allLink,String type){
		 Map<String,ScoredElement<UrlItem>> possibleLinks=new HashMap<String,ScoredElement<UrlItem>>();

		 for(Node link : allLink){

			 String href=link.getAttribute("href");
		     if(StringUtil.isEmpty(href)){
		    	 continue;
		     }
		     href=href.replaceAll("#.*$", "");
		     href=href.replaceAll("/$", "");		
             href=getAbsouluteUrl(href);
             if(currentUrl.getUrl().equals(href)){
            	continue;
             }    
             String base="://"+currentUrl.getUri().getHost();
             if(href.indexOf(base)==-1){
            	 continue;
             }             
            
             int score=getPageUrlScore(href, link, type);
			 UrlItem url=new UrlItem(href);
			 ScoredElement<UrlItem> element=new ScoredElement<UrlItem>(score,url);
			 possibleLinks.put(url.getKey(),element);
		 }
		 return possibleLinks;
	}
	
    private static Map<String,Pattern> pageTypePattern=new HashMap<String, Pattern>();
    static{
	 pageTypePattern.put("nextLink", ReadAbilityConfig.getNextLink());
	 pageTypePattern.put("nextText", ReadAbilityConfig.getNextText());
	 pageTypePattern.put("provText", ReadAbilityConfig.getPrevText());
	 pageTypePattern.put("provLink", ReadAbilityConfig.getPrevLink());
	 pageTypePattern.put("relateLink", ReadAbilityConfig.getNextStoryLink());
	 pageTypePattern.put("relatePositive", ReadAbilityConfig.getPositiveWeightRegex());
	 pageTypePattern.put("relateText", ReadAbilityConfig.getRelateText());
    }
	
	private int getPageUrlScore(String href, Node link, String type) {

		if("next".equals(type)||"prov".equals(type)) return getNextOrProvPageScore(href, link, type);
		return getPossiblePageScore(href, link, type);
	}
	
	private int getPossiblePageScore(String href, Node link, String type) {
		int score = 0;

		if (match(pageTypePattern.get(type + "Link"), href)) {
				score +=5;
		}
		
		if(match(pageTypePattern.get(type+"Positive"),href)){
			   score+=5;
		}
		String path=currentUrl.getUri().getPath();
		UrlItem hrefItem=new UrlItem(href);
		String hrefPath=hrefItem.getUri().getPath();

		if(!StringUtil.isEmpty(path)&&!StringUtil.isEmpty(hrefPath)&&path.indexOf("/")!=-1&&hrefPath.indexOf("/")!=-1){
			String[] paths= path.split("/");
			String[] hrefPaths=hrefPath.split("/");
			int count=Math.min(paths.length, hrefPaths.length);
			for(int i=0;i<count;i++){
				if(paths[i].equals(hrefPaths[i])&&!StringUtil.isEmpty(hrefPaths[i])){
					score+=10;
				}
				if(paths[i].length()==hrefPaths[i].length()){
					score+=5;
				}
			}
			
		}
        
		if (link.getTextContent() == null) {
			score -= 50;
		}

		return score;
	}
	
	private int getNextOrProvPageScore(String href, Node link, String type){
		int score = 0;
		String[] types=new String[]{"next","prov"};

		for (String pageType : types) {
			if (match(pageTypePattern.get(pageType + "Link"), href)) {
				if(pageType.equals(type))
				  score += 25;
				else
				  score-=25;
			//	System.out.println(link.getTextContent()+"[1] "+score);
			}

			if (link.getTextContent() == null) {
				score -= 50;
			}
			if (match(pageTypePattern.get(pageType + "Text"),
					link.getTextContent())) {
				if(pageType.equals(type))
					  score +=50;
					else
					  score-=50;
				//System.out.println(link.getTextContent()+"[2] "+score);
			} 
			
			Node parent = link.getParentNode();
			if (parent != null&&parent.getChildElementsCount()==1) {
				if (match(pageTypePattern.get(pageType + "Text"),
						parent.getTextContent())) {
					if(pageType.equals(type))
						  score +=50;
						else
						  score-=50;
				} 		
			}
		}
		//System.out.println(link.getTextContent()+" "+score);
		return score;
	}
	
	
	
	/**
	 * 匹配字符串
	 * */
	private boolean match(Pattern pattern,String str){
		if(StringUtil.isEmpty(str)||pattern==null) return false;
		return pattern.matcher(str).find();
	}
	
	/**
	 * 返回当前路径的绝对路径
	 * */
	private String getAbsouluteUrl(String url){
		String host = currentUrl.getUri().getHost();
		int port = currentUrl.getUri().getPort();
		String path=currentUrl.getUri().getPath();
		String protocol=currentUrl.getUri().getScheme();
        StringBuilder sb=new StringBuilder();
        if(StringUtil.isEmpty(url)){
        	return currentUrl.getUrl();
        }
        if(url.toLowerCase().startsWith("javascript")){
        	return "";
        }
        else if(url.indexOf("http://")!=-1||url.indexOf("https://")!=-1){
		   return url;
	     }else if(url.startsWith("/")){
			sb.append(protocol);
			sb.append("://");
			sb.append(host);
			if(port!=-1){
				sb.append(port);
			}
			sb.append(url);
		    return sb.toString();	
		 }
	     else{
			String[] basePath=path.split("/");
			String[] toPath=url.split("/");
			sb.append(protocol);
			sb.append("://");
			sb.append(host);
			if(port!=-1){
				sb.append(port);
			}
			for(int i=0;i<basePath.length;i++){
				if(i<toPath.length){
					if(basePath[i]!=toPath[i]){
						sb.append("/").append(basePath[i]);
					}else{
						sb.append("/");
						break;
					}
				}
			}
			sb.append(url);
			return sb.toString();
		 }

	}

	public int getRelateUrlCount() {
		return relateUrlCount;
	}

	public void setRelateUrlCount(int relateUrlCount) {
		this.relateUrlCount = relateUrlCount;
	}


	

	
	
	
}
