package lec.crawer.model;

import java.net.URI;

import cn.uc.easy.utils.MD5Utils;

public class UrlItem implements IItem {
	private String url;
	private URI uri;

    
    
	public UrlItem(String url){
		this.url=url;
		this.uri=URI.create(url);
	}
	
	public String getUrl() {
		return url;
	}



	public URI getUri() {
		return uri;
	}

	public String getKey() {
		return MD5Utils.encodeByMD5(url);
	}



}
