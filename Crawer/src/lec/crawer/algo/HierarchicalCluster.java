package lec.crawer.algo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;


public class HierarchicalCluster {
    public static class BiCluster{
    	private int id;
    	private double[] vec;
    	private double distance;
    	private BiCluster left;
    	private BiCluster right;
    	private BiCluster parent;
		public BiCluster getParent() {
			return parent;
		}
		public void setParent(BiCluster parent) {
			this.parent = parent;
		}
		public int getId() {
			return id;
		}
		public void setId(int id) {
			this.id = id;
		}
		public double[] getVec() {
			return vec;
		}
		public void setVec(double[] vec) {
			this.vec = vec;
		}
		public double getDistance() {
			return distance;
		}
		public void setDistance(double distance) {
			this.distance = distance;
		}
		public BiCluster getLeft() {
			return left;
		}
		public void setLeft(BiCluster left) {
			this.left = left;
		}
		public BiCluster getRight() {
			return right;
		}
		public void setRight(BiCluster right) {
			this.right = right;
		}
    	
    	
    	
    }


    
    
    private List<BiCluster> clusers=new ArrayList<BiCluster>();
    
    private ISimilarity similarity;
    
    private double[][] rows;
    
    public HierarchicalCluster(double[][]rows,ISimilarity distance){
    	this.rows=rows;
    	for(int i=0;i<rows.length;i++){
    		BiCluster cluster=new BiCluster();
    		cluster.id=i;
    		cluster.vec=rows[i];
    		clusers.add(cluster);
    	}
    	this.similarity=distance;   	
    }
    
    
    private BiCluster merge(BiCluster b1,BiCluster b2,double dis){
    	double[] vec=new double[b1.vec.length];
    	for(int i=0;i<b1.vec.length;i++){
    		vec[i]=(b1.vec[i]+b2.vec[i])/2;
    	}
    	BiCluster newCluster=new BiCluster();
    	newCluster.vec=vec;
    	newCluster.id=-1;
    	if(b1!=null)
    	b1.parent=newCluster;
    	if(b2!=null)
    	b2.parent=newCluster;
    	newCluster.left=b1;
    	newCluster.right=b2;
    	newCluster.distance=dis;	
    	
    	return newCluster;
    }
    
    public BiCluster run(){
    	Map<String,Double> idset=new HashMap<String,Double>();
    	while(clusers.size()>1){
    		int[] lowest=new int[]{0,1};
    		double closest=similarity.get(clusers.get(0).vec, clusers.get(1).vec);
    		for(int i=0;i<clusers.size();i++){
    			for(int j=i+1;j<clusers.size();j++){
    				String key=clusers.get(i).id+","+clusers.get(j).id;
    				if(!idset.containsKey(key)){
    					idset.put(key, similarity.get(clusers.get(i).vec,clusers.get(j).vec));
    				}
    				double dis=idset.get(key);
    				if(dis>closest){
    					lowest=new int[]{i,j};
    					closest=dis;
    				}    				
    			}
    		}
    		BiCluster left=clusers.get(lowest[0]);
    		BiCluster right=clusers.get(lowest[1]);
    		BiCluster newBiCluster=merge(left, right, closest);
            clusers.remove(left);
            clusers.remove(right);    
    		clusers.add(newBiCluster);
    	}
    	
    	return clusers.get(0);
    }
    
 
    private void printWhite(int count){
    	for(int i=0;i<count;i++){
    		System.out.print(" ");
    	}
    	
    }
    public void printTree(BiCluster tree,int n){
    	if(tree==null) return;
    	printWhite(n);
    	if(tree.id<0){
    		System.out.println("->");
    	}else{
    	  System.out.println("["+Distance.printVec(tree.vec)+"]");
    	}
    	printTree(tree.left,n+1);
    	printTree(tree.right,n+1);
    	
    }
    public void printTree1(BiCluster tree){
    	 
        LinkedList<BiCluster> queue=new LinkedList<BiCluster>();
    	queue.add(tree); 
        int i=0;

        while(!queue.isEmpty()){
        	BiCluster now=queue.removeFirst();
        	       	
     

        	if(now.parent==null){
        		++i;
        		 System.out.println("["+now.id+"]"+now.distance);
        	}
        	else{
        		System.out.print("["+now.parent.hashCode()+"]");
            	printWhite((++i)/2);
         		System.out.print("["+now.hashCode()+"]");
        		if(now.parent.left==now){
  
        		    System.out.println("left>"+now.id+">>"+now.distance+">>"+Distance.printVec(now.vec));
        		  }
        		if(now.parent.right==now){
        		
        		    System.out.println("right>"+now.id+">>"+now.distance+">>"+Distance.printVec(now.vec));
        		 }
        	}
        	if(now.left!=null){

        		queue.add(now.left);
        	}
        	if(now.right!=null){
        
        		queue.add(now.right);
        	}
        }
 
    
    }
    
    public static void main(String[] args) {
		double[][] rows=new double[][]{
				{1, 1}, {2, 1}, {1, 2},
				{2, 2}, {2, 0}, {8, 8},
				{9, 8}, {8, 9}, {9, 9},
				{19, 16}, {16, 11}, {17, 15},
				{9, 18}, {8, 19}, {20, 19},
				{20, 18}, {18, 19}, {21, 19}
		};
		
		HierarchicalCluster cluster=new HierarchicalCluster(rows,new ISimilarity() {
			

			public double get(double[] row1, double[] row2) {
				 return Distance.EuclideanDistanceSimilarity(row1, row2);
			}
		});
		
		BiCluster biCluster= cluster.run();
		cluster.printTree(biCluster,0);
		
	}
    
    
}
