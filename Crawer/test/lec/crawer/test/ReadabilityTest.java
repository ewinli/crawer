package lec.crawer.test;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import jodd.io.FileUtil;
import jodd.jerry.Jerry;
import jodd.lagarto.dom.Node;

import lec.crawer.algo.ReadAbility;
import lec.crawer.model.UrlItem;
import lec.crawer.parse.IParseResult;

import org.junit.Test;

public class ReadabilityTest {
   
  /* @Test
   public void testReadability() throws IOException{
	   String content;

	   content = FileUtil.readString(new File("D:\\crawer\\2.html"),"gbk");
	   ReadAbility readAbility=new ReadAbility(content,new UrlItem("http://www.cnbeta.com/articles/218061.htm"));
	   IParseResult result= readAbility.getResult();
	  // System.out.println("title:"+result.getTitle());
	   System.out.println("==================================================");
	  // System.out.println("content:"+result.getContent());
	   System.out.println("==================================================");
	   System.out.println("nextPage:"+result.getNextPageUrl().getUrl());
	   System.out.println("provPage:"+result.getProvPageUrl().getUrl());
	   for(UrlItem item : result.getRelatePageUrls()){
		   System.out.println("releatePage:"+item.getUrl());
	   }

	  }
   */
   @Test
   public void testReadability() throws IOException{
	   String content;
	   content = FileUtil.readString(new File("D:\\crawer\\2.html"),"gbk");
	   Jerry jerry=Jerry.jerry(content);
	   Node[] nodes= jerry.$("a").get();
	   Set<String> set=new HashSet<String>();
	   for(Node node:nodes){
		   set.add(node.getAttribute("href"));
	   }
	   
	   for(String href:set){
		   System.out.println(href);
	   }
	   

	  }
   
}
