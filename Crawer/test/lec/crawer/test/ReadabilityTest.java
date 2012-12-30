package lec.crawer.test;


import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.net.URI;
import java.net.URISyntaxException;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import jodd.io.FileUtil;
import jodd.jerry.Jerry;
import jodd.lagarto.dom.Node;

import jodd.util.StringUtil;

import lec.crawer.algo.Distance;
import lec.crawer.algo.ISimilarity;
import lec.crawer.algo.KCluster;
import lec.crawer.algo.KCluster.CenterPointsMaker;
import lec.crawer.algo.rdab.ReadAbility;
import lec.crawer.algo.UrlClassifier;
import lec.crawer.model.UrlItem;
import lec.crawer.parse.IParseResult;

import org.junit.Test;
import org.wltea.analyzer.core.IKSegmenter;
import org.wltea.analyzer.core.Lexeme;

import cn.uc.easy.utils.MD5Utils;

import com.sun.jndi.toolkit.url.Uri;

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
	

	
	
	public static double[] getVector(String href) throws IOException{
		  double[] vector = new double[]{0,0,0,0};
		  StringReader r = new StringReader(href); 
		   IKSegmenter iks=new IKSegmenter(r, true);		  
		   Lexeme t;		   		   
		   vector[0]=href.length();
		   int i=0;
		   while((t=iks.next())!=null){
		     if(t.getLexemeText().startsWith("http")){
		    	 vector[1]=t.getLexemeText().length()*10;
		    	 i++;
		     }  
		     
		     if(t.getLexemeText().indexOf(".")!=-1){
		    	 vector[2]=t.getLexemeText().length()*10;
		    	 i++;
		     }
		     if(i>2){
		    	 vector[3]=t.getLexemeText().length();
		    	 break;
		     }
		     
		   }

		   return vector;
	}
	
	public static double stringToNumber(String str){
		double s=0;
		double d=0;
		byte[] bs=str.getBytes();
		int i=0;
		for(byte b:bs){
			i++;
			s+=b/i;
		}			
		return s;
	}
	
	
   @Test
   public void testReadability() throws IOException, URISyntaxException{
	  String content;
	   content = FileUtil.readString(new File("D:\\crawer\\2.html"),"gbk");
	   Jerry jerry=Jerry.jerry(content);
	   Node[] nodes= jerry.$("a").get();
	   Set<String> set=new HashSet<String>();
	   for(Node node:nodes){
		   String href=node.getAttribute("href");
		   if(href!=null&&href.startsWith("http")){
		   set.add(node.getAttribute("href"));
		   }
	   }
	   

	  double[][] vecs=new double[set.size()][4];
	  List<String> hrefs=new ArrayList<String>();
	  int i=0;
	  Set<UrlItem> urlSet=new HashSet<UrlItem>();
	  for(String href:set){
		  urlSet.add(new UrlItem(href));
	   }
       Map<String, Set<UrlItem>> map= UrlClassifier.classByHost(urlSet);
	  
        for(String key:map.keySet()){
        	Set<UrlItem> uset=map.get(key);
        	System.out.println("goup["+key+"]");
        	for(UrlItem item:uset){
        	  
        	}
        }
       
       

	   
	  }
   
}
