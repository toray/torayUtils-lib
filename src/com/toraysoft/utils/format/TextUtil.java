package com.toraysoft.utils.format;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class TextUtil {
	
	public static List<String[]> getDimensionalArray(String[] array) {
		List<String[]> list = new ArrayList<String[]>();
		String[][] twoDimensionalArray = null;
		for (int i = 0; i < array.length; i++) {
			String[] tempArray = array[i].split(",");
			list.add(tempArray);
		}
		return list;
	}
	
	
	public static String getBigDecimalText(String val){
		BigDecimal bigDecimal = new BigDecimal(val);  
        return bigDecimal.toString();  
	}
}
