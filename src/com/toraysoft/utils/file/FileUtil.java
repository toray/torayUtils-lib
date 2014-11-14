package com.toraysoft.utils.file;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;

public class FileUtil {

	/**
	 * 读取assets目录下的文本文件
	 * 
	 * @param context
	 * @param path
	 * @return
	 * @throws IOException
	 */
	public static List<String> readAssetFile(Context context, String path)
			throws IOException {
		List<String> ret = new ArrayList<String>();
		BufferedReader reader = new BufferedReader(new InputStreamReader(
				new BufferedInputStream(context.getAssets().open(path))));
		String line = null;
		while ((line = reader.readLine()) != null) {
			ret.add(line);
		}
		return ret;
	}
}
