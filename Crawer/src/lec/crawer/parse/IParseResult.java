package lec.crawer.parse;

import java.util.Date;
import java.util.List;

import lec.crawer.model.ImageItem;
import lec.crawer.model.UrlItem;

public interface IParseResult {

	public UrlItem getCurrentPageUrl();
	
	public  UrlItem getNextPageUrl();
	
	public  UrlItem getProvPageUrl();
	
	public  List<UrlItem> getRelatePageUrls();

	public List<ImageItem> getImageList();
	
	public String getDescription();
	
	public  String getContent();

	public  String getTitle();

	public String getOutput();
	
	public String getCleanContent();
	
	public Date getPublishedDate();
	
	public String getAuthor();
	
	public String getSource();
	
	
}