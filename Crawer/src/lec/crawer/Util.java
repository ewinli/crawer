package lec.crawer;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

import jodd.io.FileUtil;
import jodd.util.StringUtil;
import lec.crawer.model.UrlItem;

public class Util {
	
	/**
	 * 返回当前路径的绝对路径
	 * */
	public static  String getAbsouluteUrl(String url,UrlItem currentUrl) {
		String host = currentUrl.getUri().getHost();
		int port = currentUrl.getUri().getPort();
		String path = currentUrl.getUri().getPath();
		String protocol = currentUrl.getUri().getProtocol();
		StringBuilder sb = new StringBuilder();
		if (StringUtil.isEmpty(url)) {
			return currentUrl.getUrl();
		}
		if (url.toLowerCase().startsWith("javascript")) {
			return "";
		} else if (url.indexOf("http://") != -1
				|| url.indexOf("https://") != -1) {
			return url;
		} else if (url.startsWith("/")) {
			sb.append(protocol);
			sb.append("://");
			sb.append(host);
			if (port != -1) {
				sb.append(port);
			}
			sb.append(url);
			return sb.toString();
		} else {
			String[] basePath = path.split("/");
			String[] toPath = url.split("/");
			sb.append(protocol);
			sb.append("://");
			sb.append(host);
			if (port != -1) {
				sb.append(port);
			}
			for (int i = 0; i < basePath.length; i++) {
				if (i < toPath.length) {
					if (basePath[i] != toPath[i]) {
						sb.append("/").append(basePath[i]);
					} else {
						sb.append("/");
						break;
					}
				}
			}
			sb.append(url);
			return sb.toString();
		}

	}
	
	public synchronized static void mkdirs(String path) throws IOException{
	  	File file=new File(path);
    	if(!file.exists()){
			FileUtil.mkdirs(file);
    	}
	}
	
	public static String getString(String txt,String baseEncode,String toEncode) throws UnsupportedEncodingException{
		return new String(txt.getBytes(baseEncode),toEncode);
	}
}
