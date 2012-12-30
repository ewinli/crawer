package lec.crawer.algo.rdab;

import java.util.regex.Pattern;

import jodd.util.StringUtil;

public class ReadAbilityUtil {
	/**
	 * 匹配字符串
	 * */
	public static boolean match(Pattern pattern,String str){
		if(StringUtil.isEmpty(str)||pattern==null) return false;
		return pattern.matcher(str).find();
	}
}
