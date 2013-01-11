package lec.crawer.model;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;

import cn.uc.easy.utils.MD5Utils;

public class UrlItem implements IItem {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String url;
	private URL uri;
    private String type="html";
	public UrlItem(String url) throws MalformedURLException{
		this.url=url.trim();	
		this.uri=new URL(url);
	}
	
	public void setUrl(String url) throws MalformedURLException{
		this.url=url.trim();	
		this.uri=new URL(url);
	}
	
	public String getUrl() {
		return url;
	}



	public URL getUri() {
		return uri;
	}

	public String getKey() {
		return MD5Utils.encodeByMD5(url);
	}

	public void save() throws IOException{
		
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}



}
