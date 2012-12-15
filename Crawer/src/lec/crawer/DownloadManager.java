package lec.crawer;

import java.io.*;

import jodd.io.FileUtil;

import cn.uc.lec.cache.CacheOperator;

public class DownloadManager {
    private static String SavePath;
    
    public static void setSavePath(String path) throws IOException{
    	SavePath=path;
    	File file=new File(path);
    	if(!file.exists()){
				FileUtil.mkdirs(file);
    	}
    }
    
    public static String getSavePath(){
    	return SavePath;
    }
    
    

}
