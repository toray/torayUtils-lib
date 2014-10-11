package com.toraysoft.utils.format;

public class TimeUtil {
	public static final String[] starArr = { "魔羯座", "水瓶座", "双鱼座", "牡羊座", "金牛座",
			"双子座", "巨蟹座", "狮子座", "处女座", "天秤座", "天蝎座", "射手座" };
	public static final int[] DayArr = { 22, 20, 19, 21, 21, 21, 22, 23, 23,
			23, 23, 22 };

	public static String calculateConstellation(String birthday) {
		if (birthday == null || birthday.trim().length() == 0) {
			return null;
		}
		String[] birthdayElements = birthday.split("-");
		if (birthdayElements.length != 3) {
			return null;
		}
		try {
			int month = Integer.parseInt(birthdayElements[1]);
			int day = Integer.parseInt(birthdayElements[2]);
			if (month == 0 || day == 0 || month > 12)
				return "";
			month = day < DayArr[month - 1] ? month - 1 : month;
			return month > 0 ? starArr[month - 1] : starArr[11];
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static String ageStrByDate(String birthday) {
		if (birthday == null || birthday.trim().length() == 0) {
			return null;
		}
		try {
			String[] birthdayElements = birthday.split("-");
			int year = Integer.parseInt(birthdayElements[0]);
			int age = year % 100 / 10 * 10;
			String ageStr = age == 0 ? "0" + age : "" + age;
			return ageStr + "后 ";
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static String ageAndConstellation(String birthday) {
		StringBuffer sb = new StringBuffer();
		String age = ageStrByDate(birthday);
		String constellation = calculateConstellation(birthday);
		if (age != null)
			sb.append(age);
		if (constellation != null)
			sb.append(constellation);
		return sb.toString();
	}
}
