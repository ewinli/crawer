package lec.crawer.algo;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import jodd.util.StringUtil;

import lec.crawer.model.UrlItem;

public class UrlClassifier {
  
	public static Map<String,Set<UrlItem>> classByHost(Set<UrlItem> urlset){
		Map<String,Set<UrlItem>> map=new HashMap<String, Set<UrlItem>>();
		for(UrlItem item:urlset){
			String host=item.getUri().getHost();
			if(!map.containsKey(host))
			  map.put(host, new HashSet<UrlItem>());
			map.get(host).add(item);
		}
		
		return map;
	}
	
	public static double getUrlSimScore(String currentUrl,String url) throws URISyntaxException, MalformedURLException{
		return new UrlSim(new URL(currentUrl),new URL(url)).sim();
				
	}
	
	public static class UrlSim{
		public int site;
		public int length;
		public int dics;
		public int last;
		public URL uri1;
		public URL uri2;
		public UrlSim(URL uri1,URL uri2){
		  this.uri1=uri1;
		  this.uri2=uri2;
		  site=getSite();
		  length=getLength();
		  dics=getDics();
		  last=getLast();
		}
		
		static void reverse(String[] array){
			int count=array.length;
			for(int i=0;i<count/2;i++){
			    String tmp=array[i];  
				array[i]=array[count-1-i];
				array[count-1-i]=tmp;
			}

		}
		
		
		public int getSite() {
			int score = 0;
			String host1 = uri1.getHost();
			String scheme1 = uri1.getProtocol();

			String host2 = uri2.getHost();
			String scheme2 = uri2.getProtocol();

			if (scheme1.equals(scheme2)) {
				score += 10;
			}

			if (host1.equals(host2)) {
				score = 100;
				return score;
			}

			String[] site1s = StringUtil.split(host1, ".");
			String[] site2s = StringUtil.split(host2, ".");
			reverse(site1s);
			reverse(site2s);
			if (site1s.length== site2s.length) {
				score +=10;
			}
			
			for (int i = 0; i < site1s.length; i++) {
				for(int j=0;j<site2s.length;j++){
					if(site1s[i].equals(site2s[j])){
						score+=30;
					}
				}
			}
			if(score>70){
				score=70;
			}

			return score;
		}
		
		public int getDics(){
			double score=0;
			String last1=uri1.getPath();
			String last2=uri2.getPath();
			if((StringUtil.isEmpty(last1)&&StringUtil.isEmpty(last2))
				||(!StringUtil.isEmpty(last1)&&last1.equals(last2))
				||(!StringUtil.isEmpty(last2)&&last2.equals(last1))
				)
			{
				return 100;
			}

			if(!StringUtil.isEmpty(last1)&&StringUtil.isEmpty(last2)
				||!StringUtil.isEmpty(last2)&&StringUtil.isEmpty(last1)	)
			{
			    return 0;
			}
			 String[] array1=StringUtil.split(last1, "/");
			 
			 String[] array2=StringUtil.split(last2, "/");
			 
			 if(array1.length==array2.length){
				 score+=40;
			 }else{
				 return (int)score;
			 }
			 int eq=0;
			 for(int i=0;i<array1.length;i++){
				 if(array1[i].equals(array2[i])){
					eq++;
				 }
			 }
			 score+=((double)eq/array1.length)*60;
			 
			 
			return (int)score;
		}
		
		public int getLast(){
			double score=0;
			String last1=uri1.getPath();
			String last2=uri2.getPath();
			if(StringUtil.isEmpty(last1)||StringUtil.isEmpty(last2)){
				return (int)score;
			}
			int last1c=0;
			int last2c=0;
			if(!StringUtil.isEmpty(last1)&&last1.indexOf("/")!=-1){
		       String[] array=StringUtil.split(last1, "/");
				if(array.length>0){
		           reverse(array);
		           last1=array[0];
		           last1c=array.length;
				}
			}
			if(!StringUtil.isEmpty(last2)&&last2.indexOf("/")!=-1){
			       String[] array=StringUtil.split(last2, "/");
					if(array.length>0){
			           reverse(array);
			           last2=array[0];
			           last2c=array.length;
					}
			}
			
			if(last1c==last2c){
			  score+=40;	
			}else{
			  return (int)score;
			}
			
			int differ=Math.abs(last1.length()-last2.length());
			if(differ>0){
			  score+=(4/(differ+0.1))*(20/4);
			}else{
			  score+=20;
			}
			Pattern number=Pattern.compile("^[0-9]+$");
			Pattern word=Pattern.compile("^[a-zA-Z]+$");
			Pattern numberWord=Pattern.compile("^[a-zA-Z0-9]+$");
		    Matcher n1= number.matcher(last1);
		    Matcher n2= number.matcher(last2);
			Matcher w1=word.matcher(last1);
			Matcher w2=word.matcher(last2);
			Matcher wn1=numberWord.matcher(last1);
			Matcher wn2=numberWord.matcher(last2);
			if(n1.find()&&n2.find()){
				score+=30;
			}
			else if(w1.find()&&w2.find()){
				score+=30;
			}
				
			else if(wn1.find()&&wn2.find()){
				score+=20;
			}
			
			if(last1.equals(last2)){
				score=100;
			}
			
			return (int)score;
		}
		
		public int getLength(){
			double score=0;
			String url1=uri1.getProtocol()+"://"+uri1.getHost()+"/"+uri1.getPath();
			String url2=uri2.getProtocol()+"://"+uri2.getHost()+"/"+uri2.getPath();
			
			int differ=Math.abs( url1.length()-url2.length());
			

			score=((double)100/(100+differ))*100;
			return (int)score;
		}
		
		public double sim(){
			return 0.40*site+0.1*length+0.4*dics+0.1*last;
		}
		
		public double[] toVectors(){
			return new double[]{site,length,dics,last};
		}
		
		
		
	}
	
}
