package demo1;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cn.uc.lec.dom.NodeUtil;

import com.sun.org.apache.xalan.internal.xsltc.compiler.util.NodeType;

import jodd.io.FileUtil;
import jodd.jerry.Jerry;
import jodd.jerry.Jerry.JerryParser;
import jodd.lagarto.dom.Element;
import jodd.lagarto.dom.Node;
import jodd.util.StringUtil;
import lec.crawer.algo.ElementsTraverser;
import lec.crawer.algo.IAction;
import lec.crawer.algo.rdab.ReadAbilityConfig;
import lec.crawer.model.UrlItem;

public class DemoParser {
		
	public static String parseContent(String content){
		JerryParser jerryParser = Jerry.jerry();		
		jerryParser.getDOMBuilder().setIgnoreWhitespacesBetweenTags(true);
		jerryParser.getDOMBuilder().setIgnoreComments(true);
		StringBuilder sb=new StringBuilder();		
		Jerry jerry = jerryParser.parse(content);		
		String[] removeTags=new String[]{"meta","script","link"};
		for(String tag :removeTags){
			jerry.$(tag).remove();
		}
	     
		
		Jerry body=jerry.$("body");
		new ElementsTraverser(new IAction<Node>() {
			
			public Object doAction(Node arg) {
				if(!StringUtil.isEmpty(arg.getNodeName())){
					String unLikeMatchString=arg.getAttribute("id")+" "+arg.getAttribute("class");
					if(!"body".equalsIgnoreCase(arg.getNodeName())
						&&!"a".equalsIgnoreCase(arg.getNodeName())
						&&ReadAbilityConfig.getUnlikelyCandidatesRegex().matcher(unLikeMatchString).find()
						&&!ReadAbilityConfig.getOkMaybeItsACandidateRegex().matcher(unLikeMatchString).find()){
						Node node= arg.getParentNode();
						if(node!=null){
						//	node.removeChild(arg);
						}
						
					}	
					if("head".equals(arg.getAttribute("id"))){
					  System.out.println(arg.getNodeName()+","+arg.getAttribute("id"));
				    	NodeUtil.setNodeName(arg, "span");
					}
				// System.out.println(arg.getNodeName()+"["+arg.getHtml()+"]");
				}
				
				 return null;
			}
			
		}).traverse(body.get(0));
		
		System.out.println("========================================================");
		System.out.println(jerry.$("#head").get(0).getHtml());
		return jerry.html();
		 
	}
	
	public static void main(String[] args) {
		try {

			String content;
			content = FileUtil.readString(new File("D:\\crawer\\1.html"),"gbk");
			//parseContent(content);
		//	int count= StringUtil.count(content,',');
		//	int count4= StringUtil.count(content,'.');
		//	int count3= StringUtil.count(content,'，');
		//	int count2=StringUtil.count(content, '。');
		//	System.out.println(count4);
		/*	ScoredElement<String> s1=new ScoredElement<String>(1, "hello");
			ScoredElement<String> s2=new ScoredElement<String>(2, "hello");
			HashSet<ScoredElement<String>>set=new HashSet<ScoredElement<String>>();
			set.add(s1);
			set.add(s2);
			System.out.println(set.size());*/
			Jerry jerry= Jerry.jerry(content);
			jerry=jerry.$("div");
			System.out.println(jerry.size()+","+jerry.get().length);
		} catch (IOException e) {
			e.printStackTrace();
		}
	/*   UrlItem item=new UrlItem("http://www.cnbeta.com/html/123.html");
		
	   System.out.println(item.getUri().getScheme());
	   Pattern prevLink=Pattern.compile(".*?((下一|下)(页|篇|章|节|话)|next).*?",Pattern.CASE_INSENSITIVE);
		System.out.print("下一章 赫尔浪费及司法".length());
		*/
		

		
	}
}
