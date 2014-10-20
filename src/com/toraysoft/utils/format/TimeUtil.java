package com.toraysoft.utils.format;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import android.annotation.SuppressLint;

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
	
	public static long parseUtcTime(String time){
		 String time1=time.replaceAll("T", "-");
		 String time2=time1.replaceAll("Z", "");

		 SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd-hh:mm:ss");
	     sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
	     try {
			return sdf.parse(time2).getTime();
		} catch (ParseException e) {
			e.printStackTrace();
		}
	     return 0L;
	}
	
	public static String getConverTime(String time,boolean isDetail){
		  String time1=time.replaceAll("T", "-");
		  String time2=time1.replaceAll("Z", "");

		  SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd-hh:mm:ss");
	      sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
		  try {
			long millionSeconds = sdf.parse(time2).getTime();
			if(isDetail)
			return converDetailTime(millionSeconds);
			else
			return converTime(millionSeconds);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return "";	
	}
	
	public static String converTime(long timestamp) {
		long currentSeconds = System.currentTimeMillis() / 1000;
		long timeGap = currentSeconds - timestamp / 1000;// 与现在时间相差秒数
		String timeStr = null;
		if (timeGap > 7* 24 * 60 *60){
			timeStr = getDateTimeByMillisecond(timestamp);//几月几号
		} else if (timeGap > 24 * 60 * 60) {// 1天以上
			timeStr = timeGap / (24 * 60 * 60) + "天前";
		} else if (timeGap > 60 * 60) {// 1小时-24小时
			timeStr = timeGap / (60 * 60) + "小时前";
		} else if (timeGap > 60) {// 1分钟-59分钟
			timeStr = timeGap / 60 + "分钟前";
		} else if(timeGap > 0){// 1秒钟-59秒钟
			timeStr = timeGap + "秒前";
		} else {
			timeStr = "刚刚";
		}
		return timeStr;
	}
	
	public static String converTimeSecond(long timestamp) {
		long currentSeconds = System.currentTimeMillis() / 1000 - 28800;
		long timeGap = currentSeconds - timestamp;// 与现在时间相差秒数
		String timeStr = null;
		if (timeGap > 7* 24 * 60 *60){
			timeStr = getDateTimeByMillisecond(timestamp);//几月几号
		} else if (timeGap > 24 * 60 * 60) {// 1天以上
			timeStr = timeGap / (24 * 60 * 60) + "天前";
		} else if (timeGap > 60 * 60) {// 1小时-24小时
			timeStr = timeGap / (60 * 60) + "小时前";
		} else if (timeGap > 60) {// 1分钟-59分钟
			timeStr = timeGap / 60 + "分钟前";
		} else if(timeGap > 0){// 1秒钟-59秒钟
			timeStr = timeGap + "秒前";
		} else {
			timeStr = "刚刚";
		}
		return timeStr;
	}
	
	//几月几号
	@SuppressLint("SimpleDateFormat")
	public static String getDateTimeByMillisecond(long str) {  
	    Date date = new Date(Long.valueOf(str));  
	    SimpleDateFormat format = new SimpleDateFormat("MM-dd");  
	    String time = format.format(date); 
	    return time;  
	}
	
	public static String converDetailTime(long timestamp) {
		long currentSeconds = System.currentTimeMillis() / 1000;
		long timeGap = currentSeconds - timestamp / 1000;// 与现在时间相差秒数
		String timeStr = null;
		if (timeGap > 24 * 60 * 60){
			timeStr = getDetailDateTimeByMillisecond(timestamp);//几月几号 几时几分
		} else if (timeGap > 60 * 60) {// 1小时-24小时
			timeStr = timeGap / (60 * 60) + "小时前";
		} else if (timeGap > 60) {// 1分钟-59分钟
			timeStr = timeGap / 60 + "分钟前";
		} else if (timeGap > 0) {// 1秒钟-59秒钟
			timeStr = timeGap + "秒前";
		} else {
			timeStr = "刚刚";
		}
		return timeStr;
	}
	
	//几月几号
	@SuppressLint("SimpleDateFormat")
	public static String getDetailDateTimeByMillisecond(long str) {  
	    Date date = new Date(Long.valueOf(str));  
	    SimpleDateFormat format = new SimpleDateFormat("MM月dd日 HH:mm");  
	    String time = format.format(date); 
	    return time;  
	}
	
	public static Date parseSimpleStr2Date(String date) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		try {
			return sdf.parse(date);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static String parseDate2Str(int year,int month,int day) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Calendar mCalendar = Calendar.getInstance();
		mCalendar.set(year, month, day);
		return sdf.format(mCalendar.getTime());
	}
	
	public static String parseSec2Min(int sec){
		String min = "";
		min += sec/60>=10? sec/60:"0"+sec/60;
		min += ":";
		min += sec%60>=10? sec%60:"0"+sec%60;
		return min;
	}
}
