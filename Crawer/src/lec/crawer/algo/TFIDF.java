package lec.crawer.algo;
import java.io.IOException;
import java.io.StringReader;

import java.util.*;

import org.wltea.analyzer.core.IKSegmenter;
import org.wltea.analyzer.core.Lexeme;



public class TFIDF {

	private Map<Integer, String> wordMap=new HashMap<Integer, String>();
	
	private double[][] result;
	
	
	public Map<Integer, String> getWordMap() {
		return wordMap;
	}



	public void setWordMap(Map<Integer, String> wordMap) {
		this.wordMap = wordMap;
	}



	public double[][] getResult() {
		return result;
	}



	public void setResult(double[][] result) {
		this.result = result;
	}



	public TFIDF(String[]documents) throws IOException{		
		List< Map<String,Double>> list=new ArrayList<Map<String,Double>>();
		 Map<String,Integer> wordSet=new HashMap<String,Integer>();
		 int k=0;
		for(int i=0;i<documents.length;i++){
			 String document=documents[i];
			 Map<String,Double> map= getWordMap(document);
			 list.add(map);
			 for(String key:map.keySet()){
				 if(!wordSet.containsKey(key)){
					 wordSet.put(key,k++);
				 }
			 }
		}
		result=new double[documents.length][wordSet.size()];
		double[] rowSum=new double[documents.length];
		double[] colSum=new double[wordSet.size()];
		for(int i=0;i<result.length;i++){
		   for(String key:wordSet.keySet()){	
			   int j= wordSet.get(key);
			   Double count=list.get(i).get(key);
			   count=count==null?0:count;
			   result[i][j]=count;
			   wordMap.put(j,key);
			   rowSum[i]+=count;
			   if(count>0)
			    colSum[j]++;
	       }
		   
		}

		
		for(int i=0;i<result.length;i++){
			for(int j=0;j<result[i].length;j++){	
				double res=result[i][j];
				if(wordMap.get(j).equals("大学生")){
					System.out.println("d:"+(double)(result.length+1)/(double)(colSum[j]+1));
					System.out.println("d2:"+Math.log((double)(result.length+1)/(double)(colSum[j]+1)));
				}
				
				result[i][j]=(res/rowSum[i])*Math.log((double)(result.length+1)/(double)(colSum[j]+1));
			}
		}
		
	}
	
	
	
	public static double TF(String file, String word) throws IOException {
	
		int sum=0;
		Map<String, Double> map =getWordMap(file);		
		for(String key:map.keySet()){
			sum+=map.get(key);
		}
		Double keycount=map.get(word);
		keycount=keycount==null?0.0:keycount;
		return keycount/sum;
	}

	public static Map<String,Double> getWordMap(String file) throws IOException{
		StringReader reader = new StringReader(file);
		IKSegmenter iks = new IKSegmenter(reader, true);
		Lexeme t;
		Map<String, Double> map = new HashMap<String, Double>();
		while ((t = iks.next()) != null) {
			String key = t.getLexemeText();
			if (!map.containsKey(t.getLexemeText()))
				map.put(key, 1.0);
			else {
				double count = map.get(key);
				map.put(key, ++count);
			}
		}
		return map;
	}
	
	public static double IDF(String[] documents,String word) throws IOException{
		int count=0;
		for(String document:documents){
			Map<String,Double> map= getWordMap(document);
			if(map.containsKey(word)){
				count++;
			}
		}

		return Math.log((double)(documents.length)/(double)(count+1));
	}
	
	

	
	
	
}
