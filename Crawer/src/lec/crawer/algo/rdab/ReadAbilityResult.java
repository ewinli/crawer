package lec.crawer.algo.rdab;

import java.util.Date;
import java.util.List;

import jodd.jerry.Jerry;
import jodd.util.StringUtil;

import lec.crawer.model.ImageItem;
import lec.crawer.model.RssItem;
import lec.crawer.model.UrlItem;
import lec.crawer.parse.IParseResult;

public class ReadAbilityResult implements IParseResult {
	
	private String content;
    
	private String title;
    
	private String rssTitle;
	
	private UrlItem nextPageUrl;
    
	private UrlItem provPageUrl;
    
	private List<UrlItem> relatePageUrls;
    
	private List<ImageItem> images;
	
    private UrlItem currentUrl;
    
    public ReadAbilityResult(UrlItem item){
    	  this.currentUrl=item;
  		if(currentUrl instanceof RssItem){
  			rssTitle= ((RssItem)currentUrl).getTitle();

  		 }
    }

	public UrlItem getNextPageUrl() {
		return nextPageUrl;
	}

	public void setNextPageUrl(UrlItem nextPageUrl) {
		this.nextPageUrl = nextPageUrl;
	}

	public UrlItem getProvPageUrl() {
		return provPageUrl;
	}

	public void setProvPageUrl(UrlItem provPageUrl) {
		this.provPageUrl = provPageUrl;
	}

	public List<UrlItem> getRelatePageUrls() {
		return relatePageUrls;
	}

	public void setRelatePageUrls(List<UrlItem> relatePageUrls) {
		this.relatePageUrls = relatePageUrls;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getTitle() {
		if(!StringUtil.isEmpty(rssTitle)){
			return rssTitle;
		}
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getOutput() {
		StringBuilder sb = new StringBuilder();
		sb.append("<html>");
		
		sb.append("<head>");
		 sb.append("<title>");
	     	sb.append(getTitle());
		 sb.append("</title>");
		sb.append("</head>");
		
		sb.append("<body>");

		sb.append("<h2>");
		 sb.append(getTitle());
		sb.append("</h2>");
		
        sb.append("<article>");
		  sb.append(getContent());
        sb.append("</article>");
		
        sb.append("<ul>");
        if(nextPageUrl!=null){
	     sb.append("<li>");
		   sb.append(nextPageUrl.getUrl());
	     sb.append("</li>");
        }
        if(provPageUrl!=null){
    	 sb.append("<li>");
		  sb.append(provPageUrl.getUrl());
		 sb.append("</li>");
        }
		for (UrlItem item : relatePageUrls) {
			sb.append("<li>");			
			sb.append(item.getUrl());
			sb.append("</li>");
		}
		sb.append("</ul>");
		
		sb.append("</body>");

		sb.append("</html>");
		
		return sb.toString();
	}

	public String getCleanContent() {
	        Jerry jerry=  Jerry.jerry(content);	        
	        String txt= jerry.text();
	        return title+"\r\n"+txt;
	}

	public Date getPublishedDate() {
		if(currentUrl instanceof RssItem){
			return ((RssItem)currentUrl).getPublishedDate();
		}
		return new Date();
	}

	public String getAuthor() {
		if(currentUrl instanceof RssItem){
			return ((RssItem)currentUrl).getAuthor();
		}
		return  currentUrl.getUri().getHost();
	}

	public String getSource() {	
		return currentUrl.getUri().getHost();
	}

	public UrlItem getCurrentPageUrl() {
		return currentUrl;
	}

	public String getDescription() {
		if(currentUrl instanceof RssItem){
			return ((RssItem)currentUrl).getDescription();
		}
		return "";
	}

	public void setImageList(List<ImageItem> list){
		this.images=list;
	}
	
	public List<ImageItem> getImageList() {
		return this.images;
	}

    
    
}
