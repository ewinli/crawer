package lec.crawer.algo;

public class Distance {
	   public static String printVec(double[] vec){
	    	if(vec==null) return "";
	    	StringBuilder sb=new StringBuilder();
	    	for(int i=0;i<vec.length;i++){
	    		sb.append(vec[i]+",");
	    	}
	    	return sb.toString();
	    }
	
    //欧几里德距离
    public static double EuclideanDistance(final double[] xs, final double[] ys) {
        int N = xs.length;
        double val = 0;
        for (int i = 0; i < N; i++) {
            val += (xs[i] - ys[i]) * (xs[i] - ys[i]);
        }
        return Math.sqrt(val);
    }
    //欧几里德距离相似度 1/1+e(x,y)
    public static double EuclideanDistanceSimilarity(final double[] xs, final double[] ys) {
        return 1 / (1 + EuclideanDistance(xs, ys));
    }
    
  //平均概率期望，即求平均数
    public static double Expect(final double[] xs) {
        double N = xs.length;
        double p = 1 / N;
        double val = 0;
        for (int i = 0; i < N; i++) {
            val += xs[i] * p;
        }
        return val;
    }
    //协方差
    public static double Covariance(final double[] xs, final double[] ys) {
        int N = xs.length;
        //协方差公式
        double val1 = 0;
        double ex =Expect(xs);
        double ey =Expect(ys);
        for (int i = 0; i < N; i++) {
            val1 += (xs[i]-ex) * (ys[i]-ey);
        }
        return val1/N;
    }
    //方差
    public static double Variance(final double[] xs) {
        //方差=两个变量相同时的标准差
        return Covariance(xs, xs);
    }
    
    //标准差
    public static double StandardDeviation(final double[] xs) {
        return Math.sqrt(Variance(xs));
    }
  //皮尔逊相关系数=协方差(x,y)/[标准差(x)*标准差(y)]
    public static double PearsonCorrelation(double[] xs, double[] ys) {
        return Covariance(xs, ys) / (StandardDeviation(xs) * StandardDeviation(ys));
    }
    
  //点积,定义两个向量相乘 两个三维向量点积 [1, 3, −5]*[4, −2, −1]=1*4+2*(-2)+(-5)*(-1)
    public static double DotProduct(final double[] xs, final double[] ys) {
        int N = xs.length;
        double val = 0;
        for (int i = 0; i < N; i++) {
            val += xs[i] * ys[i];
        }
        return val;
    }   
 //余弦相似度 =a*b/(|a|*|b|) a,b为向量
    public static double CosineSimilarity(double[] xs, double[] ys) {
       //return DotProduct(xs, ys)/(Math.sqrt(DotProduct(xs, xs)*DotProduct(ys, ys)));
        double valxy = 0;
        double valx = 0;
        double valy = 0;
        for (int i = 0; i < xs.length; i++) {
            valxy += xs[i] * ys[i];
            valx += xs[i] * xs[i];
            valy += ys[i] * ys[i];
        }
        return valxy / Math.sqrt(valx * valy);
    }
    
  //余弦相似度 =a*b/(|a|*|b|) a,b为向量
    public static double AdjustedCosineSimilarity(double[] xs, double[] ys) {
        // return DotProduct(xs, ys)/(Math.sqrt(DotProduct(xs, xs)*DotProduct(ys, ys)));
        double valxy = 0;
        double valx = 0;
        double valy = 0;
        double ex=Expect(xs);
        double ey=Expect(ys);
        //对余弦相似度进行调整，每一个向量减去该向量的均值
        for (int i = 0; i < xs.length; i++) {
            valxy += (xs[i]-ex) * (ys[i]-ey);
            valx += (xs[i]-ex) * (xs[i]-ex);
            valy += (ys[i]-ey) * (ys[i]-ey);
        }
        return valxy / Math.sqrt(valx * valy);
    }
}
