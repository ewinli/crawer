package lec.crawer.parse;

import java.util.List;

import lec.crawer.model.UrlItem;

public interface IParseResult {

	public  UrlItem getNextPageUrl();

	public  void setNextPageUrl(UrlItem nextPageUrl);

	public  UrlItem getProvPageUrl();

	public  void setProvPageUrl(UrlItem provPageUrl);

	public  List<UrlItem> getRelatePageUrls();

	public  void setRelatePageUrls(List<UrlItem> relatePageUrls);

	public  String getContent();

	public  void setContent(String content);

	public  String getTitle();

	public  void setTitle(String title);

	public String getOutput();
}