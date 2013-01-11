package lec.crawer.algo.rdab.ext;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;

import jodd.jerry.Jerry;
import jodd.lagarto.dom.Node;
import jodd.util.StringUtil;
import lec.crawer.Util;
import lec.crawer.model.ImageItem;
import lec.crawer.model.UrlItem;

public class ParseImageList  implements IParseExt<List<ImageItem>> {
	 private static final ThreadLocal<ParseImageList> holder=new ThreadLocal<ParseImageList>();
	private Jerry document;
	private UrlItem currentUrl;
	 
	 private ParseImageList(Jerry document, UrlItem currentUrl) {
		 this.document=document;
		 this.currentUrl=currentUrl;

	}

	public static ParseImageList getInstance(Jerry document,UrlItem currentUrl){
		ParseImageList parser=holder.get();
		 if(parser==null){
			 parser=new ParseImageList(document, currentUrl);
			 holder.set(parser);
		 }
		 parser.document=document;
		 parser.currentUrl=currentUrl;
		 return parser;
	 }

	public List<ImageItem> parse() throws MalformedURLException {
	    List<ImageItem> list=new ArrayList<ImageItem>();
		Node[] nodes=this.document.$("img").get();
		for(Node node:nodes){
			String src=node.getAttribute("src");
			src=Util.getAbsouluteUrl(src, currentUrl);
			if(!StringUtil.isEmpty(src)){
			   node.setAttribute("src", src);
			   list.add(new ImageItem(src));
			}
		}
		return list;
	}
}
