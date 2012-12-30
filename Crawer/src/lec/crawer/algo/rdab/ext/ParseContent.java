package lec.crawer.algo.rdab.ext;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jodd.jerry.Jerry;
import jodd.jerry.Jerry.JerryParser;
import jodd.lagarto.dom.Node;
import jodd.lagarto.dom.Node.NodeType;
import jodd.util.StringUtil;
import lec.crawer.algo.ChildNodesTraverser;
import lec.crawer.algo.ElementsTraverser;
import lec.crawer.algo.IAction;
import lec.crawer.algo.ScoredElement;
import lec.crawer.algo.rdab.ReadAbilityConfig;
import lec.crawer.algo.rdab.ReadAbilityUtil;
import cn.uc.lec.dom.NodeUtil;

@Ext(name="content",type=1)
public class ParseContent implements IParseExt<String> {
	
	private Map<Node, ScoredElement<Node>> elementsScore=new HashMap<Node,ScoredElement<Node>>();
   
	private Jerry document;
	
	private static final ThreadLocal<ParseContent> holder=new ThreadLocal<ParseContent>();
	
	private ParseContent(Jerry document){
         this.document=document;
	}

	public static ParseContent getInstance(Jerry document) {
		ParseContent parser =holder.get();
		if (parser == null) {
			parser = new ParseContent(document);
			holder.set(parser);
		}
		parser.document = document;
		return parser;
	}



	public String parse() {
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
			}else if(href.startsWith("#")){
				remove=true;
			}
			else{
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
				    if(ReadAbilityUtil.match(ReadAbilityConfig.getVideoRegex(),en.getAttribute("src"))){
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
			if(ReadAbilityUtil.match(ReadAbilityConfig.getNegativeWeightRegex(),nodeCls)){
				weight-=25;
			}
			if(ReadAbilityUtil.match(ReadAbilityConfig.getPositiveWeightRegex(),nodeCls)){
				weight+=25;
			}
		}
		String nodeId=node.getAttribute("id");
		if(!StringUtil.isEmpty(nodeId)){
			if(ReadAbilityUtil.match(ReadAbilityConfig.getNegativeWeightRegex(),nodeId)){
				weight-=25;
			}
			if(ReadAbilityUtil.match(ReadAbilityConfig.getPositiveWeightRegex(),nodeId)){
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
		
		if(ReadAbilityUtil.match(ReadAbilityConfig.getLikelyParagraphDivRegex(),node.getAttribute("class"))){
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
			&&(ReadAbilityUtil.match(ReadAbilityConfig.getVideoRegex(),tag.getTextContent()))
			||ReadAbilityUtil.match(ReadAbilityConfig.getVideoRegex(),NodeUtil.getAttributeString(tag))
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
						&&ReadAbilityUtil.match(ReadAbilityConfig.getUnlikelyCandidatesRegex(), unLikeMatchString)
						&&!ReadAbilityUtil.match(ReadAbilityConfig.getOkMaybeItsACandidateRegex(), unLikeMatchString)){
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
					if(!ReadAbilityUtil.match(ReadAbilityConfig.getDivToPElementsRegex(),node.getInnerHtml())){
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


}
