package lec.crawer.parse;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import jodd.http.HttpTransfer;
import jodd.util.StringUtil;

import lec.crawer.algo.rdab.ReadAbility;
import lec.crawer.model.UrlItem;

public class SmartHtmlParser extends BaseHtmlParser{

	public ReadAbility readAbility;
	
	public IParseResult result;
	
	public SmartHtmlParser(HttpTransfer response, UrlItem item)
			throws UnsupportedEncodingException {
		super(response, item);
		readAbility=new ReadAbility(this.getContent(), item);

	}

	public List<UrlItem> getHtmlUrlList() {
		 if(result==null){
			 parse();
		 }
		 List<UrlItem> urlist=new ArrayList<UrlItem>();
		 if(result.getNextPageUrl()!=null&&!StringUtil.isEmpty(result.getNextPageUrl().getUrl())){
			 urlist.add(result.getNextPageUrl());
		 }
		List<UrlItem> relateList=result.getRelatePageUrls();
		 if(relateList!=null){
			 for(UrlItem item:relateList){
				 urlist.add(item);
			 }
		 }
		 return urlist;
	}

	public List<UrlItem> getImageUrlList() {
		return null;
	}

	public IParseResult parse() {
		if(result==null)
		   result=readAbility.getResult();
		return result;
	}

}
