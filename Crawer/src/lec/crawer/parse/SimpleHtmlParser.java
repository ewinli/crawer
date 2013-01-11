package lec.crawer.parse;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.util.List;

import jodd.http.HttpTransfer;

import lec.crawer.model.UrlItem;

public class SimpleHtmlParser extends BaseHtmlParser {

	public SimpleHtmlParser(HttpTransfer response, UrlItem item)
			throws UnsupportedEncodingException {
		super(response, item);
	}

	public List<UrlItem> getHtmlUrlList() {

		return null;
	}

	public List<UrlItem> getImageUrlList() {

		return null;
	}

	public IParseResult parse() throws MalformedURLException {

		return null;
	}

}
