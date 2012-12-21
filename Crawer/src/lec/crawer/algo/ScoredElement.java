package lec.crawer.algo;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import jodd.util.FastSort;


public  class ScoredElement<T> implements Comparable<ScoredElement<T>>{
	public double score=0;
	public T element;   	
    public boolean desc=true;
	
	public ScoredElement(double score,T element){
		this.score=score;
		this.element=element;
	}
	
	public T getTopElement(Collection<ScoredElement<T>> scoreList){
	 ScoredElement<T> topUrl=new ScoredElement<T>(0, null);
		 
		 for(ScoredElement<T> url :scoreList){
			 if(topUrl.element==null||topUrl.score<url.score){
				topUrl=url;
			 }
		 }
		   return topUrl.element;
	}
	public T getTopElement(Collection<ScoredElement<T>> scoreList,double minScore){
   	 ScoredElement<T> topUrl=new ScoredElement<T>(score, element);
  		 
  		 for(ScoredElement<T> url :scoreList){
  			 if(topUrl.element==null||topUrl.score<url.score){
  				topUrl=url;
  			 }
  		 }
  		 if(topUrl.score<=minScore){
  			 return null;
  		 }
  		   return topUrl.element;
   	}
	
	public List<T> getTopElements(Collection<ScoredElement<T>> scoreList,double minScore,int maxCount){
		ScoredElement[] array=new ScoredElement[scoreList.size()];
		if(maxCount>array.length){
			maxCount=array.length;
		}
		scoreList.toArray(array);
		FastSort.sort(array);    		
		List<T> result=new ArrayList<T>();
		int i=0;
		for(ScoredElement<T> ele : array){
			if(ele.score>=minScore&&(i++)<maxCount){
				result.add(ele.element);
			}
		}
		return result;
	}

	public int compareTo(ScoredElement<T> o) {
		int i=desc?-1:1;
		if(this.score-o.score>0){
			return 1*i;
		}else if(this.score-o.score<0){
			return -1*i;
		}			
		return 0;
	}


}
