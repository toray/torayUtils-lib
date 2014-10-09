package com.toraysoft.utils.format;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FormatUtil {
	public static boolean isMobileNO(String mobiles) {
		Pattern p = Pattern
				.compile("^((13[0-9])|(15[0-9])|(18[0-9])|(14[0-9])|(17[0-9]))\\d{8}$");
		Matcher m = p.matcher(mobiles);
		return m.matches();
	}
}
