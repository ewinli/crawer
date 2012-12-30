package lec.crawer.algo.rdab;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class ReadAbilityConfig {



	private static Map<String,String> styleClass=new HashMap<String, String>();
	static{
		styleClass.put("text", "reability_text");
	}
	
	private static int minReleateScore=80;
	
	private static int minTitleLength = 4;

	private static int paragraphSegmentLength = 100;

	private static int maxPointsForSegmentsCount = 3;

	private static int minParagraphLength = 25;

	private static int minInnerTextLength = 25;

	private static int minSiblingParagraphLength = 80;

	private static int minCommaSegments = 10;

	private static int lisCountTreshold = 100;

	private static int maxImagesInShortSegmentsCount = 2;

	private static int minInnerTextLengthInElementsWithEmbed = 75;

	private static int classWeightTreshold = 25;

	private static int maxEmbedsCount = 1;

	private static int maxArticleTitleLength = 150;

	private static int minArticleTitleLength = 15;

	private static int minArticleTitleWordsCount1 = 3;

	private static int minArticleTitleWordsCount2 = 4;

	private static float siblingScoreTresholdCoefficient = 0.2f;

	private static float maxSiblingScoreTreshold = 10.0f;

	private static float maxSiblingParagraphLinksDensity = 0.25f;

	private static float maxHeaderLinksDensity = 0.33f;

	private static float maxDensityForElementsWithSmallerClassWeight = 0.2f;

	private static float maxDensityForElementsWithGreaterClassWeight = 0.5f;

	private static Pattern unlikelyCandidatesRegex = Pattern
			.compile(
					"combx|comment|community|disqus|extra|foot|header|menu|remark|rss|shoutbox|sidebar|side|sponsor|ad-break|agegate|pagination|pager|popup|tweet|twitter",
					Pattern.CASE_INSENSITIVE);

	private static Pattern okMaybeItsACandidateRegex = Pattern.compile(
			"and|article|body|column|main|shadow", Pattern.CASE_INSENSITIVE);

	private static Pattern positiveWeightRegex = Pattern
			.compile(
					"article|body|content|entry|hentry|main|page|pagination|post|text|blog|story",
					Pattern.CASE_INSENSITIVE);

	private static Pattern negativeWeightRegex = Pattern
			.compile(
					"combx|comment|com-|contact|foot|footer|footnote|masthead|media|meta|outbrain|promo|related|scroll|shoutbox|sidebar|side|sponsor|shopping|tags|tool|widget",
					Pattern.CASE_INSENSITIVE);

	private static Pattern negativeLinkParentRegex = Pattern
			.compile(
					"(stories|articles|news|documents|posts|notes|series|historie|artykuly|artykuły|wpisy|dokumenty|serie|geschichten|erzählungen|erzahlungen)",
					Pattern.CASE_INSENSITIVE);

	private static Pattern extraneous = Pattern
			.compile(
					"print|archive|comment|discuss|e[-]?mail|share|reply|all|login|sign|single|also",
					Pattern.CASE_INSENSITIVE);

	private static Pattern divToPElementsRegex = Pattern.compile(
			"<(a|blockquote|dl|div|img|ol|p|pre|table|ul)",
			Pattern.CASE_INSENSITIVE);

	private static Pattern endOfSentenceRegex = Pattern.compile("\\.( |$)",
			Pattern.CASE_INSENSITIVE);

	private static Pattern breakBeforeParagraphRegex = Pattern.compile(
			"<br[^>]*>\\s*<p", Pattern.CASE_INSENSITIVE);

	private static Pattern normalizeSpacesRegex = Pattern.compile("\\s{2,}",
			Pattern.CASE_INSENSITIVE);

	private static Pattern killBreaksRegex = Pattern.compile(
			"(<br\\s*\\/?>(\\s|&nbsp;?)*){1,}", Pattern.CASE_INSENSITIVE);

	private static Pattern videoRegex = Pattern.compile(
			"http:\\/\\/(www\\.)?(youtube|vimeo)\\.com",
			Pattern.CASE_INSENSITIVE);

	private static Pattern replaceDoubleBrsRegex = Pattern.compile(
			"(<br[^>]*>[ \\n\\r\\t]*){2,}", Pattern.CASE_INSENSITIVE);

	private static Pattern replaceFontsRegex = Pattern.compile(
			"<(\\/?)font[^>]*>", Pattern.CASE_INSENSITIVE);

	private static Pattern articleTitleRegex = Pattern.compile(".*?title.*?",
			Pattern.CASE_INSENSITIVE);

	private static Pattern nextLink = Pattern
			.compile(
					"(next|weiter|continue|dalej|następna|nastepna>([^\\|]|$)|�([^\\|]|$))",
					Pattern.CASE_INSENSITIVE);

	private static Pattern nextText = Pattern.compile(
			".*?((下一|下)(页|篇|章|节|话)|next).*?", Pattern.CASE_INSENSITIVE);

	private static Pattern prevText = Pattern.compile(
			".*?((上一|上)(页|篇|章|节|话)|prev(ious)).*?", Pattern.CASE_INSENSITIVE);
	
	private static Pattern relateText=Pattern.compile(
			".*?(相关|推荐|其他|新闻|资讯|(你|您)还可能|" +
			"story|article|news|document|post|note|series" +
			"|historia|artykul|artykuł|wpis|dokument|seria" +
			"|geschichte|erzählung|erzahlung|artikel|serie).*?",Pattern.CASE_INSENSITIVE);
	
	private static Pattern nextStoryLink = Pattern
			.compile(
					"(story|article|news|document|post|note|series|historia|artykul|artykuł|wpis|dokument|seria|geschichte|erzählung|erzahlung|artikel|serie)",
					Pattern.CASE_INSENSITIVE);

	private static Pattern prevLink = Pattern.compile(
			"(prev|earl|[^b]old|new|wstecz|poprzednia|<|�)",
			Pattern.CASE_INSENSITIVE);

	private static Pattern pageRegex = Pattern
			.compile("pag(e|ing|inat)|([^a-z]|^)pag([^a-z]|$)",
					Pattern.CASE_INSENSITIVE);

	private static Pattern likelyParagraphDivRegex = Pattern.compile(
			"text|para|parbase", Pattern.CASE_INSENSITIVE);

	
	private static Pattern changeToDiv = Pattern.compile(
			"span|label|h3", Pattern.CASE_INSENSITIVE);
	
	public static Pattern getChangeToDiv() {
		return changeToDiv;
	}

	public static int getMinReleateScore() {
		return minReleateScore;
	}

	public static Pattern getRelateText() {
		return relateText;
	}

	public static Pattern getPrevText() {
		return prevText;
	}

	public static int getMinParagraphLength() {
		return minParagraphLength;
	}

	public static int getMinInnerTextLength() {
		return minInnerTextLength;
	}

	public static int getMinSiblingParagraphLength() {
		return minSiblingParagraphLength;
	}

	public static int getMinCommaSegments() {
		return minCommaSegments;
	}

	public static int getLisCountTreshold() {
		return lisCountTreshold;
	}

	public static int getMaxImagesInShortSegmentsCount() {
		return maxImagesInShortSegmentsCount;
	}

	public static int getMinInnerTextLengthInElementsWithEmbed() {
		return minInnerTextLengthInElementsWithEmbed;
	}

	public static int getClassWeightTreshold() {
		return classWeightTreshold;
	}

	public static int getMaxEmbedsCount() {
		return maxEmbedsCount;
	}

	public static int getMaxArticleTitleLength() {
		return maxArticleTitleLength;
	}

	public static int getMinArticleTitleLength() {
		return minArticleTitleLength;
	}

	public static int getMinArticleTitleWordsCount1() {
		return minArticleTitleWordsCount1;
	}

	public static int getMinArticleTitleWordsCount2() {
		return minArticleTitleWordsCount2;
	}

	public static float getSiblingScoreTresholdCoefficient() {
		return siblingScoreTresholdCoefficient;
	}

	public static float getMaxSiblingScoreTreshold() {
		return maxSiblingScoreTreshold;
	}

	public static float getMaxSiblingParagraphLinksDensity() {
		return maxSiblingParagraphLinksDensity;
	}

	public static float getMaxHeaderLinksDensity() {
		return maxHeaderLinksDensity;
	}

	public static float getMaxDensityForElementsWithSmallerClassWeight() {
		return maxDensityForElementsWithSmallerClassWeight;
	}

	public static float getMaxDensityForElementsWithGreaterClassWeight() {
		return maxDensityForElementsWithGreaterClassWeight;
	}

	

	public static Map<String, String> getStyleClass() {
		return styleClass;
	}

	public static int getParagraphSegmentLength() {
		return paragraphSegmentLength;
	}

	public static int getMaxPointsForSegmentsCount() {
		return maxPointsForSegmentsCount;
	}

	public static int getMinTitleLength() {
		return minTitleLength;
	}

	public static int getMaxTitleLength() {
		return maxTitleLength;
	}

	private static int maxTitleLength = 50;

	public static Pattern getNextText() {
		return nextText;
	}

	public static Pattern getUnlikelyCandidatesRegex() {
		return unlikelyCandidatesRegex;
	}

	public static Pattern getOkMaybeItsACandidateRegex() {
		return okMaybeItsACandidateRegex;
	}

	public static Pattern getPositiveWeightRegex() {
		return positiveWeightRegex;
	}

	public static Pattern getNegativeWeightRegex() {
		return negativeWeightRegex;
	}

	public static Pattern getNegativeLinkParentRegex() {
		return negativeLinkParentRegex;
	}

	public static Pattern getExtraneous() {
		return extraneous;
	}

	public static Pattern getDivToPElementsRegex() {
		return divToPElementsRegex;
	}

	public static Pattern getEndOfSentenceRegex() {
		return endOfSentenceRegex;
	}

	public static Pattern getBreakBeforeParagraphRegex() {
		return breakBeforeParagraphRegex;
	}

	public static Pattern getNormalizeSpacesRegex() {
		return normalizeSpacesRegex;
	}

	public static Pattern getKillBreaksRegex() {
		return killBreaksRegex;
	}

	public static Pattern getVideoRegex() {
		return videoRegex;
	}

	public static Pattern getReplaceDoubleBrsRegex() {
		return replaceDoubleBrsRegex;
	}

	public static Pattern getReplaceFontsRegex() {
		return replaceFontsRegex;
	}

	public static Pattern getArticleTitleRegex() {
		return articleTitleRegex;
	}

	public static Pattern getNextLink() {
		return nextLink;
	}

	public static Pattern getNextStoryLink() {
		return nextStoryLink;
	}

	public static Pattern getPrevLink() {
		return prevLink;
	}

	public static Pattern getPageRegex() {
		return pageRegex;
	}

	public static Pattern getLikelyParagraphDivRegex() {
		return likelyParagraphDivRegex;
	}

}
