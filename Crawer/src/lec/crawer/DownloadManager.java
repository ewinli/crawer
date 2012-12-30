package lec.crawer;

import java.io.*;

import jodd.io.FileUtil;

import cn.uc.lec.cache.CacheOperator;

public class DownloadManager {
    private static String sourcePath;
    private static String resultPath;
    private static String userAgent="Mozilla/5.0 (iPad; CPU OS 5_0 like Mac OS X) AppleWebKit/534.46 (KHTML, like Gecko) Version/5.1 Mobile/9A334 Safari/7534.48.3";
    public static String getUserAgent() {
		return userAgent;
	}

	public static void setUserAgent(String userAgent) {
		DownloadManager.userAgent = userAgent;
	}

	public static String getResultPath() {
		return resultPath;
	}

	public static void setResultPath(String result) throws IOException {
		resultPath = result;
	    mkdirs(result);
	}

	public static void setSource(String path) throws IOException{
    	sourcePath=path;
        mkdirs(path);
    }
	
	public static void mkdirs(String path) throws IOException{
	  	File file=new File(path);
    	if(!file.exists()){
				FileUtil.mkdirs(file);
    	}
	}
    
    public static String getSourcePath(){
    	return sourcePath;
    }
    
    

}
