package lec.crawer.parse;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;

import jodd.http.HttpTransfer;
import jodd.util.StringUtil;

import lec.crawer.algo.rdab.ReadAbility;
import lec.crawer.model.HtmlItem;
import lec.crawer.model.UrlItem;

public class SmartHtmlParser extends BaseHtmlParser{

	public ReadAbility readAbility;
	
	public IParseResult result;
	
	public SmartHtmlParser(HtmlItem item) throws UnsupportedEncodingException{
		super(item);
		readAbility=new ReadAbility(this.getContent(), item.getUrlItem());
	}
	
	public SmartHtmlParser(HttpTransfer response, UrlItem item)
			throws UnsupportedEncodingException {
		super(response, item);
		readAbility=new ReadAbility(this.getContent(), item);

	}

	public List<UrlItem> getHtmlUrlList() {
		 List<UrlItem> urlist=new ArrayList<UrlItem>();
		 return urlist;
	}

	public List<UrlItem> getImageUrlList() {
		return null;
	}

	public IParseResult parse() throws MalformedURLException {
		if(result==null)
		   result=readAbility.getResult();
		return result;
	}

}
