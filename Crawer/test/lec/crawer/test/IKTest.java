package lec.crawer.test;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

import jodd.io.FileUtil;
import jodd.io.findfile.FindFile;
import jodd.util.ArraysUtil;
import jodd.util.collection.SortedArrayList;
import lec.crawer.algo.TFIDF;

import org.junit.Test;
import org.wltea.analyzer.cfg.DefaultConfig;
import org.wltea.analyzer.dic.Dictionary;



public class IKTest {
	
	public static class KeyVal implements Comparable<KeyVal>{
		String key;
		double val;
		public KeyVal(String key,double val){
			this.key=key;
			this.val=val;
		}
		public int compareTo(KeyVal o) {
			if(o.val>this.val){
				return 1;
			}else if(o.val<this.val){
				return -1;
			}else{
				return 0;
			}
			
		}
	    @Override
	    public String toString() {
	    	return key+":"+val+"\r\n";
	    }
		
	}
	
	
   @Test
   public void testTags() throws IOException{
	   FindFile findfile=new FindFile().sortByTime().setIncludeDirs(false).searchPath("D:\\crawer\\text\\");
	   File file;
	   List<String> filelist=new ArrayList<String>();
	   while((file=findfile.nextFile())!=null){
		   String txt= FileUtil.readString(file,"gb2312");
		   filelist.add(txt);
	   }

	  String[] titles=filelist.get(0).split("\r\n");
			  //filelist.toArray(new String[0]);
	  TFIDF tfidf=new TFIDF(titles);
	  double[][] results= tfidf.getResult();
	  Map<Integer, String> map=tfidf.getWordMap();
	  
	  for(int i=0;i<results.length;i++){
		  StringBuilder sb=new StringBuilder();
		  SortedArrayList<KeyVal> kvset=new SortedArrayList<IKTest.KeyVal>();
		  for(int j=0;j<results[i].length;j++){		 
            if(results[i][j]!=0)
		      kvset.add(new KeyVal(map.get(j), results[i][j]));

		  }	  
		  for(KeyVal kv:kvset){
			  sb.append(kv.toString());
		  }
		  FileUtil.writeString("D:\\crawer\\text\\keyword\\"+i+".txt", sb.toString());
	  }
   }
   
   @Test
   public void testIK() throws IOException{
	 /* String file= FileUtil.readString("D:\\crawer\\text\\test.txt","gb2312");
	  Dictionary.initial(DefaultConfig.getInstance());	  
      Dictionary dic=Dictionary.getSingleton();
      System.out.println(dic.matchInMainDict("妹谘".toCharArray()).isMatch());*/
   }
}
