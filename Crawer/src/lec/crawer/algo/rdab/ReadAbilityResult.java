package lec.crawer.algo.rdab;

import java.util.List;

import lec.crawer.model.UrlItem;
import lec.crawer.parse.IParseResult;

public class ReadAbilityResult implements IParseResult {
	
    String content;
    
    String title;
    
    UrlItem nextPageUrl;
    
    UrlItem provPageUrl;
    
    List<UrlItem> relatePageUrls;
    


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
	     	sb.append(title);
		 sb.append("</title>");
		sb.append("</head>");
		
		sb.append("<body>");

		sb.append("<h2>");
		 sb.append(title);
		sb.append("</h2>");
		
        sb.append("<article>");
		  sb.append(content);
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

    
    
}
