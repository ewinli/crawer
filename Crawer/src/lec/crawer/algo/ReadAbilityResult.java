package lec.crawer.algo;

import java.util.List;

import lec.crawer.model.UrlItem;
import lec.crawer.parse.IParseResult;

public class ReadAbilityResult implements IParseResult {
	
    String content;
    
    String title;
    
    UrlItem nextPageUrl;
    
    UrlItem provPageUrl;
    
    List<UrlItem> relatePageUrls;
    

	/* (non-Javadoc)
	 * @see lec.crawer.algo.IParseResult#getNextPageUrl()
	 */
	public UrlItem getNextPageUrl() {
		return nextPageUrl;
	}
	/* (non-Javadoc)
	 * @see lec.crawer.algo.IParseResult#setNextPageUrl(lec.crawer.model.UrlItem)
	 */
	public void setNextPageUrl(UrlItem nextPageUrl) {
		this.nextPageUrl = nextPageUrl;
	}
	/* (non-Javadoc)
	 * @see lec.crawer.algo.IParseResult#getProvPageUrl()
	 */
	public UrlItem getProvPageUrl() {
		return provPageUrl;
	}
	/* (non-Javadoc)
	 * @see lec.crawer.algo.IParseResult#setProvPageUrl(lec.crawer.model.UrlItem)
	 */
	public void setProvPageUrl(UrlItem provPageUrl) {
		this.provPageUrl = provPageUrl;
	}
	/* (non-Javadoc)
	 * @see lec.crawer.algo.IParseResult#getRelatePageUrls()
	 */
	public List<UrlItem> getRelatePageUrls() {
		return relatePageUrls;
	}
	/* (non-Javadoc)
	 * @see lec.crawer.algo.IParseResult#setRelatePageUrls(java.util.List)
	 */
	public void setRelatePageUrls(List<UrlItem> relatePageUrls) {
		this.relatePageUrls = relatePageUrls;
	}
	/* (non-Javadoc)
	 * @see lec.crawer.algo.IParseResult#getContent()
	 */
	public String getContent() {
		return content;
	}
	/* (non-Javadoc)
	 * @see lec.crawer.algo.IParseResult#setContent(java.lang.String)
	 */
	public void setContent(String content) {
		this.content = content;
	}
	/* (non-Javadoc)
	 * @see lec.crawer.algo.IParseResult#getTitle()
	 */
	public String getTitle() {
		return title;
	}
	/* (non-Javadoc)
	 * @see lec.crawer.algo.IParseResult#setTitle(java.lang.String)
	 */
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
