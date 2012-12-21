package lec.crawer;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import jodd.http.HttpTransfer;
import jodd.jerry.Jerry;
import jodd.jerry.Jerry.JerryParser;
import jodd.lagarto.dom.Node;
import jodd.util.StringUtil;

import lec.crawer.model.HtmlItem;
import lec.crawer.model.UrlItem;
import lec.crawer.parse.CnbetaHtmlParser;
import lec.crawer.parse.IHtmlParser;
import lec.crawer.parse.SmartHtmlParser;

public class ParserFactory {
	
	
	public static IHtmlParser getHtmlParser(HttpTransfer response, UrlItem item)
			throws UnsupportedEncodingException {
		return new SmartHtmlParser(response, item);
	}

	

}
