package rcomic.utils;

/**
 * 轉換字串為unicode
 * 
 * @author ray
 * 
 */
public class ChineseWord {

	public ChineseWord() {

	}

	/**
	 * (多個)中文字轉unicode
	 * 
	 * @param txt
	 * @return
	 */
	public static String chineseToUnicode(String txt) {
		StringBuffer tmpStr = new StringBuffer();
		for (int i = 0; i < txt.length(); i++) {
			tmpStr.append("&#" + (int) txt.charAt(i) + ",");
		}
		return tmpStr.toString();
	}

	/**
	 * 一個unicode轉中文字
	 * 
	 * @param txt
	 * @return
	 */
	public static String getChinese(String txt) {
		return String.valueOf((char) Integer.parseInt(txt));
	}

	/**
	 * (多個)unicode轉中文字
	 * 
	 * @param txt
	 * @return
	 */
	public static String unicodeToChinese(String txt) {
		String[] tmpStrArray = txt.replaceAll("&#", "").split("[,]");
		StringBuffer tmpStr = new StringBuffer();

		for (int i = 0; i < tmpStrArray.length; i++) {
			tmpStr.append((char) Integer.parseInt(tmpStrArray[i]));
		}
		return tmpStr.toString();
	}

	/**
	 * 轉換多個unicode
	 * 
	 * @param txt
	 * @return
	 */
	public static String unicodeToChineseAll(String txt) {
		String[] tmpArray = txt.split("&#");
		// ArrayList al = new ArrayList();

		if (tmpArray.length == 0) {
			return txt;
		}

		for (int i = 1; i < tmpArray.length; i++) {
			// System.out.println("==" + tmpArray[i]);
			String[] tmpArray2 = tmpArray[i].split(";");
			String unicode = tmpArray2[0];
			// System.out.println("==" + unicode);
			if (!unicode.trim().equals("")) {
				// System.out.println("&#"+unicode+";");
				txt = txt.replaceAll("&#" + unicode + ";", getChinese(unicode));
			}
		}
		txt = HTMLSpecialCharacters(txt);
		return txt;
	}

	/**
	 * 轉換html特殊字元
	 * 
	 * @param str
	 * @return
	 */
	public static String HTMLSpecialCharacters(String str) {
		str = str.replaceFirst("&amp;", "&");
		str = str.replaceAll("&", "");

		str = str.replaceAll("ndash;", "–");
		str = str.replaceAll("mdash;", "—");
		str = str.replaceAll("iexcl;", "¡");
		str = str.replaceAll("iquest;", "¿");
		str = str.replaceAll("quot;", "\"");
		str = str.replaceAll("ldquo;", "“");
		str = str.replaceAll("rdquo;", "”");
		str = str.replaceAll("lsquo;", "‘");
		str = str.replaceAll("rsquo;", "’");
		str = str.replaceAll("laquo;", "«");
		str = str.replaceAll("raquo;", "»");
		str = str.replaceAll("amp;", "&");
		str = str.replaceAll("cent;", "¢");
		str = str.replaceAll("copy;", "©");
		str = str.replaceAll("divide;", "÷");
		str = str.replaceAll("gt;", ">");
		str = str.replaceAll("nbsp;", " ");

		str = str.replaceAll("lt;", "<");
		str = str.replaceAll("micro;", "µ");
		str = str.replaceAll("middot;", "·");
		str = str.replaceAll("para;", "¶");
		str = str.replaceAll("plusmn;", "±");
		str = str.replaceAll("euro;", "€");
		str = str.replaceAll("pound;", "£");
		str = str.replaceAll("reg;", "®");
		str = str.replaceAll("sect;", "§");
		str = str.replaceAll("trade;", "™");
		str = str.replaceAll("yen;", "¥");
		return str;
	}

}
