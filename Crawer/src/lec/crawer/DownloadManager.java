package lec.crawer;

import java.io.*;

import jodd.io.FileUtil;

import cn.uc.lec.cache.CacheOperator;

public class DownloadManager {
    private static String sourcePath;
    private static String ResultPath;
    
    public static String getResultPath() {
		return ResultPath;
	}

	public static void setResultPath(String resultPath) throws IOException {
		ResultPath = resultPath;
	  	File file=new File(resultPath);
    	if(!file.exists()){
				FileUtil.mkdirs(file);
    	}
	}

	public static void setSource(String path) throws IOException{
    	sourcePath=path;
    	File file=new File(path);
    	if(!file.exists()){
				FileUtil.mkdirs(file);
    	}
    }
    
    public static String getSourcePath(){
    	return sourcePath;
    }
    
    

}
