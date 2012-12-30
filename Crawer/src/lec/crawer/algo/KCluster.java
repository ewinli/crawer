package lec.crawer.algo;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;



public class KCluster {
	
	public static interface CenterPointsMaker{
		public List<double[]> makeKCenterPoints( double[][]rows,int k);
	}
	
	private double[][]rows;
	
	private int k=4;
	
	private CenterPointsMaker maker;
	
	public int getK() {
		return k;
	}

	public void setK(int k) {
		this.k = k;
	}

	private ISimilarity similarity;
	
	
	
    public KCluster(double[][]rows,ISimilarity distance){
    	this.rows=rows;
    	this.similarity=distance;    	
    }
    
    
    public KCluster(double[][]rows,ISimilarity distance,CenterPointsMaker maker){
    	this.rows=rows;
    	this.similarity=distance;    	
    	this.maker=maker;
    }
    
    
    private double max(double[][] rows,int i){
    	double max=rows[0][i];
    	for(double[]row:rows){
    		if(row[i]>max){
    			max=row[i];
    		}
    	}
    	return max;
    }
    private double min(double[][] rows,int i){
    	double min=rows[0][i];
    	for(double[]row:rows){
    		if(row[i]<min){
    			min=row[i];
    		}
    	}
    	return min;
    }

    private List<double[]> makeKCenterPoints(){
    	if(maker!=null){
    		return maker.makeKCenterPoints(rows,k);
    	}
    	List<double[]> clusters=new ArrayList<double[]>();
    	List<double[]> ranges=new ArrayList<double[]>();
    	int length=this.rows[0].length;
    	for(int i=0;i<length;i++){
    		ranges.add(new double[]{min(rows,i),max(rows,i)});
    	}
    	Random rand=new Random();
    	for(int j=0;j<k;j++){
    		double[] row=new double[length];
    		for(int i=0;i<length;i++){
    			row[i]=(rand.nextDouble()*(ranges.get(i)[1]-ranges.get(i)[0])+ranges.get(i)[0]);
    	  	}
    		clusters.add(row);
    		System.out.println(Distance.printVec(row));
    	}   	
    	
    	
    	
    	return clusters;
    }
    
    private boolean equalMatches(List<Integer>[] m1,List<Integer>[]m2){
    	if(m1==null||m2==null) return false;
    	if(m1.length!=m2.length) return false;
    	
    	boolean result=true;
    	for(int i=0;i<m1.length;i++){
  		  if(m1[i].size()!=m2[i].size()){
			  return false;
		  }
    	  for(int j=0;j<m1[i].size();j++){
    		  result&=(m1[i].get(j)==m2[i].get(j));
    	  }
    	}
    	   	
    	return result;
    }
    
    @SuppressWarnings("unchecked")
	public List<Integer>[] run(){
    	List<double[]> centerPonits=makeKCenterPoints();
    	List<Integer>[] lastMatches=null;
    	List<Integer>[] bestMatches=null;
    	for(int i=0;i<100;i++){
    	   bestMatches=new ArrayList[k];
    	   for(int b=0;b<k;b++){
    		   bestMatches[b]=new ArrayList<Integer>();
    	   }
    	   for(int j=0;j<rows.length;j++){
    		  double[]row=rows[j];
    		  int best=0;
    		  for(int l=0;l<k;l++){
    			 double d=similarity.get(row, centerPonits.get(l));
    			 if(d>similarity.get(row, centerPonits.get(best))){
    				 best=l;
    			 }
    		  }
    		  bestMatches[best].add(j);
    	   }	
    	   if(equalMatches(bestMatches,lastMatches)){
    		   break;
    	   }
    	   lastMatches=bestMatches;

    	   for(int m=0;m<k;m++){
        	   
        	 double[] avg=new double[rows[0].length];
    		 if(bestMatches[m]!=null&&bestMatches[m].size()>0){
    			 for(int rowId :bestMatches[m]){
    			    for(int ss=0;ss<rows[rowId].length;ss++){
    			    	avg[ss]+=rows[rowId][ss];
    			    }	 
    			 }
    			 for(int t=0;t<avg.length;t++){
    				 avg[t]=avg[t]/bestMatches[m].size();
    			 }
    		  centerPonits.set(m, avg);
    		 }   		      		   
    	   }  	 
         System.out.println(i);
    	}   	     	   	
    	return bestMatches;
    }
    
    
    public void print( List<Integer>[] result){
    	for(int i=0;i<result.length;i++){
    	  
    	System.out.println("group ["+i+"]");
    	   for(int rowId:result[i]){
    		   System.out.print("row["+rowId+"](");
    		   for(double value:rows[rowId]){
    			   System.out.print(value+",");
    		   }
    		   System.out.println(")");
    	   }	
    	  System.out.println();
    	}
    	   	
    }
    
    public static void main(String[] args) {
    		double[][] rows=new double[][]{
				{1, 1,2}, {2, 1,3}, {1, 2,5},
				{2, 2,1}, {2, 0,80}, {8, 8,9},
				{9, 8,5}, {8, 9,7}, {9, 9,8},
				{19, 16,9}, {16, 11,9}, {17, 15,7},
				{91, 18,4}, {81, 19,5}, {100, 19,5},
				{120, 18,6}, {118, 19,55}, {121, 19,4}
		};

    		
		KCluster kCluster=new KCluster(rows, new ISimilarity(){

			public double get(double[] row1, double[] row2) {
				return Distance.EuclideanDistanceSimilarity(row1, row2);
			}
			
		});
		kCluster.setK(2);
		List<Integer>[] result= kCluster.run();
        kCluster.print(result);
	}
    
}
